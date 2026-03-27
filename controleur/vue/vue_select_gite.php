<?php
// vue/vue_select_gite.php
$gites   = $donnees ?? [];
$message = $_GET['msg'] ?? ($message ?? '');
$page_title = 'Gîtes';
require '_layout_header.php';
?>

    <div class="page-title">🏡 Gestion des Gîtes</div>

    <?php if ($message): ?>
        <div class="alert alert-success">✔ <?php echo htmlspecialchars($message); ?></div>
    <?php endif; ?>

    <div class="action-bar">
        <a href="index.php?page=home" class="btn btn-outline">← Accueil</a>
        <?php if (in_array($_SESSION['user_statut'] ?? '', ['admin','privé','public'], true)): ?>
        <a href="index.php?page=vue_insert_gite" class="btn btn-success">+ Ajouter un gîte</a>
        <?php endif; ?>
        <span style="margin-left:auto;color:var(--c-muted);font-size:.875rem"><?php echo count($gites); ?> gîte(s)</span>
    </div>

    <div class="table-wrapper">
        <table>
            <thead>
                <tr>
                    <th>#</th>
                    <th>Adresse</th>
                    <th>Surface</th>
                    <th>Pièces</th>
                    <th>Loyer / jour</th>
                    <th>Proprio #</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <?php if (empty($gites)): ?>
                    <tr><td colspan="7">
                        <div class="empty-state">
                            <div class="empty-icon">🏠</div>
                            <p>Aucun gîte enregistré.</p>
                        </div>
                    </td></tr>
                <?php else: ?>
                    <?php foreach ($gites as $g): ?>
                    <tr>
                        <td><strong><?php echo $g['idgite']; ?></strong></td>
                        <td><?php echo htmlspecialchars($g['adresse']); ?></td>
                        <td><?php echo $g['surface']; ?> m²</td>
                        <td><?php echo $g['nbpieces']; ?></td>
                        <td><strong><?php echo number_format($g['loyer'], 0, ',', ' '); ?> €</strong></td>
                        <td><?php echo $g['idproprio']; ?></td>
                        <td class="table-actions">
                            <?php if (in_array($_SESSION['user_statut'] ?? '', ['admin','privé','public'], true)): ?>
                            <a href="index.php?page=vue_insert_gite&id=<?php echo $g['idgite']; ?>"
                               class="btn btn-warning btn-sm">✏ Modifier</a>
                            <form method="POST" action="index.php?page=gestion_gite" style="display:inline">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="<?php echo $g['idgite']; ?>">
                                <button type="submit" class="btn btn-danger btn-sm"
                                        onclick="return confirm('Supprimer ce gîte ? Ses réservations seront aussi supprimées.')">
                                    🗑 Supprimer
                                </button>
                            </form>
                            <?php else: ?>
                            <span class="badge badge-prive">Lecture seule</span>
                            <?php endif; ?>
                        </td>
                    </tr>
                    <?php endforeach; ?>
                <?php endif; ?>
            </tbody>
        </table>
    </div>

<?php require '_layout_footer.php'; ?>
