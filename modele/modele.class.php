<?php
// modele/modele.class.php

require_once 'config_db.php';

class Modele {
    protected PDO $pdo;

    public function __construct() {
        $this->pdo = ConnexionDB::getInstance()->getPDO();
    }

    // ══════════════════════════════════════════
    //  AUTHENTIFICATION
    // ══════════════════════════════════════════

    /**
     * Récupère un utilisateur par email dans la table donnée.
     * Utilise une whitelist pour éviter toute injection via $table.
     */
    public function getUtilisateurByEmail(string $email, string $table): array|false {
        $tables_autorisees = ['client', 'proprio'];
        if (!in_array($table, $tables_autorisees, true)) {
            return false;
        }
        $stmt = $this->pdo->prepare("SELECT * FROM `$table` WHERE email = :email LIMIT 1");
        $stmt->execute(['email' => $email]);
        return $stmt->fetch();
    }

    // ══════════════════════════════════════════
    //  CRUD GÉNÉRIQUE
    // ══════════════════════════════════════════

    public function selectAll(string $table): array {
        $tables_autorisees = ['client', 'proprio', 'gite', 'reservation'];
        if (!in_array($table, $tables_autorisees, true)) return [];
        $stmt = $this->pdo->query("SELECT * FROM `$table`");
        return $stmt->fetchAll();
    }

    public function selectWhereId(string $table, string $idName, mixed $idValue): array|false {
        $stmt = $this->pdo->prepare("SELECT * FROM `$table` WHERE `$idName` = :id LIMIT 1");
        $stmt->execute(['id' => $idValue]);
        return $stmt->fetch();
    }

    public function delete(string $table, string $idName, mixed $idValue): bool {
        $tables_autorisees = ['client', 'proprio', 'gite', 'reservation'];
        if (!in_array($table, $tables_autorisees, true)) return false;
        $stmt = $this->pdo->prepare("DELETE FROM `$table` WHERE `$idName` = :id");
        return $stmt->execute(['id' => $idValue]);
    }

    // ══════════════════════════════════════════
    //  CLIENT
    // ══════════════════════════════════════════

    public function insertClient(array $data): bool {
        $stmt = $this->pdo->prepare(
            "INSERT INTO client (nom, prenom, adresse, email, mdp, telephone)
             VALUES (:nom, :prenom, :adresse, :email, :mdp, :telephone)"
        );
        return $stmt->execute([
            'nom'       => $data['nom'],
            'prenom'    => $data['prenom'],
            'adresse'   => $data['adresse'],
            'email'     => $data['email'],
            'mdp'       => password_hash($data['mdp'], PASSWORD_DEFAULT),
            'telephone' => $data['telephone'],
        ]);
    }

    public function updateClient(array $data): bool {
        $params = [
            'nom'       => $data['nom'],
            'prenom'    => $data['prenom'],
            'adresse'   => $data['adresse'],
            'email'     => $data['email'],
            'telephone' => $data['telephone'],
            'idclient'  => $data['idclient'],
        ];
        $mdp_sql = '';
        if (!empty($data['mdp'])) {
            $mdp_sql = ', mdp = :mdp';
            $params['mdp'] = password_hash($data['mdp'], PASSWORD_DEFAULT);
        }
        $stmt = $this->pdo->prepare(
            "UPDATE client SET nom = :nom, prenom = :prenom, adresse = :adresse,
             email = :email, telephone = :telephone $mdp_sql
             WHERE idclient = :idclient"
        );
        return $stmt->execute($params);
    }

    // ══════════════════════════════════════════
    //  GITE
    // ══════════════════════════════════════════

    public function insertGite(array $data): bool {
        $stmt = $this->pdo->prepare(
            "INSERT INTO gite (adresse, surface, nbpieces, loyer, idproprio)
             VALUES (:adresse, :surface, :nbpieces, :loyer, :idproprio)"
        );
        return $stmt->execute([
            'adresse'   => $data['adresse'],
            'surface'   => (int)$data['surface'],
            'nbpieces'  => (int)$data['nbpieces'],
            'loyer'     => (int)$data['loyer'],
            'idproprio' => (int)$data['idproprio'],
        ]);
    }

    public function updateGite(array $data): bool {
        $stmt = $this->pdo->prepare(
            "UPDATE gite SET adresse = :adresse, surface = :surface,
             nbpieces = :nbpieces, loyer = :loyer, idproprio = :idproprio
             WHERE idgite = :idgite"
        );
        return $stmt->execute([
            'adresse'   => $data['adresse'],
            'surface'   => (int)$data['surface'],
            'nbpieces'  => (int)$data['nbpieces'],
            'loyer'     => (int)$data['loyer'],
            'idproprio' => (int)$data['idproprio'],
            'idgite'    => (int)$data['idgite'],
        ]);
    }

    // ══════════════════════════════════════════
    //  PROPRIÉTAIRE
    // ══════════════════════════════════════════

    public function insertProprio(array $data): bool {
        $stmt = $this->pdo->prepare(
            "INSERT INTO proprio (nom, prenom, adresse, email, mdp, telephone, statut)
             VALUES (:nom, :prenom, :adresse, :email, :mdp, :telephone, :statut)"
        );
        return $stmt->execute([
            'nom'       => $data['nom'],
            'prenom'    => $data['prenom'],
            'adresse'   => $data['adresse'],
            'email'     => $data['email'],
            'mdp'       => password_hash($data['mdp'], PASSWORD_DEFAULT),
            'telephone' => $data['telephone'],
            'statut'    => $data['statut'],
        ]);
    }

    public function updateProprio(array $data): bool {
        $params = [
            'nom'       => $data['nom'],
            'prenom'    => $data['prenom'],
            'adresse'   => $data['adresse'],
            'email'     => $data['email'],
            'telephone' => $data['telephone'],
            'statut'    => $data['statut'],
            'idproprio' => $data['idproprio'],
        ];
        $mdp_sql = '';
        if (!empty($data['mdp'])) {
            $mdp_sql = ', mdp = :mdp';
            $params['mdp'] = password_hash($data['mdp'], PASSWORD_DEFAULT);
        }
        $stmt = $this->pdo->prepare(
            "UPDATE proprio SET nom = :nom, prenom = :prenom, adresse = :adresse,
             email = :email, telephone = :telephone, statut = :statut $mdp_sql
             WHERE idproprio = :idproprio"
        );
        return $stmt->execute($params);
    }

    // ══════════════════════════════════════════
    //  RÉSERVATION
    // ══════════════════════════════════════════

    public function insertReservation(array $data): bool {
        $stmt = $this->pdo->prepare(
            "INSERT INTO reservation (datedebut, datefin, prix, transport, assurance, idclient, idgite, statut_r)
             VALUES (:datedebut, :datefin, :prix, :transport, :assurance, :idclient, :idgite, :statut_r)"
        );
        return $stmt->execute([
            'datedebut'  => $data['datedebut'],
            'datefin'    => $data['datefin'],
            'prix'       => (float)$data['prix'],
            'transport'  => $data['transport']  ?? 'aucun',
            'assurance'  => (int)($data['assurance'] ?? 0),
            'idclient'   => (int)$data['idclient'],
            'idgite'     => (int)$data['idgite'],
            'statut_r'   => $data['statut_r']   ?? 'en cours',
        ]);
    }

    public function updateReservation(array $data): bool {
        $stmt = $this->pdo->prepare(
            "UPDATE reservation
             SET datedebut = :datedebut, datefin = :datefin, prix = :prix,
                 transport = :transport, assurance = :assurance,
                 idclient = :idclient, idgite = :idgite,
                 statut_r = :statut_r, rapport = :rapport
             WHERE idreservation = :idreservation"
        );
        return $stmt->execute([
            'datedebut'      => $data['datedebut'],
            'datefin'        => $data['datefin'],
            'prix'           => (float)$data['prix'],
            'transport'      => $data['transport']  ?? 'aucun',
            'assurance'      => (int)($data['assurance'] ?? 0),
            'idclient'       => (int)$data['idclient'],
            'idgite'         => (int)$data['idgite'],
            'statut_r'       => $data['statut_r']   ?? 'en cours',
            'rapport'        => $data['rapport']    ?? null,
            'idreservation'  => (int)$data['idreservation'],
        ]);
    }

    /**
     * Sélectionne toutes les réservations avec JOIN client + gite,
     * avec filtres optionnels par client et/ou gite.
     */
    public function selectAllReservations(array $filters = []): array {
        $sql = "SELECT r.*,
                       c.nom AS client_nom, c.prenom AS client_prenom,
                       g.adresse AS gite_adresse
                FROM reservation r
                INNER JOIN client c ON r.idclient = c.idclient
                INNER JOIN gite   g ON r.idgite   = g.idgite
                WHERE 1 = 1";
        $params = [];

        if (!empty($filters['idclient'])) {
            $sql .= " AND r.idclient = :idclient";
            $params['idclient'] = (int)$filters['idclient'];
        }
        if (!empty($filters['idgite'])) {
            $sql .= " AND r.idgite = :idgite";
            $params['idgite'] = (int)$filters['idgite'];
        }

        $sql .= " ORDER BY r.datedebut DESC";
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute($params);
        return $stmt->fetchAll();
    }

    // ══════════════════════════════════════════
    //  STATISTIQUES (tableau de bord)
    // ══════════════════════════════════════════

    public function compterTable(string $table): int {
        $tables_autorisees = ['client', 'proprio', 'gite', 'reservation'];
        if (!in_array($table, $tables_autorisees, true)) return 0;
        $stmt = $this->pdo->query("SELECT COUNT(*) FROM `$table`");
        return (int)$stmt->fetchColumn();
    }

    public function chiffreAffaires(): float {
        $stmt = $this->pdo->query("SELECT COALESCE(SUM(prix), 0) FROM reservation WHERE statut_r = 'terminé'");
        return (float)$stmt->fetchColumn();
    }

    public function reservationsRecentes(int $limit = 5): array {
        $stmt = $this->pdo->prepare(
            "SELECT r.idreservation, r.datedebut, r.datefin, r.prix, r.statut_r,
                    c.nom AS client_nom, c.prenom AS client_prenom,
                    g.adresse AS gite_adresse
             FROM reservation r
             INNER JOIN client c ON r.idclient = c.idclient
             INNER JOIN gite   g ON r.idgite   = g.idgite
             ORDER BY r.idreservation DESC
             LIMIT :lim"
        );
        $stmt->bindValue(':lim', $limit, PDO::PARAM_INT);
        $stmt->execute();
        return $stmt->fetchAll();
    }
}
