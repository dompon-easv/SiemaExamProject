package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.be.Client;
import dk.siema.siemaexamproject.be.ProfileSetting;
import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.be.Setting;
import dk.siema.siemaexamproject.bll.exceptions.ServiceException;
import dk.siema.siemaexamproject.gui.models.ClientProfileModel;
import dk.siema.siemaexamproject.gui.util.AlertHelper;
import dk.siema.siemaexamproject.gui.util.SceneManager;
import dk.siema.siemaexamproject.gui.util.ViewPath;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class AddEditProfileController implements ApplicationServicesAware {

    @FXML
    private ListView<Client> clientListView ;
    @FXML
    private ComboBox<Setting> settingNameComboBox;
    @FXML
    private TextField numericValueTextField;
    @FXML
    private ComboBox<String> choiceValueComboBox;
    @FXML
    private TableColumn<ProfileSetting, String> settingNameColumn;
    @FXML
    private TableColumn<ProfileSetting, String> settingValueColumn;
    @FXML
    private TableView<ProfileSetting> profileSettingTableView;
    @FXML
    private TextField profileNameField;
    @FXML
    private TextField descriptionField;


    private SceneManager sceneManager;
    private ClientProfileModel model;

    private ScanningProfile profileToEdit;
    private boolean isEditMode = false;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.sceneManager = services.getSceneManager();
        this.model = services.getClientProfileModel();
    }

    private void setupClientList() {
        clientListView.setItems(model.getClients());
        try{
            model.loadAllClients();
            System.out.println("SUCCESS! Loaded " + model.getClients().size() + " clients from the DB.");
        } catch (Exception e)
        {
            System.out.println("ERROR: Something went wrong loading clients!");
            e.printStackTrace();

        }
    }

    private void setupSettingComboBox() {
        settingNameComboBox.setItems(model.getAllSettings());
        try {
            model.loadAllSettings();
        } catch (ServiceException e) {
            AlertHelper.error("Failed to load settings", e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        // 1. Set Initial UI State
        hideDynamicInputs();

        // 2. Configure UI Components
        setupTableColumns();
        setupListeners();

        // 3. Load Data
        setupClientList();
        setupSettingComboBox();
        profileSettingTableView.setItems(model.getPendingSettings());
    }

// --- HELPER METHODS ---

    private void setupTableColumns() {
        // Removed the curly braces and "return" keyword for a cleaner lambda syntax
        settingNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSetting().getName())
        );

        settingValueColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getValue())
        );
    }

    private void setupListeners() {
        // Break down listeners into their own methods so this block stays clean
        setupClientSearchListener();
        setupSettingSelectionListener();
        setupTableDoubleClickListener();
    }

    private void setupClientSearchListener() {
        clientListView.setOnKeyPressed(event -> {
            String letter = event.getText().toLowerCase();

            // Guard clause: if they didn't type a letter, stop right here.
            if (letter.isEmpty()) return;

            for (Client client : clientListView.getItems()) {
                if (client.getName().toLowerCase().startsWith(letter)) {
                    clientListView.getSelectionModel().select(client);
                    clientListView.scrollTo(client);
                    break;
                }
            }
        });
    }

    private void setupSettingSelectionListener() {
        settingNameComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            // Guard clause: if nothing is selected, do nothing.
            if (newVal == null) return;

            switch (newVal.getName()) {
                case "Color scale" -> showChoiceInput("Color", "Grayscale", "Black and white");
                case "Rotation"    -> showNumericInput("Enter degrees");
                default            -> hideDynamicInputs();
            }
        });
    }

    private void setupTableDoubleClickListener() {
        profileSettingTableView.setRowFactory(tv -> {
            TableRow<ProfileSetting> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                // Check for double click AND ensure they didn't click an empty row
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    ProfileSetting clickedSetting = row.getItem();
                    deleteSetting(clickedSetting);                }
            });
            return row;
        });
    }

    private void deleteSetting(ProfileSetting clickedSetting) {
        AlertHelper.confirm("Confirm delete", "Are you sure you want to remove the setting?");
        model.removePendingSetting(clickedSetting);
    }

    private void loadSettingForEditing(ProfileSetting clickedSetting) {
        model.getPendingSettings().remove(clickedSetting);
        settingNameComboBox.getSelectionModel().select(clickedSetting.getSetting());

        String value = clickedSetting.getValue();
        if(clickedSetting.getSetting().getName().equals("Colour Scale")) {
            choiceValueComboBox.getSelectionModel().select(value);
        } else if(clickedSetting.getSetting().getName().equals("Rotation")) {
            numericValueTextField.setText(value);
        }
    }

// --- UI STATE MANAGERS ---

    private void hideDynamicInputs() {
        numericValueTextField.setVisible(false);
        choiceValueComboBox.setVisible(false);
    }

    private void showChoiceInput(String... choices) {
        numericValueTextField.setVisible(false);
        choiceValueComboBox.setVisible(true);
        choiceValueComboBox.getItems().setAll(choices);
    }

    private void showNumericInput(String promptText) {
        choiceValueComboBox.setVisible(false);
        numericValueTextField.setVisible(true);
        numericValueTextField.setPromptText(promptText);
    }


    public void openClientManager(ActionEvent actionEvent) {
        Stage owner = (Stage) clientListView.getScene().getWindow();

        sceneManager.openDialog(ViewPath.CLIENTMANAGEMENT, "Client management", owner);
    }

    public void handleCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) clientListView.getScene().getWindow();
        stage.close();
    }

    public void handleSaveProfile(ActionEvent actionEvent) {
        Client selectedClient = clientListView.getSelectionModel().getSelectedItem();
        String name = profileNameField.getText();
        String description = descriptionField.getText();

        if (selectedClient == null || name.isEmpty() )
        { AlertHelper.warning("Missing information", "Please choose the client and fill in the name");
            return;
        }

        List<ProfileSetting> settingsToSave = new ArrayList<>(model.getPendingSettings());
        try {
            if (isEditMode) {
                profileToEdit.setName(name);
                profileToEdit.setDescription(description);
                profileToEdit.setClient(selectedClient.getId());
                profileToEdit.setSettings(settingsToSave);
                model.updateProfile(profileToEdit);
            } else {
                ScanningProfile newProfile = new ScanningProfile(selectedClient.getId(), name, description, settingsToSave);
                model.saveNewProfile(newProfile);
            }

            model.clearPendingSetting();
            handleCancel(actionEvent);
        } catch (ServiceException e)
        {
            e.printStackTrace();
            AlertHelper.error("database error", "Something went wrong while saving your profile!");

            handleCancel(actionEvent);
    }}

    public void handleAddSetting(ActionEvent actionEvent) {
        Setting selectedSetting = settingNameComboBox.getValue();
        if (selectedSetting == null) { return;}

        String finalValue = "";

        switch (selectedSetting.getName()) {
            case "Color scale" -> {
                finalValue = choiceValueComboBox.getValue();
                if (finalValue == null) { return; }
            }
            case "Rotation" -> {
                finalValue = numericValueTextField.getText();
                if (finalValue.isEmpty()) { return; }
            }
            default -> { return;}
        }
        ProfileSetting newSetting = new ProfileSetting(selectedSetting, finalValue);
        try {
            model.addPendingSetting(newSetting);
            settingNameComboBox.getSelectionModel().clearSelection();
            numericValueTextField.clear();
            choiceValueComboBox.getSelectionModel().clearSelection();
        }catch (ServiceException e){
            e.printStackTrace();
            AlertHelper.warning("Duplicate setting", e.getMessage());
        }
        settingNameComboBox.getSelectionModel().clearSelection();
        numericValueTextField.clear();
    }

    public void setProfileToEdit(ScanningProfile profileToEdit) {
        this.profileToEdit = profileToEdit;
        this.isEditMode = true;

        profileNameField.setText(profileToEdit.getName());
        descriptionField.setText(profileToEdit.getDescription());
        for (Client client : clientListView.getItems()) {
            if(client.getId() == profileToEdit.getClientId())
            {
                clientListView.getSelectionModel().select(client);
                clientListView.scrollTo(client);
                break;
            }
        }
        model.clearPendingSetting();
        if(profileToEdit.getProfileSettings() != null)
        {
            for(ProfileSetting setting : profileToEdit.getProfileSettings()) {
                try{
                    model.addPendingSetting(setting);
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
        }
        clientListView.getSelectionModel().select(profileToEdit.getClientId());
        profileSettingTableView.setItems(model.getPendingSettings());
    }
}
