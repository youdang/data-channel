package me.progape.java.datachannel.protocol.shared.row.factory;

import me.progape.java.datachannel.protocol.query.ColumnDefinition;
import me.progape.java.datachannel.protocol.utils.CharsetUtil;
import me.progape.java.datachannel.protocol.utils.DateTimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author progape
 * @date 2022-03-13
 */
public class LocalDateValueFactory extends DefaultValueFactory<LocalDate> {
    @Override
    public LocalDate createFromYear(long value) {
        return LocalDate.of((int) value, 1, 1);
    }

    @Override
    public LocalDate createFromDate(LocalDate date) {
        if (date == null) {
            return createFromNull();
        }
        return date;
    }

    @Override
    public LocalDate createFromDateTime(LocalDateTime datetime) {
        if (datetime == null) {
            return createFromNull();
        }
        return datetime.toLocalDate();
    }

    @Override
    public LocalDate createFromBytes(byte[] value, ColumnDefinition columnDefinition) {
        String decoded;
        try {
            decoded = CharsetUtil.decode(value, columnDefinition.getCollation(), null);
        } catch (Exception e) {
            return createFromNull();
        }

        LocalDate date = DateTimeUtil.parseDate(decoded);
        if (date != null) {
            return date;
        }
        LocalDateTime dateTime = DateTimeUtil.parseDateTime(decoded);
        if (dateTime != null) {
            return dateTime.toLocalDate();
        }

        return createFromNull();
    }
}
