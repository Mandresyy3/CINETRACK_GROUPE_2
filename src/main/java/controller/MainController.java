package controller;

import View.FenetrePrincipale;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainController {

    @FXML private TabPane tabPane;
    @FXML private Tab tabSalles;
    @FXML private Tab tabUtilisateurs;
    @FXML private Tab tabHistorique;

    @FXML
    private void initialize() {
        // Seul l'administrateur voit les écrans d'administration
        if (!FenetrePrincipale.isadmin) {
            tabPane.getTabs().removeAll(tabSalles, tabUtilisateurs, tabHistorique);
        }
    }
}