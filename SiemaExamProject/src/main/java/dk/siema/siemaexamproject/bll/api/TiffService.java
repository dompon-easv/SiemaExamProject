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

    // STEP 1: session cache (in-memory)
    private List<File> cachedFiles = null;

    public List<File> getAllTiffs() throws Exception {

        // STEP 2: return from memory cache
        if (cachedFiles != null && !cachedFiles.isEmpty()) {
            return cachedFiles;
        }

        OUTPUT_DIR.mkdirs();

        // STEP 3: fast path → reuse already unzipped files
        File[] existing = OUTPUT_DIR.listFiles((dir, name) -> name.toLowerCase().endsWith(".tiff"));

        if (existing != null && existing.length > 0) {
            cachedFiles = new ArrayList<>(Arrays.asList(existing));
            cachedFiles.sort(Comparator.comparingInt(this::extractNumber));
            return cachedFiles;
        }

        // STEP 4: download ZIP only if missing
        if (!ZIP_FILE.exists()) {
            try (InputStream in = apiClient.fetchAllFiles();
                 FileOutputStream fos = new FileOutputStream(ZIP_FILE)) {
                in.transferTo(fos);
            }
        }

        // STEP 5: unzip
        cachedFiles = unzip(ZIP_FILE, OUTPUT_DIR);

        // STEP 6: sort correctly
        cachedFiles.sort(Comparator.comparingInt(this::extractNumber));

        return cachedFiles;
    }

    // unzip ZIP file into folder
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

    // extract numeric ordering from filename
    private int extractNumber(File file) {
        try {
            String name = file.getName().replaceAll("\\D+", "");
            return Integer.parseInt(name);
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }
}