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
import se.peho.fittools.core.files.*;

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
                processFiles(folder, filter, false, "zip");
                break;
            case "zipfit":
                processFiles(folder, filter, true, "zip");
                break;
            case "fit":
                processFiles(folder, filter, false, "fit");
                break;
            default:
                System.out.println("Invalid mode. Use one of: zip, zipfit, fit (as first cli args)");
                return;
        }

        System.out.println("Done.");
    }

    // ----------------------------------------------------------------
    // ----------------------------------------------------------------
    // PROCESS FILES
    // ----------------------------------------------------------------
    private static void processFiles(java.io.File folder, String filter, boolean keepFit, String ext) throws Exception {

        ext = "." + ext.toLowerCase();

        File[] allFiles = folder.listFiles();
        List<File> files = new ArrayList<>();

        if (allFiles != null) {
            for (File file : allFiles) {

                String name = file.getName().toLowerCase();

                // 1. Must be a .zip or .fit file
                if (!name.endsWith(ext)) {
                    continue;
                }

                // 2. No filter provided → digits only
                if (filter == null || filter.isEmpty()) {
                    if (name.matches("\\d+\\" + ext)) {
                        files.add(file);
                    }
                }
                // 3. Filter provided → name contains filter
                else {
                    if (name.contains(filter.toLowerCase())) {
                        files.add(file);
                    }
                }
            }
        }

        if (files == null || files.isEmpty()) {
            System.out.println("No matching " + ext.toUpperCase() + " files found.");
            return;
        }


        System.out.println("Found " + files.size() + " " + ext.toUpperCase() + " files to process.");

        for (File theFile : files) {
            System.out.println("Processing: " + theFile.getName());

            File fitFile = null;
            if (ext.equals(".zip")) {

                // --- Extract .fit file from .zip ---
                fitFile = TheFile.extractFit(theFile);
                if (fitFile == null) {
                    System.out.println("  No .fit file found inside.");
                    continue;
                }
            } else {
                fitFile = theFile;
            }

            // --- Read .fit file info ---
            String baseName = readFitInfo(fitFile);
            if (baseName == null) {
                System.out.println("  Could not read FIT info.");
                if (!keepFit) fitFile.delete();
                continue;
            }
            baseName = SanitizedFilename.get(baseName);


            // --- Rename ORG FILE (zip or fit)---
            TheFile.rename(theFile, baseName, "-renamed", ext);

            if (ext.equals(".zip")) {

                // --- Rename FIT IN ZIP (optional) ---
                if (keepFit) {
                    // .fit file directly
                    TheFile.rename(fitFile, baseName, "-renamed", ".fit");
                } else {
                    fitFile.delete();
                }
            }
        }
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

            String distTimerStr = new DistTimerStr(
                sessMesg.getTotalDistance()
                ,sessMesg.getTotalTimerTime()
                ).get();

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

            return FileBaseStr.get((dateTime != null) ? dateTime : DATE_FORMAT.format(new Date())
                , profileName != null && !product.isEmpty() ? profileName : null
                , wktName != null && !wktName.isEmpty() ? wktName : null
                , distTimerStr != null && !product.isEmpty() ? distTimerStr : null
                , product != null && !product.isEmpty() ? product : null
                );

        } catch (Exception e) {
            System.out.println("  Error reading FIT: " + e.getMessage());
            return null;
        }
    }

}
