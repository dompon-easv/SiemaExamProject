package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.be.Document;
import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.bll.api.ScannerService;
import dk.siema.siemaexamproject.gui.models.ScannerModel;
import dk.siema.siemaexamproject.gui.util.DocumentTreeBuilder;

import dk.siema.siemaexamproject.gui.util.KeyBindingHelper;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.List;

public class ScannerViewController implements ApplicationServicesAware {

    private ScannerModel scannerModel;

    @FXML private Label fileNameLabel;
    @FXML private Label scanStatusLabel;
    @FXML private Label pageInfoLbl;


    @FXML private TreeView<TreeNode> documentTree;
    @FXML private ScrollPane imageContainer;

    private static final double ZOOM_FACTOR = 1.2;
    private static final double MAX_ZOOM = 5.0;
    private static final double MIN_ZOOM = 0.2;
    private double currentZoom = 1.0;

    private ImageView previewImageView;
    private DocumentTreeBuilder treeBuilder;

    public record TreeNode(String label, FileEntity file, int documentIndex) {
        @Override
        public String toString() {
            return label;
        }
    }

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.scannerModel = services.getScannerModel();
    }

    @FXML
    private void initialize() {

        scannerModel.scanningProperty().addListener((obs, oldVal, newVal) -> {
            scanStatusLabel.setText(newVal ? "Scanning..." : "Scan complete");
        });

        scannerModel.selectedFileProperty().addListener((obs, oldFile, newFile) -> {
            if (newFile != null) {
                selectFileInTree(newFile);
            }
        });

        scannerModel.documentsProperty().addListener((obs, oldVal, newVal) -> {
            rebuildTree();
        });

        treeBuilder = new DocumentTreeBuilder();
        documentTree.setRoot(treeBuilder.build(scannerModel.documentsProperty()));

        previewImageView = new ImageView();
        previewImageView.setFitHeight(500);
        previewImageView.setPreserveRatio(true);
        // imageContainer.getChildren().setAll(previewImageView);

        Group zoomGroup = new Group(previewImageView);
        StackPane centerPane = new StackPane(zoomGroup);
        imageContainer.setContent(centerPane);
        imageContainer.setPannable(true);

        pageInfoLbl.textProperty().bind(scannerModel.pageCountInfoProperty());

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

                    TreeNode node = (newItem != null) ? newItem.getValue() : null;

                    if (node == null) {
                        scannerModel.selectNode(null, -1);
                    } else {
                        scannerModel.selectNode(node.file(), node.documentIndex());
                    }

                    resetZoom();

                    if (node == null) {
                        fileNameLabel.setText("");
                    } else if (node.file() != null) {
                        fileNameLabel.setText(
                                new java.io.File(node.file().getFilePath()).getName());
                    } else {
                        fileNameLabel.setText(node.label());
                    }
                }
        );

        imageContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {

                KeyBindingHelper.setupShortcutsForScanning(
                        newScene,
                        this::startNewScan,
                        this::zoomIn,
                        this::zoomOut,
                        this::doRotate
                );
            }
        });

    }

    // ================= SCAN =================

    @FXML
    private void onStartNewScan(ActionEvent event) {
        startNewScan();
    }

    public void startNewScan() {
        scannerModel.scanNext();
    }

    // ================= TREE UPDATES =================

    private void rebuildTree() {
        TreeItem<TreeNode> root =
                treeBuilder.build(scannerModel.documentsProperty());

        documentTree.setRoot(root);
    }

    // ================= IMAGE ROTATION AND ZOOMING =================

    @FXML private void onRotateAction(ActionEvent actionEvent) {
        doRotate();}

    public void doRotate() {
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
        zoomIn(); }

    public void zoomIn() {
      if (currentZoom * ZOOM_FACTOR <= MAX_ZOOM) {
          currentZoom*= ZOOM_FACTOR;
          applyZoom();
      }
    }

    @FXML
    private void onZoomOutAction(ActionEvent event) {
        zoomOut(); }

    public void zoomOut() {
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

        imageContainer.setHvalue(0.5);
        imageContainer.setVvalue(0.5);
    }

    // ====== TREE SELECTION SYNC ======

    private void selectFileInTree(FileEntity file) {
        Platform.runLater(() -> trySelect(file, 0));
    }

    private void trySelect(FileEntity file, int attempt) {

        TreeItem<TreeNode> item = treeBuilder.getNode(file);

        if (item != null) {
            documentTree.getSelectionModel().select(item);
            documentTree.scrollTo(documentTree.getRow(item));
            return;
        }

        if (attempt < 5) {
            Platform.runLater(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {}
                trySelect(file, attempt + 1);
            });
        }
    }

    // ================= EXPORT ====================

    @FXML private void onExportAction(ActionEvent actionEvent) {
        scannerModel.exportDocuments();
        System.out.println("Export has started");
    }

}