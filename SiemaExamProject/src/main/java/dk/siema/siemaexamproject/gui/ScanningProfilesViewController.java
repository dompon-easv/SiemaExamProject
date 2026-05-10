package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.be.Client;
import dk.siema.siemaexamproject.be.ProfileSetting;
import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.bll.exceptions.ServiceException;
import dk.siema.siemaexamproject.gui.models.ClientProfileModel;
import dk.siema.siemaexamproject.gui.util.KeyBindingHelper;
import dk.siema.siemaexamproject.gui.util.LoadedView;
import dk.siema.siemaexamproject.gui.util.SceneManager;
import dk.siema.siemaexamproject.gui.util.ViewPath;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;

public class ScanningProfilesViewController implements ApplicationServicesAware {
    @FXML private TableView<ScanningProfile> profilesTable;
    @FXML private TableColumn<ScanningProfile, String> profileNameColumn;
    @FXML private TableColumn<ScanningProfile, String> descriptionColumn;
    @FXML private TableColumn<ScanningProfile, String> settingsColumn;
    @FXML private TableColumn<ScanningProfile, String> actionsColumn;
    @FXML private ComboBox<Client> companyFilterComboBox;

    private SceneManager sceneManager;
    private ClientProfileModel model;
    private ApplicationServices services;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.sceneManager = services.getSceneManager();
        this.model = services.getClientProfileModel();
    }


    @FXML
    public void initialize() {
        // Column setup
        profileNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));

        settingsColumn.setCellValueFactory(data -> {
            ScanningProfile profile = data.getValue();
            StringBuilder settingText = new StringBuilder();
            for(ProfileSetting profileSettings : profile.getProfileSettings()) {
                settingText.append(profileSettings.getSetting().getName())
                        .append(": ").append(profileSettings.getValue()).append(" | ");
            }
            return new SimpleStringProperty(settingText.toString());
        });

       setupActionsColumn();

        // observable list
        profilesTable.setItems(model.getAllProfiles());

        // loading clients
        companyFilterComboBox.setItems(model.getClients());
        try {
            model.loadAllClients();
            Client showAll = new Client(-1, "Show All");
            if (!model.getClients().isEmpty() && !model.getClients().get(0).getName().equals("Show All")) {
                model.getClients().add(0, showAll);
            }
            companyFilterComboBox.getSelectionModel().select(showAll);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // loading data
        loadAllProfiles();
        setupShortcutsListener();
        setupComboboxListener();
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(col -> new TableCell<ScanningProfile, String>() {
            private final Button editBtn = new Button("✎");
            private final Button deleteBtn = new Button("🗑");
            private final HBox box = new HBox(10, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().add("table-action-button");
                deleteBtn.getStyleClass().addAll("table-action-button", "table-action-delete");

                editBtn.setOnAction(e -> {
                    ScanningProfile profile = getTableView().getItems().get(getIndex());
                    editProfile(profile);
                });

                deleteBtn.setOnAction(e -> {
                    ScanningProfile profile = getTableView().getItems().get(getIndex());
                    deleteProfile(profile);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });
    }

    private void deleteProfile(ScanningProfile profile) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Profile");
        alert.setHeaderText("Confirm Deletion");
        alert.setContentText("Are you sure you want to delete: " + profile.getName() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                model.deleteProfile(profile);
            } catch (ServiceException e) {
                e.printStackTrace();
                // Show error alert if DB delete fails
            }
        }
    }

    private void editProfile(ScanningProfile profile) {
        Stage stage = (Stage) profilesTable.getScene().getWindow();

        LoadedView<AddEditProfileController> loaded =
                sceneManager
                        .openDialog(ViewPath.ADDPROFILEVIEW, "Edit Profile", stage);

        if (loaded != null && loaded.controller() != null) {
            loaded.controller().setProfileToEdit(profile);
        }

    }

    private void loadAllProfiles() {
        try {
            model.loadAllProfilesFromService();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    private void setupComboboxListener() {
        companyFilterComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || "Show All".equals(newVal.getName())) {
                loadAllProfiles();}
            else{
                model.filterByClient(newVal.getId());
            }
        });
    }


    private void setupShortcutsListener() {
        profilesTable.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                KeyBindingHelper.setupShortcutsForScanningProfiles(newScene, this::showAddProfile);
            }
        });
    }

    @FXML
    private void showAddProfile() {
        Stage owner = (Stage) profilesTable.getScene().getWindow();

        sceneManager.openDialog(ViewPath.ADDPROFILEVIEW, "Add Profile", owner);
    }
}

