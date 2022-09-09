package me.progape.java.datachannel.protocol.binlog.types.json;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.protocol.binlog.ColumnType;
import me.progape.java.datachannel.protocol.query.ColumnDefinition;
import me.progape.java.datachannel.protocol.shared.types.BytesWrapper;
import me.progape.java.datachannel.protocol.shared.types.Pointer;
import me.progape.java.datachannel.protocol.utils.ByteUtil;
import me.progape.java.datachannel.protocol.utils.CharsetUtil;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author progape
 * @date 2022-05-08
 */
public class JsonParser {
    private static final int SMALL_OFFSET_SIZE = 2;
    private static final int LARGE_OFFSET_SIZE = 4;
    private static final int SMALL_KEY_ENTRY_SIZE = 2 + SMALL_OFFSET_SIZE;
    private static final int LARGE_KEY_ENTRY_SIZE = 2 + LARGE_OFFSET_SIZE;
    private static final int SMALL_VALUE_ENTRY_SIZE = 1 + SMALL_OFFSET_SIZE;
    private static final int LARGE_VALUE_ENTRY_SIZE = 1 + LARGE_OFFSET_SIZE;

    private static final int BINARY_TYPE_SMALL_OBJECT = 0x0;
    private static final int BINARY_TYPE_LARGE_OBJECT = 0x1;
    private static final int BINARY_TYPE_SMALL_ARRAY = 0x2;
    private static final int BINARY_TYPE_LARGE_ARRAY = 0x3;
    private static final int BINARY_TYPE_LITERAL = 0x4;
    private static final int BINARY_TYPE_INT16 = 0x5;
    private static final int BINARY_TYPE_UINT16 = 0x6;
    private static final int BINARY_TYPE_INT32 = 0x7;
    private static final int BINARY_TYPE_UINT32 = 0x8;
    private static final int BINARY_TYPE_INT64 = 0x9;
    private static final int BINARY_TYPE_UINT64 = 0xA;
    private static final int BINARY_TYPE_DOUBLE = 0xB;
    private static final int BINARY_TYPE_STRING = 0xC;
    private static final int BINARY_TYPE_OPAQUE = 0xF;

    private static final int BINARY_LITERAL_NULL = 0x0;
    private static final int BINARY_LITERAL_TRUE = 0x1;
    private static final int BINARY_LITERAL_FALSE = 0x2;

    public static Object parse(byte[] rawValue, ColumnDefinition columnDefinition) {
        // 0000     00 02 00 44 00 12 00 03   00 15 00 04 00 0C 19 00
        // 0010     02 1F 00 6B 65 79 6C 69   73 74 05 76 61 6C 75 65
        // 0020     01 00 25 00 00 07 00 02   00 1E 00 12 00 01 00 13
        // 0030     00 01 00 0C 14 00 0C 18   00 6B 76 03 6B 65 79 05
        // 0040     76 61 6C 75 65

        JsonValue value = parseBinary(rawValue);
        return new JsonWrapper(value).toJavaObject(columnDefinition);
    }

    private static JsonValue parseBinary(byte[] rawValue) {
        BytesWrapper data = new BytesWrapper(rawValue, 0);
        int type = data.at(0);
        return parseValue(data.forward(1), rawValue.length - 1, type);
    }

    private static JsonValue parseValue(BytesWrapper data, long length, int type) {
        switch (type) {
            case BINARY_TYPE_SMALL_OBJECT:
                return parseArrayOrObject(data, length, EnumType.OBJECT, false);
            case BINARY_TYPE_LARGE_OBJECT:
                return parseArrayOrObject(data, length, EnumType.OBJECT, true);
            case BINARY_TYPE_SMALL_ARRAY:
                return parseArrayOrObject(data, length, EnumType.ARRAY, false);
            case BINARY_TYPE_LARGE_ARRAY:
                return parseArrayOrObject(data, length, EnumType.ARRAY, true);
            default:
                return parseScalar(data, length, type);
        }
    }

    private static JsonValue parseArrayOrObject(BytesWrapper data, long length, EnumType type, boolean large) {
        int offsetSize = offsetSize(large);
        shouldNot(length < 2L * offsetSize);
        long elementCount = readOffsetOrSize(data, large);
        long bytes = readOffsetOrSize(data.forward(offsetSize), large);
        shouldNot(bytes > length);
        long headerSize = 2L * offsetSize;
        if (type == EnumType.OBJECT) {
            headerSize += elementCount * keyEntrySize(large);
        }
        headerSize += elementCount * valueEntrySize(large);
        shouldNot(headerSize > bytes);
        return new JsonValue(data, bytes, type, elementCount, large);
    }

    private static JsonValue parseScalar(BytesWrapper data, long length, int type) {
        switch (type) {
            case BINARY_TYPE_LITERAL: {
                shouldNot(length < 1);
                switch (data.at(0)) {
                    case BINARY_LITERAL_NULL:
                        return new JsonValue(EnumType.LITERAL_NULL);
                    case BINARY_LITERAL_TRUE:
                        return new JsonValue(EnumType.LITERAL_TRUE);
                    case BINARY_LITERAL_FALSE:
                        return new JsonValue(EnumType.LITERAL_FALSE);
                    default:
                        return fail();
                }
            }
            case BINARY_TYPE_INT16: {
                shouldNot(length < 2);
                return new JsonValue(EnumType.INT, ByteUtil.toIntegerLE(data.getBytes(), data.getOffset(), 2));
            }
            case BINARY_TYPE_INT32: {
                shouldNot(length < 4);
                return new JsonValue(EnumType.INT, ByteUtil.toIntegerLE(data.getBytes(), data.getOffset(), 4));
            }
            case BINARY_TYPE_INT64: {
                shouldNot(length < 8);
                return new JsonValue(EnumType.INT, ByteUtil.toLongLE(data.getBytes(), data.getOffset(), 8));
            }
            case BINARY_TYPE_UINT16: {
                shouldNot(length < 2);
                return new JsonValue(EnumType.UINT, ByteUtil.toIntegerLE(data.getBytes(), data.getOffset(), 2));
            }
            case BINARY_TYPE_UINT32: {
                shouldNot(length < 4);
                return new JsonValue(EnumType.UINT, ByteUtil.toLongLE(data.getBytes(), data.getOffset(), 4));
            }
            case BINARY_TYPE_UINT64: {
                shouldNot(length < 8);
                // FIXME
                return new JsonValue(EnumType.UINT, ByteUtil.toLongLE(data.getBytes(), data.getOffset(), 8));
            }
            case BINARY_TYPE_DOUBLE: {
                shouldNot(length < 8);
                return new JsonValue(Double.longBitsToDouble(ByteUtil.toLongLE(data.getBytes(), data.getOffset(), 8)));
            }
            case BINARY_TYPE_STRING: {
                Pointer<Long> stringLength = new Pointer<>(0L);
                Pointer<Integer> n = new Pointer<>(0);
                readVariableLength(data, length, stringLength, n);
                shouldNot(length < n.get() + stringLength.get());
                return new JsonValue(data.forward(n.get()), stringLength.get());
            }
            case BINARY_TYPE_OPAQUE: {
                shouldNot(length < 1);
                ColumnType fieldType = ColumnType.codeOf((int) data.at(0));
                Pointer<Long> valueLength = new Pointer<>(0L);
                Pointer<Integer> n = new Pointer<>(0);
                shouldNot(length < 1 + n.get() + valueLength.get());
                readVariableLength(data, length, valueLength, n);
                return new JsonValue(data.forward(1 + n.get()), valueLength.get(), fieldType);
            }
            default:
                return fail();
        }
    }

    private static boolean inlinedType(int type, boolean large) {
        switch (type) {
            case BINARY_TYPE_LITERAL:
            case BINARY_TYPE_INT16:
            case BINARY_TYPE_UINT16:
                return true;
            case BINARY_TYPE_INT32:
            case BINARY_TYPE_UINT32:
                return large;
            default:
                return false;
        }
    }

    private static int offsetSize(boolean large) {
        return large ? LARGE_OFFSET_SIZE : SMALL_OFFSET_SIZE;
    }

    private static int keyEntrySize(boolean large) {
        return large ? LARGE_KEY_ENTRY_SIZE : SMALL_KEY_ENTRY_SIZE;
    }

    private static int valueEntrySize(boolean large) {
        return large ? LARGE_VALUE_ENTRY_SIZE : SMALL_VALUE_ENTRY_SIZE;
    }

    private static long readOffsetOrSize(BytesWrapper data, boolean large) {
        return ByteUtil.toLongLE(data.getBytes(), data.getOffset(), large ? 4 : 2);
    }

    private static void readVariableLength(BytesWrapper data, long length, Pointer<Long> stringLength, Pointer<Integer> num) {
        long maxBytes = Math.min(length, 5L);
        long len = 0L;
        for (int i = 0; i < maxBytes; i++) {
            len |= ((long) (data.at(i) & 0x7F) << (7 * i));
            if ((data.at(i) & 0x80) == 0) {
                shouldNot(len > 0xFFFFFFFFL);
                num.set(i + 1);
                stringLength.set(len);
                return;
            }
        }
        fail();
    }

    private static Object wrapperToJavaObject(JsonWrapper wrapper, ColumnDefinition columnDefinition) {
        JsonWrapper.Type type = wrapper.type();
        if (type == JsonWrapper.Type.OPAQUE && wrapper.fieldType() == ColumnType.VAR_STRING) {
            type = JsonWrapper.Type.STRING;
        }
        switch (type) {
            case TIME:
            case DATE:
            case DATETIME:
            case TIMESTAMP: {
                return wrapper.dateTimeToString();
            }
            case BOOLEAN: {
                return wrapper.getBoolean();
            }
            case DECIMAL:
            case DOUBLE: {
                return wrapper.getDouble();
            }
            case INT:
            case UINT: {
                return wrapper.getInt();
            }
            case STRING: {
                return CharsetUtil.decode(
                    wrapper.getData().getBytes(), wrapper.getData().getOffset(), (int) wrapper.getDataLength(),
                    columnDefinition.getCollation(), null
                );
            }
            case NULL: {
                return "null";
            }
            case ARRAY: {
                int arrayLength = wrapper.length();
                List<Object> result = Lists.newArrayListWithCapacity(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    result.add(wrapperToJavaObject(wrapper.at(i), columnDefinition));
                }
                return result;
            }
            case OBJECT: {
                Map<String, Object> result = Maps.newHashMap();
                JsonWrapperObjectIterator it = new JsonWrapperObjectIterator(columnDefinition, wrapper.getValue());
                while (it.hasNext()) {
                    Map.Entry<String, JsonWrapper> entry = it.next();
                    result.put(entry.getKey(), wrapperToJavaObject(entry.getValue(), columnDefinition));
                }
                return result;
            }
            case OPAQUE: {
                // FIXME
                throw new ProtocolException("unsupported");
            }
            default:
                return fail();
        }
    }

    private static void shouldNot(boolean condition) {
        if (condition) {
            throw new ProtocolException("invalid JSON value");
        }
    }

    private static <T> T fail() {
        throw new ProtocolException("invalid JSON value");
    }

    private enum EnumType {
        OBJECT, ARRAY, STRING, INT, UINT, DOUBLE, LITERAL_NULL, LITERAL_TRUE, LITERAL_FALSE, OPAQUE, ERROR
    }

    private static class JsonValue {
        private final BytesWrapper data;
        private final long intValue;
        private final double doubleValue;
        private final long elementCount;
        private final long length;
        private final ColumnType fieldType;
        private final EnumType type;
        private final boolean large;

        public JsonValue(BytesWrapper data, long intValue, double doubleValue, long elementCount, long length, ColumnType fieldType, EnumType type, boolean large) {
            this.data = data;
            this.intValue = intValue;
            this.doubleValue = doubleValue;
            this.elementCount = elementCount;
            this.length = length;
            this.fieldType = fieldType;
            this.type = type;
            this.large = large;
        }

        public JsonValue(BytesWrapper data, long length, EnumType type, long elementCount, boolean large) {
            this(data, 0L, 0.0D, elementCount, length, null, type, large);
        }

        public JsonValue(EnumType type, long intValue) {
            this(null, intValue, 0.0D, 0L, 0L, null, type, false);
        }

        public JsonValue(EnumType type) {
            this(null, 0L, 0.0D, 0L, 0L, null, type, false);
        }

        public JsonValue(BytesWrapper data, long length) {
            this(data, 0L, 0.0D, 0L, length, null, EnumType.STRING, false);
        }

        public JsonValue(double doubleValue) {
            this(null, 0L, doubleValue, 0L, 0L, null, EnumType.DOUBLE, false);
        }

        public JsonValue(BytesWrapper data, long length, ColumnType fieldType) {
            this(data, 0L, 0.0D, 0L, length, fieldType, EnumType.OPAQUE, false);
        }

        public JsonValue key(int i) {
            shouldNot(i >= elementCount);
            int offsetSize = offsetSize(large);
            int keyEntrySize = keyEntrySize(large);
            int valueEntrySize = valueEntrySize(large);
            int entryOffset = keyEntryOffset(i);
            long keyOffset = readOffsetOrSize(data.forward(entryOffset), large);

            BytesWrapper keyDataPointer = data.forward(entryOffset + offsetSize);
            int keyLength = ByteUtil.toIntegerLE(keyDataPointer.getBytes(), keyDataPointer.getOffset(), 2);
            shouldNot(keyOffset < entryOffset + (elementCount - i) * keyEntrySize + elementCount * valueEntrySize || length < keyOffset + keyLength);
            return new JsonValue(data.forward((int) keyOffset), keyLength);
        }

        public JsonValue element(int i) {
            shouldNot(i >= elementCount);
            int entrySize = valueEntrySize(large);
            int entryOffset = valueEntryOffset(i, (int) elementCount, type);
            int type = data.at(entryOffset);
            if (inlinedType(type, large)) {
                return parseScalar(data.forward(entryOffset + 1), entrySize - 1, type);
            }

            long valueOffset = readOffsetOrSize(data.forward(entryOffset + 1), large);
            shouldNot(length < valueOffset || valueOffset < entryOffset + entrySize);
            return parseValue(data.forward((int) valueOffset), length - valueOffset, type);
        }


        private int keyEntryOffset(int i) {
            return 2 * offsetSize(large) + keyEntrySize(large) * i;
        }

        private int valueEntryOffset(int i, int elementCount, EnumType type) {
            int firstEntryOffset = 2 * offsetSize(large);
            if (type == EnumType.OBJECT) {
                firstEntryOffset += elementCount * keyEntrySize(large);
            }
            return firstEntryOffset + valueEntrySize(large) * i;
        }

        public BytesWrapper getData() {
            return data;
        }

        public long getIntValue() {
            return intValue;
        }

        public double getDoubleValue() {
            return doubleValue;
        }

        public long getElementCount() {
            return elementCount;
        }

        public long getLength() {
            return length;
        }

        public ColumnType getFieldType() {
            return fieldType;
        }

        public EnumType getType() {
            return type;
        }

        public boolean isLarge() {
            return large;
        }
    }

    private static class JsonWrapper {
        private final JsonValue value;

        public JsonWrapper(JsonValue value) {
            this.value = value;
        }

        public Object toJavaObject(ColumnDefinition columnDefinition) {
            return wrapperToJavaObject(this, columnDefinition);
        }

        private Type type() {
            if (value == null) {
                return Type.ERROR;
            }
            EnumType enumType = value.getType();
            if (enumType == EnumType.OPAQUE) {
                switch (value.getFieldType()) {
                    case NEWDECIMAL:
                        return Type.DECIMAL;
                    case DATETIME:
                        return Type.DATETIME;
                    case DATE:
                        return Type.DATE;
                    case TIME:
                        return Type.TIME;
                    case TIMESTAMP:
                        return Type.TIMESTAMP;
                }
            }

            Type result = Type.ERROR;
            switch (enumType) {
                case STRING:
                    return Type.STRING;
                case INT:
                    return Type.INT;
                case UINT:
                    return Type.UINT;
                case DOUBLE:
                    return Type.DOUBLE;
                case LITERAL_TRUE:
                case LITERAL_FALSE:
                    return Type.BOOLEAN;
                case LITERAL_NULL:
                    return Type.NULL;
                case ARRAY:
                    return Type.ARRAY;
                case OBJECT:
                    return Type.OBJECT;
                case ERROR:
                    return Type.ERROR;
                case OPAQUE:
                    return Type.OPAQUE;
            }
            return result;
        }

        private ColumnType fieldType() {
            return value.getFieldType();
        }

        private int length() {
            return value.getType() == EnumType.ARRAY || value.getType() == EnumType.OBJECT
                ? (int) value.getElementCount()
                : 1;
        }

        public JsonWrapper at(int i) {
            return new JsonWrapper(value.element(i));
        }

        public Object dateTimeToString() {
            long data = ByteUtil.toLongLE(value.getData().getBytes(), value.getData().getOffset(), 8);
            return JsonDateTime.convertToString(data, value.getFieldType());
        }

        public boolean getBoolean() {
            return value.getType() == EnumType.LITERAL_TRUE;
        }

        public long getInt() {
            return value.getIntValue();
        }

        public double getDouble() {
            return value.getDoubleValue();
        }

        public BytesWrapper getData() {
            return value.getData();
        }

        public long getDataLength() {
            return value.getLength();
        }

        public JsonValue getValue() {
            return value;
        }

        private enum Type {
            NULL,
            DECIMAL,
            INT,
            UINT,
            DOUBLE,
            STRING,
            OBJECT,
            ARRAY,
            BOOLEAN,
            DATE,
            TIME,
            DATETIME,
            TIMESTAMP,
            OPAQUE,
            ERROR
        }
    }

    private static class JsonWrapperObjectIterator implements Iterator<Map.Entry<String, JsonWrapper>> {
        private final ColumnDefinition columnDefinition;
        private final JsonValue binaryValue;
        private int currentElementIndex;
        private Map.Entry<String, JsonWrapper> currentMember;

        public JsonWrapperObjectIterator(ColumnDefinition columnDefinition, JsonValue binaryValue) {
            this.columnDefinition = columnDefinition;
            this.binaryValue = binaryValue;
            this.currentElementIndex = 0;
        }

        @Override
        public boolean hasNext() {
            return currentElementIndex != binaryValue.getElementCount();
        }

        @Override
        public Map.Entry<String, JsonWrapper> next() {
            initializeCurrentMember();
            currentElementIndex++;
            return currentMember;
        }

        private void initializeCurrentMember() {
            JsonValue keyJsonValue = binaryValue.key(currentElementIndex);
            String key = CharsetUtil.decode(
                keyJsonValue.getData().getBytes(), keyJsonValue.getData().getOffset(), (int) keyJsonValue.getLength(), columnDefinition.getCollation(), null
            );
            JsonWrapper value = new JsonWrapper(binaryValue.element(currentElementIndex));
            currentMember = new AbstractMap.SimpleEntry<>(key, value);
        }
    }
}
