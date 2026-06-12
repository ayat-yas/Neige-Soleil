package vue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import controleur.Controleur;
import controleur.NeigeEtSoleil;
import modele.Proprietaire;

/**
 * Fenetre de connexion administrateur.
 */
public class VueConnexion extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField     tfEmail;
    private JPasswordField tfMdp;
    private JButton        btnConnexion;
    private JLabel         lblErreur;

    public VueConnexion() {
        setTitle("Neige & Soleil — Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(420, 380);
        setLocationRelativeTo(null);
        initUI();
        setVisible(true);
    }

    private void initUI() {
        // Fond principal
        JPanel fond = new JPanel(new BorderLayout());
        fond.setBackground(new Color(0x1e3a5f));

        // Titre
        JLabel lblTitre = new JLabel("Neige & Soleil", SwingConstants.CENTER);
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitre.setForeground(Color.WHITE);
        lblTitre.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 5, 0));

        JLabel lblSous = new JLabel("Administration — Client Lourd Java", SwingConstants.CENTER);
        lblSous.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSous.setForeground(new Color(0xC8DAEA));

        JPanel titrePanel = new JPanel(new BorderLayout());
        titrePanel.setBackground(new Color(0x1e3a5f));
        titrePanel.add(lblTitre, BorderLayout.CENTER);
        titrePanel.add(lblSous,  BorderLayout.SOUTH);

        // Formulaire
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(javax.swing.BorderFactory.createEmptyBorder(25, 40, 25, 40));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        gc.insets  = new Insets(6, 0, 6, 0);

        // Email
        gc.gridx = 0; gc.gridy = 0;
        JLabel lblEmail = new JLabel("Adresse e-mail");
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 12));
        form.add(lblEmail, gc);

        gc.gridy = 1;
        tfEmail = new JTextField();
        tfEmail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tfEmail.setPreferredSize(new Dimension(300, 34));
        form.add(tfEmail, gc);

        // Mdp
        gc.gridy = 2;
        JLabel lblMdp = new JLabel("Mot de passe");
        lblMdp.setFont(new Font("Segoe UI", Font.BOLD, 12));
        form.add(lblMdp, gc);

        gc.gridy = 3;
        tfMdp = new JPasswordField();
        tfMdp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tfMdp.setPreferredSize(new Dimension(300, 34));
        form.add(tfMdp, gc);

        // Erreur
        gc.gridy = 4;
        lblErreur = new JLabel(" ");
        lblErreur.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblErreur.setForeground(new Color(0xd63031));
        lblErreur.setHorizontalAlignment(SwingConstants.CENTER);
        form.add(lblErreur, gc);

        // Bouton
        gc.gridy = 5;
        gc.insets = new Insets(10, 0, 6, 0);
        btnConnexion = new JButton("Se connecter");
        btnConnexion.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnConnexion.setBackground(new Color(0x2e6da4));
        btnConnexion.setForeground(Color.WHITE);
        btnConnexion.setOpaque(true);
        btnConnexion.setBorderPainted(false);
        btnConnexion.setPreferredSize(new Dimension(300, 38));
        form.add(btnConnexion, gc);

        // Note
        gc.gridy = 6;
        JLabel note = new JLabel("jean.leclerc@mail.com / mdp789", SwingConstants.CENTER);
        note.setFont(new Font("Segoe UI", Font.ITALIC, 9));
        note.setForeground(Color.GRAY);
        form.add(note, gc);

        fond.add(titrePanel, BorderLayout.NORTH);
        fond.add(form,       BorderLayout.CENTER);
        setContentPane(fond);

        // Action connexion
        ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tenterConnexion();
            }
        };
        btnConnexion.addActionListener(action);
        tfMdp.addActionListener(action);
    }

    private void tenterConnexion() {
        String email = tfEmail.getText().trim();
        String mdp   = new String(tfMdp.getPassword());

        if (email.isEmpty() || mdp.isEmpty()) {
            lblErreur.setText("Veuillez remplir tous les champs.");
            return;
        }

        Proprietaire admin = Controleur.connecterAdmin(email, mdp);
        if (admin != null) {
            NeigeEtSoleil.setAdminConnecte(admin);
            dispose();
            NeigeEtSoleil.ouvrirPrincipale();
        } else {
            lblErreur.setText("Email ou mot de passe incorrect.");
            tfMdp.setText("");
        }
    }
}
