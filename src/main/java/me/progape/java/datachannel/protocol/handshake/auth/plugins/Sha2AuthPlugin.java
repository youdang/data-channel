package me.progape.java.datachannel.protocol.handshake.auth.plugins;

import me.progape.java.datachannel.protocol.Context;
import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.protocol.handshake.HandshakeRequest;
import me.progape.java.datachannel.protocol.handshake.auth.AuthSwitchResponse;
import me.progape.java.datachannel.protocol.handshake.auth.MoreDataResponse;
import me.progape.java.datachannel.protocol.shared.ErrorResponse;
import me.progape.java.datachannel.protocol.shared.RawRequest;
import me.progape.java.datachannel.protocol.shared.Response;
import me.progape.java.datachannel.protocol.utils.ByteUtil;
import me.progape.java.datachannel.transport.Packet;
import me.progape.java.datachannel.transport.Transport;
import reactor.core.publisher.Mono;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author progape
 * @date 2022-02-06
 */
public class Sha2AuthPlugin extends AbstractAuthPlugin {
    private static final String PUBLIC_KEY_BEGIN = "-----BEGIN PUBLIC KEY-----";
    private static final String PUBLIC_KEY_END = "-----END PUBLIC KEY-----";

    public Sha2AuthPlugin(String authPluginName, Transport transport, Context context) {
        super(authPluginName, transport, context);
    }

    @Override
    public Mono<Packet> auth(Response response) {
        byte[] seed = extractSeed(response);
        byte[] scramble = makeScramble(seed);
        HandshakeRequest handshakeRequest = new HandshakeRequest(
            (short) (response.getSequenceId() + 1), getAuthPluginName(), scramble, context
        );

        return transport.sendReceive(handshakeRequest.toPacket(transport.alloc()))
            .flatMap(p -> {
                if (p.isError()) {
                    return Mono.error(new ProtocolException(new ErrorResponse(p, context)));
                } else if (p.getPayload().getByte(0) == (byte) 0xFE) {
                    // auth switch response
                    AuthSwitchResponse authSwitchResponse = new AuthSwitchResponse(p, context);
                    AuthPlugin newAuthPlugin = AuthPluginFactory.create(authSwitchResponse.getAuthPluginName(), transport, context);
                    return newAuthPlugin.auth(authSwitchResponse);
                }

                byte fastAuthResult = p.getPayload().getByte(1);
                if (fastAuthResult == 0x03) {
                    // fast-auth succeeded, receive the OK packet
                    return transport.receive();
                } else if (fastAuthResult == 0x04) {
                    // full-auth: public key retrieval
                    RawRequest publicKeyRetrievalRequest = new RawRequest(
                        (short) (p.getSequenceId() + 1), new byte[]{0x02}
                    );
                    return transport.sendReceive(publicKeyRetrievalRequest.toPacket(transport.alloc()))
                        .flatMap(pp -> {
                            if (pp.isError()) {
                                return Mono.error(new ProtocolException(new ErrorResponse(pp, context)));
                            }
                            // send encrypted password
                            MoreDataResponse moreDataResponse = new MoreDataResponse(pp, context);
                            byte[] encryptedPassword = encryptPassword(seed, moreDataResponse.getAuthPluginData());
                            RawRequest rawRequest = new RawRequest((short) (pp.getSequenceId() + 1), encryptedPassword);
                            return transport.sendReceive(rawRequest.toPacket(transport.alloc()))
                                .flatMap(ppp -> {
                                    if (ppp.isError()) {
                                        return Mono.error(new ProtocolException(new ErrorResponse(ppp, context)));
                                    }
                                    return Mono.just(ppp);
                                });
                        });
                }
                return Mono.error(new ProtocolException("unexpected packet"));
            });
    }

    private byte[] makeScramble(byte[] seed) {
        if (seed == null || seed.length != 20) {
            throw new ProtocolException("broken seed");
        }

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new ProtocolException("SHA-256 is unsupported");
        }

        // scramble = XOR(SHA256(password), SHA256(SHA256(SHA256(password)), seed))
        byte[] passwordHash = md.digest(context.getPassword().getBytes());
        md.reset();
        byte[] passwordHashHash = md.digest(passwordHash);
        md.reset();
        md.update(passwordHashHash);
        md.update(seed);

        return ByteUtil.xor(md.digest(), passwordHash);
    }

    private byte[] encryptPassword(byte[] seed, byte[] publicKeyBytes) {
        try {
            String transformation = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";
            if (context.getServerVersion().lt("8", "0", "5")) {
                transformation = "RSA/ECB/PKCS1Padding";
            }

            Cipher rsa = Cipher.getInstance(transformation);
            RSAPublicKey publicKey = resolvePublicKey(publicKeyBytes);
            rsa.init(Cipher.ENCRYPT_MODE, publicKey);
            return rsa.doFinal(hashPassword(seed));
        } catch (Exception e) {
            throw new ProtocolException("RSA is not supported", e);
        }
    }

    private RSAPublicKey resolvePublicKey(byte[] publicKeyBytes) {
        String publicKeyStr = new String(publicKeyBytes, StandardCharsets.US_ASCII);
        if (!publicKeyStr.contains(PUBLIC_KEY_BEGIN) || !publicKeyStr.contains(PUBLIC_KEY_END)) {
            throw new ProtocolException("invalid public key");
        }
        int offset = publicKeyStr.indexOf(PUBLIC_KEY_BEGIN) + PUBLIC_KEY_BEGIN.length() + 1;
        int length = publicKeyStr.indexOf(PUBLIC_KEY_END) - offset;
        byte[] tmp = new byte[length];
        System.arraycopy(publicKeyBytes, offset, tmp, 0, length);

        byte[] certificateData = Base64.getMimeDecoder().decode(tmp);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(certificateData);
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) kf.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new ProtocolException("unsupported x509 certificate");
        }
    }

    private byte[] hashPassword(byte[] seed) {
        if (seed == null || seed.length < 20) {
            throw new ProtocolException("broken seed");
        }

        byte[] passwordBytes = context.getPassword().getBytes();
        byte[] tmp = new byte[passwordBytes.length + 1];
        System.arraycopy(passwordBytes, 0, tmp, 0, passwordBytes.length);
        tmp[passwordBytes.length] = 0x00;

        return ByteUtil.xor(tmp, seed);
    }
}
