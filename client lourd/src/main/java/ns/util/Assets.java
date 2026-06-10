package ns.util;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

// Chargement des icônes et du logo depuis les ressources embarquées
public final class Assets {

    private Assets() {}

    // Charge une icône à la taille la plus proche disponible (32, 48, 64, 128)
    public static ImageIcon icon(String name, int targetSize) {
        int[] available = {32, 48, 64, 128};
        int best = 128;
        for (int s : available) {
            if (s >= targetSize) { best = s; break; }
        }
        URL url = Assets.class.getResource("/icons/" + name + "_" + best + ".png");
        if (url == null) return null;
        if (best == targetSize) return new ImageIcon(url);
        Image img = new ImageIcon(url).getImage()
                        .getScaledInstance(targetSize, targetSize, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    // Charge le logo à la largeur demandée
    public static ImageIcon logo(int targetWidth) {
        // Fichiers disponibles : logo_60, logo_120, logo_200, logo_400
        int[] sizes = {60, 120, 200, 400};
        int best = 400;
        for (int s : sizes) { if (s >= targetWidth) { best = s; break; } }
        URL url = Assets.class.getResource("/logo/logo_" + best + ".png");
        if (url == null) return null;
        ImageIcon raw = new ImageIcon(url);
        int rawW = raw.getIconWidth();
        int rawH = raw.getIconHeight();
        if (rawW <= 0) return raw;
        int targetH = (int)(targetWidth * (double) rawH / rawW);
        Image img = raw.getImage().getScaledInstance(targetWidth, targetH, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public static final String CLIENT      = "client";
    public static final String GITE        = "gite";
    public static final String PROPRIO     = "proprio";
    public static final String RESERVATION = "reservation";
    public static final String DASHBOARD   = "dashboard";
}
