package me.progape.java.datachannel.protocol.shared.types.geometry;

import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.protocol.utils.ByteUtil;

/**
 * @author progape
 * @date 2022-05-04
 */
class WKBGeometryFactory {
    static WKBGeometry create(byte[] rawValue, int offset) {
        byte byteOrder = rawValue[offset];
        int typeCode = ByteUtil.toInteger(rawValue, offset + 1, 4, byteOrder == Constants.BIG_ENDIAN);
        WKBGeometryType type = WKBGeometryType.codeOf(typeCode);
        if (type == null) {
            throw new ProtocolException("unsupported geometry type:" + typeCode);
        }

        switch (type) {
            case POINT:
                return new WKBPoint(rawValue, offset);
            case LINE_STRING:
                return new WKBLineString(rawValue, offset);
            case POLYGON:
                return new WKBPolygon(rawValue, offset);
            case MULTI_POINT:
                return new WKBMultiPoint(rawValue, offset);
            case MULTI_LINE_STRING:
                return new WKBMultiLineString(rawValue, offset);
            case MULTI_POLYGON:
                return new WKBMultiPolygon(rawValue, offset);
            case GEOMETRY_COLLECTION:
                return new WKBGeometryCollection(rawValue, offset);
            default:
                throw new ProtocolException("unsupported geometry type:" + typeCode);
        }
    }
}
