package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.be.Client;
import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.be.User;
import dk.siema.siemaexamproject.be.enums.UserRole;
import dk.siema.siemaexamproject.bll.exceptions.ServiceException;
import dk.siema.siemaexamproject.bll.exceptions.ValidationException;
import dk.siema.siemaexamproject.gui.models.AdminModel;
import dk.siema.siemaexamproject.gui.models.ClientProfileModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.*;

public class AddEditUserController implements ApplicationServicesAware {

    @FXML private Label titleLabel;
    @FXML private TextField usernameField;
    @FXML private TextField notesField;
    @FXML private ComboBox<UserRole> roleCombo;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private ComboBox<Client> clientFilterComboBox;
    @FXML private ListView<ScanningProfile> profilesList;

    private Set<Integer> selectedProfileIds = new HashSet<>();
    private Set<Integer> profileIds = new HashSet<>();

    private AdminModel model;
    private ClientProfileModel clientProfileModel;
    private User currentUser;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.model = services.getAdminModel();
        this.clientProfileModel = services.getClientProfileModel();
    }

    @FXML
    public void initialize() {
        setupProfileChecklist();
        roleCombo.getItems().setAll(UserRole.values());
        setupClientFilter();
    }

    private void setupClientFilter() {

        clientFilterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
           if(clientProfileModel == null) { return;}
            if(newValue == null || "Show All".equals(newValue.getName())) {
                profilesList.setItems(clientProfileModel.getAllProfiles());
            } else {
                List<ScanningProfile> filteredList = clientProfileModel.getProfilesForClient(newValue.getId());
                profilesList.setItems(FXCollections.observableArrayList(filteredList));
            }
        });
    }

    private void setupProfileChecklist() {
        profilesList.setCellFactory(lv -> new ListCell<ScanningProfile>() {
                    private final CheckBox checkbox = new CheckBox();

                    {
                        checkbox.setOnAction(event -> {
                            ScanningProfile profile = getItem();
                            if (profile != null) {
                                if (checkbox.isSelected()) {
                                    selectedProfileIds.add(profile.getId());
                                } else {
                                    selectedProfileIds.remove(profile.getId());
                                }
                            }
                        });
                    }
                    @Override
            protected void updateItem(ScanningProfile item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else{
                            checkbox.setText(item.getName());
                            checkbox.setSelected(selectedProfileIds.contains(item.getId()));
                            setGraphic(checkbox);
                        }
                    }
                });
    }

    /*EDIT MODE*/
    public void setUser(User user) {
        this.currentUser = user;

        // 1. Reset UI State
        clientFilterComboBox.getSelectionModel().clearSelection();
        profileIds.clear();
        selectedProfileIds.clear();

        // 2. FORCE LOAD THE DATA IF IT IS EMPTY
        try {
            // (Make sure these method names exactly match what you have in ClientProfileModel)
            if (clientProfileModel.getClients().isEmpty()) {
                clientProfileModel.loadAllClients();
            }
            if (clientProfileModel.getAllProfiles().isEmpty()) {
                clientProfileModel.loadAllProfilesFromService();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR: Could not load clients or profiles from the database!");
        }

        // 3. Populate the UI elements now that the data is loaded!
        clientFilterComboBox.setItems(clientProfileModel.getClients());
        profilesList.setItems(clientProfileModel.getAllProfiles());

        // 4. Apply User Data (For Edit Mode)
        if (user != null) {
            titleLabel.setText("Edit User");
            usernameField.setText(user.getUsername());
            notesField.setText(user.getNotes());
            roleCombo.setValue(user.getRole());
            passwordField.clear();
            passwordField.setPromptText("Leave empty to keep current password");

            try {
                model.loadProfilesForUser(user.getId());
                List<ScanningProfile> assignedProfiles = model.getProfilesForUser();

                for (ScanningProfile p : assignedProfiles) {
                    profileIds.add(p.getId());
                    selectedProfileIds.add(p.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            titleLabel.setText("Create User");
        }

        profilesList.refresh();
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
                notesField.getText().trim(),
                passwordField.getText(),
                roleCombo.getValue()
        );

        model.createUser(user);

        for(Integer profileId : selectedProfileIds) {
            model.assignProfilesForUser(user.getId(), profileId);
        }
        model.loadUsers();
    }


    private void updateUser() throws ServiceException {

        currentUser.setUsername(usernameField.getText().trim());
        currentUser.setNotes(notesField.getText().trim());
        currentUser.changeRole(roleCombo.getValue());

        model.updateUser(currentUser);


        String newPassword = passwordField.getText();

        if (newPassword != null && !newPassword.isBlank()) {
            model.updatePassword(currentUser, newPassword);
        }

        UUID userId = currentUser.getId();
        for (Integer profileId : selectedProfileIds) {
            if (!profileIds.contains(profileId)) {
                model.assignProfilesForUser(userId, profileId);
            }
        }
        for (Integer profileId : profileIds) {
            if(!selectedProfileIds.contains(profileId))
            {
                model.deleteProfilesForUser(userId, profileId);
            }
        }
        List<String> updatedNames = new ArrayList<>();
        for (ScanningProfile p : clientProfileModel.getAllProfiles()) {
            if (selectedProfileIds.contains(p.getId())) {
                updatedNames.add(p.getName());
            }
        }
        currentUser.setProfileNames(String.join(", ", updatedNames));
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        usernameField.getScene().getWindow().hide();
    }

}