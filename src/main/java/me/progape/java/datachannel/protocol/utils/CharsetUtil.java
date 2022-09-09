package me.progape.java.datachannel.protocol.utils;

import me.progape.java.datachannel.protocol.Context;
import me.progape.java.datachannel.protocol.shared.CharacterSet;
import me.progape.java.datachannel.protocol.shared.Collation;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author progape
 * @date 2022-02-27
 */
public class CharsetUtil {
    public static String decode(byte[] bytes, Context context) {
        return decode(bytes, context.getCollation(), context);
    }

    public static String decode(byte[] bytes, Collation collation, Context context) {
        return decode(bytes, 0, bytes.length, collation, context);
    }

    public static String decode(byte[] bytes, int offset, int length, Collation collation, Context context) {
        String javaEncoding = getJavaEncodingByCollation(collation, context);
        if (javaEncoding == null) {
            return new String(bytes, offset, length, StandardCharsets.UTF_8);
        }

        try {
            Charset charset = Charset.forName(javaEncoding);
            return new String(bytes, offset, length, charset);
        } catch (Exception e) {
            return new String(bytes, offset, length, StandardCharsets.UTF_8);
        }
    }

    public static byte[] encode(String str, Context context) {
        String javaEncoding = getJavaEncodingByCollation(context.getCollation(), context);
        if (javaEncoding == null) {
            return str.getBytes(StandardCharsets.UTF_8);
        }

        try {
            Charset charset = Charset.forName(javaEncoding);
            return str.getBytes(charset);
        } catch (Exception e) {
            return str.getBytes(StandardCharsets.UTF_8);
        }
    }

    private static String getJavaEncodingByCollation(Collation collation, Context context) {
        String javaEncoding = context == null ? null : context.getCharacterEncoding();
        if (collation != null) {
            javaEncoding = collation.getCharacterSet().getMatchedJavaEncoding(javaEncoding);
        } else {
            collation = Collation.nameOf(javaEncoding);
            if (collation != null) {
                javaEncoding = collation.getCharacterSet().getMatchedJavaEncoding(javaEncoding);
            } else {
                CharacterSet characterSet = CharacterSet.nameOf(javaEncoding);
                if (characterSet != null) {
                    javaEncoding = characterSet.getMatchedJavaEncoding(javaEncoding);
                }
            }
        }
        return javaEncoding;
    }
}
