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

                String barcode = barcodeReader.readBarcode(image);

                boolean isBarcode = barcode != null;

                // 🔥 NEW RULE: barcode OR first page starts new document
                if (isBarcode || currentDocument == null) {

                    // close previous document
                    if (currentDocument != null && !currentDocument.getPages().isEmpty()) {
                        documents.add(currentDocument);
                    }

                    currentDocument = new Document();
                    currentDocument.setId(docId++);
                }

                // add page to current document
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

        // add last document
        if (currentDocument != null && !currentDocument.getPages().isEmpty()) {
            documents.add(currentDocument);
        }

        return documents;
    }
}