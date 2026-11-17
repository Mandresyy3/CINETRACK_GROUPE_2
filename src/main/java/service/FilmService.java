package service;

import Modele.Film;
import Modele.dao.FilmDao;

import java.util.List;
import java.util.Objects;

/** Applies business rules before film data reaches persistence. */
public class FilmService {
    private final FilmDao films;

    public FilmService(FilmDao films) {
        this.films = Objects.requireNonNull(films, "Le dépôt de films est obligatoire.");
    }

    public List<Film> tousLesFilms() {
        return films.findAll();
    }

    public List<Film> rechercherParTitre(String motCle) {
        return films.rechercherParTitre(termeRecherche(motCle, "Le titre recherché"));
    }

    public List<Film> rechercherParGenre(String genre) {
        return films.rechercherParGenre(termeRecherche(genre, "Le genre recherché"));
    }

    public boolean ajouter(Film film) {
        valider(film);
        return films.ajouter(film);
    }

    public boolean modifier(Film film) {
        valider(film);
        if (film.getIdFilm() <= 0) {
            throw new IllegalArgumentException("L'identifiant du film doit être positif.");
        }
        return films.update(film);
    }

    public boolean supprimer(int idFilm) {
        if (idFilm <= 0) {
            throw new IllegalArgumentException("L'identifiant du film doit être positif.");
        }
        return films.delete(idFilm);
    }

    private static void valider(Film film) {
        Objects.requireNonNull(film, "Le film est obligatoire.");
        exigerTexte(film.getTitre(), "Le titre");
        exigerTexte(film.getGenre(), "Le genre");
        if (film.getDuree() <= 0) {
            throw new IllegalArgumentException("La durée du film doit être supérieure à zéro.");
        }
        if (!Double.isFinite(film.getNote()) || film.getNote() < 0 || film.getNote() > 10) {
            throw new exceptions.NoteInvalideException(film.getNote());
        }
    }

    private static String termeRecherche(String valeur, String libelle) {
        Objects.requireNonNull(valeur, libelle + " est obligatoire.");
        return valeur.trim();
    }

    private static void exigerTexte(String valeur, String libelle) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException(libelle + " ne peut pas être vide.");
        }
    }
}
