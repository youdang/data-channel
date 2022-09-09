package me.progape.java.datachannel.protocol.handshake;

import me.progape.java.datachannel.protocol.Context;
import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.protocol.handshake.auth.AuthSwitchResponse;
import me.progape.java.datachannel.protocol.handshake.auth.plugins.AuthPlugin;
import me.progape.java.datachannel.protocol.handshake.auth.plugins.AuthPluginFactory;
import me.progape.java.datachannel.protocol.shared.ErrorResponse;
import me.progape.java.datachannel.transport.Packet;
import me.progape.java.datachannel.transport.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * @author progape
 * @date 2022-02-06
 */
public class Authenticator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Authenticator.class);

    public static Mono<Packet> auth(HandshakeResponse hr, Transport transport, Context context) {
        AuthPlugin authPlugin = AuthPluginFactory.create(hr.getAuthPluginName(), transport, context);
        return authPlugin.auth(hr)
            .flatMap(p -> {
                if (p.getPayload().getByte(0) == (byte) 0xFE) {
                    LOGGER.debug("auto switch response received");
                    // auth-switch-response
                    AuthSwitchResponse asr = new AuthSwitchResponse(p, context);
                    AuthPlugin newAuthPlugin = AuthPluginFactory.create(asr.getAuthPluginName(), transport, context);
                    return newAuthPlugin.auth(asr);
                } else if (p.isError()) {
                    return Mono.error(new ProtocolException(new ErrorResponse(p, context)));
                }
                return Mono.just(p);
            });
    }

//    public Mono<Packet> doAuth(short nextSequenceId, AuthPlugin authPlugin, boolean authSwitched) {
//        byte[] authResponse = authPlugin.auth();
//        Packet requestPacket;
//        if (authSwitched) {
//            requestPacket = new AuthSwitchRequest(nextSequenceId, authResponse).toPacket(transport.alloc());
//        } else {
//            requestPacket = new HandshakeRequest(nextSequenceId, authPlugin.getAuthPluginName(), authResponse, context)
//                .toPacket(transport.alloc());
//        }
//
//        LOGGER.debug("start authentication");
//
//        return transport.sendReceive(requestPacket)
//            .flatMap(p -> {
//                if (p.getPayload().getByte(0) == (byte) 0xFE) {
//                    LOGGER.debug("auto switch response received");
//                    // auth-switch-response
//                    AuthSwitchResponse asr = new AuthSwitchResponse(p, context);
//                    AuthPlugin newAuthPlugin = AuthPluginFactory.create(asr.getAuthPluginName(), asr.getAuthPluginData(), context);
//                    return doAuth((short) (asr.getSequenceId() + 1), newAuthPlugin, true);
//                } else if (p.getPayload().getByte(0) == (byte) 0x01) {
//                    LOGGER.debug("more data response received");
//                    // more-data-response
//                    MoreDataResponse mdr = new MoreDataResponse(p, context);
//                    authPlugin.moreData(mdr.getAuthPluginData());
//                    return doAuth((short) (mdr.getSequenceId() + 1), authPlugin, false);
//                }
//                return Mono.just(p);
//            });
//    }
}
