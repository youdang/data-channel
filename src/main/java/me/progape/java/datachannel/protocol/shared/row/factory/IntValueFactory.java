package me.progape.java.datachannel.protocol.shared.row.factory;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author progape
 * @date 2022-03-13
 */
public class IntValueFactory extends DefaultValueFactory<Integer> {
    @Override
    public Integer createFromLong(long value) {
        return (int) value;
    }

    @Override
    public Integer createFromBigInteger(BigInteger value) {
        if (value == null) {
            return createFromNull();
        }
        return value.intValue();
    }

    @Override
    public Integer createFromBigDecimal(BigDecimal value) {
        if (value == null) {
            return createFromNull();
        }
        return value.intValue();
    }

    @Override
    public Integer createFromFloat(float value) {
        return (int) value;
    }

    @Override
    public Integer createFromDouble(double value) {
        return (int) value;
    }

    @Override
    public Integer createFromYear(long value) {
        return createFromLong(value);
    }
}
