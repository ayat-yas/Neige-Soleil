package vue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel generique avec barre de recherche + tableau + boutons CRUD.
 * Herite par PanelProprietaires, PanelClients, PanelGites, PanelReservations.
 */
public abstract class PanelGenerique extends JPanel {

    private static final long serialVersionUID = 1L;

    protected JTable            table;
    protected DefaultTableModel modelTable;
    protected JTextField        tfRecherche;
    protected JButton           btnAjouter, btnModifier, btnSupprimer, btnActualiser;
    protected int               idSelectionne = -1;

    public PanelGenerique(String titrePanel, String[] colonnes) {
        setLayout(new BorderLayout(0, 8));
        setBackground(new Color(0xf4f7fb));
        setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        // Titre
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel titre = new JLabel(titrePanel);
        titre.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titre.setForeground(new Color(0x1e3a5f));
        top.add(titre, BorderLayout.WEST);

        // Barre recherche
        JPanel barreRecherche = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        barreRecherche.setBackground(new Color(0xf4f7fb));
        barreRecherche.setBorder(BorderFactory.createLineBorder(new Color(0xe2e8f0)));

        tfRecherche = new JTextField(25);
        tfRecherche.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton btnSearch = creerBouton("Rechercher", new Color(0x2e6da4));
        JButton btnReset  = creerBouton("Reinitialiser", new Color(0x718096));

        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { rechercher(tfRecherche.getText().trim()); }
        });
        btnReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { tfRecherche.setText(""); charger(); }
        });

        barreRecherche.add(new JLabel("Recherche : "));
        barreRecherche.add(tfRecherche);
        barreRecherche.add(btnSearch);
        barreRecherche.add(btnReset);

        // Tableau
        modelTable = new DefaultTableModel(colonnes, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(modelTable);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setGridColor(new Color(0xe2e8f0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0x1e3a5f));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setReorderingAllowed(false);

        table.getSelectionModel().addListSelectionListener(
            new javax.swing.event.ListSelectionListener() {
                public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) onSelectionChanged();
                }
            }
        );

        // Boutons CRUD
        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        boutons.setBackground(Color.WHITE);
        boutons.setBorder(BorderFactory.createLineBorder(new Color(0xe2e8f0)));

        btnAjouter    = creerBouton("+ Ajouter",    new Color(0x2e9e5b));
        btnModifier   = creerBouton("Modifier",     new Color(0xe67e22));
        btnSupprimer  = creerBouton("Supprimer",    new Color(0xd63031));
        btnActualiser = creerBouton("Actualiser",   new Color(0x2e6da4));

        btnAjouter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { ajouter(); }
        });
        btnModifier.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (idSelectionne < 0) {
                    JOptionPane.showMessageDialog(PanelGenerique.this, "Selectionnez une ligne.");
                    return;
                }
                modifier();
            }
        });
        btnSupprimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (idSelectionne < 0) {
                    JOptionPane.showMessageDialog(PanelGenerique.this, "Selectionnez une ligne.");
                    return;
                }
                int rep = JOptionPane.showConfirmDialog(PanelGenerique.this,
                    "Supprimer cet element ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (rep == JOptionPane.YES_OPTION) supprimer();
            }
        });
        btnActualiser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { charger(); }
        });

        boutons.add(btnAjouter);
        boutons.add(btnModifier);
        boutons.add(btnSupprimer);
        boutons.add(btnActualiser);

        // Assemblage
        JPanel nord = new JPanel(new BorderLayout(0, 6));
        nord.setOpaque(false);
        nord.add(top,           BorderLayout.NORTH);
        nord.add(barreRecherche, BorderLayout.CENTER);

        add(nord,                          BorderLayout.NORTH);
        add(new JScrollPane(table),        BorderLayout.CENTER);
        add(boutons,                       BorderLayout.SOUTH);
    }

    protected JButton creerBouton(String texte, Color couleur) {
        JButton btn = new JButton(texte);
        btn.setBackground(couleur);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }

    protected void onSelectionChanged() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            idSelectionne = (int) modelTable.getValueAt(row, 0);
        } else {
            idSelectionne = -1;
        }
    }

    // Methodes abstraites implementees dans chaque panel
    protected abstract void charger();
    protected abstract void rechercher(String filtre);
    protected abstract void ajouter();
    protected abstract void modifier();
    protected abstract void supprimer();
}
