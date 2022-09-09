package me.progape.java.datachannel.protocol.shared.row.factory;

import me.progape.java.datachannel.protocol.query.ColumnDefinition;
import me.progape.java.datachannel.protocol.utils.ByteUtil;

import java.math.BigInteger;

/**
 * @author progape
 * @date 2022-03-13
 */
public class BooleanValueFactory extends DefaultValueFactory<Boolean> {
    @Override
    public Boolean createFromLong(long value) {
        return value != 0L;
    }

    @Override
    public Boolean createFromBigInteger(BigInteger value) {
        if (value == null) {
            return createFromNull();
        }
        return value.compareTo(BigInteger.ZERO) != 0;
    }

    @Override
    public Boolean createFromYear(long value) {
        return createFromLong(value);
    }

    @Override
    public Boolean createFromBytes(byte[] value, ColumnDefinition columnDefinition) {
        return createFromByteArray(value);
    }

    @Override
    public Boolean createFromByteArray(byte[] value) {
        if (value == null) {
            return createFromNull();
        }
        return ByteUtil.ones(value) != 0;
    }

    @Override
    public Boolean createFromBit(byte[] value) {
        if (value == null) {
            return createFromNull();
        }
        return ByteUtil.ones(value) != 0;
    }

    @Override
    public Boolean createFromBoolean(boolean value) {
        return value;
    }
}
