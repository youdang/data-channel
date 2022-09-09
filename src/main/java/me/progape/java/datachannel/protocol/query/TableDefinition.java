package me.progape.java.datachannel.protocol.query;

import java.util.List;

/**
 * @author progape
 * @date 2022-02-07
 */
public final class TableDefinition {
    private long id;
    private String schemaName;
    private String tableName;
    private List<ColumnDefinition> columns;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnDefinition> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnDefinition> columns) {
        this.columns = columns;
    }
}
