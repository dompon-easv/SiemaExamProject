package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.be.enums.UserRole;
import dk.siema.siemaexamproject.gui.models.MainModel;
import dk.siema.siemaexamproject.gui.util.AlertHelper;
import dk.siema.siemaexamproject.gui.util.KeyBindingHelper;
import dk.siema.siemaexamproject.gui.util.SceneManager;
import dk.siema.siemaexamproject.gui.util.ViewPath;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.util.Map;
import dk.siema.siemaexamproject.be.User;
import javafx.scene.control.Label;

public class MainShellController implements ApplicationServicesAware {

    @FXML
    private StackPane contentContainer;
    @FXML private ToggleButton scannerButton;
    @FXML private ToggleButton adminButton;
    @FXML private Label avatarLabel;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;

    private ToggleGroup viewToggleGroup;

    private ApplicationServices applicationServices;
    private SceneManager sceneManager;
    private MainModel mainModel;

    @Override
    public void setApplicationServices(ApplicationServices services) {

        this.applicationServices = services;
        this.sceneManager = services.getSceneManager();
        this.mainModel = services.getMainModel();


    }

    @FXML
    private void initialize() {
        configureRoleUI();
        configureUserInfo();
        viewToggleGroup = new ToggleGroup();
        scannerButton.setToggleGroup(viewToggleGroup);
        adminButton.setToggleGroup(viewToggleGroup);

        scannerButton.setSelected(true);

        KeyBindingHelper.setGlobalLogoutAction(this::logout, this::showScannerView, this::showAdminView, this::showHelpAction);
        contentContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                // Calling clear here forces the helper to initialize the global shortcuts
                KeyBindingHelper.refreshAccelerators(newScene);
            }
        });
        }
       /*(showDefaultView) we used before when bypassing i´ll leave it here just in case */

    public void showDefaultView() {
        sceneManager.setContent(contentContainer, ViewPath.SCANNERVIEW);
    }


    private void configureRoleUI() {

        if (mainModel == null || mainModel.getCurrentUser() == null) {
            return;
        }

        User currentUser = mainModel.getCurrentUser();

        /* --- ADMIN ---*/
        if (mainModel.isAdmin()) {

            scannerButton.setDisable(true);
            scannerButton.setVisible(false);
            scannerButton.setManaged(false);

            adminButton.setDisable(false);
            adminButton.setVisible(true);
            adminButton.setManaged(true);

            showAdminView();

        }

        /* --- EMPLOYEE ---*/
        else {

            adminButton.setDisable(true);
            adminButton.setVisible(false);
            adminButton.setManaged(false);

            scannerButton.setDisable(false);
            scannerButton.setVisible(true);
            scannerButton.setManaged(true);

            showScannerView();
        }
    }
    @FXML
    private void showScannerView() {

        scannerButton.setSelected(true);
        adminButton.setSelected(false);

        scannerButton.getStyleClass().remove("header-chip");
        if (!scannerButton.getStyleClass().contains("header-chip-selected")) {
            scannerButton.getStyleClass().add("header-chip-selected");
        }

        adminButton.getStyleClass().remove("header-chip-selected");
        if (!adminButton.getStyleClass().contains("header-chip")) {
            adminButton.getStyleClass().add("header-chip");
        }

        sceneManager.setContent(contentContainer, ViewPath.SCANNERVIEW);
    }

    @FXML
    public void showAdminView() {

        adminButton.setSelected(true);
        scannerButton.setSelected(false);

        adminButton.getStyleClass().remove("header-chip");
        if (!adminButton.getStyleClass().contains("header-chip-selected")) {
            adminButton.getStyleClass().add("header-chip-selected");
        }

        scannerButton.getStyleClass().remove("header-chip-selected");
        if (!scannerButton.getStyleClass().contains("header-chip")) {
            scannerButton.getStyleClass().add("header-chip");
        }

        AdminShellController controller =
                sceneManager.loadInto(contentContainer, ViewPath.ADMINSHELL);

        controller.showDefaultView();
    }

    @FXML
    public void logout() {
        mainModel.logout();
        Stage currentStage = (Stage) contentContainer.getScene().getWindow();
        sceneManager.setScene(currentStage, ViewPath.LOGIN, "Login");
    }

    public void showHelpAction() {

        UserRole currentRole =
                mainModel.getCurrentUser().getRole();

        Map<UserRole, String> shortcuts = KeyBindingHelper.getShortcutInfo();
        StringBuilder helpText = new StringBuilder();

        // 3. Build the text based on role
        if (currentRole == UserRole.ADMIN) {
            helpText.append("--- SCANNER SHORTCUTS ---\n");
            helpText.append(shortcuts.get(UserRole.EMPLOYEE));
            helpText.append("\n\n--- ADMIN SHORTCUTS ---\n");
            helpText.append(shortcuts.get(UserRole.ADMIN));
        } else {
            helpText.append("--- SHORTCUTS ---\n");
            helpText.append(shortcuts.get(UserRole.EMPLOYEE));
        }

        AlertHelper.information("Shortcut list",  helpText.toString());
    }
    private void configureUserInfo() {

        User currentUser = mainModel.getCurrentUser();

        if (currentUser == null) {
            return;
        }

        userNameLabel.setText(currentUser.getUsername());
        userRoleLabel.setText(currentUser.getRole().name());
        String username = currentUser.getUsername();

        if (!username.isBlank()) {

            String initials;
            String[] parts = username.trim().split("\\s+");

            if (parts.length >= 2) {
                initials = ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
            } else {
                initials = username.substring(0, Math.min(2, username.length())).toUpperCase();
            }

            avatarLabel.setText(initials);
        }
    }
}