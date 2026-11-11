package Controleur;

import Modele.Film;
import Modele.FilmDAO;
import Modele.Salle;
import Modele.SalleDAO;
import Modele.Seance;
import Modele.SeanceDAO;
import exceptions.FilmIntrouvableException;
import java.util.ArrayList;
import java.util.List;

/**
 * SeanceControleur — Couche de coordination entre FenetreSeance et les DAO.
 * Contient toute la logique de validation pour les séances.
 * Aucun code SQL ni composant Swing ne doit apparaître ici.
 */
public class SeanceControleur {

    // -------------------------------------------------------------------------
    // Attributs
    // -------------------------------------------------------------------------

    /** DAO pour accéder aux séances */
    private SeanceDAO seanceDAO;

    /** DAO pour accéder aux films */
    private FilmDAO filmDAO;

    private SalleDAO salleDAO = new SalleDAO();

    // -------------------------------------------------------------------------
    // Constructeur
    // -------------------------------------------------------------------------

    /**
     * Crée le contrôleur et initialise les DAO.
     */
    public SeanceControleur() {
        this.seanceDAO = new SeanceDAO();
        this.filmDAO   = new FilmDAO();
    }

    public List<Salle> chargerSalles() {
        try {
            return salleDAO.findAll();
        } catch (Exception e) {
            System.err.println("Erreur chargement salles : " + e.getMessage());
            return new ArrayList<>();
        }
    }
    // -------------------------------------------------------------------------
    // chargerSeances — Charge toutes les séances
    // -------------------------------------------------------------------------

    /**
     * Retourne toutes les séances triées par date et heure.
     *
     * @return Liste de toutes les séances
     */
    public List<Seance> chargerSeances() {
        try {
            return seanceDAO.findAll();
        } catch (Exception e) {
            System.err.println("Erreur chargement séances : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // -------------------------------------------------------------------------
    // chargerSeancesParDate — Filtre les séances par date
    // -------------------------------------------------------------------------

    /**
     * Retourne les séances d'une date donnée.
     * Correspond au bouton "Rechercher" dans FenetreSeance.
     *
     * @param date Date au format YYYY-MM-DD
     * @return Liste des séances ou liste vide si date invalide
     */
    public List<Seance> chargerSeancesParDate(String date) {
        try {
            return seanceDAO.findByDate(date.trim());
        } catch (Exception e) {
            System.err.println("Erreur recherche par date : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // -------------------------------------------------------------------------
    // chargerSeancesParFilm — Filtre les séances par film
    // -------------------------------------------------------------------------

    /**
     * Retourne toutes les séances d'un film donné.
     *
     * @param idFilm Identifiant du film
     * @return Liste des séances du film
     */
    public List<Seance> chargerSeancesParFilm(String titre) {
        try {
            return seanceDAO.findByTitre(titre);
        } catch (FilmIntrouvableException e) {
            System.err.println(e.getMessage());
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Erreur recherche séances par film : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // -------------------------------------------------------------------------
    // ajouterSeance — Valide et insère une nouvelle séance
    // Logique issue du diagramme d'activité et du diagramme de séquence
    // -------------------------------------------------------------------------

    /**
     * Valide les données saisies puis insère une nouvelle séance.
     * Retourne "OK" si succès, sinon un message d'erreur.
     *
     * @param idFilm    Identifiant du film sélectionné
     * @param idSalle   Identifiant de la salle sélectionnée
     * @param date      Date de la séance (YYYY-MM-DD)
     * @param heure     Heure de la séance (HH:MM)
     * @param placesStr Nombre de places (String depuis JTextField)
     * @return "OK" si succès, message d'erreur sinon
     */
    public String ajouterSeance(int idFilm, int idSalle, String date, String heure, String placesStr) {

        // --- Validation des champs vides ---
        if (date == null || date.trim().isEmpty() ||
            heure == null || heure.trim().isEmpty()) {
            return "Veuillez remplir tous les champs (date et heure obligatoires).";
        }

        // --- Conversion du nombre de places (dans le contrôleur, jamais dans la vue) ---
        int placesDispo;
        try {
            placesDispo = Integer.parseInt(placesStr.trim());
            if (placesDispo < 0) {
                return "Le nombre de places ne peut pas être négatif.";
            }
        } catch (NumberFormatException e) {
            return "Le nombre de places doit être un entier valide.";
        }

        // --- Construction de l'objet Seance sans id (constructeur pour INSERT) ---
        Seance seance = new Seance(idFilm, idSalle, date.trim(), heure.trim(), placesDispo);

        // --- Insertion en base ---
        boolean succes = seanceDAO.insert(seance);

        if (succes) {
            return "OK";
        } else {
            // Salle déjà réservée ou autre erreur
            return "Salle déjà réservée à ce créneau. Choisissez un autre horaire ou une autre salle.";
        }
    }

public String modifierSeance(int idSeance, int idFilm, int idSalle, String date, String heure, String placesStr) {

    // Validation des champs vides
    if (date == null || date.trim().isEmpty() ||
        heure == null || heure.trim().isEmpty()) {
        return "Veuillez remplir tous les champs.";
    }

    // Conversion places
    int placesDispo;
    try {
        placesDispo = Integer.parseInt(placesStr.trim());
        if (placesDispo < 0) {
            return "Le nombre de places ne peut pas être négatif.";
        }
    } catch (NumberFormatException e) {
        return "Le nombre de places doit être un entier valide.";
    }

    // Construction de l'objet avec l'id pour le WHERE
    Seance seance = new Seance(idSeance, idFilm, idSalle, date.trim(), heure.trim(), placesDispo);

    boolean succes = seanceDAO.update(seance);

    if (succes) {
        return "OK";
    } else {
        return "Erreur lors de la modification.";
    }
}    
    // -------------------------------------------------------------------------
    // annulerSeance — Supprime une séance existante
    // -------------------------------------------------------------------------

    /**
     * Supprime une séance par son identifiant.
     *
     * @param idSeance Identifiant de la séance à supprimer
     * @return true si suppression réussie
     */
    public boolean annulerSeance(int idSeance) {
        try {
            return seanceDAO.delete(idSeance);
        } catch (Exception e) {
            System.err.println("Erreur suppression séance : " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // chargerFilms — Liste des films pour les JComboBox
    // -------------------------------------------------------------------------

    /**
     * Retourne tous les films pour alimenter les listes déroulantes.
     *
     * @return Liste de tous les films
     */
    public List<Film> chargerFilms() {
        try {
            return filmDAO.findAll();
        } catch (Exception e) {
            System.err.println("Erreur chargement films : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Seance prochaineSeance(){
        return seanceDAO.prochaineSeance();
    }
    public Seance Meilleurnote(){
        return seanceDAO.Meilleurnote();
    }

}