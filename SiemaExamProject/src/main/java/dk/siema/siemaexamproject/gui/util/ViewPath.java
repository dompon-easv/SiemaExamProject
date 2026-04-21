package dk.siema.siemaexamproject.gui.util;

import java.net.URL;

public enum ViewPath {
    MAIN("/dk/siema/siemaexamproject/gui/hello-view.fxml"),
    OTHER("other.fxml");



    private final String path;


    ViewPath(String path) {this.path = path;}
    public String getPath() {return path;}
    public URL getURL() {return ViewPath.class.getResource(path);}
}
