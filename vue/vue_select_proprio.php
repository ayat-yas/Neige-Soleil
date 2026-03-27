<?php
// vue/vue_select_proprio.php
$proprietaires = $donnees ?? [];
$message       = $_GET['msg'] ?? ($message ?? '');
$page_title    = 'Propriétaires';
require '_layout_header.php';
?>

    <div class="page-title">🔑 Gestion des Propriétaires</div>

    <?php if ($message): ?>
        <div class="alert alert-success">✔ <?php echo htmlspecialchars($message); ?></div>
    <?php endif; ?>

    <div class="action-bar">
        <a href="index.php?page=home" class="btn btn-outline">← Accueil</a>
        <a href="index.php?page=vue_insert_proprio" class="btn btn-success">+ Ajouter un propriétaire</a>
        <span style="margin-left:auto;color:var(--c-muted);font-size:.875rem"><?php echo count($proprietaires); ?> propriétaire(s)</span>
    </div>

    <div class="table-wrapper">
        <table>
            <thead>
                <tr>
                    <th>#</th>
                    <th>Nom & Prénom</th>
                    <th>Email</th>
                    <th>Téléphone</th>
                    <th>Statut</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <?php if (empty($proprietaires)): ?>
                    <tr><td colspan="6">
                        <div class="empty-state">
                            <div class="empty-icon">🔑</div>
                            <p>Aucun propriétaire enregistré.</p>
                        </div>
                    </td></tr>
                <?php else: ?>
                    <?php foreach ($proprietaires as $p): ?>
                    <?php
                    $badge_class = match($p['statut']) {
                        'admin'  => 'badge-admin',
                        'public' => 'badge-public',
                        default  => 'badge-prive',
                    };
                    ?>
                    <tr>
                        <td><strong><?php echo $p['idproprio']; ?></strong></td>
                        <td><?php echo htmlspecialchars($p['prenom'] . ' ' . $p['nom']); ?></td>
                        <td><?php echo htmlspecialchars($p['email']); ?></td>
                        <td><?php echo htmlspecialchars($p['telephone']); ?></td>
                        <td><span class="badge <?php echo $badge_class; ?>"><?php echo ucfirst($p['statut']); ?></span></td>
                        <td class="table-actions">
                            <a href="index.php?page=vue_insert_proprio&id=<?php echo $p['idproprio']; ?>"
                               class="btn btn-warning btn-sm">✏ Modifier</a>
                            <form method="POST" action="index.php?page=gestion_proprio" style="display:inline">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="<?php echo $p['idproprio']; ?>">
                                <button type="submit" class="btn btn-danger btn-sm"
                                        onclick="return confirm('ATTENTION : supprimer ce propriétaire supprime aussi ses gîtes. Continuer ?')">
                                    🗑 Supprimer
                                </button>
                            </form>
                        </td>
                    </tr>
                    <?php endforeach; ?>
                <?php endif; ?>
            </tbody>
        </table>
    </div>

<?php require '_layout_footer.php'; ?>
