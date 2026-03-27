<?php
// vue/erreur.php
$message    = $message    ?? 'Une erreur inattendue est survenue.';
$page_title = 'Erreur';
require '_layout_header.php';
?>

    <div style="text-align:center;padding:4rem 1rem">
        <div style="font-size:4rem;margin-bottom:1rem">⚠️</div>
        <h2 style="color:var(--c-danger);margin-bottom:1rem">Une erreur est survenue</h2>
        <p style="color:var(--c-muted);margin-bottom:2rem"><?php echo htmlspecialchars($message); ?></p>
        <a href="index.php?page=home" class="btn btn-primary">← Retour à l'accueil</a>
    </div>

<?php require '_layout_footer.php'; ?>
