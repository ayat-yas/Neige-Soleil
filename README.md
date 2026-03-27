# ❄ Neige & Soleil ☀ — Guide d'installation

## Structure du projet

```
neige-soleil/
├── index.php                    ← Point d'entrée unique
├── N_S_optimise.sql             ← Base de données à importer
│
├── controleur/
│   └── controleur.class.php     ← Routeur + logique métier
│
├── modele/
│   ├── config_db.php            ← Connexion PDO (Singleton)
│   └── modele.class.php         ← Toutes les requêtes SQL
│
├── vue/
│   ├── _layout_header.php       ← Header commun (navbar)
│   ├── _layout_footer.php       ← Footer commun
│   ├── vue_connexion.php
│   ├── home.php                 ← Dashboard avec stats
│   ├── erreur.php
│   ├── vue_select_client.php
│   ├── vue_select_gite.php
│   ├── vue_select_proprio.php
│   ├── vue_select_reservation.php
│   ├── vue_insert_client.php
│   ├── vue_insert_gite.php
│   ├── vue_insert_proprio.php
│   └── vue_insert_reservation.php
│
├── css/
│   └── style.css                ← Design thème montagne
│
└── images/
    ├── client_icone.png         ← À remplacer
    ├── gite_house.png           ← À remplacer
    ├── proprio_user.png         ← À remplacer
    └── reservation_calendar.png ← À remplacer
```

## Installation avec XAMPP

1. Télécharger et installer **XAMPP** → https://www.apachefriends.org
2. Lancer **Apache** et **MySQL** dans le panneau XAMPP
3. Copier le dossier `neige-soleil/` dans `C:/xampp/htdocs/`
4. Ouvrir **phpMyAdmin** → http://localhost/phpmyadmin
5. Créer une base `n&s_31_jv` (ou cliquer Import directement)
6. Importer `N_S_optimise.sql`
7. Ouvrir → http://localhost/neige-soleil/

## Comptes de test

| Type        | Email                       | Mot de passe |
|-------------|----------------------------|--------------|
| Admin       | jean.leclerc@mail.com      | mdp789       |
| Proprio pub | sophie.durand@mail.com     | mdp456       |
| Proprio prv | paul.martin@mail.com       | mdp123       |
| Client      | marie.dupont@mail.com      | client123    |
| Client      | luc.bernard@mail.com       | client456    |

## Améliorations apportées

### Sécurité
- ✅ `password_hash()` / `password_verify()` pour les nouveaux mots de passe
- ✅ Requêtes PDO préparées partout (protection injections SQL)
- ✅ Whitelist des noms de tables dans le modèle
- ✅ `filter_input()` sur les paramètres GET/POST
- ✅ `htmlspecialchars()` sur tous les affichages

### Code
- ✅ Architecture MVC propre avec layout partagé (`_layout_header/footer`)
- ✅ `match()` PHP 8 dans le routeur (plus lisible que `switch`)
- ✅ Types PHP stricts sur toutes les méthodes
- ✅ Correction du bug `transport`/`assurance` absent des INSERT
- ✅ Dashboard avec stats (nb clients, gîtes, CA, réservations récentes)
- ✅ Calcul de durée automatique dans les tableaux

### Design
- ✅ Thème "montagne" : bleu nuit + blanc + touches dorées
- ✅ Navbar fixe avec info utilisateur et badge statut
- ✅ Cards avec header gradient
- ✅ Badges colorés pour statuts (admin/public/privé, en cours/terminé)
- ✅ Empty state quand les listes sont vides
- ✅ Formulaires en grille 2 colonnes
- ✅ Responsive mobile
- ✅ Calcul durée séjour dans la liste réservations
