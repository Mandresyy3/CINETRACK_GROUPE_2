package dao;

import java.util.List;

import Modele.Seance;

public interface SeanceDAO {

    List<Seance> findAll();

    List<Seance> findByTitre(String titre);

    List<Seance> findByDate(String date);

    boolean insert(Seance s);

    boolean update(Seance s);

    boolean delete(int id);

    Seance prochaineSeance();

    Seance Meilleurnote();
}