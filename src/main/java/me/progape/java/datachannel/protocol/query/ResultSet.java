package me.progape.java.datachannel.protocol.query;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import me.progape.java.datachannel.protocol.query.row.ResultSetRowImpl;
import me.progape.java.datachannel.protocol.shared.Response;
import me.progape.java.datachannel.protocol.shared.row.Row;

import javax.annotation.CheckForNull;
import java.util.Iterator;
import java.util.List;

/**
 * @author progape
 * @date 2022-02-07
 */
public class ResultSet extends Response implements Iterable<Row> {
    private final List<ColumnDefinition> columnDefinitions = Lists.newArrayList();
    private final List<Row> rows = Lists.newArrayList();
    private ResultSet next = null;

    public List<ColumnDefinition> getColumnDefinitions() {
        return columnDefinitions;
    }

    @Override
    public Iterator<Row> iterator() {
        return new AbstractIterator<Row>() {
            private ResultSet rs = ResultSet.this;
            private int idx = 0;

            @CheckForNull
            @Override
            protected Row computeNext() {
                if (rs == null || (rs.next == null && idx >= rs.rows.size())) {
                    return endOfData();
                }
                Row nextRow = rs.rows.get(idx++);
                if (idx >= rs.rows.size()) {
                    rs = rs.next;
                    idx = 0;
                }
                return nextRow;
            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    void addColumnDefinition(ColumnDefinition columnDefinition) {
        this.columnDefinitions.add(columnDefinition);
    }

    void addRow(List<byte[]> row) {
        this.rows.add(new ResultSetRowImpl(columnDefinitions, row));
    }

    void setNext(ResultSet next) {
        this.next = next;
    }
}
