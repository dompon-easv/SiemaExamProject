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

public class AddProfileController implements ApplicationServicesAware {

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
        } catch (Exception e) {
            e.printStackTrace();
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
        return;}

        List<ProfileSetting> settingsToSave = new ArrayList<>(model.getPendingSettings());
        ScanningProfile newProfile = new ScanningProfile(selectedClient.getId(), name, description, settingsToSave);
        try
        {
            model.saveNewProfile(newProfile);
            model.clearPendingSetting();
        } catch (ServiceException e)
        {
            e.printStackTrace();
        }
        handleCancel(actionEvent);
    }

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
        }catch (ServiceException e){
            e.printStackTrace();
        }
        settingNameComboBox.getSelectionModel().clearSelection();
        numericValueTextField.clear();
    }
}
