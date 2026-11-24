package controller;

import java.util.List;

import Modele.Salle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import service.SalleService;

public class SalleController {

    @FXML private TextField txtNom;
    @FXML private TextField txtCapacite;
    @FXML private CheckBox chkLunette4D;
    @FXML private CheckBox chkDolby;
    @FXML private CheckBox chkLunette3D;

    @FXML private TableView<Salle> tableSalles;
    @FXML private TableColumn<Salle, Integer> colId;
    @FXML private TableColumn<Salle, String> colNom;
    @FXML private TableColumn<Salle, Integer> colCapacite;
    @FXML private TableColumn<Salle, String> colEquipement;

    @FXML private Label lblMessage;

    private final SalleService salleService;
    private final ObservableList<Salle> salles = FXCollections.observableArrayList();
    private List<CheckBox> casesEquipement;

    // Injection par constructeur : Main fabrique le contrôleur avec son Service
    public SalleController(SalleService salleService) {
        this.salleService = salleService;
    }

    @FXML
    private void initialize() {
        casesEquipement = List.of(chkLunette4D, chkDolby, chkLunette3D);

        colId.setCellValueFactory(new PropertyValueFactory<>("idSalle"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        colEquipement.setCellValueFactory(new PropertyValueFactory<>("equipement"));

        tableSalles.setItems(salles);

        // Un clic sur une ligne recopie la salle dans le formulaire
        tableSalles.getSelectionModel().selectedItemProperty().addListener(
                (observable, ancienne, salle) -> {
                    if (salle != null) {
                        remplirFormulaire(salle);
                    }
                });

        chargerSalles();
    }

    private void chargerSalles() {
        salles.setAll(salleService.listerSalles());
    }

    @FXML
    private void onActualiser() {
        chargerSalles();
        lblMessage.setText(salles.size() + " salle(s) affichée(s)");
    }

    @FXML
    private void onAjouter() {
        Salle salle = lireFormulaire(0);
        if (salle == null) {
            return;
        }
        try {
            salleService.ajouterSalle(salle);
            chargerSalles();
            viderFormulaire();
            lblMessage.setText("Salle ajoutée : " + salle.getNom());
        } catch (RuntimeException e) {
            afficherErreur(e.getMessage());
        }
    }

    @FXML
    private void onModifier() {
        Salle selection = tableSalles.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherErreur("Choisissez d'abord une salle dans le tableau.");
            return;
        }
        Salle salle = lireFormulaire(selection.getIdSalle());
        if (salle == null) {
            return;
        }
        try {
            salleService.modifierSalle(salle);
            chargerSalles();
            viderFormulaire();
            lblMessage.setText("Salle modifiée : " + salle.getNom());
        } catch (RuntimeException e) {
            afficherErreur(e.getMessage());
        }
    }

    @FXML
    private void onSupprimer() {
        Salle selection = tableSalles.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherErreur("Choisissez d'abord une salle dans le tableau.");
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Supprimer la salle « " + selection.getNom()
                + " » ? Ses séances seront aussi supprimées.");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        try {
            salleService.supprimerSalle(selection.getIdSalle());
            chargerSalles();
            viderFormulaire();
            lblMessage.setText("Salle supprimée : " + selection.getNom());
        } catch (RuntimeException e) {
            afficherErreur(e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Méthodes utilitaires du formulaire
    // -------------------------------------------------------------------------

    /** Lit les champs, contrôle leur forme et construit la Salle (null si la saisie est invalide). */
    private Salle lireFormulaire(int id) {
        String nom = txtNom.getText().trim();
        String capaciteTexte = txtCapacite.getText().trim();

        if (nom.isEmpty() || capaciteTexte.isEmpty()) {
            afficherErreur("Le nom et la capacité sont obligatoires.");
            return null;
        }

        int capacite;
        try {
            capacite = Integer.parseInt(capaciteTexte);
        } catch (NumberFormatException e) {
            afficherErreur("La capacité doit être un nombre entier.");
            return null;
        }

        return new Salle(id, nom, capacite, equipementsCoches());
    }

    /** Les équipements cochés, séparés par une virgule (comme dans l'écran Swing). */
    private String equipementsCoches() {
        return casesEquipement.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(java.util.stream.Collectors.joining(", "));
    }

    private void remplirFormulaire(Salle salle) {
        txtNom.setText(salle.getNom());
        txtCapacite.setText(String.valueOf(salle.getCapacite()));

        String equipement = salle.getEquipement() == null ? "" : salle.getEquipement();
        List<String> presents = List.of(equipement.split("\\s*,\\s*"));
        for (CheckBox caseCochable : casesEquipement) {
            caseCochable.setSelected(presents.contains(caseCochable.getText()));
        }
    }

    private void viderFormulaire() {
        txtNom.clear();
        txtCapacite.clear();
        casesEquipement.forEach(c -> c.setSelected(false));
        tableSalles.getSelectionModel().clearSelection();
    }

    private void afficherErreur(String message) {
        Alert alerte = new Alert(Alert.AlertType.ERROR);
        alerte.setTitle("Erreur");
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }
}