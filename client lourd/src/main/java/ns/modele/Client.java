package ns.modele;

public class Client {

    private int     idclient;
    private String  nom;
    private String  prenom;
    private String  adresse;
    private String  email;
    private String  mdp;
    private long    telephone;
    private Integer idcategorie; // nullable — lié à la table categorie

    public Client() {}

    public Client(int idclient, String nom, String prenom,
                  String adresse, String email, String mdp,
                  long telephone, Integer idcategorie) {
        this.idclient    = idclient;
        this.nom         = nom;
        this.prenom      = prenom;
        this.adresse     = adresse;
        this.email       = email;
        this.mdp         = mdp;
        this.telephone   = telephone;
        this.idcategorie = idcategorie;
    }

    public int     getIdclient()              { return idclient; }
    public void    setIdclient(int id)        { this.idclient = id; }
    public String  getNom()                   { return nom; }
    public void    setNom(String n)           { this.nom = n; }
    public String  getPrenom()                { return prenom; }
    public void    setPrenom(String p)        { this.prenom = p; }
    public String  getAdresse()               { return adresse; }
    public void    setAdresse(String a)       { this.adresse = a; }
    public String  getEmail()                 { return email; }
    public void    setEmail(String e)         { this.email = e; }
    public String  getMdp()                   { return mdp; }
    public void    setMdp(String m)           { this.mdp = m; }
    public long    getTelephone()             { return telephone; }
    public void    setTelephone(long t)       { this.telephone = t; }
    public Integer getIdcategorie()           { return idcategorie; }
    public void    setIdcategorie(Integer id) { this.idcategorie = id; }

    @Override
    public String toString() { return prenom + " " + nom + " (" + email + ")"; }
}
