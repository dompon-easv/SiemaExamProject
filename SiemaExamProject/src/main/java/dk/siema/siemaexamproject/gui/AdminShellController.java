package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.gui.util.SceneManager;
import dk.siema.siemaexamproject.gui.util.ViewPath;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class AdminShellController implements ApplicationServicesAware {

    @FXML private Button usersTabButton;
    @FXML private Button profilesTabButton;
    @FXML private Button logsTabButton;
    @FXML private StackPane adminContentContainer;

    private SceneManager sceneManager;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.sceneManager = services.getSceneManager();
    }

    @FXML
    private void initialize() {
        // Same reason as MainShellController:
        // wait until services are injected before loading default content.
    }

    public void showDefaultView() {
        sceneManager.setContent(adminContentContainer, ViewPath.USERMANAGEMENT);
        usersTabButton.getStyleClass().add("admin-tab-button-selected");
    }

    @FXML
    private void showUsers() {
        sceneManager.setContent(adminContentContainer, ViewPath.USERMANAGEMENT);
        usersTabButton.getStyleClass().add("admin-tab-button-selected");
        logsTabButton.getStyleClass().remove("admin-tab-button-selected");
        profilesTabButton.getStyleClass().remove("admin-tab-button-selected");

    }

    @FXML
    private void showProfiles() {
        sceneManager.setContent(adminContentContainer, ViewPath.SCANNINGPROFILES);
        usersTabButton.getStyleClass().remove("admin-tab-button-selected");
        logsTabButton.getStyleClass().remove("admin-tab-button-selected");
        profilesTabButton.getStyleClass().add("admin-tab-button-selected");
    }

    @FXML
    private void showLogs() {
        sceneManager.setContent(adminContentContainer, ViewPath.ACTIVITYLOGS);
        usersTabButton.getStyleClass().remove("admin-tab-button-selected");
        logsTabButton.getStyleClass().add("admin-tab-button-selected");
        profilesTabButton.getStyleClass().remove("admin-tab-button-selected");
    }
}