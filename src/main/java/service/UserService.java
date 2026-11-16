package service;

import java.util.List;

import Modele.UserModele;
import dao.UserDAO;
import exceptions.AccesDonneesException;
import exceptions.MetierException;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public List<UserModele> listerUtilisateurs() {
        return userDAO.getAll();
    }

    public void ajouterUtilisateur(UserModele user) {
        if (user.getUserName() == null || user.getUserName().isBlank()) {
            throw new MetierException("Le login est obligatoire.");
        }
        if (user.getMp() == null || user.getMp().isBlank()) {
            throw new MetierException("Le mot de passe est obligatoire.");
        }
        if (!userDAO.ajouter(user)) {
            throw new AccesDonneesException("Impossible de créer le compte (le login existe peut-être déjà).");
        }
    }

    public void modifierUtilisateur(UserModele user) {
        if (user.getUserName() == null || user.getUserName().isBlank()) {
            throw new MetierException("Le login est obligatoire.");
        }
        if (!userDAO.update(user)) {
            throw new AccesDonneesException("Impossible de modifier le compte.");
        }
    }

    public void supprimerUtilisateur(String login) {
        if (!userDAO.suprimer(login)) {
            throw new AccesDonneesException("Impossible de supprimer le compte.");
        }
    }

    // Une question, pas une action : le résultat est un booléen
    public boolean verifierIdentifiants(UserModele user) {
        return userDAO.verfier(user);
    }

    public boolean estAdministrateur(UserModele user) {
        return userDAO.isAdmin(user);
    }

    public int identifiantDe(UserModele user) {
        return userDAO.getUserID(user);
    }
}