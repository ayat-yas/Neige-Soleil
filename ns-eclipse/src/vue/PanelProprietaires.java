package vue;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.util.ArrayList;
import controleur.Controleur;
import modele.Proprietaire;

/**
 * Panel CRUD Proprietaires.
 */
public class PanelProprietaires extends PanelGenerique {

    private static final long serialVersionUID = 1L;

    public PanelProprietaires() {
        super("Gestion des Proprietaires",
              new String[]{"ID", "Nom", "Prenom", "Email", "Telephone", "Statut"});
        charger();
    }

    protected void charger() {
        modelTable.setRowCount(0);
        idSelectionne = -1;
        ArrayList<Proprietaire> liste = Controleur.getProprietaires("");
        for (Proprietaire p : liste) {
            modelTable.addRow(new Object[]{
                p.getIdproprio(), p.getNom(), p.getPrenom(),
                p.getEmail(), p.getTelephone(), p.getStatut()
            });
        }
    }

    protected void rechercher(String filtre) {
        modelTable.setRowCount(0);
        ArrayList<Proprietaire> liste = Controleur.getProprietaires(filtre);
        for (Proprietaire p : liste) {
            modelTable.addRow(new Object[]{
                p.getIdproprio(), p.getNom(), p.getPrenom(),
                p.getEmail(), p.getTelephone(), p.getStatut()
            });
        }
    }

    protected void ajouter() {
        Proprietaire p = afficherFormulaire(null);
        if (p != null) {
            Controleur.ajouterProprietaire(p);
            charger();
            JOptionPane.showMessageDialog(this, "Proprietaire ajoute avec succes.");
        }
    }

    protected void modifier() {
        Proprietaire existant = Controleur.getProprietaireById(idSelectionne);
        if (existant == null) return;
        Proprietaire p = afficherFormulaire(existant);
        if (p != null) {
            p.setIdproprio(idSelectionne);
            boolean changerMdp = !p.getMdp().isEmpty();
            Controleur.modifierProprietaire(p, changerMdp);
            charger();
            JOptionPane.showMessageDialog(this, "Proprietaire modifie avec succes.");
        }
    }

    protected void supprimer() {
        Controleur.supprimerProprietaire(idSelectionne);
        charger();
        JOptionPane.showMessageDialog(this, "Proprietaire supprime.");
    }

    private Proprietaire afficherFormulaire(Proprietaire existant) {
        boolean modif = (existant != null);

        JTextField tfNom     = new JTextField(existant != null ? existant.getNom()       : "");
        JTextField tfPrenom  = new JTextField(existant != null ? existant.getPrenom()    : "");
        JTextField tfAdresse = new JTextField(existant != null ? existant.getAdresse()   : "");
        JTextField tfEmail   = new JTextField(existant != null ? existant.getEmail()     : "");
        JTextField tfTel     = new JTextField(existant != null ? existant.getTelephone() : "");
        JPasswordField tfMdp = new JPasswordField();
        JComboBox<String> cbStatut = new JComboBox<String>(new String[]{"prive","public","admin"});
        if (existant != null) cbStatut.setSelectedItem(existant.getStatut());

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Nom *"));        form.add(tfNom);
        form.add(new JLabel("Prenom *"));     form.add(tfPrenom);
        form.add(new JLabel("Adresse *"));    form.add(tfAdresse);
        form.add(new JLabel("Email *"));      form.add(tfEmail);
        form.add(new JLabel("Telephone *"));  form.add(tfTel);
        form.add(new JLabel(modif ? "Nouveau mdp (vide=inchange)" : "Mot de passe *")); form.add(tfMdp);
        form.add(new JLabel("Statut *"));     form.add(cbStatut);

        int res = JOptionPane.showConfirmDialog(this, form,
            modif ? "Modifier le proprietaire" : "Ajouter un proprietaire",
            JOptionPane.OK_CANCEL_OPTION);

        if (res != JOptionPane.OK_OPTION) return null;

        if (tfNom.getText().isBlank() || tfPrenom.getText().isBlank() ||
            tfEmail.getText().isBlank() || tfTel.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Champs obligatoires manquants.");
            return null;
        }
        if (!modif && new String(tfMdp.getPassword()).isBlank()) {
            JOptionPane.showMessageDialog(this, "Le mot de passe est obligatoire.");
            return null;
        }

        Proprietaire p = new Proprietaire();
        p.setNom(tfNom.getText().trim());
        p.setPrenom(tfPrenom.getText().trim());
        p.setAdresse(tfAdresse.getText().trim());
        p.setEmail(tfEmail.getText().trim());
        p.setTelephone(tfTel.getText().trim());
        p.setMdp(new String(tfMdp.getPassword()));
        p.setStatut((String) cbStatut.getSelectedItem());
        return p;
    }
}
