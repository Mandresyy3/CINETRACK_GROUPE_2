package Modele;
 
public class Seance {
    // Attributs privés
    private int idSeance;
    private int idFilm;      // Clé étrangère vers films
    private int note;      // Clé étrangère vers films
    private int idSalle;     // Clé étrangère vers salles
    private String dateSeance;
    private String heure;
    private int placesDispo;
    private String titreFilm;
    // 1. Constructeur vide
    public Seance() {}//pour creer un objet vierge
 
    // 2. Constructeur complet (avec ID) - utilisé pour la lecture (SELECT)
    public Seance(int idSeance,String titreFilm, int idSalle, String dateSeance, String heure, int placesDispo) {
        this.idSeance = idSeance;
        this.idSalle = idSalle;
        this.dateSeance = dateSeance;
        this.heure = heure;
        this.placesDispo = placesDispo;
        this.titreFilm = titreFilm;
    }//donc  c'est pour voir les seances deja existantes dans la base de données
 
    // 3. Constructeur sans ID - utilisé pour l'insertion (INSERT) [2]
    public Seance(int idFilm, int idSalle, String dateSeance, String heure, int placesDispo) {
        this.idFilm = idFilm;
        this.idSalle = idSalle;
        this.dateSeance = dateSeance;
        this.heure = heure;
        this.placesDispo = placesDispo;
    }

    public Seance(int note,String titreFilm, int idSalle, String dateSeance, String heure) {
        this.note = note;
        this.idSalle = idSalle;
        this.dateSeance = dateSeance;
        this.heure = heure;
        this.titreFilm = titreFilm;
    }
 
    // Getters et Setters obligatoires
    public int getIdSeance() { return idSeance; }
    public void setIdSeance(int id) { this.idSeance = id; }
 
    public int getIdFilm() { return idFilm; }
    public void setIdFilm(int idFilm) { this.idFilm = idFilm; }
 
    public int getIdSalle() { return idSalle; }
    public void setIdSalle(int idSalle) { this.idSalle = idSalle; }
 
    public String getDateSeance() { return dateSeance; }
    public void setDateSeance(String date) { this.dateSeance = date; }
 
    public String getHeure() { return heure; }
    public void setHeure(String heure) { this.heure = heure; }
 
    public int getPlacesDispo() { return placesDispo; }
    public void setPlacesDispo(int places) { this.placesDispo = places; }

    public String getTitre() { return titreFilm; }
    public void setTitre(String titreFilm) { this.titreFilm = titreFilm; }

    public int getNote() { return note; }
    public void note(int note) { this.note = note; }




// 4. Constructeur pour UPDATE (avec idSeance + idFilm)
public Seance(int idSeance, int idFilm, int idSalle, String dateSeance, String heure, int placesDispo) {
    this.idSeance = idSeance;
    this.idFilm = idFilm;
    this.idSalle = idSalle;
    this.dateSeance = dateSeance;
    this.heure = heure;
    this.placesDispo = placesDispo;
}

}