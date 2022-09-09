package me.progape.java.datachannel.protocol.shared.row.factory;

import me.progape.java.datachannel.protocol.query.ColumnDefinition;
import me.progape.java.datachannel.protocol.utils.CharsetUtil;
import me.progape.java.datachannel.protocol.utils.DateTimeUtil;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author progape
 * @date 2022-03-13
 */
public class TimestampValueFactory extends DefaultValueFactory<Timestamp> {
    @Override
    public Timestamp createFromYear(long value) {
        return createFromDate(LocalDate.of((int) value, 1, 1));
    }

    @Override
    public Timestamp createFromDate(LocalDate date) {
        if (date == null) {
            return createFromNull();
        }
        return new Timestamp(Instant.from(date).toEpochMilli());
    }

    @Override
    public Timestamp createFromTime(LocalTime time) {
        if (time == null) {
            return createFromNull();
        }
        return new Timestamp(Instant.from(time).toEpochMilli());
    }

    @Override
    public Timestamp createFromDateTime(LocalDateTime datetime) {
        if (datetime == null) {
            return createFromNull();
        }
        return new Timestamp(Instant.from(datetime).toEpochMilli());
    }

    @Override
    public Timestamp createFromBytes(byte[] value, ColumnDefinition columnDefinition) {
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
        LocalTime time = DateTimeUtil.parseTime(decoded);
        if (time != null) {
            return createFromTime(time);
        }

        return createFromNull();
    }
}
