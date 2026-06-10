<?php
$clients    = $donnees ?? [];
$message    = $_GET['msg'] ?? ($message ?? '');
$page_title = 'Clients';
require '_layout_header.php';
?>

    <div class="page-title">👥 Gestion des Clients</div>

    <?php if ($message): ?>
        <div class="alert alert-success">✔ <?php echo htmlspecialchars($message); ?></div>
    <?php endif; ?>

    <div class="action-bar">
        <a href="index.php?page=home" class="btn btn-outline">← Accueil</a>
        <a href="index.php?page=vue_insert_client" class="btn btn-success">+ Ajouter un client</a>
        <span style="margin-left:auto;color:var(--c-muted);font-size:.85rem"><?php echo count($clients); ?> client(s)</span>
    </div>

    <div class="table-wrapper">
        <table>
            <thead>
                <tr>
                    <th>#</th><th>Nom & Prénom</th><th>Email</th><th>Téléphone</th><th>Adresse</th><th>Catégorie</th><th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <?php if (empty($clients)): ?>
                    <tr><td colspan="7">
                        <div class="empty-state"><div class="empty-icon">👤</div><p>Aucun client enregistré.</p></div>
                    </td></tr>
                <?php else: foreach ($clients as $c): ?>
                    <tr>
                        <td><strong><?php echo $c['idclient']; ?></strong></td>
                        <td><?php echo htmlspecialchars($c['prenom'] . ' ' . $c['nom']); ?></td>
                        <td><?php echo htmlspecialchars($c['email']); ?></td>
                        <td><?php echo htmlspecialchars($c['telephone']); ?></td>
                        <td><?php echo htmlspecialchars($c['adresse']); ?></td>
                        <td>
                            <?php if (!empty($c['idcategorie'])): ?>
                                <span class="badge badge-prive">#<?php echo $c['idcategorie']; ?></span>
                            <?php else: ?>
                                <span style="color:var(--c-muted);font-size:.8rem">—</span>
                            <?php endif; ?>
                        </td>
                        <td class="table-actions">
                            <a href="index.php?page=vue_insert_client&id=<?php echo $c['idclient']; ?>" class="btn btn-warning btn-sm">✏</a>
                            <form method="POST" action="index.php?page=gestion_client" style="display:inline">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="<?php echo $c['idclient']; ?>">
                                <button type="submit" class="btn btn-danger btn-sm"
                                        onclick="return confirm('Supprimer ce client et ses réservations ?')">🗑</button>
                            </form>
                        </td>
                    </tr>
                <?php endforeach; endif; ?>
            </tbody>
        </table>
    </div>

<?php require '_layout_footer.php'; ?>
