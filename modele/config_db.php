<?php
// modele/config_db.php — Connexion PDO Singleton

class ConnexionDB {
    private static ?ConnexionDB $instance = null;
    private PDO $pdo;

    private function __construct() {
        $host    = 'localhost';
        $db      = 'n&s_31_jv';
        $user    = 'root';
        $pass    = '';
        $charset = 'utf8mb4';

        $dsn     = "mysql:host=$host;dbname=$db;charset=$charset";
        $options = [
            PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES   => false,
        ];

        try {
            $this->pdo = new PDO($dsn, $user, $pass, $options);
        } catch (PDOException $e) {
            die(json_encode(['erreur' => 'Connexion impossible : ' . $e->getMessage()]));
        }
    }

    public static function getInstance(): ConnexionDB {
        if (self::$instance === null) {
            self::$instance = new ConnexionDB();
        }
        return self::$instance;
    }

    public function getPDO(): PDO {
        return $this->pdo;
    }
}
