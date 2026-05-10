package dk.siema.siemaexamproject.be;

import java.util.ArrayList;
import java.util.List;

public class Box {
    private int id;
    private String exportName;
    private int profileId;
    private String boxId;
    private List<Document> documents = new ArrayList<>();

    public Box() {}

    public int getId() {return id;}
    public void setId(int generatedId) {this.id = generatedId;}
    public int getProfileId() {return profileId;}
    public void setProfileId(int profileId) {this.profileId = profileId;}

    public String getExportName() {return exportName;}
    public void setExportName(String exportName) {this.exportName = exportName;}

    public String getBoxId() {return boxId;}
    public void setBoxId(String boxId) {this.boxId = boxId;}

    public List<Document> getDocuments() {return documents;}
}
