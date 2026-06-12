package vue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import controleur.Controleur;
import modele.Reservation;

/**
 * Panel Tableau de bord — statistiques en temps reel.
 */
public class PanelDashboard extends JPanel {

    private static final long serialVersionUID = 1L;

    private JLabel lblProprio, lblGites, lblClients, lblResa, lblCA;
    private DefaultTableModel modelTable;
    private JTable table;

    public PanelDashboard() {
        setLayout(new BorderLayout(0, 10));
        setBackground(new Color(0xf4f7fb));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        initUI();
        charger();
    }

    private void initUI() {
        // Titre
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel titre = new JLabel("Tableau de bord");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titre.setForeground(new Color(0x1e3a5f));

        JButton btnRefresh = new JButton("Actualiser");
        btnRefresh.setBackground(new Color(0x2e6da4));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { charger(); }
        });
        top.add(titre,      BorderLayout.WEST);
        top.add(btnRefresh, BorderLayout.EAST);

        // Stats
        JPanel stats = new JPanel(new GridLayout(1, 5, 12, 0));
        stats.setOpaque(false);
        stats.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        lblProprio = new JLabel("...", SwingConstants.CENTER);
        lblGites   = new JLabel("...", SwingConstants.CENTER);
        lblClients = new JLabel("...", SwingConstants.CENTER);
        lblResa    = new JLabel("...", SwingConstants.CENTER);
        lblCA      = new JLabel("...", SwingConstants.CENTER);

        stats.add(creerCard("Proprietaires", lblProprio, new Color(0x2e6da4)));
        stats.add(creerCard("Gites",         lblGites,   new Color(0xf0a500)));
        stats.add(creerCard("Clients",       lblClients, new Color(0x2e9e5b)));
        stats.add(creerCard("Reservations",  lblResa,    new Color(0x1e3a5f)));
        stats.add(creerCard("CA (termine)",  lblCA,      new Color(0xd63031)));

        // Tableau recentes
        JPanel bas = new JPanel(new BorderLayout(0, 5));
        bas.setBackground(Color.WHITE);
        bas.setBorder(BorderFactory.createLineBorder(new Color(0xe2e8f0)));

        JLabel titreResa = new JLabel("  Reservations recentes");
        titreResa.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titreResa.setForeground(new Color(0x1e3a5f));
        titreResa.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 0));

        String[] cols = {"#", "Client", "Gite", "Debut", "Fin", "Prix", "Statut"};
        modelTable = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(modelTable);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setGridColor(new Color(0xe2e8f0));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0x1e3a5f));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));

        bas.add(titreResa, BorderLayout.NORTH);
        bas.add(new JScrollPane(table), BorderLayout.CENTER);
        bas.setPreferredSize(new Dimension(0, 260));

        add(top,   BorderLayout.NORTH);
        add(stats, BorderLayout.CENTER);
        add(bas,   BorderLayout.SOUTH);
    }

    private JPanel creerCard(String titre, JLabel valeur, Color couleur) {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(couleur, 2),
            BorderFactory.createEmptyBorder(12, 8, 12, 8)
        ));
        valeur.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valeur.setForeground(couleur);
        JLabel lblTitre = new JLabel(titre, SwingConstants.CENTER);
        lblTitre.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblTitre.setForeground(new Color(0x718096));
        card.add(valeur,   BorderLayout.CENTER);
        card.add(lblTitre, BorderLayout.SOUTH);
        return card;
    }

    void charger() {
        try {
            lblProprio.setText(String.valueOf(Controleur.compter("proprio")));
            lblGites.setText(String.valueOf(Controleur.compter("gite")));
            lblClients.setText(String.valueOf(Controleur.compter("client")));
            lblResa.setText(String.valueOf(Controleur.compter("reservation")));
            lblCA.setText(Controleur.getCA() + " EUR");

            modelTable.setRowCount(0);
            ArrayList<Reservation> recentes = Controleur.getRecentes(8);
            for (Reservation r : recentes) {
                modelTable.addRow(new Object[]{
                    r.getIdreservation(), r.getNomClient(), r.getAdresseGite(),
                    r.getDatedebut(), r.getDatefin(), r.getPrix() + " EUR", r.getStatut_r()
                });
            }
        } catch (Exception e) {
            System.out.println("Erreur dashboard : " + e.getMessage());
        }
    }
}
