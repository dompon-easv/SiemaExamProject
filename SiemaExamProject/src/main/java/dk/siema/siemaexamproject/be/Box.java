package dk.siema.siemaexamproject.be;

public class Box {
    private int id;

    private String profileId;

    public Box(String boxReference) {
        this.profileId = boxReference;
    }
    public int getId() {return id;}
    public String getBoxReference() {return profileId;}
    public void setBoxReference(String boxReference) {this.profileId = boxReference;}
}
