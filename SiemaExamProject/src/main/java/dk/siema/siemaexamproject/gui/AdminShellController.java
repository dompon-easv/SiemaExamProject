package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.gui.util.SceneManager;
import dk.siema.siemaexamproject.gui.util.ViewPath;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class AdminShellController implements ApplicationServicesAware {

    @FXML
    private StackPane adminContentContainer;

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
    }

    @FXML
    private void showUsers() {
        sceneManager.setContent(adminContentContainer, ViewPath.USERMANAGEMENT);
    }

    @FXML
    private void showProfiles() {
        sceneManager.setContent(adminContentContainer, ViewPath.SCANNINGPROFILES);
    }

    @FXML
    private void showLogs() {
        sceneManager.setContent(adminContentContainer, ViewPath.ACTIVITYLOGS);
    }
}