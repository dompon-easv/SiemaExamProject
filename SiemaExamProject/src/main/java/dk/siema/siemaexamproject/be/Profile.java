package dk.siema.siemaexamproject.be;

import dk.siema.siemaexamproject.be.enums.ColorMode;

public class Profile {
    private int id;
    private String name;
    private ColorMode colorMode;
    private int rotation; // 0, 90, 180, 270
    private String splitStrategy;

    public Profile(int id, String name, int rotation, ColorMode colorMode, String splitStrategy) {
        this.id = id;
        this.name = name;
        this.rotation = rotation;
        this.colorMode = colorMode;
        this.splitStrategy = splitStrategy;
    }

    public Profile(int rotation, ColorMode colorMode) {
        this(0, "", rotation, colorMode, "barcode");
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getRotation() { return rotation; }
    public ColorMode getColorMode() { return colorMode; }
    public String getSplitStrategy() { return splitStrategy; }

    @Override
    public String toString() {
        return name;
    }
}