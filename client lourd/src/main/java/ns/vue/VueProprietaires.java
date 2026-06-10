package ns.vue;

import ns.controleur.AppControleur;
import ns.modele.Proprietaire;
import ns.util.Style;
import ns.util.UIFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VueProprietaires extends JPanel {

    private final AppControleur controleur;
    private JTable             table;
    private DefaultTableModel  model;
    private List<Proprietaire> liste;

    private JTextField     tfNom, tfPrenom, tfAdresse, tfEmail, tfTel, tfRecherche;
    private JPasswordField pfMdp;
    private JComboBox<String> cbStatut;
    private int idSelectionne = -1;

    public VueProprietaires(AppControleur controleur) {
        this.controleur = controleur;
        initUI();
        charger();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Style.BG_DEEP);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(16, 24, 8, 24));
        header.add(UIFactory.labelTitre("🔑 Gestion des Propriétaires"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePanel(), formPanel());
        split.setDividerLocation(640);
        split.setResizeWeight(0.65);
        split.setBorder(null);
        split.setBackground(Style.BG_DEEP);
        add(split, BorderLayout.CENTER);
    }

    private JPanel tablePanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Style.BG_DEEP);
        p.setBorder(BorderFactory.createEmptyBorder(0, 24, 16, 8));

        tfRecherche = UIFactory.field(20);
        JButton btnS = UIFactory.btnPrimary("Rechercher");
        JButton btnR = UIFactory.btnOutline("✕");
        btnS.addActionListener(e -> rechercher());
        btnR.addActionListener(e -> { tfRecherche.setText(""); charger(); });
        p.add(UIFactory.searchBar(tfRecherche, btnS, btnR), BorderLayout.NORTH);

        String[] cols = {"ID", "Nom", "Prénom", "Email", "Téléphone", "Statut"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UIFactory.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) remplir(); });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(Style.BG_CARD);
        scroll.getViewport().setBackground(Style.BG_CARD);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel formPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(Style.BG_DEEP);
        p.setBorder(BorderFactory.createEmptyBorder(0, 8, 16, 24));

        JPanel form = UIFactory.groupPanel("Détails du propriétaire");
        form.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 6, 5, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        tfNom = UIFactory.field(16); tfPrenom = UIFactory.field(16);
        tfAdresse = UIFactory.field(16); tfEmail = UIFactory.field(16);
        tfTel = UIFactory.field(16); pfMdp = UIFactory.passField(16);
        cbStatut = UIFactory.combo();
        cbStatut.addItem("privé"); cbStatut.addItem("public"); cbStatut.addItem("admin");

        Object[][] ch = {{"Nom *", tfNom}, {"Prénom *", tfPrenom}, {"Adresse *", tfAdresse},
                         {"Email *", tfEmail}, {"Téléphone *", tfTel}, {"Mot de passe", pfMdp}, {"Statut *", cbStatut}};
        for (int i = 0; i < ch.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0.38;
            form.add(UIFactory.label((String) ch[i][0]), gc);
            gc.gridx = 1; gc.weightx = 0.62;
            form.add((Component) ch[i][1], gc);
        }
        gc.gridx = 1; gc.gridy = ch.length;
        JLabel hint = new JLabel("Vide = inchangé (modification)");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        hint.setForeground(Style.C_MUTED);
        form.add(hint, gc);
        p.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new GridLayout(2, 2, 8, 8));
        btns.setBackground(Style.BG_DEEP);
        JButton bA = UIFactory.btnSuccess("+ Ajouter");
        JButton bM = UIFactory.btnWarning("✏ Modifier");
        JButton bS = UIFactory.btnDanger("🗑 Supprimer");
        JButton bV = UIFactory.btnOutline("✕ Vider");
        bA.addActionListener(e -> ajouter()); bM.addActionListener(e -> modifier());
        bS.addActionListener(e -> supprimer()); bV.addActionListener(e -> vider());
        btns.add(bA); btns.add(bM); btns.add(bS); btns.add(bV);
        p.add(btns, BorderLayout.SOUTH);
        return p;
    }

    void charger() {
        new SwingWorker<List<Proprietaire>, Void>() {
            @Override protected List<Proprietaire> doInBackground() throws Exception { return controleur.getProprietaires(); }
            @Override protected void done() {
                try { liste = get(); majTableau(); }
                catch (Exception ex) { UIFactory.erreur(VueProprietaires.this, ex.getMessage()); }
            }
        }.execute();
    }

    private void rechercher() {
        String r = tfRecherche.getText().trim();
        if (r.isEmpty()) { charger(); return; }
        new SwingWorker<List<Proprietaire>, Void>() {
            @Override protected List<Proprietaire> doInBackground() throws Exception { return controleur.rechercherProprietaires(r); }
            @Override protected void done() {
                try { liste = get(); majTableau(); }
                catch (Exception ex) { UIFactory.erreur(VueProprietaires.this, ex.getMessage()); }
            }
        }.execute();
    }

    private void majTableau() {
        model.setRowCount(0);
        if (liste == null) return;
        for (Proprietaire p : liste)
            model.addRow(new Object[]{p.getIdproprio(), p.getNom(), p.getPrenom(), p.getEmail(), p.getTelephone(), p.getStatut()});
    }

    private void remplir() {
        int row = table.getSelectedRow();
        if (row < 0 || liste == null || row >= liste.size()) return;
        Proprietaire p = liste.get(row);
        idSelectionne = p.getIdproprio();
        tfNom.setText(p.getNom()); tfPrenom.setText(p.getPrenom());
        tfAdresse.setText(p.getAdresse()); tfEmail.setText(p.getEmail());
        tfTel.setText(String.valueOf(p.getTelephone())); pfMdp.setText("");
        cbStatut.setSelectedItem(p.getStatut());
    }

    private void ajouter() {
        if (!valider(true)) return;
        try { if (controleur.ajouterProprietaire(construire())) { UIFactory.succes(this, "Propriétaire ajouté."); vider(); charger(); } }
        catch (Exception ex) { UIFactory.erreur(this, ex.getMessage()); }
    }

    private void modifier() {
        if (idSelectionne < 0) { UIFactory.erreur(this, "Sélectionnez un propriétaire."); return; }
        if (!valider(false)) return;
        Proprietaire p = construire(); p.setIdproprio(idSelectionne);
        try { if (controleur.modifierProprietaire(p)) { UIFactory.succes(this, "Modifié."); vider(); charger(); } }
        catch (Exception ex) { UIFactory.erreur(this, ex.getMessage()); }
    }

    private void supprimer() {
        if (idSelectionne < 0) { UIFactory.erreur(this, "Sélectionnez un propriétaire."); return; }
        if (!UIFactory.confirmer(this, "Supprimer ? ⚠ Ses gîtes seront aussi supprimés.")) return;
        try { if (controleur.supprimerProprietaire(idSelectionne)) { UIFactory.succes(this, "Supprimé."); vider(); charger(); } }
        catch (Exception ex) { UIFactory.erreur(this, ex.getMessage()); }
    }

    private void vider() {
        idSelectionne = -1;
        tfNom.setText(""); tfPrenom.setText(""); tfAdresse.setText("");
        tfEmail.setText(""); tfTel.setText(""); pfMdp.setText("");
        cbStatut.setSelectedIndex(0); table.clearSelection();
    }

    private boolean valider(boolean mdpObli) {
        if (tfNom.getText().isBlank() || tfPrenom.getText().isBlank() || tfAdresse.getText().isBlank() ||
            tfEmail.getText().isBlank() || tfTel.getText().isBlank()) {
            UIFactory.erreur(this, "Champs obligatoires manquants."); return false;
        }
        if (mdpObli && new String(pfMdp.getPassword()).isBlank()) {
            UIFactory.erreur(this, "Mot de passe obligatoire à la création."); return false;
        }
        try { Long.parseLong(tfTel.getText().trim()); }
        catch (NumberFormatException e) { UIFactory.erreur(this, "Téléphone invalide."); return false; }
        return true;
    }

    private Proprietaire construire() {
        Proprietaire p = new Proprietaire();
        p.setNom(tfNom.getText().trim()); p.setPrenom(tfPrenom.getText().trim());
        p.setAdresse(tfAdresse.getText().trim()); p.setEmail(tfEmail.getText().trim());
        p.setTelephone(Long.parseLong(tfTel.getText().trim()));
        p.setMdp(new String(pfMdp.getPassword()));
        p.setStatut((String) cbStatut.getSelectedItem());
        return p;
    }
}
