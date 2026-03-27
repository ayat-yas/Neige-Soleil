<?php
// vue/vue_connexion.php
$erreur = $erreur ?? '';
?>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion — Neige &amp; Soleil</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="login-page">
    <div class="login-card">

        <div class="login-logo">
            <span class="logo-text">❄ Neige &amp; Soleil ☀</span>
            <span class="logo-sub">Gestion de locations saisonnières</span>
        </div>

        <?php if ($erreur): ?>
            <div class="alert alert-danger">
                ⚠ <?php echo htmlspecialchars($erreur); ?>
            </div>
        <?php endif; ?>

        <form method="POST" action="index.php?page=connexion">

            <div class="form-group" style="margin-bottom:1rem">
                <label for="email">Adresse e-mail</label>
                <input type="email" id="email" name="email"
                       placeholder="votre@email.com" required
                       value="<?php echo htmlspecialchars($_POST['email'] ?? ''); ?>">
            </div>

            <div class="form-group" style="margin-bottom:1rem">
                <label for="mdp">Mot de passe</label>
                <input type="password" id="mdp" name="mdp"
                       placeholder="••••••••" required>
            </div>

            <div class="form-group" style="margin-bottom:1.75rem">
                <label for="type_user">Je suis</label>
                <select id="type_user" name="type_user" required>
                    <option value="proprio">Propriétaire / Administrateur</option>
                    <option value="client">Client</option>
                </select>
            </div>

            <button type="submit" class="btn btn-primary btn-lg" style="width:100%;justify-content:center">
                Se connecter
            </button>

        </form>
    </div>
</div>

</body>
</html>
