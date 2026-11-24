package controller;

import Modele.UserModele;
import View.FenetrePrincipale;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import service.UserService;

public class ConnexionController {

    @FXML private TextField txtLogin;
    @FXML private PasswordField txtMotDePasse;
    @FXML private Label lblErreur;

    private final UserService userService;
    private Runnable onConnexionReussie;

    // Injection par constructeur : Main fabrique le contrôleur avec son Service
    public ConnexionController(UserService userService) {
        this.userService = userService;
    }

    /** Action à exécuter quand la connexion réussit (fournie par MainApp : ouvrir l'application). */
    public void setOnConnexionReussie(Runnable action) {
        this.onConnexionReussie = action;
    }

    @FXML
    private void onConnexion() {
        String login = txtLogin.getText().trim();
        String motDePasse = txtMotDePasse.getText();

        if (login.isEmpty() || motDePasse.isEmpty()) {
            lblErreur.setText("Veuillez saisir votre login et votre mot de passe.");
            return;
        }

        UserModele utilisateur = new UserModele(login, motDePasse, false);

        if (userService.verifierIdentifiants(utilisateur)) {
            // État global hérité de la version Swing : lu par DataBaseConn pour l'audit.
            // On retient l'utilisateur seulement APRÈS vérification du mot de passe.
            FenetrePrincipale.user_id = userService.identifiantDe(utilisateur);
            FenetrePrincipale.isadmin = userService.estAdministrateur(utilisateur);
            txtMotDePasse.clear();
            if (onConnexionReussie != null) {
                onConnexionReussie.run();
            }
        } else {
            FenetrePrincipale.user_id = 0;
            FenetrePrincipale.isadmin = false;
            txtMotDePasse.clear();
            // Un seul message pour tous les cas d'échec (pas d'énumération de comptes)
            lblErreur.setText("Identifiant ou mot de passe invalide.");
        }
    }
}