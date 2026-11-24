package main;

import javax.swing.SwingUtilities;
import controller.AccueilController;

import controller.ConnexionController;
import controller.MainController;

import Controleur.FilmControleur;
import Modele.FilmDAOPostgres;
import dao.FilmDAO;
import service.FilmService;
import controller.FilmController;

import View.Connexion;
import View.FenetrePrincipale;

import Controleur.SalleControler;
import controller.SalleController;
import Modele.SalleDAOPostgres;
import dao.SalleDAO;
import service.SalleService;

import Controleur.SeanceControleur;
import controller.SeanceController;
import Modele.SeanceDAOPostgres;
import service.SeanceService;

import Controleur.UserControleur;
import Modele.UserDAOPostgres;
import service.UserService;

import controller.UserController;
import Modele.AuditLogDAOPostgres;
import service.AuditLogService;
import controller.AuditLogController;

public class Main {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {

        @Override
        public void run() {
        new Connexion(() -> new FenetrePrincipale());
        }

        });
    }
    public static FilmControleur filmControleur() {
        FilmDAO filmDAO = new FilmDAOPostgres();
        FilmService filmService = new FilmService(filmDAO);
        return new FilmControleur(filmService);
    }
    public static FilmService filmService() {
        return new FilmService(new FilmDAOPostgres());
    }
    public static SalleService salleService() {
        return new SalleService(new SalleDAOPostgres());
    }

    public static SeanceService seanceService() {
        return new SeanceService(new SeanceDAOPostgres());
    }

    public static UserService userService() {
        return new UserService(new UserDAOPostgres());
    }

    public static AuditLogService auditLogService() {
        return new AuditLogService(new AuditLogDAOPostgres());
    }
    /**
     * Fabrique les contrôleurs JavaFX avec leur Service (utilisée par FXMLLoader).
     * Pour chaque nouvel écran, on ajoute une ligne ici.
     */
    public static Object creerControleur(Class<?> type) {
        if (type == FilmController.class) {
            return new FilmController(filmService());
        }
        if (type == SalleController.class) {
            return new SalleController(salleService());
        }
        if (type == SeanceController.class) {
            return new SeanceController(seanceService(), filmService(), salleService());
        }
        if (type == UserController.class) {
            return new UserController(userService());
        }
        if (type == AuditLogController.class) {
            return new AuditLogController(auditLogService());
        }
        if (type == ConnexionController.class) {
            return new ConnexionController(userService());
        }
        if (type == MainController.class) {
            return new MainController();
        }
        if (type == AccueilController.class) {
            return new AccueilController(seanceService(), salleService());
        }
        throw new IllegalArgumentException("Contrôleur inconnu : " + type);
    }
    public static SalleControler salleControleur() {
        SalleDAO salleDAO = new SalleDAOPostgres();
        SalleService salleService = new SalleService(salleDAO);
        return new SalleControler(salleService);
    }
    public static SeanceControleur seanceControleur() {
        SeanceService seanceService = new SeanceService(new SeanceDAOPostgres());
        FilmService filmService = new FilmService(new FilmDAOPostgres());
        SalleService salleService = new SalleService(new SalleDAOPostgres());
        return new SeanceControleur(seanceService, filmService, salleService);
    }
    public static UserControleur userControleur() {
        UserService userService = new UserService(new UserDAOPostgres());
        return new UserControleur(userService);
    }
}
