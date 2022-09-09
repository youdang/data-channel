package me.progape.java.datachannel.protocol.shared;

import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.protocol.Context;
import me.progape.java.datachannel.protocol.utils.CharsetUtil;
import me.progape.java.datachannel.transport.Packet;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;

/**
 * @author progape
 * @date 2022-01-30
 */
public class ErrorResponse extends Response {
    private int errorCode;
    private byte sqlStateMarker;
    private byte[] sqlState;
    private String errorMessage;

    public ErrorResponse(Packet packet, Context context) {
        super(packet, context);
    }

    @Override
    protected void readPayload(ByteBuf payload, Context context) {
        // skip header
        payload.readByte();

        errorCode = ByteBufUtil.readInt2(payload);
        if (context.getCapabilityFlags().contains(CapabilityFlag.CLIENT_PROTOCOL_41)) {
            sqlStateMarker = ByteBufUtil.readFixedLengthString(payload, 1)[0];
            sqlState = ByteBufUtil.readFixedLengthString(payload, 5);
        }
        errorMessage = CharsetUtil.decode(ByteBufUtil.readRestOfPacketString(payload), context);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public byte getSqlStateMarker() {
        return sqlStateMarker;
    }

    public byte[] getSqlState() {
        return sqlState;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
