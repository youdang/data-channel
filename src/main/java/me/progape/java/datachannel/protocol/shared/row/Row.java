package me.progape.java.datachannel.protocol.shared.row;

import me.progape.java.datachannel.protocol.query.ColumnDefinition;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author progape
 * @date 2022-03-13
 */
public interface Row {
    ColumnDefinition getColumnDefinition(int index);

    int getColumnCount();

    boolean getBoolean(int index);

    byte getByte(int index);

    short getShort(int index);

    int getInt(int index);

    long getLong(int index);

    float getFloat(int index);

    double getDouble(int index);

    BigInteger getBigInteger(int index);

    BigDecimal getBigDecimal(int index);

    String getString(int index);

    LocalDate getLocalDate(int index);

    LocalTime getLocalTime(int index);

    LocalDateTime getLocalDateTime(int index);

    Date getDate(int index);

    Time getTime(int index);

    Timestamp getTimestamp(int index);
}
