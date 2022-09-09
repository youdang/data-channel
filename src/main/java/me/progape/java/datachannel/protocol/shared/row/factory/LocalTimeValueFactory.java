package me.progape.java.datachannel.protocol.shared.row.factory;

import me.progape.java.datachannel.protocol.query.ColumnDefinition;
import me.progape.java.datachannel.protocol.utils.CharsetUtil;
import me.progape.java.datachannel.protocol.utils.DateTimeUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author progape
 * @date 2022-03-13
 */
public class LocalTimeValueFactory extends DefaultValueFactory<LocalTime> {
    @Override
    public LocalTime createFromDateTime(LocalDateTime datetime) {
        if (datetime == null) {
            return createFromNull();
        }
        return datetime.toLocalTime();
    }

    @Override
    public LocalTime createFromBytes(byte[] value, ColumnDefinition columnDefinition) {
        String decoded;
        try {
            decoded = CharsetUtil.decode(value, columnDefinition.getCollation(), null);
        } catch (Exception e) {
            return createFromNull();
        }

        LocalTime time = DateTimeUtil.parseTime(decoded);
        if (time != null) {
            return time;
        }
        LocalDateTime dateTime = DateTimeUtil.parseDateTime(decoded);
        if (dateTime != null) {
            return dateTime.toLocalTime();
        }

        return createFromNull();
    }
}
