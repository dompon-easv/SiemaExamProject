package dk.siema.siemaexamproject.app;

import dk.siema.siemaexamproject.gui.util.SceneManager;
import dk.siema.siemaexamproject.gui.util.ViewPath;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {

    private ApplicationServices services;

    @Override
    public void start(Stage stage) {

        services = new ApplicationServices();
        SceneManager sceneManager = services.getSceneManager();
        sceneManager.setScene(stage, ViewPath.LOGIN, "Visione Login");
        stage.show();
    }

    @Override
    public void stop() {
        if (services != null) {
            services.shutdown();
        }
    }
}