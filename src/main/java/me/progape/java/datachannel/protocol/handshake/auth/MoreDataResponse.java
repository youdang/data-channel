package me.progape.java.datachannel.protocol.handshake.auth;

import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.protocol.Context;
import me.progape.java.datachannel.protocol.shared.Response;
import me.progape.java.datachannel.transport.Packet;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;

/**
 * @author progape
 * @date 2022-02-06
 */
public class MoreDataResponse extends Response {
    private byte[] authPluginData;

    public MoreDataResponse(Packet packet, Context context) {
        super(packet, context);
    }

    @Override
    protected void readPayload(ByteBuf payload, Context context) {
        // skip status byte
        ByteBufUtil.readInt1(payload);

        this.authPluginData = ByteBufUtil.readRestOfPacketString(payload);
    }

    public byte[] getAuthPluginData() {
        return authPluginData;
    }
}
