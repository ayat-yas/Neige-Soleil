package ns.dao;

import ns.modele.Proprietaire;
import ns.util.ConnexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Accès aux données de la table `proprio` — toutes les requêtes sont préparées
public class ProprietaireDAO {

    private final Connection conn;

    public ProprietaireDAO() {
        this.conn = ConnexionDB.getInstance().getConnexion();
    }

    public List<Proprietaire> findAll() throws SQLException {
        List<Proprietaire> liste = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM proprio ORDER BY nom, prenom");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) liste.add(map(rs));
        }
        return liste;
    }

    public Proprietaire findByEmail(String email) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM proprio WHERE email=?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<Proprietaire> findByRecherche(String recherche) throws SQLException {
        List<Proprietaire> liste = new ArrayList<>();
        String p = "%" + recherche + "%";
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM proprio WHERE nom LIKE ? OR prenom LIKE ? OR email LIKE ? ORDER BY nom")) {
            ps.setString(1, p); ps.setString(2, p); ps.setString(3, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) liste.add(map(rs));
            }
        }
        return liste;
    }

    public boolean insert(Proprietaire p) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO proprio (nom,prenom,adresse,email,mdp,telephone,statut) VALUES (?,?,?,?,?,?,?)")) {
            ps.setString(1, p.getNom());   ps.setString(2, p.getPrenom());
            ps.setString(3, p.getAdresse()); ps.setString(4, p.getEmail());
            ps.setString(5, p.getMdp());   ps.setLong(6, p.getTelephone());
            ps.setString(7, p.getStatut());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Proprietaire p) throws SQLException {
        boolean withMdp = p.getMdp() != null && !p.getMdp().isEmpty();
        String sql = "UPDATE proprio SET nom=?,prenom=?,adresse=?,email=?,telephone=?,statut=?" +
                     (withMdp ? ",mdp=?" : "") + " WHERE idproprio=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, p.getNom());   ps.setString(i++, p.getPrenom());
            ps.setString(i++, p.getAdresse()); ps.setString(i++, p.getEmail());
            ps.setLong(i++, p.getTelephone()); ps.setString(i++, p.getStatut());
            if (withMdp) ps.setString(i++, p.getMdp());
            ps.setInt(i, p.getIdproprio());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM proprio WHERE idproprio=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int count() throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM proprio");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private Proprietaire map(ResultSet rs) throws SQLException {
        return new Proprietaire(
            rs.getInt("idproprio"), rs.getString("nom"), rs.getString("prenom"),
            rs.getString("adresse"), rs.getString("email"), rs.getString("mdp"),
            rs.getLong("telephone"), rs.getString("statut")
        );
    }
}
