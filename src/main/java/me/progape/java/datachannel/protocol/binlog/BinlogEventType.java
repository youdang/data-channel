package me.progape.java.datachannel.protocol.binlog;

/**
 * @author progape
 * @date 2022-02-06
 */
public enum BinlogEventType {
    ROTATE_EVENT(0x04, RotateEvent.class),
    FORMAT_DESCRIPTION_EVENT(0x0F, FormatDescriptionEvent.class),
    XID_EVENT(0x10, XidEvent.class),
    TABLE_MAP_EVENT(0x13, TableMapEvent.class),
    WRITE_ROWS_EVENTv2(0x1E, WriteRowsEventV2.class),
    UPDATE_ROWS_EVENTv2(0x1F, UpdateRowsEventV2.class),
    DELETE_ROWS_EVENTv2(0x20, DeleteRowsEventV2.class),

    /**
     * TRANSACTION_PAYLOAD_EVENT = 0x28
     */
    ENUM_END_EVENT(0x29, null)
    ;

    private final int code;
    private final Class<? extends BinlogEvent> eventClass;

    BinlogEventType(int code, Class<? extends BinlogEvent> eventClass) {
        this.code = code;
        this.eventClass = eventClass;
    }

    public static BinlogEventType codeOf(Integer code) {
        if (code == null) {
            return null;
        }
        for (BinlogEventType binlogEventType : values()) {
            if (binlogEventType.code == code) {
                return binlogEventType;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public Class<? extends BinlogEvent> getEventClass() {
        return eventClass;
    }
}
