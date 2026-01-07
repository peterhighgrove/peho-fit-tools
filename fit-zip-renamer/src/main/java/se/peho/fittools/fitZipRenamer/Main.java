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
import com.garmin.fit.DeviceInfoMesg;
import com.garmin.fit.DeviceInfoMesgListener;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.FileIdMesgListener;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgBroadcaster;
import com.garmin.fit.SessionMesg;
import com.garmin.fit.SessionMesgListener;
import com.garmin.fit.WorkoutMesg;
import com.garmin.fit.WorkoutMesgListener;

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
            String baseName = readFitInfo(fitFile);
            if (baseName == null) {
                System.out.println("  Could not read FIT info.");
                if (!keepFit) fitFile.delete();
                continue;
            }

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
            String baseName = readFitInfo(fitFile);
            if (baseName == null) {
                System.out.println("  Could not read FIT info.");
                continue;
            }

            // replace spaces and forbidden chars with safe alternatives
            String newName = new SanitizedFilename(addSuffixFit(baseName)).get();

            Path newPath = fitFile.toPath().resolveSibling(newName);
            Files.move(fitFile.toPath(), newPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("  Renamed to: " + newName);
            System.out.println("--------------------------------");
        }
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
    // READ GARMIN FIT FILE INFO
    // ----------------------------------------------------------------
    private static String readFitInfo(File fitFile) {
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
                    Long localDT = actMesg.getLocalTimestamp();
                    if (localDT != null && localDT > 0) {
                        dateTime = DTstr.get(localDT);
                        //dateTime = new FormattedDT(localDT).get();
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
            String profileName = ProfileStr.get(
                sessMesg.getSportProfileName()
                , sessMesg.getSport()
                , sessMesg.getSubSport()
                );
            // --- Distance and Timer formatting ---

            String timerDistStr = TimerDistStr.get(
                sessMesg.getTotalTimerTime()
                ,sessMesg.getTotalDistance()
                );

            // DeviceInfo
            // --------------------------------
            DeviceInfoMesg devMesg = deviceInfos.isEmpty() ? null : deviceInfos.get(0);

            String product = ProductStr.get(
                (devMesg != null ? devMesg.getManufacturer() : null)
                ,(devMesg != null ? devMesg.getProduct() : null)
                ,(devMesg != null ? devMesg.getSoftwareVersion() : null)
                );

            // --------------------------------
            // WorkoutMesg
            WorkoutMesg workoutMesg = workouts.isEmpty() ? null : workouts.get(0);

            String wktName = WorkoutStr.get(
                (workoutMesg != null ? workoutMesg.getWktName() : null)
                );

            if (wktName !=null && profileName.contains(wktName))
                wktName = null;

            // --- Assign to info object ---

            return FileBaseStr.get(
                (dateTime != null) ? dateTime : DATE_FORMAT.format(new Date())
                , profileName != null && !product.isEmpty() ? profileName : null
                , wktName != null && !wktName.isEmpty() ? wktName : null
                , timerDistStr != null && !product.isEmpty() ? timerDistStr : null
                , product != null && !product.isEmpty() ? product : null
                );

        } catch (Exception e) {
            System.out.println("  Error reading FIT: " + e.getMessage());
            return null;
        }
    }

}
