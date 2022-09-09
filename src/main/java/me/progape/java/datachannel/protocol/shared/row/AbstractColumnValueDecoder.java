package me.progape.java.datachannel.protocol.shared.row;

import com.google.common.base.Preconditions;
import me.progape.java.datachannel.protocol.binlog.ColumnType;
import me.progape.java.datachannel.protocol.query.ColumnDefinition;
import me.progape.java.datachannel.protocol.shared.row.factory.ValueFactory;

import java.util.List;

/**
 * @author progape
 * @date 2022-03-28
 */
public abstract class AbstractColumnValueDecoder implements ColumnValueDecoder {
    protected final List<ColumnDefinition> columnDefinitions;

    protected AbstractColumnValueDecoder(List<ColumnDefinition> columnDefinitions) {
        this.columnDefinitions = columnDefinitions;
    }

    @Override
    public <T> T decode(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        if (rawValue == null) {
            return valueFactory.createFromNull();
        }

        ColumnDefinition columnDefinition = this.columnDefinitions.get(index);
        ColumnType columnType = getColumnType(index);
        boolean isUnsigned = columnDefinition.isUnsigned();

        switch (columnType) {
            case DECIMAL:
                return decodeDecimal(index, rawValue, valueFactory);
            case TINY:
                return decodeTiny(index, rawValue, isUnsigned, valueFactory);
            case SHORT:
                return decodeShort(index, rawValue, isUnsigned, valueFactory);
            case LONG:
                return decodeLong(index, rawValue, isUnsigned, valueFactory);
            case FLOAT:
                return decodeFloat(index, rawValue, valueFactory);
            case DOUBLE:
                return decodeDouble(index, rawValue, valueFactory);
            case NULL:
                return valueFactory.createFromNull();
            case TIMESTAMP:
                return decodeTimestamp(index, rawValue, valueFactory);
            case LONGLONG:
                return decodeLongLong(index, rawValue, isUnsigned, valueFactory);
            case INT24:
                return decodeInt24(index, rawValue, isUnsigned, valueFactory);
            case DATE:
                return decodeDate(index, rawValue, valueFactory);
            case TIME:
                return decodeTime(index, rawValue, valueFactory);
            case DATETIME:
                return decodeDateTime(index, rawValue, valueFactory);
            case YEAR:
                return decodeYear(index, rawValue, valueFactory);
            case NEWDATE:
                return decodeNewDate(index, rawValue, valueFactory);
            case VARCHAR:
                return decodeVarchar(index, rawValue, valueFactory);
            case BIT:
                return decodeBit(index, rawValue, valueFactory);
            case TIMESTAMP2:
                return decodeTimestamp2(index, rawValue, valueFactory);
            case DATETIME2:
                return decodeDateTime2(index, rawValue, valueFactory);
            case TIME2:
                return decodeTime2(index, rawValue, valueFactory);
            case TYPED_ARRAY:
                return decodeTypedArray(index, rawValue, valueFactory);
            case JSON:
                return decodeJson(index, rawValue, valueFactory);
            case NEWDECIMAL:
                return decodeNewDecimal(index, rawValue, valueFactory);
            case ENUM:
                return decodeEnum(index, rawValue, columnDefinition.getEnumItems(), valueFactory);
            case SET:
                return decodeSet(index, rawValue, columnDefinition.getSetItems(), valueFactory);
            case TINY_BLOB:
                return decodeTinyBlob(index, rawValue, valueFactory);
            case MEDIUM_BLOB:
                return decodeMediumBlob(index, rawValue, valueFactory);
            case LONG_BLOB:
                return decodeLongBlob(index, rawValue, valueFactory);
            case BLOB:
                return decodeBlob(index, rawValue, valueFactory);
            case VAR_STRING:
                return decodeVarString(index, rawValue, valueFactory);
            case STRING:
                return decodeString(index, rawValue, valueFactory);
            case GEOMETRY:
                return decodeGeometry(index, rawValue, valueFactory);
        }
        return valueFactory.createFromNull();
    }

    protected abstract ColumnType getColumnType(int index);

    protected abstract <T> T decodeDecimal(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeTiny(int index, byte[] rawValue, boolean isUnsigned, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeShort(int index, byte[] rawValue, boolean isUnsigned, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeLong(int index, byte[] rawValue, boolean isUnsigned, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeFloat(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeDouble(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeTimestamp(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeLongLong(int index, byte[] rawValue, boolean isUnsigned, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeInt24(int index, byte[] rawValue, boolean isUnsigned, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeDate(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeTime(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeDateTime(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeYear(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeNewDate(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeVarchar(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeBit(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeTimestamp2(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeDateTime2(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeTime2(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeTypedArray(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeJson(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeNewDecimal(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeEnum(int index, byte[] rawValue, List<String> enumItems, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeSet(int index, byte[] rawValue, List<String> setItems, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeTinyBlob(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeMediumBlob(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeLongBlob(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeBlob(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeVarString(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeString(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected abstract <T> T decodeGeometry(int index, byte[] rawValue, ValueFactory<T> valueFactory);

    protected void checkLength(byte[] rawValue, int... expect) {
        Preconditions.checkNotNull(rawValue);
        for (int i : expect) {
            if (rawValue.length == i) {
                return;
            }
        }
        throw new IllegalArgumentException();
    }
}
