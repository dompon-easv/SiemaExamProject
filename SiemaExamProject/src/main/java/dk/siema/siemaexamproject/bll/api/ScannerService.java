package dk.siema.siemaexamproject.bll.api;

import dk.siema.siemaexamproject.be.Document;

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

    public DocumentBuilderService.PageResult processFile(File file) throws Exception {
        return documentBuilderService.processFile(file);
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
}