package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.bll.api.TiffService;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class MainController implements ApplicationServicesAware {

    private TiffService service;
    private ExecutorService executor;

    @FXML
    private Label welcomeText;

    @FXML
    private FlowPane imageContainer;

    // ✅ Injected by ViewFactory
    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.service = services.getTiffService();
        this.executor = services.getExecutorService();
    }

    @FXML
    public void onLoadAllImages() {

        welcomeText.setText("Loading TIFF files...");

        Task<List<File>> task = new Task<>() {
            @Override
            protected List<File> call() throws Exception {
                return service.getAllTiffs();
            }
        };

        task.setOnSucceeded(e -> {

            imageContainer.getChildren().clear();
            List<File> files = task.getValue();

            for (File file : files) {
                executor.submit(() -> {
                    try {
                        BufferedImage img = ImageIO.read(file);

                        if (img != null) {
                            ImageView view = new ImageView(
                                    SwingFXUtils.toFXImage(img, null)
                            );

                            view.setFitWidth(200);
                            view.setPreserveRatio(true);

                            javafx.application.Platform.runLater(() ->
                                    imageContainer.getChildren().add(view)
                            );
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }

            javafx.application.Platform.runLater(() ->
                    welcomeText.setText("Images loading...")
            );
        });

        task.setOnFailed(e -> {
            welcomeText.setText("Failed to load images");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }
}