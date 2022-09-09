package me.progape.java.datachannel.protocol.handshake.auth;

import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.protocol.Context;
import me.progape.java.datachannel.protocol.shared.Response;
import me.progape.java.datachannel.transport.Packet;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;
import me.progape.java.datachannel.protocol.utils.CharsetUtil;

/**
 * @author progape
 * @date 2022-02-06
 */
public class AuthSwitchResponse extends Response {
    private String authPluginName;
    private byte[] authPluginData;

    public AuthSwitchResponse(Packet packet, Context context) {
        super(packet, context);
    }

    @Override
    protected void readPayload(ByteBuf payload, Context context) {
        // skip status byte
        ByteBufUtil.readInt1(payload);

        this.authPluginName = CharsetUtil.decode(ByteBufUtil.readNullTerminatedString(payload), context);
        this.authPluginData = ByteBufUtil.readRestOfPacketString(payload);
    }

    public String getAuthPluginName() {
        return authPluginName;
    }

    public byte[] getAuthPluginData() {
        return authPluginData;
    }
}
