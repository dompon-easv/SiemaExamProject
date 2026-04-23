package dk.siema.siemaexamproject.app;

import dk.siema.siemaexamproject.gui.MainShellController;
import dk.siema.siemaexamproject.gui.util.LoadedView;
import dk.siema.siemaexamproject.gui.util.SceneManager;
import dk.siema.siemaexamproject.gui.util.ViewFactory;
import dk.siema.siemaexamproject.gui.util.ViewPath;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) {

        ApplicationServices services = new ApplicationServices();
        SceneManager sceneManager = services.getSceneManager();

        LoadedView<MainShellController> loaded =
                sceneManager.setScene(stage, ViewPath.MAINSHELL, "Visione");

        stage.show();

        Platform.runLater(() ->
                loaded.controller().showAdminView()
        );
    }

    @Override
    public void stop() {
        AppplicationService service = new ApplicationServices();
        services.shutdown();
    }
}
