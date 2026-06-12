package modele;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Modele unique — contient TOUTES les requetes SQL de l'application.
 * Utilise des PreparedStatement pour securiser contre les injections SQL.
 */
public class modele {

    // Connexion unique a la base n&s_31_jv
    private static bdd uneBdd = new bdd("localhost", "n%26s_31_jv", "root", "");

    // =========================================================
    //  AUTHENTIFICATION
    // =========================================================

    public static Proprietaire connecterAdmin(String email, String mdp) {
        Proprietaire admin = null;
        try {
            uneBdd.seConnecter();
            String sql = "SELECT * FROM proprio WHERE email = ? LIMIT 1";
            PreparedStatement ps = uneBdd.getMaConnexion().prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String mdpBDD = rs.getString("mdp");
                // Verification mot de passe en clair (donnees de test)
                if (mdp.equals(mdpBDD)) {
                    admin = new Proprietaire(
                        rs.getInt("idproprio"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("adresse"),
                        rs.getString("email"),
                        rs.getString("mdp"),
                        String.valueOf(rs.getLong("telephone")),
                        rs.getString("statut")
                    );
                }
            }
            rs.close();
            ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur connexion : " + e.getMessage());
        }
        return admin;
    }

    // =========================================================
    //  PROPRIETAIRES
    // =========================================================

    public static ArrayList<Proprietaire> selectAllProprietaires(String filtre) {
        ArrayList<Proprietaire> liste = new ArrayList<Proprietaire>();
        try {
            uneBdd.seConnecter();
            String sql;
            PreparedStatement ps;
            if (filtre == null || filtre.equals("")) {
                sql = "SELECT * FROM proprio ORDER BY nom, prenom";
                ps  = uneBdd.getMaConnexion().prepareStatement(sql);
            } else {
                sql = "SELECT * FROM proprio WHERE nom LIKE ? OR prenom LIKE ? OR email LIKE ? ORDER BY nom";
                ps  = uneBdd.getMaConnexion().prepareStatement(sql);
                String p = "%" + filtre + "%";
                ps.setString(1, p);
                ps.setString(2, p);
                ps.setString(3, p);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(new Proprietaire(
                    rs.getInt("idproprio"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("adresse"),
                    rs.getString("email"),
                    rs.getString("mdp"),
                    String.valueOf(rs.getLong("telephone")),
                    rs.getString("statut")
                ));
            }
            rs.close(); ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur selectAllProprietaires : " + e.getMessage());
        }
        return liste;
    }

    public static Proprietaire selectProprietaireById(int id) {
        Proprietaire p = null;
        try {
            uneBdd.seConnecter();
            PreparedStatement ps = uneBdd.getMaConnexion().prepareStatement(
                "SELECT * FROM proprio WHERE idproprio = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                p = new Proprietaire(
                    rs.getInt("idproprio"), rs.getString("nom"), rs.getString("prenom"),
                    rs.getString("adresse"), rs.getString("email"), rs.getString("mdp"),
                    String.valueOf(rs.getLong("telephone")), rs.getString("statut")
                );
            }
            rs.close(); ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur selectProprietaireById : " + e.getMessage());
        }
        return p;
    }

    public static void insertProprietaire(Proprietaire p) {
        try {
            uneBdd.seConnecter();
            PreparedStatement ps = uneBdd.getMaConnexion().prepareStatement(
                "INSERT INTO proprio (nom, prenom, adresse, email, mdp, telephone, statut) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setString(3, p.getAdresse());
            ps.setString(4, p.getEmail());
            ps.setString(5, p.getMdp());
            ps.setLong(6, Long.parseLong(p.getTelephone()));
            ps.setString(7, p.getStatut());
            ps.executeUpdate();
            ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur insertProprietaire : " + e.getMessage());
        }
    }

    public static void updateProprietaire(Proprietaire p, boolean changerMdp) {
        try {
            uneBdd.seConnecter();
            String sql = changerMdp
                ? "UPDATE proprio SET nom=?, prenom=?, adresse=?, email=?, telephone=?, statut=?, mdp=? WHERE idproprio=?"
                : "UPDATE proprio SET nom=?, prenom=?, adresse=?, email=?, telephone=?, statut=? WHERE idproprio=?";
            PreparedStatement ps = uneBdd.getMaConnexion().prepareStatement(sql);
            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setString(3, p.getAdresse());
            ps.setString(4, p.getEmail());
            ps.setLong(5, Long.parseLong(p.getTelephone()));
            ps.setString(6, p.getStatut());
            if (changerMdp) {
                ps.setString(7, p.getMdp());
                ps.setInt(8, p.getIdproprio());
            } else {
                ps.setInt(7, p.getIdproprio());
            }
            ps.executeUpdate();
            ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur updateProprietaire : " + e.getMessage());
        }
    }

    public static void deleteProprietaire(int id) {
        try {
            uneBdd.seConnecter();
            PreparedStatement ps = uneBdd.getMaConnexion().prepareStatement(
                "DELETE FROM proprio WHERE idproprio = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur deleteProprietaire : " + e.getMessage());
        }
    }

    // =========================================================
    //  CLIENTS
    // =========================================================

    public static ArrayList<Client> selectAllClients(String filtre) {
        ArrayList<Client> liste = new ArrayList<Client>();
        try {
            uneBdd.seConnecter();
            String sql;
            PreparedStatement ps;
            if (filtre == null || filtre.equals("")) {
                sql = "SELECT * FROM client ORDER BY nom, prenom";
                ps  = uneBdd.getMaConnexion().prepareStatement(sql);
            } else {
                sql = "SELECT * FROM client WHERE nom LIKE ? OR prenom LIKE ? OR email LIKE ? ORDER BY nom";
                ps  = uneBdd.getMaConnexion().prepareStatement(sql);
                String pat = "%" + filtre + "%";
                ps.setString(1, pat); ps.setString(2, pat); ps.setString(3, pat);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(new Client(
                    rs.getInt("idclient"), rs.getString("nom"), rs.getString("prenom"),
                    rs.getString("adresse"), rs.getString("email"), rs.getString("mdp"),
                    String.valueOf(rs.getLong("telephone"))
                ));
            }
            rs.close(); ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur selectAllClients : " + e.getMessage());
        }
        return liste;
    }

    public static Client selectClientById(int id) {
        Client c = null;
        try {
            uneBdd.seConnecter();
            PreparedStatement ps = uneBdd.getMaConnexion().prepareStatement(
                "SELECT * FROM client WHERE idclient = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                c = new Client(
                    rs.getInt("idclient"), rs.getString("nom"), rs.getString("prenom"),
                    rs.getString("adresse"), rs.getString("email"), rs.getString("mdp"),
                    String.valueOf(rs.getLong("telephone"))
                );
            }
            rs.close(); ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur selectClientById : " + e.getMessage());
        }
        return c;
    }

    public static void insertClient(Client c) {
        try {
            uneBdd.seConnecter();
            PreparedStatement ps = uneBdd.getMaConnexion().prepareStatement(
                "INSERT INTO client (nom, prenom, adresse, email, mdp, telephone) VALUES (?,?,?,?,?,?)");
            ps.setString(1, c.getNom()); ps.setString(2, c.getPrenom());
            ps.setString(3, c.getAdresse()); ps.setString(4, c.getEmail());
            ps.setString(5, c.getMdp()); ps.setLong(6, Long.parseLong(c.getTelephone()));
            ps.executeUpdate(); ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur insertClient : " + e.getMessage());
        }
    }

    public static void updateClient(Client c, boolean changerMdp) {
        try {
            uneBdd.seConnecter();
            String sql = changerMdp
                ? "UPDATE client SET nom=?,prenom=?,adresse=?,email=?,telephone=?,mdp=? WHERE idclient=?"
                : "UPDATE client SET nom=?,prenom=?,adresse=?,email=?,telephone=? WHERE idclient=?";
            PreparedStatement ps = uneBdd.getMaConnexion().prepareStatement(sql);
            ps.setString(1, c.getNom()); ps.setString(2, c.getPrenom());
            ps.setString(3, c.getAdresse()); ps.setString(4, c.getEmail());
            ps.setLong(5, Long.parseLong(c.getTelephone()));
            if (changerMdp) { ps.setString(6, c.getMdp()); ps.setInt(7, c.getIdclient()); }
            else             { ps.setInt(6, c.getIdclient()); }
            ps.executeUpdate(); ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur updateClient : " + e.getMessage());
        }
    }

    public static void deleteClient(int id) {
        try {
            uneBdd.seConnecter();
            PreparedStatement ps = uneBdd.getMaConnexion().prepareStatement(
                "DELETE FROM client WHERE idclient = ?");
            ps.setInt(1, id); ps.executeUpdate(); ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur deleteClient : " + e.getMessage());
        }
    }

    // =========================================================
    //  GITES
    // =========================================================

    public static ArrayList<Gite> selectAllGites(String filtre) {
        ArrayList<Gite> liste = new ArrayList<Gite>();
        try {
            uneBdd.seConnecter();
            String sql;
            PreparedStatement ps;
            String base = "SELECT g.*, CONCAT(p.prenom,' ',p.nom) AS nom_proprio "
                        + "FROM gite g LEFT JOIN proprio p ON g.idproprio = p.idproprio ";
            if (filtre == null || filtre.equals("")) {
                sql = base + "ORDER BY g.adresse";
                ps  = uneBdd.getMaConnexion().prepareStatement(sql);
            } else {
                sql = base + "WHERE g.adresse LIKE ? ORDER BY g.adresse";
                ps  = uneBdd.getMaConnexion().prepareStatement(sql);
                ps.setString(1, "%" + filtre + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Gite g = new Gite(
                    rs.getInt("idgite"), rs.getString("adresse"),
                    rs.getInt("surface"), rs.getInt("nbpieces"),
                    rs.getInt("loyer"), rs.getInt("idproprio")
                );
                g.setNomProprio(rs.getString("nom_proprio"));
                liste.add(g);
            }
            rs.close(); ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur selectAllGites : " + e.getMessage());
        }
        return liste;
    }

    public static Gite selectGiteById(int id) {
        Gite g = null;
        try {
            uneBdd.seConnecter();
            PreparedStatement ps = uneBdd.getMaConnexion().prepareStatement(
                "SELECT g.*, CONCAT(p.prenom,' ',p.nom) AS nom_proprio " +
                "FROM gite g LEFT JOIN proprio p ON g.idproprio = p.idproprio " +
                "WHERE g.idgite = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                g = new Gite(rs.getInt("idgite"), rs.getString("adresse"),
                    rs.getInt("surface"), rs.getInt("nbpieces"),
                    rs.getInt("loyer"), rs.getInt("idproprio"));
                g.setNomProprio(rs.getString("nom_proprio"));
            }
            rs.close(); ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur selectGiteById : " + e.getMessage());
        }
        return g;
    }

    public static void insertGite(Gite g) {
        try {
            uneBdd.seConnecter();
            PreparedStatement ps = uneBdd.getMaConnexion().prepareStatement(
                "INSERT INTO gite (adresse,surface,nbpieces,loyer,idproprio) VALUES (?,?,?,?,?)");
            ps.setString(1, g.getAdresse()); ps.setInt(2, g.getSurface());
            ps.setInt(3, g.getNbpieces());   ps.setInt(4, g.getLoyer());
            ps.setInt(5, g.getIdproprio());
            ps.executeUpdate(); ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur insertGite : " + e.getMessage());
        }
    }

    public static void updateGite(Gite g) {
        try {
            uneBdd.seConnecter();
            PreparedStatement ps = uneBdd.getMaConnexion().prepareStatement(
                "UPDATE gite SET adresse=?,surface=?,nbpieces=?,loyer=?,idproprio=? WHERE idgite=?");
            ps.setString(1, g.getAdresse()); ps.setInt(2, g.getSurface());
            ps.setInt(3, g.getNbpieces());   ps.setInt(4, g.getLoyer());
            ps.setInt(5, g.getIdproprio());  ps.setInt(6, g.getIdgite());
            ps.executeUpdate(); ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur updateGite : " + e.getMessage());
        }
    }

    public static void deleteGite(int id) {
        try {
            uneBdd.seConnecter();
            PreparedStatement ps = uneBdd.getMaConnexion().prepareStatement(
                "DELETE FROM gite WHERE idgite = ?");
            ps.setInt(1, id); ps.executeUpdate(); ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur deleteGite : " + e.getMessage());
        }
    }

    // =========================================================
    //  RESERVATIONS
    // =========================================================

    private static final String SELECT_RESA =
        "SELECT r.*, CONCAT(c.prenom,' ',c.nom) AS nom_client, g.adresse AS adresse_gite " +
        "FROM reservation r " +
        "INNER JOIN client c ON r.idclient = c.idclient " +
        "INNER JOIN gite   g ON r.idgite   = g.idgite ";

    public static ArrayList<Reservation> selectAllReservations(String filtre) {
        ArrayList<Reservation> liste = new ArrayList<Reservation>();
        try {
            uneBdd.seConnecter();
            String sql;
            PreparedStatement ps;
            if (filtre == null || filtre.equals("")) {
                sql = SELECT_RESA + "ORDER BY r.datedebut DESC";
                ps  = uneBdd.getMaConnexion().prepareStatement(sql);
            } else {
                sql = SELECT_RESA + "WHERE r.statut_r LIKE ? ORDER BY r.datedebut DESC";
                ps  = uneBdd.getMaConnexion().prepareStatement(sql);
                ps.setString(1, "%" + filtre + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reservation res = new Reservation(
                    rs.getInt("idreservation"),
                    rs.getString("datedebut"),
                    rs.getString("datefin"),
                    rs.getInt("prix"),
                    rs.getString("transport"),
                    rs.getBoolean("assurance"),
                    rs.getInt("idclient"),
                    rs.getInt("idgite"),
                    rs.getString("statut_r"),
                    rs.getString("rapport")
                );
                res.setNomClient(rs.getString("nom_client"));
                res.setAdresseGite(rs.getString("adresse_gite"));
                liste.add(res);
            }
            rs.close(); ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur selectAllReservations : " + e.getMessage());
        }
        return liste;
    }

    public static Reservation selectReservationById(int id) {
        Reservation res = null;
        try {
            uneBdd.seConnecter();
            PreparedStatement ps = uneBdd.getMaConnexion().prepareStatement(
                SELECT_RESA + "WHERE r.idreservation = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                res = new Reservation(
                    rs.getInt("idreservation"), rs.getString("datedebut"), rs.getString("datefin"),
                    rs.getInt("prix"), rs.getString("transport"), rs.getBoolean("assurance"),
                    rs.getInt("idclient"), rs.getInt("idgite"),
                    rs.getString("statut_r"), rs.getString("rapport")
                );
                res.setNomClient(rs.getString("nom_client"));
                res.setAdresseGite(rs.getString("adresse_gite"));
            }
            rs.close(); ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur selectReservationById : " + e.getMessage());
        }
        return res;
    }

    public static void insertReservation(Reservation r) {
        try {
            uneBdd.seConnecter();
            PreparedStatement ps = uneBdd.getMaConnexion().prepareStatement(
                "INSERT INTO reservation (datedebut,datefin,prix,transport,assurance,idclient,idgite,statut_r,rapport) " +
                "VALUES (?,?,?,?,?,?,?,?,?)");
            ps.setString(1, r.getDatedebut()); ps.setString(2, r.getDatefin());
            ps.setInt(3, r.getPrix());         ps.setString(4, r.getTransport());
            ps.setBoolean(5, r.isAssurance()); ps.setInt(6, r.getIdclient());
            ps.setInt(7, r.getIdgite());       ps.setString(8, r.getStatut_r());
            ps.setString(9, r.getRapport());
            ps.executeUpdate(); ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur insertReservation : " + e.getMessage());
        }
    }

    public static void updateReservation(Reservation r) {
        try {
            uneBdd.seConnecter();
            PreparedStatement ps = uneBdd.getMaConnexion().prepareStatement(
                "UPDATE reservation SET datedebut=?,datefin=?,prix=?,transport=?,assurance=?," +
                "idclient=?,idgite=?,statut_r=?,rapport=? WHERE idreservation=?");
            ps.setString(1, r.getDatedebut()); ps.setString(2, r.getDatefin());
            ps.setInt(3, r.getPrix());         ps.setString(4, r.getTransport());
            ps.setBoolean(5, r.isAssurance()); ps.setInt(6, r.getIdclient());
            ps.setInt(7, r.getIdgite());       ps.setString(8, r.getStatut_r());
            ps.setString(9, r.getRapport());   ps.setInt(10, r.getIdreservation());
            ps.executeUpdate(); ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur updateReservation : " + e.getMessage());
        }
    }

    public static void deleteReservation(int id) {
        try {
            uneBdd.seConnecter();
            PreparedStatement ps = uneBdd.getMaConnexion().prepareStatement(
                "DELETE FROM reservation WHERE idreservation = ?");
            ps.setInt(1, id); ps.executeUpdate(); ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur deleteReservation : " + e.getMessage());
        }
    }

    // =========================================================
    //  STATISTIQUES
    // =========================================================

    public static int compterTable(String table) {
        int nb = 0;
        try {
            uneBdd.seConnecter();
            PreparedStatement ps = uneBdd.getMaConnexion().prepareStatement(
                "SELECT COUNT(*) FROM " + table);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) nb = rs.getInt(1);
            rs.close(); ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur compterTable : " + e.getMessage());
        }
        return nb;
    }

    public static int getChiffreAffaires() {
        int ca = 0;
        try {
            uneBdd.seConnecter();
            PreparedStatement ps = uneBdd.getMaConnexion().prepareStatement(
                "SELECT COALESCE(SUM(prix),0) FROM reservation WHERE statut_r = 'termine'");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) ca = rs.getInt(1);
            rs.close(); ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur getChiffreAffaires : " + e.getMessage());
        }
        return ca;
    }

    public static ArrayList<Reservation> selectReservationsRecentes(int limit) {
        ArrayList<Reservation> liste = new ArrayList<Reservation>();
        try {
            uneBdd.seConnecter();
            PreparedStatement ps = uneBdd.getMaConnexion().prepareStatement(
                SELECT_RESA + "ORDER BY r.idreservation DESC LIMIT ?");
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reservation r = new Reservation(
                    rs.getInt("idreservation"), rs.getString("datedebut"), rs.getString("datefin"),
                    rs.getInt("prix"), rs.getString("transport"), rs.getBoolean("assurance"),
                    rs.getInt("idclient"), rs.getInt("idgite"),
                    rs.getString("statut_r"), rs.getString("rapport")
                );
                r.setNomClient(rs.getString("nom_client"));
                r.setAdresseGite(rs.getString("adresse_gite"));
                liste.add(r);
            }
            rs.close(); ps.close();
            uneBdd.seDeconnecter();
        } catch (SQLException e) {
            System.out.println("Erreur selectReservationsRecentes : " + e.getMessage());
        }
        return liste;
    }
}
