<?php
// vue/vue_insert_reservation.php
$donnee       = $donnee       ?? [];
$message      = $message      ?? '';
$clients_list = $clients_list ?? [];
$gites_list   = $gites_list   ?? [];
$mode         = isset($donnee['idreservation']) ? 'Modifier' : 'Ajouter';
$action       = isset($donnee['idreservation']) ? 'update'   : 'insert';
$page_title   = $mode . ' une réservation';
$estProprio   = in_array($_SESSION['user_statut'] ?? '', ['admin','privé','public'], true);
require '_layout_header.php';
?>

    <div class="page-title">📅 <?php echo $mode; ?> une Réservation</div>

    <?php if ($message): ?>
        <div class="alert <?php echo str_contains($message, 'Erreur') ? 'alert-danger' : 'alert-success'; ?>">
            <?php echo htmlspecialchars($message); ?>
        </div>
    <?php endif; ?>

    <div class="card">
        <div class="card-header">
            <span class="icon">📅</span>
            <h3><?php echo $mode; ?> une réservation</h3>
        </div>
        <div class="card-body">
            <form method="POST" action="index.php?page=vue_insert_reservation">
                <input type="hidden" name="action" value="<?php echo $action; ?>">
                <?php if (isset($donnee['idreservation'])): ?>
                    <input type="hidden" name="idreservation" value="<?php echo $donnee['idreservation']; ?>">
                <?php endif; ?>

                <div class="form-grid">

                    <!-- Client (proprio : liste déroulante / client : caché) -->
                    <?php if ($estProprio): ?>
                    <div class="form-group full">
                        <label for="idclient">Client *</label>
                        <select id="idclient" name="idclient" required>
                            <option value="">— Sélectionner un client —</option>
                            <?php foreach ($clients_list as $c): ?>
                                <option value="<?php echo $c['idclient']; ?>"
                                    <?php echo (isset($donnee['idclient']) && $donnee['idclient'] == $c['idclient']) ? 'selected' : ''; ?>>
                                    <?php echo htmlspecialchars($c['prenom'] . ' ' . $c['nom'] . ' — ' . $c['email']); ?>
                                </option>
                            <?php endforeach; ?>
                        </select>
                    </div>
                    <?php else: ?>
                        <input type="hidden" name="idclient" value="<?php echo $_SESSION['user_id']; ?>">
                    <?php endif; ?>

                    <!-- Gîte -->
                    <div class="form-group full">
                        <label for="idgite">Gîte *</label>
                        <select id="idgite" name="idgite" required>
                            <option value="">— Sélectionner un gîte —</option>
                            <?php foreach ($gites_list as $g): ?>
                                <option value="<?php echo $g['idgite']; ?>"
                                    <?php echo (isset($donnee['idgite']) && $donnee['idgite'] == $g['idgite']) ? 'selected' : ''; ?>>
                                    <?php echo htmlspecialchars($g['adresse'] . ' — ' . number_format($g['loyer'], 0, ',', ' ') . ' €/jour'); ?>
                                </option>
                            <?php endforeach; ?>
                        </select>
                    </div>

                    <!-- Dates -->
                    <div class="form-group">
                        <label for="datedebut">Date de début *</label>
                        <input type="date" id="datedebut" name="datedebut" required
                               value="<?php echo htmlspecialchars($donnee['datedebut'] ?? ''); ?>">
                    </div>
                    <div class="form-group">
                        <label for="datefin">Date de fin *</label>
                        <input type="date" id="datefin" name="datefin" required
                               value="<?php echo htmlspecialchars($donnee['datefin'] ?? ''); ?>">
                    </div>

                    <!-- Prix -->
                    <div class="form-group">
                        <label for="prix">Prix total (€) *</label>
                        <input type="number" step="0.01" id="prix" name="prix" min="0" required
                               value="<?php echo htmlspecialchars($donnee['prix'] ?? ''); ?>">
                    </div>

                    <!-- Transport -->
                    <div class="form-group">
                        <label for="transport">Transport *</label>
                        <select id="transport" name="transport" required>
                            <?php foreach (['voiture', 'train', 'avion'] as $t): ?>
                                <option value="<?php echo $t; ?>"
                                    <?php echo (isset($donnee['transport']) && $donnee['transport'] === $t) ? 'selected' : ''; ?>>
                                    <?php echo ucfirst($t); ?>
                                </option>
                            <?php endforeach; ?>
                        </select>
                    </div>

                    <!-- Assurance -->
                    <div class="form-group">
                        <label for="assurance">Assurance *</label>
                        <select id="assurance" name="assurance" required>
                            <option value="0" <?php echo (isset($donnee['assurance']) && $donnee['assurance'] == 0) ? 'selected' : ''; ?>>Non</option>
                            <option value="1" <?php echo (isset($donnee['assurance']) && $donnee['assurance'] == 1) ? 'selected' : ''; ?>>Oui</option>
                        </select>
                    </div>

                    <!-- Statut (proprio uniquement) -->
                    <?php if ($estProprio): ?>
                    <div class="form-group">
                        <label for="statut_r">Statut *</label>
                        <select id="statut_r" name="statut_r" required>
                            <?php foreach (['en cours', 'terminé', 'non réservé'] as $s): ?>
                                <option value="<?php echo $s; ?>"
                                    <?php echo (isset($donnee['statut_r']) && $donnee['statut_r'] === $s) ? 'selected' : ''; ?>>
                                    <?php echo ucfirst($s); ?>
                                </option>
                            <?php endforeach; ?>
                        </select>
                    </div>

                    <!-- Rapport (proprio uniquement) -->
                    <div class="form-group full">
                        <label for="rapport">Rapport / Notes</label>
                        <textarea id="rapport" name="rapport" rows="3"
                                  placeholder="Observations, incidents, notes…"><?php echo htmlspecialchars($donnee['rapport'] ?? ''); ?></textarea>
                    </div>
                    <?php else: ?>
                        <input type="hidden" name="statut_r" value="en cours">
                    <?php endif; ?>

                </div>

                <div class="form-actions">
                    <button type="submit" class="btn <?php echo $mode === 'Ajouter' ? 'btn-success' : 'btn-warning'; ?>">
                        <?php echo $mode === 'Ajouter' ? '+ Ajouter' : '✏ Modifier'; ?> la réservation
                    </button>
                    <a href="index.php?page=gestion_reservation" class="btn btn-outline">← Annuler</a>
                </div>
            </form>
        </div>
    </div>

<?php require '_layout_footer.php'; ?>
