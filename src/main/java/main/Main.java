package main;

import javax.swing.SwingUtilities;
import Controleur.FilmControleur;
import Modele.FilmDAOPostgres;
import dao.FilmDAO;
import service.FilmService;
import View.Connexion;
import View.FenetrePrincipale;
import Controleur.SalleControler;
import Modele.SalleDAOPostgres;
import dao.SalleDAO;
import service.SalleService;

import Controleur.SeanceControleur;
import Modele.SeanceDAOPostgres;
import service.SeanceService;

import Controleur.UserControleur;
import Modele.UserDAOPostgres;
import service.UserService;

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
