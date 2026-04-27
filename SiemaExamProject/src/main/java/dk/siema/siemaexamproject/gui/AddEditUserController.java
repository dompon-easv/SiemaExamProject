package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.be.User;
import dk.siema.siemaexamproject.be.enums.UserRole;
import dk.siema.siemaexamproject.bll.exceptions.ServiceException;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AddEditUserController implements ApplicationServicesAware {

    @FXML private Label titleLabel;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<UserRole> roleCombo;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private ApplicationServices services;
    private User currentUser;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.services = services;
    }

    @FXML
    public void initialize() {
        roleCombo.getItems().setAll(UserRole.values());
    }

    // EDIT MODE
    public void setUser(User user) {
        this.currentUser = user;

        if (user != null) {
            titleLabel.setText("Edit User");

            usernameField.setText(user.getUsername());
            emailField.setText(user.getEmail());
            roleCombo.setValue(user.getRole());

            passwordField.setPromptText("Leave empty to keep current password");
        }
    }

    // SAVE
    @FXML
    private void handleSave() {
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

    // CREATE
    private void createUser() throws ServiceException {
        User user = new User(
                usernameField.getText(),
                emailField.getText(),
                passwordField.getText(),
                roleCombo.getValue()
        );

        services.getUserService().createUser(user);
    }

    // UPDATE
    private void updateUser() throws ServiceException {

        currentUser.setUsername(usernameField.getText());
        currentUser.setEmail(emailField.getText());
        currentUser.changeRole(roleCombo.getValue());

        services.getUserService().updateUser(currentUser);

        // only update password if provided
        if (!passwordField.getText().isBlank()) {
            services.getUserService()
                    .updatePassword(currentUser.getId(), passwordField.getText());
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