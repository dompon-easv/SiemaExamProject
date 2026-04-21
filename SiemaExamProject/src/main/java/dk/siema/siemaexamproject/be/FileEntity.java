package dk.siema.siemaexamproject.be;

public class FileEntity {
    private int id;
    private int referenceId; //fetched from API
    private int sortOrder;  //order after sorting
    private String filePath; // path to TIFF file
    private int rotation; // 0,90,180,270
    private boolean isBarcode;

    public FileEntity(int id, int referenceId, int sortOrder, String filePath, int rotation, boolean isBarcode) {
        this.id = id;
        this.referenceId = referenceId;
        this.sortOrder = sortOrder;
        this.filePath = filePath;
        this.rotation = rotation;
        this.isBarcode = isBarcode;
    }
    public int getId() {return id;}
    public int getReferenceId() {return referenceId;}

    public int getSortOrder() {return sortOrder;}
    public void setSortOrder(int sortOrder) {this.sortOrder = sortOrder;}

    public String getFilePath() {return filePath;}
    public void setFilePath(String filePath) {this.filePath = filePath;}

    public int getRotation() {return rotation;}
    public void setRotation(int rotation) {this.rotation = rotation;}

    public boolean isBarcode() {return isBarcode;}
}
