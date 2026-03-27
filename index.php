<?php
// index.php — Point d'entrée unique de l'application

require_once 'controleur/controleur.class.php';

$controleur = new Controleur();
$controleur->gererRequete();
