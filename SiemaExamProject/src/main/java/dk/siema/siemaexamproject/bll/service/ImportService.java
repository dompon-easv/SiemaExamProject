package dk.siema.siemaexamproject.bll.service;

import dk.siema.siemaexamproject.be.enums.ImportMode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImportService {

    public List<File> selectFiles(ImportMode mode, Window ownerWindow) {

        return switch (mode) {
            case FILES -> chooseFiles(ownerWindow);
            case DIRECTORY -> chooseDirectory(ownerWindow);
        };
    }

    private List<File> chooseFiles(Window ownerWindow) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Import Images");

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images",
                        "*.tif", "*.tiff", "*.png", "*.jpg", "*.jpeg", "*.bmp")
        );

        List<File> files = chooser.showOpenMultipleDialog(ownerWindow);
        return files != null ? files : List.of();
    }

    private List<File> chooseDirectory(Window ownerWindow) {
        System.out.println("=== chooseDirectory called ===");
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Folder");

        File dir = chooser.showDialog(ownerWindow);
        System.out.println("Selected directory: " + (dir != null ? dir.getAbsolutePath() : "NULL"));

        if (dir == null) {
            System.out.println("No directory selected");
            return List.of();
        }

        List<File> result = collectImageFiles(dir);
        System.out.println("Found " + result.size() + " image files in directory");
        return result;
    }

    private List<File> collectImageFiles(File dir) {
        System.out.println("collectImageFiles scanning: " + dir.getAbsolutePath());
        List<File> result = new ArrayList<>();

        File[] files = dir.listFiles();
        if (files == null) {
            System.out.println("  Cannot list files (null returned)");
            return result;
        }

        System.out.println("  Total items in directory: " + files.length);

        for (File f : files) {
            if (f.isDirectory()) {
                System.out.println("  Entering subdirectory: " + f.getName());
                result.addAll(collectImageFiles(f));
            } else if (isImage(f)) {
                System.out.println("  Found image: " + f.getName());
                result.add(f);
            } else {
                System.out.println("  Skipping non-image: " + f.getName());
            }
        }

        return result;
    }

    private boolean isImage(File file) {
        String n = file.getName().toLowerCase();
        boolean isImg = n.endsWith(".tif") || n.endsWith(".tiff")
                || n.endsWith(".png") || n.endsWith(".jpg")
                || n.endsWith(".jpeg") || n.endsWith(".bmp")
                || n.endsWith(".gif");  // Added GIF support
        if (!isImg) {
            System.out.println("    Not an image: " + n);
        }
        return isImg;
    }
}