package controller;

import java.time.format.DateTimeFormatter;

import Modele.AuditLogModele;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import service.AuditLogService;

public class AuditLogController {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @FXML private TableView<AuditLogModele> tableLogs;
    @FXML private TableColumn<AuditLogModele, String> colDate;
    @FXML private TableColumn<AuditLogModele, String> colUtilisateur;
    @FXML private TableColumn<AuditLogModele, String> colTable;
    @FXML private TableColumn<AuditLogModele, String> colAction;
    @FXML private TableColumn<AuditLogModele, Integer> colRecord;

    @FXML private TextArea txtDetail;
    @FXML private Label lblMessage;

    private final AuditLogService auditLogService;
    private final ObservableList<AuditLogModele> logs = FXCollections.observableArrayList();

    // Injection par constructeur : Main fabrique le contrôleur avec son Service
    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @FXML
    private void initialize() {
        colDate.setCellValueFactory(cellule -> new SimpleStringProperty(
                cellule.getValue().getCreatedAt() == null ? ""
                        : cellule.getValue().getCreatedAt().toLocalDateTime().format(FORMAT_DATE)));
        colUtilisateur.setCellValueFactory(cellule -> new SimpleStringProperty(
                cellule.getValue().getLogin() == null ? "(inconnu)" : cellule.getValue().getLogin()));
        colTable.setCellValueFactory(new PropertyValueFactory<>("tableName"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("action"));
        colRecord.setCellValueFactory(new PropertyValueFactory<>("recordId"));

        tableLogs.setItems(logs);

        // Un clic sur une ligne affiche le contenu avant et après la modification
        tableLogs.getSelectionModel().selectedItemProperty().addListener(
                (observable, ancien, log) -> {
                    if (log == null) {
                        txtDetail.clear();
                    } else {
                        txtDetail.setText("AVANT :\n" + (log.getOldData() == null ? "(aucune)" : log.getOldData())
                                + "\n\nAPRÈS :\n" + (log.getNewData() == null ? "(aucune)" : log.getNewData()));
                    }
                });

        chargerLogs();
    }

    private void chargerLogs() {
        logs.setAll(auditLogService.listerLogs());
    }

    @FXML
    private void onActualiser() {
        chargerLogs();
        lblMessage.setText(logs.size() + " ligne(s) d'historique");
    }
}