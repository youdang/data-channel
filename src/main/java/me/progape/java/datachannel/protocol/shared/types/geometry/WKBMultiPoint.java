package me.progape.java.datachannel.protocol.shared.types.geometry;

import com.google.common.collect.Lists;
import me.progape.java.datachannel.protocol.utils.ByteUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author progape
 * @date 2022-05-04
 */
class WKBMultiPoint implements WKBGeometry {
    private final List<WKBPoint> points;

    WKBMultiPoint(byte[] rawValue, int offset) {
        byte byteOrder = rawValue[offset];
        offset += Constants.WKB_HEADER_LENGTH;

        int pointCount = ByteUtil.toInteger(rawValue, offset, 4, byteOrder == Constants.BIG_ENDIAN);
        this.points = Lists.newArrayListWithCapacity(pointCount);

        offset += 4;
        while (offset < rawValue.length && pointCount > 0) {
            WKBPoint point = new WKBPoint(rawValue, offset);
            this.points.add(point);
            offset += point.size();
            pointCount--;
        }
    }

    @Override
    public int size() {
        return Constants.WKB_HEADER_LENGTH + 4 + points.stream().mapToInt(WKBPoint::size).sum();
    }

    @Override
    public String toString() {
        return points.stream()
            .map(point -> point.getPoint().getX() + " " + point.getPoint().getY())
            .collect(Collectors.joining(",", "MULTIPOINT(", ")"));
    }
}
