package dk.siema.siemaexamproject.bll.api;

import dk.siema.siemaexamproject.dal.api.TiffApiClient;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TiffService {

    private final TiffApiClient apiClient = new TiffApiClient();

    private static final File OUTPUT_DIR = new File("images/all");

    public List<File> getAllTiffs() throws Exception {

        resetFolder();

        // download fresh zip every time (prevents stale cache issues)
        InputStream zipStream = apiClient.fetchAllFiles();

        File zipFile = new File("all_tiffs.zip");

        try (FileOutputStream fos = new FileOutputStream(zipFile)) {
            zipStream.transferTo(fos);
        }

        List<File> files = unzip(zipFile, OUTPUT_DIR);

        //enforce correct numeric order (1.tiff, 2.tiff, ...)
        files.sort(Comparator.comparingInt(this::extractNumber));

        return files;
    }

    private void resetFolder() {
        if (OUTPUT_DIR.exists()) {
            for (File f : Objects.requireNonNull(OUTPUT_DIR.listFiles())) {
                f.delete();
            }
        } else {
            OUTPUT_DIR.mkdirs();
        }
    }

    private List<File> unzip(File zipFile, File outputDir) throws Exception {

        List<File> files = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {

            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {

                File outFile = new File(outputDir, entry.getName());
                outFile.getParentFile().mkdirs();

                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    zis.transferTo(fos);
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