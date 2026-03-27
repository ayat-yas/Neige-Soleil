<?php
// controleur/controleur.class.php

require_once 'modele/modele.class.php';

class Controleur {
    protected Modele $modele;

    public function __construct() {
        if (session_status() === PHP_SESSION_NONE) {
            session_start();
        }
        $this->modele = new Modele();
    }

    // ══════════════════════════════════════════
    //  ROUTEUR PRINCIPAL
    // ══════════════════════════════════════════

    public function gererRequete(): void {
        $page = filter_input(INPUT_GET, 'page', FILTER_SANITIZE_SPECIAL_CHARS) ?? 'connexion';

        // Toujours aller à connexion si non connecté (sauf déconnexion)
        if (!isset($_SESSION['user_id']) && $page !== 'connexion') {
            $this->redirect('connexion');
        }

        match ($page) {
            'connexion'              => $this->gererConnexion(),
            'deconnexion'            => $this->gererDeconnexion(),
            'home'                   => $this->gererHome(),
            'gestion_client'         => $this->autoriserProprio() && $this->gererCrud('client'),
            'gestion_proprio'        => $this->autoriserProprio() && $this->gererCrud('proprio'),
            'gestion_gite'           => $this->autoriserConnecte() && $this->gererCrud('gite'),
            'gestion_reservation'    => $this->autoriserConnecte() && $this->gererCrud('reservation'),
            'vue_insert_client'      => $this->gererFormulaire('client'),
            'vue_insert_gite'        => $this->gererFormulaire('gite'),
            'vue_insert_proprio'     => $this->gererFormulaire('proprio'),
            'vue_insert_reservation' => $this->gererFormulaire('reservation'),
            default                  => $this->vue('erreur', ['message' => 'Page introuvable.']),
        };
    }

    // ══════════════════════════════════════════
    //  CONNEXION / DÉCONNEXION
    // ══════════════════════════════════════════

    private function gererConnexion(): void {
        $erreur = '';

        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $email      = trim($_POST['email']     ?? '');
            $mdp        = $_POST['mdp']            ?? '';
            $type_user  = $_POST['type_user']      ?? '';

            if (!in_array($type_user, ['client', 'proprio'], true)) {
                $erreur = 'Type utilisateur invalide.';
            } else {
                $user = $this->modele->getUtilisateurByEmail($email, $type_user);

                // Support des mots de passe en clair (legacy) ET hashés (nouveaux)
                $mdp_ok = $user && (
                    password_verify($mdp, $user['mdp']) ||
                    $mdp === $user['mdp']   // rétrocompatibilité données de test
                );

                if ($mdp_ok) {
                    $_SESSION['user_id']     = $user['id' . $type_user];
                    $_SESSION['user_nom']    = $user['prenom'] . ' ' . $user['nom'];
                    $_SESSION['user_statut'] = $type_user === 'proprio' ? ($user['statut'] ?? 'privé') : 'client';
                    $_SESSION['user_type']   = $type_user;
                    $this->redirect('home');
                } else {
                    $erreur = 'Identifiants incorrects.';
                }
            }
        }

        $this->vue('vue_connexion', ['erreur' => $erreur]);
    }

    private function gererDeconnexion(): void {
        session_unset();
        session_destroy();
        $this->redirect('connexion');
    }

    // ══════════════════════════════════════════
    //  ACCUEIL
    // ══════════════════════════════════════════

    private function gererHome(): void {
        $stats = [];
        if ($this->estProprio()) {
            $stats = [
                'nb_clients'      => $this->modele->compterTable('client'),
                'nb_gites'        => $this->modele->compterTable('gite'),
                'nb_reservations' => $this->modele->compterTable('reservation'),
                'chiffre'         => $this->modele->chiffreAffaires(),
                'recentes'        => $this->modele->reservationsRecentes(5),
            ];
        }
        $this->vue('home', ['stats' => $stats]);
    }

    // ══════════════════════════════════════════
    //  CRUD (SELECT + DELETE)
    // ══════════════════════════════════════════

    private function gererCrud(string $table): bool {
        $idName  = 'id' . $table;
        $message = filter_input(INPUT_GET, 'msg', FILTER_SANITIZE_SPECIAL_CHARS) ?? '';

        // DELETE
        if ($_SERVER['REQUEST_METHOD'] === 'POST'
            && ($_POST['action'] ?? '') === 'delete'
            && !empty($_POST['id'])
        ) {
            $this->autoriserProprio();
            $ok      = $this->modele->delete($table, $idName, (int)$_POST['id']);
            $message = $ok
                ? ucfirst($table) . ' supprimé avec succès.'
                : 'Erreur lors de la suppression.';
        }

        if ($table === 'reservation') {
            $this->afficherReservations($message);
        } else {
            $donnees = $this->modele->selectAll($table);
            $this->vue('vue_select_' . $table, [
                'donnees' => $donnees,
                'message' => $message,
                'table'   => $table,
            ]);
        }
        return true;
    }

    private function afficherReservations(string $message = ''): void {
        $statut = $_SESSION['user_statut'] ?? 'client';

        $filters = [
            'idclient' => filter_input(INPUT_GET, 'filter_client', FILTER_VALIDATE_INT) ?: null,
            'idgite'   => filter_input(INPUT_GET, 'filter_gite',   FILTER_VALIDATE_INT) ?: null,
        ];

        // Un client ne voit QUE ses réservations
        if ($statut === 'client') {
            $filters['idclient'] = $_SESSION['user_id'];
        }

        $donnees      = $this->modele->selectAllReservations($filters);
        $clients_list = $this->estProprio() ? $this->modele->selectAll('client') : [];
        $gites_list   = $this->modele->selectAll('gite');

        $this->vue('vue_select_reservation', [
            'donnees'       => $donnees,
            'message'       => $message,
            'filters'       => $filters,
            'clients_list'  => $clients_list,
            'gites_list'    => $gites_list,
            'peut_modifier' => $this->estProprio(),
        ]);
    }

    // ══════════════════════════════════════════
    //  FORMULAIRES INSERT / UPDATE
    // ══════════════════════════════════════════

    private function gererFormulaire(string $table): void {
        $idName    = 'id' . $table;
        $idValue   = filter_input(INPUT_GET, 'id', FILTER_VALIDATE_INT);
        $message   = '';
        $donnee    = [];

        // Droits : seul un client peut insérer une réservation sans droits proprio
        $insertion_client_resa = ($table === 'reservation' && !$idValue && !$this->estProprio());
        if (!$insertion_client_resa) {
            $this->autoriserProprio();
        }

        // ── POST : INSERT ou UPDATE ──
        if ($_SERVER['REQUEST_METHOD'] === 'POST' && !empty($_POST['action'])) {
            $data = $_POST;
            unset($data['action']);

            try {
                if ($_POST['action'] === 'insert') {
                    if ($table === 'reservation' && !$this->estProprio()) {
                        $data['idclient']  = $_SESSION['user_id'];
                        $data['statut_r']  = 'en cours';
                    }
                    $method = 'insert' . ucfirst($table);
                    if (!method_exists($this->modele, $method)) {
                        throw new Exception("Méthode $method introuvable.");
                    }
                    if ($this->modele->$method($data)) {
                        $this->redirect('gestion_' . $table, urlencode(ucfirst($table) . ' ajouté avec succès.'));
                    }
                    throw new Exception("Erreur lors de l'insertion.");

                } elseif ($_POST['action'] === 'update' && !empty($data[$idName])) {
                    $this->autoriserProprio();
                    $method = 'update' . ucfirst($table);
                    if (!method_exists($this->modele, $method)) {
                        throw new Exception("Méthode $method introuvable.");
                    }
                    if ($this->modele->$method($data)) {
                        $this->redirect('gestion_' . $table, urlencode(ucfirst($table) . ' modifié avec succès.'));
                    }
                    throw new Exception("Erreur lors de la modification.");
                }
            } catch (Exception $e) {
                $message = $e->getMessage();
            }
        }

        // ── GET : Pré-remplissage formulaire modification ──
        if ($idValue) {
            $donnee = $this->modele->selectWhereId($table, $idName, $idValue);
            if (!$donnee) {
                $message = 'Enregistrement introuvable.';
            }
        }

        // Listes déroulantes pour clés étrangères
        $extra = [];
        if ($table === 'gite') {
            $extra['proprietaires'] = $this->modele->selectAll('proprio');
        } elseif ($table === 'reservation') {
            $extra['clients_list'] = $this->modele->selectAll('client');
            $extra['gites_list']   = $this->modele->selectAll('gite');
        }

        $this->vue('vue_insert_' . $table, array_merge([
            'donnee'  => $donnee,
            'message' => $message,
            'idName'  => $idName,
        ], $extra));
    }

    // ══════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════

    protected function vue(string $nom, array $donnees = []): void {
        extract($donnees);
        $chemin = 'vue/' . $nom . '.php';
        if (file_exists($chemin)) {
            require $chemin;
        } else {
            $message = "Vue '$nom' introuvable.";
            require 'vue/erreur.php';
        }
    }

    private function redirect(string $page, string $msg = ''): never {
        $url = 'index.php?page=' . $page;
        if ($msg) $url .= '&msg=' . $msg;
        header('Location: ' . $url);
        exit;
    }

    private function estProprio(): bool {
        return in_array($_SESSION['user_statut'] ?? '', ['admin', 'privé', 'public'], true);
    }

    private function autoriserProprio(): bool {
        if (!$this->estProprio()) {
            $this->redirect('home');
        }
        return true;
    }

    private function autoriserConnecte(): bool {
        if (!isset($_SESSION['user_id'])) {
            $this->redirect('connexion');
        }
        return true;
    }
}
