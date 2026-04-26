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

    public ScannerService(TiffService tiffService,
                          DocumentBuilderService documentBuilderService,
                          ExecutorService cpuExecutor) {

        this.tiffService = tiffService;
        this.documentBuilderService = documentBuilderService;
        this.cpuExecutor = cpuExecutor;
    }

    // STEP 1: Start scan and fetch TIFF files (I/O operation)
    public List<Document> scan() throws Exception {

        List<File> files = tiffService.getAllTiffs();

        // STEP 2: Submit all files for parallel processing (CPU-bound work)
        List<Future<DocumentBuilderService.PageResult>> futures = new ArrayList<>();

        for (File file : files) {
            futures.add(cpuExecutor.submit(() ->
                    documentBuilderService.processFile(file)
            ));
        }

        // STEP 3: Collect results from parallel execution
        List<DocumentBuilderService.PageResult> pages = new ArrayList<>();

        for (Future<DocumentBuilderService.PageResult> future : futures) {
            DocumentBuilderService.PageResult result = future.get();

            if (result != null) {
                pages.add(result);
            }
        }

        // STEP 4: Build final document structure
        return documentBuilderService.buildDocuments(pages);
    }
}