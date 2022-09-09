package me.progape.java.datachannel.protocol.shared.row.factory;

import me.progape.java.datachannel.protocol.query.ColumnDefinition;
import me.progape.java.datachannel.protocol.shared.types.geometry.MySQLGeometry;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

/**
 * @author progape
 * @date 2022-03-13
 */
public abstract class DefaultValueFactory<T> implements ValueFactory<T> {
    private final Type targetType;

    public DefaultValueFactory() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new IllegalArgumentException("not actual type information");
        } else {
            targetType = ((ParameterizedType) superclass).getActualTypeArguments()[0];
        }
    }

    @Override
    public String targetType() {
        return targetType.getTypeName();
    }

    @Override
    public T createFromNull() {
        return null;
    }

    @Override
    public T createFromLong(long value) {
        return unsupported("LONG");
    }

    @Override
    public T createFromBigInteger(BigInteger value) {
        return unsupported("BIG_INTEGER");
    }

    @Override
    public T createFromBigDecimal(BigDecimal value) {
        return unsupported("BIG_DECIMAL");
    }

    @Override
    public T createFromFloat(float value) {
        return unsupported("FLOAT");
    }

    @Override
    public T createFromDouble(double value) {
        return unsupported("DOUBLE");
    }

    @Override
    public T createFromYear(long value) {
        return unsupported("YEAR");
    }

    @Override
    public T createFromDate(LocalDate date) {
        return unsupported("DATE");
    }

    @Override
    public T createFromTime(LocalTime time) {
        return unsupported("TIME");
    }

    @Override
    public T createFromDateTime(LocalDateTime datetime) {
        return unsupported("DATETIME");
    }

    @Override
    public T createFromBytes(byte[] value, ColumnDefinition columnDefinition) {
        return unsupported("BYTES");
    }

    @Override
    public T createFromByteArray(byte[] value) {
        return unsupported("BYTE_ARRAY");
    }

    @Override
    public T createFromObject(Object value) {
        return unsupported("OBJECT");
    }

    @Override
    public T createFromBit(byte[] value) {
        return unsupported("BIT");
    }

    @Override
    public T createFromSet(Set<String> value) {
        return unsupported("SET");
    }

    @Override
    public T createFromEnum(String enumValue) {
        return unsupported("ENUM");
    }

    @Override
    public T createFromBoolean(boolean value) {
        return unsupported("BOOLEAN");
    }

    @Override
    public T createFromGeometry(MySQLGeometry geometry, ColumnDefinition columnDefinition) {
        return unsupported("GEOMETRY");
    }

    private T unsupported(String sourceType) {
        throw new UnsupportedOperationException("unsupported conversion from " + sourceType + " to " + targetType());
    }
}
