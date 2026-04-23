package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.be.Document;
import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.bll.api.DocumentBuilderService;
import dk.siema.siemaexamproject.bll.api.TiffService;
import dk.siema.siemaexamproject.gui.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class ScannerViewController implements ApplicationServicesAware {

    private SceneManager sceneManager;
    private TiffService tiffService;
    private DocumentBuilderService documentBuilderService;
    private ExecutorService executor;

    @FXML private Label welcomeText;
    @FXML private ComboBox<String> profileComboBox;
    @FXML private Label profileDescriptionLabel;
    @FXML private FlowPane imageContainer;
    @FXML private TreeView<TreeNode> documentTree;

    // clean wrapper
    public record TreeNode(String label, FileEntity file) {
        @Override
        public String toString() {
            return label;
        }
    }

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.sceneManager = services.getSceneManager();
        this.tiffService = services.getTiffService();
        this.documentBuilderService = services.getDocumentBuilderService();
        this.executor = services.getExecutorService();
    }

    @FXML
    private void initialize() {
        setupProfiles();

        documentTree.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldItem, newItem) -> {

                    if (newItem == null) return;

                    TreeNode node = newItem.getValue();

                    if (node != null && node.file() != null) {
                        showImage(new File(node.file().getFilePath()));
                    }
                }
        );
    }

    // ================= SCAN =================

    @FXML
    public void onStartNewScan() {

        welcomeText.setText("Scanning TIFF files...");

        Task<List<File>> task = new Task<>() {
            @Override
            protected List<File> call() throws Exception {
                return tiffService.getAllTiffs();
            }
        };

        task.setOnSucceeded(e -> {

            List<File> files = task.getValue();

            List<Document> documents =
                    documentBuilderService.buildDocuments(files);

            buildDocumentTree(documents);

            imageContainer.getChildren().clear();

            welcomeText.setText("Scan complete!");
        });

        task.setOnFailed(e -> {
            welcomeText.setText("Scan failed");
            task.getException().printStackTrace();
        });

        executor.submit(task);
    }

    // ================= TREE =================

    private void buildDocumentTree(List<Document> documents) {

        TreeItem<TreeNode> root =
                new TreeItem<>(new TreeNode("BOX", null));
        root.setExpanded(true);

        int docIndex = 1;

        for (Document doc : documents) {

            TreeItem<TreeNode> docNode =
                    new TreeItem<>(new TreeNode("Document " + docIndex++, null));

            int pageIndex = 1;

            for (FileEntity file : doc.getPages()) {

                String label = "Page " + pageIndex++;

                if (file.isBarcode()) {
                    label += " (BARCODE)";
                }

                TreeItem<TreeNode> pageNode =
                        new TreeItem<>(new TreeNode(label, file));

                docNode.getChildren().add(pageNode);
            }

            root.getChildren().add(docNode);
        }

        documentTree.setRoot(root);
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

    // ================= PROFILES =================

    private void setupProfiles() {

        Map<String, String> profileDescriptions = Map.of(
                "Invoice Scanning", "Standard invoice documents",
                "Contract Documents", "Legal contracts and agreements",
                "Receipt Scanning", "Quick receipt scanning"
        );

        profileComboBox.setItems(
                FXCollections.observableArrayList(profileDescriptions.keySet())
        );

        profileComboBox.getSelectionModel().selectFirst();

        updateProfileDescription(profileComboBox.getValue(), profileDescriptions);

        profileComboBox.valueProperty().addListener((obs, oldV, newV) ->
                updateProfileDescription(newV, profileDescriptions)
        );
    }

    private void updateProfileDescription(String profile, Map<String, String> map) {
        profileDescriptionLabel.setText(
                map.getOrDefault(profile, "Standard invoice documents")
        );
    }
}
