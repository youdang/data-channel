package me.progape.java.datachannel;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.progape.java.datachannel.protocol.binlog.row.BinlogRowImpl;
import me.progape.java.datachannel.protocol.binlog.ColumnMeta;
import me.progape.java.datachannel.protocol.binlog.DeleteRowsEventV2;
import me.progape.java.datachannel.protocol.binlog.TableMeta;
import me.progape.java.datachannel.protocol.binlog.UpdateRowsEventV2;
import me.progape.java.datachannel.protocol.binlog.WriteRowsEventV2;
import me.progape.java.datachannel.protocol.query.ColumnDefinition;
import me.progape.java.datachannel.protocol.shared.row.Row;
import me.progape.java.datachannel.protocol.query.TableDefinition;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author progape
 * @date 2022-03-20
 */
public class Transaction {
    private final String host;
    private final int port;
    private final String binlogFilename;
    private final long binlogPosition;
    private final Map</*schemaName.tableName*/String, List<RowChange>> changes = Maps.newHashMap();

    public Transaction(String host, int port, String binlogFilename, long binlogPosition) {
        this.host = host;
        this.port = port;
        this.binlogFilename = binlogFilename;
        this.binlogPosition = binlogPosition;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getBinlogFilename() {
        return binlogFilename;
    }

    public long getBinlogPosition() {
        return binlogPosition;
    }

    public Map<String, List<RowChange>> getChanges() {
        return changes;
    }

    public Transaction add(TableDefinition tableDefinition, WriteRowsEventV2 writeRowsEventV2) {
        add(
            tableDefinition, writeRowsEventV2.getTableMeta(),
            null, null,
            writeRowsEventV2.getColumnIndices1(), writeRowsEventV2.getRows1()
        );
        return this;
    }

    public Transaction add(TableDefinition tableDefinition, DeleteRowsEventV2 deleteRowsEventV2) {
        add(
            tableDefinition, deleteRowsEventV2.getTableMeta(),
            deleteRowsEventV2.getColumnIndices1(), deleteRowsEventV2.getRows1(),
            null, null
        );
        return this;
    }

    public Transaction add(TableDefinition tableDefinition, UpdateRowsEventV2 updateRowsEventV2) {
        add(
            tableDefinition, updateRowsEventV2.getTableMeta(),
            updateRowsEventV2.getColumnIndices1(), updateRowsEventV2.getRows1(),
            updateRowsEventV2.getColumnIndices2(), updateRowsEventV2.getRows2()
        );
        return this;
    }

    private void add(TableDefinition tableDefinition, TableMeta tableMeta,
                     List<Integer> columnIndices1, List<List<byte[]>> rows1,
                     List<Integer> columnIndices2, List<List<byte[]>> rows2) {
        Preconditions.checkArgument(tableDefinition.getColumns().size() >= anyOf(columnIndices1, columnIndices2).size());
        Preconditions.checkArgument(tableMeta.getColumnMetas().size() >= anyOf(columnIndices1, columnIndices2).size());

        String name = tableDefinition.getSchemaName() + "." + tableDefinition.getTableName();

        RowChange.ChangeType changeType;
        if (columnIndices1 == null || rows1 == null) {
            changeType = RowChange.ChangeType.INSERT;
        } else if (columnIndices2 == null || rows2 == null) {
            changeType = RowChange.ChangeType.DELETE;
        } else {
            changeType = RowChange.ChangeType.UPDATE;
            Preconditions.checkArgument(columnIndices1.size() == columnIndices2.size());
            for (int i = 0; i < columnIndices1.size(); i++) {
                Preconditions.checkArgument(Objects.equals(columnIndices1.get(i), columnIndices2.get(i)));
            }
            Preconditions.checkArgument(rows1.size() == rows2.size());
            for (int i = 0; i < rows1.size(); i++) {
                Preconditions.checkArgument(rows1.get(i).size() == rows2.get(i).size());
            }
        }

        List<ColumnDefinition> columnDefinitions = anyOf(columnIndices1, columnIndices2).stream()
            .map(i -> tableDefinition.getColumns().get(i))
            .collect(Collectors.toList());
        List<ColumnMeta> columnMetas = anyOf(columnIndices1, columnIndices2).stream()
            .map(i -> tableMeta.getColumnMetas().get(i))
            .collect(Collectors.toList());

        int changedRowCount = anyOf(rows1, rows2).size();
        List<RowChange> rowChanges = Lists.newArrayListWithCapacity(changedRowCount);
        for (int i = 0; i < changedRowCount; i++) {
            List<byte[]> beforeColumns = rows1 == null ? null : rows1.get(i);
            List<byte[]> afterColumns = rows2 == null ? null : rows2.get(i);
            Preconditions.checkArgument(beforeColumns != null || afterColumns != null);
            int changedColumnCount = anyOf(beforeColumns, afterColumns).size();
            Preconditions.checkArgument(columnDefinitions.size() == changedColumnCount);
            Preconditions.checkArgument(columnMetas.size() == changedColumnCount);

            Row before = new BinlogRowImpl(columnDefinitions, columnMetas, beforeColumns);
            Row after = new BinlogRowImpl(columnDefinitions, columnMetas, afterColumns);
            rowChanges.add(new RowChange(changeType, columnDefinitions, before, after));
        }

        this.changes.merge(name, rowChanges, (a, b) -> {
            a.addAll(b);
            return a;
        });
    }

    private <T> T anyOf(T a, T b) {
        return a == null ? b : a;
    }

    public static class RowChange {
        private final ChangeType changeType;
        private final List<ColumnDefinition> columnDefinitions;
        private final Row before;
        private final Row after;

        public RowChange(ChangeType changeType, List<ColumnDefinition> columnDefinitions, Row before, Row after) {
            this.changeType = changeType;
            this.columnDefinitions = columnDefinitions;
            this.before = before;
            this.after = after;
        }

        public ChangeType getChangeType() {
            return changeType;
        }

        public List<ColumnDefinition> getColumnDefinitions() {
            return columnDefinitions;
        }

        public Row getBefore() {
            return before;
        }

        public Row getAfter() {
            return after;
        }

        public enum ChangeType {
            INSERT, DELETE, UPDATE
        }
    }
}
