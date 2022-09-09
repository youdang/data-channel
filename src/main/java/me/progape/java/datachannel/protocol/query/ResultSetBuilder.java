package me.progape.java.datachannel.protocol.query;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.protocol.Context;
import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.protocol.binlog.ColumnType;
import me.progape.java.datachannel.protocol.shared.CapabilityFlag;
import me.progape.java.datachannel.protocol.shared.Collation;
import me.progape.java.datachannel.protocol.shared.EofResponse;
import me.progape.java.datachannel.protocol.shared.ErrorResponse;
import me.progape.java.datachannel.protocol.shared.OkResponse;
import me.progape.java.datachannel.protocol.shared.StatusFlag;
import me.progape.java.datachannel.transport.Packet;
import me.progape.java.datachannel.transport.StreamFinisher;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;
import me.progape.java.datachannel.protocol.utils.CharsetUtil;

import java.util.List;
import java.util.Set;

/**
 * @author progape
 * @date 2022-02-13
 */
public class ResultSetBuilder {
    private static final int STATUS_FAILED = -1;
    private static final int STATUS_FINISHED = 0;
    private static final int STATUS_READING_COLUMN_COUNT = 1;
    private static final int STATUS_READING_COLUMN_DEFINITIONS = 2;
    private static final int STATUS_READING_EOF = 3;
    private static final int STATUS_READING_ROWS = 4;

    private final StreamFinisher finisher;
    private final ResultSet resultSet;
    private ResultSet current;

    private int status;
    private int columnCount;

    public ResultSetBuilder(StreamFinisher finisher) {
        this.finisher = finisher;
        this.resultSet = new ResultSet();
        this.current = this.resultSet;

        this.status = STATUS_READING_COLUMN_COUNT;
        this.columnCount = 0;
    }

    public ResultSetBuilder readPacket(Packet packet, Context context) {
        if (packet.isError()) {
            this.status = STATUS_FAILED;
            this.finisher.finish();
            throw new ProtocolException(new ErrorResponse(packet, context));
        }

        switch (status) {
            case STATUS_READING_COLUMN_COUNT:
                readColumnCount(packet);
                break;
            case STATUS_READING_COLUMN_DEFINITIONS:
                readColumnDefinitions(packet, context);
                break;
            case STATUS_READING_EOF:
                readColumnDefinitionEof(packet);
                break;
            case STATUS_READING_ROWS:
                readRows(packet, context);
                break;
            default:
                throw new ProtocolException("invalid packet sequence");
        }
        return this;
    }

    public ResultSet build() {
        return resultSet;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void readColumnCount(Packet packet) {
        this.columnCount = (int) ByteBufUtil.readLengthEncodedInt(packet.getPayload());
        this.status = STATUS_READING_COLUMN_DEFINITIONS;
    }

    private void readColumnDefinitions(Packet packet, Context context) {
        this.current.addColumnDefinition(readColumnDefinition(packet.getPayload(), context));
        if (this.current.getColumnDefinitions().size() == this.columnCount) {
            // end of column definitions
            if (context.getCapabilityFlags().contains(CapabilityFlag.CLIENT_DEPRECATE_EOF)) {
                this.status = STATUS_READING_ROWS;
            } else {
                this.status = STATUS_READING_EOF;
            }
        }
    }

    private void readColumnDefinitionEof(Packet packet) {
        if (!packet.isEof()) {
            this.status = STATUS_FAILED;
            finisher.finish();
            throw new ProtocolException("unexpected packet");
        }
        this.status = STATUS_READING_ROWS;
    }

    private void readRows(Packet packet, Context context) {
        if (packet.isOk() || packet.isEof()) {
            Set<StatusFlag> statusFlags = packet.isOk()
                ? new OkResponse(packet, context).getStatusFlags()
                : new EofResponse(packet, context).getStatusFlags();

            if (statusFlags != null && statusFlags.contains(StatusFlag.SERVER_MORE_RESULTS_EXISTS)) {
                // has more result set
                this.status = STATUS_READING_COLUMN_COUNT;

                ResultSet next = new ResultSet();
                this.current.setNext(next);
                this.current = next;
                this.columnCount = 0;
            } else {
                this.status = STATUS_FINISHED;
                this.finisher.finish();
            }
            return;
        } else if (packet.isError()) {
            this.status = STATUS_FAILED;
            this.finisher.finish();
            throw new ProtocolException(new ErrorResponse(packet, context));
        }

        this.current.addRow(readRow(packet.getPayload()));
    }

    private ColumnDefinition readColumnDefinition(ByteBuf payload, Context context) {
        // skip catalog field
        byte[] catalogName = ByteBufUtil.readLengthEncodedString(payload);
        byte[] schemaName = ByteBufUtil.readLengthEncodedString(payload);
        byte[] tableName = ByteBufUtil.readLengthEncodedString(payload);
        byte[] originTableName = ByteBufUtil.readLengthEncodedString(payload);
        byte[] columnName = ByteBufUtil.readLengthEncodedString(payload);
        byte[] originColumnName = ByteBufUtil.readLengthEncodedString(payload);

        // skip 0x0C
        ByteBufUtil.readLengthEncodedInt(payload);

        Collation collation = Collation.idOf(ByteBufUtil.readInt2(payload));
        int columnLength = (int) ByteBufUtil.readInt4(payload);
        ColumnType columnType = ColumnType.codeOf((int) ByteBufUtil.readInt1(payload));
        Set<ColumnDefinitionFlag> flags = ColumnDefinitionFlag.decode(ByteBufUtil.readInt2(payload));
        int decimals = ByteBufUtil.readInt1(payload);

        // @see mysql-connector-java's NativeProtocol.java
        boolean isUnsigned = flags.contains(ColumnDefinitionFlag.UNSIGNED_FLAG);
        boolean isBinary = flags.contains(ColumnDefinitionFlag.BINARY_FLAG);
        boolean isImplicitTemporaryTable = tableName.length > 0 && CharsetUtil.decode(tableName, collation, context).startsWith("#sql_");
        boolean isOpaqueBinary =
            (isBinary
                && collation == Collation.BINARY
                && (columnType == ColumnType.STRING || columnType == ColumnType.VAR_STRING || columnType == ColumnType.VARCHAR))
            ? !isImplicitTemporaryTable
            : "binary".equalsIgnoreCase(collation.getCharacterSet().getMatchedJavaEncoding(null));
        MySQLType mySQLType = MySQLType.of(columnType, collation, columnLength, isUnsigned, isBinary, isOpaqueBinary);

        switch (mySQLType) {
            case TINYINT:
            case TINYINT_UNSIGNED:
            case SMALLINT:
            case SMALLINT_UNSIGNED:
            case MEDIUMINT:
            case MEDIUMINT_UNSIGNED:
            case INT:
            case INT_UNSIGNED:
            case BIGINT:
            case BIGINT_UNSIGNED:
            case BOOLEAN:
                columnLength = (int) mySQLType.getPrecision();
                break;
            case DECIMAL:
                columnLength--;
                if (decimals > 0) {
                    columnLength--;
                }
                break;
            case DECIMAL_UNSIGNED:
                if (decimals > 0) {
                    columnLength--;
                }
                break;
            case FLOAT:
            case FLOAT_UNSIGNED:
            case DOUBLE:
            case DOUBLE_UNSIGNED:
                if (decimals == 31) {
                    decimals = 0;
                }
                break;
            default:
                break;
        }

        // skip filler
        ByteBufUtil.readInt2(payload);

        ColumnDefinition columnDefinition = new ColumnDefinition();
        columnDefinition.setCatalogName(CharsetUtil.decode(catalogName, collation, context));
        columnDefinition.setSchemaName(CharsetUtil.decode(schemaName, collation, context));
        columnDefinition.setTableName(CharsetUtil.decode(originTableName, collation, context));
        columnDefinition.setTableNameAlias(CharsetUtil.decode(tableName, collation, context));
        columnDefinition.setName(CharsetUtil.decode(originColumnName, collation, context));
        columnDefinition.setNameAlias(CharsetUtil.decode(columnName, collation, context));
        columnDefinition.setType(columnType);
        columnDefinition.setMySQLType(mySQLType);
        columnDefinition.setCollation(collation);
        columnDefinition.setLength(columnLength);
        columnDefinition.setDecimals(decimals);
        columnDefinition.setFlags(flags);

        return columnDefinition;
    }

    private List<byte[]> readRow(ByteBuf payload) {
        List<byte[]> row = Lists.newArrayList();
        while (payload.readableBytes() > 0) {
            if (payload.getUnsignedByte(payload.readerIndex()) == 0xFB) {
                ByteBufUtil.readInt1(payload);
                row.add(null);
            } else {
                row.add(ByteBufUtil.readLengthEncodedString(payload));
            }
        }
        return row;
    }
}
