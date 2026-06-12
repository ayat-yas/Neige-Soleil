package controleur;

import java.util.ArrayList;
import modele.Client;
import modele.Gite;
import modele.Proprietaire;
import modele.Reservation;
import modele.modele;

/**
 * Controleur principal — fait le lien entre les vues et le modele.
 * Architecture MVC cote client lourd Java/Swing.
 */
public class Controleur {

    // =========================================================
    //  AUTHENTIFICATION
    // =========================================================

    public static Proprietaire connecterAdmin(String email, String mdp) {
        if (email == null || email.trim().isEmpty()) return null;
        if (mdp   == null || mdp.trim().isEmpty())   return null;
        return modele.connecterAdmin(email.trim(), mdp.trim());
    }

    // =========================================================
    //  PROPRIETAIRES
    // =========================================================

    public static ArrayList<Proprietaire> getProprietaires(String filtre) {
        return modele.selectAllProprietaires(filtre);
    }

    public static Proprietaire getProprietaireById(int id) {
        return modele.selectProprietaireById(id);
    }

    public static void ajouterProprietaire(Proprietaire p) {
        if (p.getNom().isEmpty() || p.getPrenom().isEmpty() || p.getEmail().isEmpty()) return;
        modele.insertProprietaire(p);
    }

    public static void modifierProprietaire(Proprietaire p, boolean changerMdp) {
        if (p.getNom().isEmpty() || p.getPrenom().isEmpty() || p.getEmail().isEmpty()) return;
        modele.updateProprietaire(p, changerMdp);
    }

    public static void supprimerProprietaire(int id) {
        modele.deleteProprietaire(id);
    }

    // =========================================================
    //  CLIENTS
    // =========================================================

    public static ArrayList<Client> getClients(String filtre) {
        return modele.selectAllClients(filtre);
    }

    public static Client getClientById(int id) {
        return modele.selectClientById(id);
    }

    public static void ajouterClient(Client c) {
        if (c.getNom().isEmpty() || c.getPrenom().isEmpty() || c.getEmail().isEmpty()) return;
        modele.insertClient(c);
    }

    public static void modifierClient(Client c, boolean changerMdp) {
        if (c.getNom().isEmpty() || c.getPrenom().isEmpty() || c.getEmail().isEmpty()) return;
        modele.updateClient(c, changerMdp);
    }

    public static void supprimerClient(int id) {
        modele.deleteClient(id);
    }

    // =========================================================
    //  GITES
    // =========================================================

    public static ArrayList<Gite> getGites(String filtre) {
        return modele.selectAllGites(filtre);
    }

    public static Gite getGiteById(int id) {
        return modele.selectGiteById(id);
    }

    public static void ajouterGite(Gite g) {
        if (g.getAdresse().isEmpty()) return;
        modele.insertGite(g);
    }

    public static void modifierGite(Gite g) {
        if (g.getAdresse().isEmpty()) return;
        modele.updateGite(g);
    }

    public static void supprimerGite(int id) {
        modele.deleteGite(id);
    }

    // =========================================================
    //  RESERVATIONS
    // =========================================================

    public static ArrayList<Reservation> getReservations(String filtre) {
        return modele.selectAllReservations(filtre);
    }

    public static Reservation getReservationById(int id) {
        return modele.selectReservationById(id);
    }

    public static void ajouterReservation(Reservation r) {
        modele.insertReservation(r);
    }

    public static void modifierReservation(Reservation r) {
        modele.updateReservation(r);
    }

    public static void supprimerReservation(int id) {
        modele.deleteReservation(id);
    }

    // =========================================================
    //  STATISTIQUES
    // =========================================================

    public static int compter(String table) {
        return modele.compterTable(table);
    }

    public static int getCA() {
        return modele.getChiffreAffaires();
    }

    public static ArrayList<Reservation> getRecentes(int nb) {
        return modele.selectReservationsRecentes(nb);
    }
}
