package dk.siema.siemaexamproject.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ScanningProfilesViewController {
    @FXML
    private TableView<ProfileRow> profilesTable;
    @FXML private TableColumn<ProfileRow, String> profileNameColumn;
    @FXML private TableColumn<ProfileRow, String> descriptionColumn;
    @FXML private TableColumn<ProfileRow, String> methodColumn;
    @FXML private TableColumn<ProfileRow, String> settingsColumn;
    @FXML private TableColumn<ProfileRow, String> actionsColumn;

    @FXML
    public void initialize() {
        profileNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().profileName()));
        descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().description()));
        methodColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().method()));
        settingsColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().settings()));
        actionsColumn.setCellValueFactory(data -> new SimpleStringProperty("✎  🗑"));

        profilesTable.setItems(FXCollections.observableArrayList(
                new ProfileRow("Invoice Scanning", "Standard invoice documents", "Barcode", "300 DPI, Grayscale, TIFF"),
                new ProfileRow("Contract Documents", "Legal contracts and agreements", "Manual", "600 DPI, Color, PDF"),
                new ProfileRow("Receipt Scanning", "Quick receipt scanning", "Barcode", "150 DPI, Black & White, TIFF")
        ));
    }

    public record ProfileRow(String profileName, String description, String method, String settings) {}
}

