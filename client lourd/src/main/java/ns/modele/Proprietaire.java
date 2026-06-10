package ns.modele;

public class Proprietaire {

    private int    idproprio;
    private String nom;
    private String prenom;
    private String adresse;
    private String email;
    private String mdp;
    private long   telephone;
    private String statut; // 'privé' | 'public' | 'admin'

    public Proprietaire() {}

    public Proprietaire(int idproprio, String nom, String prenom,
                        String adresse, String email, String mdp,
                        long telephone, String statut) {
        this.idproprio = idproprio;
        this.nom       = nom;
        this.prenom    = prenom;
        this.adresse   = adresse;
        this.email     = email;
        this.mdp       = mdp;
        this.telephone = telephone;
        this.statut    = statut;
    }

    public int    getIdproprio()           { return idproprio; }
    public void   setIdproprio(int id)     { this.idproprio = id; }
    public String getNom()                 { return nom; }
    public void   setNom(String n)         { this.nom = n; }
    public String getPrenom()              { return prenom; }
    public void   setPrenom(String p)      { this.prenom = p; }
    public String getAdresse()             { return adresse; }
    public void   setAdresse(String a)     { this.adresse = a; }
    public String getEmail()               { return email; }
    public void   setEmail(String e)       { this.email = e; }
    public String getMdp()                 { return mdp; }
    public void   setMdp(String m)         { this.mdp = m; }
    public long   getTelephone()           { return telephone; }
    public void   setTelephone(long t)     { this.telephone = t; }
    public String getStatut()              { return statut; }
    public void   setStatut(String s)      { this.statut = s; }

    @Override
    public String toString() { return prenom + " " + nom + " (" + statut + ")"; }
}
