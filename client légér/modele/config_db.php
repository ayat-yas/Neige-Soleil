<?php
class ConnexionDB {
    private static ?ConnexionDB $instance = null;
    private PDO $pdo;

    private function __construct() {
        $dsn = "mysql:host=localhost;dbname=n&s_31_jv;charset=utf8mb4";
        $options = [
            PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES   => false,
        ];
        try {
            $this->pdo = new PDO($dsn, 'root', '', $options);
        } catch (PDOException $e) {
            die('Connexion impossible : ' . $e->getMessage());
        }
    }

    public static function getInstance(): self {
        if (self::$instance === null) {
            self::$instance = new self();
        }
        return self::$instance;
    }

    public function getPDO(): PDO {
        return $this->pdo;
    }
}
