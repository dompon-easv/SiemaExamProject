package dk.siema.siemaexamproject.be;

import java.io.File;
import java.util.UUID;

public class FileEntity {
    private int id;
    private UUID referenceId;
    private int documentId;//fetched from API
    private int sortOrder;  //order after sorting
    private String filePath; // path to TIFF file
    private int rotation; // 0,90,180,270
    private boolean isBarcode;

    //constructor for existing files
    public FileEntity(int id, UUID referenceId,int documentId, int sortOrder, String filePath, int rotation, boolean isBarcode) {
        this.id = id;
        this.referenceId = referenceId;
        this.documentId = documentId;
        this.sortOrder = sortOrder;
        this.filePath = filePath;
        this.rotation = rotation;
        this.isBarcode = isBarcode;
    }
    //constructor for API
    public FileEntity(UUID referenceId, int sortOrder, String filePath, int rotation, boolean isBarcode) {
        this.id = 0;
        this.referenceId = referenceId;
        this.sortOrder = sortOrder;
        this.filePath = filePath;
        this.rotation = rotation;
        this.isBarcode = isBarcode;
    }

    public int getId() {return id;}
    public UUID getReferenceId() {return referenceId;}

    public int getDocumentId() {return documentId;}
    public void setDocumentId(int documentId) {this.documentId=documentId;}

    public int getSortOrder() {return sortOrder;}
    public void setSortOrder(int sortOrder) {this.sortOrder = sortOrder;}

    public String getFilePath() {return filePath;}
    public void setFilePath(String filePath) {this.filePath = filePath;}

    public int getRotation() {return rotation;}
    public void setRotation(int rotation) {this.rotation = rotation;}

    public boolean isBarcode() {return isBarcode;}
    public File toFile() {
        return new File(filePath);
    }
}
