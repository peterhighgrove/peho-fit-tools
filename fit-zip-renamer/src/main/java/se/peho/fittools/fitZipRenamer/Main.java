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
            String newZipName = formatZipFilename(baseName);
            Path newZipPath = zipFile.toPath().resolveSibling(newZipName);
            Files.move(zipFile.toPath(), newZipPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("  Renamed ZIP to: " + newZipName);

            // --- Rename FIT (optional) ---
            if (keepFit) {
                String newFitName = formatFitFilename(baseName);
                Path newFitPath = fitFile.toPath().resolveSibling(newFitName);
                Files.move(fitFile.toPath(), newFitPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("  Renamed FIT to: " + newFitName);
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
            String newName = formatFitFilename(baseName);

            // replace spaces and forbidden chars with safe alternatives
            Path newPath = fitFile.toPath().resolveSibling(sanitizeFilename(newName));
            Files.move(fitFile.toPath(), newPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("  Renamed to: " + sanitizeFilename(newName));
        }
    }

    // ----------------------------------------------------------------
    // FORMAT BASE NAME
    // ----------------------------------------------------------------
    private static String formatBaseName (ActivityInfo info) {
        return String.format(
            "%s%s%s%s%s%s%s",
            info.dateTime,
            info.profile != null && !info.profile.isEmpty() ? "-" + info.profile : "",
            info.workoutName != null && !info.workoutName.isEmpty() ? "-" + info.workoutName : "",
            info.timer != null && !info.timer.isEmpty() ? "-" + info.timer : "",
            info.distance != null && !info.distance.isEmpty() ? "-" + info.distance + "" : "",
            info.product != null && !info.product.isEmpty() ? "-" + info.product : "",
            info.swVer != null && !info.swVer.isEmpty() ? "-" + info.swVer : ""
        );
    }

    // ----------------------------------------------------------------
    // FORMAT FIT FILENAME
    // ----------------------------------------------------------------
    private static String formatFitFilename(String baseName) {
        return baseName + "-garminfit.fit";
    }
    
    // ----------------------------------------------------------------
    // FORMAT ZIP FILENAME
    // ----------------------------------------------------------------
    private static String formatZipFilename(String baseName) {
        return baseName + "-garminzip.zip";
    }
    
    // ----------------------------------------------------------------
    // SANITIZE FILENAME
    // ----------------------------------------------------------------
    private static String sanitizeFilename(String name) {
        return name.replaceAll("[\\\\/:*?\"<>|!]", "-");
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

            ActivityMesg actMesg = activities.isEmpty() ? null : activities.get(0);

            // prefer ActivityMesg.localTimestamp if present
            String dateTime = null;
            if (actMesg != null) {
                // ActivityMesg.getLocalTimestamp() typically returns Long (Garmin epoch seconds)
                try {
                    Long localTs = actMesg.getLocalTimestamp();
                    if (localTs != null && localTs > 0) {
                        long unixMs = (localTs + 631065600L) * 1000L; // Garmin epoch -> unix ms
                        dateTime = FitDateTime.toString(localTs);
                        //dateTime = DATE_FORMAT.format(new Date(unixMs));
                    }
                } catch (Exception e) {
                    // safest: ignore and fallback below
                }
            }

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
            String profile;
            if (sessMesg.getSportProfileName() != null) {
                profile = sessMesg.getSportProfileName().toLowerCase();
            } else if (sessMesg.getSport() != null) {
                profile = sessMesg.getSport().toString().toLowerCase();
                if (sessMesg.getSubSport() != null) {
                    profile += "-" + sessMesg.getSubSport().toString().toLowerCase();
                }
            } else {
                profile = null;
            }
            // sanitize profile (replace spaces)
            profile = profile
                .replace(",", ".")
                .replace("elliptical-(bike)", "elliptical")
                .replace("skierg-(bike)", "skierg")
                .replace("fitness_equipment-", "")
                ;

            // --- Distance and Timer formatting ---

            Float totalTimer = sessMesg.getTotalTimerTime(); // seconds
            Float totalDist = sessMesg.getTotalDistance();   // meters

            // --- Timer as m.ss ---
            long totalSeconds = (totalTimer != null) ? Math.round(totalTimer) : 0L;
            long totalMinutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;
            String timer = String.format("%d.%02dmin", totalMinutes, seconds)
                .replace(",", ".")
                .replace(".00", "")
                ;

            // --- Distance as km, trimmed, no trailing zeros or dot ---
            double distanceKm = (totalDist != null) ? (totalDist / 1000.0) : 0.0;
            String distanceStr = String.format("%.1fkm", distanceKm)
                .replace(",", ".")
                .replace(".0", "")
                ; 

            // --------------------------------
            // DeviceInfo
            DeviceInfoMesg devMesg = deviceInfos.isEmpty() ? null : deviceInfos.get(0);
            String product = null;
            String manuf = null;
            String swVersion = null;
            if (devMesg != null) {
                Integer manufNo = devMesg.getManufacturer();
                manuf = Manufacturer.getStringFromValue(manufNo != null ? manufNo : null);
                Integer productNo = devMesg.getProduct();
                product = GarminProduct.getStringFromValue(productNo != null ? productNo : null);

                Float swVersionF = devMesg.getSoftwareVersion() != null ? devMesg.getSoftwareVersion() : null;
                swVersion = swVersionF != null ? 
                    String.format("v%.2f", devMesg.getSoftwareVersion())
                    .replace(",", ".")
                     : null;

                // Could use manuf/product for something if needed
                System.out.println("  Extracted info: DateTime=" + dateTime + ", Profile=" + profile + ", Distance=" + distanceStr + ", Timer=" + timer);
                System.out.println("   From DevInfo Manufacturer: " + manuf + "(" + manufNo + ")"
                    + ", Product: " + product + " (" + productNo + ")"
                    + ", sw:" + swVersionF
                    );
            }

            // --------------------------------
            // FileIdInfo
            FileIdMesg fileIdMesg = fileIds.isEmpty() ? null : fileIds.get(0);
            //String swVersion2 = fileIdMesg.get != null ? fileIdMesg.getSoftwareVersion().toString() : "unknown";
            String product2 = null;
            String manuf2 = null;
            if (fileIdMesg != null) {
                Integer manufNo2 = fileIdMesg.getManufacturer();
                manuf2 = Manufacturer.getStringFromValue(manufNo2 != null ? manufNo2 : null);
                Integer productNo2 = fileIdMesg.getProduct();
                product2 = GarminProduct.getStringFromValue(productNo2 != null ? productNo2 : null);

                
                // Could use manuf/product for something if needed
                System.out.println("    From FileId Manufacturer: " + manuf2 + "(" + manufNo2 + ")"
                    + " Product: " + product2 + " (" + productNo2 + ")"
                    );
            }

            if (manuf.isEmpty()) {
                manuf = manuf2;
            }
            if (manuf.equals("CONCEPT2")) {
                product = "concept2";
                swVersion = null;
            }

            if (product.isEmpty()) {
                product = product2;
            }

            if (product.contains("EPIX_GEN2_PRO")) {
                product = "epix2pro";
            } else if (product.contains("EPIX_GEN2")) {
                product = "epix2";
            } else if (product.contains("FENIX6X")) {
                product = "f6x";
            }

            // --------------------------------
            // WorkoutMesg (for future use)
            WorkoutMesg workoutMesg = workouts.isEmpty() ? null : workouts.get(0);
            String wktName = null;
            if (workoutMesg != null) {
                System.out.println("  Workout name: " + workoutMesg.getWktName());
                wktName = workoutMesg.getWktName()
                    .replace(",", ".")
                    .replace("/", "_")
                    .replace("Bike ", "")
                    .replace("Run ", "")
                    .replace("Styrka ", "")
                    .replace(" (bike)", "")
                    .replace("HR", "")
                    ;
            }


            // --- Assign to info object ---

            ActivityInfo actInfo = new ActivityInfo();
            actInfo.dateTime = (dateTime != null) ? dateTime : DATE_FORMAT.format(new Date()); // best-effort
            actInfo.profile = profile;
            actInfo.distance = distanceStr;
            actInfo.timer = timer;
            actInfo.product = product != null && !product.isEmpty() ? product : null;
            actInfo.swVer = swVersion != null && !swVersion.isEmpty() ? swVersion : null;
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
        public String swVer;
        public String workoutName;
    }
}
