package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.gui.util.SceneManager;
import dk.siema.siemaexamproject.gui.util.ViewPath;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

public class UserManagementController implements ApplicationServicesAware {
    @FXML
    private TableView<UserRow> usersTable;
    @FXML private TableColumn<UserRow, String> usernameColumn;
    @FXML private TableColumn<UserRow, String> emailColumn;
    @FXML private TableColumn<UserRow, String> roleColumn;
    @FXML private TableColumn<UserRow, String> profileAccessColumn;
    @FXML private TableColumn<UserRow, String> statusColumn;
    @FXML private TableColumn<UserRow, String> actionsColumn;
    private SceneManager sceneManager;
    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.sceneManager = services.getSceneManager();
    }

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

    public void showAddUser(ActionEvent actionEvent) {

        Stage owner = (Stage) ((Node) actionEvent.getSource())
                .getScene()
                .getWindow();

        sceneManager.openDialog(ViewPath.ADDUSERVIEW, "Add User", owner);
    }

    public record UserRow(String username, String email, String role, String profileAccess, String status) {}
}

