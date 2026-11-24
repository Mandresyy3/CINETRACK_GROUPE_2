package Controleur;

import java.util.HashMap;
import java.util.List;

import Modele.Film;
import exceptions.MetierException;
import exceptions.NoteInvalideException;
import service.FilmService;


public class FilmControleur {

    private final FilmService filmService;

    // Injection par constructeur : le contrôleur reçoit son Service, il ne le fabrique pas
    public FilmControleur(FilmService filmService) {
        this.filmService = filmService;
    }

    public boolean ajouter(HashMap<String, String> films) {
        try {
            filmService.ajouterFilm(creeFilms(films));
            return true;
        } catch (NoteInvalideException nie) {
            System.out.println(nie.getLocalizedMessage());
            return false;
        } catch (MetierException me) {
            System.err.println(me.getMessage());
            return false;
        }
    }

    private Film creeFilms(HashMap<String, String> films) {
        try {
            return new Film(films.get("Titre"),
                    films.get("Genre"),
                    Integer.parseInt(films.get("Durée (minutes)")),
                    Double.parseDouble(films.get("Note")),
                    films.get("Synopsys"));
        } catch (Exception e) {
            System.err.println("messgae " + e.getCause() + "    " + e.getLocalizedMessage());
        }
        return new Film();
    }

    public List<Film> chercherParTitre(String titre) {
        return filmService.rechercherParTitre(titre);
    }

    public List<Film> chercherParGenre(String genre) {
        return filmService.rechercherParGenre(genre);
    }

    public List<Film> recupereFilm() {
        return filmService.listerFilms();
    }

    public boolean supprimerFilm(int id) {
        try {
            filmService.supprimerFilm(id);
            return true;
        } catch (MetierException me) {
            System.err.println(me.getMessage());
            return false;
        }
    }

    public boolean modifierFilm(Film film) {
        try {
            filmService.modifierFilm(film);
            return true;
        } catch (MetierException me) {
            System.err.println(me.getMessage());
            return false;
        }
    }
}