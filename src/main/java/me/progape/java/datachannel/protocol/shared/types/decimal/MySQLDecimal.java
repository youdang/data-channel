package me.progape.java.datachannel.protocol.shared.types.decimal;

import me.progape.java.datachannel.protocol.utils.ByteUtil;

import java.math.BigDecimal;

/**
 * @author progape
 * @date 2022-04-09
 */
public class MySQLDecimal {
    private static final int SIZE_OF_INT = 4;
    private static final int DIG_PER_DEC1 = 9;
    private static final int[] DIG_2_BYTES = {0, 1, 1, 2, 2, 3, 3, 4, 4, 4};
    private static final int DECIMAL_BUFF_LENGTH = 9;
    private static final int[] POWERS_10 = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000};

    ////////////////////////////////
    // 原始数据
    ////////////////////////////////
    /**
     * 小数点前整数部分长度
     */
    private int intg;
    /**
     * 小数点后小数部分长度
     */
    private int frac;
    private final int intg0;
    private final int frac0;
    private final int intg0x;
    private final int frac0x;
    private final int binSize;

    ////////////////////////////////
    // 解析后数据
    ////////////////////////////////
    private boolean parsed = false;
    /**
     * 符号位，true-负数，false-正数
     */
    private boolean sign = false;
    private final int[] buf = new int[DECIMAL_BUFF_LENGTH];

    public MySQLDecimal(int precision, int scale) {
        this.intg = precision - scale;
        this.frac = scale;
        this.intg0 = this.intg / DIG_PER_DEC1;
        this.frac0 = this.frac / DIG_PER_DEC1;
        this.intg0x = this.intg - this.intg0 * DIG_PER_DEC1;
        this.frac0x = this.frac - this.frac0 * DIG_PER_DEC1;
        this.binSize = this.intg0 * SIZE_OF_INT + DIG_2_BYTES[this.intg0x] + this.frac0 * SIZE_OF_INT + DIG_2_BYTES[this.frac0x];
    }

    public int getBinSize() {
        return this.binSize;
    }

    public void parse(byte[] data) {
        if (data.length < binSize) {
            throw new IllegalArgumentException("insufficient bytes");
        }
        int mask = (data[0] & 0x80) != 0 ? 0 : -1;
        byte[] copy = new byte[binSize];
        System.arraycopy(data, 0, copy, 0, binSize);

        // parse sign
        this.sign = mask != 0;
        copy[0] ^= 0x80;

        int dataOffset = 0;
        int bufIndex = 0;

        // parse integer part
        if (this.intg0x != 0) {
            int i = DIG_2_BYTES[this.intg0x];
            this.buf[bufIndex++] = ByteUtil.toIntegerBE(copy, dataOffset, i) ^ mask;
            dataOffset += i;
        }
        for (int i = 0; i < this.intg0; i++) {
            this.buf[bufIndex++] = ByteUtil.toIntegerBE(copy, dataOffset, 4) ^ mask;
            dataOffset += SIZE_OF_INT;
        }

        // parse fraction part
        for (int i = 0; i < this.frac0; i++) {
            this.buf[bufIndex++] = ByteUtil.toIntegerBE(copy, dataOffset, 4) ^ mask;
            dataOffset += SIZE_OF_INT;
        }
        if (this.frac0x != 0) {
            int i = DIG_2_BYTES[this.frac0x];
            this.buf[bufIndex++] = (ByteUtil.toIntegerBE(copy, dataOffset, i) ^ mask) * POWERS_10[DIG_PER_DEC1 - this.frac0x];
        }

        // no digits
        if (this.intg == 0 && this.frac == 0) {
            this.buf[0] = 0;
            this.intg = 1;
            this.frac = 0;
            this.sign = false;
        }

        this.parsed = true;
    }

    public BigDecimal toBigDecimal() {
        if (!this.parsed) {
            throw new IllegalStateException("not parsed");
        }

        // if no integer part, append leading zero before decimal point
        int intg = this.intg;
        int frac = this.frac;
        int intgLength = Math.max(1, intg);
        int fracLength = frac;

        // sign + intgLength + (frac > 0 ? decimal_point + frac : frac)
        int length = (this.sign ? 1 : 0) + intgLength + (frac != 0 ? 1 : 0) + fracLength;
        StringBuilder sb = new StringBuilder(length);

        // convert sign
        if (this.sign) {
            sb.append('-');
        }

        int bufIndex = 0;

        // convert integer part
        if (intg == 0) {
            sb.append('0');
        } else {
            int digitsInPartialWord = intg % DIG_PER_DEC1;
            if (digitsInPartialWord != 0) {
                int x = this.buf[bufIndex++];
                if (x >= POWERS_10[digitsInPartialWord]) {
                    x %= POWERS_10[digitsInPartialWord];
                }

                writeDigits(x, digitsInPartialWord, sb);
                intg -= digitsInPartialWord;
            }
            while (intg > 0) {
                writeDigits(this.buf[bufIndex++], DIG_PER_DEC1, sb);
                intg -= DIG_PER_DEC1;
            }
        }

        // convert fraction part
        if (frac != 0) {
            sb.append('.');
            while (frac >= DIG_PER_DEC1) {
                writeDigits(this.buf[bufIndex++], DIG_PER_DEC1, sb);
                frac -= DIG_PER_DEC1;
            }
            if (frac > 0) {
                int x = this.buf[bufIndex++] / POWERS_10[DIG_PER_DEC1 - frac];
                writeDigits(x, frac, sb);
            }
        }
        return new BigDecimal(sb.toString());
    }

    private void writeDigits(int number, int digits, StringBuilder sb) {
        char[] dest = new char[digits];
        int pos = digits;

        while (pos > 0) {
            dest[--pos] = (char) ('0' + (number % 10));
            number /= 10;
        }

        sb.append(dest);
    }
}
