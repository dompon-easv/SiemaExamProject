package dk.siema.siemaexamproject.bll.api;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ScannerService {

    private final TiffService tiffService;
    private final DocumentBuilderService documentBuilderService;
    private final ExecutorService cpuExecutor;

    public List<File> getAllFiles() throws Exception {
        return tiffService.getAllTiffs();
    }

    public DocumentBuilderService.PageResult processFile(File file) throws Exception {
        return documentBuilderService.processFile(file);
    }

    public ScannerService(TiffService tiffService,
                          DocumentBuilderService documentBuilderService,
                          ExecutorService cpuExecutor) {

        this.tiffService = tiffService;
        this.documentBuilderService = documentBuilderService;
        this.cpuExecutor = cpuExecutor;
    }
}