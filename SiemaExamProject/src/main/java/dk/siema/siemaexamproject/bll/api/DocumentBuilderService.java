package dk.siema.siemaexamproject.bll.api;

import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.be.Profile;
import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.be.enums.ColorMode;
import dk.siema.siemaexamproject.bll.util.BarcodeReader;
import com.github.f4b6a3.uuid.UuidCreator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.File;
import java.util.UUID;

import static dk.siema.siemaexamproject.be.enums.ColorMode.BLACK_WHITE;
import static dk.siema.siemaexamproject.be.enums.ColorMode.GRAYSCALE;


public class DocumentBuilderService {

    private final BarcodeReader barcodeReader = new BarcodeReader();

    public record PageResult(FileEntity entity, boolean barcode) {}

    public PageResult processFile(File file, Profile profile) throws Exception {

        System.out.println("Processing file: " + file.getName());
        System.out.println("Applying profile - Rotation: " + profile.getRotation() +
                ", ColorMode: " + profile.getColorMode());

        BufferedImage image = ImageIO.read(file);
        if (image == null) return null;

        System.out.println("Original image - Type: " + image.getType() +
                ", Size: " + image.getWidth() + "x" + image.getHeight());

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

        boolean isBarcode = barcodeReader.readBarcode(image) != null;

        UUID referenceId = UuidCreator.getTimeOrderedEpoch();

        FileEntity entity = new FileEntity(
                referenceId,
                0,
                file.getAbsolutePath(),
                0,
                isBarcode
        );

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

        BufferedImage result = new BufferedImage(w, h, img.getType());

        Graphics2D g = result.createGraphics();
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
}