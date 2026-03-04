package se.peho.fittools.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import se.peho.fittools.core.strings.StringsDebug;

public class Conf {
    String configFilename = "";
	int hoursToAdd = 0;
	String filePathPrefix = "";
	String inputFilePath = "";
	String extraFilename = "";
    String profileNameSuffix = "";
	int timeOffsetSec = 0;
	String command = "";
	String startWithWktStep = "";
	String newWktName = "";
	int C2FitFileDistanceStartCorrection = 0;
	String useManualC2SyncSeconds = "";
	int c2SyncSecondsC2File = 0;
	int c2SyncSecondsLapDistCalc = 0;
    Set<String> debugFlags = new HashSet<>();
	
    // Getters and setters

    public String getFilePathPrefix() { return filePathPrefix; }
    public void setFilePathPrefix(String filePathPrefix) { this.filePathPrefix = filePathPrefix; }

    public String getInputFilePath() { return inputFilePath; }
    public void setInputFilePath(String inputFilePath) { this.inputFilePath = inputFilePath; }

    public String getExtraFilename() { return extraFilename; }
    public void setExtraFilename(String extraFilename) { this.extraFilename = extraFilename; }

    public String getProfileNameSuffix() { return profileNameSuffix; }
    public void setProfileNameSuffix(String profileNameSuffix) { this.profileNameSuffix = profileNameSuffix; }

    public int getTimeOffsetSec() { return timeOffsetSec; }
    public void setTimeOffsetSec(int timeOffsetSec) { this.timeOffsetSec = timeOffsetSec; }

    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }

    public String getStartWithWktStep() { return startWithWktStep; }
    public void setStartWithWktStep(String startWithWktStep) { this.startWithWktStep = startWithWktStep; }

    public String getNewWktName() { return newWktName; }
    public void setNewWktName(String newWktName) { this.newWktName = newWktName; }

    public int getC2FitFileDistanceStartCorrection() { return C2FitFileDistanceStartCorrection; }
    public void setC2FitFileDistanceStartCorrection(int c2FitFileDistanceStartCorrection) { this.C2FitFileDistanceStartCorrection = c2FitFileDistanceStartCorrection; }

    public String getUseManualC2SyncSeconds() { return useManualC2SyncSeconds; }
    public void setUseManualC2SyncSeconds(String useManualC2SyncSeconds) { this.useManualC2SyncSeconds = useManualC2SyncSeconds; }

    public int getC2SyncSecondsC2File() { return c2SyncSecondsC2File; }
    public void setC2SyncSecondsC2File(int c2SyncSecondsC2File) { this.c2SyncSecondsC2File = c2SyncSecondsC2File; }

    public int getC2SyncSecondsLapDistCalc() { return c2SyncSecondsLapDistCalc; }
    public void setC2SyncSecondsLapDistCalc(int c2SyncSecondsLapDistCalc) { this.c2SyncSecondsLapDistCalc = c2SyncSecondsLapDistCalc; }

    public boolean isDebug()           { return debugFlags.contains("debug") || !debugFlags.isEmpty(); }
    public boolean isDebugLaps()        { return debugFlags.contains("debug") || debugFlags.contains("debuglaps"); }
    public boolean isDebugWkt()         { return debugFlags.contains("debug") || debugFlags.contains("debugwkt"); }
    public boolean isDebugFixData()     { return debugFlags.contains("debug") || debugFlags.contains("debugfixdata"); }
    public boolean isDebugSync()        { return debugFlags.contains("debug") || debugFlags.contains("debugsync"); }
    public boolean isDebugSplit()       { return debugFlags.contains("debug") || debugFlags.contains("debugsplit"); }
    public boolean isDebugDevFields()   { return debugFlags.contains("debug") || debugFlags.contains("debugdevfields"); }
    public boolean isDebugStrings()      { return debugFlags.contains("debug") || debugFlags.contains("debugstrings"); }

    public Conf(String[] args) {

        // ================================================================
        // STEP 0: Strip --debug* options from the front of args.
        // They trigger CLI mode and the remaining args are parsed normally.
        // Supported: --debug (all), --debuglaps, --debugfixdata, --debugsync
        // ================================================================
        int debugArgCount = 0;
        while (debugArgCount < args.length && args[debugArgCount].toLowerCase().startsWith("--debug")) {
            debugFlags.add(args[debugArgCount].substring(2).toLowerCase()); // store without "--"
            debugArgCount++;
        }
        if (debugArgCount > 0) {
            args = Arrays.copyOfRange(args, debugArgCount, args.length);
            System.out.println("============= DEBUG FLAGS: " + debugFlags + " =============");
        }
        StringsDebug.enabled = isDebugStrings();

        if (args.length > 0) {
            // ================================================================
            // POPULATE FROM CLI ARGUMENTS
            // filePathPrefix = current working directory
            // ================================================================
            System.out.println("============= ARGS (" + args.length + ") =============");
            filePathPrefix = resolveDownloadsFolder() + File.separator;

            if (args.length >= 1) setProfileNameSuffix(args[0]);
            if (args.length >= 2) {
                try {
                    setTimeOffsetSec(Integer.parseInt(args[1]) * 60);
                } catch (NumberFormatException e) {
                    System.out.println("ERROR: timeOffsetMin argument \"" + args[1] + "\" is not a valid integer. Defaulting to 2 minutes.");
                    setTimeOffsetSec(2 * 60);
                }
            }
            if (args.length >= 3) setInputFilePath(args[2]);
            if (args.length >= 4) setExtraFilename(args[3]);
            if (args.length >= 5) {
                setCommand(args[4]);
                if (args.length >= 6) {
                    if (getCommand().equalsIgnoreCase("corr")) {
                        setC2FitFileDistanceStartCorrection(Integer.parseInt(args[5]));
                    } else if (getCommand().equalsIgnoreCase("wkt") && args.length >= 7) {
                        setStartWithWktStep(args[5]);
                        setNewWktName(args[6]);
                    }
                }
            }

        } else {
            // ================================================================
            // POPULATE FROM CONF.TXT
            // ================================================================
            System.out.println("============= NO ARGS =============");

            Path configFilePath = PehoUtils.findConfigFile();
            if (configFilePath == null) {
                System.out.println("------> No configuration file found.");
                System.out.println("Using default values:");
                System.out.println("+++++++++++++++++++++++++++++++++++");
                String defaultPath = resolveDownloadsFolder();
                System.out.println("filePathPrefix: "    + defaultPath + File.separator + "  (Downloads if exists, else current path)");
                System.out.println("profileNameSuffix: " + "gym jobbet");
                System.out.println("timeOffsetSec: "     + "120 sec (2 minutes)");
                System.out.println("inputFilePath: "     + "aaa.zip / aaa.fit (searched in current path)");
                System.out.println("extraFilename: "     + "bbb.zip / bbb.fit / bbb.txt (searched in current path)");
                System.out.println("+++++++++++++++++++++++++++++++++++");

            } else {
                configFilename = configFilePath.toString();
                System.out.println("------>  Using configuration file: " + configFilename);

                try (BufferedReader br = new BufferedReader(new FileReader(configFilename))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (!line.isBlank()) {
                            String[] parts = line.split(" ", 2);
                            String variable = parts[0];
                            if (!variable.startsWith("//") && !variable.isBlank() && parts.length > 1) {
                                String value = parts[1].isBlank() ? "" : parts[1];
                                switch (variable) {
                                    case "filePathPrefix":                  filePathPrefix = value; break;
                                    case "profileNameSuffix":               profileNameSuffix = value; break;
                                    case "timeOffsetMin":                   timeOffsetSec = Integer.parseInt(value) * 60; break;
                                    case "inputFilePath":                   inputFilePath = value; break;
                                    case "extraFilename":                   extraFilename = value; break;
                                    case "command":                         command = value; break;
                                    case "startWithWktStep":                startWithWktStep = value; break;
                                    case "newWktName":                      newWktName = value; break;
                                    case "C2FitFileDistanceStartCorrection": C2FitFileDistanceStartCorrection = Integer.parseInt(value); break;
                                    case "useManualC2SyncSeconds":          useManualC2SyncSeconds = value; break;
                                    case "c2SyncSecondsC2File":             c2SyncSecondsC2File = Integer.parseInt(value); break;
                                    case "c2SyncSecondsLapDistCalc":        c2SyncSecondsLapDistCalc = Integer.parseInt(value); break;
                                    case "hoursToAdd":                      hoursToAdd = Integer.parseInt(value); break;
                                } //switch
                            } //if not comment
                        } //if not blank
                    } //while
                } catch (IOException | NumberFormatException e) {
                    e.printStackTrace();
                }

                // If filePathPrefix from conf.txt is missing or not a valid directory,
                // fall back to the directory where conf.txt was found.
                if (filePathPrefix.isEmpty() || !new File(filePathPrefix).isDirectory()) {
                    filePathPrefix = configFilePath.getParent().toString() + File.separator;
                    System.out.println("------------- filePathPrefix not valid, using conf.txt directory: " + filePathPrefix);
                }
            }
        }

        // If filePathPrefix is still empty, default to Downloads (if it exists) or current working directory
        if (filePathPrefix.isEmpty()) {
            String downloadsFolder = resolveDownloadsFolder();
            if (!downloadsFolder.equals(System.getProperty("user.dir"))) {
                filePathPrefix = downloadsFolder;
                System.out.println("------> filePathPrefix not set, defaulting to Downloads: " + filePathPrefix);
            } else {
                filePathPrefix = downloadsFolder;
                System.out.println("-------> filePathPrefix not set, Downloads not found, defaulting to current path: " + filePathPrefix);
            }
        }

        // Ensure filePathPrefix ends with a separator
        if (!filePathPrefix.isEmpty() && !filePathPrefix.endsWith("/") && !filePathPrefix.endsWith(File.separator)) {
            filePathPrefix += File.separator;
        }

        // ================================================================
        // STEP 4: Resolve inputFilePath – fallback to aaa.zip / aaa.fit
        // ================================================================
        String resolvedInput = inputFilePath.isEmpty() || new File(inputFilePath).isAbsolute()
                ? inputFilePath
                : filePathPrefix + inputFilePath;
        inputFilePath = resolveValidFile(resolvedInput, new String[]{"aaa.zip", "aaa.fit"}, "inputFilePath");

        // ================================================================
        // STEP 5: Resolve extraFilename – fallback to bbb.zip / bbb.fit / bbb.txt
        // ================================================================
        String resolvedExtra = extraFilename.isEmpty() || new File(extraFilename).isAbsolute()
                ? extraFilename
                : filePathPrefix + extraFilename;
        extraFilename = resolveValidFile(resolvedExtra, new String[]{"bbb.zip", "bbb.fit", "bbb.txt"}, "extraFilename");

        // ================================================================
        // STEP 6: Default timeOffsetSec → 2 minutes
        // ================================================================
        if (timeOffsetSec == 0) {
            setTimeOffsetSec(2 * 60);
            System.out.println("------> timeOffsetSec not set, defaulting to 2 min (120 sec)");
        }

        // ================================================================
        // STEP 7: Default profileNameSuffix → "gym jobbet"
        // ================================================================
        if (profileNameSuffix.isEmpty()) {
            setProfileNameSuffix("gym jobbet");
            System.out.println("------> profileNameSuffix not set, defaulting to \"gym jobbet\"");
        }

        System.out.println("+++++++++++++++++++++++++++++++++++");
        System.out.println("filePathPrefix: "                  + getFilePathPrefix());
        System.out.println("profileNameSuffix: "               + getProfileNameSuffix());
        System.out.println("timeOffsetSec: "                   + getTimeOffsetSec());
        System.out.println("inputFilePath: "                   + getInputFilePath());
        System.out.println("extraFilename: "                   + getExtraFilename());
        System.out.println("command: "                         + getCommand());
        System.out.println("startWithWktStep: "                + getStartWithWktStep());
        System.out.println("newWktName: "                      + getNewWktName());
        System.out.println("C2FitFileDistanceStartCorrection: "+ getC2FitFileDistanceStartCorrection());
        System.out.println("useManualC2SyncSeconds: "          + getUseManualC2SyncSeconds());
        System.out.println("c2SyncSecondsC2File: "             + getC2SyncSecondsC2File());
        System.out.println("c2SyncSecondsLapDistCalc: "        + getC2SyncSecondsLapDistCalc());
        System.out.println("+++++++++++++++++++++++++++++++++++");
        System.out.println("Input file used: " + getInputFilePath());

    } //constructor Conf

    // ================================================================
    // Checks whether 'path' points to an existing file. If not, tries
    // each entry in 'fallbacks' (relative to filePathPrefix). Returns
    // the resolved (and possibly unzipped) path, or exits if nothing found.
    // ================================================================
    private String resolveValidFile(String path, String[] fallbacks, String fieldName) {
        // Try the primary path
        if (!path.isEmpty()) {
            String resolved = extractIfNeeded(path);
            if (resolved != null) return resolved;
        }
        // Try fallback candidates in filePathPrefix directory
        for (String fallback : fallbacks) {
            String fallbackPath = filePathPrefix + fallback;
            if (new File(fallbackPath).isFile()) {
                System.out.println("------> " + fieldName + " not found at \"" + path + "\", using fallback: " + fallbackPath);
                String resolved = extractIfNeeded(fallbackPath);
                if (resolved != null) return resolved;
            }
        }
        System.out.println("**********************");
        System.out.println("ERROR: No valid file found for [" + fieldName + "].");
        System.out.println("  Primary path tried : " + (path.isEmpty() ? "(empty)" : path));
        System.out.println("  Fallbacks tried in : " + filePathPrefix);
        for (String fb : fallbacks) System.out.println("    - " + fb);
        System.out.println("**********************");
        printCliUsage();
        System.exit(1);
        return null;
    }

    // ================================================================
    // Returns the Downloads folder path based on path existence.
    // Falls back to the current working directory if not found.
    //   Android : /storage/emulated/0/Download  (checked by path, not OS detection,
    //             since Termux JVM lacks android.os.Build on its classpath)
    //   Windows : ~/Downloads
    //   Linux   : ~/Downloads
    // ================================================================
    private static String resolveDownloadsFolder() {
        // Android (including Termux): check by path existence, not class detection
        String androidPath = "/storage/emulated/0/Download";
        if (new File(androidPath).isDirectory()) return androidPath;

        // Windows / Linux
        String downloadsPath = System.getProperty("user.home") + File.separator + "Downloads";
        if (new File(downloadsPath).isDirectory()) return downloadsPath;

        return System.getProperty("user.dir"); // ultimate fallback
    }

    // ================================================================
    // Prints CLI usage / parameter help to stdout.
    // ================================================================
    private static void printCliUsage() {
        System.out.println();
        System.out.println("Usage: <program> <profileNameSuffix> [timeOffsetMin] [inputFile] [extraFile] [command [commandArgs...]]");
        System.out.println();
        System.out.println("  profileNameSuffix  Profile name suffix string (default: \"gym jobbet\").");
        System.out.println("  timeOffsetMin      Time offset in whole minutes (default: 2).");
        System.out.println("  inputFile          Path to the primary input file (e.g. aaa.zip or aaa.fit).");
        System.out.println("                     Falls back to aaa.zip / aaa.fit in the working directory.");
        System.out.println("  extraFile          Path to the secondary file (e.g. bbb.zip / bbb.fit / bbb.txt).");
        System.out.println("                     Falls back to bbb.zip / bbb.fit / bbb.txt in the working directory.");
        System.out.println("  command            Optional command to run. Supported values:");
        System.out.println("                       corr <distCorrection>  - distance start correction (integer metres).");
        System.out.println("                       wkt  <startStep> <newWktName>  - workout merge/rename.");
        System.out.println();
        System.out.println("Alternatively, place a conf.txt file in the working directory or a parent directory.");
        System.out.println("Supported conf.txt keys: filePathPrefix, inputFilePath, extraFilename,");
        System.out.println("  profileNameSuffix, timeOffsetMin, command, startWithWktStep, newWktName,");
        System.out.println("  C2FitFileDistanceStartCorrection, useManualC2SyncSeconds,");
        System.out.println("  c2SyncSecondsC2File, c2SyncSecondsLapDistCalc, hoursToAdd.");
    }

    // ================================================================
    // Returns the usable file path for 'path':
    //   - non-zip: returned as-is if the file exists, null otherwise.
    //   - zip: extracted, then renamed to <zipBaseName>.<innerExt> so
    //     that two different zips never collide on the same output file.
    // ================================================================
    private String extractIfNeeded(String path) {
        File f = new File(path);
        if (!f.exists() || !f.isFile()) return null;

        if (PehoUtils.getFileExtension(f).equals("zip")) {
            String extracted = PehoUtils.unzip(f);
            if (extracted == null || extracted.isEmpty()) return null;

            File extractedFile = new File(extracted);
            // Build target name: <zip-base-name>.<inner-extension>
            // e.g. aaa.zip containing foo.fit  →  aaa.fit
            String zipBase   = f.getName().replaceAll("(?i)\\.zip$", "");
            String innerExt  = PehoUtils.getFileExtension(extractedFile);
            File   target    = new File(f.getParent(), zipBase + "." + innerExt);

            if (!extractedFile.getAbsolutePath().equals(target.getAbsolutePath())) {
                if (!extractedFile.renameTo(target)) {
                    System.out.println("WARNING: could not rename " + extractedFile + " → " + target + ", using original name.");
                    return extractedFile.getPath();
                }
            }
            System.out.println("------> UNZIPPED: " + f.getName() + " → " + target.getName());
            return target.getPath();
        }
        return path;
    }

} //class Conf
