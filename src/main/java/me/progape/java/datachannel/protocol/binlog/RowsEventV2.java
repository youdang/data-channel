package me.progape.java.datachannel.protocol.binlog;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;
import me.progape.java.datachannel.protocol.utils.ByteUtil;

import java.util.List;

/**
 * @author progape
 * @date 2022-02-06
 */
public abstract class RowsEventV2 extends BinlogEvent {
    protected long tableId;
    protected TableMeta tableMeta;
    protected byte[] extraData;
    protected List<Integer> columnIndices1;
    protected List<List<byte[]>> rows1;

    public RowsEventV2(BinlogEventHeader header, ByteBuf payload, BinlogContext context) {
        super(header, payload, context);
    }

    public long getTableId() {
        return this.tableId;
    }

    public TableMeta getTableMeta() {
        return tableMeta;
    }

    public List<Integer> getColumnIndices1() {
        return this.columnIndices1;
    }

    public List<List<byte[]>> getRows1() {
        return this.rows1;
    }

    @Override
    protected void readPostHeader(ByteBuf payload, BinlogContext binlogContext) {
        this.tableId = ByteBufUtil.readInt6(payload);
        // skip 2 reserved bytes
        ByteBufUtil.readInt2(payload);

        int extraDataLength = ByteBufUtil.readInt2(payload);
        this.extraData = ByteBufUtil.readFixedLengthString(payload, extraDataLength - 2);
    }

    @Override
    protected void readBody(ByteBuf payload, BinlogContext binlogContext) {
        TableMeta tableMeta = binlogContext.getTable(this.tableId);
        if (tableMeta == null) {
            throw new ProtocolException("table #" + this.tableId + " not found");
        }
        this.tableMeta = tableMeta;

        int columnCount = (int) ByteBufUtil.readLengthEncodedInt(payload);

        readBody(payload, binlogContext, columnCount);

        int restLength = realReadableLength(payload, binlogContext);
        while (restLength > 0) {
            readRow(payload, binlogContext);
            restLength = realReadableLength(payload, binlogContext);
        }
    }

    protected void readBody(ByteBuf payload, BinlogContext context, int columnCount) {
        this.columnIndices1 = ByteUtil.bitmap2Indices(
            ByteBufUtil.readFixedLengthString(payload, (columnCount + 7) / 8), columnCount
        );
        this.rows1 = Lists.newArrayList();
    }

    protected void readRow(ByteBuf payload, BinlogContext context) {
        this.rows1.add(_readRow(payload, this.columnIndices1));
    }

    protected final List<byte[]> _readRow(ByteBuf payload, List<Integer> columnIndices) {
        int columnCount = columnIndices.size();

        List<Integer> nullIndices = ByteUtil.bitmap2Indices(
            ByteBufUtil.readFixedLengthString(payload, (columnCount + 7) / 8), columnCount
        );

        List<byte[]> row = Lists.newArrayListWithCapacity(columnIndices.size());
        for (int index : columnIndices) {
            if (nullIndices.contains(index)) {
                row.add(null);
                continue;
            }
            ColumnMeta columnMeta = this.tableMeta.getColumnMetas().get(index);
            if (columnMeta == null) {
                throw new ProtocolException("table #" + this.tableId + "'s column #" + index + " not found");
            }

            ColumnType type = columnMeta.getType();
            if (type == ColumnType.STRING) {
                ColumnType realType = ColumnType.codeOf(columnMeta.getMeta() >>> 8);
                if (realType == ColumnType.ENUM || realType == ColumnType.SET) {
                    type = realType;
                }
            } else if (type == ColumnType.DATE) {
                type = ColumnType.NEWDATE;
            }

            // handle STRING type
            int length = 0;
            if (type == ColumnType.STRING) {
                int meta = columnMeta.getMeta();
                if (meta >= 256) {
                    int byte0 = meta >>> 8;
                    int byte1 = meta & 0xFF;
                    if ((byte0 & 0x30) != 0x30) {
                        length = byte1 | (((byte0 & 0x30) ^ 0x30) << 4);
                        type = ColumnType.codeOf(byte0 | 0x30);
                    } else {
                        length = byte1;
                    }
                } else {
                    length = meta;
                }
            }

            row.add(type.readBinlogValue(columnMeta.getMeta(), length, payload));
        }
        return row;
    }
}
