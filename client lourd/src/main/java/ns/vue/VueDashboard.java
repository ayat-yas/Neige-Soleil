package ns.vue;

import ns.controleur.AppControleur;
import ns.modele.Reservation;
import ns.util.Assets;
import ns.util.Style;
import ns.util.UIFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class VueDashboard extends JPanel {

    private final AppControleur controleur;
    private JLabel lblProprio, lblGites, lblClients, lblResa, lblCA;
    private DefaultTableModel modelRecentes;

    public VueDashboard(AppControleur controleur) {
        this.controleur = controleur;
        initUI();
        charger();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Style.BG_DEEP);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titre = UIFactory.labelTitre("🏠  Tableau de bord");
        JButton btnRefresh = UIFactory.btnOutline("↻ Actualiser");
        btnRefresh.addActionListener(e -> charger());
        header.add(titre, BorderLayout.WEST);
        header.add(btnRefresh, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Stat cards avec icônes
        JPanel stats = new JPanel(new GridLayout(1, 5, 14, 0));
        stats.setOpaque(false);
        lblProprio = statLabel(); lblGites = statLabel();
        lblClients = statLabel(); lblResa  = statLabel(); lblCA = statLabel();

        stats.add(statCard("Propriétaires", lblProprio, Style.C_ICE,   Assets.icon(Assets.PROPRIO,     48)));
        stats.add(statCard("Gîtes",         lblGites,   Style.C_ACCENT, Assets.icon(Assets.GITE,        48)));
        stats.add(statCard("Clients",        lblClients, Style.C_SUCCESS,Assets.icon(Assets.CLIENT,      48)));
        stats.add(statCard("Réservations",   lblResa,    Style.C_PRIMARY,Assets.icon(Assets.RESERVATION, 48)));
        stats.add(statCard("CA terminé",     lblCA,      Style.C_DANGER, Assets.icon(Assets.DASHBOARD,   48)));
        add(stats, BorderLayout.CENTER);

        // Réservations récentes
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(Style.BG_CARD);
        panel.setBorder(BorderFactory.createLineBorder(Style.C_BORDER_HI, 1, true));

        JLabel tRecent = new JLabel("  Réservations récentes");
        tRecent.setFont(Style.FONT_SUBTITLE);
        tRecent.setForeground(Style.C_TEXT);
        tRecent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
        tRecent.setBackground(Style.C_TABLE_HEADER);
        tRecent.setOpaque(true);

        String[] cols = {"#", "Client", "Gîte", "Début", "Fin", "Prix", "Statut"};
        modelRecentes = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(modelRecentes);
        UIFactory.styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Style.BG_CARD);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        panel.add(tRecent, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(0, 280));
        add(panel, BorderLayout.SOUTH);
    }

    private JLabel statLabel() {
        JLabel l = new JLabel("…", SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 26));
        return l;
    }

    private JPanel statCard(String titre, JLabel valLbl, Color couleur, ImageIcon icon) {
        JPanel c = new JPanel(new BorderLayout(0, 6));
        c.setBackground(Style.BG_CARD);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, couleur),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Style.C_BORDER, 1),
                BorderFactory.createEmptyBorder(14, 10, 14, 10)
            )
        ));

        // Icône en haut
        if (icon != null) {
            JLabel iconLbl = new JLabel(icon, SwingConstants.CENTER);
            iconLbl.setOpaque(false);
            c.add(iconLbl, BorderLayout.NORTH);
        }

        valLbl.setForeground(couleur);
        c.add(valLbl, BorderLayout.CENTER);

        JLabel t = new JLabel(titre, SwingConstants.CENTER);
        t.setFont(Style.FONT_SMALL);
        t.setForeground(Style.C_TEXT_DIM);
        c.add(t, BorderLayout.SOUTH);
        return c;
    }

    private String statutVisuel(Reservation r) {
        if ("à valider".equals(r.getStatut_r())) return "À valider";
        LocalDate today = LocalDate.now();
        if (r.getDatefin().isBefore(today)) return "Terminée";
        if (!r.getDatedebut().isAfter(today)) return "En cours";
        return "Planifiée";
    }

    void charger() {
        new SwingWorker<Void, Void>() {
            int nbP, nbG, nbC, nbR, ca;
            List<Reservation> recentes;
            @Override protected Void doInBackground() {
                try {
                    nbP = controleur.compterProprietaires();
                    nbG = controleur.compterGites();
                    nbC = controleur.compterClients();
                    nbR = controleur.compterReservations();
                    ca  = controleur.getChiffreAffaires();
                    recentes = controleur.getReservationsRecentes(8);
                } catch (Exception ex) { ex.printStackTrace(); }
                return null;
            }
            @Override protected void done() {
                lblProprio.setText(String.valueOf(nbP));
                lblGites.setText(String.valueOf(nbG));
                lblClients.setText(String.valueOf(nbC));
                lblResa.setText(String.valueOf(nbR));
                lblCA.setText(ca + " €");
                modelRecentes.setRowCount(0);
                if (recentes != null) for (Reservation r : recentes)
                    modelRecentes.addRow(new Object[]{
                        r.getIdreservation(), r.getNomClient(), r.getAdresseGite(),
                        r.getDatedebut(), r.getDatefin(), r.getPrix() + " €", statutVisuel(r)
                    });
            }
        }.execute();
    }
}
