package me.progape.java.datachannel.protocol.shared.types.geometry;

import com.google.common.collect.Lists;
import me.progape.java.datachannel.protocol.utils.ByteUtil;

import java.util.List;

/**
 * @author progape
 * @date 2022-05-04
 */
class LinearRing implements HasSize {
    private final List<Point> points;

    LinearRing(List<Point> points) {
        this.points = points;
    }

    @Override
    public int size() {
        return 4 + points.stream().mapToInt(Point::size).sum();
    }

    List<Point> getPoints() {
        return points;
    }

    static LinearRing create(byte[] rawValue, byte byteOrder, int offset) {
        int pointCount = ByteUtil.toInteger(rawValue, offset, 4, byteOrder == Constants.BIG_ENDIAN);
        List<Point> points = Lists.newArrayListWithCapacity(pointCount);

        offset += 4;
        while (offset < rawValue.length && pointCount > 0) {
            Point point = Point.create(rawValue, byteOrder, offset);
            points.add(point);
            offset += point.size();
            pointCount--;
        }
        return new LinearRing(points);
    }
}
