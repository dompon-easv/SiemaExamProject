package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.gui.util.SceneManager;
import dk.siema.siemaexamproject.gui.util.ViewPath;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class LoginViewController implements ApplicationServicesAware {

    private ApplicationServices applicationServices;
    private SceneManager sceneManager;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.applicationServices = services;
        this.sceneManager = services.getSceneManager();
    }

    @FXML
    private void signIn(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        sceneManager.setScene(currentStage, ViewPath.MAINSHELL, "Visione");
    }
}