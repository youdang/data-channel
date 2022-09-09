package me.progape.java.datachannel.protocol.shared.row.factory;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author progape
 * @date 2022-03-13
 */
public class BigDecimalValueFactory extends DefaultValueFactory<BigDecimal> {
    @Override
    public BigDecimal createFromLong(long value) {
        return new BigDecimal(value);
    }

    @Override
    public BigDecimal createFromBigInteger(BigInteger value) {
        if (value == null) {
            return createFromNull();
        }
        return new BigDecimal(value);
    }

    @Override
    public BigDecimal createFromBigDecimal(BigDecimal value) {
        if (value == null) {
            return createFromNull();
        }
        return value;
    }

    @Override
    public BigDecimal createFromFloat(float value) {
        return new BigDecimal(value);
    }

    @Override
    public BigDecimal createFromDouble(double value) {
        return new BigDecimal(value);
    }

    @Override
    public BigDecimal createFromYear(long value) {
        return createFromLong(value);
    }
}
