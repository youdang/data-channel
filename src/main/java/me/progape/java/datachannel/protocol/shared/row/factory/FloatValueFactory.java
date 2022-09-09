package me.progape.java.datachannel.protocol.shared.row.factory;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author progape
 * @date 2022-03-13
 */
public class FloatValueFactory extends DefaultValueFactory<Float> {
    @Override
    public Float createFromLong(long value) {
        return (float) value;
    }

    @Override
    public Float createFromBigInteger(BigInteger value) {
        if (value == null) {
            return createFromNull();
        }
        return value.floatValue();
    }

    @Override
    public Float createFromBigDecimal(BigDecimal value) {
        if (value == null) {
            return createFromNull();
        }
        return value.floatValue();
    }

    @Override
    public Float createFromFloat(float value) {
        return value;
    }

    @Override
    public Float createFromDouble(double value) {
        return (float) value;
    }

    @Override
    public Float createFromYear(long value) {
        return createFromLong(value);
    }
}
