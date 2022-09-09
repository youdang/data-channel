package me.progape.java.datachannel.protocol.shared.row.factory;

import me.progape.java.datachannel.protocol.query.ColumnDefinition;
import me.progape.java.datachannel.protocol.utils.CharsetUtil;
import me.progape.java.datachannel.protocol.utils.DateTimeUtil;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author progape
 * @date 2022-03-13
 */
public class DateValueFactory extends DefaultValueFactory<Date> {
    @Override
    public Date createFromYear(long value) {
        return createFromDateTime(LocalDateTime.of(
            (int)value, 1, 1,
            0, 0, 0, 0
        ));
    }

    @Override
    public Date createFromDate(LocalDate date) {
        if (date == null) {
            return createFromNull();
        }
        return createFromDateTime(LocalDateTime.of(
            date.getYear(), date.getMonth(), date.getDayOfMonth(),
            0, 0, 0, 0
        ));
    }

    @Override
    public Date createFromTime(LocalTime time) {
        if (time == null) {
            return createFromNull();
        }
        return createFromDateTime(LocalDateTime.of(
            1970, 1, 1,
            time.getHour(), time.getMinute(), time.getSecond(), time.getNano()
        ));
    }

    @Override
    public Date createFromDateTime(LocalDateTime datetime) {
        if (datetime == null) {
            return createFromNull();
        }
        return new Date(Instant.from(datetime).toEpochMilli());
    }

    @Override
    public Date createFromBytes(byte[] value, ColumnDefinition columnDefinition) {
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
        LocalDate date = DateTimeUtil.parseDate(decoded);
        if (date != null) {
            return createFromDate(date);
        }

        return createFromNull();
    }
}
