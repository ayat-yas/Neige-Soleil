package ns.controleur;

import ns.dao.*;
import ns.modele.*;

import java.sql.SQLException;
import java.util.List;

// Contrôleur MVC — relie les vues aux DAO, gère la session admin
public class AppControleur {

    private final ProprietaireDAO proprietaireDAO;
    private final ClientDAO       clientDAO;
    private final GiteDAO         giteDAO;
    private final ReservationDAO  reservationDAO;

    private Proprietaire adminConnecte;

    public AppControleur() {
        this.proprietaireDAO = new ProprietaireDAO();
        this.clientDAO       = new ClientDAO();
        this.giteDAO         = new GiteDAO();
        this.reservationDAO  = new ReservationDAO();
    }

    // ── Authentification ─────────────────────────────────────────────────────

    // Supporte les mots de passe en clair (données de test) et hashés (PHP password_hash)
    public boolean connecter(String email, String mdp) {
        try {
            Proprietaire p = proprietaireDAO.findByEmail(email);
            if (p == null) return false;
            if (mdp.equals(p.getMdp())) {
                adminConnecte = p;
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur d'authentification", e);
        }
    }

    public void deconnecter() { adminConnecte = null; }
    public Proprietaire getAdminConnecte() { return adminConnecte; }

    // ── Propriétaires ────────────────────────────────────────────────────────

    public List<Proprietaire> getProprietaires()                     throws SQLException { return proprietaireDAO.findAll(); }
    public List<Proprietaire> rechercherProprietaires(String q)      throws SQLException { return proprietaireDAO.findByRecherche(q); }
    public boolean ajouterProprietaire(Proprietaire p)               throws SQLException { return proprietaireDAO.insert(p); }
    public boolean modifierProprietaire(Proprietaire p)              throws SQLException { return proprietaireDAO.update(p); }
    public boolean supprimerProprietaire(int id)                     throws SQLException { return proprietaireDAO.delete(id); }
    public int compterProprietaires()                                throws SQLException { return proprietaireDAO.count(); }

    // ── Clients ──────────────────────────────────────────────────────────────

    public List<Client> getClients()                   throws SQLException { return clientDAO.findAll(); }
    public List<Client> rechercherClients(String q)    throws SQLException { return clientDAO.findByRecherche(q); }
    public boolean ajouterClient(Client c)             throws SQLException { return clientDAO.insert(c); }
    public boolean modifierClient(Client c)            throws SQLException { return clientDAO.update(c); }
    public boolean supprimerClient(int id)             throws SQLException { return clientDAO.delete(id); }
    public int compterClients()                        throws SQLException { return clientDAO.count(); }

    // ── Gites ────────────────────────────────────────────────────────────────

    public List<Gite> getGites()                  throws SQLException { return giteDAO.findAll(); }
    public List<Gite> rechercherGites(String q)   throws SQLException { return giteDAO.findByRecherche(q); }
    public boolean ajouterGite(Gite g)            throws SQLException { return giteDAO.insert(g); }
    public boolean modifierGite(Gite g)           throws SQLException { return giteDAO.update(g); }
    public boolean supprimerGite(int id)          throws SQLException { return giteDAO.delete(id); }
    public int compterGites()                     throws SQLException { return giteDAO.count(); }

    // ── Réservations ─────────────────────────────────────────────────────────

    public List<Reservation> getReservations()                   throws SQLException { return reservationDAO.findAll(); }
    public List<Reservation> getReservationsByClient(int id)     throws SQLException { return reservationDAO.findByClient(id); }
    public List<Reservation> getReservationsByStatut(String s)   throws SQLException { return reservationDAO.findByStatut(s); }
    public List<Reservation> getReservationsRecentes(int limit)  throws SQLException { return reservationDAO.findRecentes(limit); }
    public boolean ajouterReservation(Reservation r)             throws SQLException { return reservationDAO.insert(r); }
    public boolean modifierReservation(Reservation r)            throws SQLException { return reservationDAO.update(r); }
    public boolean supprimerReservation(int id)                  throws SQLException { return reservationDAO.delete(id); }
    public int compterReservations()                             throws SQLException { return reservationDAO.count(); }
    public int getChiffreAffaires()                              throws SQLException { return reservationDAO.chiffreAffaires(); }
}
