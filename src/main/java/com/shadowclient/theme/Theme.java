package com.shadowclient.theme;

/** Immutable colour palette (ARGB). */
public record Theme(String name, int background, int panel, int header, int accent, int accent2,
                    int text, int dim, int outline) {

    public static final Theme DARK = new Theme("Dark",
            0xCC0B0B0F, 0xF2121216, 0xFF1A1A21, 0xFF9FB4FF, 0xFF4A5A8F, 0xFFEDEDF2, 0xFF8A8A96, 0xFF2A2A33);

    public static final Theme RED = new Theme("Red",
            0xCC0F0809, 0xF2160C0E, 0xFF241014, 0xFFFF4D5A, 0xFF9E1B2B, 0xFFEDEDF2, 0xFF9A8A8C, 0xFF3A1A1F);

    public static final Theme PURPLE = new Theme("Purple",
            0xCC0C0910, 0xF2120E18, 0xFF1E1528, 0xFFB77CFF, 0xFF5E2CA5, 0xFFEDEDF2, 0xFF918AA0, 0xFF2E2140);
}
