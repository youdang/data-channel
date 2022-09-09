package me.progape.java.datachannel.protocol.shared;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import me.progape.java.datachannel.transport.Packet;

/**
 * @author progape
 * @date 2022-02-04
 */
public abstract class Request {
    protected final short sequenceId;

    public Request(short sequenceId) {
        this.sequenceId = sequenceId;
    }

    public Packet toPacket(ByteBufAllocator alloc) {
        ByteBuf payload = alloc.buffer();
        writePayload(payload);
        return new Packet(payload.readableBytes(), sequenceId, payload);
    }

    protected abstract void writePayload(ByteBuf payload);
}
