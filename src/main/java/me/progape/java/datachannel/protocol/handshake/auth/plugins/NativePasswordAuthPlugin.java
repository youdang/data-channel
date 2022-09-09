package me.progape.java.datachannel.protocol.handshake.auth.plugins;

import me.progape.java.datachannel.protocol.Context;
import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.protocol.handshake.HandshakeRequest;
import me.progape.java.datachannel.protocol.shared.Response;
import me.progape.java.datachannel.transport.Packet;
import me.progape.java.datachannel.transport.Transport;
import reactor.core.publisher.Mono;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author progape
 * @date 2022-02-06
 */
public class NativePasswordAuthPlugin extends AbstractAuthPlugin {
    public NativePasswordAuthPlugin(Transport transport, Context context) {
        super("mysql_native_password", transport, context);
    }

    @Override
    public Mono<Packet> auth(Response response) {
        byte[] seed = extractSeed(response);
        byte[] scramble = makeScramble(seed);

        short nextSequenceId = (short) (response.getSequenceId() + 1);
        Packet requestPacket = new HandshakeRequest(nextSequenceId, getAuthPluginName(), scramble, context)
            .toPacket(transport.alloc());

        return transport.sendReceive(requestPacket);
    }

    private byte[] makeScramble(byte[] seed) {
        if (seed == null || seed.length != 20) {
            throw new ProtocolException("broken seed");
        }

        // scramble = SHA1(password) XOR SHA1(seed + SHA1(SHA1(password)))
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new ProtocolException("SHA-1 is unsupported");
        }
        byte[] password = context.getPassword().getBytes();
        byte[] passwordHash = md.digest(password);
        md.reset();
        byte[] passwordHashHash = md.digest(passwordHash);
        md.reset();
        md.update(seed);
        md.update(passwordHashHash);
        byte[] scramble = md.digest();
        for (int i = 0; i < scramble.length; i++) {
            scramble[i] ^= passwordHash[i];
        }

        return scramble;
    }
}
