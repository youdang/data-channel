package me.progape.java.datachannel.protocol.binlog;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;
import me.progape.java.datachannel.protocol.utils.ByteUtil;
import me.progape.java.datachannel.protocol.utils.CharsetUtil;

import java.util.List;

/**
 * @author progape
 * @date 2022-02-06
 */
public class TableMapEvent extends BinlogEvent {
    private long tableId;
    private String schemaName;
    private String tableName;

    public TableMapEvent(BinlogEventHeader header, ByteBuf payload, BinlogContext context) {
        super(header, payload, context);
    }

    @Override
    protected void readPostHeader(ByteBuf payload, BinlogContext binlogContext) {
        this.tableId = ByteBufUtil.readInt6(payload);
        // skip 2 reserved bytes
        ByteBufUtil.readInt2(payload);
    }

    @Override
    protected void readBody(ByteBuf payload, BinlogContext binlogContext) {
        this.schemaName = CharsetUtil.decode(
            ByteBufUtil.readFixedLengthString(payload, ByteBufUtil.readInt1(payload)), binlogContext.getContext()
        );
        // skip 0x00
        ByteBufUtil.readInt1(payload);

        this.tableName = CharsetUtil.decode(
            ByteBufUtil.readFixedLengthString(payload, ByteBufUtil.readInt1(payload)), binlogContext.getContext()
        );
        // skip 0x00
        ByteBufUtil.readInt1(payload);

        int columnCount = (int) ByteBufUtil.readLengthEncodedInt(payload);
        byte[] columnTypes = ByteBufUtil.readFixedLengthString(payload, columnCount);
        byte[] columnMetas = ByteBufUtil.readLengthEncodedString(payload);
        byte[] nullBitmap = ByteBufUtil.readFixedLengthString(payload, (columnCount + 7) / 8);

        List<ColumnMeta> columnMetaList = Lists.newArrayListWithCapacity(columnCount);
        int offset = 0;
        for (int i = 0; i < columnCount; i++) {
            ColumnType type = ColumnType.codeOf(Byte.toUnsignedInt(columnTypes[i]));
            if (type == null) {
                throw new ProtocolException("unsupported column type");
            }

            ColumnMeta columnMeta = buildColumnMeta(type, columnMetas, offset, !ByteUtil.isBitSet(nullBitmap, i));
            columnMetaList.add(columnMeta);
            offset += columnMeta.getMetaLength();
        }

        TableMeta tableMeta = new TableMeta(this.tableId, this.schemaName, this.tableName, columnMetaList);
        binlogContext.updateTable(tableMeta);
    }

    /**
     * see rpl_utility.cc read_field_metadata
     */
    private ColumnMeta buildColumnMeta(ColumnType type, byte[] columnMetas, int offset, boolean isNotNull) {
        boolean isArray = false;
        ColumnType originType = type;
        if (type == ColumnType.TYPED_ARRAY) {
            type = ColumnType.codeOf((int) columnMetas[offset]);
            isArray = true;
            offset += 1;
        }

        int meta = 0;
        int metaLength = 0;
        switch (type) {
            case TINY_BLOB:
            case BLOB:
            case MEDIUM_BLOB:
            case LONG_BLOB:
            case DOUBLE:
            case FLOAT:
            case GEOMETRY:
            case TIME2:
            case DATETIME2:
            case TIMESTAMP2:
            case JSON:
                meta = ByteUtil.toIntegerLE(columnMetas, offset, 1);
                metaLength = 1;
                break;
            case SET:
            case ENUM:
            case STRING:
            case DECIMAL:
            case NEWDECIMAL:
                meta = ByteUtil.toIntegerBE(columnMetas, offset, 2);
                metaLength = 2;
                break;
            case BIT:
                meta = ByteUtil.toIntegerLE(columnMetas, offset, 2);
                metaLength = 2;
                break;
            case VARCHAR:
                if (isArray) {
                    meta = ByteUtil.toIntegerLE(columnMetas, offset, 3);
                    metaLength = 3;
                } else {
                    meta = ByteUtil.toIntegerLE(columnMetas, offset, 2);
                    metaLength = 2;
                }
                break;
            default:
                break;
        }
        return new ColumnMeta(originType, meta, isArray ? metaLength + 1 : metaLength, isNotNull);
    }

    public long getTableId() {
        return tableId;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getTableName() {
        return tableName;
    }
}
