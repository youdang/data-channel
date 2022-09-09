package me.progape.java.datachannel.transport.utils;

import io.netty.buffer.ByteBuf;

/**
 * Little-Endian
 *
 * @author progape
 * @date 2022-01-09
 */
public final class ByteBufUtil {
    public static short readInt1(ByteBuf buffer) {
        if (buffer.readableBytes() < 1) {
            throw new RuntimeException("insufficient bytes");
        }
        return (short) (buffer.readByte() & 0xFF);
    }

    public static int readInt2(ByteBuf buffer) {
        if (buffer.readableBytes() < 2) {
            throw new RuntimeException("insufficient bytes");
        }
        int result = 0;

        result |= buffer.readUnsignedByte();
        result |= ((int) buffer.readUnsignedByte() << 8);

        return result;
    }

    public static int readInt3(ByteBuf buffer) {
        if (buffer.readableBytes() < 3) {
            throw new RuntimeException("insufficient bytes");
        }
        int result = 0;

        result |= buffer.readUnsignedByte();
        result |= (((int) buffer.readUnsignedByte()) << 8);
        result |= (((int) buffer.readUnsignedByte()) << 16);

        return result;
    }

    public static long readInt4(ByteBuf buffer) {
        if (buffer.readableBytes() < 4) {
            throw new RuntimeException("insufficient bytes");
        }
        long result = 0;

        result |= buffer.readUnsignedByte();
        result |= (((long) buffer.readUnsignedByte()) << 8);
        result |= (((long) buffer.readUnsignedByte()) << 16);
        result |= (((long) buffer.readUnsignedByte()) << 24);

        return result;
    }

    public static long readInt6(ByteBuf buffer) {
        if (buffer.readableBytes() < 6) {
            throw new RuntimeException("insufficient bytes");
        }
        long result = 0L;

        result |= buffer.readUnsignedByte();
        result |= (((long) buffer.readUnsignedByte()) << 8);
        result |= (((long) buffer.readUnsignedByte()) << 16);
        result |= (((long) buffer.readUnsignedByte()) << 24);
        result |= (((long) buffer.readUnsignedByte()) << 32);
        result |= (((long) buffer.readUnsignedByte()) << 40);

        return result;
    }

    public static long readInt8(ByteBuf buffer) {
        if (buffer.readableBytes() < 8) {
            throw new RuntimeException("insufficient bytes");
        }
        long result = 0L;

        result |= buffer.readUnsignedByte();
        result |= (((long) buffer.readUnsignedByte()) << 8);
        result |= (((long) buffer.readUnsignedByte()) << 16);
        result |= (((long) buffer.readUnsignedByte()) << 24);
        result |= (((long) buffer.readUnsignedByte()) << 32);
        result |= (((long) buffer.readUnsignedByte()) << 40);
        result |= (((long) buffer.readUnsignedByte()) << 48);
        result |= (((long) buffer.readUnsignedByte()) << 56);

        return result;
    }

    public static long readLengthEncodedInt(ByteBuf buffer) {
        int firstByte = buffer.readUnsignedByte();
        if (firstByte < 0xFB) {
            return firstByte;
        } else if (firstByte == 0xFC) {
            return readInt2(buffer);
        } else if (firstByte == 0xFD) {
            return readInt3(buffer);
        } else if (firstByte == 0xFE) {
            return readInt8(buffer);
        } else {
            throw new RuntimeException("invalid length encoded integer");
        }
    }

    public static byte[] readNullTerminatedString(ByteBuf buffer) {
        int length = buffer.bytesBefore((byte) 0x00);
        if (length < 0) {
            throw new RuntimeException("invalid string type");
        }
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        // skip 0x00
        buffer.skipBytes(1);

        return bytes;
    }

    public static byte[] readFixedLengthString(ByteBuf buffer, int length) {
        if (buffer.readableBytes() < length) {
            throw new RuntimeException("insufficient bytes");
        }
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        return bytes;
    }

    public static byte[] readLengthEncodedString(ByteBuf buffer) {
        long length = readLengthEncodedInt(buffer);
        if (length > Integer.MAX_VALUE) {
            throw new RuntimeException("data too long");
        }
        if (buffer.readableBytes() < length) {
            throw new RuntimeException("insufficient bytes");
        }
        byte[] bytes = new byte[(int) length];
        buffer.readBytes(bytes);
        return bytes;
    }

    public static byte[] readRestOfPacketString(ByteBuf buffer) {
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        return bytes;
    }

    public static void writeInt1(ByteBuf buffer, short value) {
        buffer.writeByte(value & 0xFF);
    }

    public static void writeInt2(ByteBuf buffer, int value) {
        buffer.writeByte(value & 0xFF);
        buffer.writeByte((value >>> 8) & 0xFF);
    }

    public static void writeInt3(ByteBuf buffer, int value) {
        buffer.writeByte(value & 0xFF);
        buffer.writeByte((value >>> 8) & 0xFF);
        buffer.writeByte((value >>> 16) & 0xFF);
    }

    public static void writeInt4(ByteBuf buffer, long value) {
        buffer.writeByte((int) (value & 0xFF));
        buffer.writeByte((int) ((value >>> 8) & 0xFF));
        buffer.writeByte((int) ((value >>> 16) & 0xFF));
        buffer.writeByte((int) ((value >>> 24) & 0xFF));
    }

    public static void writeInt6(ByteBuf buffer, long value) {
        buffer.writeByte((int) (value & 0xFF));
        buffer.writeByte((int) ((value >>> 8) & 0xFF));
        buffer.writeByte((int) ((value >>> 16) & 0xFF));
        buffer.writeByte((int) ((value >>> 24) & 0xFF));
        buffer.writeByte((int) ((value >>> 32) & 0xFF));
        buffer.writeByte((int) ((value >>> 40) & 0xFF));
    }

    public static void writeInt8(ByteBuf buffer, long value) {
        buffer.writeByte((int) (value & 0xFF));
        buffer.writeByte((int) ((value >>> 8) & 0xFF));
        buffer.writeByte((int) ((value >>> 16) & 0xFF));
        buffer.writeByte((int) ((value >>> 24) & 0xFF));
        buffer.writeByte((int) ((value >>> 32) & 0xFF));
        buffer.writeByte((int) ((value >>> 40) & 0xFF));
        buffer.writeByte((int) ((value >>> 48) & 0xFF));
        buffer.writeByte((int) ((value >>> 56) & 0xFF));
    }

    public static void writeFixedLengthString(ByteBuf buffer, byte[] value) {
        if (value == null) {
            throw new RuntimeException("invalid value");
        }
        buffer.writeBytes(value);
    }

    public static void writeNullTerminatedString(ByteBuf buffer, byte[] value) {
        if (value == null) {
            throw new RuntimeException("invalid value");
        }
        buffer.writeBytes(value);
        buffer.writeByte(0x00);
    }

    public static void writeRestOfPacketString(ByteBuf buffer, byte[] value) {
        if (value == null) {
            throw new RuntimeException("invalid value");
        }

        buffer.writeBytes(value);
    }
}
