package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.be.Client;
import dk.siema.siemaexamproject.be.ProfileSetting;
import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.bll.exceptions.ServiceException;
import dk.siema.siemaexamproject.gui.models.ClientProfileModel;
import dk.siema.siemaexamproject.gui.util.KeyBindingHelper;
import dk.siema.siemaexamproject.gui.util.SceneManager;
import dk.siema.siemaexamproject.gui.util.ViewPath;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;

public class ScanningProfilesViewController implements ApplicationServicesAware {
    @FXML
    private TableView<ScanningProfile> profilesTable;
    @FXML private TableColumn<ScanningProfile, String> profileNameColumn;
    @FXML private TableColumn<ScanningProfile, String> descriptionColumn;
    @FXML private TableColumn<ScanningProfile, String> settingsColumn;
    @FXML private TableColumn<ScanningProfile, String> actionsColumn;
    @FXML private ComboBox<Client> companyFilterComboBox;

    private SceneManager sceneManager;
    private ClientProfileModel model;
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
        actionsColumn.setCellValueFactory(data -> new SimpleStringProperty("✎  🗑"));

        settingsColumn.setCellValueFactory(data -> {
            ScanningProfile profile = data.getValue();
            StringBuilder settingText = new StringBuilder();
            for(ProfileSetting profileSettings : profile.getProfileSettings()) {
                settingText.append(profileSettings.getSetting().getName())
                        .append(": ").append(profileSettings.getValue()).append(" | ");
            }
            return new SimpleStringProperty(settingText.toString());
        });

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

    private void loadAllProfiles() {
        try {
            model.loadAllProfilesFromService();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
    private void setupComboboxListener() {
        companyFilterComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.getName().equals("Show All")) {
                loadAllProfiles();}
            else{
                loadProfilesForClient(newVal); // Run the method we wrote earlier!
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

    private void loadProfilesForClient(Client client) {
        try{
            List<ScanningProfile> profiles = model.getProfilesForClient(client.getId());
            profilesTable.getItems().setAll(profiles);
        } catch (Exception e)
        { e.printStackTrace(); }
    }

}

