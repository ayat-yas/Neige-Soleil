package vue;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.util.ArrayList;
import controleur.Controleur;
import modele.Client;

public class PanelClients extends PanelGenerique {

    private static final long serialVersionUID = 1L;

    public PanelClients() {
        super("Gestion des Clients",
              new String[]{"ID", "Nom", "Prenom", "Email", "Telephone"});
        charger();
    }

    protected void charger() {
        modelTable.setRowCount(0);
        idSelectionne = -1;
        ArrayList<Client> liste = Controleur.getClients("");
        for (Client c : liste) {
            modelTable.addRow(new Object[]{
                c.getIdclient(), c.getNom(), c.getPrenom(), c.getEmail(), c.getTelephone()
            });
        }
    }

    protected void rechercher(String filtre) {
        modelTable.setRowCount(0);
        ArrayList<Client> liste = Controleur.getClients(filtre);
        for (Client c : liste) {
            modelTable.addRow(new Object[]{
                c.getIdclient(), c.getNom(), c.getPrenom(), c.getEmail(), c.getTelephone()
            });
        }
    }

    protected void ajouter() {
        Client c = afficherFormulaire(null);
        if (c != null) { Controleur.ajouterClient(c); charger(); JOptionPane.showMessageDialog(this, "Client ajoute."); }
    }

    protected void modifier() {
        Client existant = Controleur.getClientById(idSelectionne);
        if (existant == null) return;
        Client c = afficherFormulaire(existant);
        if (c != null) {
            c.setIdclient(idSelectionne);
            Controleur.modifierClient(c, !c.getMdp().isEmpty());
            charger();
            JOptionPane.showMessageDialog(this, "Client modifie.");
        }
    }

    protected void supprimer() {
        Controleur.supprimerClient(idSelectionne);
        charger();
        JOptionPane.showMessageDialog(this, "Client supprime.");
    }

    private Client afficherFormulaire(Client existant) {
        boolean modif = (existant != null);
        JTextField tfNom     = new JTextField(existant != null ? existant.getNom()       : "");
        JTextField tfPrenom  = new JTextField(existant != null ? existant.getPrenom()    : "");
        JTextField tfAdresse = new JTextField(existant != null ? existant.getAdresse()   : "");
        JTextField tfEmail   = new JTextField(existant != null ? existant.getEmail()     : "");
        JTextField tfTel     = new JTextField(existant != null ? existant.getTelephone() : "");
        JPasswordField tfMdp = new JPasswordField();

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Nom *"));       form.add(tfNom);
        form.add(new JLabel("Prenom *"));    form.add(tfPrenom);
        form.add(new JLabel("Adresse *"));   form.add(tfAdresse);
        form.add(new JLabel("Email *"));     form.add(tfEmail);
        form.add(new JLabel("Telephone *")); form.add(tfTel);
        form.add(new JLabel(modif ? "Nouveau mdp (vide=inchange)" : "Mot de passe *")); form.add(tfMdp);

        int res = JOptionPane.showConfirmDialog(this, form,
            modif ? "Modifier le client" : "Ajouter un client", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return null;

        if (tfNom.getText().isBlank() || tfEmail.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Champs obligatoires manquants."); return null;
        }
        Client c = new Client();
        c.setNom(tfNom.getText().trim()); c.setPrenom(tfPrenom.getText().trim());
        c.setAdresse(tfAdresse.getText().trim()); c.setEmail(tfEmail.getText().trim());
        c.setTelephone(tfTel.getText().trim()); c.setMdp(new String(tfMdp.getPassword()));
        return c;
    }
}
