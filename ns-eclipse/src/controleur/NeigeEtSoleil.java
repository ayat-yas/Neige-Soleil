package controleur;

import vue.VueConnexion;
import vue.VuePrincipale;
import modele.Proprietaire;

/**
 * Point d'entree — Client Lourd Java/Swing — BTS SIO SLAM 2026.
 * Le setVisible est appele dans le constructeur de chaque vue.
 */
public class NeigeEtSoleil {

    private static VueConnexion  uneVueConnexion;
    private static VuePrincipale uneVuePrincipale;
    private static Proprietaire  adminConnecte;

    public static void main(String[] args) {
        // La vue appelle setVisible(true) dans son constructeur
        uneVueConnexion = new VueConnexion();
    }

    public static void rendreVisibleVueConnexion(boolean visible) {
        if (uneVueConnexion != null) {
            uneVueConnexion.setVisible(visible);
        }
    }

    public static void ouvrirPrincipale() {
        if (uneVuePrincipale != null) {
            uneVuePrincipale.dispose();
        }
        uneVuePrincipale = new VuePrincipale();
    }

    public static Proprietaire getAdminConnecte() { return adminConnecte; }
    public static void setAdminConnecte(Proprietaire p) { adminConnecte = p; }
}
