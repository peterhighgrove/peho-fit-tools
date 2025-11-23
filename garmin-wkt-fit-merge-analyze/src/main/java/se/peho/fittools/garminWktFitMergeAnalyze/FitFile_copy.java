package se.peho.fittools.garminWktFitMergeAnalyze;
import com.garmin.fit.*;

import java.io.File;
import java.nio.file.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.util.TimeZone;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.Scanner;

//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
public class FitFile_copy {

    public static final int FID_TIME = FileIdMesg.TimeCreatedFieldNum; //long
    public static final int FID_MANU = FileIdMesg.ManufacturerFieldNum; //int
    public static final int FID_PROD = FileIdMesg.ProductFieldNum; //int
    public static final int FID_PRODNAME = FileIdMesg.ProductNameFieldNum; //string
    public static final int DINFO_TIME = DeviceInfoMesg.TimestampFieldNum; //long
    public static final int DINFO_SWVER = DeviceInfoMesg.SoftwareVersionFieldNum; //float
    public static final int ACT_TIME = ActivityMesg.TimestampFieldNum; //long
    public static final int ACT_LOCTIME = ActivityMesg.LocalTimestampFieldNum; //long
    public static final int WKT_NAME = WorkoutMesg.WktNameFieldNum; //long
    public static final int SES_TIME = SessionMesg.TimestampFieldNum; //long
    public static final int SES_STIME = SessionMesg.StartTimeFieldNum; //long
    public static final int SES_PROFILE = SessionMesg.SportProfileNameFieldNum; //string
    public static final int SES_SPORT = SessionMesg.SportFieldNum; //short -> .getByValue -> Sport
    public static final int SES_SUBSPORT = SessionMesg.SubSportFieldNum; //short -> .getByValue -> SubSport
    public static final int SES_TIMER = SessionMesg.TotalTimerTimeFieldNum; //float
    public static final int SES_ETIMER = SessionMesg.TotalElapsedTimeFieldNum; //float
    public static final int SES_DIST = SessionMesg.TotalDistanceFieldNum; //float
    public static final int SES_SPEED = SessionMesg.AvgSpeedFieldNum; //float
    public static final int SES_ESPEED = SessionMesg.EnhancedAvgSpeedFieldNum; //float
    public static final int EVE_TIME = EventMesg.TimestampFieldNum; //long
    public static final int EVE_STIME = EventMesg.StartTimestampFieldNum; //long
    public static final int EVE_EVENT = EventMesg.EventFieldNum; //long
    public static final int EVE_TYPE = EventMesg.EventTypeFieldNum; //long
    public static final int SPL_STIME = SplitMesg.StartTimeFieldNum; //long
    public static final int SPL_ETIME = SplitMesg.EndTimeFieldNum; //long
    public static final int LAP_TIME = LapMesg.TimestampFieldNum; //long
    public static final int LAP_STIME = LapMesg.StartTimeFieldNum; //long
    public static final int LAP_TIMER = LapMesg.TotalTimerTimeFieldNum; //float
    public static final int LAP_ETIMER = LapMesg.TotalElapsedTimeFieldNum; //float
    public static final int LAP_DIST = LapMesg.TotalDistanceFieldNum; //float
    public static final int LAP_SPEED = LapMesg.AvgSpeedFieldNum; //float
    public static final int LAP_ESPEED = LapMesg.EnhancedAvgSpeedFieldNum; //float
    public static final int REC_TIME = RecordMesg.TimestampFieldNum; //long
    public static final int REC_DIST = RecordMesg.DistanceFieldNum; //float
    public static final int REC_HR = RecordMesg.HeartRateFieldNum; //int
    public static final int REC_SPEED = RecordMesg.SpeedFieldNum; //float
    public static final int REC_ESPEED = RecordMesg.EnhancedSpeedFieldNum; //float
    public static final int REC_POW = RecordMesg.PowerFieldNum; //int
    public static final int REC_LAT = RecordMesg.PositionLatFieldNum; //int
    public static final int REC_LON = RecordMesg.PositionLongFieldNum; //int
    public static final int REC_EALT = RecordMesg.EnhancedAltitudeFieldNum; //float

    String manufacturer;
    int productNo;
    String product = "";
    Float swVer;
    DateTime activityDateTimeUTC;  // Original file
    DateTime activityDateTimeLocal;// Original file
    DateTime activityDateTimeLocalOrg;// Original file
    Long diffMinutesLocalUTC;

    String wktName;
    Sport sport;
    SubSport subsport;
    String sportProfile;
    Float totalTimerTime; //ActivityMesg, excl pauses
    Float totalDistance;
    Float totalDistanceOrg;
    Float avgSpeed; // m/s
    Float maxSpeed; // m/s
    int avgCadence;
    int maxCadence;
    int avgPower;
    int maxPower;

    int numberOfLaps;
    DateTime timeFirstRecord;
    DateTime timeFirstRecordOrg;   // Original file
    DateTime timeLastRecord;
    int numberOfRecords;

    String savedStrOrgFileInfo = "";
    String savedStrLapsAllInfo = "";
    String savedStrLapsActiveInfoShort = "";
    String savedStrLapsRestInfoShort = "";
    String savedStrLapsActiveInfoLong = "";
    String savedStrLapsRestInfoLong = "";


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

    Boolean allMesgFlag = false;
    List<Mesg> allMesg = new ArrayList<>();
    List<Mesg> fileIdMesg = new ArrayList<>();
    List<Mesg> deviceInfoMesg = new ArrayList<>();
    List<Mesg> wktSessionMesg = new ArrayList<>();
    List<Mesg> wktStepMesg = new ArrayList<>();
    List<Mesg> wktRecordMesg = new ArrayList<>();
    List<Mesg> activityMesg = new ArrayList<>();
    List<Mesg> sessionMesg = new ArrayList<>();
    List<Mesg> lapMesg = new ArrayList<>();
    List<Mesg> eventMesg = new ArrayList<>();
    List<Mesg> recordMesg = new ArrayList<>();

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
    List<CourseMesg> courseRecords = new ArrayList<>();
    List<DeveloperDataIdMesg> devDataIdRecords = new ArrayList<>();
    List<DeveloperFieldDescription> devFieldDescrRecords = new ArrayList<>();
    List<DeveloperFieldDefinition> devFieldDefRecords = new ArrayList<>();
    List<FieldDescriptionMesg> fieldDescrRecords = new ArrayList<>();
    List<SessionMesg> sessionRecords = new ArrayList<>();
    List<LapMesg> lapRecords = new ArrayList<>();
    List<RecordMesg> secRecords = new ArrayList<>();
    List<SplitMesg> splitRecords = new ArrayList<>();
    List<SplitSummaryMesg> splitSumRecords = new ArrayList<>();
    List<SpeedZoneMesg> speedZoneRecords = new ArrayList<>();
    List<CadenceZoneMesg> cadZoneRecords = new ArrayList<>();
    List<TimeInZoneMesg> timeInZoneRecords = new ArrayList<>();
    List<HrZoneMesg> hrZoneRecords = new ArrayList<>();
    List<PowerZoneMesg> powerZoneRecords = new ArrayList<>();

    List<LapExtraMesg> lapExtraRecords = new ArrayList<>(); //Not Garmin SDK
    List<RecordExtraMesg> secExtraRecords = new ArrayList<>(); //Not Garmin SDK
    List<GapMesg> gapRecords = new ArrayList<>(); //Not Garmin SDK
    List<PauseMesg> pauseRecords = new ArrayList<>(); //Not Garmin SDK

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
    public FitFile_copy (int syncSecC2File, int syncSecLapDistCalc) {
    	this.c2SyncSecondsC2File = syncSecC2File;
    	this.c2SyncSecondsLapDistCalc = syncSecLapDistCalc;
    }
    public FitFile_copy () {

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    class GapMesg {
        int no;
        Long timeStart;
        Long timeStop;
        Long timeGap; //seconds
        int ixStart;
        int ixStop;
        int ixLap;
        Float distStart;
        Float distStop;
        int latStart;
        int lonStart;
        int latStop;
        int lonStop;
        Float distGap;
        Float distGapGps;
        Float altStart;
        Float altStop;
        Float altGap;

        public GapMesg() {

        }

        public GapMesg(int pauseNo, Long startDateTime, Long stopDateTime,
         int ixStart, int ixStop, int ixLap,
          Float dist,
           int lat1, int lon1, int lat2, int lon2,
           Float altStart, Float altStop) {
            this.no = pauseNo;
            this.timeStart = startDateTime;
            this.timeStop = stopDateTime;
            this.timeGap = stopDateTime - startDateTime;
            this.ixStart = ixStart;
            this.ixStop = ixStop;
            this.ixLap = ixLap;
            this.distStart = dist;
            this.latStart = lat1;
            this.lonStart = lon1;
            this.latStop = lat2;
            this.lonStop = lon2;
            this.distGapGps = (float) GeoUtils.distCalc(this.latStart, this.lonStart, this.latStop, this.lonStop);
            this.altStart = altStart;
            this.altStop = altStop;
            this.altGap = altStop - altStart;
        }

        public void calcTimeGap () {
            this.timeGap = this.timeStop - this.timeStart;
        }

        public void calcDistGapGps () {
            this.distGapGps = (float) GeoUtils.distCalc(this.latStart, this.lonStart, this.latStop, this.lonStop);
        }

        public void calcAltGap () {
            this.altGap = this.altStop - this.altStart;
        }

        public void calcDistGap () {
            this.distGap = this.distStop - this.distStart;
        }

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    class PauseMesg {
        int no;
        Long timeStart;
        Long timeStop;
        Long timePause; //seconds
        int ixStart;
        int ixStop;
        int ixEvStart;
        int ixEvStop;
        int ixLap;
        Float distStart;
        int latStart;
        int lonStart;
        int latStop;
        int lonStop;
        Float distPause;
        Float altStart;
        Float altStop;
        Float altPause;

        public PauseMesg() {

        }

        public PauseMesg(int pauseNo, Long startDateTime, Long stopDateTime,
         int ixStart, int ixStop, int ixEvStart, int ixEvStop, int ixLap,
          Float dist,
           int lat1, int lon1, int lat2, int lon2,
           Float altStart, Float altStop) {
            this.no = pauseNo;
            this.timeStart = startDateTime;
            this.timeStop = stopDateTime;
            this.timePause = stopDateTime - startDateTime;
            this.ixStart = ixStart;
            this.ixStop = ixStop;
            this.ixEvStart = ixEvStart;
            this.ixEvStop = ixEvStop;
            this.ixLap = ixLap;
            this.distStart = dist;
            this.latStart = lat1;
            this.lonStart = lon1;
            this.latStop = lat2;
            this.lonStop = lon2;
            this.distPause = (float) GeoUtils.distCalc(this.latStart, this.lonStart, this.latStop, this.lonStop);
            this.altStart = altStart;
            this.altStop = altStop;
            this.altPause = altStop - altStart;
        }

    }
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
    public void addDistToRecords(int fromRecordIx, Float distToAdd) {
        Float recordDist;
        //  +1 cause not to update 3 new records
        int ix = 0;
        for (ix = fromRecordIx; ix < numberOfRecords; ix++) {
            recordDist = recordMesg.get(ix).getFieldFloatValue(REC_DIST);
            recordMesg.get(ix).setFieldValue(REC_DIST, (recordDist + distToAdd));
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public int findIxInAllMesg(Long timeToSearchFor) {
        int ix = 0;
        // FIND IX i allMesg list
        for (Mesg record : allMesg) {
            if (record.getNum() == MesgNum.RECORD) {
                if (record.getFieldLongValue(REC_TIME) != null) {
                    if (record.getFieldLongValue(REC_TIME).equals(timeToSearchFor)) {
                        break;
                    }
                }
            }
            ix += 1;
        }
        return ix;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public int findIxInRecordMesg(Long timeToSearchFor) {
        int ix = 0;
        // FIND IX i allMesg list
        for (Mesg record : recordMesg) {
            //if (record.getNum() == MesgNum.RECORD) {
                if (record.getFieldLongValue(REC_TIME) != null) {
                    if (record.getFieldLongValue(REC_TIME).equals(timeToSearchFor)) {
                        break;
                    }
                }
            //}
            ix += 1;
        }
        return ix;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void createGapList() {
        int gapCounter = 0;
        int gapThreshold = 1; //number of seconds to define a gap
        boolean inPause = false;

        int recordIx = 0;
        int pauseIx = 0;
        int lapIx = 0;

        gapRecords.clear();

        Long lastRecordTime = recordMesg.get(0).getFieldLongValue(REC_TIME) - 1;

        for (Mesg record : recordMesg) {
            
            // if statment to see if this is a PAUSE
            if (!inPause && pauseRecords.size() > 0 && (record.getFieldLongValue(REC_TIME) >= pauseRecords.get(pauseIx).timeStart)) {
                inPause = true;

            // if in PAUSE, see if pause ends
            } else if (inPause && (record.getFieldLongValue(REC_TIME) >= pauseRecords.get(pauseIx).timeStop)) {
                if (pauseIx < pauseRecords.size()-1) {
                    pauseIx += 1;
                }
                inPause = false;

            // if in PAUSE and see records
            } else if (inPause) {
                System.out.println("WARNING - Records in pause");

            } else if ((!inPause && (record.getFieldLongValue(REC_TIME) - lastRecordTime) > gapThreshold)) {
                // Check if GAP

                gapCounter += 1;

                GapMesg newGap = new GapMesg();
                newGap.no = gapCounter;
                newGap.timeStart = lastRecordTime;
                newGap.timeStop = record.getFieldLongValue(REC_TIME);
                newGap.calcTimeGap();
                newGap.ixStart = recordIx-1;
                newGap.ixStop = recordIx;

                // Find LapNo
                try {
                    while ((lapMesg.get(lapIx).getFieldLongValue(LAP_STIME) <= record.getFieldLongValue(REC_TIME))) {
                        lapIx += 1;
                        if (lapIx >= lapMesg.size()) {
                            break;
                        }
                    }
                } catch(Exception e) {
                }
                newGap.ixLap = lapIx-1;

                newGap.distStart = recordMesg.get(recordIx-1).getFieldFloatValue(REC_DIST);
                newGap.distStop = record.getFieldFloatValue(REC_DIST);
                newGap.calcDistGap();

                newGap.latStart = recordMesg.get(recordIx-1).getFieldIntegerValue(REC_LAT);
                newGap.lonStart = recordMesg.get(recordIx-1).getFieldIntegerValue(REC_LON);
                newGap.latStop = record.getFieldIntegerValue(REC_LAT);
                newGap.lonStop = record.getFieldIntegerValue(REC_LON);
                newGap.calcDistGapGps();

                newGap.altStart = recordMesg.get(recordIx-1).getFieldFloatValue(REC_EALT);
                newGap.altStop = record.getFieldFloatValue(REC_EALT);
                newGap.calcAltGap();

                gapRecords.add(newGap);
            }
            lastRecordTime = record.getFieldLongValue(REC_TIME);
            recordIx += 1;
        }

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printGapList(String gapCommandInput) {

        int hrDiff = 0;
        String hrSign = "";
        String newMinDistToShowUserInput = null;
        Long newMinDistToShow = 0l;

        if (gapCommandInput != null && gapCommandInput.equals("f")) {
            int minDistToShow = 10;
            Scanner userInput = new Scanner (System.in);

            while (newMinDistToShowUserInput == null) {
                System.out.print("Enter min distance for gaps to show: ");
                newMinDistToShowUserInput = userInput.nextLine();
                System.out.println();

                if (PehoUtils.safeParseInt(newMinDistToShowUserInput) == null) {
                    System.out.println("Not an integer as answer. Redo!");
                    System.out.println("-------------------------------");
                    newMinDistToShowUserInput = null;
                } else {
                    System.out.println("   -------------------------------");
                    System.out.println("   FILTERED LIST with dist higher than " + newMinDistToShowUserInput + " m.");
                    System.out.println("   -------------------------------");
                    newMinDistToShow = (long) PehoUtils.safeParseInt(newMinDistToShowUserInput);
                }
            }
        }

        System.out.println("--------------------------------------------------");
        System.out.println("GAPS IN FILE");
        System.out.println(" File  between " + FitDateTime.toString(timeFirstRecord,diffMinutesLocalUTC) + " >>>> " + FitDateTime.toString(timeLastRecord,diffMinutesLocalUTC));
        System.out.println(String.format(" TotalTime:%1$.0fsec Dist:%2$.0fm", totalTimerTime, totalDistance));
        //System.out.print(" Event:" + record.getEvent());
        //System.out.print(" No:" + record.getEvent().getValue());

        for (GapMesg record : gapRecords) {
            if (record.distGap >= newMinDistToShow) {
                System.out.print("   Gap (" + record.no + ")");
                System.out.print(String.format(" %1$dsec %2$.0fm ele%3$.1fm", record.timeGap, record.distGap, record.altGap));
                hrDiff = recordMesg.get(record.ixStop).getFieldIntegerValue(REC_HR) - recordMesg.get(record.ixStart).getFieldIntegerValue(REC_HR);
                if (hrDiff>=0) {
                    hrSign = "+";
                }
                System.out.print(String.format(" HR:%1$d%2$s%3$d", recordMesg.get(record.ixStart).getFieldIntegerValue(REC_HR), hrSign, hrDiff));

                if (gapCommandInput == null) {
                    // Show minimal
                } else if (gapCommandInput.equals("g")) {
                    System.out.print(String.format("  @dist:%1$.0fm", record.distStart));
                    System.out.print(" " + FitDateTime.toString(new DateTime(record.timeStart),diffMinutesLocalUTC));
                    System.out.print(" ele:" + (record.altStart) + "m");
                    System.out.print(" lapNo:" + (record.ixLap+1));
                    System.out.print("   @ix:" + (record.ixStart) + "->" + (record.ixStop));
                    //System.out.print("sec TimerTrigger:" + record.getTimerTrigger());
                }
                System.out.println();
            }

        }
        System.out.println("--------------------------------------------------");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void createPauseList() {
        int pauseCounter = 0;
        boolean inPause = false;
        Long startPauseTime = 0l;
        int ixRecordStart = 0;
        int ixRecordStop = 0;
        int ixEvStart = 0;
        int ixEvStop = 0;
        int ixLap = 0;

        int latStart = 0;
        int lonStart = 0;
        int latStop = 0;
        int lonStop = 0;


        int recordIx = 0;
        int lapIx = 0;
        int eventIx = 0;

        pauseRecords.clear();

        for (Mesg record : eventMesg){
            //if (Event.getStringFromValue(record.getEvent()).equals("TIMER")) {
            if (record.getFieldValue(EVE_EVENT).equals(Event.TIMER.getValue())) {
                // -------------- 
                // STOP event (pause START)
                if (pauseCounter > 0 && !inPause && record.getFieldValue(EVE_TYPE).equals(EventType.START.getValue())) {
                    System.out.println("==> WARNING - START Event w/o Stopping first @" + FitDateTime.toString(record.getFieldLongValue(EVE_TIME),diffMinutesLocalUTC));

                } else if (!inPause && record.getFieldValue(EVE_TYPE).equals(EventType.STOP_ALL.getValue())) {
                    inPause = true;
                    pauseCounter += 1;

                    if (record.getFieldLongValue(EVE_TIME).equals(timeLastRecord.getTimestamp())) {
                        //System.out.println("   SLUT   " + FitDateTime.toString(record.getTimestamp()));
                    } else {
                        startPauseTime = record.getFieldLongValue(EVE_TIME);

                        // FIND record in secRecords
                        while (!recordMesg.get(recordIx).getFieldLongValue(REC_TIME).equals(record.getFieldLongValue(EVE_TIME))) {
                            recordIx += 1;
                        }
                        ixRecordStart = recordIx;
                        ixEvStart = eventIx;
                }
                }
                
                // -------------- 
                // START event (pause END)
                if (inPause && record.getFieldValue(EVE_TYPE).equals(EventType.START.getValue())) {

                    while (!recordMesg.get(recordIx).getFieldLongValue(REC_TIME).equals(record.getFieldLongValue(EVE_TIME))) {
                        recordIx += 1;
                    }

                    // Find LapNo
                    try {
                        while ((lapMesg.get(lapIx).getFieldLongValue(LAP_STIME) <= record.getFieldLongValue(EVE_TIME))) {
                            lapIx += 1;
                            if (lapIx >= (lapMesg.size()-0)) {
                                break;
                            }
                        }
                    } catch(Exception e) {
                    }
                    ixRecordStop = recordIx;
                    ixLap = lapIx-1;
                    ixEvStop = eventIx;

                    Long timeStop = record.getFieldLongValue(EVE_TIME);
                    Float distStart = recordMesg.get(ixRecordStart).getFieldFloatValue(REC_DIST);
                    if (recordMesg.get(ixRecordStart).getFieldIntegerValue(REC_LAT) != null) {
                        latStart = recordMesg.get(ixRecordStart).getFieldIntegerValue(REC_LAT);
                        lonStart = recordMesg.get(ixRecordStart).getFieldIntegerValue(REC_LON);
                    } else {
                        int i = 0;
                        while (recordMesg.get(ixRecordStart-i).getFieldIntegerValue(REC_LAT) == null){
                            i++;
                        }
                        latStart = recordMesg.get(ixRecordStart-i).getFieldIntegerValue(REC_LAT);
                        lonStart = recordMesg.get(ixRecordStart-i).getFieldIntegerValue(REC_LON);
                    }
                    if (recordMesg.get(ixRecordStop).getFieldIntegerValue(REC_LAT) != null) {
                        latStop = recordMesg.get(ixRecordStop).getFieldIntegerValue(REC_LAT);
                        lonStop = recordMesg.get(ixRecordStop).getFieldIntegerValue(REC_LON);
                    } else {
                        int i = 0;
                        while (recordMesg.get(ixRecordStop-i).getFieldIntegerValue(REC_LAT) == null){
                            i++;
                        }
                        latStop = recordMesg.get(ixRecordStop-i).getFieldIntegerValue(REC_LAT);
                        lonStop = recordMesg.get(ixRecordStop-i).getFieldIntegerValue(REC_LON);
                    }
                    Float altStart = recordMesg.get(ixRecordStart).getFieldFloatValue(REC_EALT);
                    Float altStop = recordMesg.get(ixRecordStop).getFieldFloatValue(REC_EALT);
                    pauseRecords.add(new PauseMesg(pauseCounter, startPauseTime, timeStop,
                     ixRecordStart, ixRecordStop, ixEvStart, ixEvStop, ixLap,
                      distStart, latStart, lonStart, latStop, lonStop,
                        altStart, altStop));

                    inPause = false;
                }
            }
            eventIx += 1;

        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printPauseList(String pauseCommandInput) {

        int hrDiff = 0;
        String hrSign = "";
        String newMinDistToShowUserInput = null;
        Long newMinDistToShow = 0l;

        if (pauseCommandInput != null && pauseCommandInput.equals("f")) {
            int minDistToShow = 10;
            Scanner userInput = new Scanner (System.in);

            while (newMinDistToShowUserInput == null) {
                System.out.print("Enter min distance for pauses to show: ");
                newMinDistToShowUserInput = userInput.nextLine();
                System.out.println();

                if (PehoUtils.safeParseInt(newMinDistToShowUserInput) == null)  {
                    System.out.println("Not an integer as answer. Redo!");
                    System.out.println("-------------------------------");
                    newMinDistToShowUserInput = null;
                } else {
                    System.out.println("   -------------------------------");
                    System.out.println("   FILTERED LIST with dist higher than " + newMinDistToShowUserInput + " m.");
                    System.out.println("   -------------------------------");
                    newMinDistToShow = (long) PehoUtils.safeParseInt(newMinDistToShowUserInput);
                }
            }
        }

        System.out.println("--------------------------------------------------");
        System.out.println("PAUSES IN FILE");
        System.out.println(" File  between " + FitDateTime.toString(timeFirstRecord,diffMinutesLocalUTC) + " >>>> " + FitDateTime.toString(timeLastRecord,diffMinutesLocalUTC));
        System.out.println(String.format(" TotalTime:%1$.0fsec Dist:%2$.0fm", totalTimerTime, totalDistance));
        //System.out.print(" Event:" + record.getEvent());
        //System.out.print(" No:" + record.getEvent().getValue());

        for (PauseMesg record : pauseRecords) {
            if ((record.ixStop - record.ixStart) > 1) {
                System.out.println("==> WARNING - Data Records in pause! Pause no: " + record.no);
            }
            if (record.distPause >= newMinDistToShow) {
                System.out.print("   Pause (" + record.no + ")");
                System.out.print(String.format(" %1$dsec %2$.0fm ele%3$.1fm", record.timePause, record.distPause, record.altPause));
                hrDiff = recordMesg.get(record.ixStop).getFieldIntegerValue(REC_HR) - recordMesg.get(record.ixStart).getFieldIntegerValue(REC_HR);
                if (hrDiff>0) {
                    hrSign = "+";
                }
                System.out.print(String.format(" HR:%1$d%2$s%3$d", recordMesg.get(record.ixStart).getFieldIntegerValue(REC_HR), hrSign, hrDiff));
                System.out.print(" @dist:" + PehoUtils.m2km2(record.distStart) + "km");

                if (pauseCommandInput == null) {
                    // Show minimal
                } else if (pauseCommandInput.equals("d")) {
                    System.out.print(" " + FitDateTime.toString(record.timeStart,diffMinutesLocalUTC));
                    System.out.print(" Ele:" + (record.altStart) + "m");
                    System.out.print(" lapNo:" + (record.ixLap+1));
                    System.out.print("   @ix:" + (record.ixStart) + "->" + (record.ixStop));
                    System.out.print(" @ixEv:" + (record.ixEvStart) + "->" + (record.ixEvStop));
                    //System.out.print("sec TimerTrigger:" + record.getTimerTrigger());
                }
                System.out.println();
            }

        }
        System.out.println("--------------------------------------------------");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void fillRecordsInGap() {

            savedStrOrgFileInfo += "Filling gaps with 1sec records" + System.lineSeparator();

            int numberOfNewSeconds = 0;
            int numberOfNewRecords = 0;
            int allMesgIxStart = 0;
            int recordMesgIxStart = 0;

            Mesg startGapRecord;
            Long startTime = 0l;
            Float startDist = 0f;
            Float startSpeed = 0f;
            Short startHr = 0;
            int startPow = 0;
            int startLat = 0;
            int startLon = 0;
            Float startAlt = 0f;

            Mesg stopGapRecord;
            Long stopTime = 0l;
            Float stopDist = 0f;
            Float stopSpeed = 0f;
            Short stopHr = 0;
            int stopPow = 0;
            int stopLat = 0;
            int stopLon = 0;
            Float stopAlt = 0f;

            Double distDelta = 0d;
            Double speedDelta = 0d;
            Double hrDelta = 0d;
            Double powDelta = 0d;
            Double altDelta = 0d;

        for (GapMesg record : gapRecords) {
            numberOfNewSeconds = record.timeGap.intValue();
            //System.out.println("numberOfNewSeconds: "+numberOfNewSeconds);

            numberOfNewRecords = numberOfNewSeconds - 1;
            //System.out.println("numberOfNewRecords: "+numberOfNewRecords);

            allMesgIxStart = findIxInAllMesg(record.timeStop);
            recordMesgIxStart = findIxInRecordMesg(record.timeStop);

            startGapRecord = recordMesg.get(findIxInRecordMesg(record.timeStart));
            startTime = startGapRecord.getFieldLongValue(REC_TIME);
            startDist = startGapRecord.getFieldFloatValue(REC_DIST);
            startSpeed = startGapRecord.getFieldFloatValue(REC_ESPEED);
            startHr = startGapRecord.getFieldShortValue(REC_HR);
            startPow = startGapRecord.getFieldIntegerValue(REC_POW);
            startLat = startGapRecord.getFieldIntegerValue(REC_LAT);
            startLon = startGapRecord.getFieldIntegerValue(REC_LON);
            startAlt = startGapRecord.getFieldFloatValue(REC_EALT);

            stopGapRecord = recordMesg.get(recordMesgIxStart);
            stopTime = stopGapRecord.getFieldLongValue(REC_TIME);
            stopDist = stopGapRecord.getFieldFloatValue(REC_DIST);
            stopSpeed = stopGapRecord.getFieldFloatValue(REC_ESPEED);
            stopHr = stopGapRecord.getFieldShortValue(REC_HR);
            stopPow = stopGapRecord.getFieldIntegerValue(REC_POW);
            stopLat = stopGapRecord.getFieldIntegerValue(REC_LAT);
            stopLon = stopGapRecord.getFieldIntegerValue(REC_LON);
            stopAlt = stopGapRecord.getFieldFloatValue(REC_EALT);

            distDelta = (double) (stopDist - startDist) / numberOfNewSeconds;
            //System.out.println("distDelta: "+distDelta);
            speedDelta = (double) (stopSpeed - startSpeed) / numberOfNewSeconds;
            //System.out.println("speedDelta: "+speedDelta);
            hrDelta = (double) (stopHr - startHr) / numberOfNewSeconds;
            powDelta = (double) (stopPow - startPow) / numberOfNewSeconds;
            //System.out.println("powerDelta: "+powDelta);
            altDelta = (double) (stopAlt - startAlt) / numberOfNewSeconds;
            //System.out.println("altDelta: "+altDelta);
            savedStrOrgFileInfo += "-- GapNo: "+record.no+", dist: "+record.distGap+"m, time: "+record.timeGap+"sec, @Dist: "+startDist+"-"+stopDist+"m, time: "+FitDateTime.toString(record.timeStart,diffMinutesLocalUTC) + System.lineSeparator();

            for (int i=1; i<=numberOfNewRecords; i++) {
                Mesg newRecord = new Mesg(startGapRecord);
                newRecord.setFieldValue(REC_TIME, startTime + i);
                newRecord.setFieldValue(REC_DIST, startDist + distDelta * i);
                newRecord.setFieldValue(REC_ESPEED, startSpeed + speedDelta * i);
                newRecord.setFieldValue(REC_SPEED, startSpeed + speedDelta * i);
                newRecord.setFieldValue(REC_HR, Math.round(startHr + hrDelta * i));
                newRecord.setFieldValue(REC_POW, Math.round(startPow + powDelta * i));
                newRecord.setFieldValue(REC_EALT, startAlt + altDelta * i);
                double[] newRecordCoords = GeoUtils.interpolate(startLat, startLon, stopLat, stopLon, distDelta * i);
                newRecord.setFieldValue(REC_LAT, GeoUtils.toSemicircles(newRecordCoords[0]));
                newRecord.setFieldValue(REC_LON, GeoUtils.toSemicircles(newRecordCoords[1]));

                allMesg.add(allMesgIxStart + i-1, newRecord); // -1 due to that i doesn't start at 0
                recordMesg.add(recordMesgIxStart + i-1, newRecord); // -1 due to that i doesn't start at 0
                numberOfRecords++;
            }
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void addRecordAtStart(Long timeToIncrease, double[] coords) {
        // Parsing GPS input data
        // ----------------------
        int newLatSemi = GeoUtils.toSemicircles(coords[0]);
        int newLonSemi = GeoUtils.toSemicircles(coords[1]);
        /* System.out.printf("Input: %-40s →\n", input);
        System.out.printf("  Decimal Degrees: Lat %.8f, Lon %.8f%n", coords[0], coords[1]);
        System.out.printf("  Garmin Semicircles: Lat %d, Lon %d%n", newLatSemi, newLonSemi);
        System.out.printf("  Back to Decimal: Lat %.8f, Lon %.8f%n%n", GeoUtils.fromSemicircles(newLatSemi), GeoUtils.fromSemicircles(newLonSemi));
        */

        // Collecting existing record data
        // --------------------------------

        Mesg orgStartRecord = recordMesg.get(0);
        Long startTime = orgStartRecord.getFieldLongValue(REC_TIME);
        Float startDist = orgStartRecord.getFieldFloatValue(REC_DIST);
        int startLat = orgStartRecord.getFieldIntegerValue(REC_LAT);
        int startLon = orgStartRecord.getFieldIntegerValue(REC_LON);
        Float startAlt = orgStartRecord.getFieldFloatValue(REC_EALT);

        String info2 = "";
        info2 += "ORG TIMES" + System.lineSeparator();
        info2 += "startTime:"+FitDateTime.toString(new DateTime(startTime)) + System.lineSeparator();

        Double distFromNew = GeoUtils.distCalc(newLatSemi, newLonSemi, startLat, startLon);
        Long orgStartTime = startTime;
        startTime -= timeToIncrease;
        timeFirstRecord = new DateTime(startTime);



        //Float newTotalDistChange = (float) distFromNew + gapToChange.distGap;
        //Long timeFromNew = (long) (gapToChange.timeGap - timeToNew);
        //Float newAlt = (float) (gapToChange.altStart + gapToChange.altGap * (distToNew / (distToNew + distFromNew)));

        // Setting data in NEW RECORD
        // --------------------------
        Mesg newStartRecord = new Mesg(orgStartRecord);
        newStartRecord.setFieldValue(REC_TIME, startTime);
        newStartRecord.setFieldValue(REC_DIST, 0);
        newStartRecord.setFieldValue(REC_SPEED, distFromNew / timeToIncrease);
        newStartRecord.setFieldValue(REC_ESPEED, distFromNew / timeToIncrease);
        newStartRecord.setFieldValue(REC_LAT, newLatSemi);
        newStartRecord.setFieldValue(REC_LON, newLonSemi);
        //newStartRecord.setFieldValue(REC_EALT, newAlt);

        String info = "";
        info += "-- New gap coordinates:" + System.lineSeparator();
        info += String.format("    Decimal Degrees: Lat %.8f, Lon %.8f%n", coords[0], coords[1]);
        info += String.format("    Garmin Semicircles: Lat %d, Lon %d%n", newLatSemi, newLonSemi);
        info += String.format("    Back to Decimal: Lat %.8f, Lon %.8f%n", GeoUtils.fromSemicircles(newLatSemi), GeoUtils.fromSemicircles(newLonSemi));
        info += "   >>> Dist/Time from new point:" + Math.round(distFromNew) + "m / " + timeToIncrease + "sec " + PehoUtils.sec2minSecLong(timeToIncrease) + "min" + System.lineSeparator();
        savedStrOrgFileInfo += info;
        System.out.print(info);

        // Adding new start record
        allMesg.add(findIxInAllMesg(orgStartTime), newStartRecord);
        recordMesg.add(0, newStartRecord);
        numberOfRecords++;

        eventMesg.get(0).setFieldValue(EVE_TIME, startTime);

        // Setting speed on the original start record
        recordMesg.get(1).setFieldValue(REC_SPEED, distFromNew / timeToIncrease);
        recordMesg.get(1).setFieldValue(REC_ESPEED, distFromNew / timeToIncrease);

        // Adding distance to all records starting from org start record
        addDistToRecords(1, (float) (distFromNew-0.0));

        // Log for troubleshooting
        Long lapTime = lapMesg.get(0).getFieldLongValue(LAP_TIME);
        Long lapSTime = lapMesg.get(0).getFieldLongValue(LAP_STIME);
        Long sesTime = lapMesg.get(0).getFieldLongValue(SES_TIME);
        Long sesSTime = lapMesg.get(0).getFieldLongValue(SES_STIME);

        info2 += "Lap time/stime:"+FitDateTime.toString(new DateTime(lapTime))+" / "+FitDateTime.toString(new DateTime(lapSTime)) + System.lineSeparator();
        info2 += "Ses time/stime:"+FitDateTime.toString(new DateTime(sesTime))+" / "+FitDateTime.toString(new DateTime(sesSTime)) + System.lineSeparator();
        info2 += "Act time UTC:"+FitDateTime.toString(activityDateTimeUTC) + System.lineSeparator();
        info2 += "Act time Loc:"+FitDateTime.toString(activityDateTimeLocal) + System.lineSeparator();

        // Updating LAP DATA
        //------------------
        lapTime -= timeToIncrease;
        lapSTime -= timeToIncrease;
        lapMesg.get(0).setFieldValue(LAP_TIME, lapTime);
        lapMesg.get(0).setFieldValue(LAP_STIME, lapSTime);

        Float lapTimer = lapMesg.get(0).getFieldFloatValue(LAP_TIMER) + timeToIncrease;
        Float lapETimer = lapMesg.get(0).getFieldFloatValue(LAP_ETIMER) + timeToIncrease;
        Float lapDist = (float) (lapMesg.get(0).getFieldFloatValue(LAP_DIST) + distFromNew);
        lapMesg.get(0).setFieldValue(LAP_TIMER, lapTimer);
        lapMesg.get(0).setFieldValue(LAP_ETIMER, lapETimer);
        lapMesg.get(0).setFieldValue(LAP_DIST, lapDist);
        lapMesg.get(0).setFieldValue(LAP_SPEED, (lapDist / lapTimer));
        lapMesg.get(0).setFieldValue(LAP_ESPEED, (lapDist / lapTimer));

        //----------------------
        // Updating SESSION DATA
        //----------------------
        sesTime -= timeToIncrease;
        sesSTime -= timeToIncrease;
        sessionMesg.get(0).setFieldValue(SES_TIME, sesTime);
        sessionMesg.get(0).setFieldValue(SES_STIME, sesSTime);

        totalTimerTime += timeToIncrease;
        sessionMesg.get(0).setFieldValue(SES_TIMER, totalTimerTime);
        sessionMesg.get(0).setFieldValue(SES_ETIMER, sessionMesg.get(0).getFieldFloatValue(SES_ETIMER) + timeToIncrease);

        totalDistance = recordMesg.get(numberOfRecords-1).getFieldFloatValue(RecordMesg.DistanceFieldNum);
        sessionMesg.get(0).setFieldValue(SES_DIST, totalDistance);

        avgSpeed = totalDistance / totalTimerTime;
        sessionMesg.get(0).setFieldValue(SES_SPEED, avgSpeed);
        sessionMesg.get(0).setFieldValue(SES_ESPEED, avgSpeed);

        //----------------------
        // Updating ACTIVITY DATA
        //----------------------
        activityDateTimeUTC = new DateTime(activityDateTimeUTC.getTimestamp() - timeToIncrease);
        activityDateTimeLocal = new DateTime(activityDateTimeLocal.getTimestamp() - timeToIncrease);
        activityMesg.get(0).setFieldValue(ACT_TIME, activityDateTimeUTC.getTimestamp());
        activityMesg.get(0).setFieldValue(ACT_LOCTIME, activityDateTimeLocal.getTimestamp());

        info2 += "NEW TIMES" + System.lineSeparator();
        info2 += "startTime:"+FitDateTime.toString(new DateTime(startTime)) + System.lineSeparator();
        info2 += "Lap time/stime:"+FitDateTime.toString(new DateTime(lapTime))+" / "+FitDateTime.toString(new DateTime(lapSTime)) + System.lineSeparator();
        info2 += "Ses time/stime:"+FitDateTime.toString(new DateTime(sesTime))+" / "+FitDateTime.toString(new DateTime(sesSTime)) + System.lineSeparator();
        info2 += "Act time UTC:"+FitDateTime.toString(activityDateTimeUTC) + System.lineSeparator();
        info2 += "Act time Loc:"+FitDateTime.toString(activityDateTimeLocal) + System.lineSeparator();
        System.out.print(info2);
        savedStrOrgFileInfo += info2;


    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void addRecordInGap(int gapNo, double[] coords) {

        // Parsing GPS input data
        // ----------------------
        int newLatSemi = GeoUtils.toSemicircles(coords[0]);
        int newLonSemi = GeoUtils.toSemicircles(coords[1]);
        /* System.out.printf("Input: %-40s →\n", input);
        System.out.printf("  Decimal Degrees: Lat %.8f, Lon %.8f%n", coords[0], coords[1]);
        System.out.printf("  Garmin Semicircles: Lat %d, Lon %d%n", newLatSemi, newLonSemi);
        System.out.printf("  Back to Decimal: Lat %.8f, Lon %.8f%n%n", GeoUtils.fromSemicircles(newLatSemi), GeoUtils.fromSemicircles(newLonSemi));
        */

        // Collecting existing record data
        // --------------------------------

        GapMesg gapToChange = gapRecords.get(gapNo-1);

        Mesg startGapRecord = recordMesg.get(gapToChange.ixStart);
        Long startTime = startGapRecord.getFieldLongValue(REC_TIME);
        Float startDist = startGapRecord.getFieldFloatValue(REC_DIST);
        int startLat = startGapRecord.getFieldIntegerValue(REC_LAT);
        int startLon = startGapRecord.getFieldIntegerValue(REC_LON);
        Float startAlt = startGapRecord.getFieldFloatValue(REC_EALT);

        Mesg stopGapRecord = recordMesg.get(gapToChange.ixStop);
        Long stopTime = stopGapRecord.getFieldLongValue(REC_TIME);
        int stopLat = stopGapRecord.getFieldIntegerValue(REC_LAT);
        int stopLon = stopGapRecord.getFieldIntegerValue(REC_LON);
        Float stopAlt = stopGapRecord.getFieldFloatValue(REC_EALT);

        Double distToNew = GeoUtils.distCalc(startLat, startLon, newLatSemi, newLonSemi);
        Double distFromNew = GeoUtils.distCalc(newLatSemi, newLonSemi, stopLat, stopLon);
        Float newTotalDistChange = (float) (distToNew + distFromNew) - gapToChange.distGap;
        Long timeToNew = (long) (gapToChange.timeGap * (distToNew / (distToNew + distFromNew)));
        Long timeFromNew = (long) (gapToChange.timeGap - timeToNew);
        Float newAlt = (float) (gapToChange.altStart + gapToChange.altGap * (distToNew / (distToNew + distFromNew)));

        // Setting data in NEW RECORD
        // --------------------------
        Mesg newGapRecord = new Mesg(stopGapRecord);
        newGapRecord.setFieldValue(REC_TIME, startTime + timeToNew);
        newGapRecord.setFieldValue(REC_DIST, startDist + distToNew);
        newGapRecord.setFieldValue(REC_SPEED, distToNew / timeToNew);
        newGapRecord.setFieldValue(REC_ESPEED, distToNew / timeToNew);
        newGapRecord.setFieldValue(REC_LAT, newLatSemi);
        newGapRecord.setFieldValue(REC_LON, newLonSemi);
        newGapRecord.setFieldValue(REC_EALT, newAlt);
        
        stopGapRecord.setFieldValue(REC_DIST, newGapRecord.getFieldFloatValue(REC_DIST) + distFromNew);
        stopGapRecord.setFieldValue(REC_SPEED, distFromNew / timeFromNew);
        stopGapRecord.setFieldValue(REC_ESPEED, distFromNew / timeFromNew);


        addDistToRecords(gapToChange.ixStop+1, newTotalDistChange);

        String info = "";
        info += "-- New gap coordinates:" + System.lineSeparator();
        info += String.format("    Decimal Degrees: Lat %.8f, Lon %.8f%n", coords[0], coords[1]);
        info += String.format("    Garmin Semicircles: Lat %d, Lon %d%n", newLatSemi, newLonSemi);
        info += String.format("    Back to Decimal: Lat %.8f, Lon %.8f%n", GeoUtils.fromSemicircles(newLatSemi), GeoUtils.fromSemicircles(newLonSemi));

        info += "   >>> ixStart:" + gapToChange.ixStart + System.lineSeparator();
        info += "   >>> Calc old dist:" + Math.round(GeoUtils.distCalc(startLat, startLon, stopLat, stopLon)) + "m" + System.lineSeparator();
        info += "   >>> Garmin old Dist/Time" + gapToChange.distGap + "m / " + PehoUtils.sec2minSecLong(gapToChange.timeGap) + "sec" + System.lineSeparator();
        info += "   >>> Dist/Time to new point:" + Math.round(distToNew) + "m / " + Math.round(gapToChange.timeGap * (distToNew / (distToNew + distFromNew))) + "sec" + System.lineSeparator();
        info += "   >>> Dist/Time from new point:" + Math.round(distFromNew) + "m / " + Math.round(gapToChange.timeGap * (distFromNew / (distToNew + distFromNew))) + "sec" + System.lineSeparator();
        info += "   >>> Dist change:" + Math.round(newTotalDistChange) + "m" + System.lineSeparator();
        info += "   >>> ixStop:" + gapToChange.ixStop + System.lineSeparator();
        savedStrOrgFileInfo += info;
        System.out.print(info);

        allMesg.add(findIxInAllMesg(stopTime), newGapRecord);
        recordMesg.add(gapToChange.ixStop, newGapRecord);
        numberOfRecords++;

        // Updating LAP DATA
        //------------------
        Float lapTime = lapMesg.get(gapToChange.ixLap).getFieldFloatValue(LAP_TIMER);
        Float lapDist = lapMesg.get(gapToChange.ixLap).getFieldFloatValue(LAP_DIST) + newTotalDistChange;
        //lapMesg.get(gapToChange.ixLap).setFieldValue(LAP_TIMER, (lapTime + pauseToShorten.timePause - newPauseTime));
        //lapMesg.get(gapToChange.ixLap).setFieldValue(LAP_ETIMER, (lapTime + pauseToShorten.timePause - newPauseTime));
        lapMesg.get(gapToChange.ixLap).setFieldValue(LAP_DIST, lapDist);
        lapMesg.get(gapToChange.ixLap).setFieldValue(LAP_SPEED, (lapDist / lapTime));
        lapMesg.get(gapToChange.ixLap).setFieldValue(LAP_ESPEED, (lapDist / lapTime));

        // Updating SESSION DATA
        //----------------------
        //totalTimerTime += (float) gapToChange.timeGap - newPauseTime;
        //sessionMesg.get(0).setFieldValue(SES_TIMER, totalTimerTime);
        //elapsedTimerTime += (float) gapToChange.timeGap - newPauseTime;
        //sessionMesg.get(0).setFieldValue(SES_ETIMER, elapsedTimerTime);

        totalDistance = recordMesg.get(numberOfRecords-1).getFieldFloatValue(RecordMesg.DistanceFieldNum);
        sessionMesg.get(0).setFieldValue(SES_DIST, totalDistance);

        avgSpeed = totalDistance / totalTimerTime;
        sessionMesg.get(0).setFieldValue(SES_SPEED, avgSpeed);
        sessionMesg.get(0).setFieldValue(SES_ESPEED, avgSpeed);

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void increasePause(int pauseNo, Long secondsToPutIntoPause) {

        PauseMesg pauseToIncrease = pauseRecords.get(pauseNo-1);

        Mesg startPauseEvent = eventMesg.get(pauseToIncrease.ixEvStart);
        Long orgStartEventTime = startPauseEvent.getFieldLongValue(EVE_TIME);

        Mesg startPauseRecord = recordMesg.get(pauseToIncrease.ixStart);
        Float orgStartPauseDist = startPauseRecord.getFieldFloatValue(REC_DIST);

        Long newStartEventTime = orgStartEventTime - secondsToPutIntoPause;
        startPauseEvent.setFieldValue(EVE_TIME, newStartEventTime);

        Long recordToDeleteTime = orgStartEventTime;
        int i = 0;
        int recordToDeleteIx = pauseToIncrease.ixStart;
        int allMesgToDeleteIx = findIxInAllMesg(orgStartEventTime);
        while (recordToDeleteTime > newStartEventTime) {
            //System.out.println("Deleting record time:"+recordToDeleteTime+" Rix:"+recordToDeleteIx+" Aix:"+allMesgToDeleteIx);
            allMesg.remove(allMesgToDeleteIx);
            recordMesg.remove(recordToDeleteIx);

            allMesgToDeleteIx--;
            while (allMesg.get(allMesgToDeleteIx).getNum() != MesgNum.RECORD){
                allMesgToDeleteIx--;
            }
            recordToDeleteIx--;
            recordToDeleteTime--;
            i++;

        }
        numberOfRecords -= i;

        savedStrOrgFileInfo += "Increased pause no: " + pauseNo + System.lineSeparator();
        savedStrOrgFileInfo += "-- Pause increased with " + secondsToPutIntoPause + "sec to " + PehoUtils.sec2minSecLong(pauseToIncrease.timePause+secondsToPutIntoPause) + "min" + System.lineSeparator();


        // Increase distance after the shortened pause, starting from 1 after pause stop
        // ------------------------------------------------------
        Float newStartPauseDist = recordMesg.get(recordToDeleteIx).getFieldFloatValue(REC_DIST);
        Float distChangeValue = newStartPauseDist-orgStartPauseDist; // Will be negative
        System.out.println("Dist:"+orgStartPauseDist+"-"+newStartPauseDist+"="+distChangeValue);
        addDistToRecords(recordToDeleteIx+1, distChangeValue);

        // Updating LAP DATA
        //------------------
        Float lapTime = lapMesg.get(pauseToIncrease.ixLap).getFieldFloatValue(LAP_TIMER) - secondsToPutIntoPause;
        Float lapDist = lapMesg.get(pauseToIncrease.ixLap).getFieldFloatValue(LAP_DIST) + distChangeValue;
        lapMesg.get(pauseToIncrease.ixLap).setFieldValue(LAP_TIMER, (lapTime));
        //lapMesg.get(pauseToShorten.ixLap).setFieldValue(LAP_ETIMER, (lapTime - secondsToPutIntoPause));
        lapMesg.get(pauseToIncrease.ixLap).setFieldValue(LAP_DIST, (lapDist));
        lapMesg.get(pauseToIncrease.ixLap).setFieldValue(LAP_SPEED, (lapDist / lapTime));
        lapMesg.get(pauseToIncrease.ixLap).setFieldValue(LAP_ESPEED, (lapDist / lapTime));

        // Updating SESSION DATA
        //----------------------
        totalTimerTime -= (float) secondsToPutIntoPause;
        sessionMesg.get(0).setFieldValue(SES_TIMER, totalTimerTime);
        //elapsedTimerTime -= (float) secondsToPutIntoPause;
        //sessionMesg.get(0).setFieldValue(SES_ETIMER, elapsedTimerTime);


        totalDistance = recordMesg.get(numberOfRecords-1).getFieldFloatValue(RecordMesg.DistanceFieldNum);
        sessionMesg.get(0).setFieldValue(SES_DIST, totalDistance);

        avgSpeed = totalDistance / totalTimerTime;
        sessionMesg.get(0).setFieldValue(SES_SPEED, avgSpeed);
        sessionMesg.get(0).setFieldValue(SES_ESPEED, avgSpeed);

    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void shortenPause(int pauseNo, Long newPauseTime) {

        PauseMesg pauseToShorten = pauseRecords.get(pauseNo-1);

        if (newPauseTime > pauseToShorten.timePause) {
            System.out.println("----- NEW Pause time is to large ------");
            System.exit(0);
        }
        if (pauseNo > pauseRecords.size()) {
            System.out.println("----- PauseNo is to large ------");
            System.exit(0);
        }

        Mesg startPauseRecord = recordMesg.get(pauseToShorten.ixStart);
        Mesg stopGapRecord = recordMesg.get(pauseToShorten.ixStop); // Org PAUSE STOP = New GAP STOP

        Long startPauseTime = startPauseRecord.getFieldLongValue(REC_TIME);
        Float startPauseDist = startPauseRecord.getFieldFloatValue(REC_DIST);
        Float startPauseAlt = startPauseRecord.getFieldFloatValue(REC_EALT);
        int startPauseLat = startPauseRecord.getFieldIntegerValue(REC_LAT);
        int startPauseLon = startPauseRecord.getFieldIntegerValue(REC_LON);

        Long startGapTime = startPauseTime + newPauseTime;
        Float startGapDist = startPauseDist;
        Float startGapAlt = startPauseAlt;
        int startGapLat = startPauseLat;
        int startGapLon = startPauseLon;
        int startGapPow = 0;
        if (recordMesg.get(pauseToShorten.ixStop+1).getFieldIntegerValue(REC_POW) != null){
            startGapPow = recordMesg.get(pauseToShorten.ixStop+1).getFieldIntegerValue(REC_POW);
        }

        Long stopGapTime = stopGapRecord.getFieldLongValue(REC_TIME); // New GAP END
        Float stopGapDist = startGapDist + pauseToShorten.distPause; // New GAP END
        Float stopGapAlt = stopGapRecord.getFieldFloatValue(REC_EALT);
        int stopGapLat = stopGapRecord.getFieldIntegerValue(REC_LAT);
        int stopGapLon = stopGapRecord.getFieldIntegerValue(REC_LON);
        int stopGapPow = startGapPow;

        // Speed Vaules
        Float startGapSpeed = (float) (pauseToShorten.distPause / (stopGapTime - startGapTime)) ;
        Float stopGapSpeed = startGapSpeed;

        stopGapRecord.setFieldValue(REC_SPEED, stopGapSpeed);
        stopGapRecord.setFieldValue(REC_ESPEED, stopGapSpeed);

        // Power Value always missing in record after Pause
        stopGapRecord.setFieldValue(REC_POW, stopGapPow);

        savedStrOrgFileInfo += "Shortened pause no: " + pauseNo + System.lineSeparator();
        savedStrOrgFileInfo += "-- Pause decreased from " + pauseToShorten.timePause + "sec to " + newPauseTime + "sec" + System.lineSeparator();
        savedStrOrgFileInfo += "--> newSpeed:"+PehoUtils.mps2minpkm(startGapSpeed)+"km/min gpsDist:"+pauseToShorten.distPause+
            "m gapStartDist:"+startGapDist+"m gapEnd:"+stopGapDist+
            "m gapEnd-startTime:"+(stopGapTime - startGapTime)+"s newTime:"+newPauseTime+"s" + System.lineSeparator();
        System.out.println("--> newSpeed:"+PehoUtils.mps2minpkm(startGapSpeed)+"km/min gpsDist:"+pauseToShorten.distPause+
            "m gapStartDist:"+startGapDist+"m gapEnd:"+stopGapDist+
            "m gapEnd-startTime:"+(stopGapTime - startGapTime)+"s newTime:"+newPauseTime+"s");

        // Create new PAUSE STOP & GAP START
        // ------------------------------------------------
        Mesg startGapNewRecord = new Mesg(startPauseRecord); // New PAUSE STOP = GAP START
        eventMesg.get(pauseToShorten.ixEvStop).setFieldValue(EVE_TIME, startGapTime);
        startGapNewRecord.setFieldValue(REC_TIME, startGapTime);
        startGapNewRecord.setFieldValue(REC_SPEED, startGapSpeed);
        startGapNewRecord.setFieldValue(REC_ESPEED, startGapSpeed);
        startGapNewRecord.setFieldValue(REC_POW, startGapPow);
        allMesg.add(findIxInAllMesg(stopGapTime), startGapNewRecord);
        recordMesg.add(pauseToShorten.ixStop, startGapNewRecord);
        numberOfRecords++;

        // Increase distance after the shortened pause, starting from 1 after pause stop
        // ------------------------------------------------------
        addDistToRecords(pauseToShorten.ixStop+1, pauseToShorten.distPause);

        // Updating LAP DATA
        //------------------
        Float lapTime = lapMesg.get(pauseToShorten.ixLap).getFieldFloatValue(LAP_TIMER) + pauseToShorten.timePause - newPauseTime;
        Float lapDist = lapMesg.get(pauseToShorten.ixLap).getFieldFloatValue(LAP_DIST) + pauseToShorten.distPause;
        lapMesg.get(pauseToShorten.ixLap).setFieldValue(LAP_TIMER, (lapTime));
        //lapMesg.get(pauseToShorten.ixLap).setFieldValue(LAP_ETIMER, (lapTime));
        lapMesg.get(pauseToShorten.ixLap).setFieldValue(LAP_DIST, (lapDist));
        lapMesg.get(pauseToShorten.ixLap).setFieldValue(LAP_SPEED, (lapDist / lapTime));
        lapMesg.get(pauseToShorten.ixLap).setFieldValue(LAP_ESPEED, (lapDist / lapTime));

        // Updating SESSION DATA
        //----------------------
        totalTimerTime += (float) pauseToShorten.timePause - newPauseTime;
        sessionMesg.get(0).setFieldValue(SES_TIMER, (totalTimerTime));
        //elapsedTimerTime += (float) pauseToShorten.timePause - newPauseTime;
        //sessionMesg.get(0).setFieldValue(SES_ETIMER, elapsedTimerTime);

        totalDistance = recordMesg.get(numberOfRecords-1).getFieldFloatValue(RecordMesg.DistanceFieldNum);
        sessionMesg.get(0).setFieldValue(SES_DIST, (totalDistance));

        avgSpeed = totalDistance / totalTimerTime;
        sessionMesg.get(0).setFieldValue(SES_SPEED, (avgSpeed));
        sessionMesg.get(0).setFieldValue(SES_ESPEED, (avgSpeed));
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
    	File oldNameFile = new File(oldName);
    	File newNameFile = new File(newName);
    	if (newNameFile.exists()) {
    		// Rename file (or directory)
            try {
                Files.move(Paths.get(newName), Paths.get(newName + "-backup"), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("============== RENAME BACKUP UNSUCCESSFUL from:" + newName + " to:" + newName + "-backup");
            }
            System.out.println("============== RENAME BACKUP SUCCESS from:" + newName + " to:" + newName + "-backup");
    	}
		
        try {
            Files.move(Paths.get(oldName), Paths.get(newName), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("============== RENAME SRC FILE UNSUCCESSFUL from:" + oldName + " to:" + newName);
        }
        System.out.println("============== RENAME SRC FILE SUCCESS from:" + oldName + " to:" + newName);
    }
    	
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String getFilenameAndSetNewSportProfileName(String suffix, String outputFilenameBase) {
        
        String newProfileName = "";

        if (allMesgFlag) {
            if (suffix.equals("")){
                newProfileName = sportProfile;
            } else {
                newProfileName = suffix;
            }
            if (!wktRecords.isEmpty()) {
                if (wktRecords.get(0).getWktName() != null) {
                    newProfileName = wktRecords.get(0).getWktName();
                }
            }

            newProfileName = newProfileName + " " + PehoUtils.m2km1(totalDistance) + "km";
            sessionMesg.get(0).setFieldValue(SES_PROFILE, newProfileName);
        } else {
            newProfileName = sportProfile;

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
                sessionRecords.get(0).setSportProfileName(newProfileName + " " + ((float) (Math.round(totalDistance/100))/10) + "km " + suffix);
                //System.exit(0);
            } else {
                if (wktStepRecords.isEmpty()) {
                    System.out.println("========> NO wkt STEP RECORDS");
                    //System.exit(0);
                } else {

                    if (wktRecords.get(0).getWktName() == null) {
                        wktRecords.get(0).setWktName(wktRecords.get(0).getWktName() + "");
                        System.out.println("================ wktName == NULL");
                    }
                    String newWktName = wktRecords.get(0).getWktName();
                    newWktName = newWktName.replace("Bike ","");
                    newWktName = newWktName.replace(" (bike)","");
                    newWktName = newWktName.replace("HR","");
                    newProfileName = newProfileName + " " + newWktName + " " + ((float) (Math.round(totalDistance/100))/10) + "km " + suffix;
                    sessionRecords.get(0).setSportProfileName(newProfileName);
                }
            }
        }

        System.out.println("----> New SportProfile:  " + newProfileName);

        outputFilenameBase = "-" + newProfileName;
        outputFilenameBase = outputFilenameBase.replace("/","!");
        outputFilenameBase = outputFilenameBase.replace("×","x");

        System.out.println("----> New FilenameBase: " + outputFilenameBase);
        return outputFilenameBase;
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
        Long currentTimeStamp = 0l;
        Long nextLapStartTime = 0l;
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
    public void SyncDataInTimeFromSkiErg(String useManualC2SyncSeconds) {
         
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
            Long currentTimeStamp = 0l;
            Long nextLapStartTime = 0l;
            Float currentLapTime = 0f;
            String currentLapIntensity = "";
            Long currentLapTimeEnd = 0l;
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
                if (lapRecords.get(lapIx).getEnhancedMaxSpeed() != null && record.getEnhancedSpeed() > lapRecords.get(lapIx).getEnhancedMaxSpeed()) {
                    lapRecords.get(lapIx).setEnhancedMaxSpeed(record.getEnhancedSpeed());
                    lapRecords.get(lapIx).setMaxSpeed(record.getEnhancedSpeed());
                }
                
                //--------------
                // Calc LAPSUM MAX CAD POWER
                if (recordIx<(numberOfRecords-tempC2SyncSecondsC2File-1)) {
                    currentLapSumCadence += secRecords.get(recordIx+tempC2SyncSecondsC2File).getCadence();
                    currentLapSumPower += secRecords.get(recordIx+tempC2SyncSecondsC2File).getPower();
                }
                if (lapRecords.get(lapIx).getMaxCadence() != null && record.getCadence() > lapRecords.get(lapIx).getMaxCadence()) {
                    lapRecords.get(lapIx).setMaxCadence(record.getCadence());
                }
                if (lapRecords.get(lapIx).getMaxPower() != null && record.getPower() > lapRecords.get(lapIx).getMaxPower()) {
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
                        
                        if (useManualC2SyncSeconds.toLowerCase().equals("auto1")) {
                        activeFakeSumSpeed =activeFakeSumSpeed+ (float) lapRecords.get(lapIx).getAvgSpeed();
                        activeFakeSumCad =activeFakeSumCad+ (float) lapRecords.get(lapIx).getAvgCadence();
                        activeFakeSumPower =activeFakeSumPower+ (float) lapRecords.get(lapIx).getAvgPower();
                        } else {
                        if (lapRecords.get(lapIx).getAvgPower() > maxActiveLapAvgPower) {
                            maxActiveLapAvgPower = (float) lapRecords.get(lapIx).getAvgPower();
                        }
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
        Long currentTimeStamp = 0l;
        Long nextLapStartTime = 0l;
        Float currentLapTime = 0f;
        String currentLapIntensity = "";
        Long currentLapTimeEnd = 0l;
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
            if (lapRecords.get(lapIx).getEnhancedMaxSpeed() != null && record.getEnhancedSpeed() > lapRecords.get(lapIx).getEnhancedMaxSpeed()) {
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
            if (lapRecords.get(lapIx).getMaxCadence() != null && record.getCadence() > lapRecords.get(lapIx).getMaxCadence()) {
                lapRecords.get(lapIx).setMaxCadence(record.getCadence());
            }
            if (lapRecords.get(lapIx).getMaxPower() != null && record.getPower() > lapRecords.get(lapIx).getMaxPower()) {
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
                    lapRecords.get(lapIx).setTotalDistance((float) secRecords.get(recordIx+c2SyncSecondsLapDistCalc).getDistance() - lastLapTotalDistance);
                    lastLapTotalDistance = secRecords.get(recordIx+c2SyncSecondsLapDistCalc).getDistance();

                    lapRecords.get(lapIx).setAvgSpeed((float) (lapRecords.get(lapIx).getTotalDistance() / lapRecords.get(lapIx).getTotalTimerTime()));
                    //System.out.println("--- lap: " + lapNo + " dist: " + lapRecords.get(lapIx).getTotalDistance() + " time: " + lapRecords.get(lapIx).getTotalTimerTime() + " speed: "+ (float) (lapRecords.get(lapIx).getAvgSpeed())+" "+(float) (lapRecords.get(lapIx).getTotalDistance() / lapRecords.get(lapIx).getTotalTimerTime()));
                    lapRecords.get(lapIx).setEnhancedAvgSpeed((float) (lapRecords.get(lapIx).getTotalDistance() / lapRecords.get(lapIx).getTotalTimerTime()));
                /*} else {
                    // LAP DIST & AVG SPEED last lap
                    lapRecords.get(lapIx).setTotalDistance((float) secRecords.get(recordIx).getDistance() - lastLapTotalDistance);
                    lapRecords.get(lapIx).setAvgSpeed((float) lapRecords.get(lapIx).getTotalDistance() / lapRecords.get(lapIx).getTotalTimerTime());
                    lapRecords.get(lapIx).setEnhancedAvgSpeed((float) lapRecords.get(lapIx).getTotalDistance() / lapRecords.get(lapIx).getTotalTimerTime());
                }*/
                
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
        Long currentTimeStamp = 0l;
        Long nextLapStartTime = 0l;
        Float currentLapTime = 0f;
        String currentLapIntensity = "";
        Long currentLapTimeEnd = 0l;

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
            //System.err.println(" === lapDist: " + record.getTotalDistance() + " lapTime: " + record.getTotalTimerTime() +" speed: " + mps2kmph3(record.getEnhancedAvgSpeed()));
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

            System.out.println("-- END MesgBroadcaster(decode)." + sweDateTime.format(Calendar.getInstance().getTime()));

            if (allMesgFlag) {
                decode.read(in, new MesgListener() {
                    public void onMesg(Mesg mesg) {
                        allMesg.add(mesg);

                        switch (mesg.getNum()) {
                            case MesgNum.FILE_ID:
                                fileIdMesg.add(mesg);
                                break;
                            case MesgNum.DEVICE_INFO:
                                deviceInfoMesg.add(mesg);
                                break;
                            case MesgNum.ACTIVITY:
                                activityMesg.add(mesg);
                                break;
                            case MesgNum.WORKOUT_SESSION:
                                wktSessionMesg.add(mesg);
                                break;
                            case MesgNum.WORKOUT_STEP:
                                wktStepMesg.add(mesg);
                                break;
                            case MesgNum.WORKOUT:
                                wktRecordMesg.add(mesg);
                                break;
                            case MesgNum.SESSION:
                                sessionMesg.add(mesg);
                                break;
                            case MesgNum.LAP:
                                lapMesg.add(mesg);
                                break;
                            case MesgNum.EVENT:
                                eventMesg.add(mesg);
                                break;
                            case MesgNum.RECORD:
                                recordMesg.add(mesg);
                                break;
                        }
                    }
                });
            } else {
                broadcaster = new MesgBroadcaster(decode);
                addListeners();
                decode.read(in, broadcaster);
            }

            // Decode the FIT file

            try {
                in.close();
            } catch (Exception e) {
                System.out.println("============== Closing Exception: " + e);
            }

            if (allMesgFlag) {
                // allMESG FLAG
                //-----------------
                if (fileIdMesg.get(0).getFieldIntegerValue(FID_MANU) != null) {
                    manufacturer = Manufacturer.getStringFromValue(fileIdMesg.get(0).getFieldIntegerValue(FID_MANU));
                    if (manufacturer == "GARMIN") {
                        if (fileIdMesg.get(0).getFieldIntegerValue(FID_MANU) != null) {
                            productNo = fileIdMesg.get(0).getFieldIntegerValue(FID_PROD);
                            product = GarminProduct.getStringFromValue(fileIdMesg.get(0).getFieldIntegerValue(FID_PROD));
                        }
                    }
                }

                swVer = deviceInfoMesg.get(0).getFieldFloatValue(DINFO_SWVER);
                timeFirstRecord = new DateTime (recordMesg.get(0).getFieldLongValue(REC_TIME));

                if (activityMesg.get(0).getFieldLongValue(ACT_TIME) == null) {
                    activityMesg.get(0).setFieldValue(ACT_TIME, timeFirstRecord.getTimestamp());
                }
                activityDateTimeUTC = new DateTime (activityMesg.get(0).getFieldLongValue(ACT_TIME));
                if (activityMesg.get(0).getFieldLongValue(ACT_LOCTIME) == null) {
                    activityMesg.get(0).setFieldValue(ACT_LOCTIME, timeFirstRecord.getTimestamp());
                }
                activityDateTimeLocal = new DateTime(activityMesg.get(0).getFieldLongValue(ACT_LOCTIME));
                diffMinutesLocalUTC = (activityDateTimeLocal.getTimestamp() - activityDateTimeUTC.getTimestamp()) / 60;
                activityDateTimeLocalOrg = activityDateTimeLocal;

                if (!wktRecordMesg.isEmpty()) {
                    if (wktRecordMesg.get(0).getFieldStringValue(WKT_NAME) != null) {
                        wktName = wktRecordMesg.get(0).getFieldStringValue(WKT_NAME);
                    }
                }

                if (sessionMesg.get(0).getFieldValue(SES_SPORT) != null) {
                    sport = Sport.getByValue(sessionMesg.get(0).getFieldShortValue(SES_SPORT));
                }
                if (sessionMesg.get(0).getFieldValue(SES_SUBSPORT) != null) {
                    subsport = SubSport.getByValue(sessionMesg.get(0).getFieldShortValue(SES_SUBSPORT));
                }
                if (sessionMesg.get(0).getFieldStringValue(SES_PROFILE) == null) {
                    sessionMesg.get(0).setFieldValue(SES_PROFILE, "noProfile");
                } else {
                    sportProfile = sessionMesg.get(0).getFieldStringValue(SES_PROFILE);
                }
                if (sessionMesg.get(0).getFieldFloatValue(SES_TIMER) != null) {
                    totalTimerTime = sessionMesg.get(0).getFieldFloatValue(SES_TIMER);
                }
                if (sessionMesg.get(0).getFieldFloatValue(SES_DIST) != null) {
                    totalDistance = sessionMesg.get(0).getFieldFloatValue(SES_DIST);
                    totalDistanceOrg = totalDistance;
                }
                if (sessionMesg.get(0).getFieldFloatValue(SES_SPEED) != null) {
                    avgSpeed = sessionMesg.get(0).getFieldFloatValue(SES_SPEED);
                }
                if (sessionMesg.get(0).getFieldFloatValue(SES_ESPEED) != null) {
                    avgSpeed = sessionMesg.get(0).getFieldFloatValue(SES_ESPEED);
                }

                numberOfLaps = lapMesg.size();
                
                timeFirstRecordOrg = timeFirstRecord;
                timeLastRecord = new DateTime (recordMesg.get(recordMesg.size() - 1).getFieldLongValue(REC_TIME));
                numberOfRecords = recordMesg.size();

            } else {
                // NOT allMESG FLAG
                //-----------------
                try {
                    
                    if (sessionRecords.get(0).getSportProfileName() == null) {
                        sessionRecords.get(0).setSportProfileName("noProfile");
                    } 

                    numberOfLaps = lapRecords.size();
                    timeFirstRecord = secRecords.get(0).getTimestamp();
                    timeFirstRecordOrg = timeFirstRecord;
                    timeLastRecord = secRecords.get(secRecords.size() - 1).getTimestamp();

                    if (activityRecords.get(0).getLocalTimestamp() == null) {
                        activityRecords.get(0).setLocalTimestamp(secRecords.get(0).getTimestamp().getTimestamp());
                    }
                    if (activityRecords.get(0).getTimestamp() == null) {
                        activityRecords.get(0).setTimestamp(secRecords.get(0).getTimestamp());
                    }
                    diffMinutesLocalUTC = (activityRecords.get(0).getLocalTimestamp() - activityRecords.get(0).getTimestamp().getTimestamp()) / 60;
                    activityDateTimeUTC = activityRecords.get(0).getTimestamp();
                    activityDateTimeLocal = new DateTime(activityRecords.get(0).getLocalTimestamp());
                    activityDateTimeLocalOrg = activityDateTimeLocal;
                    
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
            }
            System.out.println("FIT file successfully read. Total records: " + numberOfRecords + " -- " + FitDateTime.toString(timeLastRecord,0));

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

            if (allMesgFlag) {

                for (Mesg record : allMesg) {
                    encode.write(record);
                }

            } else {

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
                for (SpeedZoneMesg record : speedZoneRecords) {
                    encode.write(record);
                }
                for (CadenceZoneMesg record : cadZoneRecords) {
                    encode.write(record);
                }
                for (TimeInZoneMesg record : timeInZoneRecords) {
                    encode.write(record);
                }
                for (HrZoneMesg record : hrZoneRecords) {
                    encode.write(record);
                }
                for (PowerZoneMesg record : powerZoneRecords) {
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
                for (CourseMesg record : courseRecords) {
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
                for (SplitSummaryMesg record : splitSumRecords) {
                    encode.write(record);
                }
                for (SplitMesg record : splitRecords) {
                    encode.write(record);
                }
                for (RecordMesg record : secRecords) {
                    encode.write(record);
                }
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

        broadcaster.addListener(new MesgListener() {
            @Override
            public void onMesg(Mesg mesg) {
                allMesg.add(mesg);

                switch (mesg.getNum()) {
                    case MesgNum.SESSION:
                        sessionMesg.add(mesg);
                        break;
                    case MesgNum.LAP:
                        lapMesg.add(mesg);
                        break;
                    case MesgNum.EVENT:
                        eventMesg.add(mesg);  // Just save original reference
                        break;
                    case MesgNum.RECORD:
                        recordMesg.add(mesg);  // Just save original reference
                        break;
                }
            }
        });
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
        
        broadcaster.addListener(new SpeedZoneMesgListener() {
            @Override
            public void onMesg(SpeedZoneMesg mesg) {
                speedZoneRecords.add(mesg);
            }
        });
        
        broadcaster.addListener(new CadenceZoneMesgListener() {
            @Override
            public void onMesg(CadenceZoneMesg mesg) {
                cadZoneRecords.add(mesg);
            }
        });
        
        broadcaster.addListener(new TimeInZoneMesgListener() {
            @Override
            public void onMesg(TimeInZoneMesg mesg) {
                timeInZoneRecords.add(mesg);
            }
        });
        
        broadcaster.addListener(new HrZoneMesgListener() {
            @Override
            public void onMesg(HrZoneMesg mesg) {
                hrZoneRecords.add(mesg);
            }
        });
        
        broadcaster.addListener(new PowerZoneMesgListener() {
            @Override
            public void onMesg(PowerZoneMesg mesg) {
                powerZoneRecords.add(mesg);
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
        
        broadcaster.addListener(new CourseMesgListener() {
            @Override
            public void onMesg(CourseMesg mesg) {
                courseRecords.add(mesg);
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
        
        broadcaster.addListener(new SplitMesgListener() {
            @Override
            public void onMesg(SplitMesg mesg) {
                splitRecords.add(mesg);
            }
        });
        
        broadcaster.addListener(new SplitSummaryMesgListener() {
            @Override
            public void onMesg(SplitSummaryMesg mesg) {
                splitSumRecords.add(mesg);
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
        if (allMesgFlag) {
            Long timeToChange;
            for (Mesg mesg : allMesg) {
                switch (mesg.getNum()) {
                    case MesgNum.FILE_ID:
                        timeToChange = mesg.getFieldLongValue(FID_TIME);
                        if (timeToChange != null) {
                            mesg.setFieldValue(FID_TIME, timeToChange + changeSeconds);
                        }
                        break;
                    case MesgNum.ACTIVITY:
                        timeToChange = mesg.getFieldLongValue(ACT_TIME);
                        if (timeToChange != null) {
                            mesg.setFieldValue(ACT_TIME, timeToChange + changeSeconds);
                        }
                        timeToChange = mesg.getFieldLongValue(ACT_LOCTIME);
                        if (timeToChange != null) {
                            mesg.setFieldValue(ACT_LOCTIME, timeToChange + changeSeconds);
                        }
                        break;
                    case MesgNum.DEVICE_INFO:
                        timeToChange = mesg.getFieldLongValue(DINFO_TIME);
                        if (timeToChange != null) {
                            mesg.setFieldValue(DINFO_TIME, timeToChange + changeSeconds);
                        }
                        break;
                    case MesgNum.EVENT:
                        timeToChange = mesg.getFieldLongValue(EVE_TIME);
                        if (timeToChange != null) {
                            mesg.setFieldValue(EVE_TIME, timeToChange + changeSeconds);
                        }
                        timeToChange = mesg.getFieldLongValue(EVE_STIME);
                        if (timeToChange != null) {
                            mesg.setFieldValue(EVE_STIME, timeToChange + changeSeconds);
                        }
                        break;
                    case MesgNum.SESSION:
                        timeToChange = mesg.getFieldLongValue(SES_TIME);
                        if (timeToChange != null) {
                            mesg.setFieldValue(SES_TIME, timeToChange + changeSeconds);
                        }
                        timeToChange = mesg.getFieldLongValue(SES_STIME);
                        if (timeToChange != null) {
                            mesg.setFieldValue(SES_STIME, timeToChange + changeSeconds);
                        }
                        break;
                    case MesgNum.LAP:
                        timeToChange = mesg.getFieldLongValue(LAP_TIME);
                        if (timeToChange != null) {
                            mesg.setFieldValue(LAP_TIME, timeToChange + changeSeconds);
                        }
                        timeToChange = mesg.getFieldLongValue(LAP_STIME);
                        if (timeToChange != null) {
                            mesg.setFieldValue(LAP_STIME, timeToChange + changeSeconds);
                        }
                        break;
                    case MesgNum.SPLIT:
                        timeToChange = mesg.getFieldLongValue(SPL_STIME);
                        if (timeToChange != null) {
                            mesg.setFieldValue(SPL_STIME, timeToChange + changeSeconds);
                        }
                        timeToChange = mesg.getFieldLongValue(SPL_ETIME);
                        if (timeToChange != null) {
                            mesg.setFieldValue(SPL_ETIME, timeToChange + changeSeconds);
                        }
                        break;
                    case MesgNum.RECORD:
                        timeToChange = mesg.getFieldLongValue(REC_TIME);
                        //System.out.print("RecTime:"+timeToChange+" ");
                        if (timeToChange != null) {
                            mesg.setFieldValue(REC_TIME, timeToChange + changeSeconds);
                            //System.out.print(timeToChange+changeSeconds);
                        }
                        break;
                }
            }
            timeFirstRecord = new DateTime (recordMesg.get(0).getFieldLongValue(REC_TIME));
            timeLastRecord = new DateTime (recordMesg.get(recordMesg.size() - 1).getFieldLongValue(REC_TIME));
            activityDateTimeLocal = new DateTime(activityMesg.get(0).getFieldLongValue(ACT_LOCTIME));
        } else {
            for (FileIdMesg record : fileIdRecords) {
                // Modify the timestamp
                if (record.getTimeCreated() != null) {
                    record.setTimeCreated(new DateTime(record.getTimeCreated().getTimestamp() + changeSeconds)); // Add 3 minutes
                }
            }
            for (ActivityMesg record : activityRecords) {
                // Modify the timestamp
                if (record.getTimestamp() != null) {
                    record.setTimestamp(new DateTime(record.getTimestamp().getTimestamp() + changeSeconds)); // Add 3 minutes
                }
                //Get local_timestamp field Comment: 
                //timestamp EPOCH expressed in local time, used to convert activity timestamps to local time
                if (record.getLocalTimestamp() != null) {
                    record.setLocalTimestamp(record.getLocalTimestamp() + changeSeconds); // Add 3 minutes
                }
            }
            for (DeviceInfoMesg record : deviceInfoRecords) {
                // Modify the timestamp
                if (record.getTimestamp() != null) {
                    record.setTimestamp(new DateTime(record.getTimestamp().getTimestamp() + changeSeconds)); // Add 3 minutes
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
            for (SplitMesg record : splitRecords) {
                // Modify the timestamp
                if (record.getStartTime() != null) {
                    DateTime timeStamp2change = record.getStartTime();
                    record.setStartTime(new DateTime(timeStamp2change.getTimestamp() + changeSeconds)); // Add 3 minutes
                }
                if (record.getEndTime() != null) {
                    DateTime timeStamp2change2 = record.getEndTime();
                    record.setEndTime(new DateTime(timeStamp2change2.getTimestamp() + changeSeconds)); // Add 3 minutes
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
            activityDateTimeLocal = new DateTime(activityRecords.get(0).getLocalTimestamp());
        }
    }
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void createFileSummary() {
        savedStrOrgFileInfo += "--------------------------------------------------" + System.lineSeparator();
        savedStrOrgFileInfo += " --> Manufacturer:" + manufacturer + ", " + product + "(" + productNo + ")" + ", SW: v" + swVer + System.lineSeparator();
        savedStrOrgFileInfo += " --> Sport:"+ sport + ", SubSport:" + subsport + ", SportProfile:" + sportProfile + ", WktName:" + wktName + System.lineSeparator();
        savedStrOrgFileInfo += " --> Org activity dateTime Local:" + FitDateTime.toString(activityDateTimeLocalOrg) + System.lineSeparator();
        savedStrOrgFileInfo += " --> New activity dateTime Local:" + FitDateTime.toString(activityDateTimeLocal) + System.lineSeparator();
        savedStrOrgFileInfo += " --> Org activity DateTime UTC:  " + FitDateTime.toString(activityDateTimeUTC) + System.lineSeparator();
        savedStrOrgFileInfo += " --> timeZone:                   " + FitDateTime.offsetToTimeZoneString(diffMinutesLocalUTC) + System.lineSeparator();
        savedStrOrgFileInfo += " --> Org start datetime UTC:     " + FitDateTime.toString(timeFirstRecordOrg) + System.lineSeparator();
        savedStrOrgFileInfo += " --> New start datetime UTC:     " + FitDateTime.toString(timeFirstRecord) + System.lineSeparator();
        
        savedStrOrgFileInfo += "--------------------------------------------------" + System.lineSeparator();
        //System.out.print(savedStrOrgFileInfo);
            /*for (Mesg mesg : allMesg) {
                switch (mesg.getNum()) {
                    case MesgNum.FILE_ID:
                        System.out.println(FitDateTime.toString(new DateTime(mesg.getFieldLongValue(FID_TIME))));
                        break;
                    case MesgNum.ACTIVITY:
                        System.out.println(FitDateTime.toString(new DateTime(mesg.getFieldLongValue(ACT_TIME))));
                        System.out.println(FitDateTime.toString(new DateTime(mesg.getFieldLongValue(ACT_LOCTIME))));
                        break;
                    case MesgNum.DEVICE_INFO:
                        System.out.println(FitDateTime.toString(new DateTime(mesg.getFieldLongValue(DINFO_TIME))));
                        break;
                    case MesgNum.EVENT:
                        System.out.println(FitDateTime.toString(new DateTime(mesg.getFieldLongValue(EVE_TIME))));
                        //System.out.println(FitDateTime.toString(new DateTime(mesg.getFieldLongValue(EVE_STIME)));
                        break;
                    case MesgNum.SESSION:
                        System.out.println(FitDateTime.toString(new DateTime(mesg.getFieldLongValue(SES_TIME))));
                        break;
                    case MesgNum.LAP:
                        System.out.println(FitDateTime.toString(new DateTime(mesg.getFieldLongValue(LAP_TIME))));
                        System.out.println(FitDateTime.toString(new DateTime(mesg.getFieldLongValue(LAP_STIME))));
                        break;
                    case MesgNum.SPLIT:
                        System.out.println(FitDateTime.toString(new DateTime(mesg.getFieldLongValue(SPL_STIME))));
                        System.out.println(FitDateTime.toString(new DateTime(mesg.getFieldLongValue(SPL_ETIME))));
                        break;
                    case MesgNum.RECORD:
                        //System.out.println(FitDateTime.toString(new DateTime(mesg.getFieldLongValue(REC_TIME))));
                        break;
                }
            }*/

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
    public void printCourse() {
        System.out.println("--------------------------------------------------");
        System.out.println("Course Messages");
        for (CourseMesg record : courseRecords){
            System.out.print(" Capabilities:" + record.getCapabilities());
            System.out.print(" Name:" + record.getName());
            System.out.print(" Sport:" + record.getSport());
            System.out.print(" SubSPort:" + record.getSubSport());
            System.out.println();
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
                    System.out.print(" StartTime: " + FitDateTime.toString(record.getStartTime(),diffMinutesLocalUTC));
                }
                if (record.getTimestamp() != null) {
                    System.out.print(" Timestamp: " + FitDateTime.toString(record.getTimestamp(),diffMinutesLocalUTC));
                }
                if (record.getTotalTimerTime() != null) {
                    System.out.print(" LapTime: " + record.getTotalTimerTime());
                }
                if (record.getTotalDistance() != null) {
                    System.out.print(" LapDist: " + record.getTotalDistance());
                }
                if (record.getAvgSpeed() != null) {
                    System.out.print(" LapSpeed: " + record.getAvgSpeed());
                }
                if (record.getEnhancedAvgSpeed() != null) {
                    System.out.print(" LapEnhSpeed: " + record.getEnhancedAvgSpeed());
                }
                if (record.getAvgCadence() != null) {
                    System.out.print(" LapCad: " + record.getAvgCadence());
                }
                if (record.getAvgRunningCadence() != null) {
                    System.out.print(" LapRunningCad: " + record.getAvgRunningCadence());
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
                if (record.getStartTime() != null) {
                    System.out.print(" StartTime: " + FitDateTime.toString(record.getStartTime(),diffMinutesLocalUTC));
                }
                if (record.getTimestamp() != null) {
                    System.out.print(" Timestamp: " + FitDateTime.toString(record.getTimestamp(),diffMinutesLocalUTC));
                }
                if (record.getTotalTimerTime() != null) {
                    System.out.print(" LapTime: " + record.getTotalTimerTime());
                }
                if (record.getTotalDistance() != null) {
                    System.out.print(" LapDist: " + record.getTotalDistance());
                }
                System.out.print(" DistFrom: " + secRecords.get(lapExtraRecords.get(i).recordIxStart).getDistance());
                System.out.print(" DistTo: " + secRecords.get(lapExtraRecords.get(i).recordIxEnd).getDistance());
                if (record.getEnhancedAvgSpeed() != null) {
                    System.out.print(" LapPace: " + record.getEnhancedAvgSpeed());
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
                System.out.print("--Sp avg:" + PehoUtils.mps2minp500m(avgSpeed));
                System.out.print(" max:" + PehoUtils.mps2minp500m(maxSpeed));
            } else {
                System.out.print("--Sp avg:" + PehoUtils.mps2minpkm(avgSpeed));
                System.out.print(" max:" + PehoUtils.mps2minpkm(maxSpeed));
            }
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printLapAvgSpeed (Float avgSpeed) {
        if (avgSpeed != null) {
            if (isSkiErgFile()) {
                System.out.print(" " + PehoUtils.mps2minp500m(avgSpeed) + "min/500m");
            } else {
                System.out.print(" " + PehoUtils.mps2minpkm(avgSpeed) + "min/km");
                System.out.print(" " + PehoUtils.mps2kmph1(avgSpeed) + "km/h");
            }
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String lapAvgSpeed (Float avgSpeed) {
        String tempString = "";
        if (avgSpeed != null) {
            if (isSkiErgFile()) {
                tempString += " " + PehoUtils.mps2minp500m(avgSpeed) + "min/500m";
            } else {
                tempString += " " + PehoUtils.mps2minpkm(avgSpeed) + "min/km";
                tempString += " " + PehoUtils.mps2kmph1(avgSpeed) + "km/h";
            }
        }
        return tempString;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String lapEndSum2String (Float avgCad, Float avgSpeed, Float avgPower, Float dist) {
        String tempString = "";
        tempString += "avgCad: " + (int) Math.round(avgCad) + "spm";
        if (isSkiErgFile()) {
            tempString += ", avgPace: " + PehoUtils.mps2minp500m(avgSpeed) + "min/500m";
            tempString += ", avgPow: " + (int) Math.round(avgPower) + "W";
        } else {
            tempString += ", avgPace: " + PehoUtils.mps2minpkm(avgSpeed) + "min/km";
            tempString += String.format(", avgSp: %.1fkm/h", avgSpeed * 3.60);
        }
        tempString += String.format(", sumDist: %.1fkm", + dist / 1000);
        tempString += System.lineSeparator();
        return tempString;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printLapAllSummery () {
        int i = 0;
        int lapNo = 1;
        for (LapMesg record : lapRecords) {
            System.out.print("Lap:" + lapNo);
            if (record.getStartTime() != null) {
                System.out.print(" StartTime:" + FitDateTime.toString(record.getStartTime(),diffMinutesLocalUTC));
            }
            /*if (record.getTimestamp() != null) {
                System.out.print(" Timestamp: " + record.getTimestamp());
            }*/
            if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                System.out.print(" lv" + lapExtraRecords.get(i).level.intValue());
            }
            if (lapExtraRecords.get(i).stepLen != null && !isSkiErgFile()) {
                System.out.print(" steplen" + (int)(lapExtraRecords.get(i).stepLen*100)+"cm");
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
            printLapAvgMaxSpeed(record.getEnhancedAvgSpeed(), record.getEnhancedMaxSpeed());
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
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printLapLongSummery () {
        System.out.println("---- ACTIVE LAPS ----");
        int i = 0;
        int lapNo = 1;
        for (LapMesg record : lapRecords) {
            if (Intensity.getStringFromValue(record.getIntensity()).equals("ACTIVE")) {
                System.out.print("Lap:" + lapNo);
                if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                    System.out.print(" lv" + lapExtraRecords.get(i).level.intValue());
                }
                if (record.getTotalTimerTime() != null) {
                    System.out.print(" LapTime: " + PehoUtils.sec2minSecShort(record.getTotalTimerTime()));
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
                printLapAvgMaxSpeed(record.getEnhancedAvgSpeed(), record.getMaxSpeed());
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
                    System.out.print(" LapTime: " + PehoUtils.sec2minSecShort(record.getTotalTimerTime()));
                }
                System.out.print(" HR start:" + lapExtraRecords.get(i).hrStart);
                System.out.print(" max:" + lapRecords.get(i).getMaxHeartRate());
                System.out.print("" + (lapExtraRecords.get(i).hrMin-lapRecords.get(i).getMaxHeartRate()));
                System.out.print("-->min:" + lapExtraRecords.get(i).hrMin);
                System.out.print(" end:" + lapExtraRecords.get(i).hrEnd);
                if (record.getTotalDistance() != null) {
                    System.out.print("--Dist:" + record.getTotalDistance());
                }
                printLapAvgMaxSpeed(record.getEnhancedAvgSpeed(), record.getMaxSpeed());
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
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printWriteLapSummery (String filename) {
        try {
            savedStrLapsActiveInfoShort += "---- ACTIVE LAPS ----" + System.lineSeparator();
            int i = 0;
            int lapNo = 1;
            for (LapMesg record : lapRecords) {
                if (Intensity.getStringFromValue(record.getIntensity()).equals("ACTIVE")) {
                    savedStrLapsActiveInfoShort += "Lap" + lapNo;
                    if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                        if (isTreadmillFile()) {
                        	 savedStrLapsActiveInfoShort += " " +  lapExtraRecords.get(i).level.intValue() + "%";
                        } else {
                        	savedStrLapsActiveInfoShort += " lv" + lapExtraRecords.get(i).level.intValue();
                        }
                    }
                    if (i > 0) {
                        savedStrLapsActiveInfoShort += " HRmin" + lapExtraRecords.get(i-1).hrMin;
                    } else {
                        savedStrLapsActiveInfoShort += " HR";
                    }
                    savedStrLapsActiveInfoShort += ">st" + lapExtraRecords.get(i).hrStart;
                    savedStrLapsActiveInfoShort += "+" + (lapRecords.get(i).getMaxHeartRate()-lapExtraRecords.get(i).hrMin);
                    savedStrLapsActiveInfoShort += "->max" + lapRecords.get(i).getMaxHeartRate();
                    savedStrLapsActiveInfoShort += " end" + lapExtraRecords.get(i).hrEnd;
                    if (record.getTotalTimerTime() != null) {
                        savedStrLapsActiveInfoShort += " " + PehoUtils.sec2minSecShort(record.getTotalTimerTime()) + "min";
                    }
                    if (record.getAvgCadence() != null) {
                        savedStrLapsActiveInfoShort += " " + record.getAvgCadence() + "spm";
                    }
                    if (isSkiErgFile()) {
                    	savedStrLapsActiveInfoShort += " " + PehoUtils.sec2minSecLong(500 / record.getEnhancedAvgSpeed()) + "min/500m";
                    } else {
                    	savedStrLapsActiveInfoShort += " " + PehoUtils.sec2minSecLong(1000 / record.getEnhancedAvgSpeed()) + "min/km";
                    	savedStrLapsActiveInfoShort += " " + String.format("%.1fkm/h", record.getEnhancedAvgSpeed() * 3.60);
                    }
                    if (record.getAvgPower() != null) {
                        savedStrLapsActiveInfoShort += " " + record.getAvgPower() + "W";
                    }
                    if (record.getTotalDistance() != null) {
                        savedStrLapsActiveInfoShort += " " + String.format("%.1fkm",record.getTotalDistance() / 1000);
                    }
                    if (lapExtraRecords.get(i).avgDragFactor != null && isSkiErgFile()) {
                        savedStrLapsActiveInfoShort += " df" + (int) Math.round(lapExtraRecords.get(i).avgDragFactor);
                    }
                    if (lapExtraRecords.get(i).avgStrokeLen != null && isSkiErgFile()) {
                        savedStrLapsActiveInfoShort += " sl" + lapExtraRecords.get(i).avgStrokeLen;
                    }
                    if (lapExtraRecords.get(i).stepLen != null && !isSkiErgFile()) {
                        savedStrLapsActiveInfoShort += " step" + (int)(lapExtraRecords.get(i).stepLen*100) +"cm";
                    }
                    savedStrLapsActiveInfoShort += System.lineSeparator();
                }
                i++;
                lapNo++;
            }
            savedStrLapsActiveInfoShort += lapEndSum2String(activeAvgCad, activeAvgSpeed, activeAvgPower, activeDist);
            
            savedStrLapsRestInfoShort += "---- REST LAPS ----" + System.lineSeparator();
            i=0;
            lapNo=1;
            for (LapMesg record : lapRecords) {
                if (Intensity.getStringFromValue(record.getIntensity()).equals("REST") || Intensity.getStringFromValue(record.getIntensity()).equals("RECOVERY")) {
                    savedStrLapsRestInfoShort += "Lap" + lapNo;
                    if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                        if (isTreadmillFile()) {
                        	 savedStrLapsRestInfoShort += " " +  lapExtraRecords.get(i).level.intValue() + "%";
                        } else {
                        	savedStrLapsRestInfoShort += " lv" + lapExtraRecords.get(i).level.intValue();
                        }
                    }
                    savedStrLapsRestInfoShort += " HRst" + lapExtraRecords.get(i).hrStart;
                    savedStrLapsRestInfoShort += ">max" + lapRecords.get(i).getMaxHeartRate();
                    savedStrLapsRestInfoShort += "" + (lapExtraRecords.get(i).hrMin-lapRecords.get(i).getMaxHeartRate());
                    savedStrLapsRestInfoShort += "->min" + lapExtraRecords.get(i).hrMin;
                    savedStrLapsRestInfoShort += " end" + lapExtraRecords.get(i).hrEnd;
                    if (record.getTotalTimerTime() != null) {
                        savedStrLapsRestInfoShort += " " + PehoUtils.sec2minSecShort(record.getTotalTimerTime()) + "min";
                    }
                    if (record.getAvgCadence() != null) {
                        savedStrLapsRestInfoShort += " " + record.getAvgCadence() + "spm";
                    }
                    if (isSkiErgFile()) {
                    	savedStrLapsRestInfoShort += " " + PehoUtils.sec2minSecLong(500 / record.getEnhancedAvgSpeed()) + "min/500m";
                    } else {
                    	savedStrLapsRestInfoShort += " " + PehoUtils.sec2minSecLong(1000 / record.getEnhancedAvgSpeed()) + "min/km";
                    	savedStrLapsRestInfoShort += " " + String.format("%.1fkm/h", record.getEnhancedAvgSpeed() * 3.60);
                    }
                    if (record.getAvgPower() != null) {
                        savedStrLapsRestInfoShort += " " + record.getAvgPower() + "W";
                    }
                    if (record.getTotalDistance() != null) {
                        savedStrLapsRestInfoShort += " " + String.format("%.1fkm",record.getTotalDistance() / 1000);
                    }
                    if (lapExtraRecords.get(i).stepLen != null && !isSkiErgFile()) {
                        savedStrLapsRestInfoShort += " step" + (int)(lapExtraRecords.get(i).stepLen*100)+"cm";
                    }
                    savedStrLapsRestInfoShort += System.lineSeparator();
                }
                i++;
                lapNo++;
            }
            savedStrLapsRestInfoShort += lapEndSum2String(restAvgCad, restAvgSpeed, restAvgPower, restDist);
        }
        catch (FitRuntimeException e) {
            System.out.println("LAP ERROR!!!!");
        }

        System.out.print(savedStrOrgFileInfo);
        System.out.print(savedStrLapsActiveInfoShort);
        System.out.print(savedStrLapsRestInfoShort);
        try {
            FileWriter myWriter = new FileWriter(filename);
            myWriter.write(savedStrOrgFileInfo);
            myWriter.write(savedStrLapsActiveInfoShort);
            myWriter.write(savedStrLapsRestInfoShort);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printRecordMesg (int ix1, int ix2) {
        for (i=ix1; i<=ix2; i++) {
            System.out.print(FitDateTime.toString(new DateTime(recordMesg.get(i).getFieldLongValue(REC_TIME))));
            System.out.print(((recordMesg.get(i).getFieldLongValue(REC_LAT))));
            System.out.print(((recordMesg.get(i).getFieldLongValue(REC_LON))));
            System.out.println();
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