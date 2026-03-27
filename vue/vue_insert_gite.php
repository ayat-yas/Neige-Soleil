<?php
// vue/vue_insert_gite.php
$donnee        = $donnee        ?? [];
$message       = $message       ?? '';
$proprietaires = $proprietaires ?? [];
$mode          = isset($donnee['idgite']) ? 'Modifier' : 'Ajouter';
$action        = isset($donnee['idgite']) ? 'update'   : 'insert';
$page_title    = $mode . ' un gîte';
require '_layout_header.php';
?>

    <div class="page-title">🏡 <?php echo $mode; ?> un Gîte</div>

    <?php if ($message): ?>
        <div class="alert <?php echo str_contains($message, 'Erreur') ? 'alert-danger' : 'alert-success'; ?>">
            <?php echo htmlspecialchars($message); ?>
        </div>
    <?php endif; ?>

    <div class="card">
        <div class="card-header">
            <span class="icon">🏡</span>
            <h3><?php echo $mode; ?> un gîte</h3>
        </div>
        <div class="card-body">
            <form method="POST" action="index.php?page=vue_insert_gite">
                <input type="hidden" name="action" value="<?php echo $action; ?>">
                <?php if (isset($donnee['idgite'])): ?>
                    <input type="hidden" name="idgite" value="<?php echo $donnee['idgite']; ?>">
                <?php endif; ?>

                <div class="form-grid">
                    <div class="form-group full">
                        <label for="adresse">Adresse du gîte *</label>
                        <input type="text" id="adresse" name="adresse" required
                               placeholder="Ex : Chalet A, Les Alpes"
                               value="<?php echo htmlspecialchars($donnee['adresse'] ?? ''); ?>">
                    </div>
                    <div class="form-group">
                        <label for="surface">Surface (m²) *</label>
                        <input type="number" id="surface" name="surface" min="1" required
                               value="<?php echo htmlspecialchars($donnee['surface'] ?? ''); ?>">
                    </div>
                    <div class="form-group">
                        <label for="nbpieces">Nombre de pièces *</label>
                        <input type="number" id="nbpieces" name="nbpieces" min="1" required
                               value="<?php echo htmlspecialchars($donnee['nbpieces'] ?? ''); ?>">
                    </div>
                    <div class="form-group">
                        <label for="loyer">Loyer journalier (€) *</label>
                        <input type="number" id="loyer" name="loyer" min="0" required
                               value="<?php echo htmlspecialchars($donnee['loyer'] ?? ''); ?>">
                    </div>
                    <div class="form-group">
                        <label for="idproprio">Propriétaire *</label>
                        <select id="idproprio" name="idproprio" required>
                            <option value="">— Sélectionner —</option>
                            <?php foreach ($proprietaires as $p): ?>
                                <option value="<?php echo $p['idproprio']; ?>"
                                    <?php echo (isset($donnee['idproprio']) && $donnee['idproprio'] == $p['idproprio']) ? 'selected' : ''; ?>>
                                    <?php echo htmlspecialchars($p['prenom'] . ' ' . $p['nom'] . ' (' . ucfirst($p['statut']) . ')'); ?>
                                </option>
                            <?php endforeach; ?>
                        </select>
                    </div>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn <?php echo $mode === 'Ajouter' ? 'btn-success' : 'btn-warning'; ?>">
                        <?php echo $mode === 'Ajouter' ? '+ Ajouter' : '✏ Modifier'; ?> le gîte
                    </button>
                    <a href="index.php?page=gestion_gite" class="btn btn-outline">← Annuler</a>
                </div>
            </form>
        </div>
    </div>

<?php require '_layout_footer.php'; ?>
