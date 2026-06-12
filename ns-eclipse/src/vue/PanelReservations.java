package vue;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.util.ArrayList;
import controleur.Controleur;
import modele.Client;
import modele.Gite;
import modele.Reservation;

public class PanelReservations extends PanelGenerique {

    private static final long serialVersionUID = 1L;

    public PanelReservations() {
        super("Gestion des Reservations",
              new String[]{"ID", "Client", "Gite", "Debut", "Fin", "Prix EUR", "Transport", "Assurance", "Statut"});
        charger();
    }

    protected void charger() {
        modelTable.setRowCount(0);
        idSelectionne = -1;
        ArrayList<Reservation> liste = Controleur.getReservations("");
        for (Reservation r : liste) {
            modelTable.addRow(new Object[]{
                r.getIdreservation(), r.getNomClient(), r.getAdresseGite(),
                r.getDatedebut(), r.getDatefin(), r.getPrix(),
                r.getTransport(), r.isAssurance() ? "Oui" : "Non", r.getStatut_r()
            });
        }
    }

    protected void rechercher(String filtre) {
        modelTable.setRowCount(0);
        ArrayList<Reservation> liste = Controleur.getReservations(filtre);
        for (Reservation r : liste) {
            modelTable.addRow(new Object[]{
                r.getIdreservation(), r.getNomClient(), r.getAdresseGite(),
                r.getDatedebut(), r.getDatefin(), r.getPrix(),
                r.getTransport(), r.isAssurance() ? "Oui" : "Non", r.getStatut_r()
            });
        }
    }

    protected void ajouter() {
        Reservation r = afficherFormulaire(null);
        if (r != null) { Controleur.ajouterReservation(r); charger(); JOptionPane.showMessageDialog(this, "Reservation ajoutee."); }
    }

    protected void modifier() {
        Reservation existant = Controleur.getReservationById(idSelectionne);
        if (existant == null) return;
        Reservation r = afficherFormulaire(existant);
        if (r != null) {
            r.setIdreservation(idSelectionne);
            Controleur.modifierReservation(r);
            charger();
            JOptionPane.showMessageDialog(this, "Reservation modifiee.");
        }
    }

    protected void supprimer() {
        Controleur.supprimerReservation(idSelectionne);
        charger();
        JOptionPane.showMessageDialog(this, "Reservation supprimee.");
    }

    private Reservation afficherFormulaire(Reservation existant) {
        ArrayList<Client> clients = Controleur.getClients("");
        ArrayList<Gite>   gites   = Controleur.getGites("");
        if (clients.isEmpty() || gites.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun client ou gite disponible."); return null;
        }

        JComboBox<Client> cbClient = new JComboBox<Client>();
        for (Client c : clients) cbClient.addItem(c);

        JComboBox<Gite> cbGite = new JComboBox<Gite>();
        for (Gite g : gites) cbGite.addItem(g);

        JTextField tfDebut = new JTextField(existant != null ? existant.getDatedebut() : "2025-01-01");
        JTextField tfFin   = new JTextField(existant != null ? existant.getDatefin()   : "2025-01-08");
        JTextField tfPrix  = new JTextField(existant != null ? String.valueOf(existant.getPrix()) : "");

        JComboBox<String> cbTransport = new JComboBox<String>(new String[]{"voiture","train","avion"});
        JCheckBox chkAssurance = new JCheckBox("Oui");

        JComboBox<String> cbStatut = new JComboBox<String>(
            new String[]{"en cours","termine","non reserve"});

        JTextArea taRapport = new JTextArea(3, 20);
        taRapport.setLineWrap(true);

        if (existant != null) {
            for (int i = 0; i < cbClient.getItemCount(); i++)
                if (cbClient.getItemAt(i).getIdclient() == existant.getIdclient()) { cbClient.setSelectedIndex(i); break; }
            for (int i = 0; i < cbGite.getItemCount(); i++)
                if (cbGite.getItemAt(i).getIdgite() == existant.getIdgite()) { cbGite.setSelectedIndex(i); break; }
            cbTransport.setSelectedItem(existant.getTransport());
            chkAssurance.setSelected(existant.isAssurance());
            cbStatut.setSelectedItem(existant.getStatut_r());
            if (existant.getRapport() != null) taRapport.setText(existant.getRapport());
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Client *"));           form.add(cbClient);
        form.add(new JLabel("Gite *"));             form.add(cbGite);
        form.add(new JLabel("Date debut (YYYY-MM-DD) *")); form.add(tfDebut);
        form.add(new JLabel("Date fin (YYYY-MM-DD) *"));   form.add(tfFin);
        form.add(new JLabel("Prix total EUR *"));   form.add(tfPrix);
        form.add(new JLabel("Transport *"));        form.add(cbTransport);
        form.add(new JLabel("Assurance"));          form.add(chkAssurance);
        form.add(new JLabel("Statut *"));           form.add(cbStatut);
        form.add(new JLabel("Rapport / Notes"));    form.add(new JScrollPane(taRapport));

        int res = JOptionPane.showConfirmDialog(this, form,
            existant != null ? "Modifier la reservation" : "Ajouter une reservation",
            JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return null;

        if (tfDebut.getText().isBlank() || tfFin.getText().isBlank() || tfPrix.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Champs obligatoires manquants."); return null;
        }
        try {
            Client client = (Client) cbClient.getSelectedItem();
            Gite   gite   = (Gite)   cbGite.getSelectedItem();
            Reservation r = new Reservation();
            r.setIdclient(client.getIdclient());
            r.setIdgite(gite.getIdgite());
            r.setDatedebut(tfDebut.getText().trim());
            r.setDatefin(tfFin.getText().trim());
            r.setPrix(Integer.parseInt(tfPrix.getText().trim()));
            r.setTransport((String) cbTransport.getSelectedItem());
            r.setAssurance(chkAssurance.isSelected());
            r.setStatut_r((String) cbStatut.getSelectedItem());
            r.setRapport(taRapport.getText().trim().isEmpty() ? null : taRapport.getText().trim());
            return r;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Le prix doit etre un nombre entier."); return null;
        }
    }
}
