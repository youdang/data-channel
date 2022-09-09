package me.progape.java.datachannel.protocol.shared.row.factory;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author progape
 * @date 2022-03-13
 */
public class DoubleValueFactory extends DefaultValueFactory<Double> {
    @Override
    public Double createFromLong(long value) {
        return (double) value;
    }

    @Override
    public Double createFromBigInteger(BigInteger value) {
        if (value == null) {
            return createFromNull();
        }
        return value.doubleValue();
    }

    @Override
    public Double createFromBigDecimal(BigDecimal value) {
        if (value == null) {
            return createFromNull();
        }
        return value.doubleValue();
    }

    @Override
    public Double createFromFloat(float value) {
        return (double) value;
    }

    @Override
    public Double createFromDouble(double value) {
        return value;
    }

    @Override
    public Double createFromYear(long value) {
        return createFromLong(value);
    }
}
