package dk.siema.siemaexamproject.bll.api;

import dk.siema.siemaexamproject.be.ActivityLog;
import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.be.Profile;
import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.be.enums.ColorMode;
import dk.siema.siemaexamproject.bll.util.BarcodeReader;
import com.github.f4b6a3.uuid.UuidCreator;

import dk.siema.siemaexamproject.dal.interfaces.IActivityLogDAO;
import dk.siema.siemaexamproject.dal.interfaces.IBoxDAO;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.UUID;

import static dk.siema.siemaexamproject.be.enums.ColorMode.BLACK_WHITE;
import static dk.siema.siemaexamproject.be.enums.ColorMode.GRAYSCALE;


public class DocumentBuilderService {

    private final BarcodeReader barcodeReader = new BarcodeReader();
    private final IBoxDAO boxDAO;
    private final IActivityLogDAO activityLogDAO;

    public DocumentBuilderService(IBoxDAO boxDAO, IActivityLogDAO activityLogDAO) {
        this.boxDAO = boxDAO;
        this.activityLogDAO = activityLogDAO;
    }

    public record PageResult(FileEntity entity, boolean barcode) {}

    public boolean hasBarcode(File file) throws Exception {
        BufferedImage image = ImageIO.read(file);
        if (image == null) return false;
        return barcodeReader.readBarcode(image) != null;
    }

    public PageResult processFile(File file, Profile profile) throws Exception {

        System.out.println("Processing file: " + file.getName());
        System.out.println("Applying profile - Rotation: " + profile.getRotation() +
                ", ColorMode: " + profile.getColorMode());

        BufferedImage image = ImageIO.read(file);
        if (image == null) return null;

        System.out.println("Original image - Type: " + image.getType() +
                ", Size: " + image.getWidth() + "x" + image.getHeight());

        boolean isBarcode = barcodeReader.readBarcode(image) != null;

        // Apply Profile
        if (profile != null) {
            if (profile.getRotation() != 0) {
                image = rotate(image, profile.getRotation());
                System.out.println("Applied rotation: " + profile.getRotation());
            }
            if (profile.getColorMode() != ColorMode.COLOR) {
                image = applyColorMode(image, profile.getColorMode());
                System.out.println("Applied color mode: " + profile.getColorMode());
            }
        }

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

        // Store rotation in the entity
        if (profile != null) {
            entity.setRotation(profile.getRotation());
            entity.setColorMode(profile.getColorMode().toString());
        }

        return new PageResult(entity, isBarcode);
    }

    // ================= ROTATION =================

    private BufferedImage rotate(BufferedImage img, int angle) {

        if (angle == 0) return img;

        double r = Math.toRadians(angle);

        int w = img.getWidth();
        int h = img.getHeight();

        int type = (img.getType() == BufferedImage.TYPE_CUSTOM) ?
                BufferedImage.TYPE_INT_RGB : img.getType();

        BufferedImage result = new BufferedImage(w, h, type);

        Graphics2D g = result.createGraphics();
        //quality improvement that makes the text in the scan stay sharp
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.rotate(r, w / 2.0, h / 2.0);
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return result;
    }

    // ================= COLOR MODES =================

    private BufferedImage applyColorMode(BufferedImage img, ColorMode mode) {

        return switch (mode) {

            case GRAYSCALE -> {
                BufferedImage gray = new BufferedImage(
                        img.getWidth(),
                        img.getHeight(),
                        BufferedImage.TYPE_BYTE_GRAY
                );
                Graphics g = gray.getGraphics();
                g.drawImage(img, 0, 0, null);
                g.dispose();
                yield gray;
            }

            case BLACK_WHITE -> {
                BufferedImage bw = new BufferedImage(
                        img.getWidth(),
                        img.getHeight(),
                        BufferedImage.TYPE_BYTE_BINARY
                );
                Graphics g = bw.getGraphics();
                g.drawImage(img, 0, 0, null);
                g.dispose();
                yield bw;
            }

            default -> img;
        };
    }

    // ================= DELETE FILE =================

    public void deleteStagedFile(UUID referenceId) throws Exception {
        boxDAO.deleteStagedFile(referenceId);
    }

    // ================= IMPORT =================

    public PageResult importFile(File file, Profile profile) throws Exception {
        System.out.println("Importing file: " + file.getName());
        System.out.println("Applying profile - Rotation: " + profile.getRotation() +
                ", ColorMode: " + profile.getColorMode());

        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            System.err.println("Failed to read image: " + file.getAbsolutePath());
            return null;
        }

        System.out.println("Original image - Type: " + image.getType() +
                ", Size: " + image.getWidth() + "x" + image.getHeight());

        // Check for barcode (optional - can be false for imported files)
        boolean isBarcode = barcodeReader.readBarcode(image) != null;

        // Apply Profile settings
        if (profile != null) {
            if (profile.getRotation() != 0) {
                image = rotate(image, profile.getRotation());
                System.out.println("Applied rotation: " + profile.getRotation());
            }
            if (profile.getColorMode() != ColorMode.COLOR) {
                image = applyColorMode(image, profile.getColorMode());
                System.out.println("Applied color mode: " + profile.getColorMode());
            }
        }

        // Prepare for DB
        byte[] fileData;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Determine output format based on file extension or default to TIFF
            String format = getImageFormat(file);
            ImageIO.write(image, format, outputStream);
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

        // Stage the file
        boxDAO.stageFile(entity);

        // Store rotation and color mode in the entity
        if (profile != null) {
            entity.setRotation(profile.getRotation());
            entity.setColorMode(profile.getColorMode().toString());
        }

        return new PageResult(entity, isBarcode);
    }

    private String getImageFormat(File file) {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".png")) return "png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "jpg";
        if (fileName.endsWith(".bmp")) return "bmp";
        return "tiff";
    }


}