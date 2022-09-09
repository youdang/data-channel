package me.progape.java.datachannel.protocol.shared;

/**
 * @author progape
 * @date 2022-02-19
 */
public enum Collation {
    BIG5_CHINESE_CI(1, "big5_chinese_ci", CharacterSet.BIG5, true),
    LATIN2_CZECH_CS(2, "latin2_czech_cs", CharacterSet.LATIN2, false),
    DEC8_SWEDISH_CI(3, "dec8_swedish_ci", CharacterSet.DEC8, true),
    CP850_GENERAL_CI(4, "cp850_general_ci", CharacterSet.CP850, true),
    LATIN1_GERMAN1_CI(5, "latin1_german1_ci", CharacterSet.LATIN1, false),
    HP8_ENGLISH_CI(6, "hp8_english_ci", CharacterSet.HP8, true),
    KOI8R_GENERAL_CI(7, "koi8r_general_ci", CharacterSet.KOI8R, true),
    LATIN1_SWEDISH_CI(8, "latin1_swedish_ci", CharacterSet.LATIN1, true),
    LATIN2_GENERAL_CI(9, "latin2_general_ci", CharacterSet.LATIN2, true),
    SWE7_SWEDISH_CI(10, "swe7_swedish_ci", CharacterSet.SWE7, true),
    ASCII_GENERAL_CI(11, "ascii_general_ci", CharacterSet.ASCII, true),
    UJIS_JAPANESE_CI(12, "ujis_japanese_ci", CharacterSet.UJIS, true),
    SJIS_JAPANESE_CI(13, "sjis_japanese_ci", CharacterSet.SJIS, true),
    CP1251_BULGARIAN_CI(14, "cp1251_bulgarian_ci", CharacterSet.CP1251, false),
    LATIN1_DANISH_CI(15, "latin1_danish_ci", CharacterSet.LATIN1, false),
    HEBREW_GENERAL_CI(16, "hebrew_general_ci", CharacterSet.HEBREW, true),
    TIS620_THAI_CI(18, "tis620_thai_ci", CharacterSet.TIS620, true),
    EUCKR_KOREAN_CI(19, "euckr_korean_ci", CharacterSet.EUCKR, true),
    LATIN7_ESTONIAN_CS(20, "latin7_estonian_cs", CharacterSet.LATIN7, false),
    LATIN2_HUNGARIAN_CI(21, "latin2_hungarian_ci", CharacterSet.LATIN2, false),
    KOI8U_GENERAL_CI(22, "koi8u_general_ci", CharacterSet.KOI8U, true),
    CP1251_UKRAINIAN_CI(23, "cp1251_ukrainian_ci", CharacterSet.CP1251, false),
    GB2312_CHINESE_CI(24, "gb2312_chinese_ci", CharacterSet.GB2312, true),
    GREEK_GENERAL_CI(25, "greek_general_ci", CharacterSet.GREEK, true),
    CP1250_GENERAL_CI(26, "cp1250_general_ci", CharacterSet.CP1250, true),
    LATIN2_CROATIAN_CI(27, "latin2_croatian_ci", CharacterSet.LATIN2, false),
    GBK_CHINESE_CI(28, "gbk_chinese_ci", CharacterSet.GBK, true),
    CP1257_LITHUANIAN_CI(29, "cp1257_lithuanian_ci", CharacterSet.CP1257, false),
    LATIN5_TURKISH_CI(30, "latin5_turkish_ci", CharacterSet.LATIN5, true),
    LATIN1_GERMAN2_CI(31, "latin1_german2_ci", CharacterSet.LATIN1, false),
    ARMSCII8_GENERAL_CI(32, "armscii8_general_ci", CharacterSet.ARMSCII8, true),
    UTF8_GENERAL_CI(33, "utf8_general_ci", CharacterSet.UTF8, true),
    CP1250_CZECH_CS(34, "cp1250_czech_cs", CharacterSet.CP1250, false),
    UCS2_GENERAL_CI(35, "ucs2_general_ci", CharacterSet.UCS2, true),
    CP866_GENERAL_CI(36, "cp866_general_ci", CharacterSet.CP866, true),
    KEYBCS2_GENERAL_CI(37, "keybcs2_general_ci", CharacterSet.KEYBCS2, true),
    MACCE_GENERAL_CI(38, "macce_general_ci", CharacterSet.MACCE, true),
    MACROMAN_GENERAL_CI(39, "macroman_general_ci", CharacterSet.MACROMAN, true),
    CP852_GENERAL_CI(40, "cp852_general_ci", CharacterSet.CP852, true),
    LATIN7_GENERAL_CI(41, "latin7_general_ci", CharacterSet.LATIN7, true),
    LATIN7_GENERAL_CS(42, "latin7_general_cs", CharacterSet.LATIN7, false),
    MACCE_BIN(43, "macce_bin", CharacterSet.MACCE, false),
    CP1250_CROATIAN_CI(44, "cp1250_croatian_ci", CharacterSet.CP1250, false),
    UTF8MB4_GENERAL_CI(45, "utf8mb4_general_ci", CharacterSet.UTF8MB4, true),
    UTF8MB4_BIN(46, "utf8mb4_bin", CharacterSet.UTF8MB4, false),
    LATIN1_BIN(47, "latin1_bin", CharacterSet.LATIN1, false),
    LATIN1_GENERAL_CI(48, "latin1_general_ci", CharacterSet.LATIN1, false),
    LATIN1_GENERAL_CS(49, "latin1_general_cs", CharacterSet.LATIN1, false),
    CP1251_BIN(50, "cp1251_bin", CharacterSet.CP1251, false),
    CP1251_GENERAL_CI(51, "cp1251_general_ci", CharacterSet.CP1251, true),
    CP1251_GENERAL_CS(52, "cp1251_general_cs", CharacterSet.CP1251, false),
    MACROMAN_BIN(53, "macroman_bin", CharacterSet.MACROMAN, false),
    UTF16_GENERAL_CI(54, "utf16_general_ci", CharacterSet.UTF16, true),
    UTF16_BIN(55, "utf16_bin", CharacterSet.UTF16, false),
    UTF16LE_GENERAL_CI(56, "utf16le_general_ci", CharacterSet.UTF16LE, true),
    CP1256_GENERAL_CI(57, "cp1256_general_ci", CharacterSet.CP1256, true),
    CP1257_BIN(58, "cp1257_bin", CharacterSet.CP1257, false),
    CP1257_GENERAL_CI(59, "cp1257_general_ci", CharacterSet.CP1257, true),
    UTF32_GENERAL_CI(60, "utf32_general_ci", CharacterSet.UTF32, true),
    UTF32_BIN(61, "utf32_bin", CharacterSet.UTF32, false),
    UTF16LE_BIN(62, "utf16le_bin", CharacterSet.UTF16LE, false),
    BINARY(63, "CharacterSet.BINARY", CharacterSet.BINARY, true),
    ARMSCII8_BIN(64, "armscii8_bin", CharacterSet.ARMSCII8, false),
    ASCII_BIN(65, "ascii_bin", CharacterSet.ASCII, false),
    CP1250_BIN(66, "cp1250_bin", CharacterSet.CP1250, false),
    CP1256_BIN(67, "cp1256_bin", CharacterSet.CP1256, false),
    CP866_BIN(68, "cp866_bin", CharacterSet.CP866, false),
    DEC8_BIN(69, "dec8_bin", CharacterSet.DEC8, false),
    GREEK_BIN(70, "greek_bin", CharacterSet.GREEK, false),
    HEBREW_BIN(71, "hebrew_bin", CharacterSet.HEBREW, false),
    HP8_BIN(72, "hp8_bin", CharacterSet.HP8, false),
    KEYBCS2_BIN(73, "keybcs2_bin", CharacterSet.KEYBCS2, false),
    KOI8R_BIN(74, "koi8r_bin", CharacterSet.KOI8R, false),
    KOI8U_BIN(75, "koi8u_bin", CharacterSet.KOI8U, false),
    LATIN2_BIN(77, "latin2_bin", CharacterSet.LATIN2, false),
    LATIN5_BIN(78, "latin5_bin", CharacterSet.LATIN5, false),
    LATIN7_BIN(79, "latin7_bin", CharacterSet.LATIN7, false),
    CP850_BIN(80, "cp850_bin", CharacterSet.CP850, false),
    CP852_BIN(81, "cp852_bin", CharacterSet.CP852, false),
    SWE7_BIN(82, "swe7_bin", CharacterSet.SWE7, false),
    UTF8_BIN(83, "utf8_bin", CharacterSet.UTF8, false),
    BIG5_BIN(84, "big5_bin", CharacterSet.BIG5, false),
    EUCKR_BIN(85, "euckr_bin", CharacterSet.EUCKR, false),
    GB2312_BIN(86, "gb2312_bin", CharacterSet.GB2312, false),
    GBK_BIN(87, "gbk_bin", CharacterSet.GBK, false),
    SJIS_BIN(88, "sjis_bin", CharacterSet.SJIS, false),
    TIS620_BIN(89, "tis620_bin", CharacterSet.TIS620, false),
    UCS2_BIN(90, "ucs2_bin", CharacterSet.UCS2, false),
    UJIS_BIN(91, "ujis_bin", CharacterSet.UJIS, false),
    GEOSTD8_GENERAL_CI(92, "geostd8_general_ci", CharacterSet.GEOSTD8, true),
    GEOSTD8_BIN(93, "geostd8_bin", CharacterSet.GEOSTD8, false),
    LATIN1_SPANISH_CI(94, "latin1_spanish_ci", CharacterSet.LATIN1, false),
    CP932_JAPANESE_CI(95, "cp932_japanese_ci", CharacterSet.CP932, true),
    CP932_BIN(96, "cp932_bin", CharacterSet.CP932, false),
    EUCJPMS_JAPANESE_CI(97, "eucjpms_japanese_ci", CharacterSet.EUCJPMS, true),
    EUCJPMS_BIN(98, "eucjpms_bin", CharacterSet.EUCJPMS, false),
    CP1250_POLISH_CI(99, "cp1250_polish_ci", CharacterSet.CP1250, false),
    UTF16_UNICODE_CI(101, "utf16_unicode_ci", CharacterSet.UTF16, false),
    UTF16_ICELANDIC_CI(102, "utf16_icelandic_ci", CharacterSet.UTF16, false),
    UTF16_LATVIAN_CI(103, "utf16_latvian_ci", CharacterSet.UTF16, false),
    UTF16_ROMANIAN_CI(104, "utf16_romanian_ci", CharacterSet.UTF16, false),
    UTF16_SLOVENIAN_CI(105, "utf16_slovenian_ci", CharacterSet.UTF16, false),
    UTF16_POLISH_CI(106, "utf16_polish_ci", CharacterSet.UTF16, false),
    UTF16_ESTONIAN_CI(107, "utf16_estonian_ci", CharacterSet.UTF16, false),
    UTF16_SPANISH_CI(108, "utf16_spanish_ci", CharacterSet.UTF16, false),
    UTF16_SWEDISH_CI(109, "utf16_swedish_ci", CharacterSet.UTF16, false),
    UTF16_TURKISH_CI(110, "utf16_turkish_ci", CharacterSet.UTF16, false),
    UTF16_CZECH_CI(111, "utf16_czech_ci", CharacterSet.UTF16, false),
    UTF16_DANISH_CI(112, "utf16_danish_ci", CharacterSet.UTF16, false),
    UTF16_LITHUANIAN_CI(113, "utf16_lithuanian_ci", CharacterSet.UTF16, false),
    UTF16_SLOVAK_CI(114, "utf16_slovak_ci", CharacterSet.UTF16, false),
    UTF16_SPANISH2_CI(115, "utf16_spanish2_ci", CharacterSet.UTF16, false),
    UTF16_ROMAN_CI(116, "utf16_roman_ci", CharacterSet.UTF16, false),
    UTF16_PERSIAN_CI(117, "utf16_persian_ci", CharacterSet.UTF16, false),
    UTF16_ESPERANTO_CI(118, "utf16_esperanto_ci", CharacterSet.UTF16, false),
    UTF16_HUNGARIAN_CI(119, "utf16_hungarian_ci", CharacterSet.UTF16, false),
    UTF16_SINHALA_CI(120, "utf16_sinhala_ci", CharacterSet.UTF16, false),
    UTF16_GERMAN2_CI(121, "utf16_german2_ci", CharacterSet.UTF16, false),
    UTF16_CROATIAN_CI(122, "utf16_croatian_ci", CharacterSet.UTF16, false),
    UTF16_UNICODE_520_CI(123, "utf16_unicode_520_ci", CharacterSet.UTF16, false),
    UTF16_VIETNAMESE_CI(124, "utf16_vietnamese_ci", CharacterSet.UTF16, false),
    UCS2_UNICODE_CI(128, "ucs2_unicode_ci", CharacterSet.UCS2, false),
    UCS2_ICELANDIC_CI(129, "ucs2_icelandic_ci", CharacterSet.UCS2, false),
    UCS2_LATVIAN_CI(130, "ucs2_latvian_ci", CharacterSet.UCS2, false),
    UCS2_ROMANIAN_CI(131, "ucs2_romanian_ci", CharacterSet.UCS2, false),
    UCS2_SLOVENIAN_CI(132, "ucs2_slovenian_ci", CharacterSet.UCS2, false),
    UCS2_POLISH_CI(133, "ucs2_polish_ci", CharacterSet.UCS2, false),
    UCS2_ESTONIAN_CI(134, "ucs2_estonian_ci", CharacterSet.UCS2, false),
    UCS2_SPANISH_CI(135, "ucs2_spanish_ci", CharacterSet.UCS2, false),
    UCS2_SWEDISH_CI(136, "ucs2_swedish_ci", CharacterSet.UCS2, false),
    UCS2_TURKISH_CI(137, "ucs2_turkish_ci", CharacterSet.UCS2, false),
    UCS2_CZECH_CI(138, "ucs2_czech_ci", CharacterSet.UCS2, false),
    UCS2_DANISH_CI(139, "ucs2_danish_ci", CharacterSet.UCS2, false),
    UCS2_LITHUANIAN_CI(140, "ucs2_lithuanian_ci", CharacterSet.UCS2, false),
    UCS2_SLOVAK_CI(141, "ucs2_slovak_ci", CharacterSet.UCS2, false),
    UCS2_SPANISH2_CI(142, "ucs2_spanish2_ci", CharacterSet.UCS2, false),
    UCS2_ROMAN_CI(143, "ucs2_roman_ci", CharacterSet.UCS2, false),
    UCS2_PERSIAN_CI(144, "ucs2_persian_ci", CharacterSet.UCS2, false),
    UCS2_ESPERANTO_CI(145, "ucs2_esperanto_ci", CharacterSet.UCS2, false),
    UCS2_HUNGARIAN_CI(146, "ucs2_hungarian_ci", CharacterSet.UCS2, false),
    UCS2_SINHALA_CI(147, "ucs2_sinhala_ci", CharacterSet.UCS2, false),
    UCS2_GERMAN2_CI(148, "ucs2_german2_ci", CharacterSet.UCS2, false),
    UCS2_CROATIAN_CI(149, "ucs2_croatian_ci", CharacterSet.UCS2, false),
    UCS2_UNICODE_520_CI(150, "ucs2_unicode_520_ci", CharacterSet.UCS2, false),
    UCS2_VIETNAMESE_CI(151, "ucs2_vietnamese_ci", CharacterSet.UCS2, false),
    UCS2_GENERAL_MYSQL500_CI(159, "ucs2_general_mysql500_ci", CharacterSet.UCS2, false),
    UTF32_UNICODE_CI(160, "utf32_unicode_ci", CharacterSet.UTF32, false),
    UTF32_ICELANDIC_CI(161, "utf32_icelandic_ci", CharacterSet.UTF32, false),
    UTF32_LATVIAN_CI(162, "utf32_latvian_ci", CharacterSet.UTF32, false),
    UTF32_ROMANIAN_CI(163, "utf32_romanian_ci", CharacterSet.UTF32, false),
    UTF32_SLOVENIAN_CI(164, "utf32_slovenian_ci", CharacterSet.UTF32, false),
    UTF32_POLISH_CI(165, "utf32_polish_ci", CharacterSet.UTF32, false),
    UTF32_ESTONIAN_CI(166, "utf32_estonian_ci", CharacterSet.UTF32, false),
    UTF32_SPANISH_CI(167, "utf32_spanish_ci", CharacterSet.UTF32, false),
    UTF32_SWEDISH_CI(168, "utf32_swedish_ci", CharacterSet.UTF32, false),
    UTF32_TURKISH_CI(169, "utf32_turkish_ci", CharacterSet.UTF32, false),
    UTF32_CZECH_CI(170, "utf32_czech_ci", CharacterSet.UTF32, false),
    UTF32_DANISH_CI(171, "utf32_danish_ci", CharacterSet.UTF32, false),
    UTF32_LITHUANIAN_CI(172, "utf32_lithuanian_ci", CharacterSet.UTF32, false),
    UTF32_SLOVAK_CI(173, "utf32_slovak_ci", CharacterSet.UTF32, false),
    UTF32_SPANISH2_CI(174, "utf32_spanish2_ci", CharacterSet.UTF32, false),
    UTF32_ROMAN_CI(175, "utf32_roman_ci", CharacterSet.UTF32, false),
    UTF32_PERSIAN_CI(176, "utf32_persian_ci", CharacterSet.UTF32, false),
    UTF32_ESPERANTO_CI(177, "utf32_esperanto_ci", CharacterSet.UTF32, false),
    UTF32_HUNGARIAN_CI(178, "utf32_hungarian_ci", CharacterSet.UTF32, false),
    UTF32_SINHALA_CI(179, "utf32_sinhala_ci", CharacterSet.UTF32, false),
    UTF32_GERMAN2_CI(180, "utf32_german2_ci", CharacterSet.UTF32, false),
    UTF32_CROATIAN_CI(181, "utf32_croatian_ci", CharacterSet.UTF32, false),
    UTF32_UNICODE_520_CI(182, "utf32_unicode_520_ci", CharacterSet.UTF32, false),
    UTF32_VIETNAMESE_CI(183, "utf32_vietnamese_ci", CharacterSet.UTF32, false),
    UTF8_UNICODE_CI(192, "utf8_unicode_ci", CharacterSet.UTF8, false),
    UTF8_ICELANDIC_CI(193, "utf8_icelandic_ci", CharacterSet.UTF8, false),
    UTF8_LATVIAN_CI(194, "utf8_latvian_ci", CharacterSet.UTF8, false),
    UTF8_ROMANIAN_CI(195, "utf8_romanian_ci", CharacterSet.UTF8, false),
    UTF8_SLOVENIAN_CI(196, "utf8_slovenian_ci", CharacterSet.UTF8, false),
    UTF8_POLISH_CI(197, "utf8_polish_ci", CharacterSet.UTF8, false),
    UTF8_ESTONIAN_CI(198, "utf8_estonian_ci", CharacterSet.UTF8, false),
    UTF8_SPANISH_CI(199, "utf8_spanish_ci", CharacterSet.UTF8, false),
    UTF8_SWEDISH_CI(200, "utf8_swedish_ci", CharacterSet.UTF8, false),
    UTF8_TURKISH_CI(201, "utf8_turkish_ci", CharacterSet.UTF8, false),
    UTF8_CZECH_CI(202, "utf8_czech_ci", CharacterSet.UTF8, false),
    UTF8_DANISH_CI(203, "utf8_danish_ci", CharacterSet.UTF8, false),
    UTF8_LITHUANIAN_CI(204, "utf8_lithuanian_ci", CharacterSet.UTF8, false),
    UTF8_SLOVAK_CI(205, "utf8_slovak_ci", CharacterSet.UTF8, false),
    UTF8_SPANISH2_CI(206, "utf8_spanish2_ci", CharacterSet.UTF8, false),
    UTF8_ROMAN_CI(207, "utf8_roman_ci", CharacterSet.UTF8, false),
    UTF8_PERSIAN_CI(208, "utf8_persian_ci", CharacterSet.UTF8, false),
    UTF8_ESPERANTO_CI(209, "utf8_esperanto_ci", CharacterSet.UTF8, false),
    UTF8_HUNGARIAN_CI(210, "utf8_hungarian_ci", CharacterSet.UTF8, false),
    UTF8_SINHALA_CI(211, "utf8_sinhala_ci", CharacterSet.UTF8, false),
    UTF8_GERMAN2_CI(212, "utf8_german2_ci", CharacterSet.UTF8, false),
    UTF8_CROATIAN_CI(213, "utf8_croatian_ci", CharacterSet.UTF8, false),
    UTF8_UNICODE_520_CI(214, "utf8_unicode_520_ci", CharacterSet.UTF8, false),
    UTF8_VIETNAMESE_CI(215, "utf8_vietnamese_ci", CharacterSet.UTF8, false),
    UTF8_GENERAL_MYSQL500_CI(223, "utf8_general_mysql500_ci", CharacterSet.UTF8, false),
    UTF8MB4_UNICODE_CI(224, "utf8mb4_unicode_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_ICELANDIC_CI(225, "utf8mb4_icelandic_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_LATVIAN_CI(226, "utf8mb4_latvian_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_ROMANIAN_CI(227, "utf8mb4_romanian_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_SLOVENIAN_CI(228, "utf8mb4_slovenian_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_POLISH_CI(229, "utf8mb4_polish_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_ESTONIAN_CI(230, "utf8mb4_estonian_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_SPANISH_CI(231, "utf8mb4_spanish_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_SWEDISH_CI(232, "utf8mb4_swedish_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_TURKISH_CI(233, "utf8mb4_turkish_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_CZECH_CI(234, "utf8mb4_czech_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_DANISH_CI(235, "utf8mb4_danish_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_LITHUANIAN_CI(236, "utf8mb4_lithuanian_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_SLOVAK_CI(237, "utf8mb4_slovak_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_SPANISH2_CI(238, "utf8mb4_spanish2_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_ROMAN_CI(239, "utf8mb4_roman_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_PERSIAN_CI(240, "utf8mb4_persian_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_ESPERANTO_CI(241, "utf8mb4_esperanto_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_HUNGARIAN_CI(242, "utf8mb4_hungarian_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_SINHALA_CI(243, "utf8mb4_sinhala_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_GERMAN2_CI(244, "utf8mb4_german2_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_CROATIAN_CI(245, "utf8mb4_croatian_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_UNICODE_520_CI(246, "utf8mb4_unicode_520_ci", CharacterSet.UTF8MB4, false),
    UTF8MB4_VIETNAMESE_CI(247, "utf8mb4_vietnamese_ci", CharacterSet.UTF8MB4, false),
    GB18030_CHINESE_CI(248, "gb18030_chinese_ci", CharacterSet.GB18030, true),
    GB18030_BIN(249, "gb18030_bin", CharacterSet.GB18030, false),
    ;

    private final int id;
    private final String name;
    private final CharacterSet characterSet;
    private final boolean isDefault;

    Collation(int id, String name, CharacterSet characterSet, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.characterSet = characterSet;
        this.isDefault = isDefault;
    }

    public static Collation idOf(Integer id) {
        if (id == null) {
            return null;
        }

        for (Collation collation : values()) {
            if (collation.id == id) {
                return collation;
            }
        }
        return null;
    }

    public static Collation nameOf(String name) {
        if (name == null) {
            return null;
        }

        for (Collation collation : values()) {
            if (collation.name.equalsIgnoreCase(name)) {
                return collation;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CharacterSet getCharacterSet() {
        return characterSet;
    }

    public boolean isDefault() {
        return isDefault;
    }
}
