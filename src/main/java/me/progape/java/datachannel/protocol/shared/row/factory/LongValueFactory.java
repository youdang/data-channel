package me.progape.java.datachannel.protocol.shared.row.factory;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author progape
 * @date 2022-03-13
 */
public class LongValueFactory extends DefaultValueFactory<Long> {
    @Override
    public Long createFromLong(long value) {
        return value;
    }

    @Override
    public Long createFromBigInteger(BigInteger value) {
        if (value == null) {
            return createFromNull();
        }
        return value.longValue();
    }

    @Override
    public Long createFromBigDecimal(BigDecimal value) {
        if (value == null) {
            return createFromNull();
        }
        return value.longValue();
    }

    @Override
    public Long createFromFloat(float value) {
        return (long) value;
    }

    @Override
    public Long createFromDouble(double value) {
        return (long) value;
    }

    @Override
    public Long createFromYear(long value) {
        return value;
    }
}
