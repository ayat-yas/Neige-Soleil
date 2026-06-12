package modele;

/**
 * Entite Gite — correspond a la table gite.
 */
public class Gite {

    private int    idgite;
    private String adresse;
    private int    surface;
    private int    nbpieces;
    private int    loyer;
    private int    idproprio;
    private String nomProprio;

    public Gite() {}

    public Gite(int idgite, String adresse, int surface,
                int nbpieces, int loyer, int idproprio) {
        this.idgite    = idgite;
        this.adresse   = adresse;
        this.surface   = surface;
        this.nbpieces  = nbpieces;
        this.loyer     = loyer;
        this.idproprio = idproprio;
    }

    public int    getIdgite()              { return idgite; }
    public void   setIdgite(int id)        { this.idgite = id; }
    public String getAdresse()             { return adresse; }
    public void   setAdresse(String a)     { this.adresse = a; }
    public int    getSurface()             { return surface; }
    public void   setSurface(int s)        { this.surface = s; }
    public int    getNbpieces()            { return nbpieces; }
    public void   setNbpieces(int n)       { this.nbpieces = n; }
    public int    getLoyer()               { return loyer; }
    public void   setLoyer(int l)          { this.loyer = l; }
    public int    getIdproprio()           { return idproprio; }
    public void   setIdproprio(int id)     { this.idproprio = id; }
    public String getNomProprio()          { return nomProprio; }
    public void   setNomProprio(String n)  { this.nomProprio = n; }

    public String toString() {
        return adresse + " (" + loyer + " euros/j)";
    }
}
