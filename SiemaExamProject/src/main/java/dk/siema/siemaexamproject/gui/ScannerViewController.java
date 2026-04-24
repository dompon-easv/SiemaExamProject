package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.be.Document;
import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.bll.api.ScannerService;
import dk.siema.siemaexamproject.gui.models.ScannerModel;
import dk.siema.siemaexamproject.gui.util.DocumentTreeBuilder;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ScannerViewController implements ApplicationServicesAware {

    private ScannerService scannerService;
    private ScannerModel scannerModel;
    private ExecutorService executor;

    @FXML private Label welcomeText;
    @FXML private TreeView<TreeNode> documentTree;
    @FXML private FlowPane imageContainer;



    public record TreeNode(String label, FileEntity file) {
        @Override
        public String toString() {
            return label;
        }
    }

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.scannerService = services.getScannerService();
        this.scannerModel = services.getScannerModel();
        this.executor = services.getExecutorService();
    }

    @FXML
    private void initialize() {
        refreshTree();

        documentTree.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldItem, newItem) -> {
                    if (newItem == null) return;

                    TreeNode node = newItem.getValue();
                    if (node != null && node.file() != null) {
                        showImage(node.file().toFile());
                    }
                }
        );
    }

    // ================= SCAN =================

    @FXML
    public void onStartNewScan() {

        welcomeText.setText("Scanning...");

        Task<List<Document>> task = new Task<>() {
            @Override
            protected List<Document> call() throws Exception {
                return scannerService.scan();
            }
        };

        task.setOnSucceeded(e -> {
            scannerModel.setDocuments(task.getValue());
            refreshTree();
            welcomeText.setText("Scan complete");
        });

        task.setOnFailed(e -> {
            welcomeText.setText("Scan failed");
            task.getException().printStackTrace();
        });

        executor.submit(task);
    }

    // ================= UI UPDATE =================

    private void refreshTree() {
        documentTree.setRoot(
                DocumentTreeBuilder.build(scannerModel.getDocuments())
        );
    }

    // ================= IMAGE =================

    private void showImage(File file) {
        try {
            BufferedImage img = javax.imageio.ImageIO.read(file);
            if (img == null) return;

            ImageView view = new ImageView(SwingFXUtils.toFXImage(img, null));
            view.setFitWidth(400);
            view.setPreserveRatio(true);

            imageContainer.getChildren().setAll(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}