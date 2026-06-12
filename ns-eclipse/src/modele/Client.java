package modele;

/**
 * Entite Client — correspond a la table client.
 */
public class Client {

    private int    idclient;
    private String nom;
    private String prenom;
    private String adresse;
    private String email;
    private String mdp;
    private String telephone;

    public Client() {}

    public Client(int idclient, String nom, String prenom,
                  String adresse, String email, String mdp, String telephone) {
        this.idclient  = idclient;
        this.nom       = nom;
        this.prenom    = prenom;
        this.adresse   = adresse;
        this.email     = email;
        this.mdp       = mdp;
        this.telephone = telephone;
    }

    public int    getIdclient()            { return idclient; }
    public void   setIdclient(int id)      { this.idclient = id; }
    public String getNom()                 { return nom; }
    public void   setNom(String nom)       { this.nom = nom; }
    public String getPrenom()              { return prenom; }
    public void   setPrenom(String p)      { this.prenom = p; }
    public String getAdresse()             { return adresse; }
    public void   setAdresse(String a)     { this.adresse = a; }
    public String getEmail()               { return email; }
    public void   setEmail(String e)       { this.email = e; }
    public String getMdp()                 { return mdp; }
    public void   setMdp(String m)         { this.mdp = m; }
    public String getTelephone()           { return telephone; }
    public void   setTelephone(String t)   { this.telephone = t; }

    public String toString() {
        return prenom + " " + nom + " (" + email + ")";
    }
}
