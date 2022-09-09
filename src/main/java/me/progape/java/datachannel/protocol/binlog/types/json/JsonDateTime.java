package me.progape.java.datachannel.protocol.binlog.types.json;

import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.protocol.binlog.ColumnType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author progape
 * @date 2022-05-08
 */
public class JsonDateTime {
    private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter HH_MM_SS = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Object convertToString(long data, ColumnType columnType) {
        boolean negative = false;
        if (data < 0) {
            data = -data;
            negative = true;
        }

        switch (columnType) {
            case TIME: {
                long hms = data >>> 24;
                return HH_MM_SS.format(LocalTime.of(
                    (int) ((hms >> 12) % (1 << 10)), (int) ((hms >> 6) % (1 << 6)), (int) (hms % (1 << 6)),
                    (int) (data % (1L << 24))
                ));
            }
            case DATE: {
                long ymdhms = data >>> 24;
                long ymd = ymdhms >> 17;
                long ym = ymd >> 5;
                return YYYY_MM_DD.format(LocalDate.of(
                    (int) (ym / 13), (int) (ym % 13), (int) (ymd % (1 << 5))
                ));
            }
            case DATETIME:
            case TIMESTAMP: {
                long ymdhms = data >>> 24;
                long ymd = ymdhms >> 17;
                long ym = ymd >> 5;
                long hms = ymdhms % (1 << 17);
                return YYYY_MM_DD_HH_MM_SS.format(LocalDateTime.of(
                    (int) (ym / 13), (int) (ym % 13), (int) (ymd % (1 << 5)),
                    (int) (hms >> 12), (int) ((hms >> 6) % (1 << 6)), (int) (hms % (1 << 6)),
                    (int) (data % (1L << 24))
                ));
            }
            default:
                throw new ProtocolException("invalid JSON value");
        }
    }
}
