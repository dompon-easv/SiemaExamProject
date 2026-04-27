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
import javafx.scene.shape.Rectangle;

import java.util.List;

public class ScannerViewController implements ApplicationServicesAware {

    private ScannerService scannerService;
    private ScannerModel scannerModel;

    @FXML private Label welcomeText;
    @FXML private TreeView<TreeNode> documentTree;
    @FXML private FlowPane imageContainer;

    private static final double ZOOM_FACTOR = 1.2;
    private static final double MAX_ZOOM = 5.0;
    private static final double MIN_ZOOM = 0.2;
    private double currentZoom = 1.0;

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

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(imageContainer.widthProperty());
        clip.heightProperty().bind(imageContainer.heightProperty());
        imageContainer.setClip(clip);

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
                        resetZoom();

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

    // ================= IMAGE ROTATION AND ZOOMING =================

    @FXML private void onRotateAction(ActionEvent actionEvent) {
        TreeItem<TreeNode> selectedItem = documentTree.getSelectionModel().getSelectedItem();

        if (selectedItem != null && selectedItem.getValue() != null) {
            FileEntity file = selectedItem.getValue().file();

            scannerModel.rotateFile(file);

            previewImageView.setRotate(file.getRotation());
        }
    }
    public void onPreviousPageAction(ActionEvent actionEvent) {scannerModel.goToPreviousPage();}

    public void onNextPageAction(ActionEvent actionEvent) {scannerModel.goToNextPage();}


    @FXML
    private void onZoomInAction(ActionEvent event) {
      if (currentZoom * ZOOM_FACTOR <= MAX_ZOOM) {
          currentZoom*= ZOOM_FACTOR;
          applyZoom();
      }
    }

    @FXML
    private void onZoomOutAction(ActionEvent event) {
        if (currentZoom / ZOOM_FACTOR >= MIN_ZOOM) {
            currentZoom /= ZOOM_FACTOR;
            applyZoom();
        }
    }

    private void applyZoom() {
        previewImageView.setScaleX(currentZoom);
        previewImageView.setScaleY(currentZoom);
    }

    private void resetZoom() {
        currentZoom = 1.0;
        applyZoom();
    }

    // ================= UI UPDATE =================

    private void refreshTree() {
        documentTree.setRoot(
                DocumentTreeBuilder.build(scannerModel.getDocuments())
        );
    }

}