package Controleur;

import java.util.HashMap;
import java.util.List;

import Modele.Film;
import Modele.FilmDAO;
import service.FilmService;

public class FilmControleur {
    private final FilmService service;

    public FilmControleur() {
        this(new FilmService(new FilmDAO()));
    }

    public FilmControleur(FilmService service) {
        this.service = java.util.Objects.requireNonNull(service, "Le service de films est obligatoire.");
    }

    public boolean ajouter(HashMap<String, String> films) {
        try {
            return service.ajouter(creeFilms(films));
        } catch (IllegalArgumentException nie) {
            return false;
        }
    }

    private Film creeFilms(HashMap<String, String> films) {
        return new Film(films.get("Titre"),
                films.get("Genre"),
                Integer.parseInt(films.get("Durée (minutes)")),
                Double.parseDouble(films.get("Note")),
                films.get("Synopsis") != null ? films.get("Synopsis") : films.get("Synopsys"));
    }

    public List<Film> chercherParTitre(String titre) {
        return service.rechercherParTitre(titre);
    }

    public List<Film> chercherParGenre(String genre) {
        return service.rechercherParGenre(genre);
    }

    public List<Film> recupereFilm() {
        return service.tousLesFilms();
    }
    public boolean supprimerFilm(int id) {
        try {
            return service.supprimer(id);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
    public boolean modifierFilm(Film film) {
        try {
            return service.modifier(film);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
