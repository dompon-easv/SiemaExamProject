package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.be.User;
import dk.siema.siemaexamproject.bll.exceptions.*;
import dk.siema.siemaexamproject.gui.util.AlertHelper;
import dk.siema.siemaexamproject.gui.util.LoadedView;
import dk.siema.siemaexamproject.gui.util.ViewPath;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class UserManagementController implements ApplicationServicesAware {

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> profileAccessColumn;
    @FXML private TableColumn<User, Void> actionsColumn;

    private ApplicationServices services;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.services = services;
        tryLoadUsers();
    }

    @FXML
    public void initialize() {

        usernameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUsername()));

        emailColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEmail()));

        roleColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getRole().name()));

        profileAccessColumn.setCellValueFactory(data ->
                new SimpleStringProperty("N/A"));

        setupActionsColumn();

        initialized = true;
        tryLoadUsers(); // safe call
    }
    private boolean initialized = false;


    private void loadUsers() {
        try {
            usersTable.setItems(FXCollections.observableArrayList(
                    services.getUserService().getAllUsers()
            ));

        } catch (DataAccessException e) {
            AlertHelper.error("Database Error", e.getMessage());

        } catch (ServiceException e) {
            AlertHelper.error("Error", e.getMessage());
        }
    }


    private void setupActionsColumn() {
        actionsColumn.setCellFactory(col -> new TableCell<>() {

            private final Button editBtn = new Button("✎");
            private final Button deleteBtn = new Button("🗑");
            private final HBox box = new HBox(6, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().add("table-action-button");

                deleteBtn.getStyleClass().addAll(
                        "table-action-button",
                        "table-action-delete"
                );

                box.getStyleClass().add("table-action-container");

                editBtn.setTooltip(new Tooltip("Edit user"));
                deleteBtn.setTooltip(new Tooltip("Delete user"));

                editBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    openEditUser(user);
                });

                deleteBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    deleteUser(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }


    public void showAddUser(ActionEvent actionEvent) {

        Stage owner = (Stage) ((Node) actionEvent.getSource())
                .getScene()
                .getWindow();

        services.getSceneManager()
                .openDialog(ViewPath.ADDUSERVIEW, "Add User", owner);

        loadUsers();
    }


    private void openEditUser(User user) {

        Stage stage = (Stage) usersTable.getScene().getWindow();

        LoadedView<AddEditUserController> loaded =
                services.getSceneManager()
                        .openDialog(ViewPath.ADDUSERVIEW, "Edit User", stage);

        loaded.controller().setUser(user);

        loadUsers();
    }


    private void deleteUser(User user) {
        try {
            services.getUserService().deleteUser(user.getId());
            loadUsers();

        } catch (ValidationException e) {
            AlertHelper.warning("Invalid Input", e.getMessage());

        } catch (DataAccessException e) {
            AlertHelper.error("Database Error", e.getMessage());

        } catch (ServiceException e) {
            AlertHelper.error("Error", e.getMessage());
        }
    }
    private void tryLoadUsers() {
        if (initialized && services != null) {
            loadUsers();
        }
    }
}