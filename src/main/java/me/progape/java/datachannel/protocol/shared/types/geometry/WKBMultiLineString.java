package me.progape.java.datachannel.protocol.shared.types.geometry;

import com.google.common.collect.Lists;
import me.progape.java.datachannel.protocol.utils.ByteUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author progape
 * @date 2022-05-04
 */
class WKBMultiLineString implements WKBGeometry {
    private final List<WKBLineString> lineStrings;

    WKBMultiLineString(byte[] rawValue, int offset) {
        byte byteOrder = rawValue[offset];
        offset += Constants.WKB_HEADER_LENGTH;

        int lineStringCount = ByteUtil.toInteger(rawValue, offset, 4, byteOrder == Constants.BIG_ENDIAN);
        this.lineStrings = Lists.newArrayListWithCapacity(lineStringCount);
        offset += 4;

        while (offset < rawValue.length && lineStringCount > 0) {
            WKBLineString lineString = new WKBLineString(rawValue, offset);
            this.lineStrings.add(lineString);
            offset += lineString.size();
            lineStringCount--;
        }
    }

    @Override
    public int size() {
        return Constants.WKB_HEADER_LENGTH + 4 + lineStrings.stream().mapToInt(WKBLineString::size).sum();
    }

    @Override
    public String toString() {
        return lineStrings.stream()
            .map(lineString -> {
                return lineString.getPoints().stream()
                    .map(point -> point.getX() + " " + point.getY())
                    .collect(Collectors.joining(",", "(", ")"));
            })
            .collect(Collectors.joining(",", "MULTILINESTRING(", ")"));
    }
}
