package dk.siema.siemaexamproject.bll.api;

import dk.siema.siemaexamproject.be.Document;

import java.io.File;
import java.util.*;
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

    // wrapper to preserve order
    private record FileJob(int index, File file) {}

    public List<Document> scan() throws Exception {

        List<File> files = tiffService.getAllTiffs();

        List<Future<DocumentBuilderService.PageResult>> futures = new ArrayList<>();

        // STEP 1: attach index BEFORE parallelism
        List<FileJob> jobs = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            jobs.add(new FileJob(i, files.get(i)));
        }

        Map<Integer, DocumentBuilderService.PageResult> orderedResults = new ConcurrentHashMap<>();

        for (FileJob job : jobs) {
            futures.add(executor.submit(() -> {
                var result = documentBuilderService.processFile(job.file());
                orderedResults.put(job.index(), result);
                return result;
            }));
        }

        for (Future<?> f : futures) {
            f.get();
        }

        // STEP 2: restore order explicitly
        List<DocumentBuilderService.PageResult> pages = new ArrayList<>();

        for (int i = 0; i < jobs.size(); i++) {
            pages.add(orderedResults.get(i));
        }

        return documentBuilderService.buildDocuments(pages);
    }
}