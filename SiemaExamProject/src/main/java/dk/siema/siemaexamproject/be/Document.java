package dk.siema.siemaexamproject.be;

import java.util.ArrayList;
import java.util.List;

public class Document {
    private int id;
    private String boxId;
    private String status;
    private List<FileEntity> files = new ArrayList<>();

    public Document(){}

    public void addFile(FileEntity file){
        this.files.add(file);
    }

    public int getId(){return id;}
    public void setId(int id){this.id = id;}

    public List<FileEntity> getFiles() {return files;}
    public void setFiles(List<FileEntity> files) {this.files = files;}
    public String getBoxId() { return boxId; }
    public void setBoxId(String boxId) { this.boxId = boxId; }

    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}
}
