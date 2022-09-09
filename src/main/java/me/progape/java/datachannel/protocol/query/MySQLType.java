package me.progape.java.datachannel.protocol.query;

import me.progape.java.datachannel.protocol.binlog.ColumnType;
import me.progape.java.datachannel.protocol.shared.Collation;

/**
 * @author progape
 * @date 2022-03-13
 */
public enum MySQLType {
    DECIMAL(ColumnType.DECIMAL, 65L),
    DECIMAL_UNSIGNED(ColumnType.DECIMAL, 65L),
    BOOLEAN(ColumnType.TINY, 3L),
    TINYINT(ColumnType.TINY, 3L),
    TINYINT_UNSIGNED(ColumnType.TINY, 3L),
    SMALLINT(ColumnType.SHORT, 5L),
    SMALLINT_UNSIGNED(ColumnType.SHORT, 5L),
    MEDIUMINT(ColumnType.INT24, 7L),
    MEDIUMINT_UNSIGNED(ColumnType.INT24, 8L),
    INT(ColumnType.LONG, 10L),
    INT_UNSIGNED(ColumnType.LONG, 10L),
    BIGINT(ColumnType.LONGLONG, 19L),
    BIGINT_UNSIGNED(ColumnType.LONGLONG, 20L),
    FLOAT(ColumnType.FLOAT, 12L),
    FLOAT_UNSIGNED(ColumnType.FLOAT, 12L),
    DOUBLE(ColumnType.DOUBLE, 22L),
    DOUBLE_UNSIGNED(ColumnType.DOUBLE, 22L),
    NULL(ColumnType.NULL, 0L),
    TIMESTAMP(ColumnType.TIMESTAMP, 26L),
    DATE(ColumnType.DATE, 10L),
    TIME(ColumnType.TIME, 16L),
    DATETIME(ColumnType.DATETIME, 26L),
    YEAR(ColumnType.YEAR, 4L),
    VARCHAR(ColumnType.VARCHAR, 65535L),
    VARBINARY(ColumnType.VAR_STRING, 65535L),
    BIT(ColumnType.BIT, 1L),
    JSON(ColumnType.JSON, 1073741824L),
    ENUM(ColumnType.ENUM, 65535L),
    SET(ColumnType.SET, 64L),
    TINYBLOB(ColumnType.TINY_BLOB, 255L),
    TINYTEXT(ColumnType.TINY_BLOB, 255L),
    MEDIUMBLOB(ColumnType.MEDIUM_BLOB, 16777215L),
    MEDIUMTEXT(ColumnType.MEDIUM_BLOB, 16777215L),
    LONGBLOB(ColumnType.LONG_BLOB, 4294967295L),
    LONGTEXT(ColumnType.LONG_BLOB, 4294967295L),
    BLOB(ColumnType.BLOB, 65535L),
    TEXT(ColumnType.BLOB, 65535L),
    CHAR(ColumnType.STRING, 255L),
    BINARY(ColumnType.STRING, 255L),
    GEOMETRY(ColumnType.GEOMETRY, 65535L),
    UNKNOWN(null, 65535L),
    ;

    private final ColumnType columnType;
    private final long precision;

    MySQLType(ColumnType columnType, long precision) {
        this.columnType = columnType;
        this.precision = precision;
    }

    public static MySQLType of(ColumnType columnType, Collation collation, int length, boolean isUnsigned, boolean isBinary, boolean isOpaqueBinary) {
        switch (columnType) {
            case DECIMAL:
            case NEWDECIMAL:
                return isUnsigned ? DECIMAL_UNSIGNED : DECIMAL;
            case TINY:
                if (!isUnsigned && length == 1) {
                    return BIT;
                }
                return isUnsigned ? TINYINT_UNSIGNED : TINYINT;
            case SHORT:
                return isUnsigned ? SMALLINT_UNSIGNED : SMALLINT;
            case INT24:
                return isUnsigned ? MEDIUMINT_UNSIGNED : MEDIUMINT;
            case LONG:
                return isUnsigned ? INT_UNSIGNED : INT;
            case LONGLONG:
                return isUnsigned ? BIGINT_UNSIGNED : BIGINT;
            case FLOAT:
                return isUnsigned ? FLOAT_UNSIGNED : FLOAT;
            case DOUBLE:
                return isUnsigned ? DOUBLE_UNSIGNED : DOUBLE;
            case NULL:
                return NULL;
            case DATE:
                return DATE;
            case TIME:
                return TIME;
            case DATETIME:
                return DATETIME;
            case YEAR:
                return YEAR;
            case TIMESTAMP:
                return TIMESTAMP;
            case VARCHAR:
            case VAR_STRING:
                if (isOpaqueBinary) {
                    return VARBINARY;
                }
                return VARCHAR;
            case BIT:
                return BIT;
            case SET:
                return SET;
            case ENUM:
                return ENUM;
            case JSON:
                return JSON;
            case TINY_BLOB:
                if (!isBinary || collation != Collation.BINARY) {
                    return TINYTEXT;
                }
                return TINYBLOB;
            case MEDIUM_BLOB:
                if (!isBinary || collation != Collation.BINARY) {
                    return MEDIUMTEXT;
                }
                return MEDIUMBLOB;
            case BLOB:
                ColumnType fixedColumnType;
                if (length <= TINYBLOB.getPrecision()) {
                    fixedColumnType = ColumnType.TINY_BLOB;
                } else if (length <= BLOB.getPrecision()) {
                    if (!isBinary || collation != Collation.BINARY) {
                        return TEXT;
                    }
                    return BLOB;
                } else if (length <= MEDIUMBLOB.getPrecision()) {
                    fixedColumnType = ColumnType.MEDIUM_BLOB;
                } else {
                    fixedColumnType = ColumnType.LONG_BLOB;
                }
                return of(fixedColumnType, collation, length, isUnsigned, isBinary, isOpaqueBinary);
            case LONG_BLOB:
                if (!isBinary || collation != Collation.BINARY) {
                    return LONGTEXT;
                }
                return LONGBLOB;
            case STRING:
                if (isOpaqueBinary) {
                    return BINARY;
                }
                return CHAR;
            case GEOMETRY:
                return GEOMETRY;
            default:
                return UNKNOWN;
        }
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public long getPrecision() {
        return precision;
    }
}
