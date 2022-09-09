package me.progape.java.datachannel.protocol.binlog;

import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;

/**
 * @author progape
 * @date 2022-02-06
 */
public class XidEvent extends BinlogEvent {
    private long xid;

    public XidEvent(BinlogEventHeader header, ByteBuf payload, BinlogContext context) {
        super(header, payload, context);
    }

    @Override
    protected void readBody(ByteBuf payload, BinlogContext binlogContext) {
        this.xid = ByteBufUtil.readInt8(payload);
    }

    public long getXid() {
        return xid;
    }
}
