package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.be.Document;
import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.bll.api.ScannerService;
import dk.siema.siemaexamproject.gui.models.ScannerModel;
import dk.siema.siemaexamproject.gui.util.DocumentTreeBuilder;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class ScannerViewController implements ApplicationServicesAware {

    private ScannerService scannerService;
    private ScannerModel scannerModel;

    @FXML private Label welcomeText;
    @FXML private TreeView<TreeNode> documentTree;
    @FXML private FlowPane imageContainer;

    private ImageView previewImageView;

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
    }

    @FXML
    private void initialize() {

        previewImageView = new ImageView();
        previewImageView.setFitHeight(500);
        previewImageView.setPreserveRatio(true);
        imageContainer.getChildren().setAll(previewImageView);

        refreshTree();

        // bind preview image
        scannerModel.currentPreviewImageProperty().addListener(
                (obs, oldImg, newImg) -> previewImageView.setImage(newImg)
        );
        // Ask the model what is the rotation of currently selected file
        FileEntity currentFile = scannerModel.selectedFileProperty().get();

        if (currentFile != null) {
            //spin the frame to match the memory
            previewImageView.setRotate(currentFile.getRotation());
        } else {
            previewImageView.setRotate(0);
        } // reset if nothing is selected


        // selection handling
        documentTree.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldItem, newItem) -> {

                    if (newItem == null || newItem.getValue() == null) return;

                    FileEntity file = newItem.getValue().file();

                    if (file != null) {
                        System.out.println(" You clicked on " + file.getFilePath() + "In memory has rotation: " + file.getRotation());
                        scannerModel.setSelectedFile(file);

                        previewImageView.setRotate(file.getRotation());

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
            scannerModel.clear();
            scannerModel.setDocuments(task.getValue());
            refreshTree();
            welcomeText.setText("Scan complete");
        });

        task.setOnFailed(e -> {
            welcomeText.setText("Scan failed");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    // ================= IMAGE ROTATION =================

    @FXML private void onRotateAction(ActionEvent actionEvent) {
        TreeItem<TreeNode> selectedItem = documentTree.getSelectionModel().getSelectedItem();

        if (selectedItem != null && selectedItem.getValue() != null) {
            FileEntity file = selectedItem.getValue().file();

            scannerModel.rotateFile(file);

            previewImageView.setRotate(file.getRotation());
        }
    }

    // ================= UI UPDATE =================

    private void refreshTree() {
        documentTree.setRoot(
                DocumentTreeBuilder.build(scannerModel.getDocuments())
        );
    }

}