package me.progape.java.datachannel.protocol.shared.row.factory;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author progape
 * @date 2022-03-13
 */
public class ByteValueFactory extends DefaultValueFactory<Byte> {
    @Override
    public Byte createFromLong(long value) {
        return (byte) value;
    }

    @Override
    public Byte createFromBigInteger(BigInteger value) {
        if (value == null) {
            return createFromNull();
        }
        return value.byteValue();
    }

    @Override
    public Byte createFromBigDecimal(BigDecimal value) {
        if (value == null) {
            return createFromNull();
        }
        return value.byteValue();
    }

    @Override
    public Byte createFromFloat(float value) {
        return (byte) value;
    }

    @Override
    public Byte createFromDouble(double value) {
        return (byte) value;
    }

    @Override
    public Byte createFromYear(long value) {
        return createFromLong(value);
    }
}
