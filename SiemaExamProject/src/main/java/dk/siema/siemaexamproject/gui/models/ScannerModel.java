package dk.siema.siemaexamproject.gui.models;

import dk.siema.siemaexamproject.be.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScannerModel {

    private List<Document> documents = new ArrayList<>();

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
    }
}