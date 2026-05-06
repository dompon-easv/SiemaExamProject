package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.be.User;
import dk.siema.siemaexamproject.bll.exceptions.AuthenticationException;
import dk.siema.siemaexamproject.bll.exceptions.ServiceException;
import dk.siema.siemaexamproject.bll.exceptions.ValidationException;
import dk.siema.siemaexamproject.gui.util.SceneManager;
import dk.siema.siemaexamproject.gui.util.ViewPath;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginViewController implements ApplicationServicesAware {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private ApplicationServices services;
    private SceneManager sceneManager;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.services = services;
        this.sceneManager = services.getSceneManager();
    }

    @FXML
    private void signIn(ActionEvent event) {

        try {

            User user = services
                    .getAdminModel()
                    .authenticate(
                            usernameField.getText(),
                            passwordField.getText()
                    );

            services.getMainModel()
                    .setCurrentUser(user);

            Stage currentStage =
                    (Stage) ((Node) event.getSource())
                            .getScene()
                            .getWindow();

            sceneManager.setScene(currentStage, ViewPath.MAINSHELL, "Visione");

        } catch (AuthenticationException e) {

            errorLabel.setText("Invalid username or password");

        } catch (ValidationException e) {

            errorLabel.setText(e.getMessage());

        } catch (ServiceException e) {

            errorLabel.setText("Login failed");
        }
    }
}