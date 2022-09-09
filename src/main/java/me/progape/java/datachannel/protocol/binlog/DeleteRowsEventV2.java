package me.progape.java.datachannel.protocol.binlog;

import io.netty.buffer.ByteBuf;

/**
 * @author progape
 * @date 2022-02-06
 */
public class DeleteRowsEventV2 extends RowsEventV2 {
    public DeleteRowsEventV2(BinlogEventHeader header, ByteBuf payload, BinlogContext context) {
        super(header, payload, context);
    }
}
