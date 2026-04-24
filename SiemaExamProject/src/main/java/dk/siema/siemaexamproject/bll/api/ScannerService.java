package dk.siema.siemaexamproject.bll.api;

import dk.siema.siemaexamproject.be.Document;

import java.io.File;
import java.util.List;

public class ScannerService {

    private final TiffService tiffService;
    private final DocumentBuilderService documentBuilderService;

    public ScannerService(TiffService tiffService,
                          DocumentBuilderService documentBuilderService) {
        this.tiffService = tiffService;
        this.documentBuilderService = documentBuilderService;
    }

    public List<Document> scan() throws Exception {

        List<File> tiffFiles = tiffService.getAllTiffs();

        return documentBuilderService.buildDocuments(tiffFiles);
    }
}