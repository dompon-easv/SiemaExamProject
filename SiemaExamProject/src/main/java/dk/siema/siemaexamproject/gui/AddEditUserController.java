package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.be.User;
import dk.siema.siemaexamproject.be.enums.UserRole;
import dk.siema.siemaexamproject.bll.exceptions.ServiceException;
import dk.siema.siemaexamproject.gui.models.AdminModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AddEditUserController implements ApplicationServicesAware {

    @FXML private Label titleLabel;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<UserRole> roleCombo;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private AdminModel model;
    private User currentUser;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.model = services.getAdminModel();
    }

    @FXML
    public void initialize() {
        roleCombo.getItems().setAll(UserRole.values());
    }

    /*EDIT MODE*/
    public void setUser(User user) {
        this.currentUser = user;

        if (user != null) {
            titleLabel.setText("Edit User");

            usernameField.setText(user.getUsername());
            emailField.setText(user.getEmail());
            roleCombo.setValue(user.getRole());

            passwordField.clear();
            passwordField.setPromptText("Leave empty to keep current password");
        }
    }


    @FXML
    private void handleSave() {
        errorLabel.setText("");
        try {
            if (currentUser == null) {
                createUser();
            } else {
                updateUser();
            }

            closeWindow();

        } catch (ServiceException e) {
            errorLabel.setText(e.getMessage());
        }
    }


    private void createUser() throws ServiceException {

        User user = new User(
                usernameField.getText().trim(),
                emailField.getText().trim(),
                passwordField.getText(),
                roleCombo.getValue()
        );

        model.createUser(user);
    }


    private void updateUser() throws ServiceException {

        currentUser.setUsername(usernameField.getText().trim());
        currentUser.setEmail(emailField.getText().trim());
        currentUser.changeRole(roleCombo.getValue());

        model.updateUser(currentUser);


        String newPassword = passwordField.getText();

        if (newPassword != null && !newPassword.isBlank()) {
            model.updatePassword(currentUser, newPassword);
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        usernameField.getScene().getWindow().hide();
    }
}