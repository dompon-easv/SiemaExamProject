package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.gui.models.ClientProfileModel;
import dk.siema.siemaexamproject.gui.util.SceneManager;
import dk.siema.siemaexamproject.gui.util.ViewPath;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class AddProfileController implements ApplicationServicesAware {

    @FXML
    ListView clientListView;

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

    @FXML
    public void initialize() {
        if (model != null) {
            setupClientList();
        }
    }


    public void openClientManager(ActionEvent actionEvent) {
        Stage owner = (Stage) clientListView.getScene().getWindow();

        sceneManager.openDialog(ViewPath.CLIENTMANAGEMENT, "Client management", owner);
    }
}
