package vue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import controleur.NeigeEtSoleil;
import modele.Proprietaire;

/**
 * Fenetre principale avec onglets apres connexion.
 */
public class VuePrincipale extends JFrame {

    private static final long serialVersionUID = 1L;

    public VuePrincipale() {
        Proprietaire admin = NeigeEtSoleil.getAdminConnecte();
        String nom = admin != null ? admin.getPrenom() + " " + admin.getNom() : "Admin";

        setTitle("Neige & Soleil — " + nom);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        initUI(nom, admin);
    }

    private void initUI(String nom, Proprietaire admin) {
        setLayout(new BorderLayout());

        // ── Navbar ──
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(new Color(0x1e3a5f));
        navbar.setPreferredSize(new Dimension(getWidth(), 50));

        JLabel brand = new JLabel("   Neige & Soleil");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 16));
        brand.setForeground(Color.WHITE);

        JPanel droite = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        droite.setOpaque(false);

        String statut = admin != null ? admin.getStatut() : "";
        JLabel user = new JLabel("Bonjour " + nom + "  |  " + statut);
        user.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        user.setForeground(new Color(0xa8d8f0));

        JButton btnDeco = new JButton("Deconnexion");
        btnDeco.setBackground(new Color(0xd63031));
        btnDeco.setForeground(Color.WHITE);
        btnDeco.setBorderPainted(false);
        btnDeco.setFocusPainted(false);
        btnDeco.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnDeco.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int rep = JOptionPane.showConfirmDialog(VuePrincipale.this,
                    "Voulez-vous vous deconnecter ?", "Confirmation",
                    JOptionPane.YES_NO_OPTION);
                if (rep == JOptionPane.YES_OPTION) {
                    dispose();
                    NeigeEtSoleil.setAdminConnecte(null);
                    vue.VueConnexion vc = new vue.VueConnexion();
                    vc.setVisible(true);
                }
            }
        });

        droite.add(user);
        droite.add(btnDeco);
        navbar.add(brand, BorderLayout.WEST);
        navbar.add(droite, BorderLayout.EAST);

        // ── Onglets ──
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));

        tabs.addTab("  Tableau de bord  ", new PanelDashboard());
        tabs.addTab("  Proprietaires    ", new PanelProprietaires());
        tabs.addTab("  Gites            ", new PanelGites());
        tabs.addTab("  Clients          ", new PanelClients());
        tabs.addTab("  Reservations     ", new PanelReservations());

        // ── Footer ──
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(new Color(0x1e3a5f));
        JLabel fl = new JLabel("Neige & Soleil — Client Lourd Java/Swing — BTS SIO SLAM 2026");
        fl.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        fl.setForeground(new Color(0x718096));
        footer.add(fl);

        add(navbar, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
