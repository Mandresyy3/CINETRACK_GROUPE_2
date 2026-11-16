package service;

import java.util.List;

import Modele.Film;
import dao.FilmDAO;
import exceptions.AccesDonneesException;
import exceptions.NoteInvalideException;

public class FilmService {

    private final FilmDAO filmDAO;

    // Injection par constructeur : le Service reçoit son DAO, il ne le fabrique pas
    public FilmService(FilmDAO filmDAO) {
        this.filmDAO = filmDAO;
    }

    public List<Film> listerFilms() {
        return filmDAO.findAll();
    }

    public List<Film> rechercherParTitre(String titre) {
        return filmDAO.rechercherParTitre(titre);
    }

    public List<Film> rechercherParGenre(String genre) {
        return filmDAO.rechercherParGenre(genre);
    }

    public void ajouterFilm(Film film) {
        verifierNote(film);
        if (!filmDAO.ajouter(film)) {
            throw new AccesDonneesException("Impossible d'enregistrer le film.");
        }
    }

    public void modifierFilm(Film film) {
        verifierNote(film);
        if (!filmDAO.update(film)) {
            throw new AccesDonneesException("Impossible de modifier le film.");
        }
    }

    public void supprimerFilm(int id) {
        if (!filmDAO.delete(id)) {
            throw new AccesDonneesException("Impossible de supprimer le film.");
        }
    }
    // Règle métier : la note est comprise entre 0 et 10
    private void verifierNote(Film film) {
        if (film.getNote() < 0 || film.getNote() > 10) {
            throw new NoteInvalideException(film.getNote());
        }
    }
}