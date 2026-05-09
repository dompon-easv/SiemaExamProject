package dk.siema.siemaexamproject.bll.api;

import dk.siema.siemaexamproject.be.Document;
import dk.siema.siemaexamproject.be.Profile;
import dk.siema.siemaexamproject.be.ProfileSetting;
import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.be.enums.ColorMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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

        currentDocument.addPage(page.entity());

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
}