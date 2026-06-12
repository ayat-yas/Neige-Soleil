package modele;

/**
 * Entite Reservation — correspond a la table reservation.
 */
public class Reservation {

    private int     idreservation;
    private String  datedebut;
    private String  datefin;
    private int     prix;
    private String  transport;
    private boolean assurance;
    private int     idclient;
    private int     idgite;
    private String  statut_r;
    private String  rapport;
    private String  nomClient;
    private String  adresseGite;

    public Reservation() {}

    public Reservation(int idreservation, String datedebut, String datefin,
                       int prix, String transport, boolean assurance,
                       int idclient, int idgite, String statut_r, String rapport) {
        this.idreservation = idreservation;
        this.datedebut     = datedebut;
        this.datefin       = datefin;
        this.prix          = prix;
        this.transport     = transport;
        this.assurance     = assurance;
        this.idclient      = idclient;
        this.idgite        = idgite;
        this.statut_r      = statut_r;
        this.rapport       = rapport;
    }

    public int     getIdreservation()            { return idreservation; }
    public void    setIdreservation(int id)      { this.idreservation = id; }
    public String  getDatedebut()                { return datedebut; }
    public void    setDatedebut(String d)        { this.datedebut = d; }
    public String  getDatefin()                  { return datefin; }
    public void    setDatefin(String d)          { this.datefin = d; }
    public int     getPrix()                     { return prix; }
    public void    setPrix(int p)                { this.prix = p; }
    public String  getTransport()               { return transport; }
    public void    setTransport(String t)        { this.transport = t; }
    public boolean isAssurance()                 { return assurance; }
    public void    setAssurance(boolean a)       { this.assurance = a; }
    public int     getIdclient()                 { return idclient; }
    public void    setIdclient(int id)           { this.idclient = id; }
    public int     getIdgite()                   { return idgite; }
    public void    setIdgite(int id)             { this.idgite = id; }
    public String  getStatut_r()                 { return statut_r; }
    public void    setStatut_r(String s)         { this.statut_r = s; }
    public String  getRapport()                  { return rapport; }
    public void    setRapport(String r)          { this.rapport = r; }
    public String  getNomClient()                { return nomClient; }
    public void    setNomClient(String n)        { this.nomClient = n; }
    public String  getAdresseGite()              { return adresseGite; }
    public void    setAdresseGite(String a)      { this.adresseGite = a; }

    public String toString() {
        return "#" + idreservation + " - " + nomClient + " / " + adresseGite;
    }
}
