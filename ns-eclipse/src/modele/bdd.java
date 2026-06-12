package modele;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe de connexion a la base de donnees MySQL.
 * Equivalent de ConnexionDB.java — version simplifiee compatible tous JRE.
 */
public class bdd {

    private String host;
    private String nomBase;
    private String user;
    private String motDePasse;
    private Connection maConnexion;

    public bdd(String host, String nomBase, String user, String motDePasse) {
        this.host        = host;
        this.nomBase     = nomBase;
        this.user        = user;
        this.motDePasse  = motDePasse;
    }

    public void seConnecter() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + host + ":3306/" + nomBase
                       + "?useSSL=false&serverTimezone=Europe/Paris&characterEncoding=UTF-8";
            maConnexion = DriverManager.getConnection(url, user, motDePasse);
        } catch (ClassNotFoundException e) {
            System.out.println("Driver MySQL introuvable : " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Erreur connexion BDD : " + e.getMessage());
        }
    }

    public void seDeconnecter() {
        try {
            if (maConnexion != null && !maConnexion.isClosed()) {
                maConnexion.close();
            }
        } catch (SQLException e) {
            System.out.println("Erreur deconnexion : " + e.getMessage());
        }
    }

    public Connection getMaConnexion() {
        return maConnexion;
    }
}
