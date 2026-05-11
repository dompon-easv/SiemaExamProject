package dk.siema.siemaexamproject.gui.models;

import dk.siema.siemaexamproject.be.Box;
import dk.siema.siemaexamproject.be.Document;
import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.be.Profile;
import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.bll.api.DocumentBuilderService;
import dk.siema.siemaexamproject.bll.api.ScannerService;
import dk.siema.siemaexamproject.gui.util.AlertHelper;
import dk.siema.siemaexamproject.bll.service.ExportService;
import dk.siema.siemaexamproject.gui.ScannerViewController;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class ScannerModel {

    private final ListProperty<Document> documents = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final BooleanProperty scanning = new SimpleBooleanProperty(false);
    private final BooleanProperty isExporting = new SimpleBooleanProperty(false);
    private final ObjectProperty<FileEntity> selectedFile = new SimpleObjectProperty<>();
    private final ObjectProperty<Image> currentPreviewImage = new SimpleObjectProperty<>();
    private final StringProperty pageCountInfo = new SimpleStringProperty("0 / 0");
    private final StringProperty totalScanInfo = new SimpleStringProperty("Total scanned files: 0");
    private final StringProperty currentBoxId = new SimpleStringProperty();

    private boolean isFirstScan = true;

    //private List<File> files; getalltiffs
    //private int currentIndex = 0; getalltiffs

    private final ScannerService scannerService;
    private final ExecutorService ioExecutor;
    private final ExportService exportService;

    // Simple in-memory cache for loaded images (key = file path)
    private final Map<String, Image> imageCache = new ConcurrentHashMap<>();

    public ScannerModel(ExecutorService ioExecutor, ScannerService scannerService, ExportService exportService) {
        this.ioExecutor = ioExecutor;
        this.scannerService = scannerService;
        this.exportService = exportService;
    }

    public ReadOnlyBooleanProperty scanningProperty() {
        return scanning;
    }
    public BooleanProperty isExportingProperty() {return isExporting;}

    public ReadOnlyListProperty<Document> documentsProperty() {
        return documents;
    }

    public ObjectProperty<FileEntity> selectedFileProperty() {
        return selectedFile;
    }

    public ObjectProperty<Image> currentPreviewImageProperty() {
        return currentPreviewImage;
    }

    // ============ SCANNING PROFILES ============

    private final ObjectProperty<Profile> selectedProfile = new SimpleObjectProperty<>();

    public ObjectProperty<Profile> selectedProfileProperty() {
        return selectedProfile;
    }

    public Profile getSelectedProfile() {
        return selectedProfile.get();
    }

    public void setSelectedProfile(Profile profile) {
        selectedProfile.set(profile);
    }

    private BufferedImage applyColorMode(BufferedImage img, String colorMode) {
        if (colorMode == null || colorMode.equals("COLOR")) return img;

        switch (colorMode) {
            case "GRAYSCALE": {
                BufferedImage gray = new BufferedImage(
                        img.getWidth(),
                        img.getHeight(),
                        BufferedImage.TYPE_BYTE_GRAY
                );
                Graphics g = gray.getGraphics();
                g.drawImage(img, 0, 0, null);
                g.dispose();
                return gray;
            }
            case "BLACK_WHITE": {
                BufferedImage bw = new BufferedImage(
                        img.getWidth(),
                        img.getHeight(),
                        BufferedImage.TYPE_BYTE_BINARY
                );
                Graphics g = bw.getGraphics();
                g.drawImage(img, 0, 0, null);
                g.dispose();
                return bw;
            }
            default:
                return img;
        }
    }

    private BufferedImage rotateImage(BufferedImage img, int angle) {
        if (angle == 0) return img;

        double r = Math.toRadians(angle);
        int w = img.getWidth();
        int h = img.getHeight();

        BufferedImage result = new BufferedImage(w, h, img.getType());
        Graphics2D g = result.createGraphics();
        g.rotate(r, w / 2.0, h / 2.0);
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return result;
    }

    // ================= BOX ID =================

    public String getCurrentBoxId() {
        return currentBoxId.get();
    }

    public void setCurrentBoxId(String boxId) {
        currentBoxId.set(boxId);
        scannerService.setCurrentBoxId(boxId);
    }

    public StringProperty currentBoxIdProperty() {
        return currentBoxId;
    }

    // ================= SCAN =================

    public StringProperty totalInfoProperty() {
        return totalScanInfo;
    }

    public void scanNext() {

        Profile profile = selectedProfile.get();
        System.out.println("scanNext called with profile: " + profile);
        if (profile == null) {
            System.out.println("ERROR: No profile selected!");
            Platform.runLater(() -> {
                AlertHelper.warning("No Profile Selected", "Please select a scanning profile before starting.");
            });
            return;
        }
        System.out.println("Using profile - Rotation: " + profile.getRotation() +
                ", ColorMode: " + profile.getColorMode());

        Task<DocumentBuilderService.PageResult> task = new Task<>() {
            @Override
            protected DocumentBuilderService.PageResult call() throws Exception {
                File file;

                if (isFirstScan) {
                    //try to get barcode as first scan
                    file = scannerService.getBarcodeFile();
                    isFirstScan = false;
                } else {
                    //subsequent scan fetch random file
                    file = scannerService.getRandomFile();
                }

                if (file == null) return null;

                return scannerService.processFile(file, profile);
            }

            @Override
            protected void running() {
                scanning.set(true);
            }

            @Override
            protected void succeeded() {
                scanning.set(false);

                DocumentBuilderService.PageResult page = getValue();
                if (page == null) return;

                List<Document> updateDocs = scannerService.handlePage(page);

                Platform.runLater(() -> {
                    documents.setAll(updateDocs);
                    setSelectedFile(page.entity());
                    updateTotalScannedFiles();
                });
            }

            @Override
            protected void failed() {
                scanning.set(false);
                getException().printStackTrace();
            }
        };

        ioExecutor.submit(task);
    }

    // ================= SELECTION =================

    public void selectNode(FileEntity file, int documentIndex) {

        // PAGE
        if (file != null) {
            setSelectedFile(file);
            return;
        }

        // DOCUMENT
        if (documentIndex >= 0 && documentIndex < documents.size()) {
            Document doc = documents.get(documentIndex);

            if (!doc.getFiles().isEmpty()) {
                setSelectedFile(doc.getFiles().get(0));
                return;
            }
        }

        // BOX
        setSelectedFile(null);
    }

    public void setSelectedFile(FileEntity file) {

        if (file != null && file.equals(selectedFile.get())) return;

        selectedFile.set(file);
        updatePageCountInfo();

        if (file == null) {
            currentPreviewImage.set(null);
            return;
        }

        String key = file.getFilePath();

        Image cached = imageCache.get(key);
        if (cached != null) {
            currentPreviewImage.set(cached);
            return;
        }

        currentPreviewImage.set(null);
        loadImageAsync(file);
    }

    // ================= IMAGE LOADING =================

    private void loadImageAsync(FileEntity file) {

        if (file == null || file.toFile() == null) return;

        ioExecutor.submit(() -> {
            try {
                BufferedImage img = ImageIO.read(file.toFile());

                // Apply color mode
                if (file.getColorMode() != null && !file.getColorMode().equals("COLOR")) {
                    img = applyColorMode(img, file.getColorMode());
                }

                Image fxImage = (img != null) ? SwingFXUtils.toFXImage(img, null) : null;

                Platform.runLater(() -> {
                    imageCache.put(file.getFilePath(), fxImage);

                    if (file.equals(selectedFile.get())) {
                        currentPreviewImage.set(fxImage);
                    }
                });

            } catch (IOException e) {
                Platform.runLater(() -> {
                    if (file.equals(selectedFile.get())) {
                        currentPreviewImage.set(null);
                    }
                });
            }
        });
    }

    // ================= PAGE NAVIGATION =================

    public StringProperty pageCountInfoProperty() {return pageCountInfo;}

    public void goToNextPage() {
        FileEntity current = selectedFile.get();
        if (current == null) return;

        List<FileEntity> allPages = getAllPagesFlattened();
        int currentIndex = allPages.indexOf(current);

        if (currentIndex >= 0 && currentIndex < allPages.size() - 1) {
            setSelectedFile(allPages.get(currentIndex + 1));
        }
    }

    public void goToPreviousPage() {
        FileEntity current = selectedFile.get();
        if (current == null) return;

        List<FileEntity> allPages = getAllPagesFlattened();
        int currentIndex = allPages.indexOf(current);

        if (currentIndex > 0) {
            setSelectedFile(allPages.get(currentIndex - 1));
        }
    }

    private List<FileEntity> getAllPagesFlattened() {
        List<FileEntity> flatList = new ArrayList<>();
        for (Document doc : documents) {
            flatList.addAll(doc.getFiles());
        }
        return flatList;
    }

    private void updateTotalScannedFiles() {

        int totalFiles = 0;

        for (Document doc : documents) {
            totalFiles += doc.getFiles().size();
        }

        totalScanInfo.set("Total scanned files: " + totalFiles);
    }

    private void updatePageCountInfo() {

        FileEntity current = selectedFile.get();

        if (current == null || documents.isEmpty()) {
            pageCountInfo.set("No file selected");
            return;
        }

        for (int d = 0; d < documents.size(); d++) {
            Document doc = documents.get(d);
            List<FileEntity> filesInDoc = doc.getFiles();

            for (int f = 0; f < filesInDoc.size(); f++) {
                FileEntity checkFile = filesInDoc.get(f);


                if (checkFile.getFilePath().equals(current.getFilePath())) {
                    //Found the file , build the label.
                    int docNumber = d + 1;
                    int totalFilesInDoc = filesInDoc.size();

                    pageCountInfo.set("Document " + docNumber + " — Page " + (f + 1) + "/" + totalFilesInDoc);
                }
            }
        }
    }

    // ================= IMAGE ROTATION =================

    public void rotateFile(FileEntity file) {
        int newRotation = (file.getRotation() + 90) % 360;
        file.setRotation(newRotation);
    }

    // ================= MOVING FILES ========================

    public FileEntity findFileByPath(String path) {
        for (Document doc : documents) {
            for (FileEntity file : doc.getFiles()) {
                if (file.getFilePath().equals(path)) {
                    return file;
                }
            }
        }
        return null;
    }

    public void moveFileBefore(FileEntity file, int targetDocIndex, FileEntity targetFile) {

        if (file == null) return;

        Document source = findDocument(file);
        Document target = documents.get(targetDocIndex);

        if (source == null || target == null) return;

        source.getFiles().remove(file);

        List<FileEntity> list = target.getFiles();
        int index = list.indexOf(targetFile);

        if (index < 0) list.add(file);
        else list.add(index, file);

        documents.setAll(new ArrayList<>(documents));
    }

    public void moveFileToDocument(FileEntity file, int targetDocIndex) {

        if (file == null) return;

        Document source = findDocument(file);
        Document target = documents.get(targetDocIndex);

        if (source == null || target == null) return;

        source.getFiles().remove(file);
        target.getFiles().add(file);

        documents.setAll(new ArrayList<>(documents));
    }

    public void moveFileToLastDocument(FileEntity file) {
        if (documents.isEmpty()) return;
        moveFileToDocument(file, documents.size() - 1);
    }

    private Document findDocument(FileEntity file) {
        for (Document doc : documents) {
            if (doc.getFiles().contains(file)) return doc;
        }
        return null;
    }

    public void handleMove(FileEntity file, int targetDocIndex, FileEntity targetFile) {

        if (file == null) return;

        // FILE → insert before file
        if (targetFile != null) {
            moveFileBefore(file, targetDocIndex, targetFile);
            return;
        }

        // DOCUMENT → append to document
        if (targetDocIndex >= 0) {
            moveFileToDocument(file, targetDocIndex);
            return;
        }

        // BOX → last document
        moveFileToLastDocument(file);
    }

    // ================= EXPORT ========================

    public Task<Void> exportDocument(File targetDir, boolean isMultiPage, String exportName, int profileId) {
        if (isExporting.get()) return null;
        isExporting.set(true);

        Task<Void> exportTask = new Task<>() {
            @Override
            protected Void call() throws Exception {

                Box exportedBox = new Box();
                exportedBox.setExportName(exportName);
                exportedBox.setProfileId(profileId);

                //Fix the Sort Order and relationships based on current state of TreeView/List state

                List<Document> currentTreeView = new ArrayList<>(documents.get());
                for (Document doc : currentTreeView) {
                    List<FileEntity> files = doc.getFiles();
                    for (int i = 0; i < files.size(); i++) {
                        files.get(i).setSortOrder(i + 1);
                    }
                }
                exportedBox.getDocuments().addAll(currentTreeView);


                exportService.processExport(exportedBox, targetDir, isMultiPage, this);
                return null;
            }
        };

        exportTask.setOnSucceeded(e -> isExporting.set(false));
        exportTask.setOnFailed(e -> {
            isExporting.set(false);
            exportTask.getException().printStackTrace();
        });
        ioExecutor.submit(exportTask);
        return exportTask;
    }

    public void resetState(){
        //clear the observable list
        documents.clear();
        //reset selection properties
        selectedFile.set(null);
        currentPreviewImage.set(null);

        //clear the image cache to free up memory
        imageCache.clear();

        //reset counter strings
        updateTotalScannedFiles();

    }


    //updates the rotation only for currently selected file
    public void updateRotationForCurrentFile(int newAngle) {
        FileEntity current = selectedFile.get();
        if (current != null) {
            current.setRotation(newAngle);
        }
    }

    public void updateRotationForFiles(List<FileEntity> filesToRotate, int newAngle) {
        for (FileEntity file : filesToRotate) {
            file.setRotation(newAngle);
        }
    }
}