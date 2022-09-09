package me.progape.java.datachannel.protocol.shared;

import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;

/**
 * @author progape
 * @date 2022-05-07
 */
public class RawRequest extends Request {
    private final byte[] payload;

    public RawRequest(short sequenceId, byte[] payload) {
        super(sequenceId);
        this.payload = payload;
    }

    @Override
    protected void writePayload(ByteBuf payload) {
        ByteBufUtil.writeRestOfPacketString(payload, this.payload);
    }
}
