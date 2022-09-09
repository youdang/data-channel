package me.progape.java.datachannel.protocol.binlog;

import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.protocol.shared.types.decimal.MySQLDecimal;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;

/**
 * internal binlog column type
 *
 * <p>
 * see rows_event.h
 * </p>
 *
 * @author progape
 * @date 2022-02-06
 */
public enum ColumnType {
    DECIMAL(0x00),
    TINY(0x01) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            return ByteBufUtil.readFixedLengthString(payload, 1);
        }
    },
    SHORT(0x02) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            return ByteBufUtil.readFixedLengthString(payload, 2);
        }
    },
    LONG(0x03) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            return ByteBufUtil.readFixedLengthString(payload, 4);
        }
    },
    FLOAT(0x04) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            return ByteBufUtil.readFixedLengthString(payload, 4);
        }
    },
    DOUBLE(0x05) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            return ByteBufUtil.readFixedLengthString(payload, 8);
        }
    },
    NULL(0x06),
    TIMESTAMP(0x07) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            return ByteBufUtil.readFixedLengthString(payload, 4);
        }
    },
    LONGLONG(0x08) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            return ByteBufUtil.readFixedLengthString(payload, 8);
        }
    },
    INT24(0x09) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            return ByteBufUtil.readFixedLengthString(payload, 3);
        }
    },
    DATE(0x0A),
    TIME(0x0B) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            return ByteBufUtil.readFixedLengthString(payload, 3);
        }
    },
    DATETIME(0x0C) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            return ByteBufUtil.readFixedLengthString(payload, 8);
        }
    },
    YEAR(0x0D) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            return ByteBufUtil.readFixedLengthString(payload, 1);
        }
    },
    NEWDATE(0x0E) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            return ByteBufUtil.readFixedLengthString(payload, 3);
        }
    },
    VARCHAR(0x0F) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            int len = meta;
            if (len < 256) {
                len = ByteBufUtil.readInt1(payload);
                return ByteBufUtil.readFixedLengthString(payload, len);
            } else {
                len = ByteBufUtil.readInt2(payload);
                return ByteBufUtil.readFixedLengthString(payload, len);
            }
        }
    },
    BIT(0x10) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            int nbits = (meta >>> 8) * 8 + (meta & 0xFF);
            return ByteBufUtil.readFixedLengthString(payload, (nbits + 7) / 8);
        }
    },
    TIMESTAMP2(0x11) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            return ByteBufUtil.readFixedLengthString(payload, 4 + (meta + 1) / 2);
        }
    },
    DATETIME2(0x12) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            return ByteBufUtil.readFixedLengthString(payload, 5 + (meta + 1) / 2);
        }
    },
    TIME2(0x13) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            return ByteBufUtil.readFixedLengthString(payload, 3 + (meta + 1) / 2);
        }
    },
    TYPED_ARRAY(0x14),
    JSON(0xF5) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            long len;
            switch (meta) {
                case 1:
                    len = ByteBufUtil.readInt1(payload);
                    break;
                case 2:
                    len = ByteBufUtil.readInt2(payload);
                    break;
                case 3:
                    len = ByteBufUtil.readInt3(payload);
                    break;
                case 4:
                    len = ByteBufUtil.readInt4(payload);
                    break;
                default:
                    return null;
            }
            return ByteBufUtil.readFixedLengthString(payload, (int) len);
        }
    },
    NEWDECIMAL(0xF6) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            int precision = meta >>> 8;
            int scale = meta & 0xFF;
            int binSize = new MySQLDecimal(precision, scale).getBinSize();
            return ByteBufUtil.readFixedLengthString(payload, binSize);
        }
    },
    ENUM(0xF7) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            int len = meta & 0xFF;
            if (len != 1 && len != 2) {
                return null;
            }
            return ByteBufUtil.readFixedLengthString(payload, len);
        }
    },
    SET(0xF8) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            return ByteBufUtil.readFixedLengthString(payload, meta & 0xFF);
        }
    },
    TINY_BLOB(0xF9),
    MEDIUM_BLOB(0xFA),
    LONG_BLOB(0xFB),
    BLOB(0xFC) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            long len;
            switch (meta) {
                case 1:
                    len = ByteBufUtil.readInt1(payload);
                    break;
                case 2:
                    len = ByteBufUtil.readInt2(payload);
                    break;
                case 3:
                    len = ByteBufUtil.readInt3(payload);
                    break;
                case 4:
                    len = ByteBufUtil.readInt4(payload);
                    break;
                default:
                    return null;
            }
            return ByteBufUtil.readFixedLengthString(payload, (int) len);
        }
    },
    VAR_STRING(0xFD) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            int len = meta;
            if (len < 256) {
                len = ByteBufUtil.readInt1(payload);
                return ByteBufUtil.readFixedLengthString(payload, len);
            } else {
                len = ByteBufUtil.readInt2(payload);
                return ByteBufUtil.readFixedLengthString(payload, len);
            }
        }
    },
    STRING(0xFE) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            if (length < 256) {
                int len = ByteBufUtil.readInt1(payload);
                return ByteBufUtil.readFixedLengthString(payload, len);
            } else {
                int len = ByteBufUtil.readInt2(payload);
                return ByteBufUtil.readFixedLengthString(payload, len);
            }
        }
    },
    GEOMETRY(0xFF) {
        @Override
        public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
            long len;
            switch (meta) {
                case 1:
                    len = ByteBufUtil.readInt1(payload);
                    break;
                case 2:
                    len = ByteBufUtil.readInt2(payload);
                    break;
                case 3:
                    len = ByteBufUtil.readInt3(payload);
                    break;
                case 4:
                    len = ByteBufUtil.readInt4(payload);
                    break;
                default:
                    return null;
            }
            return ByteBufUtil.readFixedLengthString(payload, (int) len);
        }
    },
    ;

    private final int code;

    ColumnType(int code) {
        this.code = code;
    }

    public static ColumnType codeOf(Integer code) {
        if (code == null) {
            return null;
        }
        for (ColumnType columnType : values()) {
            if (columnType.code == code) {
                return columnType;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public byte[] readBinlogValue(int meta, int length, ByteBuf payload) {
        throw new ProtocolException("invalid binlog type");
    }
}
