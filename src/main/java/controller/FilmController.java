package controller;

import Modele.Film;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import service.FilmService;
import javafx.scene.control.ButtonType;

public class FilmController {

    @FXML private TextField txtTitre;
    @FXML private ComboBox<String> comboGenre;
    @FXML private TextField txtDuree;
    @FXML private TextField txtNote;
    @FXML private TextField txtSynopsis;

    @FXML private TableView<Film> tableFilms;
    @FXML private TableColumn<Film, Integer> colId;
    @FXML private TableColumn<Film, String> colTitre;
    @FXML private TableColumn<Film, String> colGenre;
    @FXML private TableColumn<Film, Integer> colDuree;
    @FXML private TableColumn<Film, Double> colNote;

    @FXML private Label lblMessage;

    private final FilmService filmService;
    private final ObservableList<Film> films = FXCollections.observableArrayList();

    // Injection par constructeur : MainApp fabrique le contrôleur avec son Service
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idFilm"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colDuree.setCellValueFactory(new PropertyValueFactory<>("duree"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        tableFilms.setItems(films);

        comboGenre.getItems().addAll("Action", "Animation", "Comédie", "Drame",
                "Horreur", "Science-fiction", "Thriller");

        // Un clic sur une ligne recopie le film dans le formulaire
        tableFilms.getSelectionModel().selectedItemProperty().addListener(
                (observable, ancien, film) -> {
                    if (film != null) {
                        remplirFormulaire(film);
                    }
                });

        chargerFilms();
    }

    private void chargerFilms() {
        films.setAll(filmService.listerFilms());
    }

    @FXML
    private void onActualiser() {
        chargerFilms();
        lblMessage.setText(films.size() + " film(s) affiché(s)");
    }

    @FXML
    private void onAjouter() {
        Film film = lireFormulaire(0);
        if (film == null) {
            return; // l'erreur a déjà été affichée
        }
        try {
            filmService.ajouterFilm(film);
            chargerFilms();
            viderFormulaire();
            lblMessage.setText("Film ajouté : " + film.getTitre());
        } catch (RuntimeException e) {
            afficherErreur(e.getMessage()); // le message vient du Service
        }
    }

    @FXML
    private void onModifier() {
        Film selection = tableFilms.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherErreur("Choisissez d'abord un film dans le tableau.");
            return;
        }
        // Le formulaire contient les nouvelles valeurs ; l'id reste celui du film choisi
        Film film = lireFormulaire(selection.getIdFilm());
        if (film == null) {
            return;
        }
        try {
            filmService.modifierFilm(film);
            chargerFilms();
            viderFormulaire();
            lblMessage.setText("Film modifié : " + film.getTitre());
        } catch (RuntimeException e) {
            afficherErreur(e.getMessage());
        }
    }

    @FXML
    private void onSupprimer() {
        Film selection = tableFilms.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherErreur("Choisissez d'abord un film dans le tableau.");
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Supprimer le film « " + selection.getTitre()
                + " » ? Ses séances seront aussi supprimées.");

        // showAndWait() renvoie le bouton cliqué : on supprime seulement si c'est OK
        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        try {
            filmService.supprimerFilm(selection.getIdFilm());
            chargerFilms();
            viderFormulaire();
            lblMessage.setText("Film supprimé : " + selection.getTitre());
        } catch (RuntimeException e) {
            afficherErreur(e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Méthodes utilitaires du formulaire
    // -------------------------------------------------------------------------

    /** Lit les champs, contrôle leur forme et construit le Film (null si la saisie est invalide). */
    private Film lireFormulaire(int id) {
        String titre = txtTitre.getText().trim();
        String genre = comboGenre.getValue();
        String dureeTexte = txtDuree.getText().trim();
        String noteTexte = txtNote.getText().trim().replace(',', '.');
        String synopsis = txtSynopsis.getText().trim();

        if (titre.isEmpty() || genre == null || dureeTexte.isEmpty() || noteTexte.isEmpty()) {
            afficherErreur("Le titre, le genre, la durée et la note sont obligatoires.");
            return null;
        }

        int duree;
        try {
            duree = Integer.parseInt(dureeTexte);
        } catch (NumberFormatException e) {
            afficherErreur("La durée doit être un nombre entier.");
            return null;
        }

        double note;
        try {
            note = Double.parseDouble(noteTexte);
        } catch (NumberFormatException e) {
            afficherErreur("La note doit être un nombre.");
            return null;
        }

        try {
            return new Film(id, titre, genre, duree, note, synopsis);
        } catch (RuntimeException e) {
            afficherErreur(e.getMessage());
            return null;
        }
    }

    private void remplirFormulaire(Film film) {
        txtTitre.setText(film.getTitre());
        comboGenre.setValue(film.getGenre());
        txtDuree.setText(String.valueOf(film.getDuree()));
        txtNote.setText(String.valueOf(film.getNote()));
        txtSynopsis.setText(film.getSynopsis() == null ? "" : film.getSynopsis());
    }

    private void viderFormulaire() {
        txtTitre.clear();
        comboGenre.setValue(null);
        txtDuree.clear();
        txtNote.clear();
        txtSynopsis.clear();
        tableFilms.getSelectionModel().clearSelection();
    }

    private void afficherErreur(String message) {
        Alert alerte = new Alert(Alert.AlertType.ERROR);
        alerte.setTitle("Erreur");
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }
}