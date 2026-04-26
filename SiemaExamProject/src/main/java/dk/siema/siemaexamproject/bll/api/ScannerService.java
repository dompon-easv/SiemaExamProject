package dk.siema.siemaexamproject.bll.api;

import dk.siema.siemaexamproject.be.Document;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public class ScannerService {

    private final TiffService tiffService;
    private final DocumentBuilderService documentBuilderService;
    private final ExecutorService executor;

    public ScannerService(TiffService tiffService,
                          DocumentBuilderService documentBuilderService,
                          ExecutorService executor) {

        this.tiffService = tiffService;
        this.documentBuilderService = documentBuilderService;
        this.executor = executor;
    }

    private record IndexedResult(int index, DocumentBuilderService.PageResult result) {}

    public List<Document> scan() throws Exception {

        List<File> files = tiffService.getAllTiffs();

        CompletionService<IndexedResult> completion =
                new ExecutorCompletionService<>(executor);

        // submit jobs
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

        // rebuild correct order
        List<DocumentBuilderService.PageResult> pages = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            pages.add(ordered.get(i));
        }

        return documentBuilderService.buildDocuments(pages);
    }
}