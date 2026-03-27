<?php
// vue/home.php
$page_title     = 'Accueil';
$statut         = $_SESSION['user_statut'] ?? 'client';
$nom            = $_SESSION['user_nom']    ?? 'Visiteur';
$stats          = $stats ?? [];
$estProprio     = in_array($statut, ['admin', 'privé', 'public'], true);
require '_layout_header.php';
?>

    <div class="page-title">
        🏠 Tableau de bord
    </div>

    <!-- Stats (propriétaires uniquement) -->
    <?php if ($estProprio && !empty($stats)): ?>
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-icon">👥</div>
            <div class="stat-info">
                <div class="value"><?php echo $stats['nb_clients']; ?></div>
                <div class="label">Clients</div>
            </div>
        </div>
        <div class="stat-card accent">
            <div class="stat-icon">🏡</div>
            <div class="stat-info">
                <div class="value"><?php echo $stats['nb_gites']; ?></div>
                <div class="label">Gîtes</div>
            </div>
        </div>
        <div class="stat-card success">
            <div class="stat-icon">📅</div>
            <div class="stat-info">
                <div class="value"><?php echo $stats['nb_reservations']; ?></div>
                <div class="label">Réservations</div>
            </div>
        </div>
        <div class="stat-card danger">
            <div class="stat-icon">💶</div>
            <div class="stat-info">
                <div class="value"><?php echo number_format($stats['chiffre'], 0, ',', ' '); ?> €</div>
                <div class="label">Chiffre d'affaires</div>
            </div>
        </div>
    </div>
    <?php endif; ?>

    <!-- Menu navigation -->
    <div class="card" style="margin-bottom:2rem">
        <div class="card-header">
            <span class="icon">🗂</span>
            <h3>Actions disponibles</h3>
        </div>
        <div class="card-body">
            <div class="menu-grid">
                <a href="index.php?page=gestion_gite" class="menu-item">
                    <img src="images/gite_house.png" alt="Gîtes">
                    Gérer les Gîtes
                </a>
                <a href="index.php?page=gestion_reservation" class="menu-item">
                    <img src="images/reservation_calendar.png" alt="Réservations">
                    Gérer les Réservations
                </a>
                <?php if ($estProprio): ?>
                <a href="index.php?page=gestion_client" class="menu-item admin-only">
                    <img src="images/client_icone.png" alt="Clients">
                    Gérer les Clients
                </a>
                <a href="index.php?page=gestion_proprio" class="menu-item admin-only">
                    <img src="images/proprio_user.png" alt="Propriétaires">
                    Gérer les Propriétaires
                </a>
                <?php endif; ?>
            </div>
        </div>
    </div>

    <!-- Réservations récentes (proprio uniquement) -->
    <?php if ($estProprio && !empty($stats['recentes'])): ?>
    <div class="card">
        <div class="card-header">
            <span class="icon">🕐</span>
            <h3>Réservations récentes</h3>
        </div>
        <div class="card-body" style="padding:0">
            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Client</th>
                            <th>Gîte</th>
                            <th>Dates</th>
                            <th>Prix</th>
                            <th>Statut</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php foreach ($stats['recentes'] as $r): ?>
                        <tr>
                            <td><?php echo $r['idreservation']; ?></td>
                            <td><?php echo htmlspecialchars($r['client_prenom'] . ' ' . $r['client_nom']); ?></td>
                            <td><?php echo htmlspecialchars($r['gite_adresse']); ?></td>
                            <td><?php echo date('d/m/Y', strtotime($r['datedebut'])); ?> → <?php echo date('d/m/Y', strtotime($r['datefin'])); ?></td>
                            <td><strong><?php echo number_format($r['prix'], 2, ',', ' '); ?> €</strong></td>
                            <td>
                                <?php
                                $badge_class = match($r['statut_r']) {
                                    'en cours' => 'badge-encours',
                                    'terminé'  => 'badge-termine',
                                    default    => 'badge-nonresa',
                                };
                                ?>
                                <span class="badge <?php echo $badge_class; ?>"><?php echo ucfirst($r['statut_r']); ?></span>
                            </td>
                        </tr>
                        <?php endforeach; ?>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <?php endif; ?>

<?php require '_layout_footer.php'; ?>
