package me.progape.java.datachannel.protocol.shared.row;

import me.progape.java.datachannel.protocol.shared.row.factory.ValueFactory;

/**
 * @author progape
 * @date 2022-03-13
 */
public interface ColumnValueDecoder {
    <T> T decode(int index, byte[] rawValue, ValueFactory<T> valueFactory);
}
