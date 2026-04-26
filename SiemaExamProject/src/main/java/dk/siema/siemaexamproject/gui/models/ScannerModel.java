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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScannerModel {

    private List<Document> documents = new ArrayList<>();

    private final ObjectProperty<FileEntity> selectedFile = new SimpleObjectProperty<>();
    private final ObjectProperty<Image> currentPreviewImage = new SimpleObjectProperty<>();

    // single background thread for image loading
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

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
    }

    public ObjectProperty<FileEntity> selectedFileProperty() {
        return selectedFile;
    }

    public ObjectProperty<Image> currentPreviewImageProperty() {
        return currentPreviewImage;
    }

    public void setSelectedFile(FileEntity file) {

        // avoid reloading same file
        if (file != null && file.equals(selectedFile.get())) return;

        selectedFile.set(file);

        // clear UI immediately (better UX)
        currentPreviewImage.set(null);

        loadImageAsync(file);
    }

    // ================= ASYNC IMAGE LOADING =================

    // async loading (no UI freeze)
    private void loadImageAsync(FileEntity file) {

        if (file == null || file.toFile() == null) {
            currentPreviewImage.set(null);
            return;
        }

        executor.submit(() -> {

            try {
                BufferedImage img = ImageIO.read(file.toFile());

                Image fxImage = (img != null)
                        ? SwingFXUtils.toFXImage(img, null)
                        : null;

                // 🔥 capture selection at start
                FileEntity expectedFile = file;

                Platform.runLater(() -> {

                    // 🔥 race-condition protection
                    if (expectedFile.equals(selectedFile.get())) {
                        currentPreviewImage.set(fxImage);
                    }
                });

            } catch (IOException e) {

                Platform.runLater(() -> currentPreviewImage.set(null));
                e.printStackTrace();
            }
        });
    }

    // ================= CLEANUP =================

    //this method should be called once application is closed
    public void shutdown() {
        executor.shutdown();
    }

    public void rotateFile(FileEntity file) {
        int newRotation = (file.getRotation() + 90) % 360;
        file.setRotation(newRotation);
    }
}