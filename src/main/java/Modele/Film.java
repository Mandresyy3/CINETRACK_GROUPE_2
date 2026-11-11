package Modele;
/**
 * Classe représentant un film du catalogue CinéTrack.
 * Contient toutes les informations d'un film : titre, genre, durée, note et synopsis.
 */
public class Film {

    // -------------------------------------------------------------------------
    // Attributs privés
    // -------------------------------------------------------------------------
    private int idFilm;
    private String titre;
    private String genre;
    private int duree;       // en minutes
    private double note;     // entre 0 et 10
    private String synopsis;

    // -------------------------------------------------------------------------
    // Constructeur vide
    // -------------------------------------------------------------------------
    public Film() {
    }

    // -------------------------------------------------------------------------
    // Constructeur complet (avec id) — utilisé pour les SELECT
    // -------------------------------------------------------------------------
    public Film(int idFilm, String titre, String genre, int duree, double note, String synopsis) {
        this.idFilm   = idFilm;
        this.titre    = titre;
        this.genre    = genre;
        this.duree    = duree;
        this.note     = note;
        this.synopsis = synopsis;
    }

    // -------------------------------------------------------------------------
    // Constructeur sans id — utilisé pour les INSERT
    // -------------------------------------------------------------------------
    public Film(String titre, String genre, int duree, double note, String synopsis) {
        this.titre    = titre;
        this.genre    = genre;
        this.duree    = duree;
        this.note     = note;
        this.synopsis = synopsis;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /** @return l'identifiant du film */
    public int getIdFilm() {
        return idFilm;
    }

    /** @return le titre du film */
    public String getTitre() {
        return titre;
    }

    /** @return le genre du film */
    public String getGenre() {
        return genre;
    }

    /** @return la durée du film en minutes */
    public int getDuree() {
        return duree;
    }

    /** @return la note du film (entre 0 et 10) */
    public double getNote() {
        return note;
    }

    /** @return le synopsis du film */
    public String getSynopsis() {
        return synopsis;
    }

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    /** @param idFilm l'identifiant à définir */
    public void setIdFilm(int idFilm) {
        this.idFilm = idFilm;
    }

    /** @param titre le titre à définir */
    public void setTitre(String titre) {
        this.titre = titre;
    }

    /** @param genre le genre à définir */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /** @param duree la durée en minutes à définir */
    public void setDuree(int duree) {
        this.duree = duree;
    }

    // -------------------------------------------------------------------------
    // getDureeFormatee — Retourne la durée au format "Xh Ymin"
    // -------------------------------------------------------------------------

    /**
     * Retourne la durée du film dans un format lisible.
     * Exemple : 115 minutes → "1h 55min"
     *
     * @return la durée formatée en heures et minutes
     */
    public String getDureeFormatee() {
        int heures  = duree / 60;   // division entière
        int minutes = duree % 60;   // reste = minutes
        return heures + "h " + minutes + "min";
    }

}