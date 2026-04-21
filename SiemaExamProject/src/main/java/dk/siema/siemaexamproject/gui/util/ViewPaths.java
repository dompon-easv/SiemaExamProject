package dk.siema.siemaexamproject.gui.util;

import java.net.URL;

public enum ViewPaths {
    MAIN("/dk/siema/siemaexamproject/gui/hello-view.fxml"),
    OTHER("other.fxml");



    private final String path;


    ViewPaths(String path) {this.path = path;}
    public String getPath() {return path;}
    public URL getURL() {return ViewPaths.class.getResource(path);}
}
