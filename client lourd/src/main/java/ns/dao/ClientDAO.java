package ns.dao;

import ns.modele.Client;
import ns.util.ConnexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Accès aux données de la table `client` — idcategorie nullable (FK vers categorie)
public class ClientDAO {

    private final Connection conn;

    public ClientDAO() {
        this.conn = ConnexionDB.getInstance().getConnexion();
    }

    public List<Client> findAll() throws SQLException {
        List<Client> liste = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM client ORDER BY nom, prenom");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) liste.add(map(rs));
        }
        return liste;
    }

    public Client findById(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM client WHERE idclient=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<Client> findByRecherche(String recherche) throws SQLException {
        List<Client> liste = new ArrayList<>();
        String p = "%" + recherche + "%";
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM client WHERE nom LIKE ? OR prenom LIKE ? OR email LIKE ? ORDER BY nom")) {
            ps.setString(1, p); ps.setString(2, p); ps.setString(3, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) liste.add(map(rs));
            }
        }
        return liste;
    }

    public boolean insert(Client c) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO client (nom,prenom,adresse,email,mdp,telephone,idcategorie) VALUES (?,?,?,?,?,?,?)")) {
            ps.setString(1, c.getNom());   ps.setString(2, c.getPrenom());
            ps.setString(3, c.getAdresse()); ps.setString(4, c.getEmail());
            ps.setString(5, c.getMdp());   ps.setLong(6, c.getTelephone());
            if (c.getIdcategorie() != null) ps.setInt(7, c.getIdcategorie());
            else ps.setNull(7, Types.INTEGER);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Client c) throws SQLException {
        boolean withMdp = c.getMdp() != null && !c.getMdp().isEmpty();
        String sql = "UPDATE client SET nom=?,prenom=?,adresse=?,email=?,telephone=?,idcategorie=?" +
                     (withMdp ? ",mdp=?" : "") + " WHERE idclient=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, c.getNom());   ps.setString(i++, c.getPrenom());
            ps.setString(i++, c.getAdresse()); ps.setString(i++, c.getEmail());
            ps.setLong(i++, c.getTelephone());
            if (c.getIdcategorie() != null) ps.setInt(i++, c.getIdcategorie());
            else ps.setNull(i++, Types.INTEGER);
            if (withMdp) ps.setString(i++, c.getMdp());
            ps.setInt(i, c.getIdclient());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM client WHERE idclient=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int count() throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM client");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private Client map(ResultSet rs) throws SQLException {
        int idcat = rs.getInt("idcategorie");
        Integer idcategorie = rs.wasNull() ? null : idcat;
        return new Client(
            rs.getInt("idclient"), rs.getString("nom"), rs.getString("prenom"),
            rs.getString("adresse"), rs.getString("email"), rs.getString("mdp"),
            rs.getLong("telephone"), idcategorie
        );
    }
}
