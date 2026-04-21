package dk.siema.siemaexamproject.gui.util;

import javafx.scene.control.Alert;

import static sun.tools.jconsole.Messages.INFO;

public class AlertHelper {
    public static void InformationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(INFO);
        alert.setContentText(message);
    }
    public static void WarningAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(WARNING);
        alert.setContentText(message);
    }
}
