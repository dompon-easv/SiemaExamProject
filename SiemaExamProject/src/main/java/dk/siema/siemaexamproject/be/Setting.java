package dk.siema.siemaexamproject.be;

public class Setting {
    private int id;
    private String name;
    private String defaultValue;


    public Setting(int id, String name, String defaultValue) {
        this.id = id;
        this.name = name;
        this.defaultValue = defaultValue;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return name;
    }
}
