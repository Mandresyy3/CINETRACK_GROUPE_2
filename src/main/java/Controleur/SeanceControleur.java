package Controleur;

import java.util.ArrayList;
import java.util.List;

import Modele.Film;
import Modele.Salle;
import Modele.Seance;
import exceptions.MetierException;
import service.FilmService;
import service.SalleService;
import service.SeanceService;

/**
 * SeanceControleur : coordination entre la vue des séances et les Services.
 * Aucun SQL ni règle métier ici : le contrôleur lit la saisie, appelle le Service
 * et traduit le résultat en message pour l'écran.
 */
public class SeanceControleur {

    private final SeanceService seanceService;
    private final FilmService filmService;
    private final SalleService salleService;

    // Injection par constructeur : le contrôleur reçoit ses Services
    public SeanceControleur(SeanceService seanceService, FilmService filmService, SalleService salleService) {
        this.seanceService = seanceService;
        this.filmService = filmService;
        this.salleService = salleService;
    }

    public List<Salle> chargerSalles() {
        try {
            return salleService.listerSalles();
        } catch (Exception e) {
            System.err.println("Erreur chargement salles : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Film> chargerFilms() {
        try {
            return filmService.listerFilms();
        } catch (Exception e) {
            System.err.println("Erreur chargement films : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Seance> chargerSeances() {
        try {
            return seanceService.listerSeances();
        } catch (Exception e) {
            System.err.println("Erreur chargement séances : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Séances d'une date donnée (format YYYY-MM-DD).
     */
    public List<Seance> chargerSeancesParDate(String date) {
        try {
            return seanceService.rechercherParDate(date);
        } catch (Exception e) {
            System.err.println("Erreur recherche par date : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Séances dont le film contient le titre saisi.
     */
    public List<Seance> chargerSeancesParFilm(String titre) {
        try {
            return seanceService.rechercherParTitre(titre);
        } catch (Exception e) {
            System.err.println("Erreur recherche séances par film : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Ajoute une séance. Retourne "OK" si succès, sinon un message d'erreur.
     */
    public String ajouterSeance(int idFilm, int idSalle, String date, String heure, String placesStr) {
        int placesDispo;
        try {
            placesDispo = Integer.parseInt(placesStr.trim());
        } catch (NumberFormatException e) {
            return "Le nombre de places doit être un entier valide.";
        }

        Seance seance = new Seance(idFilm, idSalle, nettoyer(date), nettoyer(heure), placesDispo);

        try {
            seanceService.ajouterSeance(seance);
            return "OK";
        } catch (MetierException me) {
            return me.getMessage();
        }
    }

    /**
     * Modifie une séance. Retourne "OK" si succès, sinon un message d'erreur.
     */
    public String modifierSeance(int idSeance, int idFilm, int idSalle, String date, String heure, String placesStr) {
        int placesDispo;
        try {
            placesDispo = Integer.parseInt(placesStr.trim());
        } catch (NumberFormatException e) {
            return "Le nombre de places doit être un entier valide.";
        }

        Seance seance = new Seance(idSeance, idFilm, idSalle, nettoyer(date), nettoyer(heure), placesDispo);

        try {
            seanceService.modifierSeance(seance);
            return "OK";
        } catch (MetierException me) {
            return me.getMessage();
        }
    }

    public boolean annulerSeance(int idSeance) {
        try {
            seanceService.annulerSeance(idSeance);
            return true;
        } catch (MetierException me) {
            System.err.println(me.getMessage());
            return false;
        }
    }

    public Seance prochaineSeance() {
        return seanceService.prochaineSeance();
    }

    public Seance Meilleurnote() {
        return seanceService.meilleureNote();
    }

    // Nettoyage de la saisie (espaces), pas une règle métier
    private String nettoyer(String texte) {
        return texte == null ? null : texte.trim();
    }
}