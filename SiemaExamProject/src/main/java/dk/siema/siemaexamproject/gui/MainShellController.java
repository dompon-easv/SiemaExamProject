package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.gui.util.SceneManager;
import dk.siema.siemaexamproject.gui.util.ViewPath;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainShellController implements ApplicationServicesAware {

    @FXML
    private StackPane contentContainer;

    private ApplicationServices applicationServices;
    private SceneManager sceneManager;

    @Override
    public void setApplicationServices(ApplicationServices services) {
      this.sceneManager = services.getSceneManager();
    }

    @FXML
    private void initialize() {
        // initialize() may run before ApplicationServices is injected,
        // so do not load default content here.
    }

    public void showDefaultView() {
        sceneManager.setContent(contentContainer, ViewPath.SCANNERVIEW);
    }

    @FXML
    public void showScannerView() {
        sceneManager.setContent(contentContainer, ViewPath.SCANNERVIEW);
    }

    @FXML
    public void showAdminView() {
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