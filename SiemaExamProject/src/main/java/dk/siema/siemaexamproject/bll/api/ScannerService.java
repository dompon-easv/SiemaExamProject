package dk.siema.siemaexamproject.bll.api;

import dk.siema.siemaexamproject.be.Document;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

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

    private record IndexedResult(int index, DocumentBuilderService.PageResult result) {}

    public List<Document> scan() throws Exception {

        List<File> files = tiffService.getAllTiffs();

        CompletionService<IndexedResult> completion =
                new ExecutorCompletionService<>(cpuExecutor);

        // submit CPU jobs
        for (int i = 0; i < files.size(); i++) {
            int index = i;
            File file = files.get(i);

            completion.submit(() -> {
                var result = documentBuilderService.processFile(file);
                return new IndexedResult(index, result);
            });
        }

        // collect results
        Map<Integer, DocumentBuilderService.PageResult> ordered = new HashMap<>();

        for (int i = 0; i < files.size(); i++) {
            IndexedResult r = completion.take().get();
            ordered.put(r.index(), r.result());
        }

        // correct ordering
        List<DocumentBuilderService.PageResult> pages = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            pages.add(ordered.get(i));
        }

        return documentBuilderService.buildDocuments(pages);
    }
}