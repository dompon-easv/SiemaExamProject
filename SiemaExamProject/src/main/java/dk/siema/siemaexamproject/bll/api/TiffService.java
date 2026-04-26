package dk.siema.siemaexamproject.bll.api;

import dk.siema.siemaexamproject.dal.api.TiffApiClient;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TiffService {

    private final TiffApiClient apiClient = new TiffApiClient();

    // Folder where TIFF files are stored locally after unzip
    private static final File OUTPUT_DIR = new File("images/all");

    // Simple in-memory cache to avoid re-downloading and re-unzipping
    private List<File> cachedFiles = null;

    public List<File> getAllTiffs() throws Exception {

        // If files are already loaded in this session, reuse them
        if (cachedFiles != null) {
            return cachedFiles;
        }

        // Ensure we start with a clean folder (no stale files)
        resetFolder();

        // Download ZIP file from API
        InputStream zipStream = apiClient.fetchAllFiles();

        File zipFile = new File("all_tiffs.zip");

        // Save ZIP file locally
        try (FileOutputStream fos = new FileOutputStream(zipFile)) {
            zipStream.transferTo(fos);
        }

        // Unzip files into OUTPUT_DIR
        cachedFiles = unzip(zipFile, OUTPUT_DIR);

        // Sort files so processing order is correct (1, 2, 3, ...)
        cachedFiles.sort(Comparator.comparingInt(this::extractNumber));

        return cachedFiles;
    }

    // Deletes old files before a new scan
    private void resetFolder() {
        if (OUTPUT_DIR.exists()) {
            for (File f : Objects.requireNonNull(OUTPUT_DIR.listFiles())) {
                f.delete();
            }
        } else {
            OUTPUT_DIR.mkdirs();
        }
    }

    // Extracts ZIP archive into a folder
    private List<File> unzip(File zipFile, File outputDir) throws Exception {

        List<File> files = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {

            ZipEntry entry;

            // Loop through all files inside ZIP
            while ((entry = zis.getNextEntry()) != null) {

                File outFile = new File(outputDir, entry.getName());
                outFile.getParentFile().mkdirs();

                // Write file to disk
                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    zis.transferTo(fos);
                }

                files.add(outFile);
                zis.closeEntry();
            }
        }

        return files;
    }

    // Extract numeric part from filename for correct ordering
    private int extractNumber(File file) {
        try {
            String name = file.getName().replaceAll("\\D+", "");
            return Integer.parseInt(name);
        } catch (Exception e) {
            return Integer.MAX_VALUE; // fallback if parsing fails
        }
    }
}