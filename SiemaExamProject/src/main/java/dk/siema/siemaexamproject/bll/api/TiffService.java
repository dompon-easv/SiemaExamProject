package dk.siema.siemaexamproject.bll.api;

import dk.siema.siemaexamproject.dal.api.TiffApiClient;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TiffService {

    private final TiffApiClient apiClient = new TiffApiClient();

    public List<File> getAllTiffs() throws Exception {

        File folder = new File("images/all");

        // ✅ If already downloaded → skip API
        if (folder.exists() && folder.listFiles() != null && folder.listFiles().length > 0) {
            return List.of(folder.listFiles());
        }

        InputStream zipStream = apiClient.fetchAllFiles();

        File zipFile = new File("all_tiffs.zip");

        try (FileOutputStream fos = new FileOutputStream(zipFile)) {
            zipStream.transferTo(fos);
        }

        return unzip(zipFile, "images/all");
    }

    private List<File> unzip(File zipFile, String outputDir) throws Exception {

        List<File> files = new ArrayList<>();

        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdirs();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {

            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {

                File outFile = new File(dir, entry.getName());

                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    zis.transferTo(fos);
                }

                files.add(outFile);
                zis.closeEntry();
            }
        }

        return files;
    }
}