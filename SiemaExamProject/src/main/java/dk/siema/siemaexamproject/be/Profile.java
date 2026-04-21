package dk.siema.siemaexamproject.be;

public class Profile {
    private int id;
    private String name;
    private String splitStrategy;

    public Profile(int id, String name, String splitStrategy) {
        this.id = id;
        this.name = name;
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
