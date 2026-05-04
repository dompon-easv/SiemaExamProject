package dk.siema.siemaexamproject.be;

import java.util.List;

public class Box {
    private int id;
    private String profileId;
    private List<Document> documents;

    public Box(String boxReference) {
        this.profileId = boxReference;
    }
    public int getId() {return id;}
    public void setId(int generatedId) {this.id = generatedId;}
    public String getProfileId() {return profileId;}


    public List<Document> getDocuments() {return documents;}
}
