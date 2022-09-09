package me.progape.java.datachannel.protocol.shared.types.geometry;

/**
 * @author progape
 * @date 2022-05-04
 */
enum WKBGeometryType {
    POINT(1),
    LINE_STRING(2),
    POLYGON(3),
    MULTI_POINT(4),
    MULTI_LINE_STRING(5),
    MULTI_POLYGON(6),
    GEOMETRY_COLLECTION(7),
    ;

    private final int code;

    static WKBGeometryType codeOf(Integer code) {
        if (code == null) {
            return null;
        }
        for (WKBGeometryType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }

    WKBGeometryType(int code) {
        this.code = code;
    }
}
