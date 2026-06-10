package ns;

import ns.controleur.AppControleur;
import ns.util.ConnexionDB;
import ns.util.Style;
import ns.vue.VueConnexion;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Forcer les couleurs dark globalement AVANT tout composant
        UIManager.put("Panel.background",              Style.BG_DEEP);
        UIManager.put("OptionPane.background",         Style.BG_CARD);
        UIManager.put("OptionPane.messageForeground",  Style.C_TEXT);
        UIManager.put("Table.background",              Style.BG_CARD);
        UIManager.put("Table.foreground",              Style.C_TEXT);
        UIManager.put("Table.selectionBackground",     Style.C_SELECT);
        UIManager.put("Table.selectionForeground",     Style.C_TEXT);
        UIManager.put("TableHeader.background",        Style.C_TABLE_HEADER);
        UIManager.put("TableHeader.foreground",        Style.C_TEXT);
        UIManager.put("TabbedPane.background",         Style.BG_DEEP);
        UIManager.put("TabbedPane.foreground",         Style.C_TEXT);
        UIManager.put("TabbedPane.selected",           new Color(0x0d1525));
        UIManager.put("TabbedPane.contentAreaColor",   new Color(0x0d1525));
        UIManager.put("TabbedPane.tabAreaBackground",  Style.BG_DEEP);
        UIManager.put("ScrollPane.background",         Style.BG_CARD);
        UIManager.put("Viewport.background",           Style.BG_CARD);
        UIManager.put("SplitPane.background",          Style.BG_DEEP);
        UIManager.put("SplitPaneDivider.background",   Style.C_BORDER);
        UIManager.put("Label.foreground",              Style.C_TEXT);
        UIManager.put("TextField.background",          Style.BG_INPUT);
        UIManager.put("TextField.foreground",          Style.C_TEXT);
        UIManager.put("TextField.caretForeground",     Style.C_ICE);
        UIManager.put("PasswordField.background",      Style.BG_INPUT);
        UIManager.put("PasswordField.foreground",      Style.C_TEXT);
        UIManager.put("ComboBox.background",           Style.BG_INPUT);
        UIManager.put("ComboBox.foreground",           Style.C_TEXT);
        UIManager.put("Button.background",             new Color(0x1a3a6e));
        UIManager.put("Button.foreground",             Style.C_TEXT);

        SwingUtilities.invokeLater(() -> {
            try {
                ConnexionDB.getInstance();
                AppControleur ctrl = new AppControleur();
                new VueConnexion(ctrl).setVisible(true);
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(null,
                    "Impossible de se connecter à la base de données.\n\n" +
                    "Vérifiez que :\n" +
                    "  • WAMP/XAMPP est démarré\n" +
                    "  • La base 'n&s_31_jv' existe\n" +
                    "  • mysql-connector-j.jar est dans lib/\n\n" +
                    "Erreur : " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()),
                    "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
