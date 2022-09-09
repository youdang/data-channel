package me.progape.java.datachannel.protocol.query.row;

import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.protocol.binlog.ColumnType;
import me.progape.java.datachannel.protocol.query.ColumnDefinition;
import me.progape.java.datachannel.protocol.shared.row.AbstractColumnValueDecoder;
import me.progape.java.datachannel.protocol.shared.row.factory.ValueFactory;
import me.progape.java.datachannel.protocol.utils.ByteUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * // TODO
 * @author progape
 * @date 2022-03-28
 */
public class ResultSetColumnValueDecoder extends AbstractColumnValueDecoder {
    public ResultSetColumnValueDecoder(List<ColumnDefinition> columnDefinitions) {
        super(columnDefinitions);
    }

    @Override
    protected ColumnType getColumnType(int index) {
        return this.columnDefinitions.get(index).getType();
    }

    @Override
    protected <T> T decodeDecimal(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        char[] str = new char[rawValue.length];
        for (int i = 0; i < rawValue.length; i++) {
            str[i] = (char) rawValue[i];
        }
        return valueFactory.createFromBigDecimal(new BigDecimal(str));
    }

    @Override
    protected <T> T decodeTiny(int index, byte[] rawValue, boolean isUnsigned, ValueFactory<T> valueFactory) {
        checkLength(rawValue, 1);
        int value = ByteUtil.toIntegerLE(rawValue, 0, 1);
        return valueFactory.createFromLong(value);
    }

    @Override
    protected <T> T decodeShort(int index, byte[] rawValue, boolean isUnsigned, ValueFactory<T> valueFactory) {
        checkLength(rawValue, 2);
        int value = ByteUtil.toIntegerLE(rawValue, 0, 2);
        return valueFactory.createFromLong(value);
    }


    @Override
    protected <T> T decodeLong(int index, byte[] rawValue, boolean isUnsigned, ValueFactory<T> valueFactory) {
        checkLength(rawValue, 4);
        long value = ByteUtil.toLongLE(rawValue, 0, 4);
        return valueFactory.createFromLong(value);
    }

    @Override
    protected <T> T decodeFloat(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        checkLength(rawValue, 4);
        int value = ByteUtil.toIntegerLE(rawValue, 0, 4);
        return valueFactory.createFromDouble(Float.intBitsToFloat(value));
    }

    @Override
    protected <T> T decodeDouble(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        checkLength(rawValue, 8);
        long value = ByteUtil.toLongLE(rawValue, 0, 8);
        return valueFactory.createFromDouble(Double.longBitsToDouble(value));
    }

    @Override
    protected <T> T decodeTimestamp(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        if (rawValue == null) {
            throw new IllegalArgumentException();
        }
        if (rawValue.length == 0) {
            return valueFactory.createFromDateTime(LocalDateTime.of(0, 1, 1, 0, 0, 0));
        }
        checkLength(rawValue, 4, 7, 11);

        int year = ByteUtil.toIntegerLE(rawValue, 0, 2);
        int month = rawValue[2];
        int day = rawValue[3];
        int hour = 0;
        int minute = 0;
        int second = 0;
        int nanos = 0;
        if (rawValue.length > 4) {
            hour = rawValue[4];
            minute = rawValue[5];
            second = rawValue[6];
        }
        if (rawValue.length > 7) {
            nanos = 1000 * ByteUtil.toIntegerLE(rawValue, 7, 4);
        }
        return valueFactory.createFromDateTime(LocalDateTime.of(
            year, month, day, hour, minute, second, nanos
        ));
    }

    @Override
    protected <T> T decodeLongLong(int index, byte[] rawValue, boolean isUnsigned, ValueFactory<T> valueFactory) {
        checkLength(rawValue, 8);
        if (isUnsigned && (rawValue[7] & 0x80) != 0) {
            byte[] value = new byte[]{
                0, // sign
                rawValue[7], rawValue[6], rawValue[5], rawValue[4],
                rawValue[3], rawValue[2], rawValue[1], rawValue[0]
            };
            BigInteger tmp = new BigInteger(value);
            return valueFactory.createFromBigInteger(tmp);
        } else {
            long value = ByteUtil.toLongLE(rawValue, 0, 8);
            return valueFactory.createFromLong(value);
        }
    }

    @Override
    protected <T> T decodeInt24(int index, byte[] rawValue, boolean isUnsigned, ValueFactory<T> valueFactory) {
        checkLength(rawValue, 3);
        int value = ByteUtil.toIntegerLE(rawValue, 0, 3);
        return valueFactory.createFromLong(value);
    }

    @Override
    protected <T> T decodeDate(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        int year = ByteUtil.toIntegerLE(rawValue, 0, 2);
        int month = rawValue[2];
        int day = rawValue[3];
        return valueFactory.createFromDate(LocalDate.of(year, month, day));
    }

    @Override
    protected <T> T decodeTime(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        if (rawValue == null) {
            throw new IllegalArgumentException();
        }
        if (rawValue.length == 0) {
            return valueFactory.createFromTime(LocalTime.of(0, 0, 0, 0));
        }
        checkLength(rawValue, 8, 12);

        boolean negative = rawValue[0] == 1;
        int day = ByteUtil.toIntegerLE(rawValue, 1, 4);
        int hour = rawValue[5];
        int minute = rawValue[6];
        int second = rawValue[7];
        int nanos = 0;
        if (rawValue.length > 8) {
            nanos = 1000 * ByteUtil.toIntegerLE(rawValue, 8, 4);
        }
        if (negative) {
            day *= -1;
        }
        return valueFactory.createFromTime(LocalTime.of(day * 24 + hour, minute, second, nanos));
    }

    @Override
    protected <T> T decodeDateTime(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        if (rawValue == null) {
            throw new IllegalArgumentException();
        }
        if (rawValue.length == 0) {
            return valueFactory.createFromDateTime(LocalDateTime.of(0, 1, 1, 0, 0, 0));
        }
        checkLength(rawValue, 4, 7, 11);

        int year = ByteUtil.toIntegerLE(rawValue, 0, 2);
        int month = rawValue[2];
        int day = rawValue[3];
        int hour = 0;
        int minute = 0;
        int second = 0;
        int nanos = 0;
        if (rawValue.length > 4) {
            hour = rawValue[4];
            minute = rawValue[5];
            second = rawValue[6];
        }
        if (rawValue.length > 7) {
            nanos = 1000 * ByteUtil.toIntegerLE(rawValue, 7, 4);
        }
        return valueFactory.createFromDateTime(LocalDateTime.of(
            year, month, day, hour, minute, second, nanos
        ));
    }

    @Override
    protected <T> T decodeYear(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        checkLength(rawValue, 2);
        int year = ByteUtil.toIntegerLE(rawValue, 0, 2);
        return valueFactory.createFromYear(year);
    }

    @Override
    protected <T> T decodeNewDate(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        throw new ProtocolException("unsupported");
    }

    @Override
    protected <T> T decodeVarchar(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        throw new ProtocolException("unsupported");
    }


    @Override
    protected <T> T decodeBit(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        return valueFactory.createFromBit(rawValue);
    }

    @Override
    protected <T> T decodeTimestamp2(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        throw new ProtocolException("unsupported");
    }

    @Override
    protected <T> T decodeDateTime2(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        throw new ProtocolException("unsupported");
    }

    @Override
    protected <T> T decodeTime2(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        throw new ProtocolException("unsupported");
    }

    @Override
    protected <T> T decodeTypedArray(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        throw new ProtocolException("unsupported");
    }

    @Override
    protected <T> T decodeJson(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        throw new ProtocolException("unsupported");
    }

    @Override
    protected <T> T decodeNewDecimal(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        throw new ProtocolException("unsupported");
    }

    @Override
    protected <T> T decodeEnum(int index, byte[] rawValue, List<String> enumItems, ValueFactory<T> valueFactory) {
        throw new ProtocolException("unsupported");
    }

    @Override
    protected <T> T decodeSet(int index, byte[] rawValue, List<String> setItems, ValueFactory<T> valueFactory) {
        throw new ProtocolException("unsupported");
    }

    @Override
    protected <T> T decodeTinyBlob(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        throw new ProtocolException("unsupported");
    }

    @Override
    protected <T> T decodeMediumBlob(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        throw new ProtocolException("unsupported");
    }

    @Override
    protected <T> T decodeLongBlob(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        throw new ProtocolException("unsupported");
    }

    @Override
    protected <T> T decodeBlob(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        return valueFactory.createFromBytes(rawValue, this.columnDefinitions.get(index));
    }

    @Override
    protected <T> T decodeVarString(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        return valueFactory.createFromBytes(rawValue, this.columnDefinitions.get(index));
    }

    @Override
    protected <T> T decodeString(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        return valueFactory.createFromBytes(rawValue, this.columnDefinitions.get(index));
    }

    @Override
    protected <T> T decodeGeometry(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        throw new ProtocolException("unsupported");
    }
}
