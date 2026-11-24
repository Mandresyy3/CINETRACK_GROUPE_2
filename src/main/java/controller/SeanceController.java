package controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Modele.Film;
import Modele.Salle;
import Modele.Seance;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import service.FilmService;
import service.SalleService;
import service.SeanceService;

public class SeanceController {

    @FXML private ComboBox<Film> comboFilm;
    @FXML private ComboBox<Salle> comboSalle;
    @FXML private DatePicker dateSeance;
    @FXML private TextField txtHeure;
    @FXML private TextField txtPlaces;
    @FXML private TextField txtRechercheFilm;
    @FXML private DatePicker dateRecherche;

    @FXML private TableView<Seance> tableSeances;
    @FXML private TableColumn<Seance, Integer> colId;
    @FXML private TableColumn<Seance, String> colFilm;
    @FXML private TableColumn<Seance, String> colSalle;
    @FXML private TableColumn<Seance, String> colDate;
    @FXML private TableColumn<Seance, String> colHeure;
    @FXML private TableColumn<Seance, Integer> colPlaces;

    @FXML private Label lblMessage;

    private final SeanceService seanceService;
    private final FilmService filmService;
    private final SalleService salleService;

    private final ObservableList<Seance> seances = FXCollections.observableArrayList();
    private final Map<Integer, String> nomsSalles = new HashMap<>();

    // Injection par constructeur : Main fabrique le contrôleur avec ses Services
    public SeanceController(SeanceService seanceService, FilmService filmService, SalleService salleService) {
        this.seanceService = seanceService;
        this.filmService = filmService;
        this.salleService = salleService;
    }

    @FXML
    private void initialize() {
        // Les listes déroulantes affichent le titre du film et le nom de la salle
        comboFilm.setConverter(new StringConverter<Film>() {
            @Override public String toString(Film film) { return film == null ? "" : film.getTitre(); }
            @Override public Film fromString(String texte) { return null; }
        });
        comboSalle.setConverter(new StringConverter<Salle>() {
            @Override public String toString(Salle salle) { return salle == null ? "" : salle.getNom(); }
            @Override public Salle fromString(String texte) { return null; }
        });

        colId.setCellValueFactory(new PropertyValueFactory<>("idSeance"));
        colFilm.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateSeance"));
        colHeure.setCellValueFactory(new PropertyValueFactory<>("heure"));
        colPlaces.setCellValueFactory(new PropertyValueFactory<>("placesDispo"));
        // La séance ne connaît que le numéro de la salle : on affiche son nom
        colSalle.setCellValueFactory(cellule -> new SimpleStringProperty(
                nomsSalles.getOrDefault(cellule.getValue().getIdSalle(),
                        String.valueOf(cellule.getValue().getIdSalle()))));

        tableSeances.setItems(seances);

        // Un clic sur une ligne recopie la séance dans le formulaire
        tableSeances.getSelectionModel().selectedItemProperty().addListener(
                (observable, ancienne, seance) -> {
                    if (seance != null) {
                        remplirFormulaire(seance);
                    }
                });

        chargerListes();
        chargerSeances();
    }

    /** Remplit les deux listes déroulantes et la table de correspondance des noms de salles. */
    private void chargerListes() {
        comboFilm.getItems().setAll(filmService.listerFilms());

        List<Salle> salles = salleService.listerSalles();
        comboSalle.getItems().setAll(salles);
        nomsSalles.clear();
        for (Salle salle : salles) {
            nomsSalles.put(salle.getIdSalle(), salle.getNom());
        }
    }

    private void chargerSeances() {
        seances.setAll(seanceService.listerSeances());
    }

    @FXML
    private void onActualiser() {
        chargerListes();
        chargerSeances();
        txtRechercheFilm.clear();
        dateRecherche.setValue(null);
        lblMessage.setText(seances.size() + " séance(s) affichée(s)");
    }

    @FXML
    private void onRechercherFilm() {
        String titre = txtRechercheFilm.getText().trim();
        if (titre.isEmpty()) {
            afficherErreur("Saisissez un titre (ou une partie du titre).");
            return;
        }
        seances.setAll(seanceService.rechercherParTitre(titre));
        lblMessage.setText(seances.size() + " séance(s) trouvée(s)");
    }

    @FXML
    private void onRechercherDate() {
        LocalDate date = dateRecherche.getValue();
        if (date == null) {
            afficherErreur("Choisissez une date.");
            return;
        }
        seances.setAll(seanceService.rechercherParDate(date.toString()));
        lblMessage.setText(seances.size() + " séance(s) trouvée(s)");
    }

    @FXML
    private void onAjouter() {
        Seance seance = lireFormulaire(0);
        if (seance == null) {
            return;
        }
        try {
            seanceService.ajouterSeance(seance);
            chargerSeances();
            viderFormulaire();
            lblMessage.setText("Séance ajoutée.");
        } catch (RuntimeException e) {
            afficherErreur(e.getMessage());
        }
    }

    @FXML
    private void onModifier() {
        Seance selection = tableSeances.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherErreur("Choisissez d'abord une séance dans le tableau.");
            return;
        }
        Seance seance = lireFormulaire(selection.getIdSeance());
        if (seance == null) {
            return;
        }
        try {
            seanceService.modifierSeance(seance);
            chargerSeances();
            viderFormulaire();
            lblMessage.setText("Séance modifiée.");
        } catch (RuntimeException e) {
            afficherErreur(e.getMessage());
        }
    }

    @FXML
    private void onSupprimer() {
        Seance selection = tableSeances.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherErreur("Choisissez d'abord une séance dans le tableau.");
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Supprimer la séance de « " + selection.getTitre() + " » du "
                + selection.getDateSeance() + " ?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        try {
            seanceService.annulerSeance(selection.getIdSeance());
            chargerSeances();
            viderFormulaire();
            lblMessage.setText("Séance supprimée.");
        } catch (RuntimeException e) {
            afficherErreur(e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Méthodes utilitaires du formulaire
    // -------------------------------------------------------------------------

    /** Lit les champs, contrôle leur forme et construit la Séance (null si la saisie est invalide). */
    private Seance lireFormulaire(int id) {
        Film film = comboFilm.getValue();
        Salle salle = comboSalle.getValue();
        LocalDate date = dateSeance.getValue();
        String heure = txtHeure.getText().trim();
        String placesTexte = txtPlaces.getText().trim();

        if (film == null || salle == null || date == null || heure.isEmpty() || placesTexte.isEmpty()) {
            afficherErreur("Le film, la salle, la date, l'heure et le nombre de places sont obligatoires.");
            return null;
        }

        try {
            LocalTime.parse(heure); // contrôle de la forme : HH:mm
        } catch (DateTimeParseException e) {
            afficherErreur("L'heure doit être au format HH:mm, par exemple 20:00.");
            return null;
        }
        if (heure.length() != 5) {
            afficherErreur("L'heure doit être au format HH:mm, par exemple 20:00.");
            return null;
        }

        int places;
        try {
            places = Integer.parseInt(placesTexte);
        } catch (NumberFormatException e) {
            afficherErreur("Le nombre de places doit être un nombre entier.");
            return null;
        }

        return new Seance(id, film.getIdFilm(), salle.getIdSalle(), date.toString(), heure, places);
    }

    private void remplirFormulaire(Seance seance) {
        // Le film et la salle sont retrouvés dans les listes (titre du film, numéro de salle)
        comboFilm.setValue(comboFilm.getItems().stream()
                .filter(f -> f.getTitre().equals(seance.getTitre()))
                .findFirst().orElse(null));
        comboSalle.setValue(comboSalle.getItems().stream()
                .filter(s -> s.getIdSalle() == seance.getIdSalle())
                .findFirst().orElse(null));

        dateSeance.setValue(LocalDate.parse(seance.getDateSeance()));
        String heure = seance.getHeure();
        txtHeure.setText(heure.length() > 5 ? heure.substring(0, 5) : heure); // 20:00:00 devient 20:00
        txtPlaces.setText(String.valueOf(seance.getPlacesDispo()));
    }

    private void viderFormulaire() {
        comboFilm.setValue(null);
        comboSalle.setValue(null);
        dateSeance.setValue(null);
        txtHeure.clear();
        txtPlaces.clear();
        tableSeances.getSelectionModel().clearSelection();
    }

    private void afficherErreur(String message) {
        Alert alerte = new Alert(Alert.AlertType.ERROR);
        alerte.setTitle("Erreur");
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }
}