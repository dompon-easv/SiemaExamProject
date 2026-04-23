package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.be.Document;
import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.bll.api.DocumentBuilderService;
import dk.siema.siemaexamproject.bll.api.TiffService;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class MainController implements ApplicationServicesAware {

    private TiffService tiffService;
    private DocumentBuilderService documentBuilderService;
    private ExecutorService executor;

    @FXML private Label welcomeText;
    @FXML private FlowPane imageContainer;
    @FXML private TreeView<String> documentTree;

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.tiffService = services.getTiffService();
        this.documentBuilderService = services.getDocumentBuilderService();
        this.executor = services.getExecutorService();
    }

    @FXML
    public void onLoadAllImages() {

        welcomeText.setText("Loading TIFF files...");

        Task<List<File>> task = new Task<>() {
            @Override
            protected List<File> call() throws Exception {
                return tiffService.getAllTiffs();
            }
        };

        task.setOnSucceeded(e -> {

            List<File> files = task.getValue();

            // 🔥 1. BUILD DOCUMENT STRUCTURE (BARCODE LOGIC)
            List<Document> documents =
                    documentBuilderService.buildDocuments(files);

            // 🔥 2. UPDATE TREE VIEW
            buildBoxTree(documents);

            // 🔥 3. OPTIONAL: show images
            imageContainer.getChildren().clear();

            for (File file : files) {
                executor.submit(() -> {
                    try {
                        BufferedImage img = ImageIO.read(file);

                        if (img != null) {
                            ImageView view = new ImageView(
                                    SwingFXUtils.toFXImage(img, null)
                            );

                            view.setFitWidth(150);
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

            welcomeText.setText("Documents built from barcodes!");

        });

        task.setOnFailed(e -> {
            welcomeText.setText("Failed to load files");
            task.getException().printStackTrace();
        });

        executor.submit(task);
    }

    private void buildBoxTree(List<Document> documents) {

        TreeItem<String> root = new TreeItem<>("BOX");
        root.setExpanded(true);

        int docIndex = 1;

        for (Document doc : documents) {

            TreeItem<String> docNode =
                    new TreeItem<>("Document " + docIndex++);

            int pageIndex = 1;

            for (FileEntity file : doc.getPages()) {

                String label = "Page " + pageIndex++;

                if (file.isBarcode()) {
                    label += " (BARCODE)";
                }

                docNode.getChildren().add(new TreeItem<>(label));
            }

            root.getChildren().add(docNode);
        }

        documentTree.setRoot(root);
    }
}