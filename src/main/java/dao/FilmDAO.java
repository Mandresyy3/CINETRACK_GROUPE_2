package dao;

import java.util.List;

import Modele.Film;

public interface FilmDAO {

    List<Film> findAll();

    List<Film> rechercherParGenre(String genre);

    List<Film> rechercherParTitre(String motCle);

    boolean ajouter(Film film);

    boolean update(Film film);

    boolean delete(int id);
}