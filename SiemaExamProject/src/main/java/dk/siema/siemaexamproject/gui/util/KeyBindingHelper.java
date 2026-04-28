package dk.siema.siemaexamproject.gui.util;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.util.HashMap;
import java.util.Map;

public class KeyBindingHelper {

    // --- SCOPE BUCKETS ---
    private static final Map<KeyCombination, Runnable> globalShortcuts = new HashMap<>();
    private static final Map<KeyCombination, Runnable> shellShortcuts = new HashMap<>();
    private static final Map<KeyCombination, Runnable> viewShortcuts = new HashMap<>();

    // Private constructor prevents instantiation
    private KeyBindingHelper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ================= 1. GLOBAL SCOPE =================

    public static void setGlobalLogoutAction(Runnable logoutAction) {
        globalShortcuts.clear();
        if (logoutAction != null) {
            globalShortcuts.put(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN), logoutAction);
        }
        // Note: This just saves it. It gets applied to the scene the next time
        // refreshAccelerators is called (usually when a view loads).
    }

    // ================= 2. SHELL SCOPE =================

    public static void setupShortcutsForAdminShell(Scene scene, Runnable onUserManagement, Runnable onProfiles, Runnable onActivityLogs) {
        shellShortcuts.clear();
        shellShortcuts.put(new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN), onUserManagement);
        shellShortcuts.put(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN), onProfiles);
        shellShortcuts.put(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN), onActivityLogs);

        refreshAccelerators(scene);
    }

    public static void clearShellShortcuts(Scene scene) {
        shellShortcuts.clear();
        refreshAccelerators(scene);
    }

    // ================= 3. VIEW SCOPE =================

    public static void setupShortcutsForScanning(Scene scene, Runnable onScan, Runnable onZoomIn, Runnable onZoomOut, Runnable onRotation) {
        viewShortcuts.clear();
        viewShortcuts.put(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), onScan);
        viewShortcuts.put(new KeyCodeCombination(KeyCode.PLUS, KeyCombination.CONTROL_DOWN), onZoomIn);
        viewShortcuts.put(new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN), onZoomOut);
        viewShortcuts.put(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN), onRotation);

        refreshAccelerators(scene);
    }

    public static void setupShortcutsForUserManagement(Scene scene, Runnable onAddUser, Runnable onEditUser, Runnable onDeleteUser) {
        viewShortcuts.clear();
        viewShortcuts.put(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), onAddUser);
        viewShortcuts.put(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN), onEditUser);
        viewShortcuts.put(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN), onDeleteUser);

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

    // ================= CORE ENGINE =================

    /**
     * Wipes the scene's current shortcuts and safely re-applies them layer by layer.
     * Can be called manually if you just need to force a refresh.
     */
    public static void refreshAccelerators(Scene scene) {
        if (scene == null) return;

        // 1. Wipe everything currently attached to the window
        scene.getAccelerators().clear();

        // 2. Stack them back up, from broadest to most specific
        scene.getAccelerators().putAll(globalShortcuts);
        scene.getAccelerators().putAll(shellShortcuts);
        scene.getAccelerators().putAll(viewShortcuts);
    }
}