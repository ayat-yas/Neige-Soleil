package vue;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.util.ArrayList;
import controleur.Controleur;
import modele.Gite;
import modele.Proprietaire;

public class PanelGites extends PanelGenerique {

    private static final long serialVersionUID = 1L;

    public PanelGites() {
        super("Gestion des Gites",
              new String[]{"ID", "Adresse", "Surface m2", "Pieces", "Loyer EUR/j", "Proprietaire"});
        charger();
    }

    protected void charger() {
        modelTable.setRowCount(0);
        idSelectionne = -1;
        ArrayList<Gite> liste = Controleur.getGites("");
        for (Gite g : liste) {
            modelTable.addRow(new Object[]{
                g.getIdgite(), g.getAdresse(), g.getSurface(),
                g.getNbpieces(), g.getLoyer(), g.getNomProprio()
            });
        }
    }

    protected void rechercher(String filtre) {
        modelTable.setRowCount(0);
        ArrayList<Gite> liste = Controleur.getGites(filtre);
        for (Gite g : liste) {
            modelTable.addRow(new Object[]{
                g.getIdgite(), g.getAdresse(), g.getSurface(),
                g.getNbpieces(), g.getLoyer(), g.getNomProprio()
            });
        }
    }

    protected void ajouter() {
        Gite g = afficherFormulaire(null);
        if (g != null) { Controleur.ajouterGite(g); charger(); JOptionPane.showMessageDialog(this, "Gite ajoute."); }
    }

    protected void modifier() {
        Gite existant = Controleur.getGiteById(idSelectionne);
        if (existant == null) return;
        Gite g = afficherFormulaire(existant);
        if (g != null) {
            g.setIdgite(idSelectionne);
            Controleur.modifierGite(g);
            charger();
            JOptionPane.showMessageDialog(this, "Gite modifie.");
        }
    }

    protected void supprimer() {
        Controleur.supprimerGite(idSelectionne);
        charger();
        JOptionPane.showMessageDialog(this, "Gite supprime.");
    }

    private Gite afficherFormulaire(Gite existant) {
        ArrayList<Proprietaire> proprios = Controleur.getProprietaires("");
        if (proprios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun proprietaire disponible."); return null;
        }

        JTextField tfAdresse = new JTextField(existant != null ? existant.getAdresse()  : "");
        JTextField tfSurface = new JTextField(existant != null ? String.valueOf(existant.getSurface())   : "");
        JTextField tfPieces  = new JTextField(existant != null ? String.valueOf(existant.getNbpieces())  : "");
        JTextField tfLoyer   = new JTextField(existant != null ? String.valueOf(existant.getLoyer())     : "");

        JComboBox<Proprietaire> cbProprio = new JComboBox<Proprietaire>();
        for (Proprietaire p : proprios) cbProprio.addItem(p);
        if (existant != null) {
            for (int i = 0; i < cbProprio.getItemCount(); i++) {
                if (cbProprio.getItemAt(i).getIdproprio() == existant.getIdproprio()) {
                    cbProprio.setSelectedIndex(i); break;
                }
            }
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Adresse *"));      form.add(tfAdresse);
        form.add(new JLabel("Surface m2 *"));   form.add(tfSurface);
        form.add(new JLabel("Nb pieces *"));    form.add(tfPieces);
        form.add(new JLabel("Loyer EUR/j *"));  form.add(tfLoyer);
        form.add(new JLabel("Proprietaire *")); form.add(cbProprio);

        int res = JOptionPane.showConfirmDialog(this, form,
            existant != null ? "Modifier le gite" : "Ajouter un gite", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return null;

        if (tfAdresse.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "L'adresse est obligatoire."); return null;
        }
        try {
            Proprietaire proprio = (Proprietaire) cbProprio.getSelectedItem();
            return new Gite(0, tfAdresse.getText().trim(),
                Integer.parseInt(tfSurface.getText().trim()),
                Integer.parseInt(tfPieces.getText().trim()),
                Integer.parseInt(tfLoyer.getText().trim()),
                proprio.getIdproprio());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Surface, Pieces et Loyer doivent etre des nombres.");
            return null;
        }
    }
}
