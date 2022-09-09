package me.progape.java.datachannel.protocol.shared.row.factory;

import me.progape.java.datachannel.protocol.query.ColumnDefinition;
import me.progape.java.datachannel.protocol.shared.types.geometry.MySQLGeometry;

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
public interface ValueFactory<T> {
    String targetType();

    T createFromNull();

    T createFromLong(long value);

    T createFromBigInteger(BigInteger value);

    T createFromBigDecimal(BigDecimal value);

    T createFromFloat(float value);

    T createFromDouble(double value);

    T createFromYear(long value);

    T createFromDate(LocalDate date);

    T createFromTime(LocalTime time);

    T createFromDateTime(LocalDateTime timestamp);

    T createFromBytes(byte[] value, ColumnDefinition columnDefinition);

    T createFromByteArray(byte[] value);

    T createFromObject(Object json);

    T createFromBit(byte[] value);

    T createFromSet(Set<String> value);

    T createFromEnum(String enumValue);

    T createFromBoolean(boolean value);

    T createFromGeometry(MySQLGeometry geometry, ColumnDefinition columnDefinition);

    default long bitToLong(byte[] bits) {
        long valueAsLong = 0;
        for (byte bit : bits) {
            valueAsLong = valueAsLong << 8 | bit & 0xff;
        }
        return valueAsLong;
    }
}
