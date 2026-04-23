package dk.siema.siemaexamproject.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class UserManagementController {
    @FXML
    private TableView<UserRow> usersTable;
    @FXML private TableColumn<UserRow, String> usernameColumn;
    @FXML private TableColumn<UserRow, String> emailColumn;
    @FXML private TableColumn<UserRow, String> roleColumn;
    @FXML private TableColumn<UserRow, String> profileAccessColumn;
    @FXML private TableColumn<UserRow, String> statusColumn;
    @FXML private TableColumn<UserRow, String> actionsColumn;

    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().username()));
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().email()));
        roleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().role()));
        profileAccessColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().profileAccess()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().status()));
        actionsColumn.setCellValueFactory(data -> new SimpleStringProperty("✎  🗑"));

        usersTable.setItems(FXCollections.observableArrayList(
                new UserRow("admin", "admin@visione.com", "admin", "Invoice Scanning, Contract Documents, Receipt Scanning", "● Active"),
                new UserRow("scanner_user", "user@visione.com", "user", "Invoice Scanning, Receipt Scanning", "● Active"),
                new UserRow("qa_user", "qa@visione.com", "user", "Contract Documents", "● Active")
        ));
    }

    public record UserRow(String username, String email, String role, String profileAccess, String status) {}
}

