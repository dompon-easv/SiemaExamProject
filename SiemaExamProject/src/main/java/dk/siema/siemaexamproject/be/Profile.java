package dk.siema.siemaexamproject.be;

public class Profile {
    private int id;
    private String name;
    private int rotation; //0,90,180,270
    private String splitStrategy;

    public Profile(int id, String name,int rotation, String splitStrategy) {
        this.id = id;
        this.name = name;
        this.rotation = rotation;
        this.splitStrategy = splitStrategy;
    }

    public int getId() {return id;}
    public String getName() {return name;}
    public String getSplitStrategy() {return splitStrategy;}

    @Override
    public String toString() {
        return name;
    }
}
