<?php
$reservations  = $donnees       ?? [];
$message       = $message       ?? '';
$filters       = $filters       ?? ['idclient' => null, 'idgite' => null];
$clients_list  = $clients_list  ?? [];
$gites_list    = $gites_list    ?? [];
$peut_modifier = $peut_modifier ?? false;
$page_title    = 'Réservations';
require '_layout_header.php';

$today      = (new DateTime())->format('Y-m-d');
$cout_total = array_sum(array_column($reservations, 'prix'));

// Détermine le badge et le libellé selon la date réelle ou le statut forcé
function getStatutBadge(array $r, string $today): array {
    if ($r['statut_r'] === 'à valider') return ['badge-avalider', 'À valider'];
    if ($r['datefin'] < $today)         return ['badge-termine',  'Terminée'];
    if ($r['datedebut'] <= $today)      return ['badge-encours',  'En cours'];
    return ['badge-planifie', 'Planifiée'];
}
?>

    <div class="page-title">📅 Gestion des Réservations</div>

    <?php if ($message): ?>
        <div class="alert alert-success">✔ <?php echo htmlspecialchars($message); ?></div>
    <?php endif; ?>

    <div class="action-bar">
        <a href="index.php?page=home" class="btn btn-outline">← Accueil</a>
        <a href="index.php?page=vue_insert_reservation" class="btn btn-success">+ Ajouter</a>
        <span style="margin-left:auto;color:var(--c-muted);font-size:.85rem"><?php echo count($reservations); ?> réservation(s)</span>
    </div>

    <?php if ($peut_modifier): ?>
    <div class="filter-bar">
        <form method="GET" action="index.php" style="display:contents">
            <input type="hidden" name="page" value="gestion_reservation">
            <div class="form-group">
                <label for="filter_client">Client</label>
                <select id="filter_client" name="filter_client">
                    <option value="">Tous</option>
                    <?php foreach ($clients_list as $c): ?>
                        <option value="<?php echo $c['idclient']; ?>"
                            <?php echo ($filters['idclient'] == $c['idclient']) ? 'selected' : ''; ?>>
                            <?php echo htmlspecialchars($c['prenom'] . ' ' . $c['nom']); ?>
                        </option>
                    <?php endforeach; ?>
                </select>
            </div>
            <div class="form-group">
                <label for="filter_gite">Gîte</label>
                <select id="filter_gite" name="filter_gite">
                    <option value="">Tous</option>
                    <?php foreach ($gites_list as $g): ?>
                        <option value="<?php echo $g['idgite']; ?>"
                            <?php echo ($filters['idgite'] == $g['idgite']) ? 'selected' : ''; ?>>
                            <?php echo htmlspecialchars($g['adresse']); ?>
                        </option>
                    <?php endforeach; ?>
                </select>
            </div>
            <div style="display:flex;gap:.5rem;align-items:flex-end">
                <button type="submit" class="btn btn-primary">🔍 Filtrer</button>
                <a href="index.php?page=gestion_reservation" class="btn btn-outline">✕</a>
            </div>
        </form>
    </div>
    <?php endif; ?>

    <div class="table-wrapper">
        <table>
            <thead>
                <tr>
                    <th>#</th><th>Client</th><th>Gîte</th><th>Dates</th><th>Durée</th>
                    <th>Prix</th><th>Transport</th><th>Assurance</th><th>Statut</th>
                    <?php if ($peut_modifier): ?><th>Actions</th><?php endif; ?>
                </tr>
            </thead>
            <tbody>
                <?php if (empty($reservations)): ?>
                    <tr><td colspan="<?php echo $peut_modifier ? 10 : 9; ?>">
                        <div class="empty-state"><div class="empty-icon">📅</div><p>Aucune réservation trouvée.</p></div>
                    </td></tr>
                <?php else: ?>
                    <?php foreach ($reservations as $r):
                        $duree = (new DateTime($r['datedebut']))->diff(new DateTime($r['datefin']))->days;
                        [$badge, $label] = getStatutBadge($r, $today);
                    ?>
                    <tr>
                        <td><strong><?php echo $r['idreservation']; ?></strong></td>
                        <td><?php echo htmlspecialchars($r['client_prenom'] . ' ' . $r['client_nom']); ?></td>
                        <td><?php echo htmlspecialchars($r['gite_adresse']); ?></td>
                        <td>
                            <?php echo date('d/m/Y', strtotime($r['datedebut'])); ?><br>
                            <small style="color:var(--c-muted)">→ <?php echo date('d/m/Y', strtotime($r['datefin'])); ?></small>
                        </td>
                        <td><?php echo $duree; ?> j.</td>
                        <td><strong><?php echo number_format($r['prix'], 2, ',', ' '); ?> €</strong></td>
                        <td><?php echo ucfirst($r['transport'] ?? '—'); ?></td>
                        <td>
                            <?php if ($r['assurance'] == 1): ?>
                                <span class="badge badge-public">Oui</span>
                            <?php else: ?>
                                <span style="color:var(--c-muted);font-size:.78rem">Non</span>
                            <?php endif; ?>
                        </td>
                        <td><span class="badge <?php echo $badge; ?>"><?php echo $label; ?></span></td>
                        <?php if ($peut_modifier): ?>
                        <td class="table-actions">
                            <a href="index.php?page=vue_insert_reservation&id=<?php echo $r['idreservation']; ?>" class="btn btn-warning btn-sm">✏</a>
                            <form method="POST" action="index.php?page=gestion_reservation" style="display:inline">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="<?php echo $r['idreservation']; ?>">
                                <button type="submit" class="btn btn-danger btn-sm"
                                        onclick="return confirm('Supprimer cette réservation ?')">🗑</button>
                            </form>
                        </td>
                        <?php endif; ?>
                    </tr>
                    <?php endforeach; ?>

                    <!-- Ligne coût total -->
                    <tr class="total-row">
                        <td colspan="5" style="text-align:right;padding-right:1rem;font-size:.82rem">
                            Total — <?php echo count($reservations); ?> réservation<?php echo count($reservations) > 1 ? 's' : ''; ?>
                        </td>
                        <td colspan="<?php echo $peut_modifier ? 5 : 4; ?>" style="font-size:1.05rem">
                            <?php echo number_format($cout_total, 2, ',', ' '); ?> €
                        </td>
                    </tr>
                <?php endif; ?>
            </tbody>
        </table>
    </div>

<?php require '_layout_footer.php'; ?>
