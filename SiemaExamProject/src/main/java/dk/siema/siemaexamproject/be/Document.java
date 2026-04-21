package dk.siema.siemaexamproject.be;

import java.util.ArrayList;
import java.util.List;

public class Document {
    private int id;
    private String status;
    private List<FileEntity> pages;

    public Document(){
        pages = new ArrayList<>();
        this.status = "In Progress";
    }

    public void addPage(FileEntity page){
        this.pages.add(page);
    }

    public void getId(int id){this.id = id;}
    public void setId(int id){this.id = id;}

    public List<FileEntity> getPages() {return pages;}
    public void setPages(List<FileEntity> pages) {this.pages = pages;}

    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}
}
