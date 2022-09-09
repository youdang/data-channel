package me.progape.java.datachannel.protocol.shared.types.geometry;

import com.google.common.collect.Lists;
import me.progape.java.datachannel.protocol.utils.ByteUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author progape
 * @date 2022-05-04
 */
class WKBPolygon implements WKBGeometry {
    private final List<LinearRing> linearRings;

    WKBPolygon(byte[] rawValue, int offset) {
        byte byteOrder = rawValue[offset];
        offset += Constants.WKB_HEADER_LENGTH;

        int ringCount = ByteUtil.toInteger(rawValue, offset, 4, byteOrder == Constants.BIG_ENDIAN);
        linearRings = Lists.newArrayListWithCapacity(ringCount);

        offset += 4;
        while (offset < rawValue.length && ringCount > 0) {
            LinearRing linearRing = LinearRing.create(rawValue, byteOrder, offset);
            this.linearRings.add(linearRing);
            offset += linearRing.size();
            ringCount--;
        }
    }

    List<LinearRing> getLinearRings() {
        return linearRings;
    }

    @Override
    public int size() {
        return Constants.WKB_HEADER_LENGTH + 4 + linearRings.stream().mapToInt(LinearRing::size).sum();
    }

    @Override
    public String toString() {
        return linearRings.stream()
            .map(linearRing -> {
                return linearRing.getPoints().stream()
                    .map(point -> point.getX() + " " + point.getY())
                    .collect(Collectors.joining(",", "(", ")"));
            })
            .collect(Collectors.joining(",", "POLYGON(", ")"));
    }
}
