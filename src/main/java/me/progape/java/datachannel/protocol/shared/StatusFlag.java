package me.progape.java.datachannel.protocol.shared;

import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author progape
 * @date 2022-01-27
 */
public enum StatusFlag {
    SERVER_STATUS_IN_TRANS(0x0001),
    SERVER_STATUS_AUTOCOMMIT(0x0002),
    SERVER_MORE_RESULTS_EXISTS(0x0008),
    SERVER_STATUS_NO_GOOD_INDEX_USED(0x0010),
    SERVER_STATUS_NO_INDEX_USED(0x0020),
    SERVER_STATUS_CURSOR_EXISTS(0x0040),
    SERVER_STATUS_LAST_ROW_SENT(0x0080),
    SERVER_STATUS_DB_DROPPED(0x0100),
    SERVER_STATUS_NO_BACKSLASH_ESCAPES(0x0200),
    SERVER_STATUS_METADATA_CHANGED(0x0400),
    SERVER_QUERY_WAS_SLOW(0x0800),
    SERVER_PS_OUT_PARAMS(0x1000),
    SERVER_STATUS_IN_TRANS_READONLY(0x2000),
    SERVER_SESSION_STATE_CHANGED(0x4000),
    ;

    private final int code;

    StatusFlag(int code) {
        this.code = code;
    }

    public static Set<StatusFlag> decode(Integer statusFlags) {
        if (statusFlags == null) {
            return Sets.newHashSet();
        }
        return Arrays.stream(values())
            .filter(statusFlag -> (statusFlag.getCode() & statusFlags) != 0)
            .collect(Collectors.toSet());
    }

    public int getCode() {
        return code;
    }
}
