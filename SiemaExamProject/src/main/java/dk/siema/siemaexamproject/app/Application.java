package dk.siema.siemaexamproject.app;

import dk.siema.siemaexamproject.gui.util.SceneManager;
import dk.siema.siemaexamproject.gui.util.ViewFactory;
import dk.siema.siemaexamproject.gui.util.ViewPath;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {

        ApplicationServices services = new ApplicationServices();
        ViewFactory viewFactory = new ViewFactory(services);

        SceneManager sceneManager = new SceneManager(viewFactory);
        sceneManager.openWindow(ViewPath.MAIN, "Menu");
    }
}
