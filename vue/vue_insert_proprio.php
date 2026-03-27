<?php
// vue/vue_insert_proprio.php
$donnee  = $donnee  ?? [];
$message = $message ?? '';
$mode    = isset($donnee['idproprio']) ? 'Modifier' : 'Ajouter';
$action  = isset($donnee['idproprio']) ? 'update'   : 'insert';
$page_title = $mode . ' un propriétaire';
require '_layout_header.php';
?>

    <div class="page-title">🔑 <?php echo $mode; ?> un Propriétaire</div>

    <?php if ($message): ?>
        <div class="alert <?php echo str_contains($message, 'Erreur') ? 'alert-danger' : 'alert-success'; ?>">
            <?php echo htmlspecialchars($message); ?>
        </div>
    <?php endif; ?>

    <div class="card">
        <div class="card-header">
            <span class="icon">🔑</span>
            <h3><?php echo $mode; ?> un propriétaire</h3>
        </div>
        <div class="card-body">
            <form method="POST" action="index.php?page=vue_insert_proprio">
                <input type="hidden" name="action" value="<?php echo $action; ?>">
                <?php if (isset($donnee['idproprio'])): ?>
                    <input type="hidden" name="idproprio" value="<?php echo $donnee['idproprio']; ?>">
                <?php endif; ?>

                <div class="form-grid">
                    <div class="form-group">
                        <label for="nom">Nom *</label>
                        <input type="text" id="nom" name="nom" required
                               value="<?php echo htmlspecialchars($donnee['nom'] ?? ''); ?>">
                    </div>
                    <div class="form-group">
                        <label for="prenom">Prénom *</label>
                        <input type="text" id="prenom" name="prenom" required
                               value="<?php echo htmlspecialchars($donnee['prenom'] ?? ''); ?>">
                    </div>
                    <div class="form-group">
                        <label for="email">Email *</label>
                        <input type="email" id="email" name="email" required
                               value="<?php echo htmlspecialchars($donnee['email'] ?? ''); ?>">
                    </div>
                    <div class="form-group">
                        <label for="telephone">Téléphone *</label>
                        <input type="tel" id="telephone" name="telephone" required
                               value="<?php echo htmlspecialchars($donnee['telephone'] ?? ''); ?>">
                    </div>
                    <div class="form-group full">
                        <label for="adresse">Adresse *</label>
                        <input type="text" id="adresse" name="adresse" required
                               value="<?php echo htmlspecialchars($donnee['adresse'] ?? ''); ?>">
                    </div>
                    <div class="form-group">
                        <label for="statut">Statut *</label>
                        <select id="statut" name="statut" required>
                            <?php foreach (['privé', 'public', 'admin'] as $s): ?>
                                <option value="<?php echo $s; ?>"
                                    <?php echo (isset($donnee['statut']) && $donnee['statut'] === $s) ? 'selected' : ''; ?>>
                                    <?php echo ucfirst($s); ?>
                                </option>
                            <?php endforeach; ?>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="mdp">
                            Mot de passe <?php echo $mode === 'Modifier' ? '<small class="form-hint">(vide = inchangé)</small>' : '*'; ?>
                        </label>
                        <input type="password" id="mdp" name="mdp"
                               <?php echo $mode === 'Ajouter' ? 'required' : ''; ?>
                               placeholder="••••••••">
                    </div>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn <?php echo $mode === 'Ajouter' ? 'btn-success' : 'btn-warning'; ?>">
                        <?php echo $mode === 'Ajouter' ? '+ Ajouter' : '✏ Modifier'; ?> le propriétaire
                    </button>
                    <a href="index.php?page=gestion_proprio" class="btn btn-outline">← Annuler</a>
                </div>
            </form>
        </div>
    </div>

<?php require '_layout_footer.php'; ?>
