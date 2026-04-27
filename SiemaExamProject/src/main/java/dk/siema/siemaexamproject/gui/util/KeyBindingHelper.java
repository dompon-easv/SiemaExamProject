package dk.siema.siemaexamproject.gui.util;

import javafx.scene.Scene;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;

public class KeyBindingHelper {

    private static Runnable globalLogoutAction;

    public static void setupShortcutsForScanning(Scene scene, Runnable onScan, Runnable onZoomIn, Runnable onZoomOut, Runnable onRotation){
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN), onScan);

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.PLUS, KeyCombination.CONTROL_DOWN), onZoomIn);

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN), onZoomOut);

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN), onRotation);

    }

    public static void setupShortcutsForUserManagement(Scene scene, Runnable onAddUser, Runnable onEditUser, Runnable onDeleteUser){
        scene.getAccelerators().put( new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), onAddUser);

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN), onEditUser);

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN), onDeleteUser);
    }

    public static void setupShortcutsForScanningProfiles(Scene scene, Runnable onNewProfile){
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), onNewProfile);
    }

    public static void setupShortcutsForAdminShell(Scene scene, Runnable onUserManagement, Runnable onProfiles, Runnable onActivityLogs){
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN), onUserManagement);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN), onProfiles);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN), onActivityLogs);
    }


    public static void setGlobalLogoutAction(Runnable logoutAction) {
        globalLogoutAction = logoutAction;
    }

    public static void clearAllShortcuts(Scene scene) {
        if (scene != null) {
            scene.getAccelerators().clear();

            // Reapply global shortcuts, no matter where you are you can  use logout shortcut
            if (globalLogoutAction != null) {
                scene.getAccelerators().put(
                        new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN),
                        globalLogoutAction
                );
            }
        }
    }
}
