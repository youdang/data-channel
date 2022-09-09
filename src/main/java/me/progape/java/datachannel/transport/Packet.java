package me.progape.java.datachannel.transport;

import io.netty.buffer.ByteBuf;

/**
 * @author progape
 * @date 2022-01-23
 */
public class Packet {
    private final int payloadLength;
    private final short sequenceId;
    private final ByteBuf payload;

    public Packet(int payloadLength, short sequenceId, ByteBuf payload) {
        this.payloadLength = payloadLength;
        this.sequenceId = sequenceId;
        this.payload = payload;
    }

    public boolean isOk() {
        byte firstByte = payload.getByte(0);
        return firstByte == (byte) 0x00;
    }

    public boolean isError() {
        byte firstByte = payload.getByte(0);
        return firstByte == (byte) 0xFF;
    }

    public boolean isEof() {
        byte firstByte = payload.getByte(0);
        return firstByte == (byte) 0xFE && payload.readableBytes() < 9;
    }

    public int getPayloadLength() {
        return payloadLength;
    }

    public short getSequenceId() {
        return sequenceId;
    }

    public ByteBuf getPayload() {
        return payload;
    }
}
