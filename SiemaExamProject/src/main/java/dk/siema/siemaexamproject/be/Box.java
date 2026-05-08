package dk.siema.siemaexamproject.be;

import java.util.List;

public class Box {
    private int id;
    private String profileId;
    private String boxId;
    private List<Document> documents;

    public Box(String boxId) {
        this.boxId = boxId;
    }

    public int getId() {return id;}
    public void setId(int generatedId) {this.id = generatedId;}
    public String getProfileId() {return profileId;}

    public String getBoxId() {return boxId;}
    public void setBoxId(String boxId) {this.boxId = boxId;}

    public List<Document> getDocuments() {return documents;}
}
