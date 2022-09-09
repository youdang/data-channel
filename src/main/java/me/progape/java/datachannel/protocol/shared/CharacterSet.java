package me.progape.java.datachannel.protocol.shared;

import com.google.common.collect.ImmutableList;

import java.util.Locale;

/**
 * @author progape
 * @date 2022-02-04
 */
public enum CharacterSet {
    BIG5("big5", new String[]{"Big5"}),
    DEC8("dec8", new String[]{"Cp1252"}),
    CP850("cp850", new String[]{"Cp850", "Cp437"}),
    HP8("hp8", new String[]{"Cp1252"}),
    KOI8R("koi8r", new String[]{"KOI8_R"}),
    LATIN1("latin1", new String[]{"Cp1252", "ISO8859_1"}),
    LATIN2("latin2", new String[]{"ISO8859_2"}),
    SWE7("swe7", new String[]{"Cp1252"}),
    ASCII("ascii", new String[]{"US-ASCII", "ASCII"}),
    UJIS("ujis", new String[]{"EUC_JP"}),
    SJIS("sjis", new String[]{"SHIFT_JIS", "Cp943", "WINDOWS-31J"}),
    HEBREW("hebrew", new String[]{"ISO8859_8"}),
    TIS620("tis620", new String[]{"TIS620"}),
    EUCKR("euckr", new String[]{"EUC-KR"}),
    KOI8U("koi8u", new String[]{"KOI8_R"}),
    GB2312("gb2312", new String[]{"GB2312"}),
    GREEK("greek", new String[]{"ISO8859_7", "greek"}),
    CP1250("cp1250", new String[]{"Cp1250"}),
    GBK("gbk", new String[]{"GBK"}),
    LATIN5("latin5", new String[]{"ISO8859_9"}),
    ARMSCII8("armscii8", new String[]{"Cp1252"}),
    UTF8("utf8", new String[]{"UTF-8"}),
    UCS2("ucs2", new String[]{"UnicodeBig"}),
    CP866("cp866", new String[]{"Cp866"}),
    KEYBCS2("keybcs2", new String[]{"Cp852"}),
    MACCE("macce", new String[]{"MacCentralEurope"}),
    MACROMAN("macroman", new String[]{"MacRoman"}),
    CP852("cp852", new String[]{"Cp852"}),
    LATIN7("latin7", new String[]{"ISO-8859-13"}),
    CP1251("cp1251", new String[]{"Cp1251"}),
    UTF16("utf16", new String[]{"UTF-16"}),
    UTF16LE("utf16le", new String[]{"UTF-16LE"}),
    CP1256("cp1256", new String[]{"Cp1256"}),
    CP1257("cp1257", new String[]{"Cp1257"}),
    UTF32("utf32", new String[]{"UTF-32"}),
    BINARY("binary", new String[]{"ISO8859_1"}),
    GEOSTD8("geostd8", new String[]{"Cp1252"}),
    CP932("cp932", new String[]{"WINDOWS-31J"}),
    EUCJPMS("eucjpms", new String[]{"EUC_JP_Solaris"}),
    GB18030("gb18030", new String[]{"GB18030"}),
    UTF8MB4("utf8mb4", new String[]{"UTF-8"}),
    ;

    private final String name;
    /**
     * upper-cased
     */
    private final ImmutableList<String> javaEncodings;

    CharacterSet(String name, String[] javaEncodings) {
        this.name = name;
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (String javaEncoding : javaEncodings) {
            builder.add(javaEncoding.toUpperCase(Locale.ENGLISH));
        }
        this.javaEncodings = builder.build();
    }

    public static CharacterSet nameOf(String name) {
        if (name == null) {
            return null;
        }
        for (CharacterSet characterSet : values()) {
            if (characterSet.name.equalsIgnoreCase(name)) {
                return characterSet;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getMatchedJavaEncoding(String javaEncoding) {
        if (javaEncoding != null && javaEncodings.contains(javaEncoding.toUpperCase(Locale.ENGLISH))) {
            return javaEncoding;
        }
        return javaEncodings.get(0);
    }
}
