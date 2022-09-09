package me.progape.java.datachannel.protocol.handshake.auth.plugins;

import me.progape.java.datachannel.protocol.Context;
import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.protocol.handshake.HandshakeResponse;
import me.progape.java.datachannel.protocol.handshake.auth.AuthSwitchResponse;
import me.progape.java.datachannel.protocol.shared.Response;
import me.progape.java.datachannel.transport.Packet;
import me.progape.java.datachannel.transport.Transport;
import reactor.core.publisher.Mono;

/**
 * @author progape
 * @date 2022-02-06
 */
public abstract class AbstractAuthPlugin implements AuthPlugin {
    protected final String authPluginName;
    protected final Transport transport;
    protected final Context context;

    public AbstractAuthPlugin(String authPluginName, Transport transport, Context context) {
        this.authPluginName = authPluginName;
        this.transport = transport;
        this.context = context;
    }

    @Override
    public String getAuthPluginName() {
        return authPluginName;
    }

    @Override
    public Mono<Packet> auth(Response response) {
        throw new ProtocolException("unsupported auth plugin");
    }

    protected byte[] extractSeed(Response response) {
        if (response instanceof HandshakeResponse) {
            return ((HandshakeResponse) response).getAuthPluginData();
        } else if (response instanceof AuthSwitchResponse) {
            return  ((AuthSwitchResponse) response).getAuthPluginData();
        } else {
            throw new ProtocolException("unexpected response");
        }
    }
}
