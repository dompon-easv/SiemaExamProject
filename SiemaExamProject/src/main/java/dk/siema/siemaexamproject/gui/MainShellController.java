package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.gui.util.KeyBindingHelper;
import dk.siema.siemaexamproject.gui.util.SceneManager;
import dk.siema.siemaexamproject.gui.util.ViewPath;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainShellController implements ApplicationServicesAware {

    @FXML
    private StackPane contentContainer;
    @FXML private ToggleButton scannerButton;
    @FXML private ToggleButton adminButton;

    private ToggleGroup viewToggleGroup;

    private ApplicationServices applicationServices;
    private SceneManager sceneManager;

    @Override
    public void setApplicationServices(ApplicationServices services) {
      this.sceneManager = services.getSceneManager();
    }

    @FXML
    private void initialize() {
        viewToggleGroup = new ToggleGroup();
        scannerButton.setToggleGroup(viewToggleGroup);
        adminButton.setToggleGroup(viewToggleGroup);

        scannerButton.setSelected(true);

        KeyBindingHelper.setGlobalLogoutAction(this::logout);
        contentContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                // Calling clear here forces the helper to initialize the global shortcuts
                KeyBindingHelper.refreshAccelerators(newScene);
            }
        });
        }

    public void showDefaultView() {
        sceneManager.setContent(contentContainer, ViewPath.SCANNERVIEW);
    }

    @FXML
    private void showScannerView() {
        scannerButton.getStyleClass().add("header-chip-selected");
        adminButton.getStyleClass().remove("header-chip-selected");
        sceneManager.setContent(contentContainer, ViewPath.SCANNERVIEW);
    }

    @FXML
    public void showAdminView() {
        adminButton.getStyleClass().add("header-chip-selected");
        scannerButton.getStyleClass().remove("header-chip-selected");

        AdminShellController controller =
                sceneManager.loadInto(contentContainer, ViewPath.ADMINSHELL);
        controller.showDefaultView();
    }

    @FXML
    public void logout() {
        Stage currentStage = (Stage) contentContainer.getScene().getWindow();
        sceneManager.setScene(currentStage, ViewPath.LOGIN, "Login");
    }
}