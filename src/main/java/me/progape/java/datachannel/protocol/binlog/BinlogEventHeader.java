package me.progape.java.datachannel.protocol.binlog;

import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.transport.Packet;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;

import java.util.Set;

/**
 * @author progape
 * @date 2022-02-06
 */
public final class BinlogEventHeader {
    private final short sequenceId;
    private long timestamp;
    private BinlogEventType eventType;
    private long serverId;
    private long eventSize;
    private long nextBinlogPosition;
    private Set<BinlogEventFlag> binlogEventFlags;

    public BinlogEventHeader(Packet packet) {
        this.sequenceId = packet.getSequenceId();

        readHeader(packet.getPayload());
    }

    private void readHeader(ByteBuf payload) {
        if (ByteBufUtil.readInt1(payload) != 0x00) {
            throw new ProtocolException("invalid binlog event");
        }

        this.timestamp = ByteBufUtil.readInt4(payload);
        this.eventType = BinlogEventType.codeOf((int) ByteBufUtil.readInt1(payload));
        this.serverId = ByteBufUtil.readInt4(payload);
        this.eventSize = ByteBufUtil.readInt4(payload);
        this.nextBinlogPosition = ByteBufUtil.readInt4(payload);
        this.binlogEventFlags = BinlogEventFlag.decode(ByteBufUtil.readInt2(payload));
    }

    public short getSequenceId() {
        return sequenceId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public BinlogEventType getEventType() {
        return eventType;
    }

    public long getServerId() {
        return serverId;
    }

    public long getEventSize() {
        return eventSize;
    }

    public long getNextBinlogPosition() {
        return nextBinlogPosition;
    }

    public Set<BinlogEventFlag> getBinlogEventFlags() {
        return binlogEventFlags;
    }
}
