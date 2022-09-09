package me.progape.java.datachannel.protocol.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author progape
 * @date 2022-05-04
 */
public class DateTimeUtil {
    private static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter HH_MM_SS = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static LocalDateTime parseDateTime(String str) {
        try {
            return LocalDateTime.parse(str, YYYY_MM_DD_HH_MM_SS);
        } catch (Exception e) {
            return null;
        }
    }

    public static LocalDate parseDate(String str) {
        try {
            return LocalDate.parse(str, YYYY_MM_DD);
        } catch (Exception e) {
            return null;
        }
    }

    public static LocalTime parseTime(String str) {
        try {
            return LocalTime.parse(str, HH_MM_SS);
        } catch (Exception e) {
            return null;
        }
    }
}
