package dk.siema.siemaexamproject.gui.models;

import dk.siema.siemaexamproject.be.Document;
import dk.siema.siemaexamproject.be.FileEntity;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;

public class ScannerModel {

    private List<Document> documents = new ArrayList<>();

    private final ObjectProperty<FileEntity> selectedFile = new SimpleObjectProperty<>();
    private final ObjectProperty<Image> currentPreviewImage = new SimpleObjectProperty<>();

    private final ExecutorService ioExecutor;

    // Simple in-memory cache for loaded images (key = file path)
    private final Map<String, Image> imageCache = new HashMap<>();

    public ScannerModel(ExecutorService ioExecutor) {
        this.ioExecutor = ioExecutor;
    }

    public List<Document> getDocuments() {
        return Collections.unmodifiableList(documents);
    }

    public void setDocuments(List<Document> documents) {
        this.documents = (documents == null) ? new ArrayList<>() : new ArrayList<>(documents);
    }

    public void clear() {
        documents.clear();
        selectedFile.set(null);
        currentPreviewImage.set(null);
        imageCache.clear();
    }

    public ObjectProperty<FileEntity> selectedFileProperty() {
        return selectedFile;
    }

    public ObjectProperty<Image> currentPreviewImageProperty() {
        return currentPreviewImage;
    }

    // ================= SELECTION =================

    public void setSelectedFile(FileEntity file) {

        // Avoid reloading if same file is selected again
        if (file != null && file.equals(selectedFile.get())) return;

        selectedFile.set(file);

        if (file == null) {
            currentPreviewImage.set(null);
            return;
        }

        String key = file.getFilePath();

        // If image already exists in cache, reuse it immediately
        if (imageCache.containsKey(key)) {
            currentPreviewImage.set(imageCache.get(key));
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

                // Store result in cache for future fast access
                imageCache.put(file.getFilePath(), fxImage);

                // Ensure UI update happens on JavaFX thread
                Platform.runLater(() -> {
                    if (file.equals(selectedFile.get())) {
                        currentPreviewImage.set(fxImage);
                    }
                });

            } catch (IOException e) {
                Platform.runLater(() -> currentPreviewImage.set(null));
            }
        });
    }

    // ================= IMAGE ROTATION =================

    public void rotateFile(FileEntity file) {
        int newRotation = (file.getRotation() + 90) % 360;
        file.setRotation(newRotation);
    }
}