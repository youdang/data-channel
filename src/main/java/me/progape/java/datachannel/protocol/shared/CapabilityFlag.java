package me.progape.java.datachannel.protocol.shared;

import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author progape
 * @date 2022-02-04
 */
public enum CapabilityFlag {
    CLIENT_LONG_PASSWORD(0x00000001),
    CLIENT_FOUND_ROWS(0x00000002),
    CLIENT_LONG_FLAG(0x00000004),
    CLIENT_CONNECT_WITH_DB(0x00000008),
    CLIENT_NO_SCHEMA(0x00000010),
    CLIENT_COMPRESS(0x00000020),
    CLIENT_ODBC(0x00000040),
    CLIENT_LOCAL_FILES(0x00000080),
    CLIENT_IGNORE_SPACE(0x00000100),
    CLIENT_PROTOCOL_41(0x00000200),
    CLIENT_INTERACTIVE(0x00000400),
    CLIENT_SSL(0x00000800),
    CLIENT_IGNORE_SIGPIPE(0x00001000),
    CLIENT_TRANSACTIONS(0x00002000),
    CLIENT_RESERVED(0x00004000),
    CLIENT_SECURE_CONNECTION(0x00008000),
    CLIENT_MULTI_STATEMENTS(0x00010000),
    CLIENT_MULTI_RESULTS(0x00020000),
    CLIENT_PS_MULTI_RESULTS(0x00040000),
    CLIENT_PLUGIN_AUTH(0x00080000),
    CLIENT_CONNECT_ATTRS(0x00100000),
    CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA(0x00200000),
    CLIENT_CAN_HANDLE_EXPIRED_PASSWORDS(0x00400000),
    CLIENT_SESSION_TRACK(0x00800000),
    CLIENT_DEPRECATE_EOF(0x01000000),
    ;

    private final long code;

    CapabilityFlag(int code) {
        this.code = code;
    }

    public static long encode(Set<CapabilityFlag> capabilityFlags) {
        return capabilityFlags.stream()
            .map(CapabilityFlag::getCode)
            .reduce((a, b) -> a | b)
            .orElse(0L);
    }

    public static Set<CapabilityFlag> decode(Long capabilityFlags) {
        if (capabilityFlags == null) {
            return Sets.newHashSet();
        }
        return Arrays.stream(values())
            .filter(capabilityFlag -> (capabilityFlag.getCode() & capabilityFlags) != 0)
            .collect(Collectors.toSet());
    }

    public long getCode() {
        return code;
    }
}
