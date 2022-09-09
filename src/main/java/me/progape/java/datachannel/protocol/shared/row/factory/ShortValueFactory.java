package me.progape.java.datachannel.protocol.shared.row.factory;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author progape
 * @date 2022-03-13
 */
public class ShortValueFactory extends DefaultValueFactory<Short> {
    @Override
    public Short createFromLong(long value) {
        return (short) value;
    }

    @Override
    public Short createFromBigInteger(BigInteger value) {
        if (value == null) {
            return createFromNull();
        }
        return value.shortValue();
    }

    @Override
    public Short createFromBigDecimal(BigDecimal value) {
        if (value == null) {
            return createFromNull();
        }
        return value.shortValue();
    }

    @Override
    public Short createFromFloat(float value) {
        return (short) value;
    }

    @Override
    public Short createFromDouble(double value) {
        return (short) value;
    }

    @Override
    public Short createFromYear(long value) {
        return createFromLong(value);
    }
}
