package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.gui.util.KeyBindingHelper;
import dk.siema.siemaexamproject.gui.util.SceneManager;
import dk.siema.siemaexamproject.gui.util.ViewPath;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class AdminShellController implements ApplicationServicesAware {

    @FXML private Button usersTabButton;
    @FXML private Button profilesTabButton;
    @FXML private Button logsTabButton;
    @FXML private StackPane adminContentContainer;

    private SceneManager sceneManager;

    private int currentTabIndex = 0;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.sceneManager = services.getSceneManager();
    }

    @FXML
    private void initialize() {
        adminContentContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {

                KeyBindingHelper.setupShortcutsForAdminShell(
                        newScene,
                        this::cycleViews);
            }
        });
    }

    private void cycleViews() {
       currentTabIndex = (currentTabIndex + 1) % 3;

        switch (currentTabIndex) {
            case 0 -> showUsers();
            case 1 -> showProfiles();
            case 2 -> showLogs();
        }
    }


    public void showDefaultView() {
        sceneManager.setContent(adminContentContainer, ViewPath.USERMANAGEMENT);
        usersTabButton.getStyleClass().add("admin-tab-button-selected");
    }

    @FXML
    private void showUsers() {
        currentTabIndex = 0;
        sceneManager.setContent(adminContentContainer, ViewPath.USERMANAGEMENT);
        usersTabButton.getStyleClass().add("admin-tab-button-selected");
        logsTabButton.getStyleClass().remove("admin-tab-button-selected");
        profilesTabButton.getStyleClass().remove("admin-tab-button-selected");

    }

    @FXML
    private void showProfiles() {
        currentTabIndex = 1;
        sceneManager.setContent(adminContentContainer, ViewPath.SCANNINGPROFILES);
        usersTabButton.getStyleClass().remove("admin-tab-button-selected");
        logsTabButton.getStyleClass().remove("admin-tab-button-selected");
        profilesTabButton.getStyleClass().add("admin-tab-button-selected");
    }

    @FXML
    private void showLogs() {
        currentTabIndex = 2;
        sceneManager.setContent(adminContentContainer, ViewPath.ACTIVITYLOGS);
        usersTabButton.getStyleClass().remove("admin-tab-button-selected");
        logsTabButton.getStyleClass().add("admin-tab-button-selected");
        profilesTabButton.getStyleClass().remove("admin-tab-button-selected");
    }
}