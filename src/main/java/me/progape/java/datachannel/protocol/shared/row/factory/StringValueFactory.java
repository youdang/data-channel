package me.progape.java.datachannel.protocol.shared.row.factory;

import com.google.common.io.BaseEncoding;
import me.progape.java.datachannel.protocol.query.ColumnDefinition;
import me.progape.java.datachannel.protocol.shared.types.geometry.MySQLGeometry;
import me.progape.java.datachannel.protocol.utils.CharsetUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * @author progape
 * @date 2022-03-13
 */
public class StringValueFactory extends DefaultValueFactory<String> {
    private static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("HH:mm:ss.n");
    private static final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.n");

    @Override
    public String createFromLong(long value) {
        return String.valueOf(value);
    }

    @Override
    public String createFromBigInteger(BigInteger value) {
        return value.toString();
    }

    @Override
    public String createFromBigDecimal(BigDecimal value) {
        return value.toString();
    }

    @Override
    public String createFromFloat(float value) {
        return String.valueOf(value);
    }

    @Override
    public String createFromDouble(double value) {
        return String.valueOf(value);
    }

    @Override
    public String createFromYear(long value) {
        return createFromLong(value);
    }

    @Override
    public String createFromDate(LocalDate date) {
        return DATE_PATTERN.format(date);
    }

    @Override
    public String createFromTime(LocalTime time) {
        return TIME_PATTERN.format(time);
    }

    @Override
    public String createFromDateTime(LocalDateTime datetime) {
        return DATE_TIME_PATTERN.format(datetime);
    }

    @Override
    public String createFromBytes(byte[] value, ColumnDefinition columnDefinition) {
        return CharsetUtil.decode(value, columnDefinition.getCollation(), null);
    }

    @Override
    public String createFromByteArray(byte[] value) {
        return BaseEncoding.base16().encode(value);
    }

    @Override
    public String createFromObject(Object value) {
        return value.toString();
    }

    @Override
    public String createFromBit(byte[] value) {
        return createFromLong(bitToLong(value));
    }

    @Override
    public String createFromSet(Set<String> value) {
        if (value == null) {
            return null;
        }
        return String.join(",", value);
    }

    @Override
    public String createFromEnum(String enumValue) {
        return enumValue;
    }

    @Override
    public String createFromBoolean(boolean value) {
        return String.valueOf(value);
    }

    @Override
    public String createFromGeometry(MySQLGeometry geometry, ColumnDefinition columnDefinition) {
        if (geometry == null) {
            return createFromNull();
        }
        return geometry.toString();
    }
}
