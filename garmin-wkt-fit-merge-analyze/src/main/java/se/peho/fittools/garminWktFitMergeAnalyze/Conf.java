package se.peho.fittools.garminWktFitMergeAnalyze;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Conf {
    String filename = "conf.txt";
    
    String temp = FitFile.checkFile(filename);
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
