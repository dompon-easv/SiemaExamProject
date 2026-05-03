package dk.siema.siemaexamproject.bll.api;

import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.bll.util.BarcodeReader;
import com.github.f4b6a3.uuid.UuidCreator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.File;
import java.util.UUID;


public class DocumentBuilderService {

    private final BarcodeReader barcodeReader = new BarcodeReader();

    public record PageResult(FileEntity entity, boolean barcode) {}

    public PageResult processFile(File file) throws Exception {

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
    }
}