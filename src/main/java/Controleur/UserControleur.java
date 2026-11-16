package Controleur;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import Modele.UserModele;
import View.FenetrePrincipale;
import exceptions.MetierException;
import service.UserService;

public class UserControleur {

    private final UserService userService;

    // Injection par constructeur : le contrôleur reçoit son Service
    public UserControleur(UserService userService) {
        this.userService = userService;
    }

    // "throws" conservé : la vue PaneauGestionnaire attrape encore cette exception
    public boolean ajouterGestionaire(HashMap<String, String> user) throws NoSuchAlgorithmException {
        UserModele gestionnaire = new UserModele(user.get("login"), user.get("mp"), false);
        try {
            userService.ajouterUtilisateur(gestionnaire);
            return true;
        } catch (MetierException me) {
            System.err.println(me.getMessage());
            return false;
        }
    }

    public boolean verifierConnexion(HashMap<String, String> user) {
        UserModele gestionnaire = new UserModele(user.get("login"), user.get("mp"), false);

        boolean connecte = userService.verifierIdentifiants(gestionnaire);

        if (connecte) {
            // Mot de passe correct : on retient qui est connecté (sert à l'audit)
            isAdmin(gestionnaire);
            FenetrePrincipale.user_id = userService.identifiantDe(gestionnaire);
        } else {
            // Échec : personne n'est connecté
            FenetrePrincipale.user_id = 0;
            FenetrePrincipale.isadmin = false;
        }
        return connecte;
    }

    public void isAdmin(UserModele gestionnaire) {
        FenetrePrincipale.isadmin = userService.estAdministrateur(gestionnaire);
    }

    public boolean update(int id, HashMap<String, String> User) {
        UserModele user = new UserModele(User.get("name"),
                null,
                Boolean.parseBoolean(User.get("admin")),
                id);
        try {
            userService.modifierUtilisateur(user);
            return true;
        } catch (MetierException me) {
            System.err.println(me.getMessage());
            return false;
        }
    }

    public boolean supprimer(String login) {
        try {
            userService.supprimerUtilisateur(login);
            return true;
        } catch (MetierException me) {
            System.err.println(me.getMessage());
            return false;
        }
    }

    public List<UserModele> getAll() {
        return userService.listerUtilisateurs();
    }
}