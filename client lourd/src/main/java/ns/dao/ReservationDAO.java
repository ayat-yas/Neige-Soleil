package ns.dao;

import ns.modele.Reservation;
import ns.util.ConnexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    private final Connection conn;

    private static final String SELECT_BASE =
        "SELECT r.*, CONCAT(c.prenom,' ',c.nom) AS nom_client, g.adresse AS adresse_gite " +
        "FROM reservation r " +
        "INNER JOIN client c ON r.idclient=c.idclient " +
        "INNER JOIN gite   g ON r.idgite=g.idgite ";

    public ReservationDAO() {
        this.conn = ConnexionDB.getInstance().getConnexion();
    }

    public List<Reservation> findAll() throws SQLException {
        List<Reservation> liste = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BASE + "ORDER BY r.datedebut DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) liste.add(map(rs));
        }
        return liste;
    }

    public List<Reservation> findByClient(int idclient) throws SQLException {
        List<Reservation> liste = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BASE + "WHERE r.idclient=? ORDER BY r.datedebut DESC")) {
            ps.setInt(1, idclient);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) liste.add(map(rs));
            }
        }
        return liste;
    }

    public List<Reservation> findByStatut(String statut) throws SQLException {
        List<Reservation> liste = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BASE + "WHERE r.statut_r=? ORDER BY r.datedebut DESC")) {
            ps.setString(1, statut);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) liste.add(map(rs));
            }
        }
        return liste;
    }

    public List<Reservation> findRecentes(int limit) throws SQLException {
        List<Reservation> liste = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BASE + "ORDER BY r.idreservation DESC LIMIT ?")) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) liste.add(map(rs));
            }
        }
        return liste;
    }

    public boolean insert(Reservation r) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO reservation (datedebut,datefin,prix,transport,assurance,idclient,idgite,statut_r,rapport) " +
                "VALUES (?,?,?,?,?,?,?,?,?)")) {
            ps.setDate(1, Date.valueOf(r.getDatedebut()));
            ps.setDate(2, Date.valueOf(r.getDatefin()));
            ps.setInt(3, r.getPrix());
            ps.setString(4, r.getTransport());
            ps.setBoolean(5, r.isAssurance());
            ps.setInt(6, r.getIdclient());
            ps.setInt(7, r.getIdgite());
            ps.setString(8, r.getStatut_r());
            ps.setString(9, r.getRapport());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Reservation r) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE reservation SET datedebut=?,datefin=?,prix=?,transport=?,assurance=?," +
                "idclient=?,idgite=?,statut_r=?,rapport=? WHERE idreservation=?")) {
            ps.setDate(1, Date.valueOf(r.getDatedebut()));
            ps.setDate(2, Date.valueOf(r.getDatefin()));
            ps.setInt(3, r.getPrix());
            ps.setString(4, r.getTransport());
            ps.setBoolean(5, r.isAssurance());
            ps.setInt(6, r.getIdclient());
            ps.setInt(7, r.getIdgite());
            ps.setString(8, r.getStatut_r());
            ps.setString(9, r.getRapport());
            ps.setInt(10, r.getIdreservation());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM reservation WHERE idreservation=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int count() throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM reservation");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int chiffreAffaires() throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COALESCE(SUM(prix),0) FROM reservation WHERE statut_r='terminé'");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private Reservation map(ResultSet rs) throws SQLException {
        Reservation r = new Reservation(
            rs.getInt("idreservation"),
            rs.getDate("datedebut").toLocalDate(),
            rs.getDate("datefin").toLocalDate(),
            rs.getInt("prix"),
            rs.getString("transport"),
            rs.getBoolean("assurance"),
            rs.getInt("idclient"),
            rs.getInt("idgite"),
            rs.getString("statut_r"),
            rs.getString("rapport")
        );
        r.setNomClient(rs.getString("nom_client"));
        r.setAdresseGite(rs.getString("adresse_gite"));
        return r;
    }
}
