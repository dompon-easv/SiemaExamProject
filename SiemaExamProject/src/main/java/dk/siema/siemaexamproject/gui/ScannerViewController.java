package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.dal.util.TreeSelectionHelper;
import dk.siema.siemaexamproject.gui.models.ScannerModel;
import dk.siema.siemaexamproject.gui.util.DocumentTreeBuilder;

import dk.siema.siemaexamproject.gui.util.KeyBindingHelper;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;


public class ScannerViewController implements ApplicationServicesAware {

    private ScannerModel scannerModel;

    @FXML private Label fileNameLabel;
    @FXML private Label scanStatusLabel;
    @FXML private Label pageInfoLbl;

    @FXML private Slider rotationSlider;
    @FXML private Label rotationValueLbl;


    @FXML private TreeView<TreeNode> documentTree;
    @FXML private StackPane previewContainer;
    @FXML private ScrollPane imageContainer;

    private StackPane mockRectangleVisual;



    private static final double ZOOM_FACTOR = 1.2;
    private static final double MAX_ZOOM = 5.0;
    private static final double MIN_ZOOM = 0.2;
    private double currentZoom = 1.0;
    private boolean isUpdatingSliderFromTree = false;

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

        //adding rectangle when click on box or document for rotation
        createMockDocument();
        previewContainer.getChildren().add(mockRectangleVisual);
        mockRectangleVisual.setVisible(false);
        imageContainer.setVisible(false);

        mockRectangleVisual.setOnMouseClicked(event -> doRotate());

        setupRotationSlider();

        rotationValueLbl.textProperty().bind(rotationSlider.valueProperty().asString("%.0f°;"));

        scannerModel.scanningProperty().addListener((obs, oldVal, newVal) -> {
            scanStatusLabel.setText(newVal ? "Scanning..." : "Scan complete");
        });

        treeBuilder = new DocumentTreeBuilder();

        scannerModel.documentsProperty().addListener((obs, oldVal, newVal) -> rebuildTree());

        rebuildTree();

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
                        fileNameLabel.setText("");
                        imageContainer.setVisible(false);
                        mockRectangleVisual.setVisible(false);
                        return;
                    }

                    scannerModel.selectNode(node.file(), node.documentIndex());
                    resetZoom();

                    isUpdatingSliderFromTree = true;

                    //Single file is clicked
                    if (node.file() != null) {
                        mockRectangleVisual.setVisible(false);
                        imageContainer.setVisible(true);
                        rotationSlider.setValue(node.file().getRotation());
                        previewImageView.setRotate(node.file().getRotation());
                    } else {
                        //box or document is clicked
                        imageContainer.setVisible(false);
                        mockRectangleVisual.setVisible(true);

                        int folderRotation = TreeSelectionHelper.getEffectiveFolderRotation(newItem);

                        rotationSlider.setValue(folderRotation);
                        mockRectangleVisual.setRotate(folderRotation);
                        previewImageView.setRotate(folderRotation);

                    }
                    //unlock the slider
                    isUpdatingSliderFromTree = false;

                    fileNameLabel.setText(
                            node.file() != null
                                    ? new java.io.File(node.file().getFilePath()).getName()
                                    : node.label()
                    );
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
        TreeItem<TreeNode> root = treeBuilder.build(scannerModel.documentsProperty().get());
        documentTree.setRoot(root);
        expandAll(root);
    }

    private void expandAll(TreeItem<?> item) {
        item.setExpanded(true);
        for(TreeItem<?> child : item.getChildren()) {
            expandAll(child);
        }
    }

    // ================= IMAGE ROTATION AND ZOOMING =================

    @FXML private void onRotateAction(ActionEvent actionEvent) {
        doRotate();}

    public void doRotate() {
        if (rotationSlider != null) {
            double currentAngle = rotationSlider.getValue();
            double newAngle = (currentAngle + 90) % 360;
            rotationSlider.setValue(newAngle);
        }
    }
    public void onPreviousPageAction(ActionEvent actionEvent) {scannerModel.goToPreviousPage();}

    public void onNextPageAction(ActionEvent actionEvent) {scannerModel.goToNextPage();}

    private void createMockDocument() {

        // 1. Make the paper
        Rectangle paper = new Rectangle(150, 200);
        paper.setFill(Color.WHITE);
        paper.setStroke(Color.BLACK);
        paper.setStrokeWidth(2);

        // 2. Make the indicator
        Label topIndicator = new Label("TOP ↑");
        topIndicator.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
        topIndicator.setTranslateY(10);
        StackPane.setAlignment(topIndicator, Pos.TOP_CENTER);

        mockRectangleVisual = new StackPane(paper, topIndicator);

        mockRectangleVisual.setMaxSize(paper.getWidth(),paper.getHeight());
    }

    private void setupRotationSlider() {
        rotationSlider.valueProperty().addListener((obs, oldVal, newVal) -> {

            if (isUpdatingSliderFromTree) return;
            // Get the exact angle from the slider
            int newAngle = newVal.intValue();

            //visually spin the real image and the mock rectangle instantly

            previewImageView.setRotate(newAngle);
            if(mockRectangleVisual != null) {
                mockRectangleVisual.setRotate(newAngle);
            }

            TreeItem<TreeNode> selectedItem = documentTree.getSelectionModel().getSelectedItem();

            if (selectedItem != null){
                TreeSelectionHelper.saveFolderHierarchyRotation(selectedItem,newAngle);
                List<FileEntity> filesToRotate = TreeSelectionHelper.getFilesToRotate(selectedItem);
                scannerModel.updateRotationForFiles(filesToRotate, newAngle);
            }
        });

    }



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
        TreeItem<TreeNode> item = treeBuilder.getNode(file);

        if (item != null) {
            documentTree.getSelectionModel().select(item);
            documentTree.scrollTo(documentTree.getRow(item));
        }
    }

    // ================= EXPORT ====================

    @FXML private void onExportAction(ActionEvent actionEvent) {
        scannerModel.exportDocuments();
        System.out.println("Export has started");
    }

}