package se.peho.fittools.fitZipRenamer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.garmin.fit.ActivityMesg;
import com.garmin.fit.ActivityMesgListener;
import com.garmin.fit.Decode;
import com.garmin.fit.MesgBroadcaster;
import com.garmin.fit.SessionMesg;
import com.garmin.fit.SessionMesgListener;
import com.garmin.fit.DeviceInfoMesg;
import com.garmin.fit.DeviceInfoMesgListener;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.FileIdMesgListener;
import com.garmin.fit.GarminProduct;
import com.garmin.fit.Manufacturer;
import com.garmin.fit.Mesg;
import com.garmin.fit.WorkoutMesg;
import com.garmin.fit.WorkoutMesgListener;

import se.peho.fittools.core.FitDateTime;
import se.peho.fittools.core.strings.*;

public class Main {

    private List<Mesg> deviceInfoMesg = new ArrayList<>();


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

            // --- Extract .fit file from .zip ---
            File fitFile = extractFitFile(zipFile);
            if (fitFile == null) {
                System.out.println("  No .fit file found inside.");
                continue;
            }

            // --- Read .fit file info ---
            ActivityInfo info = readFitInfo(fitFile);
            if (info == null) {
                System.out.println("  Could not read FIT info.");
                if (!keepFit) fitFile.delete();
                continue;
            }

            String baseName = formatBaseName(info);

            // --- Rename ZIP ---
            String newZipName = addSuffixZip(baseName);
            Path newZipPath = zipFile.toPath().resolveSibling(newZipName);
            Files.move(zipFile.toPath(), newZipPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("  Renamed ZIP to: " + newZipName);
            System.out.println("--------------------------------");

            // --- Rename FIT (optional) ---
            if (keepFit) {
                String newFitName = addSuffixFit(baseName);
                Path newFitPath = fitFile.toPath().resolveSibling(newFitName);
                Files.move(fitFile.toPath(), newFitPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("  Renamed FIT to: " + newFitName);
                System.out.println("--------------------------------");
            } else {
                fitFile.delete();
            }
        }
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

            // --- Read .fit file info ---
            ActivityInfo info = readFitInfo(fitFile);
            if (info == null) {
                System.out.println("  Could not read FIT info.");
                continue;
            }

            String baseName = formatBaseName(info);
            System.out.println("  Formatted base name: " + baseName);

            // replace spaces and forbidden chars with safe alternatives
            String newName = new SanitizedFilename(addSuffixFit(baseName)).getName();

            Path newPath = fitFile.toPath().resolveSibling(newName);
            Files.move(fitFile.toPath(), newPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("  Renamed to: " + newName);
            System.out.println("--------------------------------");
        }
    }

    // ----------------------------------------------------------------
    // FORMAT BASE NAME
    // ----------------------------------------------------------------
    private static String formatBaseName (ActivityInfo info) {
        return String.format(
            "%s%s%s%s%s%s"
            ,info.dateTime
            ,info.profile != null && !info.profile.isEmpty() ? "-" + info.profile : ""
            ,info.workoutName != null && !info.workoutName.isEmpty() ? "-" + info.workoutName : ""
            ,info.timer != null && !info.timer.isEmpty() ? "-" + info.timer : ""
            ,info.distance != null && !info.distance.isEmpty() ? "-" + info.distance + "" : ""
            ,info.product != null && !info.product.isEmpty() ? "-" + info.product : ""
        );
    }

    // ----------------------------------------------------------------
    // FORMAT FIT FILENAME
    // ----------------------------------------------------------------
    private static String addSuffixFit(String baseName) {
        return baseName + "-renamed.fit";
    }
    
    // ----------------------------------------------------------------
    // FORMAT ZIP FILENAME
    // ----------------------------------------------------------------
    private static String addSuffixZip(String baseName) {
        return baseName + "-renamed.zip";
    }
    
    // ----------------------------------------------------------------
    // SANITIZE FILENAME
    // ----------------------------------------------------------------
    private static String sanitizeFilename(String name) {
        return name
            .replace("/", "_")
            .replace(":", ".")
            .replaceAll("[\\\\/:*?\"<>|!]", "-")
            ;
    }

    // ----------------------------------------------------------------
    // READ GARMIN FIT FILE INFO
    // ----------------------------------------------------------------
    private static ActivityInfo readFitInfo(File fitFile) {
        final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        try {
        Decode decode = new Decode();
        MesgBroadcaster broadcaster = new MesgBroadcaster(decode);

        final List<ActivityMesg> activities   = new ArrayList<>();
        final List<SessionMesg> sessions      = new ArrayList<>();
        final List<DeviceInfoMesg> deviceInfos = new ArrayList<>();
        final List<FileIdMesg> fileIds         = new ArrayList<>();
        final List<WorkoutMesg> workouts       = new ArrayList<>();

        // ActivityMesg
        broadcaster.addListener(new ActivityMesgListener() {
            @Override
            public void onMesg(ActivityMesg m) {
                activities.add(m);
            }
    });

// SessionMesg
broadcaster.addListener(new SessionMesgListener() {
    @Override
    public void onMesg(SessionMesg m) {
        sessions.add(m);
    }
});

// DeviceInfoMesg
broadcaster.addListener(new DeviceInfoMesgListener() {
    @Override
    public void onMesg(DeviceInfoMesg m) {
        deviceInfos.add(m);
    }
});

// FileIdMesg
broadcaster.addListener(new FileIdMesgListener() {
    @Override
    public void onMesg(FileIdMesg m) {
        fileIds.add(m);
    }
});

// WorkoutMesg
broadcaster.addListener(new WorkoutMesgListener() {
    @Override
    public void onMesg(WorkoutMesg m) {
        workouts.add(m);
    }
});

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

            // ActivityMesg
            // --------------------------------
            ActivityMesg actMesg = activities.isEmpty() ? null : activities.get(0);

            // prefer ActivityMesg.localTimestamp if present
            String dateTime = null;
            if (actMesg != null) {
                // ActivityMesg.getLocalTimestamp() typically returns Long (Garmin epoch seconds)
                try {
                    Long localTs = actMesg.getLocalTimestamp();
                    if (localTs != null && localTs > 0) {
                        dateTime = FitDateTime.toString(localTs);
                        //dateTime = DATE_FORMAT.format(new Date(unixMs));
                    }
                } catch (Exception e) {
                    // safest: ignore and fallback below
                }
            }

            // SessionMesg
            // --------------------------------
            SessionMesg sessMesg = sessions.isEmpty() ? null : sessions.get(0);
            
            // fallback to SessionMesg.startTime (UTC -> Date) if no local timestamp found
            if (dateTime == null && sessMesg != null && sessMesg.getStartTime() != null) {
                dateTime = DATE_FORMAT.format(sessMesg.getStartTime().getDate());
            }

            if (sessMesg == null) {
                // no session message found — can't get sport/timer/distance reliably
                System.out.println("  No SessionMesg found in FIT: " + fitFile.getName());
                return null;
            }

            // profile: prefer sportProfileName, fallback to sport enum, else "unknown"
            String profileName = new FormattedProfileName(
                sessMesg.getSportProfileName()
                , sessMesg.getSport()
                , sessMesg.getSubSport()
                ).getName();

            // --- Distance and Timer formatting ---

            String timerDistStr = new FormattedTimerDistString(
                sessMesg.getTotalTimerTime()
                ,sessMesg.getTotalDistance()
                ).get();

            // --- Timer as m.ss ---

            // DeviceInfo
            // --------------------------------
            DeviceInfoMesg devMesg = deviceInfos.isEmpty() ? null : deviceInfos.get(0);

            // --------------------------------
            // FileIdInfo
            FileIdMesg fileIdMesg = fileIds.isEmpty() ? null : fileIds.get(0);

            String product = new FormattedProductName(
                (devMesg != null ? devMesg.getManufacturer() : null)
                ,(devMesg != null ? devMesg.getProduct() : null)
                ,(fileIdMesg != null ? fileIdMesg.getManufacturer() : null)
                ,(fileIdMesg != null ? fileIdMesg.getProduct() : null)
                ,(devMesg != null ? devMesg.getSoftwareVersion() : null)
                ).getName();

            System.out.println("  FormattedProductName='" + product + "'");
            // --------------------------------
            // WorkoutMesg
            WorkoutMesg workoutMesg = workouts.isEmpty() ? null : workouts.get(0);

            String wktName = new FormattedWorkoutName(
                (workoutMesg != null ? workoutMesg.getWktName() : null)
                ).getName();

            if (wktName !=null && profileName.contains(wktName))
                wktName = null;

            // --- Assign to info object ---

            ActivityInfo actInfo = new ActivityInfo();
            actInfo.dateTime = (dateTime != null) ? dateTime : DATE_FORMAT.format(new Date());
            actInfo.profile = profileName != null && !product.isEmpty() ? profileName : null;
            actInfo.timer = timerDistStr != null && !product.isEmpty() ? timerDistStr : null;
            actInfo.product = product != null && !product.isEmpty() ? product : null;
            actInfo.workoutName = wktName != null && !wktName.isEmpty() ? wktName : null;

            return actInfo;

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
        public String product;
        public String workoutName;
    }
}
