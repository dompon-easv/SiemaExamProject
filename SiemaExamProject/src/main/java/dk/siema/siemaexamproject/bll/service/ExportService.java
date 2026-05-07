package dk.siema.siemaexamproject.bll.service;

import dk.siema.siemaexamproject.be.Document;
import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.dal.interfaces.IBoxDAO;

import java.io.File;
import java.util.List;

public class ExportService {

    private final IBoxDAO boxDAO;

    public ExportService(IBoxDAO boxDAO) {
        this.boxDAO = boxDAO;
    }

    public void processExport (List<Document> documents) {
        for (Document doc : documents) {

            // 1. DATABASE: Save the Document and File records
            // documentRepo.create(doc);
            // fileRepo.create(file); (which includes the rotation number!)

            // 2. FILE SYSTEM: Generate the final TIFFs
            // Here you will read the original local files, apply the rotation if needed,
            // and write them to their final destination as a Single or Multi-page TIFF.

            generateFinalTiffs(doc);

            deleteTemporaryFiles(doc);
            }
        }

    private void generateFinalTiffs(Document doc){
        //Tiff generation logic here
        System.out.println("Generating temporary files...");
    }

    private void deleteTemporaryFiles(Document doc){
        for (FileEntity fileEntity: doc.getPages()){
            File tempFile = new File(fileEntity.getFilePath());
            if(tempFile.exists()){
                boolean deleted = tempFile.delete();
                if(deleted){
                    System.out.println("Deleted temporary file: " + tempFile.getAbsolutePath());
                }
            }
        }
    }
}
