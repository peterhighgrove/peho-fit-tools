package se.peho.fittools.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class Conf {
    String configFilename = "";
	int hoursToAdd = 0;
	String filePathPrefix = "";
	String inputFilePath = "";
	String extraFilename = "";
    String profileNameSuffix = "test";
	int timeOffsetSec = 0;
	String command = "";
	String startWithWktStep = "";
	String newWktName = "";
	int C2FitFileDistanceStartCorrection = 0;
	String useManualC2SyncSeconds = "";
	int c2SyncSecondsC2File = 0;
	int c2SyncSecondsLapDistCalc = 0;
	
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

	public Conf (String[] args) {

        if (args.length == 0) {

            System.out.println("============= NO ARGS =============");
            System.out.println("------------- USES ONLY CONF FILE ------");

            Path configFilePath = PehoUtils.findConfigFile();
            if (configFilePath == null) {

                System.out.println("------------- No configuration file found. Using default values. ------------- ");
                //System.exit(0);

            } else {
                System.out.println("------------- Using configuration file: " + configFilename);
                configFilename = PehoUtils.findConfigFile().toString();

                try (BufferedReader br = new BufferedReader(new FileReader(configFilename))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (!line.isBlank()) {
                            String[] parts = line.split(" ", 2); // Split into max 2 parts

                            String variable = parts[0];
                            
                            if ((!variable.startsWith("//") || !variable.isBlank()) && (parts.length>1)) {
                                String value = parts[1]; 
                                if (value.isBlank()) {
                                    value="";
                                }
                                
                                switch (variable) {
                                    case "hoursToAdd":
                                        hoursToAdd = Integer.parseInt(value);
                                        break;
                                    case "filePathPrefix":
                                        filePathPrefix = value;
                                        break;
                                    case "inputFilePath":
                                        inputFilePath = value;
                                        break;
                                    case "extraFilename":
                                        extraFilename = value;
                                        break;
                                    case "profileNameSuffix":
                                        profileNameSuffix = value;
                                        break;
                                    case "timeOffsetMin":
                                        timeOffsetSec = Integer.parseInt(value)*60;
                                        break;
                                    case "command":
                                        command = value;
                                        break;
                                    case "startWithWktStep":
                                        startWithWktStep = value;
                                        break;
                                    case "newWktName":
                                        newWktName = value;
                                        break;
                                    case "C2FitFileDistanceStartCorrection":
                                        C2FitFileDistanceStartCorrection = Integer.parseInt(value);
                                        break;
                                    case "useManualC2SyncSeconds":
                                        useManualC2SyncSeconds = value;
                                        break;
                                    case "c2SyncSecondsC2File":
                                        c2SyncSecondsC2File = Integer.parseInt(value);
                                        break;
                                    case "c2SyncSecondsLapDistCalc":
                                        c2SyncSecondsLapDistCalc = Integer.parseInt(value);
                                        break;
                                
                                
                                } //switch
                            } //if not //
                        } //if line isBlank
                    } //while

                } catch (IOException | NumberFormatException e) {
                    e.printStackTrace();
                }
            } // if filename isBlank
        }
        if (args.length == 1) {
            System.out.println("============= 1 ARGS ============= ");
            setInputFilePath(args[0]);
            setProfileNameSuffix("");
            setTimeOffsetSec(3 * 60); // 3 min
        }
        if (args.length == 2) {
            System.out.println("============= 2 ARGS ============= ");
            setInputFilePath(args[0]);
            setProfileNameSuffix(args[1]);
            setTimeOffsetSec(3 * 60); // 3 min
        }

        if (args.length == 3) {
            System.out.println("============= 3 ARGS ============= ");
            setInputFilePath(args[0]);
            setProfileNameSuffix(args[1]);
            setTimeOffsetSec(Integer.parseInt(args[2]) * 60); // 3 minutes in seconds
        }

        if (args.length == 4) {
            System.out.println("============= 4 ARGS ============= ");
            setInputFilePath(args[0]);
            setProfileNameSuffix(args[1]);
            setTimeOffsetSec(Integer.parseInt(args[2]) * 60);
            setExtraFilename(args[3]);
        }

        if (args.length == 6 || args.length == 7) {
            System.out.println("============= 6-7 ARGS ============= ");
            setInputFilePath(args[0]);
            setProfileNameSuffix(args[1]);
            setTimeOffsetSec(Integer.parseInt(args[2]) * 60);
            setExtraFilename(args[3]);

            setCommand(args[4]);
            System.out.println("============= " + getCommand() + " " + args[5]);

            if (getCommand().toLowerCase().equals("corr")) {
                setC2FitFileDistanceStartCorrection(Integer.valueOf(args[5]));

            } else if (getCommand().toLowerCase().equals("wkt")) {
                setStartWithWktStep(args[5]);
                setNewWktName(args[6]);
            }
        }

        setInputFilePath(getFilePathPrefix() + getInputFilePath());
        setInputFilePath(PehoUtils.checkFile(getInputFilePath()));
        
        System.out.println("+++++++++++++++++++++++++++++++++++");
        System.out.println("filePathPrefix: " + getFilePathPrefix());
        System.out.println("profileNameSuffix: " + getProfileNameSuffix());
        System.out.println("inputFilePath: " + getInputFilePath());
        System.out.println("extraFilename: " + getExtraFilename());
        System.out.println("timeOffsetSec: " + getTimeOffsetSec());
        System.out.println("command: " + getCommand());
        System.out.println("startWithWktStep: " + getStartWithWktStep());
        System.out.println("newWktName: " + getNewWktName());
        System.out.println("C2FitFileDistanceStartCorrection: " + getC2FitFileDistanceStartCorrection());
        System.out.println("useManualC2SyncSeconds: " + getUseManualC2SyncSeconds());
        System.out.println("c2SyncSecondsC2File: " + getC2SyncSecondsC2File());
        System.out.println("c2SyncSecondsLapDistCalc: " + getC2SyncSecondsLapDistCalc());
        System.out.println("+++++++++++++++++++++++++++++++++++");

        System.out.println("Input file used: " + getInputFilePath());


    } //constructor conf
} //class conf
