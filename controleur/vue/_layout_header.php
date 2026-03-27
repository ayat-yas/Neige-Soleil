<?php
// vue/_layout_header.php
$statut  = $_SESSION['user_statut'] ?? '';
$nom_user = $_SESSION['user_nom']   ?? '';
$page_title = $page_title ?? 'Neige &amp; Soleil';
?>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><?php echo $page_title; ?> — Neige &amp; Soleil</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="page-wrapper">

<nav class="navbar">
    <a href="index.php?page=home" class="navbar-brand">
        <span class="snowflake">❄</span>
        Neige &amp; Soleil
        <span class="sun">☀</span>
    </a>
    <?php if ($nom_user): ?>
    <div class="navbar-user">
        <span>Bonjour, <strong style="color:#fff"><?php echo htmlspecialchars($nom_user); ?></strong></span>
        <span class="badge-statut"><?php echo ucfirst($statut); ?></span>
        <a href="index.php?page=deconnexion" class="btn btn-danger btn-sm">Déconnexion</a>
    </div>
    <?php endif; ?>
</nav>

<div class="container">
