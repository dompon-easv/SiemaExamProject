package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.gui.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import java.util.Map;
import java.util.HashMap;

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

        Map<String, String> profileDescriptions = Map.of(
                "Invoice Scanning", "Standard invoice documents",
                "Contract Documents", "Legal contracts and agreements",
                "Receipt Scanning", "Quick receipt scanning"
        );

        profileComboBox.setItems(
                FXCollections.observableArrayList(profileDescriptions.keySet())
        );

        profileComboBox.getSelectionModel().selectFirst();

        // initial state
        updateProfileDescription(profileComboBox.getValue(), profileDescriptions);

        // reactive updates
        profileComboBox.valueProperty().addListener((obs, oldValue, newValue) ->
                updateProfileDescription(newValue, profileDescriptions)
        );
    }

    private void updateProfileDescription(String profile, Map<String, String> map) {
        profileDescriptionLabel.setText(
                map.getOrDefault(profile, "Standard invoice documents")
        );
    }
}
