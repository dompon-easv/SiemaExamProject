package dk.siema.siemaexamproject.be;

import java.util.ArrayList;
import java.util.List;

public class Document {
    private int id;
    private String status;
    private List<FileEntity> files;

    public Document(){
        files = new ArrayList<>();
        this.status = "In Progress";
    }

    public void addPage(FileEntity page){
        this.files.add(page);
    }

    public int getId(){return id;}
    public void setId(int id){this.id = id;}

    public List<FileEntity> getPages() {return files;}
    public void setPages(List<FileEntity> pages) {this.files = pages;}

    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}
}
