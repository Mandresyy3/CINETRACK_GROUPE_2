package controller;

import Modele.Salle;
import Modele.Seance;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import service.SalleService;
import service.SeanceService;

public class AccueilController {

    @FXML private Label lblProchaineFilm;
    @FXML private Label lblProchaineDetail;
    @FXML private Label lblMeilleurFilm;
    @FXML private Label lblMeilleurDetail;
    @FXML private Label lblMeilleurDate;

    private final SeanceService seanceService;
    private final SalleService salleService;

    // Injection par constructeur : Main fabrique le contrôleur avec ses Services
    public AccueilController(SeanceService seanceService, SalleService salleService) {
        this.seanceService = seanceService;
        this.salleService = salleService;
    }

    @FXML
    private void initialize() {
        afficher();
    }

    @FXML
    private void onActualiser() {
        afficher();
    }

    private void afficher() {
        afficherProchaineSeance();
        afficherMeilleurFilm();
    }

    private void afficherProchaineSeance() {
        Seance seance = seanceService.prochaineSeance();

        // Le DAO renvoie une séance vide (sans titre) quand il n'y en a aucune
        if (seance.getTitre() == null) {
            lblProchaineFilm.setText("Aucune séance disponible");
            lblProchaineDetail.setText("");
            return;
        }
        lblProchaineFilm.setText(seance.getTitre());
        lblProchaineDetail.setText(nomSalle(seance.getIdSalle()) + " · "
                + seance.getDateSeance() + " à " + heure(seance.getHeure()));
    }

    private void afficherMeilleurFilm() {
        Seance seance = seanceService.meilleureNote();

        if (seance.getTitre() == null) {
            lblMeilleurFilm.setText("Aucune séance disponible");
            lblMeilleurDetail.setText("");
            lblMeilleurDate.setText("");
            return;
        }
        lblMeilleurFilm.setText(seance.getTitre());
        lblMeilleurDetail.setText("Note " + seance.getNote() + "/10 · " + nomSalle(seance.getIdSalle()));
        lblMeilleurDate.setText(seance.getDateSeance() + " à " + heure(seance.getHeure()));
    }

    /** Le nom de la salle à partir de son numéro (le numéro si elle est introuvable). */
    private String nomSalle(int idSalle) {
        return salleService.listerSalles().stream()
                .filter(salle -> salle.getIdSalle() == idSalle)
                .map(Salle::getNom)
                .findFirst()
                .orElse("Salle " + idSalle);
    }

    /** 20:00:00 devient 20:00. */
    private String heure(String heure) {
        return heure != null && heure.length() > 5 ? heure.substring(0, 5) : heure;
    }
}