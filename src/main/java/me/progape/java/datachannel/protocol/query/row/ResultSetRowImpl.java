package me.progape.java.datachannel.protocol.query.row;

import me.progape.java.datachannel.protocol.query.ColumnDefinition;
import me.progape.java.datachannel.protocol.shared.row.AbstractRow;

import java.util.List;

/**
 * @author progape
 * @date 2022-03-13
 */
public class ResultSetRowImpl extends AbstractRow {
    public ResultSetRowImpl(List<ColumnDefinition> columnDefinitions, List<byte[]> rawValues) {
        super(new ResultSetColumnValueDecoder(columnDefinitions), columnDefinitions, rawValues);
    }
}
