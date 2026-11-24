package main;

import java.io.IOException;
import java.io.UncheckedIOException;

import controller.ConnexionController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static final String FEUILLE_DE_STYLE = "/css/style.css";

    @Override
    public void start(Stage stage) throws IOException {
        afficherConnexion(stage);
    }

    /** Première fenêtre : la connexion. */
    private void afficherConnexion(Stage stage) throws IOException {
        FXMLLoader loader = chargeur("/views/connexion-view.fxml");
        Parent racine = loader.load();
        Scene scene = creerScene(racine);

        // Quand la connexion réussit, on remplace la fenêtre par l'application
        ConnexionController controleur = loader.getController();
        controleur.setOnConnexionReussie(() -> afficherApplication(stage));

        stage.setTitle("CineTrack — Connexion");
        stage.setScene(scene);
        stage.show();
    }

    /** Fenêtre principale à onglets, affichée après la connexion. */
    private void afficherApplication(Stage stage) {
        try {
            Parent racine = chargeur("/views/main-view.fxml").load();
            stage.setTitle("CineTrack");
            stage.setScene(creerScene(racine));
            stage.setMaximized(true);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Chargeur FXML dont les contrôleurs sont fabriqués par Main (injection de dépendances). */
    private FXMLLoader chargeur(String chemin) {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(chemin));
        loader.setControllerFactory(Main::creerControleur);
        return loader;
    }

    /** Crée une scène avec la feuille de style de l'application. */
    private Scene creerScene(Parent racine) {
        Scene scene = new Scene(racine);
        scene.getStylesheets().add(MainApp.class.getResource(FEUILLE_DE_STYLE).toExternalForm());
        return scene;
    }

    public static void main(String[] args) {
        launch(args);
    }
}