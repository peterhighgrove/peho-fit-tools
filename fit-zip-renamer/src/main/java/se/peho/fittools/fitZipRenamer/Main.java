package se.peho.fittools.fitZipRenamer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.garmin.fit.ActivityMesg;
import com.garmin.fit.Decode;
import com.garmin.fit.MesgBroadcaster;
import com.garmin.fit.SessionMesg;

import se.peho.fittools.core.FitDateTime;

public class Main {

    public static void main(String[] args) throws Exception {

        // --- Parse CLI arguments ---
        String mode = "zip";   // default
        String folderPath = ".";
        String filter = null;

        if (args.length > 0) {
            mode = args[0].trim().toLowerCase();
        }
        if (args.length > 1) {
            folderPath = args[1].trim();
        }
        if (args.length > 2) {
            filter = args[2].trim();
        }

        java.io.File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Folder not found: " + folderPath);
            return;
        }

        switch (mode) {
            case "zip":
                processZipFiles(folder, filter, false);
                break;
            case "zipfit":
                processZipFiles(folder, filter, true);
                break;
            case "fit":
                processFitFiles(folder, filter);
                break;
            default:
                System.out.println("Invalid mode. Use one of: zip, zipfit, fit (as first cli args)");
                return;
        }

        System.out.println("Done.");
    }

    // ----------------------------------------------------------------
    // ZIP FILES
    // ----------------------------------------------------------------
    private static void processZipFiles(java.io.File folder, String filter, boolean keepFit) throws Exception {
        final String filterCopy = filter;
        java.io.File[] files = folder.listFiles((dir, name) -> {
            if (!name.toLowerCase().endsWith(".zip")) return false;
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

        for (File zipFile : files) {
            System.out.println("Processing: " + zipFile.getName());

            File fitFile = extractFitFile(zipFile);
            if (fitFile == null) {
                System.out.println("  No .fit file found inside.");
                continue;
            }

            ActivityInfo info = readFitInfo(fitFile);
            if (info == null) {
                System.out.println("  Could not read FIT info.");
                if (!keepFit) fitFile.delete();
                continue;
            }

            String baseName = String.format(
                "%s-%s-%sh-%skm",
                info.dateTime,
                info.profile,
                info.timer,
                info.distance
            ).replace(" ", "-").replace(",", ".");

            // --- Rename ZIP ---
            String newZipName = baseName + "-garminzip.zip";
            Path newZipPath = zipFile.toPath().resolveSibling(newZipName);
            Files.move(zipFile.toPath(), newZipPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("  Renamed ZIP to: " + newZipName);

            // --- Rename FIT (optional) ---
            if (keepFit) {
                String newFitName = baseName + "-garminfit.fit";
                Path newFitPath = fitFile.toPath().resolveSibling(newFitName);
                Files.move(fitFile.toPath(), newFitPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("  Renamed FIT to: " + newFitName);
            } else {
                fitFile.delete();
            }
        }
    }

    // ----------------------------------------------------------------
    // FIT FILES
    // ----------------------------------------------------------------
    private static void processFitFiles(java.io.File folder, String filter) throws Exception {
        
        final String filterCopy = filter;

        File[] files = folder.listFiles((dir, name) -> {
            if (!name.toLowerCase().endsWith(".fit")) return false;

            if (filterCopy == null || filterCopy.isEmpty()) {
                // No filter argument → only names that are numeric (like 20536941316.fit)
                return name.matches("\\d+\\.fit");
            } else {
                // If filter argument provided → match substring (case-insensitive)
                return name.toLowerCase().contains(filterCopy.toLowerCase());
            }
        });

        if (files == null || files.length == 0) {
            System.out.println("No matching FIT files found.");
            return;
        }

        for (File fitFile : files) {
            System.out.println("Processing: " + fitFile.getName());

            ActivityInfo info = readFitInfo(fitFile);
            if (info == null) {
                System.out.println("  Could not read FIT info.");
                continue;
            }

            String newName = String.format(
                "%s-%s-%skm-%smin-garminfit.fit",
                info.dateTime,
                info.profile,
                info.distance,
                info.timer
            )
            .replace(" ", "-")
            .replace(",", ".")
            .replace("elliptical-(bike)", "elliptical")
            .replace("skierg-(bike)", "skierg");

            // replace spaces and forbidden chars with safe alternatives
            Path newPath = fitFile.toPath().resolveSibling(sanitizeFilename(newName));
            Files.move(fitFile.toPath(), newPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("  Renamed to: " + sanitizeFilename(newName));
        }
    }

    private static String sanitizeFilename(String name) {
        return name.replaceAll("[\\\\/:*?\"<>|!]", "-");
    }

    // ----------------------------------------------------------------
    // EXTRACT .FIT FILE FROM .ZIP
    // ----------------------------------------------------------------
    private static File extractFitFile(File zipFile) {
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

    // ----------------------------------------------------------------
    // READ GARMIN FIT FILE INFO
    // ----------------------------------------------------------------
    private static ActivityInfo readFitInfo(File fitFile) {
        final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        try {
            Decode decode = new Decode();
            MesgBroadcaster broadcaster = new MesgBroadcaster(decode);

            final ActivityMesg[] activity = new ActivityMesg[1];
            final SessionMesg[] session = new SessionMesg[1];

            // capture ActivityMesg (for localTimestamp) and SessionMesg (fallback + sport/timer/distance)
            broadcaster.addListener((ActivityMesg m) -> activity[0] = m);
            broadcaster.addListener((SessionMesg m) -> session[0] = m);

            // check integrity (needs its own stream)
            try (FileInputStream in = new FileInputStream(fitFile)) {
                if (!decode.checkFileIntegrity(in)) {
                    System.out.println("  FIT file integrity check failed for " + fitFile.getName());
                    return null;
                }
            }

            // read actual messages
            try (FileInputStream in = new FileInputStream(fitFile)) {
                decode.read(in, broadcaster, broadcaster);
            }

            // prefer ActivityMesg.localTimestamp if present
            String dateTime = null;
            if (activity[0] != null) {
                // ActivityMesg.getLocalTimestamp() typically returns Long (Garmin epoch seconds)
                try {
                    Long localTs = activity[0].getLocalTimestamp();
                    if (localTs != null && localTs > 0) {
                        long unixMs = (localTs + 631065600L) * 1000L; // Garmin epoch -> unix ms
                        dateTime = FitDateTime.toString(localTs);
                        //dateTime = DATE_FORMAT.format(new Date(unixMs));
                    }
                } catch (Exception e) {
                    // safest: ignore and fallback below
                }
            }

            // fallback to SessionMesg.startTime (UTC -> Date) if no local timestamp found
            if (dateTime == null && session[0] != null && session[0].getStartTime() != null) {
                dateTime = DATE_FORMAT.format(session[0].getStartTime().getDate());
            }

            if (session[0] == null) {
                // no session message found — can't get sport/timer/distance reliably
                System.out.println("  No SessionMesg found in FIT: " + fitFile.getName());
                return null;
            }

            SessionMesg mesg = session[0];

            // profile: prefer sportProfileName, fallback to sport enum, else "unknown"
            String profile;
            if (mesg.getSportProfileName() != null) {
                profile = mesg.getSportProfileName().toLowerCase();
            } else if (mesg.getSport() != null) {
                profile = mesg.getSport().toString().toLowerCase();
                if (mesg.getSubSport() != null) {
                    profile += "-" + mesg.getSubSport().toString().toLowerCase();
                }
            } else {
                profile = "unknown";
            }
            // sanitize profile (replace spaces)
            profile = profile.replaceAll("\\s+", "-");

            // --- Distance and Timer formatting ---

            Float totalTimer = mesg.getTotalTimerTime(); // seconds
            Float totalDist = mesg.getTotalDistance();   // meters

            // --- Timer as m.ss ---
            long totalSeconds = (totalTimer != null) ? Math.round(totalTimer) : 0L;
            long totalMinutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;
            String timer = String.format("%d.%02d", totalMinutes, seconds);

            // --- Distance as km, trimmed, no trailing zeros or dot ---
            double distanceKm = (totalDist != null) ? (totalDist / 1000.0) : 0.0;
            String distanceStr = String.format("%.2f", distanceKm)
                .replaceAll("0+$", "")   // remove trailing zeros
                .replaceAll("\\.$", ""); // remove trailing dot if nothing after it

            // --- If distance is exactly 0, ensure it's "0" (no decimal) ---
            if (distanceStr.isEmpty()) {
                distanceStr = "0";
            }

            // --- Assign to info object ---

            ActivityInfo info = new ActivityInfo();
            info.dateTime = (dateTime != null) ? dateTime : DATE_FORMAT.format(new Date()); // best-effort
            info.profile = profile;
            info.distance = distanceStr;
            info.timer = timer;

            return info;

        } catch (Exception e) {
            System.out.println("  Error reading FIT: " + e.getMessage());
            return null;
        }
    }


    // ----------------------------------------------------------------
    // HELPER CLASS
    // ----------------------------------------------------------------
    public static class ActivityInfo {
        public String dateTime;
        public String profile;
        public String timer;
        public String distance;
    }
}
