package ns.vue;

import ns.controleur.AppControleur;
import ns.util.Assets;
import ns.util.Style;
import ns.util.UIFactory;

import javax.swing.*;
import java.awt.*;

public class VuePrincipale extends JFrame {

    private final AppControleur controleur;

    public VuePrincipale(AppControleur controleur) {
        this.controleur = controleur;
        initUI();
    }

    private void initUI() {
        String nom = controleur.getAdminConnecte() != null
            ? controleur.getAdminConnecte().getPrenom() + " " + controleur.getAdminConnecte().getNom()
            : "Admin";
        String statut = controleur.getAdminConnecte() != null
            ? controleur.getAdminConnecte().getStatut().toUpperCase() : "";

        setTitle("Neige & Soleil — " + nom);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 750));

        // ── Forcer les onglets en dark via UIManager ──
        UIManager.put("TabbedPane.background",          Style.BG_DEEP);
        UIManager.put("TabbedPane.foreground",          Style.C_TEXT);
        UIManager.put("TabbedPane.selected",            new Color(0x0d1525));
        UIManager.put("TabbedPane.contentAreaColor",    new Color(0x0d1525));
        UIManager.put("TabbedPane.tabAreaBackground",   Style.BG_DEEP);
        UIManager.put("TabbedPane.unselectedBackground",Style.BG_DEEP);
        UIManager.put("TabbedPane.shadow",              Style.BG_DEEP);
        UIManager.put("TabbedPane.darkShadow",          Style.BG_DEEP);
        UIManager.put("TabbedPane.light",               Style.BG_DEEP);
        UIManager.put("TabbedPane.highlight",           new Color(0x1e3a6e));
        UIManager.put("TabbedPane.focus",               new Color(0x1e3a6e));
        UIManager.put("TabbedPane.selectHighlight",     new Color(0x1a56db));
        UIManager.put("TabbedPane.tabInsets",           new Insets(8, 14, 8, 14));
        UIManager.put("TabbedPane.selectedForeground",  Style.C_TEXT);

        // ── Navbar ──
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(new Color(0x060d1a));
        navbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Style.C_BORDER_HI));
        navbar.setPreferredSize(new Dimension(getWidth(), 56));

        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        brandPanel.setOpaque(false);
        ImageIcon logoSmall = Assets.logo(60);
        if (logoSmall != null) brandPanel.add(new JLabel(logoSmall));
        JLabel brand = new JLabel("NEIGE & SOLEIL");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 16));
        brand.setForeground(Style.C_TEXT);
        brandPanel.add(brand);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        right.setOpaque(false);
        JLabel userLbl = new JLabel("👤 " + nom + "   |   " + statut);
        userLbl.setFont(Style.FONT_SMALL);
        userLbl.setForeground(Style.C_MUTED);

        JButton btnDeco = new JButton("Déconnexion");
        btnDeco.setFont(Style.FONT_SMALL);
        btnDeco.setBackground(new Color(0x7f1d1d));
        btnDeco.setForeground(new Color(0xfca5a5));
        btnDeco.setBorderPainted(false);
        btnDeco.setFocusPainted(false);
        btnDeco.setOpaque(true);
        btnDeco.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDeco.addActionListener(e -> {
            if (UIFactory.confirmer(this, "Se déconnecter ?")) {
                dispose();
                controleur.deconnecter();
                new VueConnexion(controleur).setVisible(true);
            }
        });

        right.add(userLbl);
        right.add(btnDeco);
        navbar.add(brandPanel, BorderLayout.WEST);
        navbar.add(right, BorderLayout.EAST);

        // ── Onglets ──
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(Style.BG_DEEP);
        tabs.setForeground(Style.C_TEXT);
        tabs.setFont(Style.FONT_BOLD);
        tabs.setOpaque(true);

        String[]    titles   = {"  Dashboard", "  Propriétaires", "  Gîtes", "  Clients", "  Réservations"};
        String[]    iconKeys = {Assets.DASHBOARD, Assets.PROPRIO, Assets.GITE, Assets.CLIENT, Assets.RESERVATION};
        Component[] panels   = {
            new VueDashboard(controleur),
            new VueProprietaires(controleur),
            new VueGites(controleur),
            new VueClients(controleur),
            new VueReservations(controleur)
        };

        for (int i = 0; i < titles.length; i++) {
            tabs.addTab(titles[i], panels[i]);
            // Label avec fond dark explicite et texte blanc
            ImageIcon ic  = Assets.icon(iconKeys[i], 22);
            JLabel    lbl = new JLabel(titles[i], ic, SwingConstants.LEFT);
            lbl.setFont(Style.FONT_BOLD);
            lbl.setForeground(Style.C_TEXT);
            lbl.setOpaque(true);
            lbl.setBackground(Style.BG_DEEP);
            lbl.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            tabs.setTabComponentAt(i, lbl);
        }

        // Listener pour mettre à jour la couleur fond du label sélectionné
        tabs.addChangeListener(e -> {
            for (int i = 0; i < tabs.getTabCount(); i++) {
                if (tabs.getTabComponentAt(i) instanceof JLabel lbl) {
                    boolean sel = (tabs.getSelectedIndex() == i);
                    lbl.setBackground(sel ? new Color(0x1a3a6e) : Style.BG_DEEP);
                    lbl.setForeground(Style.C_TEXT);
                }
            }
        });
        // Initialiser le premier onglet sélectionné
        if (tabs.getTabComponentAt(0) instanceof JLabel lbl) {
            lbl.setBackground(new Color(0x1a3a6e));
        }

        // ── Footer ──
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(new Color(0x060d1a));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Style.C_BORDER));
        JLabel fl = new JLabel("© " + java.time.Year.now() + " Neige & Soleil — Locations Saisonnières");
        fl.setFont(Style.FONT_SMALL);
        fl.setForeground(Style.C_MUTED);
        footer.add(fl);

        setLayout(new BorderLayout());
        getContentPane().setBackground(Style.BG_DEEP);
        add(navbar, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }
}
