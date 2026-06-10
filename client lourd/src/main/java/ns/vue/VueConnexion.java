package ns.vue;

import ns.controleur.AppControleur;
import ns.util.Assets;
import ns.util.Style;
import ns.util.UIFactory;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class VueConnexion extends JFrame {

    private final AppControleur controleur;
    private JTextField     tfEmail;
    private JPasswordField pfMdp;
    private JLabel         lblErreur;

    public VueConnexion(AppControleur controleur) {
        this.controleur = controleur;
        initUI();
    }

    private void initUI() {
        setTitle("Neige & Soleil — Connexion");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Fond dégradé
        JPanel fond = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setPaint(new GradientPaint(0, 0, new Color(0x060d1a), getWidth(), getHeight(), new Color(0x0f1f3d)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        fond.setLayout(new GridBagLayout());
        setContentPane(fond);

        // Carte centrale
        JPanel carte = new JPanel(new GridBagLayout());
        carte.setBackground(Style.BG_CARD);
        carte.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Style.C_BORDER_HI, 1, true),
            BorderFactory.createEmptyBorder(36, 50, 36, 50)
        ));
        carte.setPreferredSize(new Dimension(440, 520));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 0, 6, 0);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        // Logo image
        gc.gridx = 0; gc.gridy = 0;
        ImageIcon logoIcon = Assets.logo(200);
        if (logoIcon != null) {
            JLabel logoLbl = new JLabel(logoIcon, SwingConstants.CENTER);
            carte.add(logoLbl, gc);
        }

        // Titre texte
        gc.gridy = 1;
        JLabel titre = new JLabel("NEIGE & SOLEIL", SwingConstants.CENTER);
        titre.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titre.setForeground(Style.C_TEXT);
        carte.add(titre, gc);

        gc.gridy = 2;
        JLabel sous = new JLabel("Administration — Client Lourd Java", SwingConstants.CENTER);
        sous.setFont(Style.FONT_SMALL);
        sous.setForeground(Style.C_MUTED);
        carte.add(sous, gc);

        // Séparateur
        gc.gridy = 3; gc.insets = new Insets(12, 0, 12, 0);
        JSeparator sep = new JSeparator();
        sep.setForeground(Style.C_BORDER_HI);
        carte.add(sep, gc);
        gc.insets = new Insets(6, 0, 6, 0);

        // Champs
        gc.gridy = 4; carte.add(UIFactory.label("Adresse e-mail"), gc);
        gc.gridy = 5;
        tfEmail = UIFactory.field(25);
        tfEmail.setPreferredSize(new Dimension(310, 38));
        carte.add(tfEmail, gc);

        gc.gridy = 6; carte.add(UIFactory.label("Mot de passe"), gc);
        gc.gridy = 7;
        pfMdp = UIFactory.passField(25);
        pfMdp.setPreferredSize(new Dimension(310, 38));
        carte.add(pfMdp, gc);

        // Erreur
        gc.gridy = 8;
        lblErreur = new JLabel(" ", SwingConstants.CENTER);
        lblErreur.setFont(Style.FONT_SMALL);
        lblErreur.setForeground(Style.C_DANGER);
        carte.add(lblErreur, gc);

        // Bouton
        gc.gridy = 9; gc.insets = new Insets(10, 0, 6, 0);
        JButton btnConn = UIFactory.btnPrimary("Se connecter →");
        btnConn.setPreferredSize(new Dimension(310, 44));
        btnConn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        carte.add(btnConn, gc);

        // Aide
        gc.gridy = 10; gc.insets = new Insets(8, 0, 0, 0);
        JLabel note = new JLabel("Compte test : jean.leclerc@mail.com / mdp789", SwingConstants.CENTER);
        note.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        note.setForeground(Style.C_MUTED);
        carte.add(note, gc);

        fond.add(carte);

        btnConn.addActionListener(e -> tenterConnexion());
        pfMdp.addActionListener(e -> tenterConnexion());
        tfEmail.addActionListener(e -> pfMdp.requestFocus());

        pack();
        setLocationRelativeTo(null);
    }

    private void tenterConnexion() {
        lblErreur.setText(" ");
        String email = tfEmail.getText().trim();
        String mdp   = new String(pfMdp.getPassword());
        if (email.isEmpty() || mdp.isEmpty()) { lblErreur.setText("Remplissez tous les champs."); return; }

        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() { return controleur.connecter(email, mdp); }
            @Override protected void done() {
                try {
                    if (get()) { dispose(); new VuePrincipale(controleur).setVisible(true); }
                    else { lblErreur.setText("Email ou mot de passe incorrect."); pfMdp.setText(""); }
                } catch (Exception ex) { lblErreur.setText("Erreur de connexion."); }
            }
        }.execute();
    }
}
