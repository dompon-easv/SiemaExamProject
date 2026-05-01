package dk.siema.siemaexamproject.be;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class Document {
    private int id;
    private String status;

    private final ObservableList<FileEntity> pages = FXCollections.observableArrayList();

    public Document(){
        this.status = "In Progress";
    }

    public void addPage(FileEntity page){
        this.pages.add(page);
    }

    public int getId() {return id;}
    public void setId(int id){this.id = id;}

    public ObservableList<FileEntity> getPages() {return pages;}
    public void setPages(List<FileEntity> pages){this.pages.addAll(pages);}

    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}
}
