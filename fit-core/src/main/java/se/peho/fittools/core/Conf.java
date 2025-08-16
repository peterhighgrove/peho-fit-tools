package se.peho.fittools.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Conf {
    String filename = "conf.txt";
    //String temp = FitFile.checkFile(filename);
    //String filename = "C:/Users/peter/Downloads/conf.txt";
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
	
    // Getters and setters

    public String getFilePathPrefix() {
        return filePathPrefix;
    }

    public void setFilePathPrefix(String filePathPrefix) {
        this.filePathPrefix = filePathPrefix;
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public String getExtraFilename() {
        return extraFilename;
    }

    public void setExtraFilename(String extraFilename) {
        this.extraFilename = extraFilename;
    }

    public String getProfileNameSuffix() {
        return profileNameSuffix;
    }

    public void setProfileNameSuffix(String profileNameSuffix) {
        this.profileNameSuffix = profileNameSuffix;
    }

    public int getTimeOffsetSec() {
        return timeOffsetSec;
    }

    public void setTimeOffsetSec(int timeOffsetSec) {
        this.timeOffsetSec = timeOffsetSec;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getStartWithWktStep() {
        return startWithWktStep;
    }

    public void setStartWithWktStep(String startWithWktStep) {
        this.startWithWktStep = startWithWktStep;
    }

    public String getNewWktName() {
        return newWktName;
    }

    public void setNewWktName(String newWktName) {
        this.newWktName = newWktName;
    }

    public int getC2FitFileDistanceStartCorrection() {
        return C2FitFileDistanceStartCorrection;
    }

    public void setC2FitFileDistanceStartCorrection(int c2FitFileDistanceStartCorrection) {
        this.C2FitFileDistanceStartCorrection = c2FitFileDistanceStartCorrection;
    }

    public String getUseManualC2SyncSeconds() {
        return useManualC2SyncSeconds;
    }

    public void setUseManualC2SyncSeconds(String useManualC2SyncSeconds) {
        this.useManualC2SyncSeconds = useManualC2SyncSeconds;
    }

    public int getC2SyncSecondsC2File() {
        return c2SyncSecondsC2File;
    }

    public void setC2SyncSecondsC2File(int c2SyncSecondsC2File) {
        this.c2SyncSecondsC2File = c2SyncSecondsC2File;
    }

    public int getC2SyncSecondsLapDistCalc() {
        return c2SyncSecondsLapDistCalc;
    }

    public void setC2SyncSecondsLapDistCalc(int c2SyncSecondsLapDistCalc) {
        this.c2SyncSecondsLapDistCalc = c2SyncSecondsLapDistCalc;
    }

	public Conf () {

		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
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
							case "timeOffsetSec":
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
    } //method conf
} //class conf
