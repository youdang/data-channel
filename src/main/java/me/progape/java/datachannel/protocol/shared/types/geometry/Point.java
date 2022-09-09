package me.progape.java.datachannel.protocol.shared.types.geometry;

import me.progape.java.datachannel.protocol.utils.ByteUtil;

/**
 * @author progape
 * @date 2022-05-04
 */
class Point implements HasSize {
    private final double x;
    private final double y;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    double getX() {
        return x;
    }

    double getY() {
        return y;
    }

    @Override
    public int size() {
        return 16;
    }

    static Point create(byte[] rawValue, byte order, int offset) {
        boolean isBigEndian = order == Constants.BIG_ENDIAN;
        double x = Double.longBitsToDouble(ByteUtil.toLong(rawValue, offset, 8, isBigEndian));
        double y = Double.longBitsToDouble(ByteUtil.toLong(rawValue, offset + 8, 8, isBigEndian));
        return new Point(x, y);
    }
}
