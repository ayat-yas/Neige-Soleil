package ns.vue;

import ns.controleur.AppControleur;
import ns.modele.Client;
import ns.util.Style;
import ns.util.UIFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VueClients extends JPanel {

    private final AppControleur controleur;
    private JTable            table;
    private DefaultTableModel model;
    private List<Client>      liste;

    private JTextField     tfNom, tfPrenom, tfAdresse, tfEmail, tfTel, tfRecherche;
    private JPasswordField pfMdp;
    private JComboBox<String> cbCategorie;
    private int idSelectionne = -1;

    // Libellés des catégories (index = idcategorie - 1)
    private static final String[] CATEGORIES = {"", "Particulier", "Groupes", "Entreprise", "Autre"};

    public VueClients(AppControleur controleur) {
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
        header.add(UIFactory.labelTitre("👥 Gestion des Clients"), BorderLayout.WEST);
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

        String[] cols = {"ID", "Nom", "Prénom", "Email", "Téléphone", "Catégorie"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UIFactory.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) remplir(); });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Style.BG_CARD);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel formPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(Style.BG_DEEP);
        p.setBorder(BorderFactory.createEmptyBorder(0, 8, 16, 24));

        JPanel form = UIFactory.groupPanel("Détails du client");
        form.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 6, 5, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        tfNom = UIFactory.field(16); tfPrenom = UIFactory.field(16);
        tfAdresse = UIFactory.field(16); tfEmail = UIFactory.field(16);
        tfTel = UIFactory.field(16); pfMdp = UIFactory.passField(16);
        cbCategorie = UIFactory.combo();
        cbCategorie.addItem("— Aucune —");
        cbCategorie.addItem("Particulier");
        cbCategorie.addItem("Groupes");
        cbCategorie.addItem("Entreprise");
        cbCategorie.addItem("Autre");

        Object[][] ch = {{"Nom *", tfNom}, {"Prénom *", tfPrenom}, {"Adresse *", tfAdresse},
                         {"Email *", tfEmail}, {"Téléphone *", tfTel},
                         {"Mot de passe", pfMdp}, {"Catégorie", cbCategorie}};
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
        new SwingWorker<List<Client>, Void>() {
            @Override protected List<Client> doInBackground() throws Exception { return controleur.getClients(); }
            @Override protected void done() {
                try { liste = get(); majTableau(); }
                catch (Exception ex) { UIFactory.erreur(VueClients.this, ex.getMessage()); }
            }
        }.execute();
    }

    private void rechercher() {
        String r = tfRecherche.getText().trim();
        if (r.isEmpty()) { charger(); return; }
        new SwingWorker<List<Client>, Void>() {
            @Override protected List<Client> doInBackground() throws Exception { return controleur.rechercherClients(r); }
            @Override protected void done() {
                try { liste = get(); majTableau(); }
                catch (Exception ex) { UIFactory.erreur(VueClients.this, ex.getMessage()); }
            }
        }.execute();
    }

    private void majTableau() {
        model.setRowCount(0);
        if (liste == null) return;
        for (Client c : liste) {
            String cat = (c.getIdcategorie() != null && c.getIdcategorie() < CATEGORIES.length)
                ? CATEGORIES[c.getIdcategorie()] : "—";
            model.addRow(new Object[]{c.getIdclient(), c.getNom(), c.getPrenom(), c.getEmail(), c.getTelephone(), cat});
        }
    }

    private void remplir() {
        int row = table.getSelectedRow();
        if (row < 0 || liste == null || row >= liste.size()) return;
        Client c = liste.get(row);
        idSelectionne = c.getIdclient();
        tfNom.setText(c.getNom()); tfPrenom.setText(c.getPrenom());
        tfAdresse.setText(c.getAdresse()); tfEmail.setText(c.getEmail());
        tfTel.setText(String.valueOf(c.getTelephone())); pfMdp.setText("");
        // Sélectionner la catégorie (index 0 = aucune, sinon idcategorie)
        cbCategorie.setSelectedIndex(c.getIdcategorie() != null ? c.getIdcategorie() : 0);
    }

    private void ajouter() {
        if (!valider(true)) return;
        try { if (controleur.ajouterClient(construire())) { UIFactory.succes(this, "Client ajouté."); vider(); charger(); } }
        catch (Exception ex) { UIFactory.erreur(this, ex.getMessage()); }
    }

    private void modifier() {
        if (idSelectionne < 0) { UIFactory.erreur(this, "Sélectionnez un client."); return; }
        if (!valider(false)) return;
        Client c = construire(); c.setIdclient(idSelectionne);
        try { if (controleur.modifierClient(c)) { UIFactory.succes(this, "Modifié."); vider(); charger(); } }
        catch (Exception ex) { UIFactory.erreur(this, ex.getMessage()); }
    }

    private void supprimer() {
        if (idSelectionne < 0) { UIFactory.erreur(this, "Sélectionnez un client."); return; }
        if (!UIFactory.confirmer(this, "Supprimer ce client et ses réservations ?")) return;
        try { if (controleur.supprimerClient(idSelectionne)) { UIFactory.succes(this, "Supprimé."); vider(); charger(); } }
        catch (Exception ex) { UIFactory.erreur(this, ex.getMessage()); }
    }

    private void vider() {
        idSelectionne = -1;
        tfNom.setText(""); tfPrenom.setText(""); tfAdresse.setText("");
        tfEmail.setText(""); tfTel.setText(""); pfMdp.setText("");
        cbCategorie.setSelectedIndex(0); table.clearSelection();
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

    private Client construire() {
        Client c = new Client();
        c.setNom(tfNom.getText().trim()); c.setPrenom(tfPrenom.getText().trim());
        c.setAdresse(tfAdresse.getText().trim()); c.setEmail(tfEmail.getText().trim());
        c.setTelephone(Long.parseLong(tfTel.getText().trim()));
        c.setMdp(new String(pfMdp.getPassword()));
        // index 0 = aucune, index 1-4 = idcategorie 1-4
        int idx = cbCategorie.getSelectedIndex();
        c.setIdcategorie(idx > 0 ? idx : null);
        return c;
    }
}
