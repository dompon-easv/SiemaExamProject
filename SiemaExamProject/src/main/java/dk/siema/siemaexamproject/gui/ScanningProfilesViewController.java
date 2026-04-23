package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.gui.util.SceneManager;
import dk.siema.siemaexamproject.gui.util.ViewPath;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class ScanningProfilesViewController implements ApplicationServicesAware {
    @FXML
    private TableView<ProfileRow> profilesTable;
    @FXML private TableColumn<ProfileRow, String> profileNameColumn;
    @FXML private TableColumn<ProfileRow, String> descriptionColumn;
    @FXML private TableColumn<ProfileRow, String> methodColumn;
    @FXML private TableColumn<ProfileRow, String> settingsColumn;
    @FXML private TableColumn<ProfileRow, String> actionsColumn;

    private SceneManager sceneManager;
    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.sceneManager = services.getSceneManager();
    }


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

    public void showAddProfile(ActionEvent actionEvent) {
        Stage owner = (Stage) ((Node) actionEvent.getSource())
                .getScene()
                .getWindow();

        sceneManager.openDialog(ViewPath.ADDPROFILEVIEW, "Add Profile", owner);
    }

    public record ProfileRow(String profileName, String description, String method, String settings) {}
}

