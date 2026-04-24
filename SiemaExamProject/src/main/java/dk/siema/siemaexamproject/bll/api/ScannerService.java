package dk.siema.siemaexamproject.bll.api;

import dk.siema.siemaexamproject.be.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ScannerService {

    private final TiffService tiffService;
    private final DocumentBuilderService documentBuilderService;

    private final ExecutorService executor =
            Executors.newFixedThreadPool(
                    Runtime.getRuntime().availableProcessors()
            );

    public ScannerService(TiffService tiffService,
                          DocumentBuilderService documentBuilderService) {
        this.tiffService = tiffService;
        this.documentBuilderService = documentBuilderService;
    }

    public List<Document> scan() throws Exception {

        List<File> files = tiffService.getAllTiffs();

        List<Future<DocumentBuilderService.PageResult>> futures = new ArrayList<>();

        // STEP 1: parallel processing per file
        for (File file : files) {
            futures.add(executor.submit(() ->
                    documentBuilderService.processFile(file)
            ));
        }

        List<DocumentBuilderService.PageResult> pages = new ArrayList<>();

        for (Future<DocumentBuilderService.PageResult> f : futures) {
            pages.add(f.get());
        }

        // STEP 2: build documents (single-threaded grouping)
        return documentBuilderService.buildDocuments(pages);
    }
}