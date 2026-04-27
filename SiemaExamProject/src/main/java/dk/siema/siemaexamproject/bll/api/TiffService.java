package dk.siema.siemaexamproject.bll.api;

import dk.siema.siemaexamproject.dal.api.TiffApiClient;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TiffService {

    private final TiffApiClient apiClient = new TiffApiClient();

    private static final File OUTPUT_DIR = new File("images/all");
    private static final File ZIP_FILE = new File("all_tiffs.zip");

    // STEP 1: simple session cache
    private List<File> cachedFiles = null;

    public List<File> getAllTiffs() throws Exception {

        // STEP 2: return from memory if already loaded
        if (cachedFiles != null && !cachedFiles.isEmpty()) {
            return cachedFiles;
        }

        // STEP 3: if files already exist locally → reuse them (fast path)
        if (OUTPUT_DIR.exists() && OUTPUT_DIR.listFiles() != null && OUTPUT_DIR.listFiles().length > 0) {
            cachedFiles = new ArrayList<>(Arrays.asList(OUTPUT_DIR.listFiles()));
            cachedFiles.sort(Comparator.comparingInt(this::extractNumber));
            return cachedFiles;
        }

        // STEP 4: ensure folder exists
        OUTPUT_DIR.mkdirs();

        // STEP 5: download ZIP only if not already downloaded
        if (!ZIP_FILE.exists()) {
            try (InputStream in = apiClient.fetchAllFiles();
                 FileOutputStream fos = new FileOutputStream(ZIP_FILE)) {
                in.transferTo(fos);
            }
        }

        // STEP 6: unzip only if needed
        cachedFiles = unzip(ZIP_FILE, OUTPUT_DIR);

        // STEP 7: sort files correctly
        cachedFiles.sort(Comparator.comparingInt(this::extractNumber));

        return cachedFiles;
    }

    private List<File> unzip(File zipFile, File outputDir) throws Exception {

        List<File> files = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {

            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {

                File outFile = new File(outputDir, entry.getName());
                outFile.getParentFile().mkdirs();

                try (BufferedOutputStream bos =
                             new BufferedOutputStream(new FileOutputStream(outFile))) {
                    zis.transferTo(bos);
                }

                files.add(outFile);
                zis.closeEntry();
            }
        }

        return files;
    }

    private int extractNumber(File file) {
        try {
            String name = file.getName().replaceAll("\\D+", "");
            return Integer.parseInt(name);
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }
}