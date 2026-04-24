package dk.siema.siemaexamproject.bll.api;

import dk.siema.siemaexamproject.be.Document;
import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.bll.util.BarcodeReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DocumentBuilderService {

    private final BarcodeReader barcodeReader = new BarcodeReader();

    public List<Document> buildDocuments(List<File> tiffFiles) {

        List<Document> documents = new ArrayList<>();
        Document currentDocument = null;

        int docId = 1;

        for (File file : tiffFiles) {

            try {
                BufferedImage image = ImageIO.read(file);
                if (image == null) continue;

                boolean isBarcode = barcodeReader.readBarcode(image) != null;

                // barcode = start new document
                if (isBarcode) {
                    currentDocument = new Document();
                    currentDocument.setId(docId++);
                    documents.add(currentDocument);
                }

                // safety fallback
                if (currentDocument == null) {
                    currentDocument = new Document();
                    currentDocument.setId(docId++);
                    documents.add(currentDocument);
                }

                FileEntity page = new FileEntity(
                        file.hashCode(),
                        0,
                        currentDocument.getPages().size(),
                        file.getAbsolutePath(),
                        0,
                        isBarcode
                );

                currentDocument.addPage(page);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return documents;
    }
}