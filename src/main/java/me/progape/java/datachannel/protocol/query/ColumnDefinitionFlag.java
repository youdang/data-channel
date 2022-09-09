package me.progape.java.datachannel.protocol.query;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @author progape
 * @date 2022-02-13
 */
public enum ColumnDefinitionFlag {
    NOT_NULL_FLAG(1),
    PRI_KEY_FLAG(2),
    UNIQUE_KEY_FLAG(4),
    MULTIPLE_KEY_FLAG(8),
    BLOB_FLAG(16),
    UNSIGNED_FLAG(32),
    ZEROFILL_FLAG(64),
    BINARY_FLAG(128),
    ENUM_FLAG(256),
    AUTO_INCREMENT_FLAG(512),
    TIMESTAMP_FLAG(1024),
    SET_FLAG(2048),
    NO_DEFAULT_VALUE_FLAG(4096),
    ON_UPDATE_NOW_FLAG(8192),
    NUM_FLAG(32768),
    PART_KEY_FLAG(16384),
    GROUP_FLAG(32768),
    UNIQUE_FLAG(65536),
    BINCMP_FLAG(131072),
    ;

    private final int code;

    ColumnDefinitionFlag(int code) {
        this.code = code;
    }

    public static Set<ColumnDefinitionFlag> decode(Integer codes) {
        Set<ColumnDefinitionFlag> result = Sets.newHashSet();
        if (codes == null) {
            return result;
        }
        for (ColumnDefinitionFlag flag : values()) {
            if ((codes & flag.code) != 0) {
                result.add(flag);
            }
        }
        return result;
    }
}
