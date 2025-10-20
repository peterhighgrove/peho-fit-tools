package se.peho.fittools.fitZipRenamer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.garmin.fit.Decode;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.FileIdMesgListener;
import com.garmin.fit.MesgBroadcaster;
import com.garmin.fit.SessionMesg;
import com.garmin.fit.SessionMesgListener;

public class Main_copy {

    public static void main(String[] args) throws Exception {

        // Default folder = current directory
        String folderPath = ".";
        String filter = null;

        if (args.length > 0) {
            filter = args[0].trim();
        }
        if (args.length > 1) {
            folderPath = args[1].trim();
        }

        java.io.File folder = new java.io.File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Folder not found: " + folderPath);
            return;
        }

        final String filterCopy = filter;

        java.io.File[] files = folder.listFiles((dir, name) -> {
            boolean isZip = name.toLowerCase().endsWith(".zip");
            if (!isZip) return false;

            if (filterCopy == null || filterCopy.isEmpty()) {
                return name.matches("\\d+\\.zip");
            } else {
                return name.toLowerCase().contains(filterCopy.toLowerCase());
            }
        });

        if (files == null || files.length == 0) {
            System.out.println("No matching ZIP files found.");
            return;
        }

        for (java.io.File zipFile : files) {
            System.out.println("Processing: " + zipFile.getName());
            java.io.File fitFile = extractFitFile(zipFile);
            if (fitFile == null) {
                System.out.println("  No .fit file found inside.");
                continue;
            }

            ActivityInfo info = readFitInfo(fitFile);
            if (info == null) {
                System.out.println("  Could not read FIT info.");
                fitFile.delete();
                continue;
            }

            String newName = String.format(
                "%s-%s-%sh-%.2fkm-garminzip.zip",
                info.dateTime,
                info.profile,
                info.timer,
                info.distance
            ).replace(" ", "-").replace(",", ".");

            java.nio.file.Path newPath = zipFile.toPath().resolveSibling(newName);
            java.nio.file.Files.move(zipFile.toPath(), newPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println("  Renamed to: " + newName);

            fitFile.delete();
        }

        System.out.println("Done.");
    }

    private static File extractFitFile(File zipFile) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().toLowerCase().endsWith(".fit")) {
                    File fitFile = new File(entry.getName());
                    try (FileOutputStream fos = new FileOutputStream(fitFile)) {
                        zis.transferTo(fos);
                    }
                    return fitFile;
                }
            }
        }
        return null;
    }

    private static ActivityInfo readFitInfo(File fitFile) {
        try {
            Decode decode = new Decode();
            MesgBroadcaster broadcaster = new MesgBroadcaster(decode);
            ActivityListener listener = new ActivityListener();
            broadcaster.addListener((FileIdMesgListener) listener);
            broadcaster.addListener((SessionMesgListener) listener);

            try (InputStream in = new FileInputStream(fitFile)) {
                if (!decode.checkFileIntegrity((InputStream) new FileInputStream(fitFile))) {
                    System.out.println("  FIT file failed integrity check.");
                    return null;
                }
                decode.read(in, broadcaster);
            }

            return listener.info;
        } catch (Exception e) {
            System.out.println("  Error reading FIT: " + e.getMessage());
            return null;
        }
    }

    private static class ActivityInfo {
        String dateTime;
        String profile;
        String timer;
        double distance;
    }

    private static class ActivityListener implements FileIdMesgListener, SessionMesgListener {
        ActivityInfo info = new ActivityInfo();
        Date startTime;

        @Override
        public void onMesg(FileIdMesg mesg) {
            if (mesg.getTimeCreated() != null) {
                long timestamp = mesg.getTimeCreated().getTimestamp();
                // Garmin epoch starts 1989-12-31
                long epoch = 631065600L + timestamp;
                startTime = new Date(epoch * 1000);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                info.dateTime = sdf.format(startTime);
            }
        }

        @Override
        public void onMesg(SessionMesg mesg) {
            if (mesg.getSportProfileName() != null)
                info.profile = mesg.getSportProfileName().toString().toLowerCase();
            else
                if (mesg.getSport() != null)
                    info.profile = mesg.getSport().toString().toLowerCase();
                else
                    info.profile = "unknown";

            Float totalTimer = mesg.getTotalTimerTime();
            Float totalDist = mesg.getTotalDistance();

            long seconds = (totalTimer != null) ? totalTimer.longValue() : 0;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            minutes %= 60;

            info.timer = String.format("%02d.%02d", hours, minutes);
            info.distance = (totalDist != null ? totalDist / 1000.0 : 0.0);
        }
    }
}
