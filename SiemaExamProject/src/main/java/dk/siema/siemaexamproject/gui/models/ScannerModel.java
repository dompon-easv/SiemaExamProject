package dk.siema.siemaexamproject.gui.models;

import dk.siema.siemaexamproject.be.Document;
import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.bll.api.DocumentBuilderService;
import dk.siema.siemaexamproject.bll.api.ScannerService;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class ScannerModel {

    private final ListProperty<Document> documents = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final BooleanProperty scanning = new SimpleBooleanProperty(false);
    private final ObjectProperty<FileEntity> selectedFile = new SimpleObjectProperty<>();
    private final ObjectProperty<Image> currentPreviewImage = new SimpleObjectProperty<>();
    private final StringProperty pageCountInfo = new SimpleStringProperty("0 / 0");
    private final StringProperty totalScanInfo = new SimpleStringProperty("Total scanned files: 0");

    //private List<File> files; getalltiffs
    //private int currentIndex = 0; getalltiffs

    private final ScannerService scannerService;
    private final ExecutorService ioExecutor;

    // Simple in-memory cache for loaded images (key = file path)
    private final Map<String, Image> imageCache = new ConcurrentHashMap<>();

    public ScannerModel(ExecutorService ioExecutor, ScannerService scannerService) {
        this.ioExecutor = ioExecutor;
        this.scannerService = scannerService;
    }

    public ReadOnlyBooleanProperty scanningProperty() {
        return scanning;
    }

    public ReadOnlyListProperty<Document> documentsProperty() {
        return documents;
    }

    public ObjectProperty<FileEntity> selectedFileProperty() {
        return selectedFile;
    }

    public ObjectProperty<Image> currentPreviewImageProperty() {
        return currentPreviewImage;
    }

    // ================= SCAN =================

    public StringProperty totalInfoProperty() {
        return totalScanInfo;
    }

    public void scanNext() {

        Task<DocumentBuilderService.PageResult> task = new Task<>() {
            @Override
            protected DocumentBuilderService.PageResult call() throws Exception {
                File file = scannerService.getRandomFile();
                if (file == null) return null;
                return scannerService.processFile(file);
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

            if (!doc.getPages().isEmpty()) {
                setSelectedFile(doc.getPages().get(0));
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

                Image fxImage = (img != null)
                        ? SwingFXUtils.toFXImage(img, null)
                        : null;

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
            flatList.addAll(doc.getPages());
        }
        return flatList;
    }

    private void updateTotalScannedFiles() {

        int totalFiles = 0;

        for (Document doc : documents) {
            totalFiles += doc.getPages().size();
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
            List<FileEntity> filesInDoc = doc.getPages();

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

    // ================= EXPORT ========================

    public void exportDocuments() {
    }


    //updates the rotation only for currently selected file
    public void updateRotationForCurrentFile(int newAngle) {
        FileEntity current = selectedFile.get();
        if (current == null) {
            current.setRotation(newAngle);
        }
    }

    public void updateRotationForFiles(List<FileEntity> filesToRotate, int newAngle) {
        for (FileEntity file : filesToRotate) {
            file.setRotation(newAngle);
        }
    }
}