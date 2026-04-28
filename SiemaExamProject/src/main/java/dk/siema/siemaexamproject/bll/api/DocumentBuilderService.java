package dk.siema.siemaexamproject.bll.api;

import dk.siema.siemaexamproject.be.Document;
import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.bll.exceptions.APIException;
import dk.siema.siemaexamproject.bll.util.BarcodeReader;
import com.github.f4b6a3.uuid.UuidCreator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DocumentBuilderService {

    private final BarcodeReader barcodeReader = new BarcodeReader();

    // STEP 1: Result of parallel processing (one file → one PageResult)
    public record PageResult(FileEntity entity, boolean barcode) {}

    // STEP 2: Process a single file (executed in parallel threads)
    public PageResult processFile(File file) throws Exception {

        try {
            BufferedImage image = ImageIO.read(file);
            if (image == null) return null;

            boolean isBarcode = barcodeReader.readBarcode(image) != null;

             UUID referenceId = UuidCreator.getTimeOrderedEpoch();
            FileEntity entity = new FileEntity(
                    referenceId,
                    0,
                    file.getAbsolutePath(),
                    0,
                    isBarcode
            );

            return new PageResult(entity, isBarcode);

        } catch (Exception e) {
            e.printStackTrace();
            throw  new APIException("File has not been processed");
        }
    }

    // STEP 3: Build final documents (runs after all parallel processing is finished)
    public List<Document> buildDocuments(List<PageResult> pages) {

        List<Document> documents = new ArrayList<>();
        Document current = null;
        int docId = 1;

        for (PageResult page : pages) {

            if (page == null) continue;

            if (page.barcode()) {
                current = new Document();
                current.setId(docId++);
                documents.add(current);
            }

            if (current == null) {
                current = new Document();
                current.setId(docId++);
                documents.add(current);
            }

            current.addPage(page.entity());
        }

        return documents;
    }
}