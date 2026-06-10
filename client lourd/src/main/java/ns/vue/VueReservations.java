package ns.vue;

import ns.controleur.AppControleur;
import ns.modele.Client;
import ns.modele.Gite;
import ns.modele.Reservation;
import ns.util.Style;
import ns.util.UIFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class VueReservations extends JPanel {

    private final AppControleur controleur;
    private JTable            table;
    private DefaultTableModel model;
    private List<Reservation> liste;

    private JComboBox<Object> cbFiltreClient;
    private JComboBox<String> cbFiltreStatut;
    private int idSelectionne = -1;

    public VueReservations(AppControleur controleur) {
        this.controleur = controleur;
        initUI();
        charger();
    }

    private void initUI() {
        // Layout : NORTH=header+filtres, CENTER=tableau+boutons
        setLayout(new BorderLayout(0, 0));
        setBackground(Style.BG_DEEP);

        // ── Zone Nord : titre + filtres ──
        JPanel nord = new JPanel(new BorderLayout(0, 8));
        nord.setOpaque(false);
        nord.setBorder(BorderFactory.createEmptyBorder(16, 24, 8, 24));

        JLabel titre = UIFactory.labelTitre("📅 Gestion des Réservations");
        nord.add(titre, BorderLayout.NORTH);
        nord.add(filtresPanel(), BorderLayout.SOUTH);
        add(nord, BorderLayout.NORTH);

        // ── Zone Centre : tableau + boutons ──
        add(tableauPanel(), BorderLayout.CENTER);
    }

    private JPanel filtresPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        p.setBackground(Style.BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Style.C_BORDER, 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        cbFiltreClient = UIFactory.combo();
        cbFiltreClient.setPreferredSize(new Dimension(220, 28));
        cbFiltreStatut = UIFactory.combo();
        cbFiltreStatut.setPreferredSize(new Dimension(150, 28));
        cbFiltreStatut.addItem("Tous les statuts");
        cbFiltreStatut.addItem("en cours");
        cbFiltreStatut.addItem("terminé");
        cbFiltreStatut.addItem("non réservé");
        cbFiltreStatut.addItem("à valider");

        JButton btnF = UIFactory.btnPrimary("🔍 Filtrer");
        JButton btnR = UIFactory.btnOutline("✕ Tout");
        btnF.addActionListener(e -> filtrer());
        btnR.addActionListener(e -> { cbFiltreClient.setSelectedIndex(0); cbFiltreStatut.setSelectedIndex(0); charger(); });

        p.add(UIFactory.label("Client :")); p.add(cbFiltreClient);
        p.add(UIFactory.label("Statut :")); p.add(cbFiltreStatut);
        p.add(btnF); p.add(btnR);
        return p;
    }

    private JPanel tableauPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Style.BG_DEEP);
        p.setBorder(BorderFactory.createEmptyBorder(4, 24, 16, 24));

        String[] cols = {"ID", "Client", "Gîte", "Début", "Fin", "Durée (j)", "Prix (€)", "Transport", "Assurance", "Statut"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UIFactory.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                idSelectionne = (row >= 0 && liste != null && row < liste.size())
                    ? liste.get(row).getIdreservation() : -1;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Style.BG_CARD);
        p.add(scroll, BorderLayout.CENTER);

        // Boutons sous le tableau
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        btns.setBackground(Style.BG_DEEP);
        JButton bA = UIFactory.btnSuccess("+ Nouvelle");
        JButton bM = UIFactory.btnWarning("✏ Modifier");
        JButton bS = UIFactory.btnDanger("🗑 Supprimer");
        JButton bR = UIFactory.btnOutline("↻ Actualiser");
        bA.addActionListener(e -> ouvrirDialog(null));
        bM.addActionListener(e -> modifierSelectionne());
        bS.addActionListener(e -> supprimer());
        bR.addActionListener(e -> charger());
        btns.add(bA); btns.add(bM); btns.add(bS); btns.add(bR);
        p.add(btns, BorderLayout.SOUTH);
        return p;
    }

    void charger() {
        new SwingWorker<Void, Void>() {
            List<Client> clients;
            @Override protected Void doInBackground() throws Exception {
                liste   = controleur.getReservations();
                clients = controleur.getClients();
                return null;
            }
            @Override protected void done() {
                try {
                    get();
                    cbFiltreClient.removeAllItems();
                    cbFiltreClient.addItem("Tous les clients");
                    if (clients != null) clients.forEach(cbFiltreClient::addItem);
                    majTableau();
                } catch (Exception ex) { UIFactory.erreur(VueReservations.this, ex.getMessage()); }
            }
        }.execute();
    }

    private void filtrer() {
        Object clientObj = cbFiltreClient.getSelectedItem();
        String statut    = cbFiltreStatut.getSelectedItem().toString();
        Integer idClient = (clientObj instanceof Client) ? ((Client) clientObj).getIdclient() : null;

        new SwingWorker<List<Reservation>, Void>() {
            @Override protected List<Reservation> doInBackground() throws Exception {
                if (idClient != null) return controleur.getReservationsByClient(idClient);
                if (!"Tous les statuts".equals(statut)) return controleur.getReservationsByStatut(statut);
                return controleur.getReservations();
            }
            @Override protected void done() {
                try { liste = get(); majTableau(); }
                catch (Exception ex) { UIFactory.erreur(VueReservations.this, ex.getMessage()); }
            }
        }.execute();
    }

    // Calcule le statut visuel réel selon la date — cohérent avec le client léger
    private String statutVisuel(Reservation r) {
        if ("à valider".equals(r.getStatut_r())) return "À valider";
        LocalDate today = LocalDate.now();
        if (r.getDatefin().isBefore(today))                                      return "Terminée";
        if (!r.getDatedebut().isAfter(today) && !r.getDatefin().isBefore(today)) return "En cours";
        return "Planifiée";
    }

    private void majTableau() {
        model.setRowCount(0);
        if (liste == null) return;
        for (Reservation r : liste) {
            model.addRow(new Object[]{
                r.getIdreservation(), r.getNomClient(), r.getAdresseGite(),
                r.getDatedebut(), r.getDatefin(), r.getDuree(),
                r.getPrix() + " €", r.getTransport(),
                r.isAssurance() ? "Oui" : "Non",
                statutVisuel(r)
            });
        }
    }

    private void modifierSelectionne() {
        if (idSelectionne < 0) { UIFactory.erreur(this, "Sélectionnez une réservation."); return; }
        Reservation r = liste.stream().filter(x -> x.getIdreservation() == idSelectionne).findFirst().orElse(null);
        ouvrirDialog(r);
    }

    private void supprimer() {
        if (idSelectionne < 0) { UIFactory.erreur(this, "Sélectionnez une réservation."); return; }
        if (!UIFactory.confirmer(this, "Supprimer la réservation #" + idSelectionne + " ?")) return;
        try {
            if (controleur.supprimerReservation(idSelectionne)) { UIFactory.succes(this, "Supprimée."); idSelectionne = -1; charger(); }
        } catch (Exception ex) { UIFactory.erreur(this, ex.getMessage()); }
    }

    private void ouvrirDialog(Reservation resa) {
        boolean isModif = (resa != null);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            isModif ? "Modifier la réservation" : "Nouvelle réservation", true);
        dialog.getContentPane().setBackground(Style.BG_CARD);
        dialog.setLayout(new BorderLayout(0, 8));

        List<Client> clients; List<Gite> gites;
        try { clients = controleur.getClients(); gites = controleur.getGites(); }
        catch (Exception ex) { UIFactory.erreur(this, ex.getMessage()); return; }

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Style.BG_CARD);
        form.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        JComboBox<Client> cbClient   = UIFactory.combo(); clients.forEach(cbClient::addItem);
        JComboBox<Gite>   cbGite     = UIFactory.combo(); gites.forEach(cbGite::addItem);
        JTextField tfDebut           = UIFactory.field(12); tfDebut.setToolTipText("YYYY-MM-DD");
        JTextField tfFin             = UIFactory.field(12); tfFin.setToolTipText("YYYY-MM-DD");
        JTextField tfPrix            = UIFactory.field(10);
        JComboBox<String> cbTransport = UIFactory.combo();
        cbTransport.addItem("voiture"); cbTransport.addItem("train"); cbTransport.addItem("avion");
        JCheckBox chkAssurance       = new JCheckBox("Oui"); chkAssurance.setBackground(Style.BG_CARD); chkAssurance.setForeground(Style.C_TEXT);
        JComboBox<String> cbStatut   = UIFactory.combo();
        cbStatut.addItem("à valider"); cbStatut.addItem("en cours"); cbStatut.addItem("terminé"); cbStatut.addItem("non réservé");
        JTextArea taRapport          = UIFactory.textArea(3, 28);

        if (isModif) {
            for (int i = 0; i < cbClient.getItemCount(); i++) if (cbClient.getItemAt(i).getIdclient() == resa.getIdclient()) { cbClient.setSelectedIndex(i); break; }
            for (int i = 0; i < cbGite.getItemCount(); i++)   if (cbGite.getItemAt(i).getIdgite() == resa.getIdgite())       { cbGite.setSelectedIndex(i); break; }
            tfDebut.setText(resa.getDatedebut().toString()); tfFin.setText(resa.getDatefin().toString());
            tfPrix.setText(String.valueOf(resa.getPrix())); cbTransport.setSelectedItem(resa.getTransport());
            chkAssurance.setSelected(resa.isAssurance()); cbStatut.setSelectedItem(resa.getStatut_r());
            if (resa.getRapport() != null) taRapport.setText(resa.getRapport());
        } else {
            tfDebut.setText(LocalDate.now().toString());
            tfFin.setText(LocalDate.now().plusDays(7).toString());
        }

        Object[][] ch = {{"Client *", cbClient}, {"Gîte *", cbGite},
            {"Date début * (YYYY-MM-DD)", tfDebut}, {"Date fin * (YYYY-MM-DD)", tfFin},
            {"Prix total (€) *", tfPrix}, {"Transport *", cbTransport},
            {"Assurance", chkAssurance}, {"Statut *", cbStatut},
            {"Rapport", new JScrollPane(taRapport)}};

        for (int i = 0; i < ch.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0.38;
            form.add(UIFactory.label((String) ch[i][0]), gc);
            gc.gridx = 1; gc.weightx = 0.62;
            form.add((Component) ch[i][1], gc);
        }

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        btns.setBackground(Style.BG_CARD);
        JButton btnOK  = isModif ? UIFactory.btnWarning("✏ Modifier") : UIFactory.btnSuccess("+ Ajouter");
        JButton btnAnn = UIFactory.btnOutline("Annuler");
        btnAnn.addActionListener(e -> dialog.dispose());

        btnOK.addActionListener(e -> {
            if (cbClient.getSelectedItem() == null || cbGite.getSelectedItem() == null ||
                tfDebut.getText().isBlank() || tfFin.getText().isBlank() || tfPrix.getText().isBlank()) {
                UIFactory.erreur(dialog, "Champs obligatoires manquants."); return;
            }
            try {
                LocalDate debut = LocalDate.parse(tfDebut.getText().trim());
                LocalDate fin   = LocalDate.parse(tfFin.getText().trim());
                if (!fin.isAfter(debut)) { UIFactory.erreur(dialog, "La date de fin doit être après le début."); return; }

                Reservation r = new Reservation();
                r.setIdclient(((Client) cbClient.getSelectedItem()).getIdclient());
                r.setIdgite(((Gite) cbGite.getSelectedItem()).getIdgite());
                r.setDatedebut(debut); r.setDatefin(fin);
                r.setPrix(Integer.parseInt(tfPrix.getText().trim()));
                r.setTransport((String) cbTransport.getSelectedItem());
                r.setAssurance(chkAssurance.isSelected());
                r.setStatut_r((String) cbStatut.getSelectedItem());
                r.setRapport(taRapport.getText().trim().isEmpty() ? null : taRapport.getText().trim());

                boolean ok;
                if (isModif) { r.setIdreservation(resa.getIdreservation()); ok = controleur.modifierReservation(r); }
                else ok = controleur.ajouterReservation(r);

                if (ok) { UIFactory.succes(dialog, isModif ? "Réservation modifiée." : "Réservation ajoutée."); dialog.dispose(); charger(); }
            } catch (java.time.format.DateTimeParseException ex) {
                UIFactory.erreur(dialog, "Format de date invalide (YYYY-MM-DD).");
            } catch (NumberFormatException ex) {
                UIFactory.erreur(dialog, "Le prix doit être un nombre entier.");
            } catch (Exception ex) {
                UIFactory.erreur(dialog, "Erreur : " + ex.getMessage());
            }
        });

        btns.add(btnOK); btns.add(btnAnn);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btns, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(520, dialog.getHeight()));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
