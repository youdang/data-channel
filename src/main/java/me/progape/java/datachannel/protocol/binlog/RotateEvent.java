package me.progape.java.datachannel.protocol.binlog;

import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;
import me.progape.java.datachannel.protocol.utils.CharsetUtil;

/**
 * @author progape
 * @date 2022-02-06
 */
public class RotateEvent extends BinlogEvent {
    private int nextBinlogPosition;
    private String nextBinlogFilename;

    public RotateEvent(BinlogEventHeader header, ByteBuf payload, BinlogContext context) {
        super(header, payload, context);
    }

    @Override
    protected void readPostHeader(ByteBuf payload, BinlogContext binlogContext) {
        this.nextBinlogPosition = (int) ByteBufUtil.readInt8(payload);
    }

    @Override
    protected void readBody(ByteBuf payload, BinlogContext binlogContext) {
        // TODO: has checksum?
        nextBinlogFilename = CharsetUtil.decode(ByteBufUtil.readRestOfPacketString(payload), binlogContext.getContext());
    }

    public int getNextBinlogPosition() {
        return nextBinlogPosition;
    }

    public String getNextBinlogFilename() {
        return nextBinlogFilename;
    }
}
