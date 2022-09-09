package me.progape.java.datachannel.protocol.utils;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author progape
 * @date 2022-02-06
 */
public class ByteUtil {
    public static boolean isBitSet(byte bitmap, int index) {
        return index >= 0 && index < 8 && (bitmap & (0x01 << index)) != 0;
    }

    public static boolean isBitSet(byte[] bitmap, int index) {
        if (bitmap == null || index < 0 || index >= (bitmap.length << 8)) {
            return false;
        }
        return (bitmap[index / 8] & (0x01 << (index % 8))) != 0;
    }

    public static List<Integer> bitmap2Indices(byte[] bitmap, int maxCount) {
        List<Integer> indices = Lists.newArrayList();
        for (int i = 0; i < (bitmap.length << 3) && indices.size() < maxCount; i++) {
            if (isBitSet(bitmap, i)) {
                indices.add(i);
            }
        }
        return indices;
    }

    public static byte[] xor(byte[] source, byte[] with) {
        byte[] dest = new byte[source.length];
        for (int i = 0; i < source.length; i++) {
            dest[i] = (byte) (source[i] ^ with[i % with.length]);
        }
        return dest;
    }

    public static int ones(byte[] bitmap) {
        int result = 0;
        for (byte aByte : bitmap) {
            result += Integer.bitCount(aByte);
        }
        return result;
    }

    public static int ones(byte[] bitmap, int maxCount) {
        byte mask = (byte) (0xFF >>> (8 - (maxCount % 8)));
        if (mask != 0) {
            bitmap[bitmap.length - 1] = (byte) (bitmap[bitmap.length - 1] & mask);
        }

        int result = 0;
        for (byte aByte : bitmap) {
            result += Integer.bitCount(aByte);
        }
        return result;
    }

    public static int toInteger(byte[] bytes, int offset, int length, boolean isBigEndian) {
        return isBigEndian ? toIntegerBE(bytes, offset, length) : toIntegerLE(bytes, offset, length);
    }

    public static long toLong(byte[] bytes, int offset, int length, boolean isBigEndian) {
        return isBigEndian ? toLongBE(bytes, offset, length) : toLongLE(bytes, offset, length);
    }

    public static int toIntegerBE(byte[] bytes, int offset, int length) {
        if (length > 4) {
            throw new IllegalArgumentException("bytes too long");
        }
        if (offset + length > bytes.length) {
            throw new IllegalArgumentException("overflow");
        }
        int result = 0;
        for (int i = 0; i < length; i++) {
            int byteIndex = length - i - 1;
            result |= (Byte.toUnsignedInt(bytes[offset + i]) << (byteIndex << 3));
        }
        return result;
    }

    public static long toLongBE(byte[] bytes, int offset, int length) {
        if (length > 8) {
            throw new IllegalArgumentException("bytes too long");
        }
        if (offset + length > bytes.length) {
            throw new IllegalArgumentException("overflow");
        }
        long result = 0L;
        for (int i = 0; i < length; i++) {
            int byteIndex = length - i - 1;
            result |= (Byte.toUnsignedLong(bytes[offset + i]) << (byteIndex << 3));
        }
        return result;
    }

    public static int toIntegerLE(byte[] bytes, int offset, int length) {
        if (length > 4) {
            throw new IllegalArgumentException("bytes too long");
        }
        if (offset + length > bytes.length) {
            throw new IllegalArgumentException("overflow");
        }
        int result = 0;
        for (int i = 0; i < length; i++) {
            result |= (Byte.toUnsignedInt(bytes[offset + i]) << (i << 3));
        }
        return result;
    }

    public static long toLongLE(byte[] bytes, int offset, int length) {
        if (length > 8) {
            throw new IllegalArgumentException("bytes too long");
        }
        if (offset + length > bytes.length) {
            throw new IllegalArgumentException("overflow");
        }
        long result = 0;
        for (int i = 0; i < length; i++) {
            result |= (Byte.toUnsignedLong(bytes[offset + i]) << (i << 3));
        }
        return result;
    }
}
