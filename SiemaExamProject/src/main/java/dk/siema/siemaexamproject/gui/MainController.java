package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.bll.api.TiffService;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import java.util.List;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class MainController {

    @FXML
    private Label welcomeText;

    @FXML
    private FlowPane imageContainer;

    private final TiffService service = new TiffService();

    @FXML
    public void onLoadAllImages() {

        Task<List<File>> task = new Task<>() {
            @Override
            protected List<File> call() throws Exception {
                return service.getAllTiffs();
            }
        };

        task.setOnSucceeded(e -> {
            imageContainer.getChildren().clear();

            for (File file : task.getValue()) {
                try {
                    BufferedImage img = ImageIO.read(file);

                    if (img != null) {
                        ImageView view = new ImageView(
                                SwingFXUtils.toFXImage(img, null)
                        );

                        view.setFitWidth(200);
                        view.setPreserveRatio(true);

                        imageContainer.getChildren().add(view);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        new Thread(task).start();
    }
}