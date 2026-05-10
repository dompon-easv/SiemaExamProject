package dk.siema.siemaexamproject.bll.service;

import dk.siema.siemaexamproject.be.Box;
import dk.siema.siemaexamproject.be.Document;
import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.dal.interfaces.IBoxDAO;
import javafx.concurrent.Task;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ExportService {

    private final IBoxDAO boxDAO;

    public ExportService(IBoxDAO boxDAO) {
        this.boxDAO = boxDAO;
    }

    public void processExport (Box box, File targetDir, boolean isMultiPage, Task<?> task) throws IOException {


        //Create the physical folder: profileName_boxId
        File mainFolder =new File(targetDir, box.getExportName());
        if (!mainFolder.exists()) mainFolder.mkdirs();

        for (Document doc : box.getDocuments()) {
            //subfolder for docs
            File docFolder = new File(mainFolder, "Document" + doc.getId());
            docFolder.mkdirs();

            for(FileEntity file : doc.getFiles()) {
                File sourceFile = new File(file.getFilePath());

                if (!sourceFile.exists()) {
                    System.out.println("Missing source file: " + file.getFilePath());
                    continue;
                }



                //1. Read the temporary file
                BufferedImage image = ImageIO.read(sourceFile);

                //2. apply the rotation in the Model
                BufferedImage rotatedImg = rotateImage(image, file.getRotation());

                //3. save the permanent destination
                File finalFile = new File(docFolder, "File_" + file.getSortOrder() + ".tiff");
                ImageIO.write(rotatedImg, "tiff", finalFile);

                //4. prepare bytes for the Database table
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(image, "tiff", baos);
                file.setFileData(baos.toByteArray());

            }
                image.flush();
                rotatedImg.flush();
            }
        }
            boxDAO.saveBox(box);

            // 1. DATABASE: Save the Document and File records
            // documentRepo.create(doc);
            // fileRepo.create(file); (which includes the rotation number!)

            // 2. FILE SYSTEM: Generate the final TIFFs
            // Here you will read the original local files, apply the rotation if needed,
            // and write them to their final destination as a Single or Multi-page TIFF.

            for(Document doc: box.getDocuments())
                deleteTemporaryFiles(doc);


        }
    private BufferedImage rotateImage(BufferedImage img, int degree) {
        if (degree == 0 || degree % 360 == 0) return img;

        double rads = Math.toRadians(degree);
        int w = img.getWidth();
        int h = img.getHeight();

        int newW = (degree % 180 != 0) ? h : w;
        int newH = (degree % 180 != 0) ? w : h;


        int type = (img.getType() == BufferedImage.TYPE_CUSTOM) ?
                BufferedImage.TYPE_INT_RGB : img.getType();


        BufferedImage rotated = new BufferedImage(newW, newH, type);


        Graphics2D g2d = rotated.createGraphics();

        // High-quality rendering hints (optional but recommended for TIFFs)
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2d.translate((newW - w) / 2.0, (newH - h) / 2.0);
        g2d.rotate(rads, w / 2.0, h / 2.0);
        g2d.drawRenderedImage(img, null);
        g2d.dispose();

        return rotated;
    }


    private void generateFinalTiffs(Document doc){
        //Tiff generation logic here
        System.out.println("Generating temporary files...");
    }

    private void deleteTemporaryFiles(Document doc){
        for (FileEntity fileEntity: doc.getFiles()){
            File tempFile = new File(fileEntity.getFilePath());
            if(tempFile.exists()){
                boolean deleted = tempFile.delete();
                if(deleted){
                    System.out.println("Deleted temporary file: " + tempFile.getAbsolutePath());
                }
            }
        }
    }}
