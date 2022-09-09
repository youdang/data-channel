package me.progape.java.datachannel.protocol.shared.row.factory;

import me.progape.java.datachannel.protocol.query.ColumnDefinition;
import me.progape.java.datachannel.protocol.utils.CharsetUtil;
import me.progape.java.datachannel.protocol.utils.DateTimeUtil;

import java.sql.Time;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author progape
 * @date 2022-03-13
 */
public class TimeValueFactory extends DefaultValueFactory<Time> {
    @Override
    public Time createFromYear(long value) {
        return createFromDate(LocalDate.of((int) value, 1, 1));
    }

    @Override
    public Time createFromDate(LocalDate date) {
        if (date == null) {
            return createFromNull();
        }
        return new Time(Instant.from(date).toEpochMilli());
    }

    @Override
    public Time createFromTime(LocalTime time) {
        if (time == null) {
            return createFromNull();
        }
        return new Time(Instant.from(time).toEpochMilli());
    }

    @Override
    public Time createFromDateTime(LocalDateTime datetime) {
        if (datetime == null) {
            return createFromNull();
        }
        return new Time(Instant.from(datetime).toEpochMilli());
    }

    @Override
    public Time createFromBytes(byte[] value, ColumnDefinition columnDefinition) {
        String decoded;
        try {
            decoded = CharsetUtil.decode(value, columnDefinition.getCollation(), null);
        } catch (Exception e) {
            return createFromNull();
        }

        LocalDateTime dateTime = DateTimeUtil.parseDateTime(decoded);
        if (dateTime != null) {
            return createFromDateTime(dateTime);
        }
        LocalTime time = DateTimeUtil.parseTime(decoded);
        if (time != null) {
            return createFromTime(time);
        }

        return createFromNull();
    }
}
