package dao;

import java.util.List;

import Modele.UserModele;

public interface UserDAO {

    List<UserModele> getAll();

    boolean ajouter(UserModele user);

    boolean verfier(UserModele user);

    boolean suprimer(String userName);

    boolean isAdmin(UserModele user);

    boolean update(UserModele user);

    int getUserID(UserModele gestionnaire);
}