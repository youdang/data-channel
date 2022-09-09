package me.progape.java.datachannel.protocol.shared.row.factory;

import me.progape.java.datachannel.protocol.query.ColumnDefinition;
import me.progape.java.datachannel.protocol.utils.CharsetUtil;
import me.progape.java.datachannel.protocol.utils.DateTimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author progape
 * @date 2022-03-13
 */
public class LocalDateTimeValueFactory extends DefaultValueFactory<LocalDateTime> {
    @Override
    public LocalDateTime createFromYear(long value) {
        return LocalDateTime.of(
            (int) value, 1, 1,
            0, 0, 0, 0
        );
    }

    @Override
    public LocalDateTime createFromDate(LocalDate date) {
        if (date == null) {
            return createFromNull();
        }
        return LocalDateTime.of(
            date.getYear(), date.getMonth(), date.getDayOfMonth(),
            0, 0, 0, 0
        );
    }

    @Override
    public LocalDateTime createFromTime(LocalTime time) {
        if (time == null) {
            return createFromNull();
        }
        return LocalDateTime.of(
            1970, 1, 1,
            time.getHour(), time.getMinute(), time.getSecond(), time.getNano()
        );
    }

    @Override
    public LocalDateTime createFromDateTime(LocalDateTime datetime) {
        if (datetime == null) {
            return createFromNull();
        }
        return datetime;
    }

    @Override
    public LocalDateTime createFromBytes(byte[] value, ColumnDefinition columnDefinition) {
        String decoded;
        try {
            decoded = CharsetUtil.decode(value, columnDefinition.getCollation(), null);
        } catch (Exception e) {
            return createFromNull();
        }

        LocalDateTime dateTime = DateTimeUtil.parseDateTime(decoded);
        if (dateTime != null) {
            return dateTime;
        }
        LocalDate date = DateTimeUtil.parseDate(decoded);
        if (date != null) {
            return createFromDate(date);
        }

        return createFromNull();
    }
}
