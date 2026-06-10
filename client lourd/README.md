# ❄ Neige & Soleil ☀ — Client Lourd Java

Application de bureau Java/Swing connectée à la même base MySQL que le client léger PHP.

## Prérequis

- **Java 17+** — https://adoptium.net
- **WAMP ou XAMPP** avec MySQL démarré
- **Base de données importée** : importer `N_S_complet.sql` (fourni avec le client léger)

## Installation du driver MySQL

1. Télécharger `mysql-connector-j-X.X.X.jar` sur https://dev.mysql.com/downloads/connector/j/
2. Le placer dans le dossier `lib/` sous le nom `mysql-connector-j.jar`

## Lancement sur Windows

Double-cliquer sur `run.bat` — il compile et lance automatiquement.

Ou en ligne de commande :
```
javac -cp "lib\mysql-connector-j.jar" -d out -sourcepath src\main\java src\main\java\ns\Main.java
java -cp "out;lib\mysql-connector-j.jar" ns.Main
```

## Comptes de test

| Email | Mot de passe | Statut |
|-------|-------------|--------|
| jean.leclerc@mail.com | mdp789 | Admin |
| sophie.durand@mail.com | mdp456 | Public |
| paul.martin@mail.com | mdp123 | Privé |

## Architecture MVC

```
ns/
├── Main.java                    ← Point d'entrée
├── controleur/
│   └── AppControleur.java       ← Logique métier, session admin
├── modele/
│   ├── Proprietaire.java        ← Entité proprio (table `proprio`)
│   ├── Client.java              ← Entité client (table `client`)
│   ├── Gite.java                ← Entité gite (table `gite`)
│   └── Reservation.java         ← Entité reservation (table `reservation`)
├── dao/
│   ├── ProprietaireDAO.java     ← Requêtes SQL proprio
│   ├── ClientDAO.java           ← Requêtes SQL client
│   ├── GiteDAO.java             ← Requêtes SQL gite
│   └── ReservationDAO.java      ← Requêtes SQL reservation
├── vue/
│   ├── VueConnexion.java        ← Fenêtre de login
│   ├── VuePrincipale.java       ← Fenêtre principale (onglets)
│   ├── VueDashboard.java        ← Statistiques + réservations récentes
│   ├── VueProprietaires.java    ← CRUD propriétaires
│   ├── VueClients.java          ← CRUD clients
│   ├── VueGites.java            ← CRUD gites
│   └── VueReservations.java     ← CRUD réservations + filtres
└── util/
    ├── ConnexionDB.java         ← Singleton JDBC
    ├── Style.java               ← Palette et polices (thème dark)
    └── UIFactory.java           ← Composants Swing stylisés
```

## Même base que le client léger

Les deux applications partagent exactement la même base `n&s_31_jv` :
- Mêmes tables, mêmes contraintes, mêmes triggers
- Modifications visibles en temps réel dans les deux interfaces
- Statut `à valider` géré dans les deux applications
