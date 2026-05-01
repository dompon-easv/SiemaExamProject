package dk.siema.siemaexamproject.bll.api;

import dk.siema.siemaexamproject.be.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ScannerService {

    private final TiffService tiffService;
    private final DocumentBuilderService documentBuilderService;
    private final ExecutorService cpuExecutor;

    private List<File> files;
    private int currentIndex = 0;

    public DocumentBuilderService getDocumentBuilderService() {
        return documentBuilderService;
    }

    public ScannerService(TiffService tiffService,
                          DocumentBuilderService documentBuilderService,
                          ExecutorService cpuExecutor) {

        this.tiffService = tiffService;
        this.documentBuilderService = documentBuilderService;
        this.cpuExecutor = cpuExecutor;
    }

    public DocumentBuilderService.PageResult scanNext() throws Exception {

        if (files == null) {
            files = tiffService.getAllTiffs(); // already sorted
        }

        if (currentIndex >= files.size()) {
            return null; // no more files
        }

        File file = files.get(currentIndex++);
        return documentBuilderService.processFile(file);
    }
}