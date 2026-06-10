package ns.util;

import java.awt.*;

public final class Style {

    private Style() {}

    public static final Color BG_DEEP      = new Color(0x080e1a);
    public static final Color BG_CARD      = new Color(0x0d1525);
    public static final Color BG_INPUT     = new Color(0x111d33);

    public static final Color C_PRIMARY    = new Color(0x1a56db);
    public static final Color C_ICE        = new Color(0x93c5fd);
    public static final Color C_ACCENT     = new Color(0xfbbf24);
    public static final Color C_SUCCESS    = new Color(0x4ade80);
    public static final Color C_DANGER     = new Color(0xf87171);
    public static final Color C_WARNING    = new Color(0xfbbf24);

    // Textes toujours clairs — jamais de noir sur fond sombre
    public static final Color C_TEXT       = new Color(0xf1f5f9);
    public static final Color C_TEXT_DIM   = new Color(0xcbd5e1);
    public static final Color C_MUTED      = new Color(0x94a3b8);

    public static final Color C_BORDER     = new Color(0x1e3a5f);
    public static final Color C_BORDER_HI  = new Color(0x2563eb);
    public static final Color C_WHITE      = Color.WHITE;

    // Tableau
    public static final Color C_TABLE_HEADER = new Color(0x0a1628); // très sombre
    public static final Color C_ROW_EVEN     = new Color(0x0d1525);
    public static final Color C_ROW_ODD      = new Color(0x111d33);
    public static final Color C_SELECT       = new Color(0x1e3a6e);

    // Onglets
    public static final Color C_TAB_BG          = new Color(0x0d1525); // fond onglet inactif
    public static final Color C_TAB_BG_SELECTED = new Color(0x1a3a6e); // fond onglet actif
    public static final Color C_TAB_TEXT         = new Color(0xf1f5f9); // texte onglet toujours blanc

    public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD,  20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FONT_NORMAL   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BOLD     = new Font("Segoe UI", Font.BOLD,  13);

    public static final int ROW_HEIGHT = 28;
}
