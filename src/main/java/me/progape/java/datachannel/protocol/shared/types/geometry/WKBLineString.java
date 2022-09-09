package me.progape.java.datachannel.protocol.shared.types.geometry;

import com.google.common.collect.Lists;
import me.progape.java.datachannel.protocol.utils.ByteUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author progape
 * @date 2022-05-04
 */
class WKBLineString implements WKBGeometry {
    private final List<Point> points;

    WKBLineString(byte[] rawValue, int offset) {
        byte byteOrder = rawValue[offset];
        offset += Constants.WKB_HEADER_LENGTH;

        int pointCount = ByteUtil.toInteger(rawValue, offset, 4, byteOrder == Constants.BIG_ENDIAN);
        this.points = Lists.newArrayListWithCapacity(pointCount);

        offset += 4;
        while (offset < rawValue.length && pointCount > 0) {
            Point point = Point.create(rawValue, byteOrder, offset);
            this.points.add(point);
            offset += point.size();
            pointCount--;
        }
    }

    List<Point> getPoints() {
        return points;
    }

    @Override
    public int size() {
        return Constants.WKB_HEADER_LENGTH + 4 + points.stream().mapToInt(Point::size).sum();
    }

    @Override
    public String toString() {
        return points.stream()
            .map(point -> point.getX() + " " + point.getY())
            .collect(Collectors.joining(",", "LINESTRING(", ")"));
    }
}
