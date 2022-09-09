package me.progape.java.datachannel.protocol.shared.types.geometry;

import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.protocol.utils.ByteUtil;

/**
 * @author progape
 * @date 2022-05-04
 */
public class MySQLGeometryFactory {
    public static MySQLGeometry create(byte[] rawValue) {
        if (rawValue == null) {
            return null;
        }
        if (rawValue.length < Constants.SRID_LENGTH + Constants.WKB_HEADER_LENGTH) {
            throw new ProtocolException("no enough bytes");
        }
        int srid = ByteUtil.toIntegerLE(rawValue, 0, 4);
        if (srid != 0) {
            throw new ProtocolException("unsupported spatial reference system ID:" + srid);
        }

        return WKBGeometryFactory.create(rawValue, Constants.SRID_LENGTH);
    }
}
