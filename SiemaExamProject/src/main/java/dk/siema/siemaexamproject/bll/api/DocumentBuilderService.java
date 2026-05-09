package dk.siema.siemaexamproject.bll.api;

import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.bll.util.BarcodeReader;
import com.github.f4b6a3.uuid.UuidCreator;

import dk.siema.siemaexamproject.dal.interfaces.IBoxDAO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.UUID;


public class DocumentBuilderService {

    private final BarcodeReader barcodeReader = new BarcodeReader();
    private final IBoxDAO boxDAO;

    public DocumentBuilderService(IBoxDAO boxDAO) {
        this.boxDAO = boxDAO;

    }

    public record PageResult(FileEntity entity, boolean barcode) {}

    public PageResult processFile(File file) throws Exception {

        BufferedImage image = ImageIO.read(file);
        if (image == null) return null;

        boolean isBarcode = barcodeReader.readBarcode(image) != null;

        //PREPARE FOR DB
        byte[] fileData;
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "tiff", outputStream);
            fileData = outputStream.toByteArray();
        }

        image.flush();

        UUID referenceId = UuidCreator.getTimeOrderedEpoch();

        FileEntity entity = new FileEntity(
                referenceId,
                0,
                file.getAbsolutePath(),
                0,
                isBarcode
        );
        entity.setFileData(fileData);

        boxDAO.stageFile(entity);

        return new PageResult(entity, isBarcode);
    }
}