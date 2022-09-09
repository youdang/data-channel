package me.progape.java.datachannel.protocol.binlog.row;

import me.progape.java.datachannel.protocol.binlog.ColumnMeta;
import me.progape.java.datachannel.protocol.query.ColumnDefinition;
import me.progape.java.datachannel.protocol.shared.row.AbstractRow;

import java.util.List;

/**
 * @author progape
 * @date 2022-03-28
 */
public class BinlogRowImpl extends AbstractRow {
    public BinlogRowImpl(List<ColumnDefinition> columnDefinitions, List<ColumnMeta> columnMetas, List<byte[]> rawValues) {
        super(new BinlogColumnValueDecoder(columnDefinitions, columnMetas), columnDefinitions, rawValues);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("BinlogRowImpl{");
        for (int i = 0; i < getColumnCount(); i++) {
            sb.append(getColumnDefinition(i).getName()).append(":").append(getString(i)).append(",");
        }
        if (getColumnCount() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }
}
