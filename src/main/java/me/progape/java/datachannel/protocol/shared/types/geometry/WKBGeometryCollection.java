package me.progape.java.datachannel.protocol.shared.types.geometry;

import com.google.common.collect.Lists;
import me.progape.java.datachannel.protocol.utils.ByteUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author progape
 * @date 2022-05-04
 */
class WKBGeometryCollection implements WKBGeometry {
    private final List<WKBGeometry> geometries;

    WKBGeometryCollection(byte[] rawValue, int offset) {
        byte byteOrder = rawValue[offset];
        offset += Constants.WKB_HEADER_LENGTH;

        int geometryCount = ByteUtil.toInteger(rawValue, offset, 4, byteOrder == Constants.BIG_ENDIAN);
        this.geometries = Lists.newArrayListWithCapacity(geometryCount);
        offset += 4;

        while (offset < rawValue.length && geometryCount > 0) {
            WKBGeometry geometry = WKBGeometryFactory.create(rawValue, offset);
            this.geometries.add(geometry);
            offset += geometry.size();
            geometryCount--;
        }
    }

    @Override
    public int size() {
        return Constants.WKB_HEADER_LENGTH + 4 + geometries.stream().mapToInt(WKBGeometry::size).sum();
    }

    @Override
    public String toString() {
        return geometries.stream()
            .map(WKBGeometry::toString)
            .collect(Collectors.joining(",", "GEOMETRYCOLLECTION(", ")"));
    }
}
