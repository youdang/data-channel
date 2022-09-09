package me.progape.java.datachannel.protocol.binlog;

import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author progape
 * @date 2022-02-01
 */
public enum BinlogEventFlag {
    LOG_EVENT_BINLOG_IN_USE_F(0x0001),
    LOG_EVENT_FORCED_ROTATE_F(0x0002),
    LOG_EVENT_THREAD_SPECIFIC_F(0x0004),
    LOG_EVENT_SUPPRESS_USE_F(0x0008),
    LOG_EVENT_UPDATE_TABLE_MAP_VERSION_F(0x0010),
    LOG_EVENT_ARTIFICIAL_F(0x0020),
    LOG_EVENT_RELAY_LOG_F(0x0040),
    LOG_EVENT_IGNORABLE_F(0x0080),
    LOG_EVENT_NO_FILTER_F(0x0100),
    LOG_EVENT_MTS_ISOLATE_F(0x0200),
    ;

    private final int value;

    BinlogEventFlag(int value) {
        this.value = value;
    }

    public static Set<BinlogEventFlag> decode(Integer binlogEventFlags) {
        if (binlogEventFlags == null) {
            return Sets.newHashSet();
        }
        return Arrays.stream(values())
            .filter(binlogEventFlag -> (binlogEventFlag.getValue() & binlogEventFlags) != 0)
            .collect(Collectors.toSet());
    }

    public int getValue() {
        return value;
    }
}
