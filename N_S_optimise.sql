-- ============================================================
-- NEIGE & SOLEIL — Base de données
-- Version corrigée et complète
-- ============================================================

DROP DATABASE IF EXISTS `n&s_31_jv`;
CREATE DATABASE `n&s_31_jv` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `n&s_31_jv`;

-- ══════════════════════════════════════════
-- TABLE PROPRIO
-- ══════════════════════════════════════════
CREATE TABLE proprio (
    idproprio  INT          PRIMARY KEY AUTO_INCREMENT,
    nom        VARCHAR(50)  NOT NULL,
    prenom     VARCHAR(50)  NOT NULL,
    adresse    VARCHAR(150) UNIQUE NOT NULL,
    email      VARCHAR(150) UNIQUE NOT NULL,
    mdp        VARCHAR(255) NOT NULL,         -- VARCHAR(255) pour password_hash
    telephone  BIGINT       UNIQUE NOT NULL,
    statut     ENUM('privé','public','admin') NOT NULL DEFAULT 'privé'
) ENGINE=InnoDB;

-- ══════════════════════════════════════════
-- TABLE CLIENT
-- ══════════════════════════════════════════
CREATE TABLE client (
    idclient   INT          PRIMARY KEY AUTO_INCREMENT,
    nom        VARCHAR(50)  NOT NULL,
    prenom     VARCHAR(50)  NOT NULL,
    adresse    VARCHAR(150) UNIQUE NOT NULL,
    email      VARCHAR(150) UNIQUE NOT NULL,
    mdp        VARCHAR(255) NOT NULL,         -- VARCHAR(255) pour password_hash
    telephone  BIGINT       UNIQUE NOT NULL
) ENGINE=InnoDB;

-- ══════════════════════════════════════════
-- TABLE GITE
-- ══════════════════════════════════════════
CREATE TABLE gite (
    idgite     INT          PRIMARY KEY AUTO_INCREMENT,
    adresse    VARCHAR(150) UNIQUE NOT NULL,
    surface    INT          NOT NULL,
    nbpieces   INT          NOT NULL,
    loyer      INT          NOT NULL,
    idproprio  INT          NOT NULL,
    CONSTRAINT fk_gite_proprio
        FOREIGN KEY (idproprio) REFERENCES proprio(idproprio)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ══════════════════════════════════════════
-- TABLE RESERVATION (corrigée : transport aligné sur le PHP)
-- ══════════════════════════════════════════
CREATE TABLE reservation (
    idreservation  INT         PRIMARY KEY AUTO_INCREMENT,
    datedebut      DATE        NOT NULL,
    datefin        DATE        NOT NULL,
    prix           DECIMAL(10,2) NOT NULL,
    transport      ENUM('voiture','train','avion') NOT NULL DEFAULT 'voiture',
    assurance      TINYINT(1)  NOT NULL DEFAULT 0,
    idclient       INT         NOT NULL,
    idgite         INT         NOT NULL,
    statut_r       ENUM('en cours','terminé','non réservé') NOT NULL DEFAULT 'en cours',
    rapport        TEXT        NULL,
    CONSTRAINT fk_reservation_client
        FOREIGN KEY (idclient) REFERENCES client(idclient)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_reservation_gite
        FOREIGN KEY (idgite) REFERENCES gite(idgite)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ══════════════════════════════════════════
-- DONNÉES DE TEST
-- Mots de passe en clair (rétrocompatibilité)
-- Remplacer par password_hash() en production
-- ══════════════════════════════════════════

INSERT INTO proprio (nom, prenom, adresse, email, mdp, telephone, statut) VALUES
('Martin',  'Paul',   '12 rue des Lilas, Toulouse',     'paul.martin@mail.com',   'mdp123', 0612345678, 'privé'),
('Durand',  'Sophie', '45 avenue de Paris, Bordeaux',   'sophie.durand@mail.com', 'mdp456', 0623456789, 'public'),
('Leclerc', 'Jean',   '78 chemin du Lac, Lyon',         'jean.leclerc@mail.com',  'mdp789', 0634567890, 'admin');

INSERT INTO client (nom, prenom, adresse, email, mdp, telephone) VALUES
('Dupont',  'Marie', '10 rue des Fleurs, Nantes',            'marie.dupont@mail.com', 'client123', 0654321987),
('Bernard', 'Luc',   '22 boulevard Victor Hugo, Marseille',  'luc.bernard@mail.com',  'client456', 0643219876),
('Petit',   'Emma',  '5 impasse du Soleil, Lille',           'emma.petit@mail.com',   'client789', 0632109876);

INSERT INTO gite (adresse, surface, nbpieces, loyer, idproprio) VALUES
('Chalet A, Les Alpes',   100, 4, 120, 1),
('Appartement B, La Côte', 50, 2,  75, 2);

INSERT INTO reservation (datedebut, datefin, prix, transport, assurance, idclient, idgite, statut_r) VALUES
('2024-12-15', '2024-12-22', 840.00,  'voiture', 1, 1, 1, 'terminé'),
('2025-01-10', '2025-01-17', 525.00,  'train',   0, 2, 2, 'terminé'),
('2025-06-01', '2025-06-08', 840.00,  'avion',   1, 3, 1, 'en cours');
