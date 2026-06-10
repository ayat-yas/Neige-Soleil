package ns.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Singleton JDBC — une seule connexion partagée par toute l'application
public class ConnexionDB {

    // Le '&' dans le nom de base est encodé en %26 pour l'URL JDBC
    private static final String URL  = "jdbc:mysql://localhost:3306/n%26s_31_jv" +
                                       "?useSSL=false&serverTimezone=Europe/Paris&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASS = "";

    private static ConnexionDB instance;
    private Connection connexion;

    private ConnexionDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connexion = DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL introuvable — ajoutez mysql-connector-j.jar dans lib/", e);
        } catch (SQLException e) {
            throw new RuntimeException("Connexion impossible : " + e.getMessage(), e);
        }
    }

    public static ConnexionDB getInstance() {
        if (instance == null || !instance.isAlive()) {
            instance = new ConnexionDB();
        }
        return instance;
    }

    public Connection getConnexion() {
        return connexion;
    }

    private boolean isAlive() {
        try {
            return connexion != null && !connexion.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static void fermer() {
        if (instance != null && instance.isAlive()) {
            try { instance.connexion.close(); } catch (SQLException ignored) {}
            instance = null;
        }
    }
}
