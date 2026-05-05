package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.be.Client;
import dk.siema.siemaexamproject.bll.exceptions.DataAccessException;
import dk.siema.siemaexamproject.bll.exceptions.ServiceException;
import dk.siema.siemaexamproject.bll.exceptions.ValidationException;
import dk.siema.siemaexamproject.gui.models.AdminModel;
import dk.siema.siemaexamproject.gui.models.ClientProfileModel;
import dk.siema.siemaexamproject.gui.util.AlertHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ClientManagementController implements ApplicationServicesAware {

    @FXML
    private TextField newClientField;
    @FXML
    private ListView<Client> clientListView;
    @FXML
    private Button btnAdd;

    private Client clientToEdit = null;

    private ApplicationServices services;
    private ClientProfileModel model;
    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.services = services;
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

    @FXML
    public void initialize() {
        if (model != null) {
            setupClientList();
        }

        clientListView.setOnKeyPressed(event -> {
            String letter = event.getText().toLowerCase();

            if (!letter.isEmpty()) {
                for (Client client : clientListView.getItems()) {
                    if (client.getName().toLowerCase().startsWith(letter)) {
                        // Select the client
                        clientListView.getSelectionModel().select(client);
                        // Make sure the list actually scrolls down to show it
                        clientListView.scrollTo(client);
                        break; // Stop searching once we find the first match
                    }
                }
            }
        });
    }

    @FXML
    private void deleteSelectedClient(ActionEvent event) {
        System.out.println("!!! DELETE BUTTON CLICKED !!!"); // This MUST print
        Client selected = clientListView.getSelectionModel().getSelectedItem();

        // Safety first: Check if something is actually selected
        if (selected == null) {
            AlertHelper.warning("No Selection", "Please select a client to delete.");
            return;
        }

        try {
            model.deleteClient(selected);
            // Tip: You can show a success message or just let the list update automatically

        } catch (ValidationException e) {
            // Most specific first!
            AlertHelper.warning("Invalid Input", e.getMessage());

        } catch (DataAccessException e) {
            // Database specific
            AlertHelper.error("Database Error", e.getMessage());

        } catch (ServiceException e) {
            // Other general service errors
            AlertHelper.error("Error", e.getMessage());

        } catch (Exception e) {
            // THE "SAFETY NET" - Always at the very bottom
            System.out.println("--- UNEXPECTED ERROR ---");
            e.printStackTrace();
            if (e.getCause() != null) {
                System.out.println("ROOT SQL CAUSE: " + e.getCause().getMessage());
            }
            AlertHelper.error("Critical Error", "An unexpected error occurred: " + e.getMessage());
        }
    }
    @FXML
    private void updateSelectedClient() {
        Client selected = clientListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            newClientField.setText(selected.getName());
            btnAdd.setText("Save changes");
            this.clientToEdit = selected;

        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) clientListView.getScene().getWindow();
        stage.close();
    }

    public void addNewClient(ActionEvent actionEvent) throws ServiceException {
        String name = newClientField.getText();

        if (name == null || name.isEmpty()) {
            AlertHelper.warning("Empty Input", "Please enter a valid client name.");
            return;
        }

        try{
            if(clientToEdit == null) {
                Client client = new Client(name);
                model.createClient(client);
                newClientField.clear();

            } else {
                clientToEdit.setName(name);
                model.updateClient(clientToEdit);
                resetUI();
            }
        } catch (Exception e) {
            AlertHelper.warning("Error", e.getMessage());
        }


    }

    private void resetUI() {
        clientToEdit = null;
        newClientField.clear();
        btnAdd.setText("Add Client");
    }

}