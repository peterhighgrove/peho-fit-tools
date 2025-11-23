package se.peho.fittools.garminWktFitMergeAnalyze;
import com.garmin.fit.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Enumeration;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
public class FitFile {

    String product = "";
    String manufacturer;
    Float swVer;
    Sport sport;
    SubSport subsport;
    String sportProfile;
    String wktName;
    Float totalTimerTime; //ActivityMesg, excl pauses
    Float totalDistance;
    Float avgSpeed; // m/s
    Float maxSpeed; // m/s
    int avgCadence;
    int maxCadence;
    int avgPower;
    int maxPower;

    int numberOfLaps;
    int numberOfRecords;
    DateTime timeFirstRecord;
    DateTime timeLastRecord;
    int numberOfDevFields;
    String devAppToRemove = "9a0508b9-0256-4639-88b3-a2690a14ddf9";
    //List <Integer> devFieldsToRemove = Arrays.asList("Strokes", "Calories", "Distance", "Speed", "Power", 2, 6, 7);
    List <Integer> devFieldsToRemove = Arrays.asList(10, 11, 12, 23, 1, 2, 6, 7);
    List <String> devFieldNamesToUpdate = Arrays.asList("Training_session", "MaxHRevenLaps");

    //Boolean isEllipticalFile = false;
    //Boolean hasManualLapsFile = false;

    //Boolean isSkiErgFile = false;
    //Boolean hasC2File = false;
    //Boolean hasDevAppData = false;
    //Boolean isC2File = false;
    
    int i;
    FileInputStream in;
    Decode decode;
    MesgBroadcaster broadcaster;

    List<FileIdMesg> fileIdRecords = new ArrayList<>();
    List<FileCreatorMesg> fileCreatorRecords = new ArrayList<>();
    List<ActivityMesg> activityRecords = new ArrayList<>();
    List<DeviceInfoMesg> deviceInfoRecords = new ArrayList<>();
    List<UserProfileMesg> userProfileRecords = new ArrayList<>();
    List<MaxMetDataMesg> maxMetDataRecords = new ArrayList<>();
    List<MetZoneMesg> metZoneRecords = new ArrayList<>();
    List<GoalMesg> goalRecords = new ArrayList<>();
    List<WorkoutMesg> wktRecords = new ArrayList<>();
    List<WorkoutSessionMesg> wktSessionRecords = new ArrayList<>();
    List<ZonesTargetMesg> zonesTargetRecords = new ArrayList<>();
    List<WorkoutStepMesg> wktStepRecords = new ArrayList<>();
    List<EventMesg> eventRecords = new ArrayList<>();
    List<DeveloperDataIdMesg> devDataIdRecords = new ArrayList<>();
    List<DeveloperFieldDescription> devFieldDescrRecords = new ArrayList<>();
    List<DeveloperFieldDefinition> devFieldDefRecords = new ArrayList<>();
    List<FieldDescriptionMesg> fieldDescrRecords = new ArrayList<>();
    List<SessionMesg> sessionRecords = new ArrayList<>();
    List<LapMesg> lapRecords = new ArrayList<>();
    List<LapExtraMesg> lapExtraRecords = new ArrayList<>();
    List<RecordMesg> secRecords = new ArrayList<>();
    List<RecordExtraMesg> secExtraRecords = new ArrayList<>();

    SimpleDateFormat sweDateTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    DateTimeFormatter sweDateTime2 = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
    ZoneId defaultZoneId = ZoneId.systemDefault();

    int maxIxFixEmptyBeginning = 100;
    int maxCadenceValue = 74;
    boolean lookingInBeginningForEmptySpeed = true;
    boolean lookingInBeginningForEmptyCadence = true;
    boolean lookingInBeginningForEmptyPower = true;
    boolean lookingInBeginningForEmptyStrokeLength = true;
    boolean lookingInBeginningForEmptyDragFactor = true;
    boolean lookingInBeginningForEmptyTrainingSession = true;
    
    Float activeTime = 0f;
    Float restTime = 0f;
    Float activeDist = 0f;
    Float restDist = 0f;

    Float activeSumSpeed = 0f;
    Float activeSumCad = 0f;
    Float activeSumPower = 0f;
    Float restSumSpeed = 0f;
    Float restSumCad = 0f;
    Float restSumPower = 0f;

    Float activeAvgSpeed = 0f;
    Float activeAvgCad = 0f;
    Float activeAvgPower = 0f;
    Float restAvgSpeed = 0f;
    Float restAvgCad = 0f;
    Float restAvgPower = 0f;

    Float activeFakeSumSpeed = 0f;
    Float activeFakeSumCad = 0f;
    Float activeFakeSumPower = 0f;
    
    int c2SyncSecondsLapDistCalc = 0; // for distance, speed
    int c2SyncSecondsC2File = 0; // for power, cadence

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    class RecordExtraMesg {
        int lapNo;
        DateTime C2DateTime;

        public RecordExtraMesg(int lapNo, DateTime C2DateTime) {
            this.lapNo = lapNo;
            this.C2DateTime = C2DateTime;
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    class LapExtraMesg {
        int hrStart;
        int hrEnd;
        int hrMin;
        DateTime timeEnd;
        int lapNo;
        int recordIxStart;
        int recordIxEnd;
        Float stepLen;
        Float level;
        Float avgStrokeLen;
        Float maxStrokeLen;
        Float avgDragFactor;
        Float maxDragFactor;

        public LapExtraMesg(int hrStart, int hrEnd, int hrMin, DateTime timeEnd, int lapNo, int recordIxStart, 
                int recordIxEnd, Float stepLen, Float level, Float avgStrokeLen, Float maxStrokeLen, Float avgDragFactor, Float maxDragFactor) {
            this.hrStart = hrStart;
            this.hrEnd = hrEnd;
            this.hrMin = hrMin;
            this.timeEnd = timeEnd;
            this.lapNo = lapNo;
            this.recordIxStart = recordIxStart;
            this.recordIxEnd = recordIxEnd;
            this.stepLen = stepLen;
            this.level = level;
            this.avgStrokeLen = avgStrokeLen;
            this.maxStrokeLen = maxStrokeLen;
            this.avgDragFactor = avgDragFactor;
            this.maxDragFactor = maxDragFactor;
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public FitFile (int syncSecC2File, int syncSecLapDistCalc) {
    	this.c2SyncSecondsC2File = syncSecC2File;
    	this.c2SyncSecondsLapDistCalc = syncSecLapDistCalc;
    }
    public FitFile () {
    	
    }
    
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String checkFile(String fitFilePath) {
        File newFitFile = new File(fitFilePath);
        System.out.println("=========> EXTENSION: " + getFileExtension(newFitFile));
        if (getFileExtension(newFitFile).equals("zip")) {
            fitFilePath = unzip(newFitFile);
            newFitFile = new File(fitFilePath);
        }
        if (!newFitFile.exists()) {
            System.out.println("**********************");
            System.out.println(getFileExtension(newFitFile).toUpperCase()+" FILE DO NOT EXIST: "+newFitFile);
            System.out.println("**********************");
            System.exit(0);
        }
        return fitFilePath;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String getFileExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf(".");
        
        if (lastDotIndex == -1 || lastDotIndex == 0) {
            return ""; // No extension found
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String unzip(File zipFile) {
        if (!zipFile.exists() || !zipFile.isFile()) {
            System.out.println("**********************");
            System.out.println("NO ZIP file.");
            System.out.println("**********************");
            System.exit(0);
        }

        String destDirectory = zipFile.getParent(); // Extract to same directory
        String unzippedFile = "";
        File destDir = new File(destDirectory);

        try (FileInputStream fis = new FileInputStream(zipFile);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(destDir, entry.getName());
                unzippedFile = newFile.getPath();
                System.out.println("=========> FILENAME IN ZIP: " + unzippedFile);

                if (entry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs(); // Ensure parent exists
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return unzippedFile;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void initLapExtraRecords() {

        int hrStart = 0;
        int hrEnd = 0;
        int hrMin = 9999;
        DateTime timeEnd = null;
        int lapNo = 0;
        int recordIxStart = 0;
        int recordIxEnd = 0;
        Float stepLen = null;
        Float level = 0f;
        Float avgStrokeLen = 0f;
        Float maxStrokeLen = 0f;
        Float avgDragFactor = 0f;
        Float maxDragFactor = 0f;

        for (LapMesg record : lapRecords) {
            lapExtraRecords.add(new LapExtraMesg(hrStart, hrEnd, hrMin, timeEnd, recordIxStart, recordIxEnd, lapNo, stepLen, level, avgStrokeLen, maxStrokeLen, avgDragFactor, maxDragFactor));
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void wktAddSteps(String wktSteps, String wktName) {

        System.out.println("---------> WKT COMMAND MADE!");

        // ADD wktRecord if EMPTY
        //----------------------
        if (wktRecords.isEmpty()) {
            WorkoutMesg wktRecord = new WorkoutMesg();
            wktRecords.add(wktRecord);
            System.out.println("---------> NO wktRecord, ADDING!");
        }

        // ADD wktName if not empty in arguments
        //----------------------
        if (!wktName.equals("")) {
            wktRecords.get(0).setWktName(wktName);
        }

        // ADD wktSession Record if EMPTY
        //----------------------
        if (wktSessionRecords.isEmpty()) {
            WorkoutSessionMesg wktSessionRecord = new WorkoutSessionMesg();
            wktSessionRecords.add(wktSessionRecord);
            System.out.println("---------> NO wktSession, ADDING!");
        }

        // ADD wktStepRecords if EMPTY
        //----------------------
        if (wktStepRecords.isEmpty()) {
            for (LapMesg record : lapRecords) {
                WorkoutStepMesg wktStepRecord = new WorkoutStepMesg();
                wktStepRecords.add(wktStepRecord);
            }
            System.out.println("---------> NO wktSteps, ADDING!");
        }

        System.out.println("---------> StepIntensity changing to: " + wktSteps);

        int recordIx = 0;
        for (LapMesg record : lapRecords) {
            switch (wktSteps.toLowerCase()) {

                case "allactive":
                    record.setIntensity(Intensity.ACTIVE);
                    if (recordIx == 0) {
                        record.setIntensity(Intensity.WARMUP);
                    }
                    break;

                case "warmupthenactive":
                    if (((recordIx+1) % 2) == 0) {
                        record.setIntensity(Intensity.ACTIVE);
                    }
                    if (((recordIx+1) % 2) == 1) {
                        record.setIntensity(Intensity.RECOVERY);
                    }
                    if (recordIx == 0) {
                        record.setIntensity(Intensity.WARMUP);
                    }
                    if (recordIx+1 == lapRecords.size()) {
                        record.setIntensity(Intensity.COOLDOWN);
                    }
                    break;

                case "restthenactive":
                    if (((recordIx+1) % 2) == 0) {
                        record.setIntensity(Intensity.ACTIVE);
                    }
                    if (((recordIx+1) % 2) == 1) {
                        record.setIntensity(Intensity.RECOVERY);
                    }
                    if ((recordIx+1) == lapRecords.size()) {
                        record.setIntensity(Intensity.COOLDOWN);
                    }
                    break;

                case "activethenrest":
                    if (((recordIx+1) % 2) == 0) {
                        record.setIntensity(Intensity.RECOVERY);
                    }
                    if (((recordIx+1) % 2) == 1) {
                        record.setIntensity(Intensity.ACTIVE);
                    }
                    if ((recordIx+1) == lapRecords.size()) {
                        record.setIntensity(Intensity.COOLDOWN);
                    }
                    break;

                case "nochange":
                    break;

                default:
                    System.out.println("==========> NO CORRRECT wkt command. Allowed: allActive, warmupThenActive, restThenActive, activeThenRest, noChange");
            }
            recordIx++;
        }

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static void renameFile(String oldName, String newName) {
        if (oldName.equals(newName)) {
        	System.out.println("===FILE ALREADY CORRECT NAME: "+oldName);
        	return ;
        }
        boolean success;
    	File oldNameFile = new File(oldName);
    	File newNameFile = new File(newName);
    	if (newNameFile.exists()) {
    		// Rename file (or directory)
    		File newNameBackup = new File(newName + "-backup");
			success = newNameFile.renameTo(newNameBackup);
			
            System.out.println("============== RENAME BACKUP SUCCESS? " + success +" from:"+newNameFile.getPath()+ " to:" + newNameBackup.getPath());
			if (!success) {
			   // File was not successfully renamed
			}
    	}
    	success = oldNameFile.renameTo(newNameFile);
		
        System.out.println("============== RENAME SUCCESS? " + success +" from:"+oldNameFile.getPath()+ " to:" + newNameFile.getPath());
        if (!success) {
            // File was not successfully renamed
        }
    }
    	
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String getFilenameAndSetNewSportProfileName(String suffix, String outputFile, int hoursToAdd) {
        String newProfileName = sessionRecords.get(0).getSportProfileName();
        newProfileName = newProfileName.replace(" (bike)","");

        newProfileName = newProfileName.replace("spinbike","SBike");
        newProfileName = newProfileName.replace("SpinBike","SBike");
        newProfileName = newProfileName.replace("Spin","SpinBike");
        newProfileName = newProfileName.replace("SBike","SpinBike");
        newProfileName = newProfileName.replace("Cykel inne","SpinBike");

        newProfileName = newProfileName.replace("Elliptical","CT");
        newProfileName = newProfileName.replace("Ellipt","Elliptical");
        newProfileName = newProfileName.replace("CT","Elliptical");
        newProfileName = newProfileName.replace("ct","Elliptical");

        if (wktRecords.isEmpty()) {
            System.out.println("========> NO wkt RECORDS");
            System.exit(0);
        }
        if (wktStepRecords.isEmpty()) {
            System.out.println("========> NO wkt STEP RECORDS");
            System.exit(0);
        }

        if (wktRecords.get(0).getWktName() == null) {
            wktRecords.get(0).setWktName(wktRecords.get(0).getWktName() + "");
            System.out.println("================ wktName == NULL");
        }
        String newWktName = wktRecords.get(0).getWktName();
        newWktName = newWktName.replace("Bike ","");
        newWktName = newWktName.replace(" (bike)","");
        newWktName = newWktName.replace("HR","");

        sessionRecords.get(0).setSportProfileName(newProfileName + " " + newWktName + " " + ((float) (Math.round(totalDistance/100))/10) + "km " + suffix);
        System.out.println("----> SportProfile: "+sessionRecords.get(0).getSportProfileName());

        outputFile = sessionRecords.get(0).getSportProfileName();
        outputFile = outputFile.replace("/","!");
        outputFile = outputFile.replace("×","x");
        String[] parts = timeFirstRecord.toString().split(" "); // Split w space divider
        String yyyy = parts[5] + "-";
        String mm = "";
        switch (parts[1]) {
            case "Jan":
                mm = "01";
                break;
            case "Feb":
                mm = "02";
                break;
            case "Mar":
                mm = "03";
                break;
            case "Apr":
                mm = "04";
                break;
            case "May":
                mm = "05";
                break;
            case "Jun":
                mm = "06";
                break;
            case "Jul":
                mm = "07";
                break;
            case "Aug":
                mm = "08";
                break;
            case "Sep":
                mm = "09";
                break;
            case "JOct":
                mm = "10";
                break;
            case "Nov":
                mm = "11";
                break;
            case "Dec":
                mm = "12";
                break;
        }
        mm = mm + "-";
        String dd = parts[2] + "-";
        String[] timeParts = parts[3].split(":"); // Split time with :
        int hhInt = Integer.valueOf(timeParts[0])+hoursToAdd; 
        String hh = ""; 
        if (hhInt < 10) {
            hh = "0" + hhInt + "-";
        } else {
            hh = hhInt + "-";
        }
        String min = timeParts[1] + "-"; 
        String ss = timeParts[2] + "-"; 

        outputFile = yyyy + mm + dd + hh + min + ss + outputFile;
        System.err.println("----> Filename: " + outputFile);
        return outputFile;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void addDevFieldDescr() {
        byte[] appId = new byte[]{
            0x1, 0x1, 0x2, 0x3,
            0x5, 0x8, 0xD, 0x15,
            0x22, 0x37, 0x59, (byte) 0x90,
            (byte) 0xE9, 0x79, 0x62, (byte) 0xDB
        };

        DeveloperDataIdMesg developerIdMesg = new DeveloperDataIdMesg();
        for (int i = 0; i < appId.length; i++) {
            developerIdMesg.setApplicationId(i, appId[i]);
        }
        developerIdMesg.setDeveloperDataIndex((short)2);
        developerIdMesg.setApplicationVersion((long)100);
        devDataIdRecords.add(developerIdMesg);

        FieldDescriptionMesg activeTimeFieldDescriptionMesg = new FieldDescriptionMesg();
        activeTimeFieldDescriptionMesg.setDeveloperDataIndex((short)2);
        activeTimeFieldDescriptionMesg.setFieldDefinitionNumber((short)0);
        activeTimeFieldDescriptionMesg.setFitBaseTypeId((short)Fit.BASE_TYPE_UINT8);
        activeTimeFieldDescriptionMesg.setFieldName(0, "ActiveTime");
        activeTimeFieldDescriptionMesg.setUnits(0, "min");
        fieldDescrRecords.add(activeTimeFieldDescriptionMesg);

        DeveloperField activeTimeField = new DeveloperField(activeTimeFieldDescriptionMesg, developerIdMesg);

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void renameDevFieldName() {
        for (FieldDescriptionMesg record : fieldDescrRecords) {
            if (devFieldNamesToUpdate.contains(record.getFieldName())) {
                record.setFieldName(0, "ActiveTime");
            }
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void removeDevFieldDescr() {
        System.out.println("-REMOVE DEV FIELDS --------------------------------------");
        numberOfDevFields = devFieldDescrRecords.size();
        System.out.println("--- No of Dev Fields: " + numberOfDevFields);

        short newIx = 0;
        for (int Ix = 0; Ix <= numberOfDevFields-1; Ix++) {
            //System.out.println("-LOOP1 Ix: "+Ix+" of "+devFieldDescrRecords.get(Ix).getApplicationId().toString());
            if (devFieldDescrRecords.get(Ix).getApplicationId().toString().equals(devAppToRemove)) {
                //System.out.println("--LOOP2 Ix: "+Ix+" of "+devFieldDescrRecords.get(Ix).getApplicationId().toString());
                if (devFieldsToRemove.contains((int) fieldDescrRecords.get(Ix).getFieldDefinitionNumber())) {
                    //System.out.println("---LOOP3 Ix: "+Ix+" of "+numberOfDevFields);
                    fieldDescrRecords.remove(Ix);
                    devFieldDescrRecords.remove(Ix);
                    Ix--;
                    numberOfDevFields--;
                } else {
                    //fieldDescrRecords.get(Ix).setFieldDefinitionNumber(newIx);
                    newIx++;
                }
            }
        }
        numberOfDevFields = devFieldDescrRecords.size();
        System.out.println("--- No of Dev Fields: " + numberOfDevFields);
        System.out.println("--------------------------------------------------");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void addDeveloperfields() {

        // ITS NOT possible to get dev fields to display in GC if not defined with a CIQ APP

        int recordIx = 0;
        int lapIx = 0;
        int lapNo = 1;
        long currentTimeStamp = 0;
        long nextLapStartTime = 0;
        Float currentLapTime = 0f;
        String currentLapIntensity = "";


        byte[] appId = new byte[]{
            0x2, 0x1, 0x2, 0x3,
            0x5, 0x8, 0xD, 0x15,
            0x22, 0x37, 0x59, (byte) 0x90,
            (byte) 0xE9, 0x79, 0x62, (byte) 0xDB
        };

        DeveloperDataIdMesg developerIdMesg = new DeveloperDataIdMesg();
        for (int i = 0; i < appId.length; i++) {
            developerIdMesg.setApplicationId(i, appId[i]);
        }
        developerIdMesg.setDeveloperDataIndex((short)2);
        developerIdMesg.setApplicationVersion((long)100);
        devDataIdRecords.add(developerIdMesg);

        FieldDescriptionMesg activeTimeFieldDescriptionMesg = new FieldDescriptionMesg();
        activeTimeFieldDescriptionMesg.setDeveloperDataIndex((short)2);
        activeTimeFieldDescriptionMesg.setFieldDefinitionNumber((short)3);
        activeTimeFieldDescriptionMesg.setFitBaseTypeId((short)136);
        activeTimeFieldDescriptionMesg.setFieldName(0, "ActiveTime");
        activeTimeFieldDescriptionMesg.setUnits(0, "min");
        fieldDescrRecords.add(activeTimeFieldDescriptionMesg);
        
        for (RecordMesg record : secRecords) {

            currentLapTime = lapRecords.get(secExtraRecords.get(recordIx).lapNo).getTotalTimerTime(); // in sec
            currentLapIntensity = Intensity.getStringFromValue(lapRecords.get(secExtraRecords.get(recordIx).lapNo).getIntensity());


            // ADD NEW DEV FIELD
            DeveloperField activeTimeField = new DeveloperField(activeTimeFieldDescriptionMesg, developerIdMesg);
            record.addDeveloperField(activeTimeField);
            if (currentLapIntensity.equals("ACTIVE")) {
                activeTimeField.setValue(currentLapTime/60);
            } else {
                activeTimeField.setValue(0f);
            }
            recordIx++;
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void changeDeveloper() {
        int recordIx = 0;
        for (FieldDescriptionMesg record : fieldDescrRecords){

            if (record.getDeveloperDataIndex() == 1) {
                record.setDeveloperDataIndex((short) (0));
            }
            recordIx++;
        }
        }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String mps2kmph3(Float speed) {
        Float newSpeed = ((float) Math.round(speed * 3600 / 1000 *1000)/1000);
        return "" + newSpeed;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String mps2kmph2(Float speed) {
        Float newSpeed = ((float) Math.round(speed * 3600 / 1000 *100)/100);
        return "" + newSpeed;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String mps2kmph1(Float speed) {
        Float newSpeed = ((float) Math.round(speed * 3600 / 1000 *10)/10);
        return "" + newSpeed;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String m2km1(Float speed) {
        String speedStr = String.valueOf(Float.valueOf(Math.round(speed / 1000 *10))/10);
        return speedStr;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String mps2minpkm(Float speed) {
        String speedStr = "";
        if (speed.equals(0)) {
            speedStr = "-";
        } else {
            speedStr = sec2minSecLong(1 / (speed / 1000));
        }
        return speedStr;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String mps2minp500m(Float speed) {
        String speedStr = "";
        if (speed.equals(0)) {
            speedStr = "-";
        } else {
            speedStr = sec2minSecLong(1 / (speed / 1000) / 2);
        }
        return speedStr;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String sec2minSecShort(Float seconds) {
        int min = (int) (seconds / 60);
        int sec = (int) (Math.round((seconds / 60 - min) * 60));
        if (sec == 60) {
            min++;
            sec = 0;
        }
        String minStr = String.valueOf(min);
        if (sec == 0) {
            minStr += "";
        } else if (sec < 10) {
            minStr += ":0" + String.valueOf(sec);
        } else {
            minStr += ":" + String.valueOf(sec);
        }
        return minStr;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String sec2minSecLong(Float seconds) {
        int min = (int) (seconds / 60);
        int sec = (int) (Math.round((seconds / 60 - min) * 60));
        if (sec == 60) {
            min++;
            sec = 0;
        }
        String minStr = String.valueOf(min);
        if (sec < 10) {
            minStr += ":0" + String.valueOf(sec);
        } else {
            minStr += ":" + String.valueOf(sec);
        }
        return minStr;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public Boolean isSkiErgFile() {
        //System.out.println("======== isSkiErgFile TEST ==========");
        Boolean isTrue = false;
        if (sportProfile.toLowerCase().contains("skierg")
            ) {
                isTrue = true;
        }
        return isTrue;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public Boolean isTreadmillFile() {
        //System.out.println("======== isSkiErgFile TEST ==========");
        Boolean isTrue = false;
        if (sportProfile.toLowerCase().contains("löpband") 
            || sportProfile.contains("treadmill")
            ) {
                isTrue = true;
        }
        return isTrue;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void checkC2StartDist () {
        // sdsd
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public Boolean hasC2FitFile(String filename) {
        Boolean isTrue = false;
        try {
            // Verify the file exists and is a valid FIT file
            File file1 = new File(filename);
            if (!file1.exists()) { // || !file1.isTrue()
                System.err.println("==========> c2FitFile not found: " + filename);
                System.exit(0);
            }
        } catch (Exception e) {
            throw new RuntimeException("==========> Error opening c2FitFile: " + filename);
        }
        isTrue = true;
        return isTrue;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void setNewSportSkiErg() {
        sport = Sport.FITNESS_EQUIPMENT;
        subsport = SubSport.INDOOR_ROWING;
        wktRecords.get(0).setSport(sport);
        wktRecords.get(0).setSubSport(subsport);
        sessionRecords.get(0).setSport(sport);
        sessionRecords.get(0).setSubSport(subsport);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void mergeC2CiqAndFitData(FitFile c2FitFile, int C2FitFileDistanceStartCorrection) {
        
        Float currentDist = 0f;
        Float currentDistBack1 = 0f;
        Float currentDistBack2 = 0f;
        Float currentSpeed = 0f;
        Float currentStrokeLength = 0f;
        Float currentDragFactor = 0f;
        Float currentTrainingSession = 0f;

        int lapNo = 1; // only for INIT of secExtraRecords for now
        DateTime C2DateTime = null;

        int recordIx = 0;
        int c2RecordIx = 0;
        RecordMesg recordNext;
        Float currentDistNext = 0f;
        Float newDistStep = 0f;
        int noneC2dataCounter = 0;
        int pauseRecordCounter = 0;
        int sameDistCounter = 1;
        
        for (RecordMesg record : secRecords) {

            //--------------
            // Initiate secExtraRecords
            secExtraRecords.add(new RecordExtraMesg(lapNo, C2DateTime));
            
            //--------------
            // Look for HR drop outs
            
            if (record.getHeartRate() == null) {
            	System.out.println(">>>>>>> HR EMPTY.  recordIx:"+recordIx);
            	if (recordIx>0) {
            		record.setHeartRate(secRecords.get(recordIx-1).getHeartRate());
            	}
            }

            // =========== MERGE/Import CIQ data to native =============
            // =========================================================
            for (DeveloperField field : record.getDeveloperFields()) {
                /*
                Power:0, 9a0508b9-0256-4639-88b3-a2690a14ddf9, 0, 1
                , Cadence:0, 9a0508b9-0256-4639-88b3-a2690a14ddf9, 0, 2
                , Speed:2.543, 9a0508b9-0256-4639-88b3-a2690a14ddf9, 0, 6
                , Distance:17, 9a0508b9-0256-4639-88b3-a2690a14ddf9, 0, 7
                , StrokeLength:0.53, 9a0508b9-0256-4639-88b3-a2690a14ddf9, 0, 8
                , DragFactor:81.0, 9a0508b9-0256-4639-88b3-a2690a14ddf9, 0, 9
                , Training_session:0.0, 03dc80ed-6991-40b0-a0cb-23925913a501, 1, 1
                 */
                if (field.getName().equals("Distance")) {
                    currentDistBack2 = currentDistBack1;
                    currentDistBack1 = currentDist;
                    currentDist = field.getFloatValue();
                    record.setDistance(currentDist);
                    field.setValue(0f);
                }
                if (field.getName().equals("Speed")) {
                    currentSpeed = field.getFloatValue();
                    record.setSpeed(currentSpeed);
                    record.setEnhancedSpeed(currentSpeed);
                    field.setValue(0f);
                }
                if (field.getName().equals("StrokeLength")) {
                    currentStrokeLength = field.getFloatValue();
                }
                if (field.getName().equals("DragFactor")) {
                    currentDragFactor = field.getFloatValue();
                }
            }

            // =========== Distance Smoothing =============
            // Smoothing distance records if 2 following record are the same, then calc avg for the one before and after the 2
            // ============================================
            if (recordIx >= 3 && recordIx < numberOfRecords-2) {
                if (record.getDistance().equals(secRecords.get(recordIx-1).getDistance())) {
                    //System.out.println("==========> Same dist in a row: " + recordIx + ", " + sameDistCounter + " @ " + currentDist + ", " + record.getTimestamp());
                    if (sameDistCounter>1) {
                        System.out.println("==========> MORE 1 Same dist in a row: " + recordIx + ", " + sameDistCounter + " @ " + currentDist + ", " + record.getTimestamp());
                    }
                    recordNext = secRecords.get(recordIx+1);
                    for (DeveloperField fieldRecordNext : recordNext.getDeveloperFields()) {
                        if (fieldRecordNext.getName().equals("Distance")) {
                            currentDistNext = fieldRecordNext.getFloatValue();
                        }
                    }
                    newDistStep = (currentDistNext - secRecords.get(recordIx-2).getDistance()) / 3;
                    secRecords.get(recordIx-1).setDistance(secRecords.get(recordIx-2).getDistance() + newDistStep);
                    record.setDistance(secRecords.get(recordIx-2).getDistance() + newDistStep*2);
                    //System.out.println("-------->" + secRecords.get(recordIx-2).getDistance() + " " + currentDistBack1 + "->" + secRecords.get(recordIx-1).getDistance() + " " + currentDist + "->" + record.getDistance() + " " + currentDistNext);
                    sameDistCounter++;
                }
                else {
                    sameDistCounter = 1;
                }
            }

            // =========== MERGE/Import C2 fitfile =============
            // =================================================
            while (c2FitFile.secRecords.get(c2RecordIx).getDistance()-0.5 <= record.getDistance()-C2FitFileDistanceStartCorrection) { // -0 = TEMP FIX
                record.setCadence(c2FitFile.secRecords.get(c2RecordIx).getCadence());
                record.setPower(c2FitFile.secRecords.get(c2RecordIx).getPower());
                secExtraRecords.get(recordIx).C2DateTime = c2FitFile.secRecords.get(c2RecordIx).getTimestamp();
                c2RecordIx++;
                if (c2RecordIx > c2FitFile.numberOfRecords-1) {
                    c2RecordIx--;
                    break;
                }
            }
            // =========== Fix EMPTY beginning of data ================
            // ========================================================
            if (numberOfRecords < maxIxFixEmptyBeginning) {
                maxIxFixEmptyBeginning = numberOfRecords - 1;
            }
            if (recordIx <= maxIxFixEmptyBeginning) {
                // FIX SPEED
                if (lookingInBeginningForEmptySpeed) {
                    if (record.getSpeed()!=0 && record.getSpeed()!=null) {
                        for (int i = recordIx; i >= 0; i--) {
                            secRecords.get(i).setSpeed(record.getSpeed());
                            secRecords.get(i).setEnhancedSpeed(record.getSpeed());
                        }
                        System.err.println("========= FIXED Beginning SPEED, first value: " + record.getSpeed() + " @ " + recordIx);
                        lookingInBeginningForEmptySpeed = false;
                    }
                }
                // FIX CADENCE
                if (lookingInBeginningForEmptyCadence) {
                    if ((record.getCadence()!=null && record.getCadence()!=0)) {
                        for (int i = recordIx-1; i >= 0; i--) {
                            secRecords.get(i).setCadence(record.getCadence());
                        }
                        System.out.println("========= FIXED Beginning CADENCE, first value: " + record.getCadence() + " @ " + recordIx);
                        lookingInBeginningForEmptyCadence = false;
                    }
                }
                // FIX POWER
                if (lookingInBeginningForEmptyPower) {
                    if ((record.getPower()!=null && record.getPower()!=0)) {
                        for (int i = recordIx-1; i >= 0; i--) {
                            secRecords.get(i).setPower(record.getPower());
                        }
                        System.out.println("========= FIXED Beginning POWER, first value: " + record.getPower() + " @ " + recordIx);
                        lookingInBeginningForEmptyPower = false;
                    }
                }
                // FIX STROKE LENGTH
                if (lookingInBeginningForEmptyStrokeLength) {
                    if ((currentStrokeLength!=null && currentStrokeLength!=0)) {
                        for (int i = recordIx-1; i >= 0; i--) {
                            for (DeveloperField field : secRecords.get(i).getDeveloperFields()) {
                                if (field.getName().equals("StrokeLength")) {
                                    field.setValue(currentStrokeLength);
                                }
                            }
                        }
                        System.out.println("========= FIXED Beginning STROKE LENGTH, first value: " + currentStrokeLength + " @ " + recordIx);
                        lookingInBeginningForEmptyStrokeLength = false;
                    }
                }
                // FIX DRAG FACTOR
                if (lookingInBeginningForEmptyDragFactor) {
                    if ((currentDragFactor!=null && (currentDragFactor!=1 && currentDragFactor!=0))) {
                        for (int i = recordIx-1; i >= 0; i--) {
                            for (DeveloperField field : secRecords.get(i).getDeveloperFields()) {
                                if (field.getName().equals("DragFactor")) {
                                    field.setValue(currentDragFactor);
                                }
                            }
                        }
                        System.out.println("========= FIXED Beginning DRAG FACTOR, first value: " + currentDragFactor + " @ " + recordIx);
                        lookingInBeginningForEmptyDragFactor = false;
                    }
                }
                // FIX TRAINING_SESSION
                if (lookingInBeginningForEmptyTrainingSession) {
                    if ((currentTrainingSession!=null && currentTrainingSession!=1)) {
                        for (int i = recordIx-1; i >= 0; i--) {
                            for (DeveloperField field : secRecords.get(i).getDeveloperFields()) {
                                if (field.getName().equals("Training_session")) {
                                    field.setValue(currentTrainingSession);
                                }
                            }
                        }
                        System.out.println("========= FIXED Beginning TRAINING_SESSION, first value: " + currentTrainingSession + " @ " + recordIx);
                        lookingInBeginningForEmptyTrainingSession = false;
                    }
                }
            }
            // =========== CHANGE TO 0 VALUES WHEN PAUSED ============
            // =================================================
            if (!lookingInBeginningForEmptyCadence) {
                if (record.getCadence() == null) {
                //if (secExtraRecords.get(recordIx).C2DateTime == null) {
                    noneC2dataCounter++;
                    if (noneC2dataCounter >= 7) {
                        System.out.println("============ C2time null >= 7 times. "+ noneC2dataCounter + "recordIx: " + recordIx);
                        pauseRecordCounter++;
                        record.setSpeed(0f);
                        record.setEnhancedSpeed(0f);
                        record.setCadence((short) (0));
                        record.setPower(0);
                        if (noneC2dataCounter == 7 && recordIx > 4) {
                            System.out.println("===>>>>>>>>> START PAUSE C2time null 7 times. recordIx: " + recordIx);
                            secRecords.get(recordIx-1).setSpeed(0f);
                            secRecords.get(recordIx-1).setEnhancedSpeed(0f);
                            secRecords.get(recordIx-1).setCadence((short) (0));
                            secRecords.get(recordIx-1).setPower(0);
                            secRecords.get(recordIx-2).setSpeed(0f);
                            secRecords.get(recordIx-2).setEnhancedSpeed(0f);
                            secRecords.get(recordIx-2).setCadence((short) (0));
                            secRecords.get(recordIx-2).setPower(0);
                            secRecords.get(recordIx-3).setSpeed(0f);
                            secRecords.get(recordIx-3).setEnhancedSpeed(0f);
                            secRecords.get(recordIx-3).setCadence((short) (0));
                            secRecords.get(recordIx-3).setPower(0);
                        }
                    } else {

                    }
                } else {
                    if (pauseRecordCounter > 0 ) {
                        System.out.println("============ Pause END. recordIx: " + recordIx + " pauseCounter: " + pauseRecordCounter + " " + noneC2dataCounter);
                    }
                    pauseRecordCounter = 0;
                    noneC2dataCounter = 0;
                }
            }

            // =========== Fix GAPS fromC2 fitfile import =============
            // ========================================================
            if (record.getCadence() == null) {
                record.setCadence(secRecords.get(recordIx-1).getCadence());
            }
            if (record.getPower() == null) {
                record.setPower(secRecords.get(recordIx-1).getPower());
            }
            // || ((record.getCadence()-secRecords.get(recordIx-1).getCadence()) > 7)
            // =========== Fix BAD PEAK data ================
            // ==============================================
            if (recordIx>0) {
            if ((record.getCadence() > maxCadenceValue)) {
                System.out.println("=======>>> Fixed Cadence PEAK from: " + secRecords.get(recordIx-1).getCadence() + "->" + record.getCadence());
                record.setCadence(secRecords.get(recordIx-1).getCadence());
            }
            }

            recordIx++;
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void SyncDataInTimeFromSkiErg() {
         
        int tempC2SyncSecondsC2File = 0;
        int tempC2SyncSecondsLapDistCalc = 0;
         
        Float lastActiveFakeSumSpeed = 0f;
        Float lastActiveFakeSumCad = 0f;
        Float lastActiveFakeSumPower = 0f;

        Float maxActiveLapAvgPower = 0f;
         
        for (i=0; i<10; i++) {
           
            activeFakeSumSpeed = 0f;
            activeFakeSumCad = 0f;
            activeFakeSumPower = 0f;

            maxActiveLapAvgPower = 0f;

			tempC2SyncSecondsC2File = i;
			tempC2SyncSecondsLapDistCalc = i;
			
            int recordIx = 0;
            int lapIx = 0;
            int lapNo = 1;
            long currentTimeStamp = 0;
            long nextLapStartTime = 0;
            Float currentLapTime = 0f;
            String currentLapIntensity = "";
            long currentLapTimeEnd = 0;
            Float lastLapTotalDistance = 0f;

            int currentLapSumCadence = 0;
            int currentLapSumPower = 0;
            Float currentLapSumStrokeLen = 0f;
            Float currentLapSumDragFactor = 0f;
            Float currentLapMaxStrokeLen = 0f;
            Float currentLapMaxDragFactor = 0f;

            int currentSessionSumCadence = 0;
            int currentSessionSumPower = 0;

            nextLapStartTime = lapRecords.get(0).getStartTime().getTimestamp();

            sessionRecords.get(0).setMaxSpeed(0f);
            sessionRecords.get(0).setEnhancedMaxSpeed(0f);
            sessionRecords.get(0).setMaxCadence((short) 0);
            sessionRecords.get(0).setMaxPower(0);


            for (RecordMesg record : secRecords) {

                //--------------
                // IF LAP START
                currentTimeStamp = record.getTimestamp().getTimestamp();
                if ( currentTimeStamp == nextLapStartTime ) {

                    // Initiate maxSpeed with 0 to be able to compare later
                    lapRecords.get(lapIx).setMaxSpeed(0f);
                    lapRecords.get(lapIx).setEnhancedMaxSpeed(0f);
                    lapRecords.get(lapIx).setMaxCadence((short) 0);
                    lapRecords.get(lapIx).setMaxPower(0);
                    
                    // Save HR and recordIx START
                    lapExtraRecords.get(lapIx).hrStart = record.getHeartRate();
                    lapExtraRecords.get(lapIx).recordIxStart = recordIx;

                    // Get LAP DATA to be used to find lap-start-end
                    currentLapTime = lapRecords.get(lapIx).getTotalTimerTime(); // in sec
                    currentLapIntensity = Intensity.getStringFromValue(lapRecords.get(lapIx).getIntensity());
                    if (lapNo < numberOfLaps) {
                        currentLapTimeEnd = lapRecords.get(lapIx+1).getStartTime().getTimestamp()-1;
                        nextLapStartTime = lapRecords.get(lapIx+1).getStartTime().getTimestamp();
                    } else {
                        currentLapTimeEnd = timeLastRecord.getTimestamp();
                    }
                    // Save LAP END to table
                    lapExtraRecords.get(lapIx).timeEnd = new DateTime(currentLapTimeEnd);
                }

                // Calc LAP HR min
                if (record.getHeartRate() == null) {
                    if (recordIx > 0) {
                        record.setHeartRate(secRecords.get(recordIx-1).getHeartRate());
                    } else {
                        record.setHeartRate((short) 60);
                    }
                }
                if ( record.getHeartRate() < lapExtraRecords.get(lapIx).hrMin ) {
                    lapExtraRecords.get(lapIx).hrMin = record.getHeartRate();
                }

                //--------------
                // Calculate LAP MAX
                if (record.getEnhancedSpeed() > lapRecords.get(lapIx).getEnhancedMaxSpeed()) {
                    lapRecords.get(lapIx).setEnhancedMaxSpeed(record.getEnhancedSpeed());
                    lapRecords.get(lapIx).setMaxSpeed(record.getEnhancedSpeed());
                }
                
                //--------------
                // Calc LAPSUM MAX CAD POWER
                if (recordIx<(numberOfRecords-tempC2SyncSecondsC2File-1)) {
                    currentLapSumCadence += secRecords.get(recordIx+tempC2SyncSecondsC2File).getCadence();
                    currentLapSumPower += secRecords.get(recordIx+tempC2SyncSecondsC2File).getPower();
                }
                if (record.getCadence() > lapRecords.get(lapIx).getMaxCadence()) {
                    lapRecords.get(lapIx).setMaxCadence(record.getCadence());
                }
                if (record.getPower() > lapRecords.get(lapIx).getMaxPower()) {
                    lapRecords.get(lapIx).setMaxPower(record.getPower());
                }

                for (DeveloperField field : secRecords.get(recordIx).getDeveloperFields()) {
                    if (field.getName().equals("StrokeLength")) {
                        currentLapSumStrokeLen += field.getFloatValue();
                        if (field.getFloatValue() > currentLapMaxStrokeLen) {
                            currentLapMaxStrokeLen = field.getFloatValue();
                        }
                    }
                    if (field.getName().equals("DragFactor")) {
                        currentLapSumDragFactor += field.getFloatValue();
                        if (field.getFloatValue() > currentLapMaxDragFactor) {
                            currentLapMaxDragFactor = field.getFloatValue();
                        }
                    }
                }

                //--------------
                // Calculate SESSION SUM & MAX
                if (record.getEnhancedSpeed() > sessionRecords.get(0).getEnhancedMaxSpeed()) {
                    sessionRecords.get(0).setEnhancedMaxSpeed(record.getEnhancedSpeed());
                    sessionRecords.get(0).setMaxSpeed(record.getEnhancedSpeed());
                }
                currentSessionSumCadence += record.getCadence();
                if (record.getCadence() > sessionRecords.get(0).getMaxCadence()) {
                    sessionRecords.get(0).setMaxCadence(record.getCadence());
                }
                currentSessionSumPower += record.getPower();
                if (record.getPower() > sessionRecords.get(0).getMaxPower()) {
                    sessionRecords.get(0).setMaxPower(record.getPower());
                }

                // LAPTIME for active laps TO CIQ defined in devFieldNamesToRename 
                for (DeveloperField field : record.getDeveloperFields()) {
                    if (devFieldNamesToUpdate.contains(field.getName())) {
                        if (currentLapIntensity.equals("ACTIVE")) {
                            field.setValue(currentLapTime/60);
                        } else {
                            field.setValue(0f);
                        }
                    }
                }

                //--------------
                // IF LAP END
                if ( currentTimeStamp == currentLapTimeEnd ) {

                    // Save HR and recordIx END
                    lapExtraRecords.get(lapIx).hrEnd = record.getHeartRate();
                    lapExtraRecords.get(lapIx).recordIxEnd = recordIx;
                    lapExtraRecords.get(lapIx).timeEnd = record.getTimestamp();

                    // Calc LAP DISTANCE & AVG SPEED
                    if (lapNo == numberOfLaps) {
                        tempC2SyncSecondsLapDistCalc=0;
                    }
                        lapRecords.get(lapIx).setTotalDistance((float) secRecords.get(recordIx+tempC2SyncSecondsLapDistCalc).getDistance() - lastLapTotalDistance);
                        lastLapTotalDistance = secRecords.get(recordIx+tempC2SyncSecondsLapDistCalc).getDistance();

                        lapRecords.get(lapIx).setAvgSpeed((float) (lapRecords.get(lapIx).getTotalDistance() / lapRecords.get(lapIx).getTotalTimerTime()));
                        //System.out.println("--- lap: " + lapNo + " dist: " + lapRecords.get(lapIx).getTotalDistance() + " time: " + lapRecords.get(lapIx).getTotalTimerTime() + " speed: "+ (float) (lapRecords.get(lapIx).getAvgSpeed())+" "+(float) (lapRecords.get(lapIx).getTotalDistance() / lapRecords.get(lapIx).getTotalTimerTime()));
                        lapRecords.get(lapIx).setEnhancedAvgSpeed((float) (lapRecords.get(lapIx).getTotalDistance() / lapRecords.get(lapIx).getTotalTimerTime()));
                    
                    // Calc LAP SUM & LAP MAX
                    lapRecords.get(lapIx).setAvgCadence((short) Math.round((float) currentLapSumCadence / (recordIx-lapExtraRecords.get(lapIx).recordIxStart+1)));
                    currentLapSumCadence = 0;

                    lapRecords.get(lapIx).setAvgPower(Math.round((float) currentLapSumPower / (recordIx-lapExtraRecords.get(lapIx).recordIxStart+1)));
                    currentLapSumPower = 0;

                    for (DeveloperField field : secRecords.get(recordIx).getDeveloperFields()) {
                        if (field.getName().equals("StrokeLength")) {
                            lapExtraRecords.get(lapIx).avgStrokeLen = (float) Math.round(100 * currentLapSumStrokeLen / (recordIx-lapExtraRecords.get(lapIx).recordIxStart+1)) /100;
                            lapExtraRecords.get(lapIx).maxStrokeLen = currentLapMaxStrokeLen;
                            currentLapSumStrokeLen = 0f;
                            currentLapMaxStrokeLen = 0f;
                        }
                        if (field.getName().equals("DragFactor")) {
                            lapExtraRecords.get(lapIx).avgDragFactor = (float) Math.round(100 * currentLapSumDragFactor / (recordIx-lapExtraRecords.get(lapIx).recordIxStart+1)) /100;
                            lapExtraRecords.get(lapIx).maxDragFactor = currentLapMaxDragFactor;
                            currentLapSumDragFactor = 0f;
                            currentLapMaxDragFactor = 0f;
                        }
                    }

                    //--------------
                    // Calculate ACTIVE LAP SUM & MAX
                    if (currentLapIntensity.equals("ACTIVE")) {
                        activeTime = activeTime + (float) (lapRecords.get(lapIx).getTotalTimerTime());
                        activeDist = activeDist + (float) (lapRecords.get(lapIx).getTotalDistance());
                        activeSumSpeed = activeSumSpeed + (float) lapRecords.get(lapIx).getAvgSpeed() * lapRecords.get(lapIx).getTotalTimerTime();
                        activeSumCad = activeSumCad + (float) (lapRecords.get(lapIx).getAvgCadence() * lapRecords.get(lapIx).getTotalTimerTime());
                        activeSumPower = activeSumPower + (float) (lapRecords.get(lapIx).getAvgPower() * lapRecords.get(lapIx).getTotalTimerTime());

                        if ((float) lapRecords.get(lapIx).getAvgSpeed() > activeFakeSumSpeed) {
                            activeFakeSumSpeed = (float) lapRecords.get(lapIx).getAvgSpeed();
                        }
                        if ((float) lapRecords.get(lapIx).getAvgCadence() > activeFakeSumCad) {
                            activeFakeSumCad = (float) lapRecords.get(lapIx).getAvgCadence();
                        }
                        if ((float) lapRecords.get(lapIx).getAvgPower() > activeFakeSumPower) {
                            activeFakeSumPower = (float) lapRecords.get(lapIx).getAvgPower();
                        }
                        //activeFakeSumSpeed =activeFakeSumSpeed+ (float) lapRecords.get(lapIx).getAvgSpeed();
                        //activeFakeSumCad =activeFakeSumCad+ (float) lapRecords.get(lapIx).getAvgCadence();
                        //activeFakeSumPower =activeFakeSumPower+ (float) lapRecords.get(lapIx).getAvgPower();

                        if (lapRecords.get(lapIx).getAvgPower() > maxActiveLapAvgPower) {
                            maxActiveLapAvgPower = (float) lapRecords.get(lapIx).getAvgPower();
                        }
                    }
                    // Calculate REST LAP SUM & MAX
                    if (currentLapIntensity.equals("REST") || currentLapIntensity.equals("RECOVERY")) {
                        restTime = restTime + (float) (lapRecords.get(lapIx).getTotalTimerTime());
                        restDist = restDist + (float) (lapRecords.get(lapIx).getTotalDistance());
                        restSumSpeed = restSumSpeed + (float) lapRecords.get(lapIx).getAvgSpeed() * lapRecords.get(lapIx).getTotalTimerTime();
                        restSumCad = restSumCad + (float) (lapRecords.get(lapIx).getAvgCadence() * lapRecords.get(lapIx).getTotalTimerTime());
                        restSumPower = restSumPower + (float) (lapRecords.get(lapIx).getAvgPower() * lapRecords.get(lapIx).getTotalTimerTime());
                    }

                    lapIx++;
                    lapNo++;
                } // IF LAP END END

                recordIx++;
            }  // FOR LOOP END

            lapIx--;
            lapNo--;
            recordIx--;
            
            // First DISTANCE to ZERO
            secRecords.get(0).setDistance(0f);

            // TOTAL DISTANCE activity
            totalDistance = secRecords.get(recordIx).getDistance();
            sessionRecords.get(0).setTotalDistance(totalDistance);

            // TOTAL AVG SPEED activity
            avgSpeed = (float) (totalDistance / totalTimerTime);
            sessionRecords.get(0).setAvgSpeed(avgSpeed);
            sessionRecords.get(0).setEnhancedAvgSpeed(avgSpeed);

            // TOTAL AVG CADENCE activity
            avgCadence = Math.round((float) currentSessionSumCadence / (recordIx));
            sessionRecords.get(0).setAvgCadence((short) avgCadence);

            // TOTAL AVG POWER activity
            avgPower = Math.round((float) currentSessionSumPower / (recordIx));
            sessionRecords.get(0).setAvgPower(avgPower);

            // Calculate ACTIVE LAP SUM & MAX
            activeAvgSpeed = (float) (activeSumSpeed / activeTime);
            activeAvgCad = (float) (activeSumCad / activeTime);
            activeAvgPower = (float) (activeSumPower / activeTime);

            // Calculate REST LAP SUM & MAX
            restAvgSpeed = (float) (restSumSpeed / restTime);
            restAvgCad = (float) (restSumCad / restTime);
            restAvgPower = (float) (restSumPower / restTime);

			if (activeFakeSumSpeed > lastActiveFakeSumSpeed) {
				c2SyncSecondsLapDistCalc = i;
				lastActiveFakeSumSpeed = activeFakeSumSpeed;
			} 
			if (activeFakeSumPower > lastActiveFakeSumPower) {
				c2SyncSecondsC2File = i;
				lastActiveFakeSumPower = activeFakeSumPower;
			} 
			
            System.out.println("_____ i: "+i+" MAXsp: "+activeFakeSumSpeed+" cad: "+activeFakeSumCad+" pow: "+activeFakeSumPower);
            
        } // FOR LOOP

        System.out.println("______ before phase shifting - syncSeconds C2 (pow, cad): "+c2SyncSecondsC2File+" lapdist (speed): "+c2SyncSecondsLapDistCalc);
        System.out.println("------------------------------------------------------------------");

        int recordIx = 0;

        for (RecordMesg record : secRecords) {

            if (recordIx<(numberOfRecords-1-c2SyncSecondsLapDistCalc)) {
                record.setDistance(secRecords.get(recordIx+c2SyncSecondsLapDistCalc).getDistance());
                record.setSpeed(secRecords.get(recordIx+c2SyncSecondsLapDistCalc).getSpeed());
                record.setEnhancedSpeed(secRecords.get(recordIx+c2SyncSecondsLapDistCalc+1).getEnhancedSpeed());
            } else {
                record.setDistance(secRecords.get(numberOfRecords-1).getDistance());
                record.setSpeed(secRecords.get(numberOfRecords-1).getSpeed());
                record.setEnhancedSpeed(secRecords.get(numberOfRecords-1).getEnhancedSpeed());
            }

            if (recordIx<(numberOfRecords-1-c2SyncSecondsC2File)) {
                record.setCadence(secRecords.get(recordIx+c2SyncSecondsC2File).getCadence());
                record.setPower(secRecords.get(recordIx+c2SyncSecondsC2File).getPower());
            } else {
                record.setCadence(secRecords.get(numberOfRecords-1).getCadence());
                record.setPower(secRecords.get(numberOfRecords-1).getPower());
            }

            recordIx++;
        }

        c2SyncSecondsLapDistCalc = 0;
        c2SyncSecondsC2File = 0;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void calcLapDataFromSecRecordsSkiErg() {
        int recordIx = 0;
        int lapIx = 0;
        int lapNo = 1;
        long currentTimeStamp = 0;
        long nextLapStartTime = 0;
        Float currentLapTime = 0f;
        String currentLapIntensity = "";
        long currentLapTimeEnd = 0;
        Float lastLapTotalDistance = 0f;

        int currentLapSumCadence = 0;
        int currentLapSumPower = 0;
        Float currentLapSumStrokeLen = 0f;
        Float currentLapSumDragFactor = 0f;
        Float currentLapMaxStrokeLen = 0f;
        Float currentLapMaxDragFactor = 0f;

        int currentSessionSumCadence = 0;
        int currentSessionSumPower = 0;
        
        activeFakeSumSpeed = 0f;
        activeFakeSumCad = 0f;
        activeFakeSumPower = 0f;

        nextLapStartTime = lapRecords.get(0).getStartTime().getTimestamp();

        sessionRecords.get(0).setMaxSpeed(0f);
        sessionRecords.get(0).setEnhancedMaxSpeed(0f);
        sessionRecords.get(0).setMaxCadence((short) 0);
        sessionRecords.get(0).setMaxPower(0);
        
        System.out.println("______ before calcLapData - syncSeconds C2 (paw, cad): "+c2SyncSecondsC2File+" dist (speed): "+c2SyncSecondsLapDistCalc);

        for (RecordMesg record : secRecords) {

            //--------------
            // IF LAP START
            currentTimeStamp = record.getTimestamp().getTimestamp();
            if ( currentTimeStamp == nextLapStartTime ) {
                //System.out.println("Lapstart " + lapIx + "  " + lapExtraRecords.get(lapIx).recordIxStart + "  " + recordIx);

                // Initiate maxSpeed with 0 to be able to compare later
                lapRecords.get(lapIx).setMaxSpeed(0f);
                lapRecords.get(lapIx).setEnhancedMaxSpeed(0f);
                lapRecords.get(lapIx).setMaxCadence((short) 0);
                lapRecords.get(lapIx).setMaxPower(0);
                
                // Save HR and recordIx START
                lapExtraRecords.get(lapIx).hrStart = record.getHeartRate();
                lapExtraRecords.get(lapIx).recordIxStart = recordIx;

                // Get LAP DATA to be used to find lap-start-end
                currentLapTime = lapRecords.get(lapIx).getTotalTimerTime(); // in sec
                currentLapIntensity = Intensity.getStringFromValue(lapRecords.get(lapIx).getIntensity());
                if (lapNo < numberOfLaps) {
                    currentLapTimeEnd = lapRecords.get(lapIx+1).getStartTime().getTimestamp()-1;
                    nextLapStartTime = lapRecords.get(lapIx+1).getStartTime().getTimestamp();
                } else {
                    currentLapTimeEnd = timeLastRecord.getTimestamp();
                }
                // Save LAP END to table
                lapExtraRecords.get(lapIx).timeEnd = new DateTime(currentLapTimeEnd);
            }

            // Calc LAP HR min
            if (record.getHeartRate() == null) {
                if (recordIx > 0) {
                    record.setHeartRate(secRecords.get(recordIx-1).getHeartRate());
                } else {
                    record.setHeartRate((short) 60);
                }
            }
            if ( record.getHeartRate() < lapExtraRecords.get(lapIx).hrMin ) {
                lapExtraRecords.get(lapIx).hrMin = record.getHeartRate();
            }

            //--------------
            // Calculate LAP MAX
            if (record.getEnhancedSpeed() > lapRecords.get(lapIx).getEnhancedMaxSpeed()) {
                lapRecords.get(lapIx).setEnhancedMaxSpeed(record.getEnhancedSpeed());
                lapRecords.get(lapIx).setMaxSpeed(record.getEnhancedSpeed());
                //System.out.println("-----recIx:"+recordIx+" lapIx:"+lapIx+" Sp:"+record.getEnhancedSpeed()+"m/s "+mps2kmph(record.getEnhancedSpeed())+"km/h");
            }
            
            //--------------
            // Calc LAPSUM MAX CAD POWER
            if (recordIx<(numberOfRecords-c2SyncSecondsC2File-1)) {
                currentLapSumCadence += secRecords.get(recordIx+c2SyncSecondsC2File).getCadence();
                currentLapSumPower += secRecords.get(recordIx+c2SyncSecondsC2File).getPower();
            }
            if (record.getCadence() > lapRecords.get(lapIx).getMaxCadence()) {
                lapRecords.get(lapIx).setMaxCadence(record.getCadence());
            }
            if (record.getPower() > lapRecords.get(lapIx).getMaxPower()) {
                lapRecords.get(lapIx).setMaxPower(record.getPower());
            }

            for (DeveloperField field : secRecords.get(recordIx).getDeveloperFields()) {
                if (field.getName().equals("StrokeLength")) {
                    currentLapSumStrokeLen += field.getFloatValue();
                    if (field.getFloatValue() > currentLapMaxStrokeLen) {
                        currentLapMaxStrokeLen = field.getFloatValue();
                    }
                }
                if (field.getName().equals("DragFactor")) {
                    currentLapSumDragFactor += field.getFloatValue();
                    if (field.getFloatValue() > currentLapMaxDragFactor) {
                        currentLapMaxDragFactor = field.getFloatValue();
                    }
                }
            }

            //--------------
            // Calculate SESSION SUM & MAX
            if (record.getEnhancedSpeed() > sessionRecords.get(0).getEnhancedMaxSpeed()) {
                sessionRecords.get(0).setEnhancedMaxSpeed(record.getEnhancedSpeed());
                sessionRecords.get(0).setMaxSpeed(record.getEnhancedSpeed());
                //System.out.println("-----recIx:"+recordIx+" 0:"+0+" Sp:"+record.getEnhancedSpeed()+"m/s "+mps2kmph(record.getEnhancedSpeed())+"km/h");
            }
            currentSessionSumCadence += record.getCadence();
            if (record.getCadence() > sessionRecords.get(0).getMaxCadence()) {
                sessionRecords.get(0).setMaxCadence(record.getCadence());
            }
            currentSessionSumPower += record.getPower();
            if (record.getPower() > sessionRecords.get(0).getMaxPower()) {
                sessionRecords.get(0).setMaxPower(record.getPower());
            }

            // LAPTIME for active laps TO CIQ defined in devFieldNamesToRename 
            for (DeveloperField field : record.getDeveloperFields()) {
                if (devFieldNamesToUpdate.contains(field.getName())) {
                    if (currentLapIntensity.equals("ACTIVE")) {
                        field.setValue(currentLapTime/60);
                    } else {
                        field.setValue(0f);
                    }
                }
            }

            //--------------
            // IF LAP END
            if ( currentTimeStamp == currentLapTimeEnd ) {
                //System.out.println("LapEND " + lapIx + "  " + lapExtraRecords.get(lapIx).recordIxStart + "  " + recordIx);

                // Save HR and recordIx END
                lapExtraRecords.get(lapIx).hrEnd = record.getHeartRate();
                lapExtraRecords.get(lapIx).recordIxEnd = recordIx;
                lapExtraRecords.get(lapIx).timeEnd = record.getTimestamp();

                // Calc LAP DISTANCE & AVG SPEED
                if (lapNo == numberOfLaps) {
                    c2SyncSecondsLapDistCalc=0;
                }
                    lapRecords.get(lapIx).setTotalDistance((float) 
                        secRecords.get(recordIx+c2SyncSecondsLapDistCalc).getDistance() - lastLapTotalDistance);
                    lastLapTotalDistance = secRecords.get(recordIx+c2SyncSecondsLapDistCalc).getDistance();

                    lapRecords.get(lapIx).setAvgSpeed((float) 
                        (lapRecords.get(lapIx).getTotalDistance() / lapRecords.get(lapIx).getTotalTimerTime()));
                    //System.out.println("--- lap: " + lapNo + " dist: " + lapRecords.get(lapIx).getTotalDistance() + " time: " + lapRecords.get(lapIx).getTotalTimerTime() + " speed: "+ (float) (lapRecords.get(lapIx).getAvgSpeed())+" "+(float) (lapRecords.get(lapIx).getTotalDistance() / lapRecords.get(lapIx).getTotalTimerTime()));
                    lapRecords.get(lapIx).setEnhancedAvgSpeed((float) 
                        (lapRecords.get(lapIx).getTotalDistance() / lapRecords.get(lapIx).getTotalTimerTime()));
                /*} else {
                    // LAP DIST & AVG SPEED last lap
                    lapRecords.get(lapIx).setTotalDistance((float) secRecords.get(recordIx).getDistance() - lastLapTotalDistance);
                    lapRecords.get(lapIx).setAvgSpeed((float) lapRecords.get(lapIx).getTotalDistance() / lapRecords.get(lapIx).getTotalTimerTime());
                    lapRecords.get(lapIx).setEnhancedAvgSpeed((float) lapRecords.get(lapIx).getTotalDistance() / lapRecords.get(lapIx).getTotalTimerTime());
                }*/
                
                // Calc LAP SUM & LAP MAX
                lapRecords.get(lapIx).setAvgCadence((short) 
                    Math.round((float) currentLapSumCadence / (recordIx-lapExtraRecords.get(lapIx).recordIxStart+1)));
                currentLapSumCadence = 0;

                lapRecords.get(lapIx).setAvgPower(
                    Math.round((float) currentLapSumPower / (recordIx-lapExtraRecords.get(lapIx).recordIxStart+1)));
                currentLapSumPower = 0;

                for (DeveloperField field : secRecords.get(recordIx).getDeveloperFields()) {
                    if (field.getName().equals("StrokeLength")) {
                        lapExtraRecords.get(lapIx).avgStrokeLen = (float) Math.round(100 * currentLapSumStrokeLen / (recordIx-lapExtraRecords.get(lapIx).recordIxStart+1)) /100;
                        lapExtraRecords.get(lapIx).maxStrokeLen = currentLapMaxStrokeLen;
                        currentLapSumStrokeLen = 0f;
                        currentLapMaxStrokeLen = 0f;
                    }
                    if (field.getName().equals("DragFactor")) {
                        lapExtraRecords.get(lapIx).avgDragFactor = (float) Math.round(100 * currentLapSumDragFactor / (recordIx-lapExtraRecords.get(lapIx).recordIxStart+1)) /100;
                        lapExtraRecords.get(lapIx).maxDragFactor = currentLapMaxDragFactor;
                        currentLapSumDragFactor = 0f;
                        currentLapMaxDragFactor = 0f;
                    }
                }

                //--------------
                // Calculate ACTIVE LAP SUM & MAX
                if (currentLapIntensity.equals("ACTIVE")) {
                    activeTime = activeTime + (float) (lapRecords.get(lapIx).getTotalTimerTime());
                    activeDist = activeDist + (float) (lapRecords.get(lapIx).getTotalDistance());
                    activeSumSpeed = activeSumSpeed + (float) lapRecords.get(lapIx).getAvgSpeed() * lapRecords.get(lapIx).getTotalTimerTime();
                    activeSumCad = activeSumCad + (float) (lapRecords.get(lapIx).getAvgCadence() * lapRecords.get(lapIx).getTotalTimerTime());
                    activeSumPower = activeSumPower + (float) (lapRecords.get(lapIx).getAvgPower() * lapRecords.get(lapIx).getTotalTimerTime());

                    activeFakeSumSpeed = activeFakeSumSpeed + (float) lapRecords.get(lapIx).getAvgSpeed();
                    activeFakeSumCad = activeFakeSumCad + (float) lapRecords.get(lapIx).getAvgCadence();
                    activeFakeSumPower = activeFakeSumPower + (float) lapRecords.get(lapIx).getAvgPower();
                }
                // Calculate REST LAP SUM & MAX
                if (currentLapIntensity.equals("REST") || currentLapIntensity.equals("RECOVERY")) {
                    restTime = restTime + (float) (lapRecords.get(lapIx).getTotalTimerTime());
                    restDist = restDist + (float) (lapRecords.get(lapIx).getTotalDistance());
                    restSumSpeed = restSumSpeed + (float) lapRecords.get(lapIx).getAvgSpeed() * lapRecords.get(lapIx).getTotalTimerTime();
                    restSumCad = restSumCad + (float) (lapRecords.get(lapIx).getAvgCadence() * lapRecords.get(lapIx).getTotalTimerTime());
                    restSumPower = restSumPower + (float) (lapRecords.get(lapIx).getAvgPower() * lapRecords.get(lapIx).getTotalTimerTime());
                }

                lapIx++;
                lapNo++;
            } // IF LAP END END

            recordIx++;
        }  // FOR LOOP END

        lapIx--;
        lapNo--;
        recordIx--;
        
        // First DISTANCE to ZERO
        secRecords.get(0).setDistance(0f);

        // TOTAL DISTANCE activity
        totalDistance = secRecords.get(recordIx).getDistance();
        sessionRecords.get(0).setTotalDistance(totalDistance);

        // TOTAL AVG SPEED activity
        avgSpeed = (float) (totalDistance / totalTimerTime);
        sessionRecords.get(0).setAvgSpeed(avgSpeed);
        sessionRecords.get(0).setEnhancedAvgSpeed(avgSpeed);

        // TOTAL AVG CADENCE activity
        avgCadence = Math.round((float) currentSessionSumCadence / (recordIx));
        sessionRecords.get(0).setAvgCadence((short) avgCadence);

        // TOTAL AVG POWER activity
        avgPower = Math.round((float) currentSessionSumPower / (recordIx));
        sessionRecords.get(0).setAvgPower(avgPower);

        // Calculate ACTIVE LAP SUM & MAX
        activeAvgSpeed = (float) (activeSumSpeed / activeTime);
        activeAvgCad = (float) (activeSumCad / activeTime);
        activeAvgPower = (float) (activeSumPower / activeTime);

        // Calculate REST LAP SUM & MAX
        restAvgSpeed = (float) (restSumSpeed / restTime);
        restAvgCad = (float) (restSumCad / restTime);
        restAvgPower = (float) (restSumPower / restTime);

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void addFixedValueForCadence(Short fixedCadence) {
        for (RecordMesg record : secRecords) {
            if (record.getCadence() == null || record.getCadence() == 0) {
                record.setCadence(fixedCadence);
            }
        } 
        for (LapMesg lapRecord : lapRecords) {
            if (lapRecord.getAvgCadence() == null || lapRecord.getAvgCadence() == 0) {
                lapRecord.setAvgCadence(fixedCadence);
                lapRecord.setMaxCadence(fixedCadence);
            }
        }
        for (SessionMesg sessionRecord : sessionRecords) {
            if (sessionRecord.getAvgCadence() == null || sessionRecord.getAvgCadence() == 0) {
                sessionRecord.setAvgCadence(fixedCadence);
                sessionRecord.setMaxCadence(fixedCadence);
            }
        }
        System.out.println("===== Added FIXED CADENCE value: " + fixedCadence + " =====");

        
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public Boolean isEllipticalFile() {
        //System.out.println("======== isEllipticalFile TEST ==========");
        Boolean isTrue = false;
        if (sportProfile.toLowerCase().contains("ellipt")
            || sportProfile.toLowerCase().contains("gymbike")
            || sportProfile.toLowerCase().contains("spinbike")
            || sportProfile.toLowerCase().contains("ct")) {
                isTrue = true;
        }
        return isTrue;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public Boolean hasManualLapsFile(String manualLapsFilename) {
        Boolean isTrue = false;
        try {
            // Verify the file exists and is a valid FIT file
            File file1 = new File(manualLapsFilename);
            if (!file1.exists()) { // || !file1.isTrue()
                System.err.println("==========> ManualLapsFile not found: " + manualLapsFilename);
                System.exit(0);
            }
        } catch (Exception e) {
            throw new RuntimeException("==========> Error opening ManualLapsFile: " + manualLapsFilename);
        }
        isTrue = true;
        return isTrue;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void setNewSportElliptical() {
        sport = Sport.FITNESS_EQUIPMENT;
        subsport = SubSport.ELLIPTICAL;
        wktRecords.get(0).setSport(sport);
        wktRecords.get(0).setSubSport(subsport);
        sessionRecords.get(0).setSport(sport);
        sessionRecords.get(0).setSubSport(subsport);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void fixEmptyBeginningElliptical() {
        int recordIx = 0;

        for (RecordMesg record : secRecords) {

            // =========== Fix EMPTY beginning of data =============
            // 
            // ========================================================
            if (numberOfRecords < maxIxFixEmptyBeginning) {
                maxIxFixEmptyBeginning = numberOfRecords - 1;
            }
            if (recordIx <= maxIxFixEmptyBeginning) {
                // FIX CADENCE
                if (lookingInBeginningForEmptyCadence) {
                    if ((record.getCadence()!=null && record.getCadence()!=0 && record.getCadence()>20)) {
                        for (int i = recordIx-1; i >= 0; i--) {
                            secRecords.get(i).setCadence(record.getCadence());
                        }
                        System.out.println("========= FIXED Beginning CADENCE, first value: " + record.getCadence() + " @ " + recordIx);
                        lookingInBeginningForEmptyCadence = false;
                    }
                }
            }
            recordIx++;
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void fixEmptyBeginningTreadmill() {
        int recordIx = 0;

        for (RecordMesg record : secRecords) {

            // =========== Fix EMPTY beginning of data =============
            // 
            // ========================================================
            if (numberOfRecords < maxIxFixEmptyBeginning) {
                maxIxFixEmptyBeginning = numberOfRecords - 1;
            }
            if (recordIx <= maxIxFixEmptyBeginning) {
                // FIX CADENCE
                if (lookingInBeginningForEmptyCadence) {
                    if (record.getCadence()!=null && record.getCadence()!=0 && record.getCadence()!=1 && record.getCadence()<100) {
                        for (int i = recordIx-1; i >= 0; i--) {
                            secRecords.get(i).setCadence(record.getCadence());
                        }
                        System.out.println("========= FIXED Beginning CADENCE, first value: " + record.getCadence() + " @ " + recordIx);
                        lookingInBeginningForEmptyCadence = false;
                    }
                }
                // FIX POWER
                if (lookingInBeginningForEmptyPower) {
                    if ((record.getPower()!=null && record.getPower()!=0)) {
                        for (int i = recordIx-1; i >= 0; i--) {
                            secRecords.get(i).setPower(record.getPower());
                        }
                        System.out.println("========= FIXED Beginning POWER, first value: " + record.getPower() + " @ " + recordIx);
                        lookingInBeginningForEmptyPower = false;
                    }
                }
            
            }
            recordIx++;
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void calcLapDataFromSecRecordsElliptical() {
        int recordIx = 0;
        int lapIx = 0;
        int lapNo = 1;
        DateTime C2DateTime = null; // NOT NEEDED for Elliptical, need to be initilized
        Float recordDist = 0f;
        Float lapSumOfRecordDist = 0f;
        Float sumOfRecordDist = 0f;
        Float corrPerMeter = 0f;
        long currentTimeStamp = 0;
        long nextLapStartTime = 0;
        Float currentLapTime = 0f;
        String currentLapIntensity = "";
        long currentLapTimeEnd = 0;

        nextLapStartTime = lapRecords.get(0).getStartTime().getTimestamp();

        sessionRecords.get(0).setMaxSpeed(0f);
        sessionRecords.get(0).setEnhancedMaxSpeed(0f);
        sessionRecords.get(0).setMaxCadence((short) 0);
        sessionRecords.get(0).setMaxPower(0);

        for (RecordMesg record : secRecords) {

            //--------------
            // Initiate secExtraRecords
            secExtraRecords.add(new RecordExtraMesg(lapNo, C2DateTime));

            //--------------
            // IF LAP START
            currentTimeStamp = record.getTimestamp().getTimestamp();
            if ( currentTimeStamp == nextLapStartTime ) {
                //System.out.println("Lapstart lapIx: " + lapIx + " recordIxStart: " + lapExtraRecords.get(lapIx).recordIxStart + " recordIx: " + recordIx);

                // Initiate maxSpeed with 0 to be able to compare later
                lapRecords.get(lapIx).setMaxSpeed(0f);
                lapRecords.get(lapIx).setEnhancedMaxSpeed(0f);
                
                // Save HR and recordIx START
                lapExtraRecords.get(lapIx).hrStart = record.getHeartRate();
                lapExtraRecords.get(lapIx).recordIxStart = recordIx;

                // Get LAP DATA to be used to find lap-start-end
                currentLapTime = lapRecords.get(lapIx).getTotalTimerTime(); // in sec
                currentLapIntensity = Intensity.getStringFromValue(lapRecords.get(lapIx).getIntensity());
                if (lapNo < numberOfLaps) {
                    currentLapTimeEnd = lapRecords.get(lapIx+1).getStartTime().getTimestamp()-1;
                    nextLapStartTime = lapRecords.get(lapIx+1).getStartTime().getTimestamp();
                } else {
                    currentLapTimeEnd = timeLastRecord.getTimestamp();
                }
                // Save LAP END to table
                lapExtraRecords.get(lapIx).timeEnd = new DateTime(currentLapTimeEnd);
            }

            //--------------
            // Calculate DIST between RECORDS based on Cadence
            if (record.getCadence() == null) {
                record.setCadence(secRecords.get(recordIx-1).getCadence());
            }
            recordDist = lapExtraRecords.get(lapIx).stepLen * record.getCadence() / 60;
            lapSumOfRecordDist += recordDist;
            sumOfRecordDist += recordDist;

            //--------------
            // Calc LAP HR min
            if (record.getHeartRate() == null) {
                record.setHeartRate(secRecords.get(recordIx-1).getHeartRate());
            }
            if ( record.getHeartRate() < lapExtraRecords.get(lapIx).hrMin ) {
                lapExtraRecords.get(lapIx).hrMin = record.getHeartRate();
            }

            //--------------
            // LEVEL TO CIQ Level 
            // LAPTIME for active laps TO CIQ TrainingSess 
            for (DeveloperField field : record.getDeveloperFields()) {
                if (field.getName().equals("Level")) {
                    field.setValue(lapExtraRecords.get(lapIx).level);
                }
                if (devFieldNamesToUpdate.contains(field.getName())) {
                    if (currentLapIntensity.equals("ACTIVE")) {
                        field.setValue(currentLapTime/60);
                    } else {
                        field.setValue(0f);
                    }
                }
            }

            //--------------
            // IF LAP END
            if ( currentTimeStamp == currentLapTimeEnd ) {
                //System.out.println("LapEND lapIx: " + lapIx + " recordIxStart: " + lapExtraRecords.get(lapIx).recordIxStart + " recordIx: " + recordIx);

                // Save HR and recordIx END
                lapExtraRecords.get(lapIx).hrEnd = record.getHeartRate();
                lapExtraRecords.get(lapIx).recordIxEnd = recordIx;

                //--------------
                // CORRECTION
                // INIT of Variables
                corrPerMeter = (lapSumOfRecordDist - lapRecords.get(lapIx).getTotalDistance()) / lapSumOfRecordDist;
                sumOfRecordDist = sumOfRecordDist - lapSumOfRecordDist;
                lapSumOfRecordDist = 0f;

                // CORRECTION RECAP LAP
                for (int j=lapExtraRecords.get(lapIx).recordIxStart; j<=lapExtraRecords.get(lapIx).recordIxEnd; j++) {
                    //System.out.println("   j:"+j+" lapix:"+lapIx);
                    recordDist = lapExtraRecords.get(lapIx).stepLen * secRecords.get(j).getCadence() / 60;
                    recordDist = recordDist - recordDist * corrPerMeter;
                    lapSumOfRecordDist += recordDist;
                    sumOfRecordDist += recordDist;
                    secRecords.get(j).setDistance(sumOfRecordDist);

                    if (j>0) {
                        secRecords.get(j).setSpeed((sumOfRecordDist - secRecords.get(j-1).getDistance()) / 1); // 1sec requirement
                        secRecords.get(j).setEnhancedSpeed((sumOfRecordDist - secRecords.get(j-1).getDistance()) / 1); // 1sec requirement
                        //secRecords.get(j).setEnhancedSpeed(secRecords.get(j).getSpeed());

                        //--------------
                        // Calculate LAP MAX
                        if (secRecords.get(j).getEnhancedSpeed() > lapRecords.get(lapIx).getEnhancedMaxSpeed()) {
                            lapRecords.get(lapIx).setEnhancedMaxSpeed(secRecords.get(j).getEnhancedSpeed());
                            lapRecords.get(lapIx).setMaxSpeed(secRecords.get(j).getEnhancedSpeed());
                            //System.out.println("-----recIx:"+recordIx+" lapIx:"+lapIx+" Sp:"+record.getEnhancedSpeed()+"m/s "+mps2kmph(record.getEnhancedSpeed())+"km/h");
                        }
                        //--------------
                        // Calculate SESSION MAX
                        if (secRecords.get(j).getEnhancedSpeed() > sessionRecords.get(0).getEnhancedMaxSpeed()) {
                            sessionRecords.get(0).setEnhancedMaxSpeed(secRecords.get(j).getEnhancedSpeed());
                            sessionRecords.get(0).setMaxSpeed(secRecords.get(j).getEnhancedSpeed());
                            //System.out.println("-----recIx:"+recordIx+" 0:"+0+" Sp:"+record.getEnhancedSpeed()+"m/s "+mps2kmph(record.getEnhancedSpeed())+"km/h");
                        }
                    }
                }

                //--------------
                // Calculate ACTIVE LAP SUM & MAX
                if (currentLapIntensity.equals("ACTIVE")) {
                    activeTime = activeTime + (lapRecords.get(lapIx).getTotalTimerTime());
                    activeDist = activeDist + (lapRecords.get(lapIx).getTotalDistance());
                    activeSumSpeed = activeSumSpeed + lapRecords.get(lapIx).getAvgSpeed() * lapRecords.get(lapIx).getTotalTimerTime();
                    activeSumCad = activeSumCad + (int) (lapRecords.get(lapIx).getAvgCadence() * lapRecords.get(lapIx).getTotalTimerTime());
                }
                // Calculate REST LAP SUM & MAX
                if (currentLapIntensity.equals("REST") || currentLapIntensity.equals("RECOVERY")) {
                    restTime = restTime + (lapRecords.get(lapIx).getTotalTimerTime());
                    restDist = restDist + (lapRecords.get(lapIx).getTotalDistance());
                    restSumSpeed = restSumSpeed + lapRecords.get(lapIx).getAvgSpeed() * lapRecords.get(lapIx).getTotalTimerTime();
                    restSumCad = restSumCad + (int) (lapRecords.get(lapIx).getAvgCadence() * lapRecords.get(lapIx).getTotalTimerTime());
                }

                lapSumOfRecordDist = 0f;
                lapIx++;
                lapNo++;
            }

            recordIx++;
        }

        // First DISTANCE to ZERO
        secRecords.get(0).setDistance(0f);
        // First SPEED to NULL
        secRecords.get(0).setSpeed(secRecords.get(1).getSpeed());
        secRecords.get(0).setEnhancedSpeed(secRecords.get(1).getSpeed());

        // Calculate ACTIVE LAP SUM & MAX
        activeAvgSpeed = activeSumSpeed / activeTime;
        activeAvgCad = (activeSumCad / activeTime);

        // Calculate REST LAP SUM & MAX
        restAvgSpeed = restSumSpeed / restTime;
        restAvgCad = (restSumCad / restTime);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void mergeLapDataFromTextFile(TextLapFile textLapFile) {
        int lapIx= 0;
        for (LapMesg record : lapRecords) {

            if (lapIx== 0) {
                record.setTotalDistance(textLapFile.lapRecords.get(lapIx).distance);
            }
            else {
                record.setTotalDistance(textLapFile.lapRecords.get(lapIx).distance - textLapFile.lapRecords.get(lapIx-1).distance);
                lapExtraRecords.get(lapIx-1).timeEnd = new DateTime(lapRecords.get(lapIx).getStartTime().getTimestamp() - 1); // 1 SEC
            }
            record.setAvgSpeed( record.getTotalDistance() / record.getTotalTimerTime() );
            record.setEnhancedAvgSpeed( record.getTotalDistance() / record.getTotalTimerTime() );
            //System.err.println(" === lapDist: " + record.getTotalDistance() + " lapTime: " + record.getTotalTimerTime() +" speed: " + mps2kmph3(record.getAvgSpeed()));
            lapExtraRecords.get(lapIx).stepLen = record.getTotalDistance() / ( record.getAvgCadence() * record.getTotalTimerTime() / 60 ); // step length acc to FFRT
            lapExtraRecords.get(lapIx).level = textLapFile.lapRecords.get(lapIx).level;
            lapIx++;
        }
        lapIx--;
        lapExtraRecords.get(lapIx).timeEnd = timeLastRecord;
        totalDistance = textLapFile.lapRecords.get(lapIx).distance;
        sessionRecords.get(0).setTotalDistance(totalDistance);
        avgSpeed = totalDistance / totalTimerTime;
        sessionRecords.get(0).setAvgSpeed(avgSpeed);
        sessionRecords.get(0).setEnhancedAvgSpeed(avgSpeed);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void readFitFile (String inputFilePath) {
        
        try {
            // Verify the file exists and is a valid FIT file
            File file = new File(inputFilePath);
            if (!file.exists()) { // || !file.isTrue()
                System.err.println("File not found: " + inputFilePath);
                return;
            }
            in = new FileInputStream(inputFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Error opening file ");
        }

        try {
            // Create a Decode object
            decode = new Decode();

            System.out.println("-- START new MesgBroadcaster(decode)." + sweDateTime.format(Calendar.getInstance().getTime()));
            
            // Create a MesgBroadcaster for decoding
            broadcaster = new MesgBroadcaster(decode);

            System.out.println("-- END MesgBroadcaster(decode)." + sweDateTime.format(Calendar.getInstance().getTime()));

            addListeners();

            // Decode the FIT file

            decode.read(in, broadcaster);

            try {
                numberOfLaps = lapRecords.size();
                timeFirstRecord = secRecords.get(0).getTimestamp();
                timeLastRecord = secRecords.get(secRecords.size() - 1).getTimestamp();
                totalTimerTime = sessionRecords.get(0).getTotalTimerTime();
                totalDistance = sessionRecords.get(0).getTotalDistance();
                if (sessionRecords.get(0).getAvgSpeed() != null) {
                    avgSpeed = sessionRecords.get(0).getAvgSpeed();
                }
                if (sessionRecords.get(0).getEnhancedAvgSpeed() != null) {
                    avgSpeed = sessionRecords.get(0).getEnhancedAvgSpeed();
                }
                manufacturer = Manufacturer.getStringFromValue(fileIdRecords.get(0).getManufacturer());
                if (manufacturer == "GARMIN") {
                    product = GarminProduct.getStringFromValue(fileIdRecords.get(0).getProduct());
                }
                swVer = deviceInfoRecords.get(0).getSoftwareVersion();
                sport = sessionRecords.get(0).getSport();
                if (sessionRecords.get(0).getSubSport() != null) {
                    subsport = sessionRecords.get(0).getSubSport();
                }
                if (sessionRecords.get(0).getSportProfileName() != null) {
                    sportProfile = sessionRecords.get(0).getSportProfileName();
                }
                if (!wktRecords.isEmpty()) {
                    if (wktRecords.get(0).getWktName() != null) {
                        wktName = wktRecords.get(0).getWktName();
                    }
                }
                numberOfRecords = secRecords.size();
            } catch (Exception e) {
                wktName = "";
                sportProfile = "";
                System.out.println("============== Exception: " + e);
            }
            System.out.println("FIT file successfully read. Total records: " + numberOfRecords + " -- " + timeLastRecord);

        } catch (FitRuntimeException e) {
            System.err.println("Error processing FIT file: " + e.getMessage());
        }
    }

//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void encodeNewFit (String outputFilePath, boolean encodeWorkoutRecords) {

        System.out.println("Encode Activity FIT File");

        try {
            FileEncoder encode;
            encode = new FileEncoder(new java.io.File(outputFilePath), Fit.ProtocolVersion.V2_0);

            for (FileIdMesg record : fileIdRecords) {
                encode.write(record);
            }
            for (FileCreatorMesg record : fileCreatorRecords) {
                encode.write(record);
            }
            for (ActivityMesg record : activityRecords) {
                encode.write(record);
            }
            for (DeviceInfoMesg record : deviceInfoRecords) {
                encode.write(record);
            }
            for (UserProfileMesg record : userProfileRecords) {
                encode.write(record);
            }
            for (MaxMetDataMesg record : maxMetDataRecords) {
                encode.write(record);
            }
            for (MetZoneMesg record : metZoneRecords) {
                encode.write(record);
            }
            for (GoalMesg record : goalRecords) {
                encode.write(record);
            }
            if (encodeWorkoutRecords) {
                for (WorkoutMesg record : wktRecords) {
                    encode.write(record);
                }
                for (WorkoutSessionMesg record : wktSessionRecords) {
                    encode.write(record);
                }
                for (ZonesTargetMesg record : zonesTargetRecords) {
                    encode.write(record);
                }
                for (WorkoutStepMesg record : wktStepRecords) {
                    encode.write(record);
                }
            }
            for (EventMesg record : eventRecords) {
                encode.write(record);
            }
            for (DeveloperDataIdMesg record : devDataIdRecords) {
                encode.write(record);
            }
/*             for (DeveloperFieldDescription record : devFieldDescrRecords) {
                encode.write(record);
            }
*/            
            for (FieldDescriptionMesg record : fieldDescrRecords) {
                encode.write(record);
            }
            for (SessionMesg record : sessionRecords) {
                encode.write(record);
            }
            for (LapMesg record : lapRecords) {
                encode.write(record);
            }
            for (RecordMesg record : secRecords) {
                encode.write(record);
            }
            // Close the encoder to finalize the file
            encode.close();
        } catch (FitRuntimeException e) {
            System.err.println("Error opening file ......fit");
            return;
        }
    }
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void addListeners() {

        broadcaster.addListener(new FileIdMesgListener() {
            @Override
            public void onMesg(FileIdMesg mesg) {
                fileIdRecords.add(mesg);
            }
        });

        broadcaster.addListener(new FileCreatorMesgListener() {
            @Override
            public void onMesg(FileCreatorMesg mesg) {
                fileCreatorRecords.add(mesg);
            }
        });

        // Add a listener to collect FileIdMesg objects
        broadcaster.addListener(new ActivityMesgListener() {
            @Override
            public void onMesg(ActivityMesg mesg) {
                activityRecords.add(mesg);
            }
        });

        broadcaster.addListener(new DeviceInfoMesgListener() {
            @Override
            public void onMesg(DeviceInfoMesg mesg) {
                deviceInfoRecords.add(mesg);
            }
        });

        broadcaster.addListener(new UserProfileMesgListener() {
            @Override
            public void onMesg(UserProfileMesg mesg) {
                userProfileRecords.add(mesg);
            }
        });
        
        broadcaster.addListener(new MaxMetDataMesgListener() {
            @Override
            public void onMesg(MaxMetDataMesg mesg) {
                maxMetDataRecords.add(mesg);
            }
        });
        
        broadcaster.addListener(new MetZoneMesgListener() {
            @Override
            public void onMesg(MetZoneMesg mesg) {
                metZoneRecords.add(mesg);
            }
        });
        
        broadcaster.addListener(new GoalMesgListener() {
            @Override
            public void onMesg(GoalMesg mesg) {
                goalRecords.add(mesg);
            }
        });
        
        broadcaster.addListener(new WorkoutMesgListener() {
            @Override
            public void onMesg(WorkoutMesg mesg) {
                wktRecords.add(mesg);
            }
        });
        
        broadcaster.addListener(new WorkoutSessionMesgListener() {
            @Override
            public void onMesg(WorkoutSessionMesg mesg) {
                wktSessionRecords.add(mesg);
            }
        });
        
        broadcaster.addListener(new ZonesTargetMesgListener() {
            @Override
            public void onMesg(ZonesTargetMesg mesg) {
                zonesTargetRecords.add(mesg);
            }
        });
        
        broadcaster.addListener(new WorkoutStepMesgListener() {
            @Override
            public void onMesg(WorkoutStepMesg mesg) {
                wktStepRecords.add(mesg);
            }
        });
        
        broadcaster.addListener(new EventMesgListener() {
            @Override
            public void onMesg(EventMesg mesg) {
                eventRecords.add(mesg);
            }
        });

        broadcaster.addListener(new DeveloperDataIdMesgListener() {
            @Override
            public void onMesg(DeveloperDataIdMesg mesg) {
                devDataIdRecords.add(mesg);
            }
        });

        decode.addListener(new DeveloperFieldDescriptionListener() {
            @Override
            public void onDescription(DeveloperFieldDescription desc) {
                devFieldDescrRecords.add(desc);
            }
        });

        broadcaster.addListener(new FieldDescriptionMesgListener() {
            @Override
            public void onMesg(FieldDescriptionMesg mesg) {
                fieldDescrRecords.add(mesg);
            }
        });

        broadcaster.addListener(new SessionMesgListener() {
            @Override
            public void onMesg(SessionMesg mesg) {
                sessionRecords.add(mesg);
            }
        });
        
        broadcaster.addListener(new LapMesgListener() {
            @Override
            public void onMesg(LapMesg mesg) {
                lapRecords.add(mesg);
            }
        });
        
        broadcaster.addListener(new RecordMesgListener() {
            @Override
            public void onMesg(RecordMesg mesg) {
                secRecords.add(mesg);
            }
        });
    }
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void changeStartTime (int changeSeconds) {
        for (FileIdMesg record : fileIdRecords) {
            // Modify the timestamp
            if (record.getTimeCreated() != null) {
                DateTime timeStamp2change = record.getTimeCreated();
                record.setTimeCreated(new DateTime(timeStamp2change.getTimestamp() + changeSeconds)); // Add 3 minutes
            }
        }
        for (ActivityMesg record : activityRecords) {
            // Modify the timestamp
            if (record.getTimestamp() != null) {
                DateTime timeStamp2change = record.getTimestamp();
                record.setTimestamp(new DateTime(timeStamp2change.getTimestamp() + changeSeconds)); // Add 3 minutes
            }
            //Get local_timestamp field Comment: 
            //timestamp EPOCH expressed in local time, used to convert activity timestamps to local time
            if (record.getLocalTimestamp() != null) {
                //DateTime timeStamp2change = record.getLocalTimestamp();
                record.setLocalTimestamp(record.getLocalTimestamp() + changeSeconds); // Add 3 minutes
            }
        }
        for (DeviceInfoMesg record : deviceInfoRecords) {
            // Modify the timestamp
            if (record.getTimestamp() != null) {
                DateTime timeStamp2change = record.getTimestamp();
                record.setTimestamp(new DateTime(timeStamp2change.getTimestamp() + changeSeconds)); // Add 3 minutes
            }
        }
        for (EventMesg record : eventRecords) {
            // Modify the timestamp
            if (record.getTimestamp() != null) {
                DateTime timeStamp2change = record.getTimestamp();
                record.setTimestamp(new DateTime(timeStamp2change.getTimestamp() + changeSeconds)); // Add 3 minutes
            }
            if (record.getStartTimestamp() != null) {
                DateTime timeStamp2change2 = record.getStartTimestamp();
                record.setStartTimestamp(new DateTime(timeStamp2change2.getTimestamp() + changeSeconds)); // Add 3 minutes
            }
        }
        for (SessionMesg record : sessionRecords) {
            // Modify the timestamp
            if (record.getTimestamp() != null) {
                DateTime timeStamp2change = record.getTimestamp();
                record.setTimestamp(new DateTime(timeStamp2change.getTimestamp() + changeSeconds)); // Add 3 minutes
            }
            if (record.getStartTime() != null) {
                DateTime timeStamp2change2 = record.getStartTime();
                record.setStartTime(new DateTime(timeStamp2change2.getTimestamp() + changeSeconds)); // Add 3 minutes
            }
        }
        for (LapMesg record : lapRecords) {
            // Modify the timestamp
            if (record.getTimestamp() != null) {
                DateTime timeStamp2change = record.getTimestamp();
                record.setTimestamp(new DateTime(timeStamp2change.getTimestamp() + changeSeconds)); // Add 3 minutes
            }
            if (record.getStartTime() != null) {
                DateTime timeStamp2change2 = record.getStartTime();
                record.setStartTime(new DateTime(timeStamp2change2.getTimestamp() + changeSeconds)); // Add 3 minutes
            }
        }
        for (RecordMesg record : secRecords) {
            // Modify the timestamp
            if (record.getTimestamp() != null) {
                DateTime timeStamp2change = record.getTimestamp();
                record.setTimestamp(new DateTime(timeStamp2change.getTimestamp() + changeSeconds)); // Add 3 minutes
            }
        }
        timeFirstRecord = secRecords.get(0).getTimestamp();
        timeLastRecord = secRecords.get(secRecords.size() - 1).getTimestamp();

    }
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printFileSummary() {
        System.out.println("--------------------------------------------------");
        System.out.println(" --> Manufacturer:" + manufacturer + ", " + product + "(" + fileIdRecords.get(0).getProduct() + ")" + ", SW: v" + swVer);
        System.out.println(" --> Sport:"+ sport + ", SubSport:" + subsport + ", SportProfile:" + sessionRecords.get(0).getSportProfileName() + ", WktName:" + wktName);
        System.out.println("--------------------------------------------------");
    }
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printFileIdInfo () {
        int i = 0;
        String manu = "";
        String prod;
        System.out.println("--------------------------------------------------");
        for (FileIdMesg record : fileIdRecords) {
            i++;
            System.out.println("File ID: " + i);
            if (record.getType() != null) {
                System.out.print(" Type: ");
                System.out.print(record.getType().getValue());
            }
            if (record.getManufacturer() != null ) {
                System.out.print(" Manufacturer: ");
                manu = Manufacturer.getStringFromValue(record.getManufacturer());
                System.out.print(manu + "(" + record.getManufacturer() + ")");
            }
            if (record.getProduct() != null) {
                System.out.print(" Product: ");
                if (manu == "GARMIN") {
                    prod = GarminProduct.getStringFromValue(record.getProduct());
                    System.out.print(prod + "(" + record.getProduct() + ")");
                }
                else {
                    System.out.print(record.getProduct());
                }
            }
            if (record.getSerialNumber() != null) {
                System.out.print(" Serial Number: ");
                System.out.print(record.getSerialNumber());
            }
            if (record.getNumber() != null) {
                System.out.print(" Number: ");
                System.out.print(record.getNumber());
            }
            System.out.println();
            if (i == 11) {
                break;
            }
        }
        System.out.println("--------------------------------------------------");
    }
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printDeviceInfo () {
        int i = 0;
        String manu = "";
        String prod;
        System.out.println("--------------------------------------------------");
        for (DeviceInfoMesg record : deviceInfoRecords) {
            i++;
            System.out.print("Device ID: " + i);
            if (record.getDeviceType() != null ) {
                System.out.print(" -- DeviceType: ");
                System.out.print("(" + record.getDeviceType() + ")");
            }
            if (record.getSoftwareVersion() != null) {
                System.out.print(" SW: v");
                System.out.print(record.getSoftwareVersion());
            }
            if (record.getManufacturer() != null ) {
                System.out.print(" Manufacturer: ");
                manu = Manufacturer.getStringFromValue(record.getManufacturer());
                System.out.print(manu + "(" + record.getManufacturer() + ")");
            }
            if (record.getProduct() != null) {
                System.out.print(" Product: ");
                if (manu == "GARMIN") {
                    prod = GarminProduct.getStringFromValue(record.getProduct());
                    System.out.print(prod + "(" + record.getProduct() + ")");
                }
                else {
                    System.out.print(record.getProduct());
                }
            }
            if (record.getProductName() != null) {
                System.out.print(" ProductName: ");
                System.out.print(record.getProductName());
            }
            if (record.getHardwareVersion() != null) {
                System.out.print(" HW: v");
                System.out.print(record.getHardwareVersion());
            }
            System.out.println();
            if (i == 11) {
                break;
            }
        }
        System.out.println("--------------------------------------------------");
    }
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printWktInfo () {
        int i = 0;
        System.out.println("--------------------------------------------------");
        for (WorkoutMesg record : wktRecords) {
            i++;
            System.out.print("Workout: " + i);
            if (record.getWktName() != null) {
                System.out.print(" WktName:");
                System.out.print(record.getWktName());
            }
            /* if (record.getWktDescription() != null) {
                System.out.print(" WktDescr:");
                System.out.print(record.getWktDescription());
            } */
            if (record.getNumValidSteps() != null) {
                System.out.print(" AntSteg:");
                System.out.print(record.getNumValidSteps());
            }
            if (record.getSport() != null) {
                System.out.print(" Sport:");
                System.out.print(record.getSport());
            }
            if (record.getSubSport() != null) {
                System.out.print(" SubSport:");
                System.out.print(record.getSubSport());
            }
            System.out.println();
            if (i == 11) {
                break;
            }
        }
        System.out.println("--------------------------------------------------");
    }
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printWktSessionInfo () {
        int i = 0;
        System.out.println("--------------------------------------------------");
        for (WorkoutSessionMesg record : wktSessionRecords) {
            i++;
            System.out.print("WorkoutSession: " + i);
            if (record.getNumValidSteps() != null) {
                System.out.print(" NoOfSteps:");
                System.out.print(record.getNumValidSteps());
            }
            if (record.getSport() != null) {
                System.out.print(" Sport:");
                System.out.print(record.getSport());
            }
            if (record.getSubSport() != null) {
                System.out.print(" SubSport:");
                System.out.print(record.getSubSport());
            }
            System.out.println();
            if (i == 11) {
                break;
            }
        }
        System.out.println("--------------------------------------------------");
    }
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printWktStepInfo () {
        int i = 0;
        int stepType;
        System.out.println("--------------------------------------------------");
        for (WorkoutStepMesg record : wktStepRecords) {
            i++;
            System.out.print("WorkoutStep: " + i);
            if (record.getMessageIndex() != null) {
                System.out.print(" StepIx:");
                System.out.print(record.getMessageIndex());
            }
            if (record.getDurationType() != null) {
                //stepType = record.getDurationType();
                System.out.print(" Type:");
                System.out.print(record.getDurationType());
                //System.out.print(WktStepDuration.getStringFromValue(record.getDurationType()) + "(" + record.getDurationType() + ")");
            }
            if (record.getDurationTime() != null) {
                System.out.print(" Tid:");
                System.out.print(record.getDurationTime() + "sec");
            }
            System.out.println();
            if (i == 11) {
                break;
            }
        }
        System.out.println("--------------------------------------------------");
    }
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printSessionInfo () {
        int i = 0;
        System.out.println("--------------------------------------------------");
        for (SessionMesg record : sessionRecords) {
            i++;
            System.out.println("Session: " + i);

            if (record.getSport() != null) {
                System.out.print(" Sport:");
                System.out.print(record.getSport());
            }
            if (record.getSubSport() != null) {
                System.out.print(" SubSport:");
                System.out.print(record.getSubSport());
            }
            if (record.getSportProfileName() != null) {
                System.out.print(" SportProfile:");
                System.out.print(record.getSportProfileName());
            }
            System.out.println();
            if (record.getStartTime() != null) {
                System.out.print(" ActivityTime:");
                System.out.print(record.getStartTime());
            }
            if (record.getTimestamp() != null) {
                System.out.print(" - ");
                System.out.print(record.getTimestamp());
            }
            if (record.getTotalTimerTime() != null) {
                System.out.print(" = ");
                System.out.print(record.getTotalTimerTime());
            }
            System.out.println();
            if (i == 11) {
                break;
            }
        }
        System.out.println("--------------------------------------------------");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printDevDataId() {
        System.out.println("--------------------------------------------------");
        for (DeveloperDataIdMesg desc : devDataIdRecords){
            System.out.print("Developer Data Id");
            System.out.print(" App Id:" + desc.getApplicationId(0));
            System.out.print(" NumApp Id:" + desc.getNumApplicationId());
            System.out.print(" App Id:" + desc.getApplicationId());
            System.out.print(" App Version:" + desc.getApplicationVersion());
            System.out.print(" DevId:" + desc.getDeveloperId());
            System.out.print(" NumDevId:" + desc.getNumDeveloperId());
            System.out.print(" DevDataIx:" + desc.getDeveloperDataIndex());
            System.out.println();
        }
        System.out.println("--------------------------------------------------");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printDevFieldDescr() {
        System.out.println("--------------------------------------------------");
        for (DeveloperFieldDescription desc : devFieldDescrRecords){
            System.out.print("Developer Field Description");
            System.out.print(" App Id:" + desc.getApplicationId());
            System.out.print(" App Version:" + desc.getApplicationVersion());
            System.out.print(" Field Num:" + desc.getFieldDefinitionNumber());
            System.out.println();
        }
        System.out.println("--------------------------------------------------");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printFieldDescr() {
        System.out.println("--------------------------------------------------");
        for (FieldDescriptionMesg record : fieldDescrRecords){
            System.out.print("Field Description");
            System.out.print(" DeveloperDataIndex:" + record.getDeveloperDataIndex());
            System.out.print(" FieldDefinitionNumber:" + record.getFieldDefinitionNumber());
            System.out.print(" FitBaseTypeId:" + record.getFitBaseTypeId());
            System.out.print(" FieldName:" + record.getFieldName(0));
            System.out.print(" Units:" + record.getUnits(0));
            System.out.println(" NativeFieldNum:" + record.getNativeFieldNum());
        }
        System.out.println("--------------------------------------------------");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printLapRecords0 () {
        int i = 0;
        int lapNo = 1;
        try {
            System.out.println("--------------------------------------------------");
            for (LapMesg record : lapRecords) {
                System.out.print("Lap:" + lapNo);
                if (record.getTimestamp() != null) {
                    System.out.print(" StartTime: " + record.getStartTime());
                }
                if (record.getTimestamp() != null) {
                    System.out.print(" Timestamp: " + record.getTimestamp());
                }
                if (record.getTotalTimerTime() != null) {
                    System.out.print(" LapTime: " + record.getTotalTimerTime());
                }
                if (record.getTotalDistance() != null) {
                    System.out.print(" LapDist: " + record.getTotalDistance());
                }
                if (record.getAvgSpeed() != null) {
                    System.out.print(" LapPace: " + record.getAvgSpeed());
                }
                if (record.getAvgCadence() != null) {
                    System.out.print(" LapCad: " + record.getAvgCadence());
                }
                if (record.getIntensity() != null) {
                    System.out.print(" WktIntensity: " + Intensity.getStringFromValue(record.getIntensity()));
                }
                if (record.getWktStepIndex() != null) {
                    System.out.print(" LapWktStepIx: " + record.getWktStepIndex());
                }
                System.out.println();
                i++;
                lapNo++;
            }
            System.out.println("--------------------------------------------------");
        }
        catch (FitRuntimeException e) {
            System.out.println("LAP ERROR!!!!");
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printLapRecords () {
        int i = 0;
        int lapNo = 1;
        try {
            System.out.println("--------------------------------------------------");
            for (LapMesg record : lapRecords) {
                System.out.print("Lap:" + lapNo);
                if (lapExtraRecords.get(i).level != null) {
                    System.out.print(" lv" + lapExtraRecords.get(i).level);
                }
            /*if (record.getStartTime() != null) {
                    System.out.print(" StartTime: " + record.getStartTime());
                }
                if (record.getTimestamp() != null) {
                    System.out.print(" Timestamp: " + record.getTimestamp());
                }*/
                if (record.getTotalTimerTime() != null) {
                    System.out.print(" LapTime: " + record.getTotalTimerTime());
                }
                if (record.getTotalDistance() != null) {
                    System.out.print(" LapDist: " + record.getTotalDistance());
                }
                System.out.print(" DistFrom: " + secRecords.get(lapExtraRecords.get(i).recordIxStart).getDistance());
                System.out.print(" DistTo: " + secRecords.get(lapExtraRecords.get(i).recordIxEnd).getDistance());
                if (record.getAvgSpeed() != null) {
                    System.out.print(" LapPace: " + record.getAvgSpeed());
                }
                if (record.getAvgCadence() != null) {
                    System.out.print(" LapCad: " + record.getAvgCadence());
                }
                if (record.getIntensity() != null) {
                    System.out.print(" WktIntensity: " + Intensity.getStringFromValue(record.getIntensity()));
                }
                if (record.getWktStepIndex() != null) {
                    System.out.print(" LapWktStepIx: " + record.getWktStepIndex());
                }
                if (lapExtraRecords.get(i).timeEnd != null) {
                    System.out.print(" TimeEnd: " + lapExtraRecords.get(i).timeEnd);
                }
                if (lapExtraRecords.get(i).stepLen != null) {
                    System.out.print(" StepLen: " + lapExtraRecords.get(i).stepLen);
                }
                if (lapExtraRecords.get(i).hrStart != 0) {
                    System.out.print(" hrStart: " + lapExtraRecords.get(i).hrStart);
                }
                if (lapExtraRecords.get(i).hrEnd != 0) {
                    System.out.print(" hrEnd: " + lapExtraRecords.get(i).hrEnd);
                }
                if (lapExtraRecords.get(i).recordIxEnd != 0) {
                    System.out.print(" recordIxEnd: " + lapExtraRecords.get(i).recordIxEnd);
                }
                if (lapExtraRecords.get(i).hrMin != 0) {
                    System.out.print(" hrMin: " + lapExtraRecords.get(i).hrMin);
                }
                System.out.println();
                i++;
                lapNo++;
            }
            System.out.println("--------------------------------------------------");
        }
        catch (FitRuntimeException e) {
            System.out.println("LAP ERROR!!!!");
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printLapAvgMaxSpeed (Float avgSpeed, Float maxSpeed) {
        if (avgSpeed != null) {
            if (isSkiErgFile()) {
                System.out.print("--Sp avg:" + mps2minp500m(avgSpeed));
                System.out.print(" max:" + mps2minp500m(maxSpeed));
            } else {
                System.out.print("--Sp avg:" + mps2minpkm(avgSpeed));
                System.out.print(" max:" + mps2minpkm(maxSpeed));
            }
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void writeLapAvgMaxSpeed (FileWriter file, Float avgSpeed, Float maxSpeed) {
        try {
            if (avgSpeed != null) {
                if (isSkiErgFile()) {
                    file.write("--Sp avg:" + mps2minp500m(avgSpeed));
                    file.write(" max:" + mps2minp500m(maxSpeed));
                } else {
                    file.write("--Sp avg:" + mps2minpkm(avgSpeed));
                    file.write(" max:" + mps2minpkm(maxSpeed));
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printLapAvgSpeed (Float avgSpeed) {
        if (avgSpeed != null) {
            if (isSkiErgFile()) {
                System.out.print(" " + mps2minp500m(avgSpeed) + "min/500m");
            } else {
                System.out.print(" " + mps2minpkm(avgSpeed) + "min/km");
                System.out.print(" " + mps2kmph1(avgSpeed) + "km/h");
            }
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void writeLapAvgSpeed (FileWriter file, Float avgSpeed) {
        try {
            if (avgSpeed != null) {
                if (isSkiErgFile()) {
                    file.write(" " + mps2minp500m(avgSpeed) + "min/500m");
                } else {
                    file.write(" " + mps2minpkm(avgSpeed) + "min/km");
                    file.write(" " + mps2kmph1(avgSpeed) + "km/h");
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void writeEndSum (FileWriter file, Float avgCad, Float avgSpeed, Float avgPower, Float dist) {
        try {
            if (isSkiErgFile()) {
                file.write("avgCad: " + (int) Math.round(avgCad) + "spm");
                file.write(", avgPace: " + mps2minp500m(avgSpeed) + "min/500m");
                file.write(", avgPow: " + (int) Math.round(avgPower) + "W");
                file.write(", lapDist: " + m2km1(dist) + "km");
                file.write(System.lineSeparator());
            } else {
                file.write("avgCad: " + (int) Math.round(avgCad) + "spm");
                file.write(", avgPace: " + mps2minpkm(avgSpeed) + "min/km");
                file.write(", avgSp: " + mps2kmph1(avgSpeed) + "km/h");
                file.write(", lapDist: " + m2km1(dist) + "km");
                file.write(System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printLapEndSum (Float avgCad, Float avgSpeed, Float avgPower, Float dist) {
        if (isSkiErgFile()) {
            System.out.print("avgCad: " + (int) Math.round(avgCad) + "spm");
            System.out.print(", avgPace: " + mps2minp500m(avgSpeed) + "min/500m");
            System.out.print(", avgPow: " + (int) Math.round(avgPower) + "W");
            System.out.print(", lapDist: " + m2km1(dist) + "km");
            System.out.println();
        } else {
            System.out.print("avgCad: " + (int) Math.round(avgCad) + "spm");
            System.out.print(", avgPace: " + mps2minpkm(avgSpeed) + "min/km");
            System.out.print(", avgSp: " + mps2kmph1(avgSpeed) + "km/h");
            System.out.print(", lapDist: " + m2km1(dist) + "km");
            System.out.println();
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void writeFileLapSummary (String filename) {
        int i = 0;
        int lapNo = 1;
        try {
            FileWriter myWriter = new FileWriter(filename);
            myWriter.write("====================== LAPS ======================");
            myWriter.write(System.lineSeparator());
            myWriter.write("--------------------------------------------------");
            myWriter.write(System.lineSeparator());
            myWriter.write("---- ALL LAPS ----");
            myWriter.write(System.lineSeparator());
            for (LapMesg record : lapRecords) {
                myWriter.write("Lap:" + lapNo);
                /*if (record.getStartTime() != null) {
                    myWriter.write(" StartTime: " + record.getStartTime());
                }
                if (record.getTimestamp() != null) {
                    myWriter.write(" Timestamp: " + record.getTimestamp());
                }*/
                if (record.getTotalTimerTime() != null) {
                    myWriter.write(" LapTime: " + record.getTotalTimerTime());
                }
                if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                    myWriter.write(" lv" + lapExtraRecords.get(i).level.intValue());
                }
                if (record.getIntensity() != null) {
                    myWriter.write(" WktIntensity: " + Intensity.getStringFromValue(record.getIntensity()));
                }
                if (Intensity.getStringFromValue(record.getIntensity()).equals("ACTIVE") || Intensity.getStringFromValue(record.getIntensity()).equals("WARMUP")) {
                    myWriter.write(" HR start:" + lapExtraRecords.get(i).hrStart);
                    myWriter.write(" min:" + lapExtraRecords.get(i).hrMin);
                    myWriter.write("+" + (lapRecords.get(i).getMaxHeartRate()-lapExtraRecords.get(i).hrMin));
                    myWriter.write("-->max:" + lapRecords.get(i).getMaxHeartRate());
                    myWriter.write(" end:" + lapExtraRecords.get(i).hrEnd);
                }
                else {
                    myWriter.write(" HR start:" + lapExtraRecords.get(i).hrStart);
                    myWriter.write(" max:" + lapRecords.get(i).getMaxHeartRate());
                    myWriter.write("" + (lapExtraRecords.get(i).hrMin-lapRecords.get(i).getMaxHeartRate()));
                    myWriter.write("-->min:" + lapExtraRecords.get(i).hrMin);
                    myWriter.write(" end:" + lapExtraRecords.get(i).hrEnd);
                }
                if (record.getTotalDistance() != null) {
                    myWriter.write("--Dist:" + record.getTotalDistance());
                }
                writeLapAvgMaxSpeed(myWriter, record.getAvgSpeed(), record.getMaxSpeed());
                if (record.getAvgCadence() != null) {
                    myWriter.write("--Cad avg:" + record.getAvgCadence());
                    myWriter.write(" max:" + record.getMaxCadence());
                }
                if (record.getAvgPower() != null) {
                    myWriter.write("--Pow avg:" + record.getAvgPower());
                    myWriter.write(" max:" + record.getMaxPower());
                }
                if (lapExtraRecords.get(i).avgDragFactor != null) {
                    myWriter.write("--DFavg:" + (int) Math.round(lapExtraRecords.get(i).avgDragFactor));
                    myWriter.write(" max:" + (int) Math.round(lapExtraRecords.get(i).maxDragFactor));
                }
                if (lapExtraRecords.get(i).avgStrokeLen != null) {
                    myWriter.write("--SLavg:" + lapExtraRecords.get(i).avgStrokeLen);
                    myWriter.write(" max:" + lapExtraRecords.get(i).maxStrokeLen);
                }
                myWriter.write(System.lineSeparator());
                i++;
                lapNo++;
            }
            myWriter.write("--------------------------------------------------");
            myWriter.write(System.lineSeparator());
            myWriter.write("---- ACTIVE LAPS ----");
            myWriter.write(System.lineSeparator());
            i=0;
            lapNo=1;
            for (LapMesg record : lapRecords) {
                if (Intensity.getStringFromValue(record.getIntensity()).equals("ACTIVE")) {
                    myWriter.write("Lap:" + lapNo);
                    if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                        myWriter.write(" lv" + lapExtraRecords.get(i).level.intValue());
                    }
                    if (record.getTotalTimerTime() != null) {
                        myWriter.write(" LapTime: " + sec2minSecShort(record.getTotalTimerTime()));
                    }
                    myWriter.write(" HR start:" + lapExtraRecords.get(i).hrStart);
                    myWriter.write(" min:" + lapExtraRecords.get(i).hrMin);
                    myWriter.write("+" + (lapRecords.get(i).getMaxHeartRate()-lapExtraRecords.get(i).hrMin));
                    myWriter.write("-->max:" + lapRecords.get(i).getMaxHeartRate());
                    myWriter.write(" end:" + lapExtraRecords.get(i).hrEnd);
                    if (record.getTotalDistance() != null) {
                        myWriter.write("--Dist:" + record.getTotalDistance());
                    }
                    writeLapAvgMaxSpeed(myWriter, record.getAvgSpeed(), record.getMaxSpeed());
                    if (record.getAvgCadence() != null) {
                        myWriter.write("--Cad avg:" + record.getAvgCadence());
                        myWriter.write(" max:" + record.getMaxCadence());
                    }
                    if (record.getAvgPower() != null) {
                        myWriter.write("--Pow avg:" + record.getAvgPower());
                        myWriter.write(" max:" + record.getMaxPower());
                    }
                    if (lapExtraRecords.get(i).avgDragFactor != null) {
                        myWriter.write("--DFavg:" + (int) Math.round(lapExtraRecords.get(i).avgDragFactor));
                        myWriter.write(" max:" + (int) Math.round(lapExtraRecords.get(i).maxDragFactor));
                    }
                    if (lapExtraRecords.get(i).avgStrokeLen != null) {
                        myWriter.write("--SLavg:" + lapExtraRecords.get(i).avgStrokeLen);
                        myWriter.write(" max:" + lapExtraRecords.get(i).maxStrokeLen);
                    }
                    myWriter.write(System.lineSeparator());
                }
                i++;
                lapNo++;
            }
            myWriter.write("---- REST LAPS ----");
            myWriter.write(System.lineSeparator());
            i=0;
            lapNo=1;
            for (LapMesg record : lapRecords) {
                if (Intensity.getStringFromValue(record.getIntensity()).equals("REST") || Intensity.getStringFromValue(record.getIntensity()).equals("RECOVERY")) {
                    myWriter.write("Lap:" + lapNo);
                    if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                        myWriter.write(" lv" + lapExtraRecords.get(i).level.intValue());
                    }
                    if (record.getTotalTimerTime() != null) {
                        myWriter.write(" LapTime: " + sec2minSecShort(record.getTotalTimerTime()));
                    }
                    myWriter.write(" HR start:" + lapExtraRecords.get(i).hrStart);
                    myWriter.write(" max:" + lapRecords.get(i).getMaxHeartRate());
                    myWriter.write("" + (lapExtraRecords.get(i).hrMin-lapRecords.get(i).getMaxHeartRate()));
                    myWriter.write("-->min:" + lapExtraRecords.get(i).hrMin);
                    myWriter.write(" end:" + lapExtraRecords.get(i).hrEnd);
                    if (record.getTotalDistance() != null) {
                        myWriter.write("--Dist:" + record.getTotalDistance());
                    }
                    writeLapAvgMaxSpeed(myWriter, record.getAvgSpeed(), record.getMaxSpeed());
                    if (record.getAvgCadence() != null) {
                        myWriter.write("--Cad avg:" + record.getAvgCadence());
                        myWriter.write(" max:" + record.getMaxCadence());
                    }
                    if (record.getAvgPower() != null) {
                        myWriter.write("--Pow avg:" + record.getAvgPower());
                        myWriter.write(" max:" + record.getMaxPower());
                    }
                    if (lapExtraRecords.get(i).avgDragFactor != null) {
                        myWriter.write("--DFavg:" + (int) Math.round(lapExtraRecords.get(i).avgDragFactor));
                        myWriter.write(" max:" + (int) Math.round(lapExtraRecords.get(i).maxDragFactor));
                    }
                    if (lapExtraRecords.get(i).avgStrokeLen != null) {
                        myWriter.write("--SLavg:" + lapExtraRecords.get(i).avgStrokeLen);
                        myWriter.write(" max:" + lapExtraRecords.get(i).maxStrokeLen);
                    }
                    myWriter.write(System.lineSeparator());
                }
                i++;
                lapNo++;
            }
            myWriter.write("--------------------------------------------------");
            myWriter.write(System.lineSeparator());
            myWriter.write("---- ACTIVE LAPS ----");
            myWriter.write(System.lineSeparator());
            i=0;
            lapNo=1;
            for (LapMesg record : lapRecords) {
                if (Intensity.getStringFromValue(record.getIntensity()).equals("ACTIVE")) {
                    myWriter.write("Lap" + lapNo);
                    if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                        myWriter.write(" lv" + lapExtraRecords.get(i).level.intValue());
                    }
                    if (i > 0) {
                        myWriter.write(" HRmin" + lapExtraRecords.get(i-1).hrMin);
                    } else {
                        myWriter.write(" HR");
                    }
                    myWriter.write(">st" + lapExtraRecords.get(i).hrStart);
                    myWriter.write("+" + (lapRecords.get(i).getMaxHeartRate()-lapExtraRecords.get(i).hrMin));
                    myWriter.write("->max" + lapRecords.get(i).getMaxHeartRate());
                    myWriter.write(" end" + lapExtraRecords.get(i).hrEnd);
                    if (record.getTotalTimerTime() != null) {
                        myWriter.write(" " + sec2minSecShort(record.getTotalTimerTime()) + "min");
                    }
                    if (record.getAvgCadence() != null) {
                        myWriter.write(" " + record.getAvgCadence() + "spm");
                    }
                    writeLapAvgSpeed(myWriter, record.getAvgSpeed());
                    if (record.getAvgPower() != null) {
                        myWriter.write(" " + record.getAvgPower() + "W");
                    }
                    if (record.getTotalDistance() != null) {
                        myWriter.write(" " + m2km1(record.getTotalDistance()) + "km");
                    }
                    if (lapExtraRecords.get(i).avgDragFactor != null) {
                        myWriter.write(" df" + (int) Math.round(lapExtraRecords.get(i).avgDragFactor));
                    }
                    if (lapExtraRecords.get(i).avgStrokeLen != null) {
                        myWriter.write(" sl" + lapExtraRecords.get(i).avgStrokeLen);
                    }
                        myWriter.write(System.lineSeparator());
                }
                i++;
                lapNo++;
            }
            writeEndSum(myWriter, activeAvgCad, activeAvgSpeed, activeAvgPower, activeDist);
            myWriter.write("---- REST LAPS ----");
            myWriter.write(System.lineSeparator());
            i=0;
            lapNo=1;
            for (LapMesg record : lapRecords) {
                if (Intensity.getStringFromValue(record.getIntensity()).equals("REST") || Intensity.getStringFromValue(record.getIntensity()).equals("RECOVERY")) {
                    myWriter.write("Lap" + lapNo);
                    if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                        myWriter.write(" lv" + lapExtraRecords.get(i).level.intValue());
                    }
                    myWriter.write(" HRst" + lapExtraRecords.get(i).hrStart);
                    myWriter.write(">max" + lapRecords.get(i).getMaxHeartRate());
                    myWriter.write("" + (lapExtraRecords.get(i).hrMin-lapRecords.get(i).getMaxHeartRate()));
                    myWriter.write("->min" + lapExtraRecords.get(i).hrMin);
                    myWriter.write(" end" + lapExtraRecords.get(i).hrEnd);
                    if (record.getTotalTimerTime() != null) {
                        myWriter.write(" " + sec2minSecShort(record.getTotalTimerTime()) + "min");
                    }
                    if (record.getAvgCadence() != null) {
                        myWriter.write(" " + record.getAvgCadence() + "spm");
                    }
                    writeLapAvgSpeed(myWriter, record.getAvgSpeed());
                    if (record.getAvgPower() != null) {
                        myWriter.write(" " + record.getAvgPower() + "W");
                    }
                    if (record.getTotalDistance() != null) {
                        myWriter.write(" " + m2km1(record.getTotalDistance()) + "km");
                    }
                    myWriter.write(System.lineSeparator());
                }
                i++;
                lapNo++;
            }
            writeEndSum(myWriter, restAvgCad, restAvgSpeed, restAvgPower, restDist);
            myWriter.write("--------------------------------------------------");
            myWriter.write(System.lineSeparator());
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printLapSummery () {
        int i = 0;
        int lapNo = 1;
        try {
            System.out.println("--------------------------------------------------");
            for (LapMesg record : lapRecords) {
                System.out.print("Lap:" + lapNo);
                /*if (record.getStartTime() != null) {
                    System.out.print(" StartTime: " + record.getStartTime());
                }
                if (record.getTimestamp() != null) {
                    System.out.print(" Timestamp: " + record.getTimestamp());
                }*/
                if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                    System.out.print(" lv" + lapExtraRecords.get(i).level.intValue());
                }
                if (record.getTotalTimerTime() != null) {
                    System.out.print(" LapTime: " + record.getTotalTimerTime());
                }
                if (record.getIntensity() != null) {
                    System.out.print(" WktIntensity: " + Intensity.getStringFromValue(record.getIntensity()));
                }
                if (Intensity.getStringFromValue(record.getIntensity()).equals("ACTIVE") || Intensity.getStringFromValue(record.getIntensity()).equals("WARMUP")) {
                    System.out.print(" HR start:" + lapExtraRecords.get(i).hrStart);
                    System.out.print(" min:" + lapExtraRecords.get(i).hrMin);
                    System.out.print("+" + (lapRecords.get(i).getMaxHeartRate()-lapExtraRecords.get(i).hrMin));
                    System.out.print("-->max:" + lapRecords.get(i).getMaxHeartRate());
                    System.out.print(" end:" + lapExtraRecords.get(i).hrEnd);
                }
                else {
                    System.out.print(" HR start:" + lapExtraRecords.get(i).hrStart);
                    System.out.print(" max:" + lapRecords.get(i).getMaxHeartRate());
                    System.out.print("" + (lapExtraRecords.get(i).hrMin-lapRecords.get(i).getMaxHeartRate()));
                    System.out.print("-->min:" + lapExtraRecords.get(i).hrMin);
                    System.out.print(" end:" + lapExtraRecords.get(i).hrEnd);
                }
                if (record.getTotalDistance() != null) {
                    System.out.print("--Dist:" + record.getTotalDistance());
                }
                printLapAvgMaxSpeed(record.getAvgSpeed(), record.getMaxSpeed());
                if (record.getAvgCadence() != null) {
                    System.out.print("--Cad avg:" + record.getAvgCadence());
                    System.out.print(" max:" + record.getMaxCadence());
                }
                if (record.getAvgPower() != null) {
                    System.out.print("--Pow avg:" + record.getAvgPower());
                    System.out.print(" max:" + record.getMaxPower());
                }
                if (lapExtraRecords.get(i).avgDragFactor != null) {
                    System.out.print("--DFavg:" + (int) Math.round(lapExtraRecords.get(i).avgDragFactor));
                    System.out.print(" max:" + (int) Math.round(lapExtraRecords.get(i).maxDragFactor));
                }
                if (lapExtraRecords.get(i).avgStrokeLen != null) {
                    System.out.print("--SLavg:" + lapExtraRecords.get(i).avgStrokeLen);
                    System.out.print(" max:" + lapExtraRecords.get(i).maxStrokeLen);
                }
                System.out.println();
                i++;
                lapNo++;
            }
            System.out.println("---- ACTIVE LAPS ----");
            i=0;
            lapNo=1;
            for (LapMesg record : lapRecords) {
                if (Intensity.getStringFromValue(record.getIntensity()).equals("ACTIVE")) {
                    System.out.print("Lap:" + lapNo);
                    if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                        System.out.print(" lv" + lapExtraRecords.get(i).level.intValue());
                    }
                        if (record.getTotalTimerTime() != null) {
                        System.out.print(" LapTime: " + sec2minSecShort(record.getTotalTimerTime()));
                    }
                    System.out.print(" HR start:" + lapExtraRecords.get(i).hrStart);
                    if (i > 0) {
                        System.out.print(" HRmin" + lapExtraRecords.get(i-1).hrMin);
                    } else {
                        System.out.print(" HR");
                    }
                    System.out.print(" min:" + lapExtraRecords.get(i).hrMin);
                    System.out.print("+" + (lapRecords.get(i).getMaxHeartRate()-lapExtraRecords.get(i).hrMin));
                    System.out.print("-->max:" + lapRecords.get(i).getMaxHeartRate());
                    System.out.print(" end:" + lapExtraRecords.get(i).hrEnd);
                    if (record.getTotalDistance() != null) {
                        System.out.print("--Dist:" + record.getTotalDistance());
                    }
                    printLapAvgMaxSpeed(record.getAvgSpeed(), record.getMaxSpeed());
                    if (record.getAvgCadence() != null) {
                        System.out.print("--Cad avg:" + record.getAvgCadence());
                        System.out.print(" max:" + record.getMaxCadence());
                    }
                    if (record.getAvgPower() != null) {
                        System.out.print("--Pow avg:" + record.getAvgPower());
                        System.out.print(" max:" + record.getMaxPower());
                    }
                    if (lapExtraRecords.get(i).avgDragFactor != null) {
                        System.out.print("--DFavg:" + (int) Math.round(lapExtraRecords.get(i).avgDragFactor));
                        System.out.print(" max:" + (int) Math.round(lapExtraRecords.get(i).maxDragFactor));
                    }
                    if (lapExtraRecords.get(i).avgStrokeLen != null) {
                        System.out.print("--SLavg:" + lapExtraRecords.get(i).avgStrokeLen);
                        System.out.print(" max:" + lapExtraRecords.get(i).maxStrokeLen);
                    }
                    System.out.println();
                }
                i++;
                lapNo++;
            }
            System.out.println("---- REST LAPS ----");
            i=0;
            lapNo=1;
            for (LapMesg record : lapRecords) {
                if (Intensity.getStringFromValue(record.getIntensity()).equals("REST") || Intensity.getStringFromValue(record.getIntensity()).equals("RECOVERY")) {
                    System.out.print("Lap:" + lapNo);
                    if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                        System.out.print(" lv" + lapExtraRecords.get(i).level.intValue());
                    }
                        if (record.getTotalTimerTime() != null) {
                        System.out.print(" LapTime: " + sec2minSecShort(record.getTotalTimerTime()));
                    }
                    System.out.print(" HR start:" + lapExtraRecords.get(i).hrStart);
                    System.out.print(" max:" + lapRecords.get(i).getMaxHeartRate());
                    System.out.print("" + (lapExtraRecords.get(i).hrMin-lapRecords.get(i).getMaxHeartRate()));
                    System.out.print("-->min:" + lapExtraRecords.get(i).hrMin);
                    System.out.print(" end:" + lapExtraRecords.get(i).hrEnd);
                    if (record.getTotalDistance() != null) {
                        System.out.print("--Dist:" + record.getTotalDistance());
                    }
                    printLapAvgMaxSpeed(record.getAvgSpeed(), record.getMaxSpeed());
                    if (record.getAvgCadence() != null) {
                        System.out.print("--Cad avg:" + record.getAvgCadence());
                        System.out.print(" max:" + record.getMaxCadence());
                    }
                    if (record.getAvgPower() != null) {
                        System.out.print("--Pow avg:" + record.getAvgPower());
                        System.out.print(" max:" + record.getMaxPower());
                    }
                    if (lapExtraRecords.get(i).avgDragFactor != null) {
                        System.out.print("--DFavg:" + (int) Math.round(lapExtraRecords.get(i).avgDragFactor));
                        System.out.print(" max:" + (int) Math.round(lapExtraRecords.get(i).maxDragFactor));
                    }
                    if (lapExtraRecords.get(i).avgStrokeLen != null) {
                        System.out.print("--SLavg:" + lapExtraRecords.get(i).avgStrokeLen);
                        System.out.print(" max:" + lapExtraRecords.get(i).maxStrokeLen);
                    }
                    System.out.println();
                }
                i++;
                lapNo++;
            }
            System.out.println("--------------------------------------------------");
            System.out.println("---- ACTIVE LAPS ----");
            i=0;
            lapNo=1;
            for (LapMesg record : lapRecords) {
                if (Intensity.getStringFromValue(record.getIntensity()).equals("ACTIVE")) {
                    System.out.print("Lap" + lapNo);
                    if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                        System.out.print(" lv" + lapExtraRecords.get(i).level.intValue());
                    }
                        if (i > 0) {
                        System.out.print(" HRmin" + lapExtraRecords.get(i-1).hrMin);
                    } else {
                        System.out.print(" HR");
                    }
                    System.out.print(">st" + lapExtraRecords.get(i).hrStart);
                    System.out.print("+" + (lapRecords.get(i).getMaxHeartRate()-lapExtraRecords.get(i).hrMin));
                    System.out.print("->max" + lapRecords.get(i).getMaxHeartRate());
                    System.out.print(" end" + lapExtraRecords.get(i).hrEnd);
                    if (record.getTotalTimerTime() != null) {
                        System.out.print(" " + sec2minSecShort(record.getTotalTimerTime()) + "min");
                    }
                    if (record.getAvgCadence() != null) {
                        System.out.print(" " + record.getAvgCadence() + "spm");
                    }
                    if (isSkiErgFile()) {}
                    printLapAvgSpeed(record.getAvgSpeed());
                    if (record.getAvgPower() != null) {
                        System.out.print(" " + record.getAvgPower() + "W");
                    }
                    if (record.getTotalDistance() != null) {
                        System.out.print(" " + m2km1(record.getTotalDistance()) + "km");
                    }
                    if (lapExtraRecords.get(i).avgDragFactor != null) {
                        System.out.print(" df" + (int) Math.round(lapExtraRecords.get(i).avgDragFactor));
                    }
                    if (lapExtraRecords.get(i).avgStrokeLen != null) {
                        System.out.print(" sl" + lapExtraRecords.get(i).avgStrokeLen);
                    }
                        System.out.println();
                }
                i++;
                lapNo++;
            }
            printLapEndSum(activeAvgCad, activeAvgSpeed, activeAvgPower, activeDist);
            System.out.println("---- REST LAPS ----");
            i=0;
            lapNo=1;
            for (LapMesg record : lapRecords) {
                if (Intensity.getStringFromValue(record.getIntensity()).equals("REST") || Intensity.getStringFromValue(record.getIntensity()).equals("RECOVERY")) {
                    System.out.print("Lap" + lapNo);
                    if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                        System.out.print(" lv" + lapExtraRecords.get(i).level.intValue());
                    }
                    System.out.print(" HRst" + lapExtraRecords.get(i).hrStart);
                    System.out.print(">max" + lapRecords.get(i).getMaxHeartRate());
                    System.out.print("" + (lapExtraRecords.get(i).hrMin-lapRecords.get(i).getMaxHeartRate()));
                    System.out.print("->min" + lapExtraRecords.get(i).hrMin);
                    System.out.print(" end" + lapExtraRecords.get(i).hrEnd);
                    if (record.getTotalTimerTime() != null) {
                        System.out.print(" " + sec2minSecShort(record.getTotalTimerTime()) + "min");
                    }
                    if (record.getAvgCadence() != null) {
                        System.out.print(" " + record.getAvgCadence() + "spm");
                    }
                    printLapAvgSpeed(record.getAvgSpeed());
                    if (record.getAvgPower() != null) {
                        System.out.print(" " + record.getAvgPower() + "W");
                    }
                    if (record.getTotalDistance() != null) {
                        System.out.print(" " + m2km1(record.getTotalDistance()) + "km");
                    }
                    System.out.println();
                }
                i++;
                lapNo++;
            }
            printLapEndSum(restAvgCad, restAvgSpeed, restAvgPower, restDist);
            System.out.println("--------------------------------------------------");
        }
        catch (FitRuntimeException e) {
            System.out.println("LAP ERROR!!!!");
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printSecRecords0 () {
        int i = 0;
        System.out.println("--------------------------------------------------");
        for (RecordMesg record : secRecords) {
            i++;
            if (i<11 || i>numberOfRecords-10 || i>3012 && i<3020) {
                System.out.print("Record:" + i);
                if (record.getTimestamp() != null) {
                    System.out.print(" Timestamp: " + record.getTimestamp());
                }
                /*if (lapRecords.get(0).getStartTime() != null) {
                    System.out.print(" LapStartTime: " + lapRecords.get(0).getStartTime());
                }*/
                if (record.getHeartRate() != null) {
                    System.out.print(" HR: " + record.getHeartRate());
                }
                if (record.getCadence() != null) {
                    System.out.print(" Cad: " + record.getCadence());
                }
                if (record.getSpeed() != null) {
                    System.out.print(" Speed: " + record.getSpeed());
                }
                if (record.getDistance() != null) {
                    System.out.print(" Dist: " + record.getDistance());
                }
                if (record.getPositionLat() != null && record.getPositionLong() != null) {
                    System.out.print(" Position: (" + record.getPositionLat() + ", " + record.getPositionLong() + ")");
                }
                //Iterable<DeveloperField> devFields = new ArrayList<>();
                //devFields = record.getDeveloperFields();
                //List<Field> allFields = new ArrayList<>();
                //allFields = record.fields();
                //Iterable devFields2 = record.getDeveloperFields();
                //System.out.println(" GETVALUE2: " + allFields.get(0).getStringValue());
                for (DeveloperField field : record.getDeveloperFields()) {
                    System.out.print(", " + field.getName() + ":" + field.getStringValue());
                    //System.out.print(", " + field.getAppId());
                    //System.out.print(", " + field.getAppUUID());
                    System.out.print(", " + field.getDeveloperDataIndex());
                    System.out.print(", " + field.getNum());
                    /*for (int j = 1; j < field.getNumValues(); j++) {
                        System.out.print(", " + field.getValue(j));
                    }*/
                    //System.out.println();
                }
                /*for (Field field : record.getFields()) {
                    System.out.print(", " + field.getName() + ":" + field.getStringValue());
                    //System.out.print(", " + field.getAppId());
                    //System.out.print(", " + field.getAppUUID());
                    //System.out.print(", " + field.getDeveloperDataIndex());
                    System.out.print(", " + field.getNum());
                    for (int j = 1; j < field.getNumValues(); j++) {
                        System.out.print(", " + field.getValue(j));
                    }
                    System.out.println();
                }*/
                System.out.println();
            }
        }
        System.out.println("--------------------------------------------------");
    }
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printSecRecords () {
        int i = 0;
        System.out.println("--------------------------------------------------");
        for (RecordMesg record : secRecords) {
            if (i<11 || i>numberOfRecords-10 || i>3012 && i<3020) {
                System.out.print("Record:" + i);
                if (record.getTimestamp() != null) {
                    System.out.print(" Timestamp: " + record.getTimestamp());
                }
                /*if (lapRecords.get(0).getStartTime() != null) {
                    System.out.print(" LapStartTime: " + lapRecords.get(12).getStartTime());
                }*/
                if (record.getHeartRate() != null) {
                    System.out.print(" Heart Rate: " + record.getHeartRate());
                }
                if (record.getSpeed() != null) {
                    System.out.print(" Speed: " + record.getSpeed());
                }
                if (record.getEnhancedSpeed() != null) {
                    System.out.print(" EnhSp: " + record.getEnhancedSpeed());
                }
                if (record.getDistance() != null) {
                    System.out.print(" Dist: " + record.getDistance());
                }
                if (record.getCadence() != null) {
                    System.out.print(" Cad: " + record.getCadence());
                }
                if (record.getPower() != null) {
                    System.out.print(" Pow: " + record.getPower());
                }
                if (secExtraRecords.get(i).C2DateTime != null) {
                    System.out.print(" C2time: " + secExtraRecords.get(i).C2DateTime);
                }
                if (record.getPositionLat() != null && record.getPositionLong() != null) {
                    System.out.print(" Position: (" + record.getPositionLat() + ", " + record.getPositionLong() + ")");
                }
                /*if (secExtraRecords.get(i).lapNo != 0) {
                    System.out.print(" LapNo: " + secExtraRecords.get(i).lapNo);
                }*/
                System.out.print(" DEV:");
                for (DeveloperField field : record.getDeveloperFields()) {
                    System.out.print(", " + field.getName() + ":" + field.getValue( 0 ));
                    for (int j = 1; j < field.getNumValues(); j++) {
                        System.out.print(", " + field.getValue(j));
                    }
                }

                System.out.println();
            }
            i++;
        }
        System.out.println("--------------------------------------------------");
    }
}