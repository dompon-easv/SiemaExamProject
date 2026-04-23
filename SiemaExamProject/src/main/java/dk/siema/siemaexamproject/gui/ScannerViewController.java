package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.gui.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class ScannerViewController implements ApplicationServicesAware {

    private SceneManager sceneManager;
    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.sceneManager = services.getSceneManager();
    }

    @FXML
    private ComboBox<String> profileComboBox;
    @FXML private Label profileDescriptionLabel;

    @FXML
    private void initialize() {
        profileComboBox.setItems(FXCollections.observableArrayList(
                "Invoice Scanning",
                "Contract Documents",
                "Receipt Scanning"
        ));
        profileComboBox.getSelectionModel().selectFirst();
        profileDescriptionLabel.setText("Standard invoice documents");

        profileComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ("Contract Documents".equals(newValue)) {
                profileDescriptionLabel.setText("Legal contracts and agreements");
            } else if ("Receipt Scanning".equals(newValue)) {
                profileDescriptionLabel.setText("Quick receipt scanning");
            } else {
                profileDescriptionLabel.setText("Standard invoice documents");
            }
        });
    }
}
