package me.progape.java.datachannel.protocol.binlog.row;

import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.protocol.binlog.ColumnMeta;
import me.progape.java.datachannel.protocol.binlog.ColumnType;
import me.progape.java.datachannel.protocol.binlog.types.json.JsonParser;
import me.progape.java.datachannel.protocol.query.ColumnDefinition;
import me.progape.java.datachannel.protocol.shared.row.AbstractColumnValueDecoder;
import me.progape.java.datachannel.protocol.shared.row.factory.ValueFactory;
import me.progape.java.datachannel.protocol.shared.types.decimal.MySQLDecimal;
import me.progape.java.datachannel.protocol.shared.types.geometry.MySQLGeometry;
import me.progape.java.datachannel.protocol.shared.types.geometry.MySQLGeometryFactory;
import me.progape.java.datachannel.protocol.utils.ByteUtil;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author progape
 * @date 2022-03-28
 */
public class BinlogColumnValueDecoder extends AbstractColumnValueDecoder {
    private final List<ColumnMeta> columnMetas;

    public BinlogColumnValueDecoder(List<ColumnDefinition> columnDefinitions, List<ColumnMeta> columnMetas) {
        super(columnDefinitions);
        this.columnMetas = columnMetas;
    }

    @Override
    protected ColumnType getColumnType(int index) {
        return this.columnMetas.get(index).getType();
    }

    @Override
    protected <T> T decodeDecimal(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        throw new ProtocolException("unsupported");
    }

    @Override
    protected <T> T decodeTiny(int index, byte[] rawValue, boolean isUnsigned, ValueFactory<T> valueFactory) {
        checkLength(rawValue, 1);
        if (isUnsigned) {
            int value = ByteUtil.toIntegerLE(rawValue, 0, 1);
            return valueFactory.createFromLong(value);
        } else {
            byte value = (byte) ByteUtil.toIntegerLE(rawValue, 0, 1);
            return valueFactory.createFromLong(value);
        }
    }

    @Override
    protected <T> T decodeShort(int index, byte[] rawValue, boolean isUnsigned, ValueFactory<T> valueFactory) {
        checkLength(rawValue, 2);
        if (isUnsigned) {
            int value = ByteUtil.toIntegerLE(rawValue, 0, 2);
            return valueFactory.createFromLong(value);
        } else {
            short value = (short) ByteUtil.toIntegerLE(rawValue, 0, 2);
            return valueFactory.createFromLong(value);
        }
    }

    @Override
    protected <T> T decodeLong(int index, byte[] rawValue, boolean isUnsigned, ValueFactory<T> valueFactory) {
        checkLength(rawValue, 4);
        if (isUnsigned) {
            long value = ByteUtil.toLongLE(rawValue, 0, 4);
            return valueFactory.createFromLong(value);
        } else {
            int value = ByteUtil.toIntegerLE(rawValue, 0, 4);
            return valueFactory.createFromLong(value);
        }
    }

    @Override
    protected <T> T decodeFloat(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        checkLength(rawValue, 4);
        int value = ByteUtil.toIntegerLE(rawValue, 0, 4);
        return valueFactory.createFromFloat(Float.intBitsToFloat(value));
    }

    @Override
    protected <T> T decodeDouble(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        checkLength(rawValue, 8);
        long value = ByteUtil.toLongLE(rawValue, 0, 8);
        return valueFactory.createFromDouble(Double.longBitsToDouble(value));
    }

    @Override
    protected <T> T decodeTimestamp(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        checkLength(rawValue, 4);
        long value = ByteUtil.toLongLE(rawValue, 0, 4);
        LocalDateTime dateTime = Instant.ofEpochSecond(value).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return valueFactory.createFromDateTime(dateTime);
    }

    @Override
    protected <T> T decodeLongLong(int index, byte[] rawValue, boolean isUnsigned, ValueFactory<T> valueFactory) {
        checkLength(rawValue, 8);
        long value = ByteUtil.toLongLE(rawValue, 0, 8);
        if (isUnsigned) {
            return valueFactory.createFromBigInteger(new BigInteger(Long.toUnsignedString(value)));
        } else {
            return valueFactory.createFromLong(value);
        }
    }

    @Override
    protected <T> T decodeInt24(int index, byte[] rawValue, boolean isUnsigned, ValueFactory<T> valueFactory) {
        checkLength(rawValue, 3);
        if (isUnsigned) {
            int value = ByteUtil.toIntegerLE(rawValue, 0, 3);
            return valueFactory.createFromLong(value);
        } else {
            short lower = (short) ByteUtil.toIntegerLE(rawValue, 0, 2);
            byte upper = (byte) ByteUtil.toIntegerLE(rawValue, 2, 1);
            int value = (((int) upper) << 16) | lower;
            return valueFactory.createFromLong(value);
        }
    }

    @Override
    protected <T> T decodeDate(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        int tmp = ByteUtil.toIntegerLE(rawValue, 0, 3);
        int day = tmp & 31;
        int month = (tmp >>> 5) & 15;
        int year = tmp >>> 9;
        return valueFactory.createFromDate(LocalDate.of(year, month, day));
    }

    @Override
    protected <T> T decodeTime(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        checkLength(rawValue, 3);
        int value = ByteUtil.toIntegerLE(rawValue, 0, 3);
        int hour = value / 10000;
        int minute = (value % 10000) / 100;
        int second = value % 100;
        return valueFactory.createFromTime(LocalTime.of(hour, minute, second));
    }


    @Override
    protected <T> T decodeDateTime(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        checkLength(rawValue, 8);
        long value = ByteUtil.toLongLE(rawValue, 0, 8);
        long date = value / 1000000;
        long time = value % 1000000;

        int year = (int) (date / 10000);
        int month = (int) ((date % 10000) / 100);
        int day = (int) (date % 100);

        int hour = (int) (time / 10000);
        int minute = (int) ((time % 10000) / 100);
        int second = (int) (time % 100);
        return valueFactory.createFromDateTime(LocalDateTime.of(year, month, day, hour, minute, second));
    }

    @Override
    protected <T> T decodeYear(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        checkLength(rawValue, 1);
        int value = ByteUtil.toIntegerLE(rawValue, 0, 1);
        return valueFactory.createFromLong(value + 1900);
    }

    @Override
    protected <T> T decodeNewDate(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        int tmp = ByteUtil.toIntegerLE(rawValue, 0, 3);
        int day = tmp & 31;
        int month = (tmp >>> 5) & 15;
        int year = tmp >>> 9;
        return valueFactory.createFromDate(LocalDate.of(year, month, day));
    }

    @Override
    protected <T> T decodeVarchar(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        return valueFactory.createFromBytes(rawValue, this.columnDefinitions.get(index));
    }

    @Override
    protected <T> T decodeBit(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        return valueFactory.createFromBit(rawValue);
    }

    @Override
    protected <T> T decodeTimestamp2(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        int meta = this.columnMetas.get(index).getMeta();
        long seconds = ByteUtil.toLongBE(rawValue, 0, 4);
        long millis = 0;
        switch (meta) {
            case 1:
            case 2:
                millis = ByteUtil.toLongBE(rawValue, 4, 1) * 10000;
                break;
            case 3:
            case 4:
                millis = ByteUtil.toLongBE(rawValue, 4, 2) * 100;
                break;
            case 5:
            case 6:
                millis = ByteUtil.toLongBE(rawValue, 4, 3);
                break;
        }
        LocalDateTime localDateTime = Instant.ofEpochSecond(seconds, millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return valueFactory.createFromDateTime(localDateTime);
    }

    @Override
    protected <T> T decodeDateTime2(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        int meta = this.columnMetas.get(index).getMeta();
        long intPart = ByteUtil.toLongBE(rawValue, 0, 5) - 0x8000000000L;
        long fracPart = 0;
        switch (meta) {
            case 1:
            case 2:
                fracPart = ByteUtil.toLongBE(rawValue, 5, 1) * 10000L;
                break;
            case 3:
            case 4:
                fracPart = ByteUtil.toLongBE(rawValue, 5, 2) * 100L;
                break;
            case 5:
            case 6:
                fracPart = ByteUtil.toLongBE(rawValue, 5, 3);
                break;
        }

        long value = (intPart << 24) + fracPart;
        if (value < 0) {
            value = -value;
        }
        intPart = value >>> 24;
        fracPart = value & 0xFFF;

        int yyyyMMdd = (int) (intPart >>> 17);
        int yyyyMM = yyyyMMdd >>> 5;
        int hhmmss = (int) (intPart % (1 << 17));

        int day = yyyyMMdd % (1 << 5);
        int month = yyyyMM % 13;
        int year = yyyyMM / 13;
        int second = hhmmss % (1 << 6);
        int minute = (hhmmss >>> 6) % (1 << 6);
        int hour = hhmmss >>> 12;

        LocalDateTime localDateTime = LocalDateTime.of(
            year, month, day, hour, minute, second, (int) fracPart
        );
        return valueFactory.createFromDateTime(localDateTime);
    }

    @Override
    protected <T> T decodeTime2(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        int meta = this.columnMetas.get(index).getMeta();
        long value;
        switch (meta) {
            case 1:
            case 2: {
                long intPart = ByteUtil.toLongBE(rawValue, 0, 3) - 0x800000L;
                long fracPart = ByteUtil.toLongBE(rawValue, 3, 1);
                if (intPart < 0 && fracPart != 0) {
                    intPart++;
                    fracPart -= 0x100;
                }
                value = (intPart << 24) + fracPart * 10000L;
            }
            break;
            case 3:
            case 4: {
                long intPart = ByteUtil.toLongBE(rawValue, 0, 3) - 0x800000L;
                long fracPart = ByteUtil.toLongBE(rawValue, 3, 2);
                if (intPart < 0 && fracPart != 0) {
                    intPart++;
                    fracPart -= 0x10000;
                }
                value = (intPart << 24) + fracPart * 100L;
            }
            break;
            case 5:
            case 6: {
                value = ByteUtil.toLongBE(rawValue, 0, 6) - 0x800000000000L;
            }
            break;
            default: {
                long intPart = ByteUtil.toLongBE(rawValue, 0, 3) - 0x800000L;
                value = intPart << 24;
            }
            break;
        }

        if (value < 0) {
            value = -value;
        }
        long intPart = value >>> 24;
        long fracPart = value & 0xFFF;

        int hour = (int) (intPart >>> 12) % (1 << 10);
        int minute = (int) (intPart >>> 6) % (1 << 6);
        int second = (int) intPart % (1 << 6);
        return valueFactory.createFromTime(LocalTime.of(hour, minute, second, (int) fracPart));
    }

    @Override
    protected <T> T decodeTypedArray(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        throw new ProtocolException("unsupported");
    }

    @Override
    protected <T> T decodeJson(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        ColumnDefinition columnDefinition = this.columnDefinitions.get(index);
        Object json = JsonParser.parse(rawValue, columnDefinition);
        return valueFactory.createFromObject(json);
    }

    @Override
    protected <T> T decodeNewDecimal(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        int meta = this.columnMetas.get(index).getMeta();
        int precision = meta >>> 8;
        int scale = meta & 0xFF;
        MySQLDecimal mysqlDecimal = new MySQLDecimal(precision, scale);
        mysqlDecimal.parse(rawValue);
        return valueFactory.createFromBigDecimal(mysqlDecimal.toBigDecimal());
    }

    @Override
    protected <T> T decodeEnum(int index, byte[] rawValue, List<String> enumItems, ValueFactory<T> valueFactory) {
        int value = ByteUtil.toIntegerLE(rawValue, 0, rawValue.length);
        return valueFactory.createFromEnum(enumItems.get(value - 1));
    }

    @Override
    protected <T> T decodeSet(int index, byte[] rawValue, List<String> setItems, ValueFactory<T> valueFactory) {
        int maxLength = (setItems.size() + 7) / 8;
        if (maxLength < rawValue.length) {
            throw new IllegalArgumentException("rawValue too long");
        }

        List<Integer> indices = ByteUtil.bitmap2Indices(rawValue, setItems.size());
        Set<String> set = indices.stream()
            .map(setItems::get)
            .collect(Collectors.toSet());
        return valueFactory.createFromSet(set);
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
        return valueFactory.createFromByteArray(rawValue);
    }

    @Override
    protected <T> T decodeVarString(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        return valueFactory.createFromBytes(rawValue, this.columnDefinitions.get(index));
    }

    @Override
    protected <T> T decodeString(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        ColumnDefinition columnDefinition = this.columnDefinitions.get(index);

        int meta = this.columnMetas.get(index).getMeta();
        ColumnType realType = ColumnType.codeOf(meta >>> 8);
        if (realType == ColumnType.ENUM) {
            return decodeEnum(index, rawValue, columnDefinition.getEnumItems(), valueFactory);
        } else if (realType == ColumnType.SET) {
            return decodeSet(index, rawValue, columnDefinition.getSetItems(), valueFactory);
        } else {
            return valueFactory.createFromBytes(rawValue, columnDefinition);
        }
    }

    @Override
    protected <T> T decodeGeometry(int index, byte[] rawValue, ValueFactory<T> valueFactory) {
        MySQLGeometry geometry = MySQLGeometryFactory.create(rawValue);
        return valueFactory.createFromGeometry(geometry, this.columnDefinitions.get(index));
    }
}
