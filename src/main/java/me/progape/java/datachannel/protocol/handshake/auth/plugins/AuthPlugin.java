package me.progape.java.datachannel.protocol.handshake.auth.plugins;

import me.progape.java.datachannel.protocol.shared.Response;
import me.progape.java.datachannel.transport.Packet;
import reactor.core.publisher.Mono;

/**
 * @author progape
 * @date 2022-02-05
 */
public interface AuthPlugin {
    String getAuthPluginName();

    Mono<Packet> auth(Response response);
}
