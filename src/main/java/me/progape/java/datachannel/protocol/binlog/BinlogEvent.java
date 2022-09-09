package me.progape.java.datachannel.protocol.binlog;

import io.netty.buffer.ByteBuf;

/**
 * @author progape
 * @date 2022-02-06
 */
public abstract class BinlogEvent {
    protected final BinlogEventHeader header;

    public BinlogEvent(BinlogEventHeader header, ByteBuf payload, BinlogContext binlogContext) {
        this.header = header;

        readPostHeader(payload, binlogContext);
        readBody(payload, binlogContext);
    }

    protected void readPostHeader(ByteBuf payload, BinlogContext binlogContext) {
    }

    protected void readBody(ByteBuf payload, BinlogContext binlogContext) {
    }

    protected int realReadableLength(ByteBuf payload, BinlogContext binlogContext) {
        return binlogContext.getContext().isHasChecksum() ? payload.readableBytes() - 4 : payload.readableBytes();
    }

    public BinlogEventHeader getHeader() {
        return header;
    }
}
