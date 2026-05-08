package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.be.ScanningProfile;

import dk.siema.siemaexamproject.gui.models.AdminModel;
import dk.siema.siemaexamproject.gui.models.MainModel;
import dk.siema.siemaexamproject.gui.models.ScannerModel;
import dk.siema.siemaexamproject.gui.util.AlertHelper;
import dk.siema.siemaexamproject.gui.util.TreeSelectionHelper;

import dk.siema.siemaexamproject.gui.util.DocumentTreeBuilder;

import dk.siema.siemaexamproject.gui.util.KeyBindingHelper;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.List;


public class ScannerViewController implements ApplicationServicesAware {


    private ScannerModel scannerModel;
    private AdminModel adminModel;
    private MainModel mainModel;

    @FXML private Label totalFilesLabel;
    @FXML private Label scanStatusLabel;
    @FXML private Label pageInfoLbl;

    @FXML private Slider rotationSlider;
    @FXML private Label rotationValueLbl;

    @FXML private ProgressBar exportProgressBar;


    @FXML private CheckBox multiPageCheckBox;


    @FXML private TextField selectBoxId;
    @FXML private TreeView<TreeNode> documentTree;
    @FXML private StackPane previewContainer;
    @FXML private ScrollPane imageContainer;

    @FXML private ComboBox profileComboBox;
    @FXML private Label profileDescriptionLabel;
    
    private StackPane mockRectangleVisual;

    private static final String DRAG_OVER_CLASS = "tree-cell-drag-over";

    private static final double ZOOM_FACTOR = 1.2;
    private static final double MAX_ZOOM = 5.0;
    private static final double MIN_ZOOM = 0.2;
    private double currentZoom = 1.0;
    private boolean isUpdatingSliderFromTree = false;

    private ImageView previewImageView;
    private DocumentTreeBuilder treeBuilder;

    public record TreeNode(String label, FileEntity file, int documentIndex) {
        public boolean isFile() {return file != null;}
        public boolean isDocument() {return file == null && documentIndex >= 0;}
        public boolean isBox() {return file == null && documentIndex < 0;}

        @Override
        public String toString() {
            return label;
        }
    }

    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.scannerModel = services.getScannerModel();
        this.adminModel = services.getAdminModel();
        this.mainModel = services.getMainModel();
    }

    @FXML
    private void initialize() {

        setProfiles();


        //adding rectangle when click on box or document for rotation
        createMockDocument();
        previewContainer.getChildren().add(mockRectangleVisual);
        mockRectangleVisual.setVisible(false);
        imageContainer.setVisible(false);

        mockRectangleVisual.setOnMouseClicked(event -> doRotate());

        setupRotationSlider();

        totalFilesLabel.textProperty().bind(scannerModel.totalInfoProperty());

        rotationValueLbl.textProperty().bind(rotationSlider.valueProperty().asString("%.0f°"));

        scannerModel.scanningProperty().addListener((obs, oldVal, newVal) -> {
            scanStatusLabel.setText(newVal ? "Scanning..." : "Scan complete");
        });

        treeBuilder = new DocumentTreeBuilder();

        scannerModel.documentsProperty().addListener((obs, oldVal, newVal) -> rebuildTree());

        rebuildTree();

        documentTree.setCellFactory(tv -> new TreeCell<>() {

            @Override
            protected void updateItem(TreeNode item, boolean empty) {
                super.updateItem(item, empty);

                setText(empty || item == null ? null : item.toString());
                setGraphic(null);

                if (item == null || empty) {
                    getStyleClass().remove(DRAG_OVER_CLASS);
                    return;
                }

                // ================= DRAG START =================
                setOnDragDetected(event -> {
                    if (item.file() == null) return;

                    Dragboard db = startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(item.file().getFilePath());
                    db.setContent(content);
                    event.consume();
                });

                // ================= DRAG OVER =================
                setOnDragOver(event -> {
                    if (event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                    event.consume();
                });

                // ================= DRAG ENTER =================
                setOnDragEntered(event -> {
                    if (!event.getDragboard().hasString()) return;

                    if (!getStyleClass().contains(DRAG_OVER_CLASS)) {
                        getStyleClass().add(DRAG_OVER_CLASS);
                    }
                });

                // ================= DRAG EXIT =================
                setOnDragExited(event -> {
                        getStyleClass().remove(DRAG_OVER_CLASS);
                });

                // ================= DROP =================
                setOnDragDropped(event -> {
                    getStyleClass().remove(DRAG_OVER_CLASS);

                    String path = event.getDragboard().getString();
                    FileEntity dragged = scannerModel.findFileByPath(path);

                    if (dragged != null && item != null) {
                        handleDrop(dragged, item);
                        event.setDropCompleted(true);
                    } else {
                        event.setDropCompleted(false);
                    }

                    event.consume();
                });
            }
        });

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

    private void setProfiles() {
        try {
            adminModel.loadProfilesForUser(mainModel.getCurrentUser().getId());
            profileComboBox.setItems(adminModel.getProfilesForUser());
        } catch (Exception e) {
            AlertHelper.error("Error", "Unable to load profiles for user " + mainModel.getCurrentUser().getId());
        }
    }


    // ================= SCAN =================

    @FXML
    private void onStartNewScan(ActionEvent event) {
        startNewScan();
    }

    public void startNewScan() {

        String boxId = selectBoxId.getText();

        if (boxId == null || boxId.isBlank()) {
            AlertHelper.error("Missing Box ID", "Please enter a Box ID before scanning.");
            return;
        }

        scannerModel.setCurrentBoxId(boxId);

        scannerModel.scanNext();
    }

    // ================= TREE UPDATES =================

    private void rebuildTree() {
        TreeItem<TreeNode> root = treeBuilder.build(
                scannerModel.documentsProperty().get(),
                scannerModel.getCurrentBoxId()
        );
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

    // ============ MOVING FILES ============

    private void handleDrop(FileEntity dragged, TreeNode targetNode) {

        if (dragged == null || targetNode == null) return;

        scannerModel.handleMove(dragged, targetNode.documentIndex(), targetNode.file());

        selectFileInTree(dragged);
    }

    // ================= EXPORT ====================

    @FXML private void onExportAction(ActionEvent actionEvent) {

        ScanningProfile selectedProfile = (ScanningProfile) profileComboBox.getValue();
        if (selectedProfile == null)
            AlertHelper.error("Profile not selected", "Select a profile");

        String boxIdInput = selectBoxId.getText().trim();
        String exportName = selectedProfile.getName() + "_" + boxIdInput;

        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Select Export Directory");
        File selectedDir = dc.showDialog(documentTree.getScene().getWindow());

        if (selectedDir != null) {
            boolean isMultiPage = multiPageCheckBox.isSelected();

            Task<Void> task = scannerModel.exportDocument(selectedDir,isMultiPage,exportName);

            //show the bar
                exportProgressBar.setVisible(true);
                exportProgressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

            scannerModel.exportDocument(selectedDir, isMultiPage, exportName);

            scannerModel.isExportingProperty().addListener((obs, wasExportingl, isNowExporting) -> {
                if(!isNowExporting){
                    Platform.runLater(() -> {exportProgressBar.setVisible(false);});
                }
            });
            }
    }
}
