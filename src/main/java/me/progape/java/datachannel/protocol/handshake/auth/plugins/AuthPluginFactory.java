package me.progape.java.datachannel.protocol.handshake.auth.plugins;

import me.progape.java.datachannel.protocol.Context;
import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.transport.Transport;

/**
 * @author progape
 * @date 2022-02-05
 */
public class AuthPluginFactory {
    private static final AuthPlugin DEFAULT_AUTH_PLUGIN =
        new AbstractAuthPlugin("", null, null) {};

    public static AuthPlugin create(String authPluginName, Transport transport, Context context) {
        if (authPluginName == null || authPluginName.isEmpty()) {
            return DEFAULT_AUTH_PLUGIN;
        }
        switch (authPluginName) {
            case "mysql_native_password":
                return new NativePasswordAuthPlugin(transport, context);
            case "sha2_password":
            case "caching_sha2_password":
                return new Sha2AuthPlugin(authPluginName, transport, context);
            default:
                throw new ProtocolException("unsupported auth method: " + authPluginName);
        }
    }
}
