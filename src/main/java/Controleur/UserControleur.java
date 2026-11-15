package Controleur;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import Modele.UserDAOPostgres;
import Modele.UserModele;
import View.FenetrePrincipale;
import dao.UserDAO;

public class UserControleur {

    private UserDAO dao;

    public UserControleur() {
        dao = new UserDAOPostgres();
    }

    public boolean ajouterGestionaire(HashMap<String, String> user) throws NoSuchAlgorithmException {
        UserModele gestionnaire = new UserModele(user.get("login"), user.get("mp"), false);
        return dao.ajouter(gestionnaire);
    }

    public boolean verifierConnexion(HashMap<String, String> user) {
        UserModele gestionnaire = new UserModele(user.get("login"), user.get("mp"), false);

        boolean connecte = dao.verfier(gestionnaire);

        if (connecte) {
            // Mot de passe correct : on retient qui est connecté (sert à l'audit)
            isAdmin(gestionnaire);
            FenetrePrincipale.user_id = dao.getUserID(gestionnaire);
        } else {
            // Échec : personne n'est connecté
            FenetrePrincipale.user_id = 0;
            FenetrePrincipale.isadmin = false;
        }
        return connecte;
    }

    public void isAdmin(UserModele gestionnaire) {
        // TODO Auto-generated method stub
        FenetrePrincipale.isadmin = dao.isAdmin(gestionnaire);
    }

    public boolean update(int id,HashMap<String,String> User){

        UserModele user = new UserModele(User.get("name"),
                                      null,
                                      Boolean.parseBoolean(User.get("admin")),
                                      id);
        return dao.update(user);
    }

    public List<UserModele> getAll() {
        return dao.getAll();
    }
}