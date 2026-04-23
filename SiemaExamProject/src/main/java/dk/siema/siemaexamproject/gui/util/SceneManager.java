package dk.siema.siemaexamproject.gui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    private final ViewFactory viewFactory;

    public SceneManager(ViewFactory viewFactory) {
        this.viewFactory = viewFactory;
    }

    // --- Create loader using ViewFactory ---
    public FXMLLoader createLoader(ViewPath viewPath) {
        return viewFactory.createLoader(viewPath.getPath());
    }

    // --- Load only the view (no container, no stage) ---
    public Parent loadView(ViewPath viewPath) {
        try {
            return createLoader(viewPath).load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load view: " + viewPath, e);
        }
    }

    // --- Load view + controller ---
    public <T> LoadedView<T> load(ViewPath viewPath) {
        try {
            FXMLLoader loader = createLoader(viewPath);
            Parent root = loader.load();
            T controller = loader.getController();
            return new LoadedView<>(root, controller);
        } catch (IOException e) {
            throw new RuntimeException("Could not load view: " + viewPath, e);
        }
    }

    // --- Replace scene on an existing stage ---
    public <T> LoadedView<T> setScene(Stage stage, ViewPath viewPath, String title) {
        LoadedView<T> loaded = load(viewPath);

        Scene scene = new Scene(loaded.root(), 1200, 800);

        stage.setScene(scene);
        stage.setTitle(title);

        return loaded; // ← IMPORTANT
    }


    // --- Open a completely new window ---
    public void openWindow(ViewPath viewPath, String title) {
        Stage stage = new Stage();
        setScene(stage, viewPath, title);
    }

    // --- Inject view into a container (USED BY SHELLS) ---
    public void setContent(Pane container, ViewPath viewPath) {
        Parent root = loadView(viewPath);
        container.getChildren().setAll(root);
    }

    // --- Inject view + return controller (VERY USEFUL) ---
    public <T> T loadInto(Pane container, ViewPath viewPath) {
        try {
            FXMLLoader loader = createLoader(viewPath);
            Parent root = loader.load();
            container.getChildren().setAll(root);
            return loader.getController();
        } catch (IOException e) {
            throw new RuntimeException("Could not load view into container: " + viewPath, e);
        }
    }
}