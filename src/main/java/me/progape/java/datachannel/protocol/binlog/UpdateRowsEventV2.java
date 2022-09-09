package me.progape.java.datachannel.protocol.binlog;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;
import me.progape.java.datachannel.protocol.utils.ByteUtil;

import java.util.List;

/**
 * @author progape
 * @date 2022-02-06
 */
public class UpdateRowsEventV2 extends RowsEventV2 {
    private List<Integer> columnIndices2;
    protected List<List<byte[]>> rows2;

    public UpdateRowsEventV2(BinlogEventHeader header, ByteBuf payload, BinlogContext context) {
        super(header, payload, context);
    }

    public List<Integer> getColumnIndices2() {
        return this.columnIndices2;
    }

    public List<List<byte[]>> getRows2() {
        return this.rows2;
    }

    @Override
    protected void readBody(ByteBuf payload, BinlogContext context, int columnCount) {
        super.readBody(payload, context, columnCount);

        this.columnIndices2 = ByteUtil.bitmap2Indices(
            ByteBufUtil.readFixedLengthString(payload, (columnCount + 7) / 8), columnCount
        );
        this.rows2 = Lists.newArrayList();
    }

    @Override
    protected void readRow(ByteBuf payload, BinlogContext context) {
        super.readRow(payload, context);

        this.rows2.add(super._readRow(payload, this.columnIndices2));
    }
}
