package me.progape.java.datachannel.protocol.shared;

import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.protocol.Context;
import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.transport.Packet;

/**
 * @author progape
 * @date 2022-02-04
 */
public abstract class Response {
    protected short sequenceId;

    public Response() {
        this.sequenceId = 0;
    }

    public Response(Packet packet, Context context) {
        this.sequenceId = packet.getSequenceId();
        if (packet.getPayload().readableBytes() < packet.getPayloadLength()) {
            throw new ProtocolException("invalid payload length");
        }

        readPayload(packet.getPayload(), context);
    }

    protected void readPayload(ByteBuf payload, Context context) {
    }

    public short getSequenceId() {
        return sequenceId;
    }
}
