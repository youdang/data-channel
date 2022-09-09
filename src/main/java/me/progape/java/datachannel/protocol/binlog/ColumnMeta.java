package me.progape.java.datachannel.protocol.binlog;

/**
 * @author progape
 * @date 2022-02-06
 */
public class ColumnMeta {
    private final ColumnType type;
    private final int meta;
    private final int metaLength;
    private final boolean isNotNull;

    public ColumnMeta(ColumnType type, int meta, int metaLength, boolean isNotNull) {
        this.type = type;
        this.meta = meta;
        this.metaLength = metaLength;
        this.isNotNull = isNotNull;
    }

    public ColumnType getType() {
        return type;
    }

    public int getMeta() {
        return meta;
    }

    public int getMetaLength() {
        return metaLength;
    }

    public boolean isNotNull() {
        return isNotNull;
    }
}
