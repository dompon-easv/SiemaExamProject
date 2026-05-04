package dk.siema.siemaexamproject.gui.util;

import java.net.URL;

public enum ViewPath {
    LOGIN("/dk/siema/siemaexamproject/gui/LoginView.fxml"),
    MAINSHELL("/dk/siema/siemaexamproject/gui/MainShell.fxml"),
    ADMINSHELL("/dk/siema/siemaexamproject/gui/AdminShell.fxml"),
    SCANNERVIEW("/dk/siema/siemaexamproject/gui/ScannerView.fxml"),
    USERMANAGEMENT("/dk/siema/siemaexamproject/gui/UserManagementView.fxml"),
    SCANNINGPROFILES("/dk/siema/siemaexamproject/gui/ScanningProfilesView.fxml"),
    ACTIVITYLOGS("/dk/siema/siemaexamproject/gui/ActivityLogsView.fxml"),
    ADDUSERVIEW("/dk/siema/siemaexamproject/gui/AddUserView.fxml"),
    ADDPROFILEVIEW("/dk/siema/siemaexamproject/gui/AddProfileView.fxml"),
<<<<<<< Updated upstream
<<<<<<< Updated upstream
    MAIN("/dk/siema/siemaexamproject/gui/hello-view.fxml"),
=======
    CLIENTMANAGEMENT("/dk/siema/siemaexamproject/gui/ClientManagementView.fxml"),
>>>>>>> Stashed changes
=======
    CLIENTMANAGEMENT("/dk/siema/siemaexamproject/gui/ClientManagementView.fxml"),
>>>>>>> Stashed changes
    OTHER("other.fxml");



    private final String path;


    ViewPath(String path) {this.path = path;}
    public String getPath() {return path;}
    public URL getURL() {return ViewPath.class.getResource(path);}
}
