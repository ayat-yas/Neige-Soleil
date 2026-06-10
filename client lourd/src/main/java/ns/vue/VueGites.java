package ns.vue;

import ns.controleur.AppControleur;
import ns.modele.Gite;
import ns.modele.Proprietaire;
import ns.util.Style;
import ns.util.UIFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VueGites extends JPanel {

    private final AppControleur controleur;
    private JTable            table;
    private DefaultTableModel model;
    private List<Gite>        liste;

    private JTextField        tfAdresse, tfSurface, tfPieces, tfLoyer, tfRecherche;
    private JComboBox<Proprietaire> cbProprio;
    private int idSelectionne = -1;

    public VueGites(AppControleur controleur) {
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
        header.add(UIFactory.labelTitre("🏡 Gestion des Gîtes"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePanel(), formPanel());
        split.setDividerLocation(640);
        split.setResizeWeight(0.65);
        split.setBorder(null);
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

        String[] cols = {"ID", "Adresse", "Surface (m²)", "Pièces", "Loyer (€/j)", "Propriétaire"};
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

        JPanel form = UIFactory.groupPanel("Détails du gîte");
        form.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 6, 5, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        tfAdresse = UIFactory.field(16); tfSurface = UIFactory.field(8);
        tfPieces  = UIFactory.field(8);  tfLoyer   = UIFactory.field(8);
        cbProprio = UIFactory.combo();

        Object[][] ch = {{"Adresse *", tfAdresse}, {"Surface m² *", tfSurface},
                         {"Pièces *", tfPieces}, {"Loyer €/j *", tfLoyer}, {"Propriétaire *", cbProprio}};
        for (int i = 0; i < ch.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0.38;
            form.add(UIFactory.label((String) ch[i][0]), gc);
            gc.gridx = 1; gc.weightx = 0.62;
            form.add((Component) ch[i][1], gc);
        }
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
        new SwingWorker<Void, Void>() {
            List<Proprietaire> proprios;
            @Override protected Void doInBackground() throws Exception {
                liste    = controleur.getGites();
                proprios = controleur.getProprietaires();
                return null;
            }
            @Override protected void done() {
                try {
                    get();
                    cbProprio.removeAllItems();
                    if (proprios != null) proprios.forEach(cbProprio::addItem);
                    majTableau();
                } catch (Exception ex) { UIFactory.erreur(VueGites.this, ex.getMessage()); }
            }
        }.execute();
    }

    private void rechercher() {
        String r = tfRecherche.getText().trim();
        if (r.isEmpty()) { charger(); return; }
        new SwingWorker<List<Gite>, Void>() {
            @Override protected List<Gite> doInBackground() throws Exception { return controleur.rechercherGites(r); }
            @Override protected void done() {
                try { liste = get(); majTableau(); }
                catch (Exception ex) { UIFactory.erreur(VueGites.this, ex.getMessage()); }
            }
        }.execute();
    }

    private void majTableau() {
        model.setRowCount(0);
        if (liste == null) return;
        for (Gite g : liste)
            model.addRow(new Object[]{g.getIdgite(), g.getAdresse(), g.getSurface(), g.getNbpieces(), g.getLoyer(), g.getNomProprio()});
    }

    private void remplir() {
        int row = table.getSelectedRow();
        if (row < 0 || liste == null || row >= liste.size()) return;
        Gite g = liste.get(row);
        idSelectionne = g.getIdgite();
        tfAdresse.setText(g.getAdresse()); tfSurface.setText(String.valueOf(g.getSurface()));
        tfPieces.setText(String.valueOf(g.getNbpieces())); tfLoyer.setText(String.valueOf(g.getLoyer()));
        for (int i = 0; i < cbProprio.getItemCount(); i++)
            if (cbProprio.getItemAt(i).getIdproprio() == g.getIdproprio()) { cbProprio.setSelectedIndex(i); break; }
    }

    private void ajouter() {
        if (!valider()) return;
        try { if (controleur.ajouterGite(construire())) { UIFactory.succes(this, "Gîte ajouté."); vider(); charger(); } }
        catch (Exception ex) { UIFactory.erreur(this, ex.getMessage()); }
    }

    private void modifier() {
        if (idSelectionne < 0) { UIFactory.erreur(this, "Sélectionnez un gîte."); return; }
        if (!valider()) return;
        Gite g = construire(); g.setIdgite(idSelectionne);
        try { if (controleur.modifierGite(g)) { UIFactory.succes(this, "Modifié."); vider(); charger(); } }
        catch (Exception ex) { UIFactory.erreur(this, ex.getMessage()); }
    }

    private void supprimer() {
        if (idSelectionne < 0) { UIFactory.erreur(this, "Sélectionnez un gîte."); return; }
        if (!UIFactory.confirmer(this, "Supprimer ce gîte ? ⚠ Ses réservations seront aussi supprimées.")) return;
        try { if (controleur.supprimerGite(idSelectionne)) { UIFactory.succes(this, "Supprimé."); vider(); charger(); } }
        catch (Exception ex) { UIFactory.erreur(this, ex.getMessage()); }
    }

    private void vider() {
        idSelectionne = -1;
        tfAdresse.setText(""); tfSurface.setText(""); tfPieces.setText(""); tfLoyer.setText("");
        if (cbProprio.getItemCount() > 0) cbProprio.setSelectedIndex(0);
        table.clearSelection();
    }

    private boolean valider() {
        if (tfAdresse.getText().isBlank() || tfSurface.getText().isBlank() || tfPieces.getText().isBlank() || tfLoyer.getText().isBlank()) {
            UIFactory.erreur(this, "Tous les champs sont obligatoires."); return false;
        }
        try { Integer.parseInt(tfSurface.getText().trim()); Integer.parseInt(tfPieces.getText().trim()); Integer.parseInt(tfLoyer.getText().trim()); }
        catch (NumberFormatException e) { UIFactory.erreur(this, "Surface, Pièces et Loyer doivent être des entiers."); return false; }
        if (cbProprio.getSelectedItem() == null) { UIFactory.erreur(this, "Sélectionnez un propriétaire."); return false; }
        return true;
    }

    private Gite construire() {
        Proprietaire p = (Proprietaire) cbProprio.getSelectedItem();
        return new Gite(0, tfAdresse.getText().trim(),
            Integer.parseInt(tfSurface.getText().trim()),
            Integer.parseInt(tfPieces.getText().trim()),
            Integer.parseInt(tfLoyer.getText().trim()),
            p.getIdproprio());
    }
}
