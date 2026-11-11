package View;

import java.awt.Color;

/**
 * Palette de couleurs centralisée pour toute l'application.
 * Change une couleur ici et elle change partout.
 */
public class UITheme {
    public static final Color BG_MAIN        = new Color(18, 18, 24);
    public static final Color BG_SIDEBAR      = new Color(12, 12, 16);
    public static final Color BG_CARD         = new Color(28, 28, 36);
    public static final Color BG_CARD_HOVER   = new Color(255, 255, 255, 18);

    public static final Color ACCENT          = new Color(224, 168, 58);  // doré, assorti au logo
    public static final Color ACCENT_TEXT     = new Color(20, 20, 24);    // texte sombre sur fond doré

    public static final Color TEXT_PRIMARY    = new Color(235, 235, 240);
    public static final Color TEXT_SECONDARY  = new Color(160, 160, 172);

    public static final int RADIUS = 16;
}