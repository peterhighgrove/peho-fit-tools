package se.peho.fittools.core;
import com.garmin.fit.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

import se.peho.fittools.core.strings.*;

//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
public class FitFileForIndoor extends FitFile {

    public String savedStrOrgFileInfo = "";

    int numberOfDevFields;
    String devAppToRemove = "9a0508b9-0256-4639-88b3-a2690a14ddf9";
    //List <Integer> devFieldsToRemove = Arrays.asList("Strokes", "Calories", "Distance", "Speed", "Power", 2, 6, 7);
    List <Integer> devFieldsToRemove = Arrays.asList(10, 11, 12, 23, 1, 2, 6, 7);
    List <String> devFieldNamesToUpdate = Arrays.asList("Training_session", "MaxHRevenLaps");

    String devAppToModify = "9a0508b9-0256-4639-88b3-a2690a14ddf9";

    Float activeFakeSumSpeed = 0f;
    Float activeFakeSumCad = 0f;
    Float activeFakeSumPower = 0f;
    
    int c2SyncSecondsLapDistCalc = 0; // for distance, speed
    int c2SyncSecondsC2File = 0; // for power, cadence

    boolean debugLaps      = false;
    boolean debugFixData   = false;
    boolean debugSync      = false;
    boolean debugSplit     = false;
    boolean debugDevFields = false;

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public FitFileForIndoor (int syncSecC2File, int syncSecLapDistCalc) {
    	this.c2SyncSecondsC2File = syncSecC2File;
    	this.c2SyncSecondsLapDistCalc = syncSecLapDistCalc;
    }
    public FitFileForIndoor () {
    	
    }

    /** Apply debug flags from Conf to this instance. Call right after construction. */
    public void setDebugFlags(Conf conf) {
        this.debugLaps      = conf.isDebugLaps();
        this.debugFixData   = conf.isDebugFixData();
        this.debugSync      = conf.isDebugSync();
        this.debugSplit     = conf.isDebugSplit();
        this.debugDevFields = conf.isDebugDevFields();
        if (conf.isDebug()) {
            System.out.println("[FitFileForIndoor] debugLaps=" + debugLaps
                + "  debugFixData=" + debugFixData
                + "  debugSync=" + debugSync
                + "  debugSplit=" + debugSplit
                + "  debugDevFields=" + debugDevFields);
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void wktAddSteps(String wktSteps, String wktName) {

        System.out.println("---------> WKT COMMAND MADE!");

        // Find The place to add WKT messages
        //----------------------
        int insertIx = 0;
        for (Mesg mesg : getAllMesg()) {
            if (mesg.getNum() == MesgNum.RECORD) {
                break;
            }
            insertIx++;
        }

        // ADD wktRecord if EMPTY
        //----------------------
        /* if (wktRecordMesg.isEmpty()) {
            Mesg wktRecord = new WorkoutMesg();
            wktRecordMesg.add(wktRecord);
            allMesg.add(insertIx, wktRecord);
            insertIx++;
            System.out.println("---------> NO wktRecord, ADDING!");
        } */

        // ADD wktName if not empty in arguments
        //----------------------
        if (!wktName.equals("")) {
            getWktRecordMesg().get(0).setFieldValue(WKT_NAME, wktName);
        }

        // ADD wktSession Record if EMPTY
        //----------------------
        /* if (wktSessionMesg.isEmpty()) {
            Mesg wktSessionRecord = new WorkoutSessionMesg();
            getWktSessionMesg().add(wktSessionRecord);
            getAllMesg().add(insertIx, wktSessionRecord);
            insertIx++;
            System.out.println("---------> NO wktSession, ADDING!");
        } */

        // ADD wktStepRecords if EMPTY
        //----------------------
        if (getWktStepMesg().isEmpty()) {
            for (Mesg lap : getLapMesg()) {
                System.out.println("---------> Adding wktStep for lap!");
                Mesg wktStepRecord = new WorkoutStepMesg();
                getWktStepMesg().add(wktStepRecord);
                getAllMesg().add(insertIx, wktStepRecord);
                insertIx++;
            }
            System.out.println("---------> NO wktSteps, ADDING!");
        }

        System.out.println("---------> StepIntensity changing to: " + wktSteps);

        int recordIx = 0;
        for (Mesg lap : getLapMesg()) {
            switch (wktSteps.toLowerCase()) {

                case "allactive":
                    lap.setFieldValue(LAP_INTENSITY, Intensity.ACTIVE);
                    if (recordIx == 0) {
                        lap.setFieldValue(LAP_INTENSITY, Intensity.WARMUP);
                    }
                    break;

                case "warmupthenactive":
                    if (((recordIx+1) % 2) == 0) {
                        lap.setFieldValue(LAP_INTENSITY, Intensity.ACTIVE);
                    }
                    if (((recordIx+1) % 2) == 1) {
                        lap.setFieldValue(LAP_INTENSITY, Intensity.RECOVERY);
                    }
                    if (recordIx == 0) {
                        lap.setFieldValue(LAP_INTENSITY, Intensity.WARMUP);
                    }
                    if (recordIx+1 == getLapMesg().size()) {
                        lap.setFieldValue(LAP_INTENSITY, Intensity.COOLDOWN);
                    }
                    break;

                case "restthenactive":
                    if (((recordIx+1) % 2) == 0) {
                        lap.setFieldValue(LAP_INTENSITY, Intensity.ACTIVE);
                    }
                    if (((recordIx+1) % 2) == 1) {
                        lap.setFieldValue(LAP_INTENSITY, Intensity.RECOVERY);
                    }
                    if ((recordIx+1) == getLapMesg().size()) {
                        lap.setFieldValue(LAP_INTENSITY, Intensity.COOLDOWN);
                    }
                    break;

                case "activethenrest":
                    if (((recordIx+1) % 2) == 0) {
                        lap.setFieldValue(LAP_INTENSITY, Intensity.RECOVERY);
                    }
                    if (((recordIx+1) % 2) == 1) {
                        lap.setFieldValue(LAP_INTENSITY, Intensity.ACTIVE);
                    }
                    if ((recordIx+1) == getLapMesg().size()) {
                        lap.setFieldValue(LAP_INTENSITY, Intensity.COOLDOWN);
                    }
                    break;

                case "2warmupthenactive":
                    if (((recordIx+1) % 2) == 0) {
                        lap.setFieldValue(LAP_INTENSITY, (Intensity.RECOVERY));
                    }
                    if (((recordIx+1) % 2) == 1) {
                        lap.setFieldValue(LAP_INTENSITY, (Intensity.ACTIVE));
                    }
                    if (recordIx == 0) {
                        lap.setFieldValue(LAP_INTENSITY, Intensity.WARMUP);
                    }
                    if (recordIx == 1) {
                        lap.setFieldValue(LAP_INTENSITY, Intensity.WARMUP);
                    }
                    if ((recordIx+1) == getLapMesg().size()) {
                        lap.setFieldValue(LAP_INTENSITY, Intensity.COOLDOWN);
                    }
                    break;

                case "nochange":
                    break;

                default:
                    System.out.println("==========> NO CORRRECT wkt command. Allowed: allActive, warmupThenActive, restThenActive, activeThenRest, noChange");
            }
            System.out.println("---------> Lap: " + (recordIx+1) + " set to " + 
                Intensity.getStringFromValue(Intensity.getByValue(lap.getFieldShortValue(LAP_INTENSITY))));
            recordIx++;
        }

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void removeDeveloperFieldDefinitions(
        List<Mesg> allMesg,
        List<Mesg> fieldDescrMesg,
        String devAppToModify,
        List<Integer> devFieldsToRemove) {

        if (debugDevFields) System.out.println("----- REMOVE DEV FIELDS for ALL MESG -----");
        if (debugDevFields) System.out.println("--- No of Dev Fields: " + fieldDescrMesg.size());

        // --- Step 1: find developer_data_index for this app
        Short devIndex = null;

        for (Mesg mesg : allMesg) {
            if (mesg.getNum() == MesgNum.DEVELOPER_DATA_ID) {
                DeveloperDataIdMesg devId = new DeveloperDataIdMesg(mesg);
                Byte[] appIdBytesObj = devId.getApplicationId();

                if (appIdBytesObj != null) {
                    byte[] appIdBytes = new byte[appIdBytesObj.length];
                    for (int i = 0; i < appIdBytesObj.length; i++) {
                        appIdBytes[i] = appIdBytesObj[i];
                    }

                    String appIdHex = bytesToHex(appIdBytes).toLowerCase();
                    String targetId = devAppToModify.toLowerCase().replace("-", "");

                    if (debugDevFields) System.out.println("   AppId (hex): " + appIdHex);
                    if (debugDevFields) System.out.println("   Compare to  : " + targetId);

                    // Compare the App IDs
                    if (appIdHex.equals(targetId)) {
                        devIndex = devId.getDeveloperDataIndex();
                        if (debugDevFields) System.out.println("Match! Developer index for app: " + devIndex);
                        break; // Stop when matched
                    }
                }
            }
        }

        if (devIndex == null) {
            if (debugDevFields) System.out.println("No developer data found for app " + devAppToModify);
            return;
        }

        // --- Step 2: remove matching developer payload fields from all messages
        // IMPORTANT: encoder requires payload and definition lists to stay consistent.
        Set<Integer> fieldNumsToRemove = new HashSet<>(devFieldsToRemove);
        int removedPayloadFields = 0;
        for (Mesg mesg : allMesg) {
            Iterable<DeveloperField> devFields = mesg.getDeveloperFields();
            if (devFields == null) {
                continue;
            }

            Iterator<DeveloperField> devIt = devFields.iterator();
            while (devIt.hasNext()) {
                DeveloperField devField = devIt.next();
                if (devField != null
                        && devField.getDeveloperDataIndex() == devIndex
                        && fieldNumsToRemove.contains(devField.getNum())) {
                    devIt.remove();
                    removedPayloadFields++;
                }
            }
        }
        if (debugDevFields) {
            System.out.println("Removed developer payload fields: " + removedPayloadFields);
        }

        // --- Step 3: remove matching FieldDescriptionMesg entries
        Iterator<Mesg> it = allMesg.iterator();
        while (it.hasNext()) {
            Mesg m = it.next();
            if (m.getNum() == MesgNum.FIELD_DESCRIPTION) {
                FieldDescriptionMesg f = new FieldDescriptionMesg(m);

                if (f.getDeveloperDataIndex() == devIndex &&
                        devFieldsToRemove.contains((int) f.getFieldDefinitionNumber())) {

                    if (debugDevFields) System.out.println("Removing dev field def #" + f.getFieldDefinitionNumber() +
                            " for app index " + devIndex);
                    it.remove();
                }
            }
        }
        fieldDescrMesg.clear();
        // Rebuild fieldDescrMesg list
        for (Mesg m : allMesg) {
            if (m.getNum() == MesgNum.FIELD_DESCRIPTION) {
                fieldDescrMesg.add(m);
            }   
        }
        numberOfDevFields = fieldDescrMesg.size();

        if (debugDevFields) System.out.println("--- Remaining dev fields: " + numberOfDevFields);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void removeDevFieldDescr() {

        removeDeveloperFieldDefinitions(
            getAllMesg(),
            getFieldDescrMesg(),
            devAppToModify,
            devFieldsToRemove
        );

        if (debugDevFields) System.out.println("--- No of Dev Fields: " + numberOfDevFields);
        if (debugDevFields) System.out.println("--------------------------------------------------");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public Boolean hasC2FitFile(String filename) {
        Boolean isTrue = false;
        try {
            // Verify the file exists and is a valid FIT file
            File file1 = new File(filename);
            if (file1.exists()) { // || !file1.isTrue()
                isTrue = true;
            } else {
                System.out.println("==========> c2FitFile NOT found: " + filename);
                System.out.println("==========> RUN with values in WATCH FILE.");
                //System.exit(0);
            }
        } catch (Exception e) {
            throw new RuntimeException("==========> Error opening c2FitFile: " + filename);
        }

        return isTrue;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void setNewSport(Sport sport, SubSport subsport) {

        if (!getWktRecordMesg().isEmpty()) {
            getWktRecordMesg().get(0).setFieldValue(WKT_SPORT, sport.getValue());
            getWktRecordMesg().get(0).setFieldValue(WKT_SUBSPORT, subsport.getValue());
        }

        getSessionMesg().get(0).setFieldValue(SES_SPORT, sport.getValue());
        getSessionMesg().get(0).setFieldValue(SES_SUBSPORT, subsport.getValue());

        for (Mesg lap : getLapMesg()) {
            lap.setFieldValue(LAP_SPORT, sport.getValue());
            lap.setFieldValue(LAP_SUBSPORT, subsport.getValue());
        }

        // --- NEW: also update "Activity Metrics" message if present ---
        Field sportField;
        for (Mesg mesg : getAllMesg()) {
            switch (mesg.getNum()) {
                case 140:  // undocumented Activity Metrics
                    sportField = mesg.getField(11);
                    if (sportField != null) {
                        mesg.setFieldValue(11, sport.getValue());
                        mesg.setFieldValue(12, subsport.getValue());
                    }
                    break;
                case MesgNum.SPLIT:
                    sportField = mesg.getField(11);
                    if (sportField != null) {
                        mesg.setFieldValue(11, sport.getValue());
                        mesg.setFieldValue(12, subsport.getValue());
                    }
                    break;
                case MesgNum.SPORT:
                    sportField = mesg.getField(SP_SPORT);
                    if (sportField != null) {
                        mesg.setFieldValue(SP_SPORT, sport.getValue());
                    }
                    Field subSportField = mesg.getField(SP_SUBSPORT);
                    if (subSportField != null) {
                        mesg.setFieldValue(SP_SUBSPORT, subsport.getValue());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void setNewSportSkiErg() {
        setNewSport(Sport.FITNESS_EQUIPMENT, SubSport.INDOOR_ROWING);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void setNewSportElliptical() {
        setNewSport(Sport.FITNESS_EQUIPMENT, SubSport.ELLIPTICAL);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void mergeC2CiqAndFitData(FitFileForIndoor c2FitFile, int C2FitFileDistanceStartCorrection) {
        

        Long C2DateTime = null;

        int noneC2dataCounter = 0;
        int pauseRecordCounter = 0;
        int sameDistCounter = 1;
        
        System.out.println("----- MERGE C2 CIQ DATA into ALL MESG -----");
        int c2RecordIx = 0;
        int recordIx = 0;

        int maxIxFixEmptyBeginning = 100;
        int maxCadenceValue = 74;
        boolean lookingInBeginningForEmptySpeed = true;
        boolean lookingInBeginningForEmptyCadence = true;
        boolean lookingInBeginningForEmptyPower = true;
        boolean lookingInBeginningForEmptyStrokeLength = true;
        boolean lookingInBeginningForEmptyDragFactor = true;
        boolean lookingInBeginningForEmptyTrainingSession = true;
        
        if (getNumberOfRecords() < maxIxFixEmptyBeginning) {
            maxIxFixEmptyBeginning = getNumberOfRecords() - 1;
        }

        for (Mesg record : getRecordMesg()) {

            Float distFromDevField = 0f;
            Float speedFromDevField = 0f;
            Float strokeLengthFromDevField = 0f;
            Float dragFactorFromDevField = 0f;
            Float trainingSessionFromDevField = 0f;

            //--------------
            // Initiate secExtraRecords
            int lapNo = 1; // only for INIT of secExtraRecords for now
            getSecExtraRecords().add(new RecordExtraMesg(lapNo, C2DateTime));
            
            //--------------
            // Look for HR drop outs
            Short hr = record.getFieldShortValue(REC_HR);
            if (hr == null) {
                System.out.println(">>>>>>> HR EMPTY.  recordIx:" + recordIx);
                if (recordIx > 0) {
                    Short hrPrev = getRecordMesg().get(recordIx - 1).getFieldShortValue(REC_HR);
                    if (hrPrev != null) {
                        record.setFieldValue(REC_HR, hrPrev);
                    }
                }
            }

            // =========== MERGE/Import CIQ developer fields to native (generic access) ============
            for (DeveloperField field : record.getDeveloperFields()) {

                String name = field.getName();
                if (name == null) {
                    continue;
                }
                switch (name) {
                    case "Distance" -> {
                        distFromDevField = field.getFloatValue();
                        // set native distance field
                        record.setFieldValue(REC_DIST, distFromDevField);
                        field.setValue(0f);
                        }
                    /* case "Cadence" -> {
                        Short cadFromDevField = field.getShortValue();
                        if (cadFromDevField != null) {
                            record.setFieldValue(REC_CAD, cadFromDevField);
                        }
                        field.setValue((short)0);
                        } */
                    case "Power" -> {
                        Integer powerFromDevField = field.getIntegerValue();
                        if (powerFromDevField != null) {
                            record.setFieldValue(REC_POW, powerFromDevField);
                        }
                        field.setValue(0);
                    }
                    case "Speed" -> {
                        speedFromDevField = field.getFloatValue();
                        if (speedFromDevField != null) {
                            record.setFieldValue(REC_SPEED, speedFromDevField);
                            record.setFieldValue(REC_ESPEED, speedFromDevField);
                        }
                        field.setValue(0f);
                    }
                    case "StrokeLength" -> {
                        strokeLengthFromDevField = field.getFloatValue();
                    }
                    case "DragFactor" -> {
                        dragFactorFromDevField = field.getFloatValue();
                    }
                    default -> {
                        continue;
                    }
                }
            }

            // =========== Distance Smoothing =============
            // Smoothing distance records if 2 following record are the same, then calc avg for the one before and after the 2
            // ============================================
            if (recordIx >= 3 && recordIx < getNumberOfRecords()-2) {
                Float distCurrent = record.getFieldFloatValue(REC_DIST);
                Float distPrev = getRecordMesg().get(recordIx-1).getFieldFloatValue(REC_DIST);

                if (distCurrent != null && distPrev != null && distCurrent.equals(distPrev)) {

                    Mesg recordNext = getRecordMesg().get(recordIx+1);
                    Float distNextFromDevField = 0f;

                    //System.out.println("==========> Same dist in a row: " + recordIx + ", " + sameDistCounter + " @ " + currentDist + ", " + record.getTimestamp());
                    if (sameDistCounter>1) {
                        System.out.println("==========> MORE 1 Same dist in a row: " + recordIx + ", " + sameDistCounter + " @ " + distCurrent + ", " + FitDateTime.toString(record.getFieldLongValue(REC_TIME)));
                    }
                    for (DeveloperField fieldRecordNext : recordNext.getDeveloperFields()) {
                        if ("Distance".equals(fieldRecordNext.getName())) {
                            distNextFromDevField = fieldRecordNext.getFloatValue();
                        }
                    }
                    
                    Mesg recordPrev1 = getRecordMesg().get(recordIx-1);
                    Mesg recordPrev2 = getRecordMesg().get(recordIx-2);
                    Float distPrev2 = recordPrev2.getFieldFloatValue(REC_DIST);
                    Float distStepNew = (distNextFromDevField - distPrev2) / 3;
                    
                    recordPrev1.setFieldValue(REC_DIST, distPrev2 + distStepNew);
                    record.setFieldValue(REC_DIST, distPrev2 + distStepNew*2);
                    //System.out.println("-------->" + getRecordMesg().get(recordIx-2).getFieldFloatValue(REC_DIST) + " " + currentDistBack1 + "->" + getRecordMesg().get(recordIx-1).getFieldFloatValue(REC_DIST) + " " + currentDist + "->" + record.getFieldFloatValue(REC_DIST) + " " + currentDistNext);
                    sameDistCounter++;
                }
                else {
                    sameDistCounter = 1;
                }
            }

            // =========== MERGE/Import C2 fitfile =============
            // =================================================
            while (c2FitFile.getRecordMesg().get(c2RecordIx).getFieldFloatValue(REC_DIST) - 0.5 <= record.getFieldFloatValue(REC_DIST) - C2FitFileDistanceStartCorrection) {
                record.setFieldValue(REC_CAD, c2FitFile.getRecordMesg().get(c2RecordIx).getFieldShortValue(REC_CAD));
                //record.setFieldValue(REC_POW, c2FitFile.getRecordMesg().get(c2RecordIx).getFieldIntegerValue(REC_POW));
                getSecExtraRecords().get(recordIx).setC2DateTime(c2FitFile.getRecordMesg().get(c2RecordIx).getFieldLongValue(REC_TIME));
                c2RecordIx++;
                if (c2RecordIx > c2FitFile.getNumberOfRecords() - 1) {
                    c2RecordIx--;
                    break;
                }
            }
            // =========== Fix EMPTY beginning of data ================
            // ========================================================
            if (recordIx <= maxIxFixEmptyBeginning) {
                // FIX SPEED
                if (lookingInBeginningForEmptySpeed) {
                    Float speed = record.getFieldFloatValue(REC_ESPEED);
                    if (speed != null && speed != 0f) {
                        if (debugFixData) System.out.println("========= FIXED Beginning SPEED, first value: " + speed + " @ " + recordIx);
                        for (int i = recordIx-1; i >= 0; i--) {
                            Mesg recordToFix = getRecordMesg().get(i);
                            if (debugFixData) System.out.println("========= FIXING SPEED, value: " + recordToFix.getFieldFloatValue(REC_ESPEED) + "->" + speed + " @" + i);
                            recordToFix.setFieldValue(REC_SPEED, speed);
                            recordToFix.setFieldValue(REC_ESPEED, speed);
                        }
                        lookingInBeginningForEmptySpeed = false;
                    }
                }
                // FIX CADENCE
                if (lookingInBeginningForEmptyCadence) {
                    Short cad = record.getFieldShortValue(REC_CAD);
                    if (cad == null) cad=0;
                    if (cad != null && cad != 0) {
                        if (debugFixData) System.out.println("========= FIXED Beginning CADENCE, first value: " + cad + " @ " + recordIx);
                        for (int i = recordIx-1; i >= 0; i--) {
                            Mesg recordToFix = getRecordMesg().get(i);
                            if (debugFixData) System.out.println("========= FIXING CADENCE, value: " + recordToFix.getFieldShortValue(REC_CAD) + "->" + cad + " @" + i);
                            recordToFix.setFieldValue(REC_CAD, cad);
                        }
                        lookingInBeginningForEmptyCadence = false;
                    }
                }
                // FIX POWER
                if (lookingInBeginningForEmptyPower) {
                    Integer power = record.getFieldIntegerValue(REC_POW);
                    if (power != null && power != 0) {
                        if (debugFixData) System.out.println("========= FIXED Beginning POWER, first value: " + power + " @ " + recordIx);
                        for (int i = recordIx-1; i >= 0; i--) {
                            Mesg recordToFix = getRecordMesg().get(i);
                            if (debugFixData) System.out.println("========= FIXING POWER, value: " + recordToFix.getFieldIntegerValue(REC_POW) + "->" + power + " @" + i);
                            recordToFix.setFieldValue(REC_POW, power);
                        }
                        lookingInBeginningForEmptyPower = false;
                    }
                }
                // FIX STROKE LENGTH
                if (lookingInBeginningForEmptyStrokeLength) {
                    if ((strokeLengthFromDevField!=null && strokeLengthFromDevField!=0)) {
                        if (debugFixData) System.out.println("========= FIXED Beginning STROKE LENGTH, first value: " + strokeLengthFromDevField + " @ " + recordIx);
                        for (int i = recordIx-1; i >= 0; i--) {
                            Mesg recordToFix = getRecordMesg().get(i);
                            for (DeveloperField field : recordToFix.getDeveloperFields()) {
                                if ("StrokeLength".equals(field.getName())) {
                                    if (debugFixData) System.out.println("========= FIXING STROKE LENGTH, value: " + field.getValue() + "->" + strokeLengthFromDevField + " @" + i);
                                    field.setValue(strokeLengthFromDevField);
                                }
                            }
                        }
                        lookingInBeginningForEmptyStrokeLength = false;
                    }
                }
                // FIX DRAG FACTOR
                if (lookingInBeginningForEmptyDragFactor) {
                    if ((dragFactorFromDevField!=null && (dragFactorFromDevField!=1 && dragFactorFromDevField!=0))) {
                        if (debugFixData) System.out.println("========= FIXED Beginning DRAG FACTOR, first value: " + dragFactorFromDevField + " @ " + recordIx);
                        for (int i = recordIx-1; i >= 0; i--) {
                            Mesg recordToFix = getRecordMesg().get(i);
                            for (DeveloperField field : recordToFix.getDeveloperFields()) {
                                if ("DragFactor".equals(field.getName())) {
                                    if (debugFixData) System.out.println("========= FIXING DRAG FACTOR, value: " + field.getValue() + "->" + dragFactorFromDevField + " @" + i);
                                    field.setValue(dragFactorFromDevField);
                                }
                            }
                        }
                        lookingInBeginningForEmptyDragFactor = false;
                    }
                }
                // FIX TRAINING_SESSION
                if (lookingInBeginningForEmptyTrainingSession) {
                    if ((trainingSessionFromDevField!=null && trainingSessionFromDevField!=1)) {
                        if (debugFixData) System.out.println("========= FIXED Beginning TRAINING_SESSION, first value: " + trainingSessionFromDevField + " @ " + recordIx);
                        for (int i = recordIx-1; i >= 0; i--) {
                            Mesg recordToFix = getRecordMesg().get(i);
                            for (DeveloperField field : recordToFix.getDeveloperFields()) {
                                if ("Training_session".equals(field.getName())) {
                                    if (debugFixData) System.out.println("========= FIXING TRAINING_SESSION, value: " + field.getValue() + "->" + trainingSessionFromDevField + " @" + i);
                                    field.setValue(trainingSessionFromDevField);
                                }
                            }
                        }
                        lookingInBeginningForEmptyTrainingSession = false;
                    }
                }
            }

            // =========== CHANGE TO 0 VALUES WHEN PAUSED ============
            // =================================================
            if (!lookingInBeginningForEmptyCadence) {
                Short cadVal = record.getFieldShortValue(REC_CAD);
                if (cadVal == null) {
                //if (secExtraRecords.get(recordIx).C2DateTime == null) {
                    noneC2dataCounter++;
                    if (noneC2dataCounter >= 7) {
                        System.out.println("============ C2time null >= 7 times. "+ noneC2dataCounter + "recordIx: " + recordIx);
                        pauseRecordCounter++;
                        record.setFieldValue(REC_SPEED, 0f);
                        record.setFieldValue(REC_ESPEED, 0f);
                        record.setFieldValue(REC_CAD, (short)0);
                        record.setFieldValue(REC_POW, 0);
                        if (noneC2dataCounter == 7 && recordIx > 4) {
                            System.out.println("===>>>>>>>>> START PAUSE C2time null 7 times. recordIx: " + recordIx);
                            Mesg recordPrev1 = getRecordMesg().get(recordIx-1);
                            recordPrev1.setFieldValue(REC_SPEED, 0f);
                            recordPrev1.setFieldValue(REC_ESPEED, 0f);
                            recordPrev1.setFieldValue(REC_CAD, (short)0);
                            recordPrev1.setFieldValue(REC_POW, 0);
                            Mesg recordPrev2 = getRecordMesg().get(recordIx-2);
                            recordPrev2.setFieldValue(REC_SPEED, 0f);
                            recordPrev2.setFieldValue(REC_ESPEED, 0f);
                            recordPrev2.setFieldValue(REC_CAD, (short)0);
                            recordPrev2.setFieldValue(REC_POW, 0);
                            Mesg recordPrev3 = getRecordMesg().get(recordIx-3);
                            recordPrev3.setFieldValue(REC_SPEED, 0f);
                            recordPrev3.setFieldValue(REC_ESPEED, 0f);
                            recordPrev3.setFieldValue(REC_CAD, (short)0);
                            recordPrev3.setFieldValue(REC_POW, 0);
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
            Short cadFixGaps = record.getFieldShortValue(REC_CAD);
            if (cadFixGaps == null) {
                Short prevCad = recordIx > 0 ? getRecordMesg().get(recordIx-1).getFieldShortValue(REC_CAD) : null;
                if (prevCad != null) {
                    record.setFieldValue(REC_CAD, prevCad);
                }
            }
            Integer pwr = record.getFieldIntegerValue(REC_POW);
            if (pwr == null) {
                Integer prevPow = recordIx > 0 ? getRecordMesg().get(recordIx-1).getFieldIntegerValue(REC_POW) : null;
                if (prevPow != null) {
                    record.setFieldValue(REC_POW, prevPow);
                }
            }
            // =========== Fix BAD PEAK/SPIKE data ==========
            // ==============================================
            if (recordIx>0) {
                Short cadFixSpike = record.getFieldShortValue(REC_CAD);
                Short cadLastFixSpike = getRecordMesg().get(recordIx-1).getFieldShortValue(REC_CAD);

                if ((cadFixSpike > maxCadenceValue) || (((cadFixSpike - cadLastFixSpike) > 9) && (cadLastFixSpike > 45))) {
                    if (debugFixData) System.out.println("=======>>> Fixed Cadence PEAK from: " + cadFixSpike + "->" + cadLastFixSpike);
                    record.setFieldValue(REC_CAD, cadLastFixSpike);
                }
            }

            recordIx++;
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    /* public void mergeCiqAndFitData() {

        int pauseRecordCounter = 0;
        int sameDistCounter = 1;
        
        int recordIx = 0;
        for (Mesg record : recordMesg) {

            Float strokeLengthFromDevField = 0f;
            Float dragFactorFromDevField = 0f;

            //--------------
            // Initiate secExtraRecords
            int lapNo = 1; // only for INIT of secExtraRecords for now
            secExtraRecords.add(new RecordExtraMesg(lapNo, null));
            
            //--------------
            // Look for HR drop outs
            
            if (record.getFieldShortValue(REC_HR) == null) {
            	System.out.println(">>>>>>> HR EMPTY.  recordIx:"+recordIx);
            	if (recordIx>0) {
            		record.setFieldValue(REC_HR, recordMesg.get(recordIx-1).getFieldShortValue(REC_HR));
            	}
            }

            // =========== MERGE/Import CIQ data to native =============
            // =========================================================
            for (DeveloperField field : record.getDeveloperFields()) {
                
                // Power:0, 9a0508b9-0256-4639-88b3-a2690a14ddf9, 0, 1
                // , Cadence:0, 9a0508b9-0256-4639-88b3-a2690a14ddf9, 0, 2
                // , Speed:2.543, 9a0508b9-0256-4639-88b3-a2690a14ddf9, 0, 6
                // , Distance:17, 9a0508b9-0256-4639-88b3-a2690a14ddf9, 0, 7
                // , StrokeLength:0.53, 9a0508b9-0256-4639-88b3-a2690a14ddf9, 0, 8
                // , DragFactor:81.0, 9a0508b9-0256-4639-88b3-a2690a14ddf9, 0, 9
                // , Training_session:0.0, 03dc80ed-6991-40b0-a0cb-23925913a501, 1, 1
                
                Float distFromDevField = 0f;
                Float speedFromDevField = 0f;
                Integer powerFromDevField = 0;
                Short cadFromDevField = 0;

                if ("Distance".equals(field.getName())) {
                    distFromDevField = field.getFloatValue();
                    //if (currentDist == null)  currentDist = 0f;
                    // set native distance field
                    record.setFieldValue(REC_DIST, distFromDevField);
                    field.setValue(0f);
                }
                if ("Cadence".equals(field.getName())) {
                    cadFromDevField = field.getShortValue();
                    //if (currentCadence == null) currentCadence = 0;
                    record.setFieldValue(REC_CAD, cadFromDevField);
                    field.setValue(0f);
                }
                if ("Power".equals(field.getName())) {
                    powerFromDevField = field.getIntegerValue();
                    //if (currentPower == null) currentPower = 0;
                    record.setFieldValue(REC_POW, powerFromDevField);
                    field.setValue(0f);
                }
                if ("Speed".equals(field.getName())) {
                    speedFromDevField = field.getFloatValue();
                    //if (currentSpeed == null) currentSpeed = 0f;
                    record.setFieldValue(REC_SPEED, speedFromDevField);
                    record.setFieldValue(REC_ESPEED, speedFromDevField);
                    field.setValue(0f);
                }
                if ("StrokeLength".equals(field.getName())) {
                    strokeLengthFromDevField = field.getFloatValue();
                }
                if ("DragFactor".equals(field.getName())) {
                    dragFactorFromDevField = field.getFloatValue();
                }
            }

            // =========== Distance Smoothing =============
            // Smoothing distance records if 2 following record are the same, then calc avg for the one before and after the 2
            // ============================================
            if (recordIx >= 3 && recordIx < numberOfRecords-2) {
                Float distCurrent = record.getFieldFloatValue(REC_DIST);
                Float distPrev = recordMesg.get(recordIx-1).getFieldFloatValue(REC_DIST);
                
                if (distCurrent.equals(distPrev)) {
                    Mesg recordNext = recordMesg.get(recordIx+1);
                    Float distNextFromDevField = 0f;
                    Float distPrev2 = recordMesg.get(recordIx-2).getFieldFloatValue(REC_DIST);
                    //System.out.println("==========> Same dist in a row: " + recordIx + ", " + sameDistCounter + " @ " + currentDist + ", " + record.getTimestamp());
                    if (sameDistCounter>1) {
                        System.out.println("==========> MORE 1 Same dist in a row: " + recordIx + ", " + sameDistCounter + " @ " + distCurrent + ", " + record.getFieldLongValue(REC_TIME));
                    }
                    for (DeveloperField fieldRecordNext : recordNext.getDeveloperFields()) {
                        if ("Distance".equals(fieldRecordNext.getName())) {
                            distNextFromDevField = fieldRecordNext.getFloatValue();
                        }
                    }

                    Float newDistStep = (distNextFromDevField - distPrev2) / 3;
                    Mesg recordPrev1 = recordMesg.get(recordIx-1);
                    recordPrev1.setFieldValue(REC_DIST, distPrev2 + newDistStep);
                    record.setFieldValue(REC_DIST, distPrev2 + newDistStep*2);
                    //System.out.println("-------->" + secRecords.get(recordIx-2).getDistance() + " " + currentDistBack1 + "->" + secRecords.get(recordIx-1).getDistance() + " " + currentDist + "->" + record.getDistance() + " " + currentDistNext);
                    sameDistCounter++;
                }
                else {
                    sameDistCounter = 1;
                }
            }

            // =========== Fix EMPTY beginning of data ================
            // ========================================================
            int maxIxFixEmptyBeginning = 100;
            int maxCadenceValue = 74;
            boolean lookingInBeginningForEmptySpeed = true;
            boolean lookingInBeginningForEmptyCadence = true;
            boolean lookingInBeginningForEmptyPower = true;
            boolean lookingInBeginningForEmptyStrokeLength = true;
            boolean lookingInBeginningForEmptyDragFactor = true;
            boolean lookingInBeginningForEmptyTrainingSession = true;
            
            if (numberOfRecords < maxIxFixEmptyBeginning) {
                maxIxFixEmptyBeginning = numberOfRecords - 1;
            }
            if (recordIx <= maxIxFixEmptyBeginning) {
                // FIX SPEED
                if (lookingInBeginningForEmptySpeed) {
                    Float speed = record.getFieldFloatValue(REC_ESPEED);
                    if (speed != null && speed !=0) {
                        for (int i = recordIx; i >= 0; i--) {
                            Mesg recordToFix = recordMesg.get(i);
                            recordToFix.setFieldValue(REC_SPEED, speed);
                            recordToFix.setFieldValue(REC_ESPEED, speed);
                        }
                        if (debugFixData) System.out.println("========= FIXED Beginning SPEED, first value: " + speed + " @ " + recordIx);
                        lookingInBeginningForEmptySpeed = false;
                    }
                }
                // FIX CADENCE
                if (lookingInBeginningForEmptyCadence) {
                    if ((record.getCadence()!=null && record.getCadence()!=0)) {
                        for (int i = recordIx-1; i >= 0; i--) {
                            Mesg recordToFix = recordMesg.get(i);
                            recordToFix.setFieldValue(REC_CADENCE, record.getCadence());
                        }
                        if (debugFixData) System.out.println("========= FIXED Beginning CADENCE, first value: " + record.getCadence() + " @ " + recordIx);
                        lookingInBeginningForEmptyCadence = false;
                    }
                }
                // FIX POWER
                if (lookingInBeginningForEmptyPower) {
                    if ((record.getPower()!=null && record.getPower()!=0)) {
                        for (int i = recordIx-1; i >= 0; i--) {
                            Mesg recordToFix = recordMesg.get(i);
                            recordToFix.setFieldValue(REC_POWER, record.getPower());
                        }
                        if (debugFixData) System.out.println("========= FIXED Beginning POWER, first value: " + record.getPower() + " @ " + recordIx);
                        lookingInBeginningForEmptyPower = false;
                    }
                }
                // FIX STROKE LENGTH
                if (lookingInBeginningForEmptyStrokeLength) {
                    if ((strokeLengthFromDevField!=null && strokeLengthFromDevField!=0)) {
                        for (int i = recordIx-1; i >= 0; i--) {
                            Mesg recordToFix = recordMesg.get(i);
                            for (DeveloperField field : recordToFix.getDeveloperFields()) {
                                if ("StrokeLength".equals(field.getName())) {
                                    field.setValue(strokeLengthFromDevField);
                                }
                            }
                        }
                        if (debugFixData) System.out.println("========= FIXED Beginning STROKE LENGTH, first value: " + strokeLengthFromDevField + " @ " + recordIx);
                        lookingInBeginningForEmptyStrokeLength = false;
                    }
                }
                // FIX DRAG FACTOR
                if (lookingInBeginningForEmptyDragFactor) {
                    if ((dragFactorFromDevField!=null && (dragFactorFromDevField!=1 && dragFactorFromDevField!=0))) {
                        for (int i = recordIx-1; i >= 0; i--) {
                            Mesg recordToFix = recordMesg.get(i);
                            for (DeveloperField field : recordToFix.getDeveloperFields()) {
                                if ("DragFactor".equals(field.getName())) {
                                    field.setValue(dragFactorFromDevField);
                                }
                            }
                        }
                        if (debugFixData) System.out.println("========= FIXED Beginning DRAG FACTOR, first value: " + dragFactorFromDevField + " @ " + recordIx);
                        lookingInBeginningForEmptyDragFactor = false;
                    }
                }
                // FIX TRAINING_SESSION
                if (lookingInBeginningForEmptyTrainingSession) {
                    if ((currentTrainingSession!=null && currentTrainingSession!=1)) {
                        for (int i = recordIx-1; i >= 0; i--) {
                            Mesg recordToFix = recordMesg.get(i);
                            for (DeveloperField field : recordToFix.getDeveloperFields()) {
                                if ("Training_session".equals(field.getName())) {
                                    field.setValue(currentTrainingSession);
                                }
                            }
                        }
                        if (debugFixData) System.out.println("========= FIXED Beginning TRAINING_SESSION, first value: " + currentTrainingSession + " @ " + recordIx);
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
                if (recordIx > 0) {
                    record.setCadence(secRecords.get(recordIx-1).getCadence());
                } else {
                    record.setCadence((short) 0);
                }
            }
            if (record.getPower() == null) {
                if (recordIx > 0) {
                    record.setPower(secRecords.get(recordIx-1).getPower());
                } else {
                    record.setPower(0);
                }
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
    } */
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void SyncDataInTimeFromSkiErg(String useManualC2SyncSeconds, Boolean hasC2Fit) {
         
        int tempC2SyncSecondsC2File = 0;
        int tempC2SyncSecondsLapDistCalc = 0;
         
        Float activeSumSpeed = 0f;
        Float activeSumCad = 0f;
        Float activeSumPower = 0f;
        Float restSumSpeed = 0f;
        Float restSumCad = 0f;
        Float restSumPower = 0f;

        Float lastActiveFakeSumSpeed = 0f;
        Float lastActiveFakeSumCad = 0f;
        Float lastActiveFakeSumPower = 0f;

        Float maxActiveLapAvgPower = 0f;
        
        System.out.println("----- CALC AUTO SYNC IN TIME FOR SHIFTING VALUES iÍN TIME -----");

        // TEST RUN FOR 10 TIMES TO SEE BEST/HIGHEST VALUE FOR ACTIVE LAPS
        // ================================================================
        for (int i = 0; i < 10; i++) {

            activeFakeSumSpeed = 0f;
            activeFakeSumCad = 0f;
            activeFakeSumPower = 0f;

            maxActiveLapAvgPower = 0f;

            tempC2SyncSecondsC2File = i;
            tempC2SyncSecondsLapDistCalc = i;

            int recordIx = 0;
            int lapIx = 0;
            int lapNo = 1;
            Long currentTimeStamp = 0L;
            Long nextLapStartTime = 0L;
            Float currentLapTime = 0f;
            String currentLapIntensity = "";
            Long currentLapTimeEnd = 0L;
            Float lastLapTotalDistance = 0f;

            int currentLapSumCadence = 0;
            int currentLapSumPower = 0;
            Float currentLapSumStrokeLen = 0f;
            Float currentLapSumDragFactor = 0f;
            Float currentLapMaxStrokeLen = 0f;
            Float currentLapMaxDragFactor = 0f;

            int currentSessionSumCadence = 0;
            int currentSessionSumPower = 0;

            // nextLapStartTime from lapMesg start time field
            nextLapStartTime = getLapMesg().get(0).getFieldLongValue(LAP_STIME);

            // Init session "max" fields (mapped to average constants if dedicated max constants not present)
            getSessionMesg().get(0).setFieldValue(SES_MSPEED, 0f);    // used as max speed placeholder
            getSessionMesg().get(0).setFieldValue(SES_EMSPEED, 0f);   // used as enhanced max speed placeholder
            getSessionMesg().get(0).setFieldValue(SES_MCAD, (short) 0); // used as max cadence placeholder
            getSessionMesg().get(0).setFieldValue(SES_MPOW, 0);      // used as max power placeholder

            for (Mesg record : getRecordMesg()) {

                // --------------
                // IF LAP START
                currentTimeStamp = record.getFieldLongValue(REC_TIME);
                if (currentTimeStamp.equals(nextLapStartTime)) {

                    // Initiate lap max fields
                    getLapMesg().get(lapIx).setFieldValue(LAP_MSPEED, 0f);
                    getLapMesg().get(lapIx).setFieldValue(LAP_EMSPEED, 0f);
                    getLapMesg().get(lapIx).setFieldValue(LAP_MCAD, (short) 0);
                    getLapMesg().get(lapIx).setFieldValue(LAP_MPOW, 0);

                    // Save HR and recordIx START
                    Short hrStart = record.getFieldShortValue(REC_HR);
                    getLapExtraRecords().get(lapIx).setHrStart(hrStart);
                    getLapExtraRecords().get(lapIx).setRecordIxStart(recordIx);

                    // Get LAP DATA to be used to find lap-start-end
                    Float lapTotalTimer = getLapMesg().get(lapIx).getFieldFloatValue(LAP_TIMER);
                    currentLapTime = (lapTotalTimer == null) ? 0f : lapTotalTimer; // in sec

                    Short lapIntensityShort = getLapMesg().get(lapIx).getFieldShortValue(LAP_INTENSITY);
                    Intensity lapIntensity = (lapIntensityShort == null) ? Intensity.INVALID : Intensity.getByValue(lapIntensityShort);
                    currentLapIntensity = Intensity.getStringFromValue(lapIntensity);

                    if (lapNo < getNumberOfLaps()) {
                        currentLapTimeEnd = getLapMesg().get(lapIx + 1).getFieldLongValue(LAP_STIME) - 1;
                        nextLapStartTime = getLapMesg().get(lapIx + 1).getFieldLongValue(LAP_STIME);
                    } else {
                        currentLapTimeEnd = timeLastRecord;
                    }
                    // Save LAP END to table (DateTime)
                    getLapExtraRecords().get(lapIx).setTimeEnd(currentLapTimeEnd);
                }

                // Calc LAP HR min
                Short recHr = record.getFieldShortValue(REC_HR);
                if (recHr == null) {
                    if (recordIx > 0) {
                        Short prevHr = getRecordMesg().get(recordIx - 1).getFieldShortValue(REC_HR);
                        record.setFieldValue(REC_HR, (prevHr == null) ? (short) 60 : prevHr);
                        recHr = record.getFieldShortValue(REC_HR);
                    } else {
                        record.setFieldValue(REC_HR, (short) 60);
                        recHr = record.getFieldShortValue(REC_HR);
                    }
                } else if (recHr < getLapExtraRecords().get(lapIx).getHrMin()) {
                    getLapExtraRecords().get(lapIx).setHrMin(recHr);
                }

                // --------------
                // Calculate LAP MAX - initialize if null
                Float recEnhancedSpeed = record.getFieldFloatValue(REC_ESPEED);
                Float lapEnhancedMaxSpeed = getLapMesg().get(lapIx).getFieldFloatValue(LAP_EMSPEED);
                if (recEnhancedSpeed > lapEnhancedMaxSpeed) {
                    getLapMesg().get(lapIx).setFieldValue(LAP_EMSPEED, recEnhancedSpeed);
                    getLapMesg().get(lapIx).setFieldValue(LAP_MSPEED, recEnhancedSpeed);
                }

                /* Float lapEnhancedMaxSpeed = getLapMesg().get(lapIx).getFieldFloatValue(LAP_EMSPEED);
                if (lapEnhancedMaxSpeed == null) {
                    getLapMesg().get(lapIx).setFieldValue(LAP_EMSPEED, 0f);
                    lapEnhancedMaxSpeed = 0f;
                }
                Float lapMaxSpeed = getLapMesg().get(lapIx).getFieldFloatValue(LAP_MSPEED);
                if (lapMaxSpeed == null) {
                    getLapMesg().get(lapIx).setFieldValue(LAP_MSPEED, 0f);
                    lapMaxSpeed = 0f;
                }

                Float recEnhancedSpeed = record.getFieldFloatValue(REC_ESPEED);
                if (recEnhancedSpeed != null && recEnhancedSpeed > lapEnhancedMaxSpeed) {
                    getLapMesg().get(lapIx).setFieldValue(LAP_EMSPEED, recEnhancedSpeed);
                    getLapMesg().get(lapIx).setFieldValue(LAP_MSPEED, recEnhancedSpeed);
                } */

                // --------------
                // Calc LAPSUM MAX CAD POWER (take values from a shifted sec/record index if available)
                if (recordIx < (getNumberOfRecords() - tempC2SyncSecondsC2File - 1)) {
                    Short shiftedCad = getRecordMesg().get(recordIx + tempC2SyncSecondsC2File).getFieldShortValue(REC_CAD);
                    Integer shiftedPow = getRecordMesg().get(recordIx + tempC2SyncSecondsC2File).getFieldIntegerValue(REC_POW);
                    if (shiftedCad != null) currentLapSumCadence += shiftedCad;
                    if (shiftedPow != null) currentLapSumPower += shiftedPow;
                }

                // ensure lap max cadence is initialized
                Short lapMaxCad = getLapMesg().get(lapIx).getFieldShortValue(LAP_MCAD);
                if (lapMaxCad == null) {
                    getLapMesg().get(lapIx).setFieldValue(LAP_MCAD, (short) 0);
                    lapMaxCad = 0;
                }
                Short recCad = record.getFieldShortValue(REC_CAD);
                if (recCad != null && recCad > lapMaxCad) {
                    getLapMesg().get(lapIx).setFieldValue(LAP_MCAD, recCad);
                }

                // ensure lap max power initialized
                Integer lapMaxPow = getLapMesg().get(lapIx).getFieldIntegerValue(LAP_MPOW);
                if (lapMaxPow == null) {
                    getLapMesg().get(lapIx).setFieldValue(LAP_MPOW, 0);
                    lapMaxPow = 0;
                }
                Integer recPow = record.getFieldIntegerValue(REC_POW);
                if (recPow != null && recPow > lapMaxPow) {
                    getLapMesg().get(lapIx).setFieldValue(LAP_MPOW, recPow);
                }

                // Developer fields for stroke length / drag factor (reading from recordMesg at recordIx)
                for (DeveloperField field : getRecordMesg().get(recordIx).getDeveloperFields()) {
                    if ("StrokeLength".equals(field.getName())) {
                        Float f = field.getFloatValue();
                        if (f != null) {
                            currentLapSumStrokeLen += f;
                            if (f > currentLapMaxStrokeLen) currentLapMaxStrokeLen = f;
                        }
                    }
                    if ("DragFactor".equals(field.getName())) {
                        Float f = field.getFloatValue();
                        if (f != null) {
                            currentLapSumDragFactor += f;
                            if (f > currentLapMaxDragFactor) currentLapMaxDragFactor = f;
                        }
                    }
                }

                // --------------
                // Calculate SESSION SUM & MAX
                Float sessionEnhancedMaxSpeed = getSessionMesg().get(0).getFieldFloatValue(SES_ESPEED);
                if (sessionEnhancedMaxSpeed == null) sessionEnhancedMaxSpeed = 0f;
                if (recEnhancedSpeed != null && recEnhancedSpeed > sessionEnhancedMaxSpeed) {
                    getSessionMesg().get(0).setFieldValue(SES_ESPEED, recEnhancedSpeed);
                    getSessionMesg().get(0).setFieldValue(SES_SPEED, recEnhancedSpeed); // also set "max" speed placeholder
                }

                if (recCad != null) currentSessionSumCadence += recCad;
                Short sessionMaxCadence = getSessionMesg().get(0).getFieldShortValue(SES_CAD);
                if (sessionMaxCadence == null) sessionMaxCadence = 0;
                if (recCad != null && recCad > sessionMaxCadence) {
                    getSessionMesg().get(0).setFieldValue(SES_CAD, recCad); // used as max cadence placeholder
                }

                if (recPow != null) currentSessionSumPower += recPow;
                Integer sessionMaxPow = getSessionMesg().get(0).getFieldIntegerValue(SES_POW);
                if (sessionMaxPow == null) sessionMaxPow = 0;
                if (recPow != null && recPow > sessionMaxPow) {
                    getSessionMesg().get(0).setFieldValue(SES_POW, recPow); // used as max power placeholder
                }

                // LAPTIME for active laps TO CIQ defined in devFieldNamesToRename 
                for (DeveloperField field : record.getDeveloperFields()) {
                    if (devFieldNamesToUpdate.contains(field.getName())) {
                        if ("ACTIVE".equals(currentLapIntensity)) {
                            field.setValue(currentLapTime / 60);
                        } else {
                            field.setValue(0f);
                        }
                    }
                }

                // --------------
                // IF LAP END
                if (currentTimeStamp.equals(currentLapTimeEnd)) {

                    // Save HR and recordIx END
                    getLapExtraRecords().get(lapIx).setHrEnd(record.getFieldShortValue(REC_HR));
                    getLapExtraRecords().get(lapIx).setRecordIxEnd(recordIx);
                    getLapExtraRecords().get(lapIx).setTimeEnd(record.getFieldLongValue(REC_TIME));

                    // Calc LAP DISTANCE & AVG SPEED
                    if (lapNo == getNumberOfLaps() || recordIx > (getNumberOfRecords() - tempC2SyncSecondsLapDistCalc - 1)) {
                        tempC2SyncSecondsLapDistCalc = 0;
                    }
                    Float shiftedDistance = getRecordMesg().get(recordIx + tempC2SyncSecondsLapDistCalc).getFieldFloatValue(REC_DIST);
                    if (shiftedDistance == null) shiftedDistance = 0f;
                    Float lapTotalDistance = shiftedDistance - lastLapTotalDistance;
                    getLapMesg().get(lapIx).setFieldValue(LAP_DIST, lapTotalDistance);
                    lastLapTotalDistance = shiftedDistance;

                    // set avg speed = distance / timer
                    Float lapTimer = getLapMesg().get(lapIx).getFieldFloatValue(LAP_TIMER);
                    Float avgSpeedVal = 0f;
                    if (lapTimer != null && lapTimer != 0f) {
                        avgSpeedVal = lapTotalDistance / lapTimer;
                    }
                    getLapMesg().get(lapIx).setFieldValue(LAP_SPEED, avgSpeedVal);
                    getLapMesg().get(lapIx).setFieldValue(LAP_ESPEED, avgSpeedVal);
                    if (debugLaps) System.out.println("Lap " + lapNo + " Distance: " + lapTotalDistance 
                    + ", Timer: " + getLapMesg().get(lapIx).getFieldFloatValue(LAP_TIMER) 
                    + ", AvgSpeed: " + getLapMesg().get(lapIx).getFieldFloatValue(LAP_SPEED) 
                    + ", avgEnhanced: " + getLapMesg().get(lapIx).getFieldFloatValue(LAP_ESPEED));

                    // Calc LAP SUM & LAP MAX
                    int denom = (recordIx - getLapExtraRecords().get(lapIx).getRecordIxStart() + 1);
                    if (denom <= 0) denom = 1;
                    short avgCad = (short) Math.round((float) currentLapSumCadence / denom);
                    getLapMesg().get(lapIx).setFieldValue(LAP_CAD, avgCad);
                    currentLapSumCadence = 0;

                    int avgPow = Math.round((float) currentLapSumPower / denom);
                    getLapMesg().get(lapIx).setFieldValue(LAP_POW, avgPow);
                    currentLapSumPower = 0;

                    // Developer fields for last record of lap - update lapExtraRecords stroke/drag
                    for (DeveloperField field : getRecordMesg().get(recordIx).getDeveloperFields()) {
                        if ("StrokeLength".equals(field.getName())) {
                            getLapExtraRecords().get(lapIx).setAvgStrokeLen((float) Math.round(100 * currentLapSumStrokeLen / denom) / 100);
                            getLapExtraRecords().get(lapIx).setMaxStrokeLen(currentLapMaxStrokeLen);
                            currentLapSumStrokeLen = 0f;
                            currentLapMaxStrokeLen = 0f;
                        }
                        if ("DragFactor".equals(field.getName())) {
                            getLapExtraRecords().get(lapIx).setAvgDragFactor((float) Math.round(100 * currentLapSumDragFactor / denom) / 100);
                            getLapExtraRecords().get(lapIx).setMaxDragFactor(currentLapMaxDragFactor);
                            currentLapSumDragFactor = 0f;
                            currentLapMaxDragFactor = 0f;
                        }
                    }

                    // --------------
                    // Calculate ACTIVE LAP SUM & MAX
                    if ("ACTIVE".equals(currentLapIntensity)) {
                        Float lapTotalTimerTime = getLapMesg().get(lapIx).getFieldFloatValue(LAP_TIMER);
                        Float lapTotDist = getLapMesg().get(lapIx).getFieldFloatValue(LAP_DIST);
                        Short lapAvgCad = getLapMesg().get(lapIx).getFieldShortValue(LAP_CAD);
                        Integer lapAvgPow = getLapMesg().get(lapIx).getFieldIntegerValue(LAP_POW);
                        Float lapAvgSpeedF = getLapMesg().get(lapIx).getFieldFloatValue(LAP_SPEED);

                        if (lapTotalTimerTime != null) setActiveTime(getActiveTime() + lapTotalTimerTime);
                        if (lapTotDist != null) setActiveDist(getActiveDist() + lapTotDist);

                        if (lapAvgSpeedF != null && lapTotalTimerTime != null) activeSumSpeed += lapAvgSpeedF * lapTotalTimerTime;
                        if (lapAvgCad != null && lapTotalTimerTime != null) activeSumCad += lapAvgCad * lapTotalTimerTime;
                        if (lapAvgPow != null && lapTotalTimerTime != null) activeSumPower += lapAvgPow * lapTotalTimerTime;

                        if (lapAvgSpeedF != null && lapAvgSpeedF > activeFakeSumSpeed) activeFakeSumSpeed = lapAvgSpeedF;
                        if (lapAvgCad != null && lapAvgCad > activeFakeSumCad) activeFakeSumCad = lapAvgCad.floatValue();
                        if (lapAvgPow != null && lapAvgPow > activeFakeSumPower) activeFakeSumPower = lapAvgPow.floatValue();

                        if (useManualC2SyncSeconds.toLowerCase().equals("auto1")) {
                            if (lapAvgSpeedF != null) activeFakeSumSpeed += lapAvgSpeedF;
                            if (lapAvgCad != null) activeFakeSumCad += lapAvgCad;
                            if (lapAvgPow != null) activeFakeSumPower += lapAvgPow;
                        } else {
                            if (lapAvgPow != null && lapAvgPow > maxActiveLapAvgPower) {
                                maxActiveLapAvgPower = lapAvgPow.floatValue();
                            }
                        }
                    }

                    // Calculate REST LAP SUM & MAX
                    if ("REST".equals(currentLapIntensity) || "RECOVERY".equals(currentLapIntensity)) {
                        Float lapTotalTimerTime = getLapMesg().get(lapIx).getFieldFloatValue(LAP_TIMER);
                        Float lapTotDist = getLapMesg().get(lapIx).getFieldFloatValue(LAP_DIST);
                        Short lapAvgCad = getLapMesg().get(lapIx).getFieldShortValue(LAP_CAD);
                        Integer lapAvgPow = getLapMesg().get(lapIx).getFieldIntegerValue(LAP_POW);
                        Float lapAvgSpeedF = getLapMesg().get(lapIx).getFieldFloatValue(LAP_SPEED);

                        if (lapTotalTimerTime != null) setRestTime(getRestTime() + lapTotalTimerTime);
                        if (lapTotDist != null) setRestDist(getRestDist() + lapTotDist);
                        
                        if (lapAvgSpeedF != null && lapTotalTimerTime != null) restSumSpeed += lapAvgSpeedF * lapTotalTimerTime;
                        if (lapAvgCad != null && lapTotalTimerTime != null) restSumCad += lapAvgCad * lapTotalTimerTime;
                        if (lapAvgPow != null && lapTotalTimerTime != null) restSumPower += lapAvgPow * lapTotalTimerTime;
                    }

                    lapIx++;
                    lapNo++;
                } // IF LAP END END

                recordIx++;
            }  // FOR LOOP END

            lapIx--;
            lapNo--;
            recordIx--;

            // First DISTANCE to ZERO (recordMesg)
            getRecordMesg().get(0).setFieldValue(REC_DIST, 0f);

            // TOTAL DISTANCE activity
            Float totalDistanceF = (Float) getRecordMesg().get(recordIx).getFieldFloatValue(REC_DIST);
            if (totalDistanceF == null) totalDistanceF = 0f;
            totalDistance = totalDistanceF;
            getSessionMesg().get(0).setFieldValue(SES_DIST, totalDistance);

            // TOTAL AVG SPEED activity
            avgSpeed = (float) (totalDistance / totalTimerTime);
            getSessionMesg().get(0).setFieldValue(SES_SPEED, avgSpeed);
            getSessionMesg().get(0).setFieldValue(SES_ESPEED, avgSpeed);

            // TOTAL AVG CADENCE activity
            avgCadence = Math.round((float) currentSessionSumCadence / (recordIx));
            getSessionMesg().get(0).setFieldValue(SES_CAD, (short) avgCadence);

            // TOTAL AVG POWER activity
            avgPower = Math.round((float) currentSessionSumPower / (recordIx));
            getSessionMesg().get(0).setFieldValue(SES_POW, avgPower);

            // Calculate ACTIVE LAP SUM & MAX
            setActiveAvgSpeed((float) (activeSumSpeed / getActiveTime()));
            setActiveAvgCad((float) (activeSumCad / getActiveTime()));
            setActiveAvgPower((float) (activeSumPower / getActiveTime()));

            // Calculate REST LAP SUM & MAX
            setRestAvgSpeed((float) (restSumSpeed / getRestTime()));
            setRestAvgCad((float) (restSumCad / getRestTime()));
            setRestAvgPower((float) (restSumPower / getRestTime()));

            if (activeFakeSumSpeed > lastActiveFakeSumSpeed) {
                c2SyncSecondsLapDistCalc = i;
                lastActiveFakeSumSpeed = activeFakeSumSpeed;
            }
            if (activeFakeSumPower > lastActiveFakeSumPower) {
                c2SyncSecondsC2File = i;
                lastActiveFakeSumPower = activeFakeSumPower;
            }

            if (debugLaps) System.out.println("_____ i: " + i + " MAXsp: " + activeFakeSumSpeed + " cad: " + activeFakeSumCad + " pow: " + activeFakeSumPower);

        }

        if (hasC2Fit) {
            System.out.println("______HAS C2fit-> before phase shifting - syncSeconds C2 (pow, cad): "+c2SyncSecondsC2File+" lapdist (speed): "+c2SyncSecondsLapDistCalc);
        } else {
            System.out.println("______NO C2fit before phase shifting - lapdist (power, speed): "+c2SyncSecondsLapDistCalc);
        }
        System.out.println("------------------------------------------------------------------");

        //=====================================================================
        //=====================================================================
        // DOING THE ACTUAL SHIFTING
        //=====================================================================
        int recordIx = 0;

        for (Mesg record : getRecordMesg()) {

            if (hasC2Fit) {
                if (recordIx < (getNumberOfRecords()-1-c2SyncSecondsLapDistCalc)) {
                    Mesg recordToUpdate = getRecordMesg().get(recordIx + c2SyncSecondsLapDistCalc);
                    record.setFieldValue(REC_DIST, recordToUpdate.getFieldFloatValue(REC_DIST));
                    record.setFieldValue(REC_SPEED, recordToUpdate.getFieldFloatValue(REC_SPEED));
                    record.setFieldValue(REC_ESPEED, recordToUpdate.getFieldFloatValue(REC_ESPEED));
                    record.setFieldValue(REC_POW, recordToUpdate.getFieldIntegerValue(REC_POW));
                } else {
                    Mesg recordLast = getRecordMesg().get(getNumberOfRecords()-1);
                    record.setFieldValue(REC_DIST, recordLast.getFieldFloatValue(REC_DIST));
                    record.setFieldValue(REC_SPEED, recordLast.getFieldFloatValue(REC_SPEED));
                    record.setFieldValue(REC_ESPEED, recordLast.getFieldFloatValue(REC_ESPEED));
                    record.setFieldValue(REC_POW, recordLast.getFieldIntegerValue(REC_POW));
                }

                if (recordIx < (getNumberOfRecords()-1-c2SyncSecondsC2File)) {
                    Mesg recordToUpdate = getRecordMesg().get(recordIx + c2SyncSecondsC2File);
                    record.setFieldValue(REC_CAD, recordToUpdate.getFieldIntegerValue(REC_CAD));
                    //record.setFieldValue(REC_POW, recordToUpdate.getFieldIntegerValue(REC_POW));
                } else {
                    Mesg recordLast = getRecordMesg().get(getNumberOfRecords()-1);
                    record.setFieldValue(REC_CAD, recordLast.getFieldIntegerValue(REC_CAD));
                    //record.setFieldValue(REC_POW, recordLast.getFieldIntegerValue(REC_POW));
                }
            } else {
                // NO C2fit
                if (recordIx<(getNumberOfRecords()-1-c2SyncSecondsLapDistCalc)) {
                    Mesg recordToUpdate = getRecordMesg().get(recordIx + c2SyncSecondsLapDistCalc);
                    record.setFieldValue(REC_DIST, recordToUpdate.getFieldFloatValue(REC_DIST));
                    record.setFieldValue(REC_SPEED, recordToUpdate.getFieldFloatValue(REC_SPEED));
                    record.setFieldValue(REC_ESPEED, recordToUpdate.getFieldFloatValue(REC_ESPEED));
                    record.setFieldValue(REC_POW, recordToUpdate.getFieldIntegerValue(REC_POW));
                    //record.setFieldValue(REC_POW, recordMesg.get(recordIx+c2SyncSecondsC2File).getFieldIntegerValue(REC_POW));
                } else {
                    Mesg recordLast = getRecordMesg().get(getNumberOfRecords()-1);
                    record.setFieldValue(REC_DIST, recordLast.getFieldFloatValue(REC_DIST));
                    record.setFieldValue(REC_SPEED, recordLast.getFieldFloatValue(REC_SPEED));
                    record.setFieldValue(REC_ESPEED, recordLast.getFieldFloatValue(REC_ESPEED));
                    record.setFieldValue(REC_POW, recordLast.getFieldIntegerValue(REC_POW));
                }
            }

            recordIx++;
        }

        // WHEN IN AUTO MODE, NO SHIFTING SHOULD BE DONE IN CALC LAP DATA
        c2SyncSecondsLapDistCalc = 0;
        c2SyncSecondsC2File = 0;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void calcLapDataFromSecRecordsSkiErg() {
        int recordIx = 0;
        int lapIx = 0;
        int lapNo = 1;
        Long currentTimeStamp = 0l;
        Long nextLapStartTime = 0l;
        Float currentLapTime = 0f;
        String currentLapIntensity = "";
        Long currentLapTimeEnd = 0l;
        Float lastLapTotalDist = 0f;

        int currentLapSumCadence = 0;
        int currentLapSumPower = 0;
        Float currentLapSumStrokeLen = 0f;
        Float currentLapSumDragFactor = 0f;
        Float currentLapMaxStrokeLen = 0f;
        Float currentLapMaxDragFactor = 0f;

        int currentSessionSumCadence = 0;
        int currentSessionSumPower = 0;
        
        Float activeSumSpeed = 0f;
        Float activeSumCad = 0f;
        Float activeSumPower = 0f;
        Float restSumSpeed = 0f;
        Float restSumCad = 0f;
        Float restSumPower = 0f;

        Float activeFakeSumSpeed = 0f;
        Float activeFakeSumCad = 0f;
        Float activeFakeSumPower = 0f;
        
        setActiveDist(0f);
        setRestDist(0f);

        // Get first lap start time (safe)
        nextLapStartTime = getLapMesg().get(0).getFieldLongValue(LAP_STIME);

        // Initialize session max metrics to zero safely
        Mesg session = getSessionMesg().get(0);
        session.setFieldValue(SES_MSPEED, 0f);
        session.setFieldValue(SES_EMSPEED, 0f);
        session.setFieldValue(SES_MCAD, (short) 0);
        session.setFieldValue(SES_MPOW, 0);
        
        System.out.println("______ before calcLapData - syncSeconds C2 (paw, cad): "+c2SyncSecondsC2File+" dist (speed): "+c2SyncSecondsLapDistCalc);

        System.out.println("----- CALC LAP DATA FROM SEC RECORDS - TOTAL RECS: " + getNumberOfRecords() + " LAPS: " + getNumberOfLaps());
        for (Mesg record : getRecordMesg()) {

            //--------------
            // IF LAP START
            Long ts = record.getFieldLongValue(REC_TIME);
            currentTimeStamp = ts != null ? ts : 0L;

            if ( currentTimeStamp.equals(nextLapStartTime) ) {

                // Initiate maxSpeed with 0 to be able to compare later
                getLapMesg().get(lapIx).setFieldValue(LAP_SPEED, 0f);
                getLapMesg().get(lapIx).setFieldValue(LAP_ESPEED, 0f);
                getLapMesg().get(lapIx).setFieldValue(LapMesg.MaxCadenceFieldNum, (short)0);
                getLapMesg().get(lapIx).setFieldValue(LapMesg.MaxPowerFieldNum, 0);
                
                // Save HR and recordIx START
                Short hrStart = record.getFieldShortValue(REC_HR);
                getLapExtraRecords().get(lapIx).setHrStart(hrStart != null ? hrStart : 0);
                getLapExtraRecords().get(lapIx).setRecordIxStart(recordIx);

                // Get LAP DATA to be used to find lap-start-end
                Float lapTimerF = getLapMesg().get(lapIx).getFieldFloatValue(LAP_TIMER);
                currentLapTime = lapTimerF != null ? lapTimerF : 0f; // in sec

                Short intensityVal = getLapMesg().get(lapIx).getFieldShortValue(LapMesg.IntensityFieldNum);
                currentLapIntensity = Intensity.getStringFromValue(Intensity.getByValue(intensityVal != null ? intensityVal : 0));

                if (lapNo < getNumberOfLaps()) {
                    Long nextStart = getLapMesg().get(lapIx+1).getFieldLongValue(LAP_STIME);
                    currentLapTimeEnd = nextStart != null ? nextStart - 1 : timeLastRecord;
                    nextLapStartTime = nextStart != null ? nextStart : nextLapStartTime;
                } else {
                    currentLapTimeEnd = timeLastRecord;
                }

                // Save LAP END to table
                getLapExtraRecords().get(lapIx).setTimeEnd(currentLapTimeEnd);
            }

            // Calc LAP HR min
            Short recHr = record.getFieldShortValue(REC_HR);
            if (recHr == null) {
                if (recordIx > 0) {
                    Short prevHr = getRecordMesg().get(recordIx-1).getFieldShortValue(REC_HR);
                    record.setFieldValue(REC_HR, prevHr != null ? prevHr : (short)60);
                    recHr = record.getFieldShortValue(REC_HR);
                } else {
                    record.setFieldValue(REC_HR, (short)60);
                    recHr = 60;
                }
            }
            if ( recHr < getLapExtraRecords().get(lapIx).getHrMin() ) {
                getLapExtraRecords().get(lapIx).setHrMin(recHr);
            }

            //--------------
            // Calculate LAP MAX
            Float recEnhSp = record.getFieldFloatValue(REC_ESPEED);
            Float lapEnhMax = getLapMesg().get(lapIx).getFieldFloatValue(LAP_EMSPEED);
            if (lapEnhMax == null) lapEnhMax = 0f;
            if (recEnhSp != null && recEnhSp > lapEnhMax) {
                getLapMesg().get(lapIx).setFieldValue(LAP_EMSPEED, recEnhSp);
                getLapMesg().get(lapIx).setFieldValue(LAP_MSPEED, recEnhSp);
            }
            
            //--------------
            // Calc LAPSUM CAD POWER
            // SHIFTED
            int idxForC2Cad = recordIx + c2SyncSecondsC2File;
            if (idxForC2Cad < getRecordMesg().size()) {
                Short cadShift = getRecordMesg().get(idxForC2Cad).getFieldShortValue(REC_CAD);
                Integer powShift = getRecordMesg().get(idxForC2Cad).getFieldIntegerValue(REC_POW);
                currentLapSumCadence += cadShift != null ? cadShift : 0;
                currentLapSumPower += powShift != null ? powShift : 0;
            }
            // Calc MAX CAD POWER
            // NON-SHIFTED
            Short recCad = record.getFieldShortValue(REC_CAD);
            if (recCad != null) {
                Short lapMaxCad = getLapMesg().get(lapIx).getFieldShortValue(LAP_MCAD);
                if (lapMaxCad == null) lapMaxCad = 0;
                if (recCad > lapMaxCad) {
                    getLapMesg().get(lapIx).setFieldValue(LAP_MCAD, recCad);
                }
            }
            Integer recPow = record.getFieldIntegerValue(REC_POW);
            if (recPow != null) {
                Integer lapMaxPow = getLapMesg().get(lapIx).getFieldIntegerValue(LAP_MPOW);
                if (lapMaxPow == null) lapMaxPow = 0;
                if (recPow > lapMaxPow) {
                    getLapMesg().get(lapIx).setFieldValue(LAP_MPOW, recPow);
                }
            }

            // Calc LAPSUM & MLAPMAX for Developer fields from current record
            // NON-SHIFTED
            Mesg secRecForDev = getRecordMesg().get(recordIx); // Why not just use 'record'???
            for (DeveloperField field : secRecForDev.getDeveloperFields()) {
                if ("StrokeLength".equals(field.getName())) {
                    currentLapSumStrokeLen += field.getFloatValue();
                    if (field.getFloatValue() > currentLapMaxStrokeLen) {
                        currentLapMaxStrokeLen = field.getFloatValue();
                    }
                }
                if ("DragFactor".equals(field.getName())) {
                    currentLapSumDragFactor += field.getFloatValue();
                    if (field.getFloatValue() > currentLapMaxDragFactor) {
                        currentLapMaxDragFactor = field.getFloatValue();
                    }
                }
            }

            //--------------
            // Calculate SESSION SUM & MAX
            Float sessEnhMax = getSessionMesg().get(0).getFieldFloatValue(SES_EMSPEED);
            if (sessEnhMax == null) sessEnhMax = 0f;
            if (recEnhSp != null && recEnhSp > sessEnhMax) {
                getSessionMesg().get(0).setFieldValue(SES_EMSPEED, recEnhSp);
                getSessionMesg().get(0).setFieldValue(SES_MSPEED, recEnhSp);
            }
            currentSessionSumCadence += recCad != null ? recCad : 0;
            if (recCad != null && recCad > getSessionMesg().get(0).getFieldShortValue(SES_MCAD)) {
                getSessionMesg().get(0).setFieldValue(SES_MCAD, recCad);
            }
            currentSessionSumPower += recPow != null ? recPow : 0;
            if (recPow != null && recPow > getSessionMesg().get(0).getFieldIntegerValue(SES_MPOW)) {
                getSessionMesg().get(0).setFieldValue(SES_MPOW, recPow);
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
            if ( currentTimeStamp.equals(currentLapTimeEnd) ) {
                //System.out.println("LapEND " + lapIx + "  " + lapExtraRecords.get(lapIx).recordIxStart + "  " + recordIx);

                // Save HR and recordIx END
                getLapExtraRecords().get(lapIx).setHrEnd(record.getFieldShortValue(REC_HR) != null ? record.getFieldShortValue(REC_HR) : 0);
                getLapExtraRecords().get(lapIx).setRecordIxEnd(recordIx);
                getLapExtraRecords().get(lapIx).setTimeEnd(currentTimeStamp);

                // Calc LAP DISTANCE & AVG SPEED
                // SHIFTED
                int idxForLapDist = recordIx + c2SyncSecondsLapDistCalc;
                Float lapTotalDist = idxForLapDist < getRecordMesg().size() ? 
                    getRecordMesg().get(idxForLapDist).getFieldFloatValue(REC_DIST) : 
                    getRecordMesg().get(getRecordMesg().size()-1).getFieldFloatValue(REC_DIST);
                //Float lastLapTotalDistanceF = lastLapTotalDistance;
                Float lapDist = (lapTotalDist != null ? lapTotalDist : 0f) - lastLapTotalDist;
                getLapMesg().get(lapIx).setFieldValue(LAP_DIST, lapDist);
                lastLapTotalDist = lapTotalDist != null ? lapTotalDist : lastLapTotalDist;

                Float lapTimer = getLapMesg().get(lapIx).getFieldFloatValue(LAP_TIMER);
                //getLapMesg().get(lapIx).setFieldValue(LapMesg.TotalMovingTimeFieldNum, lapTimer);
                Float avgSp = lapTimer != null && lapTimer != 0 ? (float) (lapDist / lapTimer) : 0f;
                getLapMesg().get(lapIx).setFieldValue(LAP_SPEED, avgSp);
                getLapMesg().get(lapIx).setFieldValue(LAP_ESPEED, avgSp);
                
                // Calc LAP SUM & LAP MAX
                int lapRecCount = recordIx - getLapExtraRecords().get(lapIx).getRecordIxStart() + 1;
                short avgCad = lapRecCount > 0 ? (short) Math.round((float) currentLapSumCadence / lapRecCount) : 0;
                getLapMesg().get(lapIx).setFieldValue(LAP_CAD, avgCad);
                currentLapSumCadence = 0;

                int avgPow = lapRecCount > 0 ? Math.round((float) currentLapSumPower / lapRecCount) : 0;
                getLapMesg().get(lapIx).setFieldValue(LAP_POW, avgPow);
                currentLapSumPower = 0;

                for (DeveloperField field : getRecordMesg().get(recordIx).getDeveloperFields()) {
                    if ("StrokeLength".equals(field.getName())) {
                        getLapExtraRecords().get(lapIx).setAvgStrokeLen((float) Math.round(100 * currentLapSumStrokeLen / (recordIx-getLapExtraRecords().get(lapIx).getRecordIxStart()+1)) /100);
                        getLapExtraRecords().get(lapIx).setMaxStrokeLen(currentLapMaxStrokeLen);
                        currentLapSumStrokeLen = 0f;
                        currentLapMaxStrokeLen = 0f;
                    }
                    if ("DragFactor".equals(field.getName())) {
                        getLapExtraRecords().get(lapIx).setAvgDragFactor((float) Math.round(100 * currentLapSumDragFactor / (recordIx-getLapExtraRecords().get(lapIx).getRecordIxStart()+1)) /100);
                        getLapExtraRecords().get(lapIx).setMaxDragFactor(currentLapMaxDragFactor);
                        currentLapSumDragFactor = 0f;
                        currentLapMaxDragFactor = 0f;
                    }
                }

                //--------------
                // Calculate ACTIVE LAP SUM & MAX
                if (currentLapIntensity.equals("ACTIVE")) {
                    setActiveTime(getActiveTime() + (lapTimer != null ? lapTimer : 0f));
                    setActiveDist(getActiveDist() + lapDist);
                    activeSumSpeed = activeSumSpeed + (getLapMesg().get(lapIx).getFieldFloatValue(LAP_SPEED) != null ? getLapMesg().get(lapIx).getFieldFloatValue(LAP_SPEED) * (lapTimer != null ? lapTimer : 0f) : 0f);
                    activeSumCad = activeSumCad + (float) ((getLapMesg().get(lapIx).getFieldShortValue(LAP_CAD) != null ? getLapMesg().get(lapIx).getFieldShortValue(LAP_CAD) : 0) * (lapTimer != null ? lapTimer : 0f));
                    activeSumPower = activeSumPower + (getLapMesg().get(lapIx).getFieldIntegerValue(LAP_POW) != null ? getLapMesg().get(lapIx).getFieldIntegerValue(LAP_POW) * (lapTimer != null ? lapTimer : 0f) : 0f);

                    activeFakeSumSpeed = activeFakeSumSpeed + (getLapMesg().get(lapIx).getFieldFloatValue(LAP_SPEED) != null ? getLapMesg().get(lapIx).getFieldFloatValue(LAP_SPEED) : 0f);
                    activeFakeSumCad = activeFakeSumCad + (getLapMesg().get(lapIx).getFieldShortValue(LAP_CAD) != null ? getLapMesg().get(lapIx).getFieldShortValue(LAP_CAD) : 0);
                    activeFakeSumPower = activeFakeSumPower + (getLapMesg().get(lapIx).getFieldIntegerValue(LAP_POW) != null ? getLapMesg().get(lapIx).getFieldIntegerValue(LAP_POW) : 0);
                }
                // Calculate REST LAP SUM & MAX
                if (currentLapIntensity.equals("REST") || currentLapIntensity.equals("RECOVERY")) {
                    setRestTime(getRestTime() + (lapTimer != null ? lapTimer : 0f));
                    setRestDist(getRestDist() + (getLapMesg().get(lapIx).getFieldFloatValue(LAP_DIST) != null ? getLapMesg().get(lapIx).getFieldFloatValue(LAP_DIST) : 0f));
                    restSumSpeed = restSumSpeed + (getLapMesg().get(lapIx).getFieldFloatValue(LAP_SPEED) != null ? getLapMesg().get(lapIx).getFieldFloatValue(LAP_SPEED) * (lapTimer != null ? lapTimer : 0f) : 0f);
                    restSumCad = restSumCad + (getLapMesg().get(lapIx).getFieldShortValue(LAP_CAD) != null ? getLapMesg().get(lapIx).getFieldShortValue(LAP_CAD) * (lapTimer != null ? lapTimer : 0f) : 0f);
                    restSumPower = restSumPower + (getLapMesg().get(lapIx).getFieldIntegerValue(LAP_POW) != null ? getLapMesg().get(lapIx).getFieldIntegerValue(LAP_POW) * (lapTimer != null ? lapTimer : 0f) : 0f);
                }

                lapIx++;
                lapNo++;
            } // IF LAP END END

            recordIx++;
        }  // FOR LOOP END

        lapIx--;
        lapNo--;
        recordIx--;
        
        // First DISTANCE value to ZERO
        getRecordMesg().get(0).setFieldValue(REC_DIST, 0f);

        // TOTAL DISTANCE activity
        Float distF = getRecordMesg().get(recordIx).getFieldFloatValue(REC_DIST);
        totalDistance = distF != null ? distF : 0f;
        getSessionMesg().get(0).setFieldValue(SES_DIST, totalDistance);

        // TOTAL AVG SPEED activity
        avgSpeed = (float) (totalDistance / totalTimerTime);
        getSessionMesg().get(0).setFieldValue(SES_SPEED, avgSpeed);
        getSessionMesg().get(0).setFieldValue(SES_ESPEED, avgSpeed);

        // TOTAL AVG CADENCE activity
        avgCadence = Math.round((float) currentSessionSumCadence / (recordIx));
        getSessionMesg().get(0).setFieldValue(SES_CAD, (short) avgCadence);

        // TOTAL AVG POWER activity
        avgPower = Math.round((float) currentSessionSumPower / (recordIx));
        getSessionMesg().get(0).setFieldValue(SES_POW, avgPower);

        // Calculate ACTIVE LAP SUM & MAX
        setActiveAvgSpeed(getActiveTime() > 0 ? activeSumSpeed / getActiveTime() : 0f);
        setActiveAvgCad(getActiveTime() > 0 ? activeSumCad / getActiveTime() : 0f);
        setActiveAvgPower(getActiveTime() > 0 ? activeSumPower / getActiveTime() : 0f);

        // Calculate REST LAP SUM & MAX
        setRestAvgSpeed(getRestTime() > 0 ? restSumSpeed / getRestTime() : 0f);
        setRestAvgCad(getRestTime() > 0 ? restSumCad / getRestTime() : 0f);
        setRestAvgPower(getRestTime() > 0 ? restSumPower / getRestTime() : 0f);

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void calcSplitRecordsBasedOnLaps() {

        int lapIx = 0;

        System.out.println("----- CALC SPLIT DATA FROM LAPS - TOTAL SPLITS: " + getSplitMesg().size() + " LAPS: " + getLapMesg().size());

        if (getLapMesg().isEmpty()) {
            if (debugSplit) System.out.println("----- No laps found, skipping split calc");
            return;
        }
        if (getSplitMesg().isEmpty()) {
            if (debugSplit) System.out.println("----- No splits found, skipping split calc");
            return;
        }

        for (Mesg lap : getLapMesg()) {
            Integer lapIxInSplitMesg = null;
            for (int i = 0; i < getSplitMesg().size(); i++) {
                Integer candidateLapIxInSplitMesg = getSplitMesg().get(i).getFieldIntegerValue(67);
                if (candidateLapIxInSplitMesg != null && candidateLapIxInSplitMesg.equals(lapIx)) {
                    lapIxInSplitMesg = i;
                    if (debugSplit) System.out.println("----- Link SPLIT record " + i + " to LAP index " + lapIx);
                    break;
                }
            }

            if (lapIxInSplitMesg == null) {
                if (debugSplit) System.out.println("----- No SPLIT found for LAP index " + lapIx + ", skipping");
                lapIx++;
                continue;
            }

            if (lapIx >= getLapExtraRecords().size()) {
                if (debugSplit) System.out.println("----- lapExtraRecords missing for LAP index " + lapIx + ", skipping");
                lapIx++;
                continue;
            }

            Float lapDist = lap.getFieldFloatValue(LAP_DIST);
            Float lapSpeed = lap.getFieldFloatValue(LAP_ESPEED);
            Float lapMaxSpeed = lap.getFieldFloatValue(LAP_EMSPEED);
            Float distAtLapStart = 0f;
            Integer lapStartRecordIx = getLapExtraRecords().get(lapIx).getRecordIxStart();
            if (lapStartRecordIx != null && lapStartRecordIx >= 0 && lapStartRecordIx < getRecordMesg().size()) {
                Float dist = getRecordMesg().get(lapStartRecordIx).getFieldFloatValue(REC_DIST);
                distAtLapStart = dist != null ? dist : 0f;
            }
            Integer lapPow = lap.getFieldIntegerValue(LAP_POW);
            Integer lapMaxPow = lap.getFieldIntegerValue(LAP_MPOW);

            getSplitMesg().get(lapIxInSplitMesg).setFieldValue(SPL_DIST, lapDist!= null ? lapDist : 0f);
            getSplitMesg().get(lapIxInSplitMesg).setFieldValue(SPL_SPEED, lapSpeed != null ? lapSpeed : 0f);
            getSplitMesg().get(lapIxInSplitMesg).setFieldValue(SPL_MSPEED, lapMaxSpeed != null ? lapMaxSpeed : 0f);
            getSplitMesg().get(lapIxInSplitMesg).setFieldValue(7, distAtLapStart != null ? distAtLapStart : 0f);
            
            /* getSplitMesg().get(splitIx).setFieldValue(40, lapPow != null ? lapPow : 0);
            getSplitMesg().get(splitIx).setFieldValue(41, lapMaxPow != null ? lapMaxPow : 0); */

            lapIx++;
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void calcSplitSummaryBasedOnSplits() {
        // NOT IMPLEMENTED YET
        for (Mesg splitSumMesg : getSplitSummaryMesg()) {
            Short splitType = splitSumMesg.getFieldShortValue(SPLSUM_TYPE);
            for (Mesg splitMesg : getSplitMesg()) {
                // IF SPLIT TYPE MATCH
                if (splitType != null && splitType.equals(splitMesg.getFieldShortValue(SPL_TYPE))) {
                    // ADD TO SUMMARY
                    splitSumMesg.setFieldValue(SPLSUM_DIST, 
                        splitSumMesg.getFieldFloatValue(SPLSUM_DIST) + 
                        splitMesg.getFieldFloatValue(SPL_DIST));
                }
            }
            splitSumMesg.setFieldValue(SPLSUM_SPEED, 
                splitSumMesg.getFieldFloatValue(SPLSUM_DIST) / 
                splitSumMesg.getFieldFloatValue(SPLSUM_TIMER));
        }
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
    public void fixEmptyBeginningElliptical() {
        int recordIx = 0;

        // =========== Fix EMPTY beginning of data =============
        // 
        // ========================================================
        int maxIxFixEmptyBeginning = 100;
        int maxCadenceValue = 74;
        boolean lookingInBeginningForEmptySpeed = true;
        boolean lookingInBeginningForEmptyCadence = true;
        boolean lookingInBeginningForEmptyPower = true;
        boolean lookingInBeginningForEmptyStrokeLength = true;
        boolean lookingInBeginningForEmptyDragFactor = true;
        boolean lookingInBeginningForEmptyTrainingSession = true;
        
        if (getNumberOfRecords() < maxIxFixEmptyBeginning) {
            maxIxFixEmptyBeginning = getNumberOfRecords() - 1;
        }

        for (Mesg record : getRecordMesg()) {
            if (recordIx <= maxIxFixEmptyBeginning) {
                // FIX CADENCE
                if (lookingInBeginningForEmptyCadence) {
                    Short cad = record.getFieldShortValue(REC_CAD);
                    if ((cad!=null && cad!=0 && cad>20)) {
                        if (debugFixData) System.out.println("========= FIXED Beginning CADENCE, first real value: " + cad + " @ " + recordIx);
                        for (int i = recordIx-1; i >= 0; i--) {
                            if (debugFixData) System.out.println("========= FIXING CADENCE, value: " + getRecordMesg().get(i).getFieldValue(REC_CAD) + "->" + cad + " @" + i);
                            getRecordMesg().get(i).setFieldValue(REC_CAD, cad);
                        }
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

        // =========== Fix EMPTY beginning of data =============
        // 
        // ========================================================
        int maxIxFixEmptyBeginning = 100;
        int maxCadenceValue = 74;
        boolean lookingInBeginningForEmptySpeed = true;
        boolean lookingInBeginningForEmptyCadence = true;
        boolean lookingInBeginningForEmptyPower = true;
        boolean lookingInBeginningForEmptyStrokeLength = true;
        boolean lookingInBeginningForEmptyDragFactor = true;
        boolean lookingInBeginningForEmptyTrainingSession = true;
        
        if (getNumberOfRecords() < maxIxFixEmptyBeginning) {
            maxIxFixEmptyBeginning = getNumberOfRecords() - 1;
        }

        for (Mesg record : getRecordMesg()) {
            if (recordIx <= maxIxFixEmptyBeginning) {
                // FIX SPEED
                if (lookingInBeginningForEmptySpeed) {
                    Float speed = record.getFieldFloatValue(REC_ESPEED);
                    if (speed!=null && speed!=0f && speed!=1 && speed<100f) {
                        if (debugFixData) System.out.println("========= FIXED Beginning SPEED, first value: " + speed + " @ " + recordIx);
                        for (int i = recordIx-1; i >= 0; i--) {
                            if (debugFixData) System.out.println("========= FIXING SPEED, value: " + getRecordMesg().get(i).getFieldValue(REC_ESPEED) + "->" + speed + " @" + i);
                            getRecordMesg().get(i).setFieldValue(REC_ESPEED, speed);
                            getRecordMesg().get(i).setFieldValue(REC_SPEED, speed);
                        }
                        lookingInBeginningForEmptySpeed = false;
                    }
                }
                // FIX CADENCE
                if (lookingInBeginningForEmptyCadence) {
                    Short cad = record.getFieldShortValue(REC_CAD);
                    if (cad!=null && cad!=0 && cad!=1 && cad<100) {
                        if (debugFixData) System.out.println("========= FIXED Beginning CADENCE, first value: " + cad + " @ " + recordIx);
                        for (int i = recordIx-1; i >= 0; i--) {
                            if (debugFixData) System.out.println("========= FIXING CADENCE, value: " + getRecordMesg().get(i).getFieldValue(REC_CAD) + "->" + cad + " @" + i);
                            getRecordMesg().get(i).setFieldValue(REC_CAD, cad);
                        }
                        lookingInBeginningForEmptyCadence = false;
                    }
                }
                // FIX POWER
                if (lookingInBeginningForEmptyPower) {
                    Integer power = record.getFieldIntegerValue(REC_POW);
                    if ((power!=null && power!=0)) {
                        if (debugFixData) System.out.println("========= FIXED Beginning POWER, first value: " + power + " @ " + recordIx);
                        for (int i = recordIx-1; i >= 0; i--) {
                            if (debugFixData) System.out.println("========= FIXING POWER, value: " + getRecordMesg().get(i).getFieldValue(REC_POW) + "->" + power + " @" + i);
                            getRecordMesg().get(i).setFieldValue(REC_POW, power);
                        }
                        lookingInBeginningForEmptyPower = false;
                    }
                }
            
            }
            recordIx++;
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void calcLapSumFromRecordMesgElliptical() {
        // Calculates LapSum's to be used in CalcLapDataFromRecordMesgElliptical
        int recordIx = 0;
        int lapIx = 0;
        int lapNo = 1;
        Long C2DateTime = null; // NOT NEEDED for Elliptical, need to be initilized
        Float recordDist = 0f;
        Float lapSumOfRecordDist = 0f;
        Float sumOfRecordDist = 0f;
        Float corrPerMeter = 0f;
        Long currentTimeStamp = 0l;
        Long nextLapStartTime = 0l;
        Float currentLapTime = 0f;
        String currentLapIntensity = "";
        Long currentLapTimeEnd = 0l;

        Float speedLapSum = 0f;
        Float cadLapSum = 0f;

        nextLapStartTime = getLapMesg().get(0).getFieldLongValue(LAP_STIME);

        for (Mesg record : getRecordMesg()) {

            //--------------
            // Initiate secExtraRecords
            getSecExtraRecords().add(new RecordExtraMesg(lapNo, C2DateTime));

            //--------------
            // IF LAP START
            currentTimeStamp = record.getFieldLongValue(REC_TIME);
            if ( currentTimeStamp.equals(nextLapStartTime) ) {
                // Get LAP DATA to be used to find lap-start-end
                Float lapTotalTimer = getLapMesg().get(lapIx).getFieldFloatValue(LAP_TIMER);
                currentLapTime = (lapTotalTimer == null) ? 0f : lapTotalTimer; // in sec

                if (lapNo < getNumberOfLaps()) {
                    currentLapTimeEnd = getLapMesg().get(lapIx + 1).getFieldLongValue(LAP_STIME) - 1;
                    nextLapStartTime = getLapMesg().get(lapIx + 1).getFieldLongValue(LAP_STIME);
                } else {
                    currentLapTimeEnd = timeLastRecord;
                }
                // Save LAP END to table
                getLapExtraRecords().get(lapIx).setTimeEnd(currentLapTimeEnd);
            }

            //--------------
            // FIX EMPTY CADENCE
            Short cadFixEmpty = record.getFieldShortValue(REC_CAD);
            if (cadFixEmpty == null) {
                if (recordIx > 0) {
                    Short cadPrevFixEmpty = getRecordMesg().get(recordIx-1).getFieldShortValue(REC_CAD);
                    record.setFieldValue(REC_CAD, cadPrevFixEmpty != null ? cadPrevFixEmpty : (short) 60);
                } else {
                    record.setFieldValue(REC_CAD, (short) 60);
                }
            }

            //--------------
            // Calc LAP SUM SPEED or CADENCE
            LapExtraMesg lapExtra = getLapExtraRecords().get(lapIx);
            if (getMySport() == FitFile.MySport.TREADMILL) {
                // TREADMILL
                Float speed = record.getFieldFloatValue(REC_ESPEED);
                speedLapSum = lapExtra.getSpeedLapSum();
                speedLapSum = speedLapSum + ((speed == null) ? 0f : speed);
                lapExtra.setSpeedLapSum(speedLapSum);
                //--System.out.println("RecordIx: " + recordIx + " Speed: " + speed + " SpeedLapSum: " + speedLapSum + " lapNo: " + lapNo);
            } else {
                // ELLIPTICAL   
                Short cadCalcSum = record.getFieldShortValue(REC_CAD);
                cadLapSum = (float) lapExtra.getCadLapSum();
                cadLapSum = cadLapSum + ((cadCalcSum == null) ? 0 : cadCalcSum);
                lapExtra.setCadLapSum(cadLapSum);
            }

            //--------------
            // IF LAP END
            if ( currentTimeStamp.equals(currentLapTimeEnd) ) {

                // Save recordIx END
                getLapExtraRecords().get(lapIx).setRecordIxEnd(recordIx);
                getLapExtraRecords().get(lapIx).setTimeEnd(record.getFieldLongValue(REC_TIME));

                // INIT of Variables
                /* sumOfRecordDist = sumOfRecordDist - lapSumOfRecordDist;
                lapSumOfRecordDist = 0f; */
                lapIx++;
                lapNo++;
            } // IF LAP END END
            
            recordIx++;
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void calcLapDataFromRecordMesgElliptical() {
        int recordIx = 0;
        int lapIx = 0;
        int lapNo = 1;
        Long C2DateTime = null; // NOT NEEDED for Elliptical, need to be initilized
        Float recordDist = 0f;
        Float lapSumOfRecordDist = 0f;
        Float sumOfRecordDist = 0f;
        Float corrPerMeter = 0f;
        Long currentTimeStamp = 0l;
        Long nextLapStartTime = 0l;
        Float currentLapTime = 0f;
        String currentLapIntensity = "";
        Long currentLapTimeEnd = 0l;

        Float activeSumSpeed = 0f;
        Float activeSumCad = 0f;
        Float activeSumPower = 0f;
        Float restSumSpeed = 0f;
        Float restSumCad = 0f;
        Float restSumPower = 0f;

        nextLapStartTime = getLapMesg().get(0).getFieldLongValue(LAP_STIME);

        getSessionMesg().get(0).setFieldValue(SES_MSPEED, 0f);
        getSessionMesg().get(0).setFieldValue(SES_EMSPEED, 0f);
        getSessionMesg().get(0).setFieldValue(SES_MCAD, (short) 0);
        getSessionMesg().get(0).setFieldValue(SES_MPOW, 0);

        for (Mesg record : getRecordMesg()) {

            //--------------
            // Initiate secExtraRecords
            getSecExtraRecords().add(new RecordExtraMesg(lapNo, C2DateTime));

            //--------------
            // IF LAP START
            currentTimeStamp = record.getFieldLongValue(REC_TIME);
            if ( currentTimeStamp.equals(nextLapStartTime) ) {
                //System.out.println("Lapstart lapIx: " + lapIx + " recordIxStart: " + getLapExtraRecords().get(lapIx).recordIxStart + " recordIx: " + recordIx);

                // Initiate maxSpeed with 0 to be able to compare later
                getLapMesg().get(lapIx).setFieldValue(LAP_MSPEED, 0f);
                getLapMesg().get(lapIx).setFieldValue(LAP_EMSPEED, 0f);
                
                // Save HR and recordIx START
                getLapExtraRecords().get(lapIx).setHrStart(record.getFieldShortValue(REC_HR));
                getLapExtraRecords().get(lapIx).setRecordIxStart(recordIx);

                // Get LAP DATA to be used to find lap-start-end
                Float lapTotalTimer = getLapMesg().get(lapIx).getFieldFloatValue(LAP_TIMER);
                currentLapTime = (lapTotalTimer == null) ? 0f : lapTotalTimer; // in sec

                Short lapIntensityShort = getLapMesg().get(lapIx).getFieldShortValue(LAP_INTENSITY);
                Intensity lapIntensity = (lapIntensityShort == null) ? Intensity.INVALID : Intensity.getByValue(lapIntensityShort);
                currentLapIntensity = Intensity.getStringFromValue(lapIntensity);

                if (lapNo < getNumberOfLaps()) {
                    currentLapTimeEnd = getLapMesg().get(lapIx + 1).getFieldLongValue(LAP_STIME) - 1;
                    nextLapStartTime = getLapMesg().get(lapIx + 1).getFieldLongValue(LAP_STIME);
                } else {
                    currentLapTimeEnd = timeLastRecord;
                }
                // Save LAP END to table
                getLapExtraRecords().get(lapIx).setTimeEnd(currentLapTimeEnd);
            }

            //--------------
            // FIX EMPTY CADENCE
            Short cad = record.getFieldShortValue(REC_CAD);
            if (cad == null) {
                if (recordIx > 0) {
                    Short cadPrev = getRecordMesg().get(recordIx-1).getFieldShortValue(REC_CAD);
                    record.setFieldValue(REC_CAD, cadPrev != null ? cadPrev : (short) 60);
                } else {
                    record.setFieldValue(REC_CAD, (short) 60);
                }
            }
            //--------------
            // Calculate DIST between RECORDS based on Cadence
            //recordDist = lapExtraRecords.get(lapIx).stepLen * cad / 60;
            if (getMySport() == FitFile.MySport.TREADMILL) {
                // TREADMILL
                Float speed = record.getFieldFloatValue(REC_ESPEED);
                Float speedLapSum = getLapExtraRecords().get(lapIx).getSpeedLapSum();
                recordDist = (speed == null) ? 0f : getLapMesg().get(lapIx).getFieldFloatValue(LAP_DIST) * (speed / speedLapSum);
                //--System.out.println("RecordIx: " + recordIx + " Speed: " + speed + " SpeedLapSum: " + speedLapSum + " Dist: " + recordDist + " lapNo: " + lapNo);
            } else {
                // ELLIPTICAL
                recordDist = getLapExtraRecords().get(lapIx).getStepLen() * cad / 60;
            }
            lapSumOfRecordDist += recordDist;
            sumOfRecordDist += recordDist;

            //--------------
            // Calc LAP HR min
            Short recHr = record.getFieldShortValue(REC_HR);
            if (recHr == null) {
                if (recordIx > 0) {
                    Short prevHr = getRecordMesg().get(recordIx - 1).getFieldShortValue(REC_HR);
                    record.setFieldValue(REC_HR, (prevHr == null) ? (short) 60 : prevHr);
                    recHr = record.getFieldShortValue(REC_HR);
                } else {
                    record.setFieldValue(REC_HR, (short) 60);
                    recHr = record.getFieldShortValue(REC_HR);
                }
            } else if (recHr < getLapExtraRecords().get(lapIx).getHrMin()) {
                getLapExtraRecords().get(lapIx).setHrMin(recHr);
            }

            //--------------
            // LEVEL TO CIQ Level 
            // LAPTIME for active laps TO CIQ TrainingSess 
            for (DeveloperField field : record.getDeveloperFields()) {
                if ("Level".equals(field.getName())) {
                    field.setValue(getLapExtraRecords().get(lapIx).getLevel());
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
            if ( currentTimeStamp.equals(currentLapTimeEnd) ) {
                if (debugLaps) System.out.println("LapEND lapIx: " + lapIx + " recordIxStart: " + getLapExtraRecords().get(lapIx).getRecordIxStart() + " recordIx: " + recordIx);

                // Save HR and recordIx END
                getLapExtraRecords().get(lapIx).setHrEnd(record.getFieldShortValue(REC_HR));
                getLapExtraRecords().get(lapIx).setRecordIxEnd(recordIx);
                getLapExtraRecords().get(lapIx).setTimeEnd(record.getFieldLongValue(REC_TIME));

                //--------------
                // CORRECTION
                // INIT of Variables
                corrPerMeter = (lapSumOfRecordDist - getLapMesg().get(lapIx).getFieldFloatValue(LAP_DIST)) / lapSumOfRecordDist;
                if (debugLaps) System.out.println("---- Before CORRECTION1 LapNo:" + lapNo + " LapDist:" + getLapMesg().get(lapIx).getFieldFloatValue(LAP_DIST) + " lapSumOfRecordDist:" + lapSumOfRecordDist + " CorrPerMeter:" + corrPerMeter + " sumOfRecordDist:" + sumOfRecordDist);
                sumOfRecordDist = sumOfRecordDist - lapSumOfRecordDist;
                if (debugLaps) System.out.println("---- Before CORRECTION2 LapNo:" + lapNo + " LapDist:" + getLapMesg().get(lapIx).getFieldFloatValue(LAP_DIST) + " lapSumOfRecordDist:" + lapSumOfRecordDist + " CorrPerMeter:" + corrPerMeter + " sumOfRecordDist:" + sumOfRecordDist);
                lapSumOfRecordDist = 0f;
                if (debugLaps) System.out.println("---- Before CORRECTION3 LapNo:" + lapNo + " LapDist:" + getLapMesg().get(lapIx).getFieldFloatValue(LAP_DIST) + " lapSumOfRecordDist:" + lapSumOfRecordDist + " CorrPerMeter:" + corrPerMeter + " sumOfRecordDist:" + sumOfRecordDist);

                // CORRECTION RECAP LAP
                for (int j=getLapExtraRecords().get(lapIx).getRecordIxStart(); j<=getLapExtraRecords().get(lapIx).getRecordIxEnd(); j++) {
                    Mesg jRecord = getRecordMesg().get(j);
                    //System.out.println("   j:"+j+" lapix:"+lapIx);
                    //recordDist = getLapExtraRecords().get(lapIx).stepLen * getRecordMesg().get(j).getFieldShortValue(REC_CAD) / 60;
                    //--------------
                    // Calculate DIST between RECORDS based on Cadence
                    //recordDist = getLapExtraRecords().get(lapIx).stepLen * cad / 60;
                    if (getMySport() == FitFile.MySport.TREADMILL) {
                        // TREADMILL
                        Float speed = jRecord.getFieldFloatValue(REC_ESPEED);
                        Float speedLapSum = getLapExtraRecords().get(lapIx).getSpeedLapSum();
                        recordDist = (speed == null) ? 0f : getLapMesg().get(lapIx).getFieldFloatValue(LAP_DIST) * (speed / speedLapSum);
                        //--System.out.println("RecordIx: " + recordIx + " Speed: " + speed + " SpeedLapSum: " + speedLapSum + " Dist: " + recordDist + " lapNo: " + lapNo);
                    } else {
                        // ELLIPTICAL
                        recordDist = getLapExtraRecords().get(lapIx).getStepLen() * jRecord.getFieldShortValue(REC_CAD) / 60;
                    }
                    recordDist = recordDist - recordDist * corrPerMeter;
                    lapSumOfRecordDist += recordDist;
                    sumOfRecordDist += recordDist;
                    jRecord.setFieldValue(REC_DIST, sumOfRecordDist);

                    if (j>0) {
                        Float recDistPrev = getRecordMesg().get(j-1).getFieldFloatValue(REC_DIST);
                        if (recDistPrev == null) recDistPrev = 0f;
                        //--------------
                        // Calculate SPEED
                        Float newSpeed = (sumOfRecordDist - recDistPrev) / 1; // 1sec requirement
                        jRecord.setFieldValue(REC_SPEED, newSpeed); // 1sec requirement
                        jRecord.setFieldValue(REC_ESPEED, newSpeed); // 1sec requirement

                        //--------------
                        // Calculate LAP MAX
                        if(newSpeed == null) newSpeed = 0f;
                        Float lapEMSpeed = getLapMesg().get(lapIx).getFieldFloatValue(LAP_EMSPEED);
                        if (lapEMSpeed == null) lapEMSpeed = 0f;

                        if (newSpeed > lapEMSpeed) {
                            getLapMesg().get(lapIx).setFieldValue(LAP_MSPEED, newSpeed);
                            getLapMesg().get(lapIx).setFieldValue(LAP_EMSPEED, newSpeed);
                            //System.out.println("-----recIx:"+recordIx+" lapIx:"+lapIx+" Sp:"+record.getEnhancedSpeed()+"m/s "+mps2kmph(record.getEnhancedSpeed())+"km/h");
                        }
                        //--------------
                        // Calculate SESSION MAX
                        Float sesEMSpeed = getSessionMesg().get(0).getFieldFloatValue(SES_EMSPEED);
                        if (sesEMSpeed == null) sesEMSpeed = 0f;
                        
                        if (newSpeed > sesEMSpeed) {
                            getSessionMesg().get(0).setFieldValue(SES_MSPEED, newSpeed);
                            getSessionMesg().get(0).setFieldValue(SES_EMSPEED, newSpeed);
                            //System.out.println("-----recIx:"+recordIx+" 0:"+0+" Sp:"+record.getEnhancedSpeed()+"m/s "+mps2kmph(record.getEnhancedSpeed())+"km/h");
                        }
                    }

                }
                if (debugLaps) System.out.println("---- After CORRECTION1 LapNo:" + lapNo + " LapDist:" + getLapMesg().get(lapIx).getFieldFloatValue(LAP_DIST) + " lapSumOfRecordDist:" + lapSumOfRecordDist + " CorrPerMeter:" + corrPerMeter + " sumOfRecordDist:" + sumOfRecordDist);

                lapSumOfRecordDist = (float) Math.round(lapSumOfRecordDist);
                sumOfRecordDist = (float) Math.round(sumOfRecordDist);
                if (debugLaps) System.out.println("---- After CORRECTION2 LapNo:" + lapNo + " LapDist:" + getLapMesg().get(lapIx).getFieldFloatValue(LAP_DIST) + " lapSumOfRecordDist:" + lapSumOfRecordDist + " CorrPerMeter:" + corrPerMeter + " sumOfRecordDist:" + sumOfRecordDist);

                //--------------
                // Calculate ACTIVE LAP SUM & MAX
                Float lapTotalTimer = getLapMesg().get(lapIx).getFieldFloatValue(LAP_TIMER);
                Float lapTotalDist = getLapMesg().get(lapIx).getFieldFloatValue(LAP_DIST);
                Float lapAvgSpeed = getLapMesg().get(lapIx).getFieldFloatValue(LAP_ESPEED);
                Short lapAvgCad = getLapMesg().get(lapIx).getFieldShortValue(LAP_CAD);

                if (currentLapIntensity.equals("ACTIVE")) {
                    setActiveTime(getActiveTime() + lapTotalTimer);
                    setActiveDist(getActiveDist() + lapTotalDist);
                    activeSumSpeed = activeSumSpeed + lapAvgSpeed * lapTotalTimer;
                    activeSumCad = activeSumCad + (int) (lapAvgCad * lapTotalTimer);
                }
                // Calculate REST LAP SUM & MAX
                if (currentLapIntensity.equals("REST") || currentLapIntensity.equals("RECOVERY")) {
                    setRestTime(getRestTime() + lapTotalTimer);
                    setRestDist(getRestDist() + lapTotalDist);
                    restSumSpeed = restSumSpeed + lapAvgSpeed * lapTotalTimer;
                    restSumCad = restSumCad + (int) (lapAvgCad * lapTotalTimer);
                }

                lapSumOfRecordDist = 0f;
                lapIx++;
                lapNo++;
            }

            recordIx++;
        }

        // First DISTANCE to ZERO
        getRecordMesg().get(0).setFieldValue(REC_DIST, 0f);
        // First SPEED to same value as second
        getRecordMesg().get(0).setFieldValue(REC_SPEED, getRecordMesg().get(1).getFieldFloatValue(REC_SPEED));
        getRecordMesg().get(0).setFieldValue(REC_ESPEED, getRecordMesg().get(1).getFieldFloatValue(REC_ESPEED));

        // Calculate ACTIVE LAPS AVG
        setActiveAvgSpeed(getActiveTime() > 0 ? activeSumSpeed / getActiveTime() : 0f);
        setActiveAvgCad(getActiveTime() > 0 ? activeSumCad / getActiveTime() : 0f);

        // Calculate REST LAPS AVG
        setRestAvgSpeed(getRestTime() > 0 ? restSumSpeed / getRestTime() : 0f);
        setRestAvgCad(getRestTime() > 0 ? restSumCad / getRestTime() : 0f);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void mergeLapDataFromTextFile(TextLapFile textLapFile) {
        int lapIx= 0;
        for (Mesg lap : getLapMesg()) {

            Float textLapDistFromStart = textLapFile.lapRecords.get(lapIx).getDistance();
            Float textLapDistFromStartPrev = (lapIx > 0) ? textLapFile.lapRecords.get(lapIx-1).getDistance() : 0f;
            Float textLapDist = (lapIx == 0) ? textLapDistFromStart : (textLapDistFromStart - textLapDistFromStartPrev);

            Float lapTimer = lap.getFieldFloatValue(LAP_TIMER);
            Integer lapCad = lap.getFieldIntegerValue(LAP_CAD);
            Long lapStartTime = lap.getFieldLongValue(LAP_STIME);

            lap.setFieldValue(LAP_DIST, textLapDist);
            lap.setFieldValue(LAP_SPEED, textLapDist / lapTimer);
            lap.setFieldValue(LAP_ESPEED, textLapDist / lapTimer);

            if (lapIx != 0) {
                getLapExtraRecords().get(lapIx-1).setTimeEnd(lapStartTime - 1); // 1 SEC
            }
            //System.err.println(" === lapDist: " + record.getTotalDistance() + " lapTime: " + record.getTotalTimerTime() +" speed: " + mps2kmph3(record.getEnhancedAvgSpeed()));
            getLapExtraRecords().get(lapIx).setStepLen(textLapDist / ( lapCad * lapTimer / 60 )); // step length acc to FFRT
            getLapExtraRecords().get(lapIx).setLevel(textLapFile.lapRecords.get(lapIx).getLevel());
            lapIx++;
        }
        // Set lapIx to last lap
        lapIx--;

        // Set Values for last lap
        getLapExtraRecords().get(lapIx).setTimeEnd(timeLastRecord);

        // Set values for session
        totalDistance = textLapFile.lapRecords.get(lapIx).getDistance();
        getSessionMesg().get(0).setFieldValue(SES_DIST, totalDistance);

        avgSpeed = totalDistance / totalTimerTime;
        getSessionMesg().get(0).setFieldValue(SES_SPEED, avgSpeed);
        getSessionMesg().get(0).setFieldValue(SES_ESPEED, avgSpeed);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void encodeIndoorFit (String outputFilePath, boolean encodeWorkoutRecords) {

        System.out.println("Encode Activity FIT File");

        try {
            FileEncoder encode;
            int sanitizedMesgCount = 0;

            encode = new FileEncoder(new java.io.File(outputFilePath), Fit.ProtocolVersion.V2_0);

            for (Mesg record : getAllMesg()) {
                // if (record.getNum() != MesgNum.SPLIT &&
                //    record.getNum() != MesgNum.SPLIT_SUMMARY) {
                if (record.getNum() != MesgNum.SPLIT) {
                    try {
                        encode.write(record);
                    } catch (NullPointerException npe) {
                        // Garmin SDK may throw when dev payload references a removed FieldDescription.
                        if (npe.getMessage() != null && npe.getMessage().contains("fieldDescriptionMesg")) {
                            Mesg sanitized = new Mesg(record);
                            Iterator<DeveloperField> devIt = sanitized.getDeveloperFields().iterator();
                            while (devIt.hasNext()) {
                                devIt.next();
                                devIt.remove();
                            }
                            encode.write(sanitized);
                            sanitizedMesgCount++;
                        } else {
                            throw npe;
                        }
                    }
                }
                
            }

            if (sanitizedMesgCount > 0) {
                System.out.println("Sanitized developer fields in messages during encode: " + sanitizedMesgCount);
            }

            // Close the encoder to finalize the file
            encode.close();
        } catch (FitRuntimeException e) {
            System.err.println("Error opening file ......fit");
            return;
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void shiftTimeFieldIfPresent(Mesg mesg, int fieldNum, int changeSeconds) {
        Long val = mesg.getFieldLongValue(fieldNum);
        if (val != null) {
            mesg.setFieldValue(fieldNum, val + changeSeconds);

            // Optional: fetch the name dynamically from the message definition
            String fieldName = (mesg.getField(fieldNum) != null)
                    ? mesg.getField(fieldNum).getName()
                    : "unknown";

            /* if (mesg.getNum() != (MesgNum.RECORD)) System.out.println("Changed Mesg: " + MesgNum.getStringFromValue(mesg.getNum()) +
                    " Field: " + fieldName + " (" + fieldNum + ") from " + val +
                    " to " + (val + changeSeconds)); */
        }
    }
        
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void createFileSummaryIndoor() {
        savedStrOrgFileInfo += "--------------------------------------------------" + System.lineSeparator();
        savedStrOrgFileInfo += " --> Manufacturer:" + getManufacturer() + ", " + getProduct() + "(" + getProductNo() + ")" + ", SW: v" + getSwVer() + System.lineSeparator();
        savedStrOrgFileInfo += " --> Sport:"+ sport + ", SubSport:" + subsport + ", SportProfile:" + sportProfile + ", WktName:" + wktName + System.lineSeparator();
        savedStrOrgFileInfo += " --> Org activity dateTime Local:" + FitDateTime.toString(getActivityDateTimeLocalOrg()) + System.lineSeparator();
        savedStrOrgFileInfo += " --> New activity dateTime Local:" + FitDateTime.toString(getActivityDateTimeLocal()) + System.lineSeparator();
        savedStrOrgFileInfo += " --> Org activity DateTime UTC:  " + FitDateTime.toString(getActivityDateTimeUTC()) + System.lineSeparator();
        savedStrOrgFileInfo += " --> timeZone:                   " + FitDateTime.offsetToTimeZoneString(getDiffMinutesLocalUTC()) + System.lineSeparator();
        savedStrOrgFileInfo += " --> Org start datetime UTC:     " + FitDateTime.toString(getTimeFirstRecordOrg()) + System.lineSeparator();
        savedStrOrgFileInfo += " --> New start datetime UTC:     " + FitDateTime.toString(getTimeFirstRecord()) + System.lineSeparator();
        
        savedStrOrgFileInfo += "--------------------------------------------------" + System.lineSeparator();


    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void saveLapSummery (String filename) {
        
        // System.out.print(savedStrLapsRestInfoShort);
        try {
            FileWriter myWriter = new FileWriter(filename);
            myWriter.write(savedStrOrgFileInfo);
            myWriter.write(getTempUpdateLogg());
            //myWriter.write(savedStrLapsRestInfoShort);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}