-- On supprime et recrée la base
DROP DATABASE IF EXISTS `n&s_31_jv`;

-- ⚠️ Le nom contient un “&” → pas valide pour un identifiant SQL, on le met entre backticks
CREATE DATABASE `n&s_31_jv`;
USE `n&s_31_jv`;

-- =========================
-- TABLE PROPRIO
-- =========================
CREATE TABLE proprio (
    idproprio INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL, 
    prenom VARCHAR(50) NOT NULL,
    adresse VARCHAR(150) UNIQUE NOT NULL, 
    email VARCHAR(150) UNIQUE NOT NULL, 
    mdp VARCHAR(150) NOT NULL, 
    telephone BIGINT UNIQUE NOT NULL,
    statut ENUM('privé', 'public', 'admin') 
) ENGINE = InnoDB;

-- =========================
-- TABLE CLIENT
-- =========================
CREATE TABLE client (
    idclient INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL, 
    prenom VARCHAR(50) NOT NULL, 
    adresse VARCHAR(150) UNIQUE NOT NULL, 
    email VARCHAR(150) UNIQUE NOT NULL, 
    mdp VARCHAR(150) NOT NULL, 
    telephone BIGINT UNIQUE NOT NULL
) ENGINE = InnoDB;

-- =========================
-- TABLE GITE
-- =========================
CREATE TABLE gite (
    idgite INT PRIMARY KEY AUTO_INCREMENT,
    adresse VARCHAR(150) UNIQUE NOT NULL, 
    surface INT NOT NULL, 
    nbpieces INT NOT NULL, 
    loyer INT NOT NULL, 
    idproprio INT NOT NULL,
    FOREIGN KEY (idproprio) REFERENCES proprio(idproprio) ON DELETE CASCADE
) ENGINE = InnoDB;

-- =========================
-- TABLE RESERVATION (CORRIGÉE : Alignée sur les noms de colonnes du PHP)
-- =========================
CREATE TABLE reservation (
    idreservation INT AUTO_INCREMENT PRIMARY KEY,
    datedebut DATE NOT NULL,
    datefin DATE NOT NULL,
    prix INT NOT NULL,

    transport ENUM('voiture', 'train', 'avion') NOT NULL,
    assurance TINYINT(1) NOT NULL DEFAULT 0,

    idclient INT NOT NULL,
    idgite INT NOT NULL,

    statut_r ENUM('en cours', 'terminé', 'non réservé') NOT NULL,
    rapport TEXT,

    CONSTRAINT fk_reservation_client
        FOREIGN KEY (idclient) REFERENCES client(idclient)
        ON DELETE CASCADE,

    CONSTRAINT fk_reservation_gite
        FOREIGN KEY (idgite) REFERENCES gite(idgite)
        ON DELETE CASCADE
) ENGINE=InnoDB;


-- =========================
-- INSERTS (Mots de passe en clair et noms de colonnes corrigés)
-- =========================

-- PROPRIÉTAIRES
INSERT INTO proprio (nom, prenom, adresse, email, mdp, telephone, statut) VALUES
('Martin', 'Paul', '12 rue des Lilas, Toulouse', 'paul.martin@mail.com', 'mdp123', 0612345678, 'privé'),
('Durand', 'Sophie', '45 avenue de Paris, Bordeaux', 'sophie.durand@mail.com', 'mdp456', 0623456789, 'public'),
('Leclerc', 'Jean', '78 chemin du Lac, Lyon', 'jean.leclerc@mail.com', 'mdp789', 0634567890, 'admin'); 

-- CLIENTS
INSERT INTO client (nom, prenom, adresse, email, mdp, telephone) VALUES
('Dupont', 'Marie', '10 rue des Fleurs, Nantes', 'marie.dupont@mail.com', 'client123', 0654321987),
('Bernard', 'Luc', '22 boulevard Victor Hugo, Marseille', 'luc.bernard@mail.com', 'client456', 0643219876),
('Petit', 'Emma', '5 impasse du Soleil, Lille', 'emma.petit@mail.com', 'client789', 0632109876);

-- GITES
INSERT INTO gite (adresse, surface, nbpieces, loyer, idproprio) VALUES
('Chalet A, Les Alpes', 100, 4, 1200, 1),
('Appart B, La Côte', 50, 2, 750, 2);

-- RESERVATIONS (Correction de l'insertion pour utiliser idreservation/datedebut/datefin)
INSERT INTO reservation (idreservation, datedebut, datefin, prix, idclient, idgite, statut_r) VALUES
(1, '2024-12-15', '2024-12-22', 8400, 1, 1, 'en cours'), 
(2, '2025-01-10', '2025-01-17', 5250, 2, 2, 'terminé');