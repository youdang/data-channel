package me.progape.java.datachannel.protocol.binlog;

import io.netty.buffer.ByteBuf;

/**
 * @author progape
 * @date 2022-02-06
 */
public class WriteRowsEventV2 extends RowsEventV2 {
    public WriteRowsEventV2(BinlogEventHeader header, ByteBuf payload, BinlogContext context) {
        super(header, payload, context);
    }
}
