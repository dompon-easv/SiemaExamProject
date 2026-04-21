package dk.siema.siemaexamproject.be;

public class Box {
    private int id;
    private String boxReference;

    public Box(String boxReference) {
        this.boxReference = boxReference;
    }
    public int getId() {return id;}
    public String getBoxReference() {return boxReference;}
    public void setBoxReference(String boxReference) {this.boxReference = boxReference;}
}
