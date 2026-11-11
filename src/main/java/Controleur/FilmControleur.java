package Controleur;

import java.util.HashMap;
import java.util.List;

import Modele.Film;
import Modele.FilmDAO;
import exceptions.NoteInvalideException;

public class FilmControleur {
    FilmDAO dao;

    public FilmControleur() {
        dao = new FilmDAO();
    }

    public boolean ajouter(HashMap<String, String> films) {
        try {
            Double note = Double.parseDouble(films.get("Note"));
            if (note >= 0.0 && note <= 10) {
                return dao.ajouter(creeFilms(films));
            } else {
                throw new NoteInvalideException(0);
            }
        } catch (NoteInvalideException nie) {
            System.out.println(nie.getLocalizedMessage() + " test 2");
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
            ;
        }
        return new Film();
    }

    public List<Film> chercherParTitre(String titre) {
        return dao.rechercherParTitre(titre);
    }

    public List<Film> chercherParGenre(String genre) {
        return dao.rechercherParGenre(genre);
    }

    public List<Film> recupereFilm() {
        return dao.findAll();
    }
    public boolean supprimerFilm(int id) {
        return dao.delete(id);
    }
    public boolean modifierFilm(Film film) {
    	return dao.update(film);				
    }
}
