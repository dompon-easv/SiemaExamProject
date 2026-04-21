package dk.siema.siemaexamproject.gui.util;

import dk.siema.siemaexamproject.app.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    public static void openWindow(ViewPath viewType, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(Application.class.getResource(viewType.getPath()));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
