-- ============================================================
-- NEIGE & SOLEIL — Base de données complète
-- Importer dans phpMyAdmin avant de lancer l'application
-- ============================================================

DROP DATABASE IF EXISTS `n&s_31_jv`;
CREATE DATABASE `n&s_31_jv` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `n&s_31_jv`;

-- ── Table categorie ───────────────────────────────────────────────────────────
CREATE TABLE categorie (
    idcategorie INT PRIMARY KEY AUTO_INCREMENT,
    libelle     ENUM('particulier','groupes','entreprise','autre') NOT NULL
) ENGINE=InnoDB;

-- ── Table proprio ─────────────────────────────────────────────────────────────
CREATE TABLE proprio (
    idproprio INT          PRIMARY KEY AUTO_INCREMENT,
    nom       VARCHAR(50)  NOT NULL,
    prenom    VARCHAR(50)  NOT NULL,
    adresse   VARCHAR(150) UNIQUE NOT NULL,
    email     VARCHAR(150) UNIQUE NOT NULL,
    mdp       VARCHAR(255) NOT NULL,
    telephone BIGINT       UNIQUE NOT NULL,
    statut    ENUM('privé','public','admin') NOT NULL DEFAULT 'privé'
) ENGINE=InnoDB;

-- ── Table client ──────────────────────────────────────────────────────────────
CREATE TABLE client (
    idclient    INT          PRIMARY KEY AUTO_INCREMENT,
    nom         VARCHAR(50)  NOT NULL,
    prenom      VARCHAR(50)  NOT NULL,
    adresse     VARCHAR(150) UNIQUE NOT NULL,
    email       VARCHAR(150) UNIQUE NOT NULL,
    mdp         VARCHAR(255) NOT NULL,
    telephone   BIGINT       UNIQUE NOT NULL,
    idcategorie INT          NULL DEFAULT NULL,
    CONSTRAINT fk_client_categorie
        FOREIGN KEY (idcategorie) REFERENCES categorie(idcategorie)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ── Table gite ────────────────────────────────────────────────────────────────
CREATE TABLE gite (
    idgite    INT          PRIMARY KEY AUTO_INCREMENT,
    adresse   VARCHAR(150) UNIQUE NOT NULL,
    surface   INT          NOT NULL,
    nbpieces  INT          NOT NULL,
    loyer     INT          NOT NULL,
    idproprio INT          NOT NULL,
    CONSTRAINT fk_gite_proprio
        FOREIGN KEY (idproprio) REFERENCES proprio(idproprio)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ── Table reservation ─────────────────────────────────────────────────────────
-- statut_r inclut 'à valider' pour les réservations créées par l'admin
CREATE TABLE reservation (
    idreservation INT            PRIMARY KEY AUTO_INCREMENT,
    datedebut     DATE           NOT NULL,
    datefin       DATE           NOT NULL,
    prix          DECIMAL(10,2)  NOT NULL,
    transport     ENUM('voiture','train','avion') NOT NULL DEFAULT 'voiture',
    assurance     TINYINT(1)     NOT NULL DEFAULT 0,
    idclient      INT            NOT NULL,
    idgite        INT            NOT NULL,
    statut_r      ENUM('en cours','terminé','non réservé','à valider') NOT NULL DEFAULT 'en cours',
    rapport       TEXT           NULL,
    CONSTRAINT fk_reservation_client
        FOREIGN KEY (idclient) REFERENCES client(idclient)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_reservation_gite
        FOREIGN KEY (idgite) REFERENCES gite(idgite)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ── Triggers règles métier ────────────────────────────────────────────────────

DELIMITER $$

-- Vérification à l'insertion : durée max 21 jours + pas de chevauchement
CREATE TRIGGER before_insert_reservation
BEFORE INSERT ON reservation
FOR EACH ROW
BEGIN
    IF DATEDIFF(NEW.datefin, NEW.datedebut) > 21 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Erreur : durée maximale de réservation dépassée (21 jours).';
    END IF;

    IF EXISTS (
        SELECT 1 FROM reservation
        WHERE idgite = NEW.idgite
          AND statut_r != 'non réservé'
          AND NEW.datedebut < datefin
          AND NEW.datefin  > datedebut
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Erreur : le gîte est déjà réservé sur cette période.';
    END IF;
END$$

-- Mêmes vérifications à la modification (exclut la ligne elle-même)
CREATE TRIGGER before_update_reservation
BEFORE UPDATE ON reservation
FOR EACH ROW
BEGIN
    IF DATEDIFF(NEW.datefin, NEW.datedebut) > 21 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Erreur : durée maximale de réservation dépassée (21 jours).';
    END IF;

    IF EXISTS (
        SELECT 1 FROM reservation
        WHERE idgite = NEW.idgite
          AND idreservation != OLD.idreservation
          AND statut_r != 'non réservé'
          AND NEW.datedebut < datefin
          AND NEW.datefin  > datedebut
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Erreur : le gîte est déjà réservé sur cette période.';
    END IF;
END$$

DELIMITER ;

-- ── Données de test ───────────────────────────────────────────────────────────

INSERT INTO categorie (libelle) VALUES ('particulier'),('groupes'),('entreprise'),('autre');

INSERT INTO proprio (nom, prenom, adresse, email, mdp, telephone, statut) VALUES
('Martin',  'Paul',   '12 rue des Lilas, Toulouse',     'paul.martin@mail.com',   'mdp123', 0612345678, 'privé'),
('Durand',  'Sophie', '45 avenue de Paris, Bordeaux',   'sophie.durand@mail.com', 'mdp456', 0623456789, 'public'),
('Leclerc', 'Jean',   '78 chemin du Lac, Lyon',         'jean.leclerc@mail.com',  'mdp789', 0634567890, 'admin');

INSERT INTO client (nom, prenom, adresse, email, mdp, telephone, idcategorie) VALUES
('Dupont',  'Marie', '10 rue des Fleurs, Nantes',           'marie.dupont@mail.com', 'client123', 0654321987, 1),
('Bernard', 'Luc',   '22 boulevard Victor Hugo, Marseille', 'luc.bernard@mail.com',  'client456', 0643219876, 3),
('Petit',   'Emma',  '5 impasse du Soleil, Lille',          'emma.petit@mail.com',   'client789', 0632109876, 2);

INSERT INTO gite (adresse, surface, nbpieces, loyer, idproprio) VALUES
('Chalet A, Les Alpes',     100, 4, 120, 1),
('Appartement B, La Côte',   50, 2,  75, 2);

INSERT INTO reservation (datedebut, datefin, prix, transport, assurance, idclient, idgite, statut_r) VALUES
('2024-12-15', '2024-12-22', 840.00, 'voiture', 1, 1, 1, 'terminé'),
('2025-01-10', '2025-01-17', 525.00, 'train',   0, 2, 2, 'terminé'),
('2026-07-01', '2026-07-08', 840.00, 'avion',   1, 1, 1, 'à valider');
