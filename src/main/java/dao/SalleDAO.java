package dao;

import java.util.List;

import Modele.Salle;

public interface SalleDAO {

    List<Salle> findAll();

    boolean ajouter(Salle salle);

    boolean modifier(Salle salle);

    boolean supprimer(int id);

    boolean existeDeja(String nom, int idActuel);
}