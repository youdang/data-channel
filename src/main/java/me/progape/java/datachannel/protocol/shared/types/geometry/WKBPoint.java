package me.progape.java.datachannel.protocol.shared.types.geometry;

/**
 * @author progape
 * @date 2022-05-04
 */
class WKBPoint implements WKBGeometry {
    private final Point point;

    WKBPoint(byte[] rawValue, int offset) {
        byte byteOrder = rawValue[offset];

        offset += Constants.WKB_HEADER_LENGTH;
        point = Point.create(rawValue, byteOrder, offset);
    }

    Point getPoint() {
        return point;
    }

    @Override
    public int size() {
        return Constants.WKB_HEADER_LENGTH + point.size();
    }

    @Override
    public String toString() {
        return "POINT(" + point.getX() + " " + point.getY() + ")";
    }
}
