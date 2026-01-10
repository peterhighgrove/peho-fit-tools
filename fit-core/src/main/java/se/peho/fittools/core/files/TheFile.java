package se.peho.fittools.core.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TheFile {
    public static void rename(File theFile, String baseName, String suffix, String ext) throws IOException {
        String newFilename = baseName + suffix + ext;
        Path newFilePath = theFile.toPath().resolveSibling(newFilename);
        Files.move(theFile.toPath(), newFilePath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("  Renamed: " + theFile.getName());
        System.out.println("       to: " + newFilename);
        System.out.println("--------------------------------");
    }
    public static File extractFit(File zipFile) {
        File outputFit = null;

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().toLowerCase().endsWith(".fit")) {
                    Path outputPath = new File(zipFile.getParentFile(), new File(entry.getName()).getName()).toPath();
                    try (OutputStream os = Files.newOutputStream(outputPath)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            os.write(buffer, 0, len);
                        }
                    }
                    outputFit = outputPath.toFile();
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("  Error extracting FIT: " + e.getMessage());
        }

        return outputFit;
    }

}
