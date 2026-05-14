package dk.siema.siemaexamproject.bll.api;

import dk.siema.siemaexamproject.be.*;
import dk.siema.siemaexamproject.be.enums.ColorMode;
import dk.siema.siemaexamproject.gui.ActivityLogsController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class ScannerService {

    private final TiffService tiffService;
    private final DocumentBuilderService documentBuilderService;
    private final ExecutorService cpuExecutor;

    private String currentBoxId;
    private Document currentDocument = null;
    private int docId = 1;
    private final List<Document> documents = new ArrayList<>();

    public ScannerService(TiffService tiffService,
                          DocumentBuilderService documentBuilderService,
                          ExecutorService cpuExecutor) {

        this.tiffService = tiffService;
        this.documentBuilderService = documentBuilderService;
        this.cpuExecutor = cpuExecutor;
    }

    public File getBarcodeFile() throws Exception {
        List<File> allFiles = tiffService.getAllTiffs();

        // Collect ALL barcode files
        List<File> barcodeFiles = new ArrayList<>();
        for (File file : allFiles) {
            if (documentBuilderService.hasBarcode(file)) {
                barcodeFiles.add(file);
                System.out.println("  Found barcode: " + file.getName());
            }
        }

        // If we found barcode files, return a RANDOM one
        if (!barcodeFiles.isEmpty()) {
            Random random = new Random();
            File randomBarcode = barcodeFiles.get(random.nextInt(barcodeFiles.size()));
            System.out.println("✓ Randomly selected barcode file: " + randomBarcode.getName());
            return randomBarcode;
        }

        //If no barcode found, return first file
        System.out.println("⚠ No barcode found, using first file: " + allFiles.get(0).getName());
        return allFiles.isEmpty() ? null : allFiles.get(0);
    }

    public File getRandomFile() throws Exception {
        return tiffService.getRandomTiff();
    }

    public List<File> getAllFiles() throws Exception {
        return tiffService.getAllTiffs();
    }

    public DocumentBuilderService.PageResult processFile(File file, Profile profile) throws Exception {
        return documentBuilderService.processFile(file, profile);
    }

    public void setCurrentBoxId(String currentBoxId) {
        this.currentBoxId = currentBoxId;
    }

    public String getCurrentBoxId() {
        return currentBoxId;
    }

    public List<Document> handlePage(DocumentBuilderService.PageResult page) {

        if (page == null) return documents;

        if (page.barcode()) {
            currentDocument = new Document();
            currentDocument.setBoxId(currentBoxId);
            currentDocument.setId(docId++);
            documents.add(currentDocument);
        }

        if (currentDocument == null) {
            currentDocument = new Document();
            currentDocument.setBoxId(currentBoxId);
            currentDocument.setId(docId++);
            documents.add(currentDocument);
        }

        currentDocument.addFile(page.entity());

        return documents;
    }

    private Profile convert(ScanningProfile sp) {

        int rotation = 0;
        ColorMode colorMode = ColorMode.COLOR;

        if (sp.getProfileSettings() != null) {
            for (ProfileSetting ps : sp.getProfileSettings()) {

                String name = ps.getSetting().getName().toLowerCase();
                String value = ps.getValue().toLowerCase();

                if (name.contains("rotation")) {
                    rotation = Integer.parseInt(value);
                }

                if (name.contains("color")) {
                    colorMode = switch (value) {
                        case "grayscale" -> ColorMode.GRAYSCALE;
                        case "black_white", "bw" -> ColorMode.BLACK_WHITE;
                        default -> ColorMode.COLOR;
                    };
                }
            }
        }

        return new Profile(rotation, colorMode);
    }

    public void resetState(){
        this.documents.clear();
        this.currentDocument = null;
        this.docId = 1;
    }

    // ============ DELETE FILE ============

    public void deleteStagedFile(UUID referenceId) throws Exception {
        documentBuilderService.deleteStagedFile(referenceId);
    }
}