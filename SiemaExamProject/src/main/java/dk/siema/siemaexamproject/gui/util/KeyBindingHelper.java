package dk.siema.siemaexamproject.gui.util;

import dk.siema.siemaexamproject.be.enums.UserRole;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyBindingHelper {

    // --- SCOPE BUCKETS ---
    private static final Map<KeyCombination, Runnable> globalShortcuts = new HashMap<>();
    private static final Map<KeyCombination, Runnable> shellShortcuts = new HashMap<>();
    private static final Map<KeyCombination, Runnable> viewShortcuts = new HashMap<>();


    private static final Map<UserRole, String> shortcutInfo = new HashMap<>();

    static {
        shortcutInfo.put(UserRole.EMPLOYEE, """
                SHOW HELP:   CTRL + H
                LOG OUT:  CTRL + L
                NEW SCAN: CTRL + S
                PREVIEW ZOOM IN: CTRL + PLUS
                PREVIEW ZOOM OUT: CTRL + MINUS
                DELETE FILE: BACKSPACE
                FILE ROTATION: CTRL + R
                EXPORT BOX: CTRL + E """);

        shortcutInfo.put(UserRole.ADMIN, """

                CYCLE THROUGH ADMIN TABS: CTRL + TAB
                OPEN NEW USER/PROFILE WINDOW: CTRL + N
                EDIT SELECTED USER/PROFILE WINDOW: CTRL + E
                DELETE SELECTED USER/PROFILE WINDOW: BACKSPACE""");
    }

    public static Map<UserRole, String> getShortcutInfo() {
        return shortcutInfo;
    }


    // Private constructor prevents instantiation
    private KeyBindingHelper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ================= 1. GLOBAL SCOPE =================

    public static void setGlobalLogoutAction(Runnable logoutAction, Runnable showHelp) {
        globalShortcuts.clear();
        if (logoutAction != null) {
            globalShortcuts.put(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN), logoutAction);
            globalShortcuts.put(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN), showHelp);
        }
    }

    // ================= 2. SHELL SCOPE =================

    public static void setupShortcutsForAdminShell(Scene scene, Runnable cycleViews) {
        shellShortcuts.clear();
        shellShortcuts.put(new KeyCodeCombination(KeyCode.TAB, KeyCombination.CONTROL_DOWN), cycleViews);

        refreshAccelerators(scene);
    }

    public static void clearShellShortcuts(Scene scene) {
        shellShortcuts.clear();
        refreshAccelerators(scene);
    }

    // ================= 3. VIEW SCOPE =================

    public static void setupShortcutsForScanning(Scene scene, Runnable onScan, Runnable onZoomIn, Runnable onZoomOut, Runnable onRotation, Runnable onExport, Runnable onDelete) {
        viewShortcuts.clear();
        viewShortcuts.put(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), onScan);
        viewShortcuts.put(new KeyCodeCombination(KeyCode.PLUS, KeyCombination.CONTROL_DOWN), onZoomIn);
        viewShortcuts.put(new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN), onZoomOut);
        viewShortcuts.put(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN), onRotation);
        viewShortcuts.put(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN), onExport);
        viewShortcuts.put(new KeyCodeCombination(KeyCode.BACK_SPACE), onDelete);

        refreshAccelerators(scene);
    }

    public static void setupShortcutsForUserManagement(Scene scene, Runnable onAddUser, Runnable onEditUser, Runnable onDeleteUser) {
        viewShortcuts.clear();
        viewShortcuts.put(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), onAddUser);
        viewShortcuts.put(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN), onEditUser);
        viewShortcuts.put(new KeyCodeCombination(KeyCode.BACK_SPACE), onDeleteUser);

        refreshAccelerators(scene);
    }

    public static void setupShortcutsForScanningProfiles(Scene scene, Runnable onNewProfile) {
        viewShortcuts.clear();
        viewShortcuts.put(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), onNewProfile);

        refreshAccelerators(scene);
    }

    public static void clearViewShortcuts(Scene scene) {
        viewShortcuts.clear();
        refreshAccelerators(scene);
    }

    public static void refreshAccelerators(Scene scene) {
        if (scene == null) return;


        javafx.application.Platform.runLater(() -> {
            // 1. Wipe everything currently attached to the window
            scene.getAccelerators().clear();

            // 2. Stack them back up, from broadest to most specific
            scene.getAccelerators().putAll(globalShortcuts);
            scene.getAccelerators().putAll(shellShortcuts);
            scene.getAccelerators().putAll(viewShortcuts);
        });
    }
}