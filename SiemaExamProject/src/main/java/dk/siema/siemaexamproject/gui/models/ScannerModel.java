package dk.siema.siemaexamproject.gui.models;

import dk.siema.siemaexamproject.be.Document;
import dk.siema.siemaexamproject.be.FileEntity;
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

public class ScannerModel {

    private List<Document> documents = new ArrayList<>();

    private final ObjectProperty<FileEntity> selectedFile = new SimpleObjectProperty<>();
    private final ObjectProperty<Image> currentPreviewImage = new SimpleObjectProperty<>();

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

    public ObjectProperty<FileEntity> selectedFileProperty() {return selectedFile;}
    public ObjectProperty<Image> currentPreviewImageProperty() {return currentPreviewImage;}

    public void setSelectedFile(FileEntity selectedFile) {
        this.selectedFile.set(selectedFile);
        loadImageForPreview(selectedFile);
    }

    private void loadImageForPreview(FileEntity selectedFile) {
        if (selectedFile == null || selectedFile.toFile() == null) {
            currentPreviewImage.set(null);
            return;
        }

        try {
            BufferedImage img = ImageIO.read(selectedFile.toFile());
            if (img != null) {
                currentPreviewImage.set(SwingFXUtils.toFXImage(img, null));
            } else {
                currentPreviewImage.set(null);
            }
        }catch (IOException e){
            currentPreviewImage.set(null);
            e.printStackTrace();

        }
    }

}