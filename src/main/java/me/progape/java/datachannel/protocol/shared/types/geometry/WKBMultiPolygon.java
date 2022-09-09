package me.progape.java.datachannel.protocol.shared.types.geometry;

import com.google.common.collect.Lists;
import me.progape.java.datachannel.protocol.utils.ByteUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author progape
 * @date 2022-05-04
 */
class WKBMultiPolygon implements WKBGeometry {
    private final List<WKBPolygon> polygons;

    WKBMultiPolygon(byte[] rawValue, int offset) {
        byte byteOrder = rawValue[offset];
        offset += Constants.WKB_HEADER_LENGTH;

        int polygonCount = ByteUtil.toInteger(rawValue, offset, 4, byteOrder == Constants.BIG_ENDIAN);
        this.polygons = Lists.newArrayListWithCapacity(polygonCount);
        offset += 4;

        while (offset < rawValue.length && polygonCount > 0) {
            WKBPolygon polygon = new WKBPolygon(rawValue, offset);
            this.polygons.add(polygon);
            offset += polygon.size();
            polygonCount--;
        }
    }

    @Override
    public int size() {
        return Constants.WKB_HEADER_LENGTH + 4 + polygons.stream().mapToInt(WKBPolygon::size).sum();
    }

    @Override
    public String toString() {
        return polygons.stream()
            .map(polygon -> {
                return polygon.getLinearRings().stream()
                    .map(linearRing -> {
                        return linearRing.getPoints().stream()
                            .map(point -> point.getX() + " " + point.getY())
                            .collect(Collectors.joining(",", "(", ")"));
                    })
                    .collect(Collectors.joining(",", "(", ")"));
            })
            .collect(Collectors.joining(",", "MULTIPOLYGON(", ")"));
    }
}
