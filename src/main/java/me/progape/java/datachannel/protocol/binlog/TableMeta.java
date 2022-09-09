package me.progape.java.datachannel.protocol.binlog;

import java.util.List;

/**
 * @author progape
 * @date 2022-02-06
 */
public class TableMeta {
    private final long id;
    private final String schema;
    private final String name;
    private final List<ColumnMeta> columnMetas;

    public TableMeta(long id, String schema, String name, List<ColumnMeta> columnMetas) {
        this.id = id;
        this.schema = schema;
        this.name = name;
        this.columnMetas = columnMetas;
    }

    public long getId() {
        return id;
    }

    public String getSchema() {
        return schema;
    }

    public String getName() {
        return name;
    }

    public List<ColumnMeta> getColumnMetas() {
        return columnMetas;
    }
}
