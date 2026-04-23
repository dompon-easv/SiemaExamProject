package dk.siema.siemaexamproject.app;

import dk.siema.siemaexamproject.bll.api.DocumentBuilderService;
import dk.siema.siemaexamproject.bll.api.TiffService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApplicationServices {

    private final ExecutorService executorService;
    private final TiffService tiffService;
    private final DocumentBuilderService documentBuilderService;

    public ApplicationServices() {
        this.executorService = Executors.newFixedThreadPool(2);
        this.tiffService = new TiffService();
        this.documentBuilderService = new DocumentBuilderService();
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public TiffService getTiffService() {
        return tiffService;
    }

    public DocumentBuilderService getDocumentBuilderService() {
        return documentBuilderService;
    }

    public void shutdown() {
        executorService.shutdown();
    }
}