package ns.dao;

import ns.modele.Gite;
import ns.util.ConnexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GiteDAO {

    private final Connection conn;

    private static final String SELECT_BASE =
        "SELECT g.*, CONCAT(p.prenom,' ',p.nom) AS nom_proprio " +
        "FROM gite g LEFT JOIN proprio p ON g.idproprio=p.idproprio ";

    public GiteDAO() {
        this.conn = ConnexionDB.getInstance().getConnexion();
    }

    public List<Gite> findAll() throws SQLException {
        List<Gite> liste = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BASE + "ORDER BY g.adresse");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) liste.add(map(rs));
        }
        return liste;
    }

    public Gite findById(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BASE + "WHERE g.idgite=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<Gite> findByRecherche(String recherche) throws SQLException {
        List<Gite> liste = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BASE + "WHERE g.adresse LIKE ? ORDER BY g.adresse")) {
            ps.setString(1, "%" + recherche + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) liste.add(map(rs));
            }
        }
        return liste;
    }

    public boolean insert(Gite g) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO gite (adresse,surface,nbpieces,loyer,idproprio) VALUES (?,?,?,?,?)")) {
            ps.setString(1, g.getAdresse()); ps.setInt(2, g.getSurface());
            ps.setInt(3, g.getNbpieces());   ps.setInt(4, g.getLoyer());
            ps.setInt(5, g.getIdproprio());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Gite g) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE gite SET adresse=?,surface=?,nbpieces=?,loyer=?,idproprio=? WHERE idgite=?")) {
            ps.setString(1, g.getAdresse()); ps.setInt(2, g.getSurface());
            ps.setInt(3, g.getNbpieces());   ps.setInt(4, g.getLoyer());
            ps.setInt(5, g.getIdproprio());  ps.setInt(6, g.getIdgite());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM gite WHERE idgite=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int count() throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM gite");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private Gite map(ResultSet rs) throws SQLException {
        Gite g = new Gite(rs.getInt("idgite"), rs.getString("adresse"),
            rs.getInt("surface"), rs.getInt("nbpieces"),
            rs.getInt("loyer"), rs.getInt("idproprio"));
        try { g.setNomProprio(rs.getString("nom_proprio")); } catch (SQLException ignored) {}
        return g;
    }
}
