package dk.siema.siemaexamproject.gui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    private final ViewFactory viewFactory;

    public SceneManager(ViewFactory viewFactory) {
        this.viewFactory = viewFactory;
    }

    public void openWindow(ViewPath viewPath, String title) {
        try {
            FXMLLoader loader = viewFactory.createLoader(viewPath);

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
            stage.show();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not open window: " + viewPath, e
            );
        }
    }
}