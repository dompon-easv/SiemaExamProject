package dk.siema.siemaexamproject.gui.models;

import dk.siema.siemaexamproject.be.Document;
import dk.siema.siemaexamproject.be.FileEntity;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScannerModel {

    private List<Document> documents = new ArrayList<>();

    private final ObjectProperty<FileEntity> selectedFile = new SimpleObjectProperty<>();
    private final ObjectProperty<Image> currentPreviewImage = new SimpleObjectProperty<>();
    private final ExecutorService ioExecutor;

    private final Map<FileEntity, Image> imageCache = new ConcurrentHashMap<>();

    public ScannerModel(ExecutorService ioExecutor) {this.ioExecutor = ioExecutor;}

    public List<Document> getDocuments() {
        return Collections.unmodifiableList(documents);
    }

    public void setDocuments(List<Document> documents) {
        this.documents = (documents == null)
                ? new ArrayList<>()
                : new ArrayList<>(documents);
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

        if (file != null && file.equals(selectedFile.get())) return;

        selectedFile.set(file);

        if (file != null && imageCache.containsKey(file)) {
            currentPreviewImage.set(imageCache.get(file));
            return;
        }

        currentPreviewImage.set(null);
        loadImageAsync(file);
    }

    // ================= ASYNC IMAGE LOADING =================

    // async image loading
    private void loadImageAsync(FileEntity file) {

        if (file == null || file.toFile() == null) {
            currentPreviewImage.set(null);
            return;
        }

        ioExecutor.submit(() -> {
            try {
                BufferedImage img = ImageIO.read(file.toFile());

                Image fxImage = (img != null)
                        ? SwingFXUtils.toFXImage(img, null)
                        : null;

                imageCache.put(file, fxImage);

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