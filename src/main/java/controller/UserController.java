package controller;

import Modele.UserModele;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import service.UserService;

public class UserController {

    @FXML private TextField txtLogin;
    @FXML private PasswordField txtMotDePasse;
    @FXML private CheckBox chkAdmin;

    @FXML private TableView<UserModele> tableUtilisateurs;
    @FXML private TableColumn<UserModele, Integer> colId;
    @FXML private TableColumn<UserModele, String> colLogin;
    @FXML private TableColumn<UserModele, String> colAdmin;

    @FXML private Label lblMessage;

    private final UserService userService;
    private final ObservableList<UserModele> utilisateurs = FXCollections.observableArrayList();

    // Injection par constructeur : Main fabrique le contrôleur avec son Service
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLogin.setCellValueFactory(new PropertyValueFactory<>("userName"));
        colAdmin.setCellValueFactory(cellule -> new SimpleStringProperty(
                Boolean.TRUE.equals(cellule.getValue().isAdmin()) ? "Oui" : "Non"));

        tableUtilisateurs.setItems(utilisateurs);

        // Un clic sur une ligne recopie le login et le rôle dans le formulaire
        tableUtilisateurs.getSelectionModel().selectedItemProperty().addListener(
                (observable, ancien, utilisateur) -> {
                    if (utilisateur != null) {
                        txtLogin.setText(utilisateur.getUserName());
                        chkAdmin.setSelected(Boolean.TRUE.equals(utilisateur.isAdmin()));
                        txtMotDePasse.clear();
                    }
                });

        chargerUtilisateurs();
    }

    private void chargerUtilisateurs() {
        utilisateurs.setAll(userService.listerUtilisateurs());
    }

    @FXML
    private void onActualiser() {
        chargerUtilisateurs();
        lblMessage.setText(utilisateurs.size() + " utilisateur(s) affiché(s)");
    }

    @FXML
    private void onAjouter() {
        String login = txtLogin.getText().trim();
        String motDePasse = txtMotDePasse.getText();

        if (login.isEmpty() || motDePasse.isEmpty()) {
            afficherErreur("Le login et le mot de passe sont obligatoires.");
            return;
        }
        try {
            // Sécurité par défaut : un nouveau compte n'est jamais administrateur
            userService.ajouterUtilisateur(new UserModele(login, motDePasse, false));
            chargerUtilisateurs();
            viderFormulaire();
            lblMessage.setText("Compte créé : " + login);
        } catch (RuntimeException e) {
            afficherErreur(e.getMessage());
        }
    }

    @FXML
    private void onModifier() {
        UserModele selection = tableUtilisateurs.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherErreur("Choisissez d'abord un utilisateur dans le tableau.");
            return;
        }
        String login = txtLogin.getText().trim();
        if (login.isEmpty()) {
            afficherErreur("Le login est obligatoire.");
            return;
        }
        try {
            // Le mot de passe n'est pas modifié ici (null) : seuls le login et le rôle changent
            userService.modifierUtilisateur(
                    new UserModele(login, null, chkAdmin.isSelected(), selection.getId()));
            chargerUtilisateurs();
            viderFormulaire();
            lblMessage.setText("Compte modifié : " + login);
        } catch (RuntimeException e) {
            afficherErreur(e.getMessage());
        }
    }

    @FXML
    private void onSupprimer() {
        UserModele selection = tableUtilisateurs.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherErreur("Choisissez d'abord un utilisateur dans le tableau.");
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Supprimer le compte « " + selection.getUserName() + " » ?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        try {
            userService.supprimerUtilisateur(selection.getUserName());
            chargerUtilisateurs();
            viderFormulaire();
            lblMessage.setText("Compte supprimé : " + selection.getUserName());
        } catch (RuntimeException e) {
            afficherErreur(e.getMessage());
        }
    }

    private void viderFormulaire() {
        txtLogin.clear();
        txtMotDePasse.clear();
        chkAdmin.setSelected(false);
        tableUtilisateurs.getSelectionModel().clearSelection();
    }

    private void afficherErreur(String message) {
        Alert alerte = new Alert(Alert.AlertType.ERROR);
        alerte.setTitle("Erreur");
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }
}