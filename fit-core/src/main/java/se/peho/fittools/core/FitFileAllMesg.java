package se.peho.fittools.core;
import com.garmin.fit.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Scanner;

//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
public class FitFileAllMesg {

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
    public static final int LAP_AVG_CADENCE   = LapMesg.AvgCadenceFieldNum;           // short
    public static final int LAP_INTENSITY     = LapMesg.IntensityFieldNum;            // enum (short) -> Intensity.getByValue()
    public static final int LAP_WKT_STEP_IDX  = LapMesg.WktStepIndexFieldNum;         // integer
    public static final int LAP_MAX_SPEED     = LapMesg.MaxSpeedFieldNum;             // float
    public static final int LAP_MAX_CADENCE   = LapMesg.MaxCadenceFieldNum;           // short    public static final int REC_TIME = RecordMesg.TimestampFieldNum; //long
    public static final int LAP_MAX_HR        = LapMesg.MaxHeartRateFieldNum;       
    public static final int LAP_ENH_MAX_SPEED = LapMesg.EnhancedMaxSpeedFieldNum;   
    public static final int LAP_AVG_POW       = LapMesg.AvgPowerFieldNum;           
    public static final int LAP_MAX_POW       = LapMesg.MaxPowerFieldNum;           
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
    public DateTime activityDateTimeUTC;  // Original file
    public DateTime activityDateTimeLocal; // Original file
    public DateTime activityDateTimeLocalOrg; // Original file
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
    //int maxCadence;
    int avgPower;
    //int maxPower;

    int numberOfLaps;
    DateTime timeFirstRecord;
    DateTime timeFirstRecordOrg;   // Original file
    DateTime timeLastRecord;
    int numberOfRecords;

    public String savedStrOrgFileInfo = "";
    //String savedStrLapsAllInfo = "";
    String savedStrLapsActiveInfoShort = "";
    String savedStrLapsRestInfoShort = "";
    //String savedStrLapsActiveInfoLong = "";
    //String savedStrLapsRestInfoLong = "";


    int numberOfDevFields;
    String devAppToRemove = "9a0508b9-0256-4639-88b3-a2690a14ddf9";
    //List <Integer> devFieldsToRemove = Arrays.asList("Strokes", "Calories", "Distance", "Speed", "Power", 2, 6, 7);
    List <Integer> devFieldsToRemove = Arrays.asList(10, 11, 12, 23, 1, 2, 6, 7);
    List <String> devFieldNamesToUpdate = Arrays.asList("Training_session", "MaxHRevenLaps");

    int i;
    FileInputStream in;
    Decode decode;
    MesgBroadcaster broadcaster;

    public Boolean allMesgFlag = false;
    List<Mesg> allMesg = new ArrayList<>();
    List<Mesg> fileIdMesg = new ArrayList<>();
    List<Mesg> deviceInfoMesg = new ArrayList<>();
    List<Mesg> wktSessionMesg = new ArrayList<>();
    List<Mesg> wktStepMesg = new ArrayList<>();
    List<Mesg> wktRecordMesg = new ArrayList<>();
    List<Mesg> activityMesg = new ArrayList<>();
    List<Mesg> sessionMesg = new ArrayList<>();
    List<Mesg> splitMesg = new ArrayList<>();
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
    public List<GapMesg> gapRecords = new ArrayList<>(); //Not Garmin SDK
    public List<PauseMesg> pauseRecords = new ArrayList<>(); //Not Garmin SDK

    SimpleDateFormat sweDateTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

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
    public FitFileAllMesg (int syncSecC2File, int syncSecLapDistCalc) {
    	this.c2SyncSecondsC2File = syncSecC2File;
    	this.c2SyncSecondsLapDistCalc = syncSecLapDistCalc;
    }
    public FitFileAllMesg () {
    	
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public class GapMesg {
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
        // Getters and Setters
        public int getNo() { return no; }
        public void setNo(int no) { this.no = no; }

        public Long getTimeStart() { return timeStart; }
        public void setTimeStart(Long timeStart) { this.timeStart = timeStart; }

        public Long getTimeStop() { return timeStop; }
        public void setTimeStop(Long timeStop) { this.timeStop = timeStop; }

        public Long getTimeGap() { return timeGap; }
        public void setTimeGap(Long timeGap) { this.timeGap = timeGap; }

        public int getIxStart() { return ixStart; }
        public void setIxStart(int ixStart) { this.ixStart = ixStart; }

        public int getIxStop() { return ixStop; }
        public void setIxStop(int ixStop) { this.ixStop = ixStop; }

        public int getIxLap() { return ixLap; }
        public void setIxLap(int ixLap) { this.ixLap = ixLap; }

        public Float getDistStart() { return distStart; }
        public void setDistStart(Float distStart) { this.distStart = distStart; }

        public Float getDistStop() { return distStop; }
        public void setDistStop(Float distStop) { this.distStop = distStop; }

        public int getLatStart() { return latStart; }
        public void setLatStart(int latStart) { this.latStart = latStart; }

        public int getLonStart() { return lonStart; }
        public void setLonStart(int lonStart) { this.lonStart = lonStart; }

        public int getLatStop() { return latStop; }
        public void setLatStop(int latStop) { this.latStop = latStop; }

        public int getLonStop() { return lonStop; }
        public void setLonStop(int lonStop) { this.lonStop = lonStop; }

        public Float getDistGap() { return distGap; }
        public void setDistGap(Float distGap) { this.distGap = distGap; }

        public Float getDistGapGps() { return distGapGps; }
        public void setDistGapGps(Float distGapGps) { this.distGapGps = distGapGps; }

        public Float getAltStart() { return altStart; }
        public void setAltStart(Float altStart) { this.altStart = altStart; }

        public Float getAltStop() { return altStop; }
        public void setAltStop(Float altStop) { this.altStop = altStop; }

        public Float getAltGap() { return altGap; }
        public void setAltGap(Float altGap) { this.altGap = altGap; }


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
    public class PauseMesg {
        int no;
        Long timeStart;
        Long timeStop;
        public Long timePause; //seconds
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
        public Float distPause;
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
    public String getFilenameAndSetNewSportProfileName(String suffix, String outputFilenameBase) {
        
        String newProfileName = "";

        if (suffix.equals("")){
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
                }
            );

            // Decode the FIT file

            try {
                in.close();
            } catch (Exception e) {
                System.out.println("============== Closing Exception: " + e);
            }

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

            for (Mesg record : allMesg) {
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
    public void changeStartTime (int changeSeconds) {
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
                    if (timeToChange != null) {
                        mesg.setFieldValue(REC_TIME, timeToChange + changeSeconds);
                    }
                    break;
            }
        }
        timeFirstRecord = new DateTime (recordMesg.get(0).getFieldLongValue(REC_TIME));
        timeLastRecord = new DateTime (recordMesg.get(recordMesg.size() - 1).getFieldLongValue(REC_TIME));
        activityDateTimeLocal = new DateTime(activityMesg.get(0).getFieldLongValue(ACT_LOCTIME));
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
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printFileIdInfo() {
        int i = 0;
        System.out.println("====FileIdInfoMesg----------------------------------------------");
        for (Mesg mesg : fileIdMesg) {
            i++;
            System.out.println("File ID: " + i);

            Long timeCreated = mesg.getFieldLongValue(FID_TIME);
            if (timeCreated != null) {
                System.out.print(" Time: " + FitDateTime.toString(new DateTime(timeCreated)));
            }

            Integer manuVal = mesg.getFieldIntegerValue(FID_MANU);
            String manuStr = "";
            if (manuVal != null) {
                manuStr = Manufacturer.getStringFromValue(manuVal);
                System.out.print(" Manufacturer: " + manuStr + "(" + manuVal + ")");
            }

            Integer prodVal = mesg.getFieldIntegerValue(FID_PROD);
            if (prodVal != null) {
                System.out.print(" Product: ");
                if ("GARMIN".equals(manuStr)) {
                    System.out.print(GarminProduct.getStringFromValue(prodVal) + "(" + prodVal + ")");
                } else {
                    System.out.print(prodVal);
                }
            }

            String prodName = mesg.getFieldStringValue(FID_PRODNAME);
            if (prodName != null) {
                System.out.print(" ProductName: " + prodName);
            }

            Long serialNum = mesg.getFieldLongValue(FileIdMesg.SerialNumberFieldNum);
            if (serialNum != null) {
                System.out.print(" Serial Number: " + serialNum);
            }

            Integer number = mesg.getFieldIntegerValue(FileIdMesg.NumberFieldNum);
            if (number != null) {
                System.out.print(" Number: " + number);
            }

            Short type = mesg.getFieldShortValue(FileIdMesg.TypeFieldNum);
            if (type != null) {
                System.out.print(" Type: " + type);
            }

            System.out.println();
            if (i == 11) {
                break;
            }
        }
        System.out.println("--------------------------------------------------");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printDeviceInfo() {
        int i = 0;
        String manu = "";
        System.out.println("====DeviceInfoMesg----------------------------------------------");
        for (Mesg mesg : deviceInfoMesg) {
            i++;
            System.out.print("Device ID: " + i);

            Integer deviceType = mesg.getFieldIntegerValue(DeviceInfoMesg.DeviceTypeFieldNum);
            if (deviceType != null) {
                System.out.print(" -- DeviceType: (" + deviceType + ")");
            }

            Float sw = mesg.getFieldFloatValue(DINFO_SWVER);
            if (sw != null) {
                System.out.print(" SW: v" + sw);
            }

            Integer manuVal = mesg.getFieldIntegerValue(DeviceInfoMesg.ManufacturerFieldNum);
            if (manuVal != null) {
                manu = Manufacturer.getStringFromValue(manuVal);
                System.out.print(" Manufacturer: " + manu + "(" + manuVal + ")");
            }

            Integer prodVal = mesg.getFieldIntegerValue(DeviceInfoMesg.ProductFieldNum);
            if (prodVal != null) {
                System.out.print(" Product: ");
                if ("GARMIN".equals(manu)) {
                    System.out.print(GarminProduct.getStringFromValue(prodVal) + "(" + prodVal + ")");
                } else {
                    System.out.print(prodVal);
                }
            }

            String prodName = mesg.getFieldStringValue(DeviceInfoMesg.ProductNameFieldNum);
            if (prodName != null) {
                System.out.print(" ProductName: " + prodName);
            }

            Float hw = mesg.getFieldFloatValue(DeviceInfoMesg.HardwareVersionFieldNum);
            if (hw != null) {
                System.out.print(" HW: v" + hw);
            }

            System.out.println();
        }
        System.out.println("--------------------------------------------------");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printWktInfo() {
        int i = 0;
        System.out.println("====WktInfoMesg----------------------------------------------");
        for (Mesg mesg : wktRecordMesg) {
            i++;
            System.out.print("Workout: " + i);

            String wktName = mesg.getFieldStringValue(WKT_NAME);
            if (wktName != null) {
                System.out.print(" WktName:");
                System.out.print(wktName);
            }

            Integer numSteps = mesg.getFieldIntegerValue(WorkoutMesg.NumValidStepsFieldNum);
            if (numSteps != null) {
                System.out.print(" NoOfSteps:");
                System.out.print(numSteps);
            }

            Short sportVal = mesg.getFieldShortValue(WorkoutMesg.SportFieldNum);
            if (sportVal != null) {
                System.out.print(" Sport:");
                System.out.print(Sport.getByValue(sportVal));
            }

            Short subSportVal = mesg.getFieldShortValue(WorkoutMesg.SubSportFieldNum);
            if (subSportVal != null) {
                System.out.print(" SubSport:");
                System.out.print(SubSport.getByValue(subSportVal));
            }

            System.out.println();
        }
        System.out.println("--------------------------------------------------");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printWktSessionInfo() {
        int i = 0;
        System.out.println("====WktSessionInfoMesg----------------------------------------------");
        for (Mesg mesg : wktSessionMesg) {
            i++;
            System.out.print("WorkoutSession: " + i);

            Integer numSteps = mesg.getFieldIntegerValue(WorkoutSessionMesg.NumValidStepsFieldNum);
            if (numSteps != null) {
                System.out.print(" NoOfSteps:");
                System.out.print(numSteps);
            }

            Short sportVal = mesg.getFieldShortValue(WorkoutSessionMesg.SportFieldNum);
            if (sportVal != null) {
                System.out.print(" Sport:");
                System.out.print(Sport.getByValue(sportVal));
            }

            Short subSportVal = mesg.getFieldShortValue(WorkoutSessionMesg.SubSportFieldNum);
            if (subSportVal != null) {
                System.out.print(" SubSport:");
                System.out.print(SubSport.getByValue(subSportVal));
            }

            System.out.println();
            if (i == 11) {
                break;
            }
        }
        System.out.println("--------------------------------------------------");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printWktStepInfo() {
        int i = 0;
        System.out.println("====WktStepInfoMesg----------------------------------------------");
        for (Mesg mesg : wktStepMesg) {
            i++;
            System.out.print("WorkoutStep: " + i);

            Integer msgIx = mesg.getFieldIntegerValue(WorkoutStepMesg.MessageIndexFieldNum);
            if (msgIx != null) {
                System.out.print(" StepIx:");
                System.out.print(msgIx);
            }

            Short durationType = mesg.getFieldShortValue(WorkoutStepMesg.DurationTypeFieldNum);
            if (durationType != null) {
                System.out.print(" Type:");
                System.out.print(durationType);
            }

            Integer durationTime = mesg.getFieldIntegerValue(WorkoutStepMesg.DurationValueFieldNum);
            if (durationTime != null) {
                System.out.print(" Tid:");
                System.out.print(durationTime + "sec");
            }

            System.out.println();
        }
        System.out.println("--------------------------------------------------");
    }
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printSessionInfo () {
        int i = 0;
        System.out.println("====SessionMesg----------------------------------------------");
        for (Mesg mesg : sessionMesg) {
            i++;
            System.out.println("Session: " + i);

            Short sportShort = mesg.getFieldShortValue(SES_SPORT);
            if (sportShort != null) {
                System.out.print(" Sport:");
                System.out.print(Sport.getByValue(sportShort));
            }
            Short subSportShort = mesg.getFieldShortValue(SES_SUBSPORT);
            if (subSportShort != null) {
                System.out.print(" SubSport:");
                System.out.print(SubSport.getByValue(subSportShort));
            }
            String profile = mesg.getFieldStringValue(SES_PROFILE);
            if (profile != null) {
                System.out.print(" SportProfile:");
                System.out.print(profile);
            }
            System.out.println();

            Long startTime = mesg.getFieldLongValue(SES_STIME);
            Long timestamp = mesg.getFieldLongValue(SES_TIME);
            if (startTime != null || timestamp != null) {
                if (startTime != null) {
                    System.out.print(" ActivityTime:");
                    System.out.print(FitDateTime.toString(new DateTime(startTime), diffMinutesLocalUTC));
                }
                if (timestamp != null) {
                    System.out.print(" - ");
                    System.out.print(FitDateTime.toString(new DateTime(timestamp), diffMinutesLocalUTC));
                }
            }

            Float totalTimer = mesg.getFieldFloatValue(SES_TIMER);
            if (totalTimer != null) {
                System.out.print(" = ");
                System.out.print(PehoUtils.sec2minSecShort(totalTimer));
            }

            System.out.println();
        }
        System.out.println("--------------------------------------------------");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printCourse() {
        System.out.println("====CourseMesg----------------------------------------------");
        for (Mesg mesg : allMesg) {
            if (mesg.getNum() == MesgNum.COURSE) {
                System.out.print(" Course:");
                Integer caps = mesg.getFieldIntegerValue(CourseMesg.CapabilitiesFieldNum);
                if (caps != null) System.out.print(" Capabilities:" + caps);
                String name = mesg.getFieldStringValue(CourseMesg.NameFieldNum);
                if (name != null) System.out.print(" Name:" + name);
                Short sport = mesg.getFieldShortValue(CourseMesg.SportFieldNum);
                if (sport != null) System.out.print(" Sport:" + Sport.getByValue(sport));
                Short subSport = mesg.getFieldShortValue(CourseMesg.SubSportFieldNum);
                if (subSport != null) System.out.print(" SubSPort:" + SubSport.getByValue(subSport));
                System.out.println();
            }
        }
        System.out.println("--------------------------------------------------");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printDevDataId() {
        System.out.println("====DevDataIdMesg----------------------------------------------");
        for (Mesg mesg : allMesg) {
            if (mesg.getNum() == MesgNum.DEVELOPER_DATA_ID) {
                System.out.print("Developer Data Id");
                Integer appId = mesg.getFieldIntegerValue(DeveloperDataIdMesg.ApplicationIdFieldNum);
                if (appId != null) System.out.print(" AppId:" + appId);

                Integer appVer = mesg.getFieldIntegerValue(DeveloperDataIdMesg.ApplicationVersionFieldNum);
                if (appVer != null) System.out.print(" AppVer:" + appVer);

                Short devId = mesg.getFieldShortValue(DeveloperDataIdMesg.DeveloperIdFieldNum);
                if (devId != null) System.out.print(" DevId:" + devId);

                Short devIx = mesg.getFieldShortValue(DeveloperDataIdMesg.DeveloperDataIndexFieldNum);
                if (devIx != null) System.out.print(" DevDataIx:" + devIx);
                System.out.println();
            }
        }
        System.out.println("--------------------------------------------------");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printFieldDescr() {
        System.out.println("====FieldDescrMesg----------------------------------------------");
        for (Mesg mesg : allMesg) {
            if (mesg.getNum() == MesgNum.FIELD_DESCRIPTION) {
                System.out.print("Field Description");
                Short devIx = mesg.getFieldShortValue(FieldDescriptionMesg.DeveloperDataIndexFieldNum);
                if (devIx != null) System.out.print(" DeveloperDataIndex:" + devIx);

                Short defNum = mesg.getFieldShortValue(FieldDescriptionMesg.FieldDefinitionNumberFieldNum);
                if (defNum != null) System.out.print(" FieldDefinitionNumber:" + defNum);

                Short baseType = mesg.getFieldShortValue(FieldDescriptionMesg.FitBaseTypeIdFieldNum);
                if (baseType != null) System.out.print(" FitBaseTypeId:" + baseType);

                String fname = mesg.getFieldStringValue(FieldDescriptionMesg.FieldNameFieldNum);
                if (fname != null) System.out.print(" FieldName:" + fname);

                String units = mesg.getFieldStringValue(FieldDescriptionMesg.UnitsFieldNum);
                if (units != null) System.out.print(" Units:" + units);

                Short nativeNum = mesg.getFieldShortValue(FieldDescriptionMesg.NativeFieldNumFieldNum);
                if (nativeNum != null) System.out.print(" NativeFieldNum:" + nativeNum);
                System.out.println();
            }
        }
        System.out.println("--------------------------------------------------");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printLapRecords0 () {
        int i = 0;
        int lapNo = 1;
        try {
            System.out.println("--------------------------------------------------");
            for (Mesg mesg : lapMesg) {
                System.out.print("Lap:" + lapNo);

                Long startTime = mesg.getFieldLongValue(LAP_STIME);
                if (startTime != null) System.out.print(" StartTime: " + FitDateTime.toString(new DateTime(startTime), diffMinutesLocalUTC));

                Long timestamp = mesg.getFieldLongValue(LAP_TIME);
                if (timestamp != null) System.out.print(" Timestamp: " + FitDateTime.toString(new DateTime(timestamp), diffMinutesLocalUTC));

                Float totalTimer = mesg.getFieldFloatValue(LAP_TIMER);
                if (totalTimer != null) System.out.print(" LapTime: " + totalTimer);

                Float totalDistance = mesg.getFieldFloatValue(LAP_DIST);
                if (totalDistance != null) System.out.print(" LapDist: " + totalDistance);

                Float avgSpeed = mesg.getFieldFloatValue(LAP_SPEED);
                if (avgSpeed != null) System.out.print(" LapSpeed: " + avgSpeed);

                Float enhAvgSpeed = mesg.getFieldFloatValue(LAP_ESPEED);
                if (enhAvgSpeed != null) System.out.print(" LapEnhSpeed: " + enhAvgSpeed);

                Short avgCadence = mesg.getFieldShortValue(LapMesg.AvgCadenceFieldNum);
                if (avgCadence != null) System.out.print(" LapCad: " + avgCadence);

                Short avgRunningCad = mesg.getFieldShortValue(LapMesg.AvgCadenceFieldNum, 0, Profile.SubFields.LAP_MESG_AVG_CADENCE_FIELD_AVG_RUNNING_CADENCE);
                if (avgRunningCad != null) System.out.print(" LapRunningCad: " + avgRunningCad);

                Short intensity = mesg.getFieldShortValue(LapMesg.IntensityFieldNum);
                if (intensity != null) System.out.print(" WktIntensity: " + Intensity.getStringFromValue(Intensity.getByValue(intensity.shortValue())));

                Integer wktStepIx = mesg.getFieldIntegerValue(LapMesg.WktStepIndexFieldNum);
                if (wktStepIx != null) System.out.print(" LapWktStepIx: " + wktStepIx);

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
    public void printLapRecords() {
        int i = 0;
        int lapNo = 1;

        try {
            System.out.println("--------------------------------------------------");

            for (Mesg mesg : lapMesg) {
                System.out.print("Lap:" + lapNo);

                // Level from extra records
                if (lapExtraRecords.get(i).level != null) {
                    System.out.print(" lv" + lapExtraRecords.get(i).level);
                }

                // Start Time
                Long startTime = mesg.getFieldLongValue(LAP_STIME);
                if (startTime != null) {
                    System.out.print(" StartTime: " + FitDateTime.toString(startTime, diffMinutesLocalUTC));
                }

                // Timestamp
                Long timestamp = mesg.getFieldLongValue(LAP_TIME);
                if (timestamp != null) {
                    System.out.print(" Timestamp: " + FitDateTime.toString(timestamp, diffMinutesLocalUTC));
                }

                // Timer
                Float totalTimer = mesg.getFieldFloatValue(LAP_TIMER);
                if (totalTimer != null) System.out.print(" LapTime: " + totalTimer);

                // Distance
                Float lapDist = mesg.getFieldFloatValue(LAP_DIST);
                if (lapDist != null) System.out.print(" LapDist: " + lapDist);

                // DistFrom / DistTo from secRecords
                //System.out.print(" DistFrom: " + secRecords.get(lapExtraRecords.get(i).recordIxStart).getDistance());
                //System.out.print(" DistTo: " + secRecords.get(lapExtraRecords.get(i).recordIxEnd).getDistance());

                // Enhanced average speed
                Float enhAvgSpeed = mesg.getFieldFloatValue(LAP_ESPEED);
                if (enhAvgSpeed != null) System.out.print(" LapPace: " + enhAvgSpeed);

                // Cadence
                Short avgCadence = mesg.getFieldShortValue(LAP_AVG_CADENCE);
                if (avgCadence != null) System.out.print(" LapCad: " + avgCadence);

                // Intensity
                Short intensityRaw = mesg.getFieldShortValue(LAP_INTENSITY);
                if (intensityRaw != null) {
                    Intensity intensityEnum = Intensity.getByValue(intensityRaw.shortValue());
                    String intensityLabel = intensityEnum != null ? Intensity.getStringFromValue(intensityEnum) : "unknown";
                    System.out.print(" WktIntensity: " + intensityLabel);
                }

                // Workout Step Index
                Integer wktStepIx = mesg.getFieldIntegerValue(LAP_WKT_STEP_IDX);
                if (wktStepIx != null) System.out.print(" LapWktStepIx: " + wktStepIx);

                /*/ Extra record fields
                if (lapExtraRecords.get(i).timeEnd != null) System.out.print(" TimeEnd: " + lapExtraRecords.get(i).timeEnd);
                if (lapExtraRecords.get(i).stepLen != null) System.out.print(" StepLen: " + lapExtraRecords.get(i).stepLen);
                if (lapExtraRecords.get(i).hrStart != 0) System.out.print(" hrStart: " + lapExtraRecords.get(i).hrStart);
                if (lapExtraRecords.get(i).hrEnd != 0) System.out.print(" hrEnd: " + lapExtraRecords.get(i).hrEnd);
                if (lapExtraRecords.get(i).recordIxEnd != 0) System.out.print(" recordIxEnd: " + lapExtraRecords.get(i).recordIxEnd);
                if (lapExtraRecords.get(i).hrMin != 0) System.out.print(" hrMin: " + lapExtraRecords.get(i).hrMin);
                */
                System.out.println();
                i++;
                lapNo++;
            }

            System.out.println("--------------------------------------------------");
        } catch (FitRuntimeException e) {
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
    public void printLapAllSummary() {
        int i = 0;
        int lapNo = 1;

        for (Mesg mesg : lapMesg) {
            System.out.print("Lap:" + lapNo);

            // Start time
            Long startTime = mesg.getFieldLongValue(LAP_STIME);
            if (startTime != null) {
                System.out.print(" StartTime:" + FitDateTime.toString(startTime, diffMinutesLocalUTC));
            }

            /*
            // Extra fields: level and step length (skip if SkiErg)
            if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                System.out.print(" lv" + lapExtraRecords.get(i).level.intValue());
            }
            if (lapExtraRecords.get(i).stepLen != null && !isSkiErgFile()) {
                System.out.print(" steplen" + (int) (lapExtraRecords.get(i).stepLen * 100) + "cm");
            }
            */

            // Total timer
            Float totalTimer = mesg.getFieldFloatValue(LAP_TIMER);
            if (totalTimer != null) System.out.print(" LapTime: " + totalTimer);

            // Intensity
            Short intensityRaw = mesg.getFieldShortValue(LAP_INTENSITY);
            String intensityLabel = null;
            if (intensityRaw != null) {
                Intensity intensityEnum = Intensity.getByValue(intensityRaw.shortValue());
                intensityLabel = intensityEnum != null ? Intensity.getStringFromValue(intensityEnum) : "unknown";
                System.out.print(" WktIntensity: " + intensityLabel);
            }

            /*
            // Heart rate logic
            Integer maxHr = mesg.getFieldIntegerValue(LAP_MAX_HR);
            if ("ACTIVE".equals(intensityLabel) || "WARMUP".equals(intensityLabel)) {
                System.out.print(" HR start:" + lapExtraRecords.get(i).hrStart);
                System.out.print(" min:" + lapExtraRecords.get(i).hrMin);
                System.out.print("+" + ((maxHr != null ? maxHr : 0) - lapExtraRecords.get(i).hrMin));
                System.out.print("-->max:" + (maxHr != null ? maxHr : "N/A"));
                System.out.print(" end:" + lapExtraRecords.get(i).hrEnd);
            } else {
                System.out.print(" HR start:" + lapExtraRecords.get(i).hrStart);
                System.out.print(" max:" + (maxHr != null ? maxHr : "N/A"));
                System.out.print("" + (lapExtraRecords.get(i).hrMin - (maxHr != null ? maxHr : 0)));
                System.out.print("-->min:" + lapExtraRecords.get(i).hrMin);
                System.out.print(" end:" + lapExtraRecords.get(i).hrEnd);
            }
            */

            // Distance
            Float totalDist = mesg.getFieldFloatValue(LAP_DIST);
            if (totalDist != null) System.out.print("--Dist:" + totalDist);

            // Speed
            Float enhAvgSpeed = mesg.getFieldFloatValue(LAP_ESPEED);
            Float enhMaxSpeed = mesg.getFieldFloatValue(LAP_ENH_MAX_SPEED);
            printLapAvgMaxSpeed(enhAvgSpeed, enhMaxSpeed);

            // Cadence
            Short avgCadence = mesg.getFieldShortValue(LAP_AVG_CADENCE);
            Short maxCadence = mesg.getFieldShortValue(LAP_MAX_CADENCE);
            if (avgCadence != null) {
                System.out.print("--Cad avg:" + avgCadence);
                System.out.print(" max:" + (maxCadence != null ? maxCadence : "N/A"));
            }

            // Power
            Integer avgPower = mesg.getFieldIntegerValue(LAP_AVG_POW);
            Integer maxPower = mesg.getFieldIntegerValue(LAP_MAX_POW);
            if (avgPower != null) {
                System.out.print("--Pow avg:" + avgPower);
                System.out.print(" max:" + (maxPower != null ? maxPower : "N/A"));
            }

            /*
            // Extra lap info: Drag Factor and Stroke Length
            if (lapExtraRecords.get(i).avgDragFactor != null) {
                System.out.print("--DFavg:" + (int) Math.round(lapExtraRecords.get(i).avgDragFactor));
                System.out.print(" max:" + (int) Math.round(lapExtraRecords.get(i).maxDragFactor));
            }
            if (lapExtraRecords.get(i).avgStrokeLen != null) {
                System.out.print("--SLavg:" + lapExtraRecords.get(i).avgStrokeLen);
                System.out.print(" max:" + lapExtraRecords.get(i).maxStrokeLen);
            }
            */

            System.out.println();
            i++;
            lapNo++;
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printLapLongSummary() {
        System.out.println("---- ACTIVE LAPS ----");
        int i = 0;
        int lapNo = 1;

        // ACTIVE laps
        for (Mesg mesg : lapMesg) {
            Short intensityRaw = mesg.getFieldShortValue(LAP_INTENSITY);
            String intensityLabel = null;
            if (intensityRaw != null) {
                Intensity intensityEnum = Intensity.getByValue(intensityRaw.shortValue());
                intensityLabel = intensityEnum != null ? Intensity.getStringFromValue(intensityEnum) : "unknown";
            }

            if ("ACTIVE".equals(intensityLabel)) {
                System.out.print("Lap:" + lapNo);

                /*
                if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                    System.out.print(" lv" + lapExtraRecords.get(i).level.intValue());
                }
                */

                Float totalTimer = mesg.getFieldFloatValue(LAP_TIMER);
                if (totalTimer != null) {
                    System.out.print(" LapTime: " + PehoUtils.sec2minSecShort(totalTimer));
                }

                /*
                System.out.print(" HR start:" + lapExtraRecords.get(i).hrStart);
                if (i > 0) {
                    System.out.print(" HRmin" + lapExtraRecords.get(i - 1).hrMin);
                } else {
                    System.out.print(" HR");
                }
                System.out.print(" min:" + lapExtraRecords.get(i).hrMin);
                System.out.print("+" + (mesg.getFieldIntegerValue(LAP_MAX_HR) - lapExtraRecords.get(i).hrMin));
                System.out.print("-->max:" + mesg.getFieldIntegerValue(LAP_MAX_HR));
                System.out.print(" end:" + lapExtraRecords.get(i).hrEnd);
                */

                Float totalDist = mesg.getFieldFloatValue(LAP_DIST);
                if (totalDist != null) System.out.print("--Dist:" + totalDist);

                Float enhAvgSpeed = mesg.getFieldFloatValue(LAP_ESPEED);
                Float enhMaxSpeed = mesg.getFieldFloatValue(LAP_ENH_MAX_SPEED);
                printLapAvgMaxSpeed(enhAvgSpeed, enhMaxSpeed);

                Short avgCadence = mesg.getFieldShortValue(LAP_AVG_CADENCE);
                Short maxCadence = mesg.getFieldShortValue(LAP_MAX_CADENCE);
                if (avgCadence != null) {
                    System.out.print("--Cad avg:" + avgCadence);
                    System.out.print(" max:" + (maxCadence != null ? maxCadence : "N/A"));
                }

                Integer avgPower = mesg.getFieldIntegerValue(LAP_AVG_POW);
                Integer maxPower = mesg.getFieldIntegerValue(LAP_MAX_POW);
                if (avgPower != null) {
                    System.out.print("--Pow avg:" + avgPower);
                    System.out.print(" max:" + (maxPower != null ? maxPower : "N/A"));
                }

                /*
                if (lapExtraRecords.get(i).avgDragFactor != null) {
                    System.out.print("--DFavg:" + (int) Math.round(lapExtraRecords.get(i).avgDragFactor));
                    System.out.print(" max:" + (int) Math.round(lapExtraRecords.get(i).maxDragFactor));
                }
                if (lapExtraRecords.get(i).avgStrokeLen != null) {
                    System.out.print("--SLavg:" + lapExtraRecords.get(i).avgStrokeLen);
                    System.out.print(" max:" + lapExtraRecords.get(i).maxStrokeLen);
                }
                */

                System.out.println();
            }
            i++;
            lapNo++;
        }

        // REST/RECOVERY laps
        System.out.println("---- REST LAPS ----");
        i = 0;
        lapNo = 1;

        for (Mesg mesg : lapMesg) {
            Short intensityRaw = mesg.getFieldShortValue(LAP_INTENSITY);
            String intensityLabel = null;
            if (intensityRaw != null) {
                Intensity intensityEnum = Intensity.getByValue(intensityRaw.shortValue());
                intensityLabel = intensityEnum != null ? Intensity.getStringFromValue(intensityEnum) : "unknown";
            }

            if ("REST".equals(intensityLabel) || "RECOVERY".equals(intensityLabel)) {
                System.out.print("Lap:" + lapNo);

                /*
                if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                    System.out.print(" lv" + lapExtraRecords.get(i).level.intValue());
                }
                */

                Float totalTimer = mesg.getFieldFloatValue(LAP_TIMER);
                if (totalTimer != null) {
                    System.out.print(" LapTime: " + PehoUtils.sec2minSecShort(totalTimer));
                }

                /*
                System.out.print(" HR start:" + lapExtraRecords.get(i).hrStart);
                System.out.print(" max:" + mesg.getFieldIntegerValue(LAP_MAX_HR));
                System.out.print("" + (lapExtraRecords.get(i).hrMin - mesg.getFieldIntegerValue(LAP_MAX_HR)));
                System.out.print("-->min:" + lapExtraRecords.get(i).hrMin);
                System.out.print(" end:" + lapExtraRecords.get(i).hrEnd);
                */

                Float totalDist = mesg.getFieldFloatValue(LAP_DIST);
                if (totalDist != null) System.out.print("--Dist:" + totalDist);

                Float enhAvgSpeed = mesg.getFieldFloatValue(LAP_ESPEED);
                Float enhMaxSpeed = mesg.getFieldFloatValue(LAP_ENH_MAX_SPEED);
                printLapAvgMaxSpeed(enhAvgSpeed, enhMaxSpeed);

                Short avgCadence = mesg.getFieldShortValue(LAP_AVG_CADENCE);
                Short maxCadence = mesg.getFieldShortValue(LAP_MAX_CADENCE);
                if (avgCadence != null) {
                    System.out.print("--Cad avg:" + avgCadence);
                    System.out.print(" max:" + (maxCadence != null ? maxCadence : "N/A"));
                }

                Integer avgPower = mesg.getFieldIntegerValue(LAP_AVG_POW);
                Integer maxPower = mesg.getFieldIntegerValue(LAP_MAX_POW);
                if (avgPower != null) {
                    System.out.print("--Pow avg:" + avgPower);
                    System.out.print(" max:" + (maxPower != null ? maxPower : "N/A"));
                }

                /*
                if (lapExtraRecords.get(i).avgDragFactor != null) {
                    System.out.print("--DFavg:" + (int) Math.round(lapExtraRecords.get(i).avgDragFactor));
                    System.out.print(" max:" + (int) Math.round(lapExtraRecords.get(i).maxDragFactor));
                }
                if (lapExtraRecords.get(i).avgStrokeLen != null) {
                    System.out.print("--SLavg:" + lapExtraRecords.get(i).avgStrokeLen);
                    System.out.print(" max:" + lapExtraRecords.get(i).maxStrokeLen);
                }
                */

                System.out.println();
            }
            i++;
            lapNo++;
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    /*
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
                //if (lapRecords.get(0).getStartTime() != null) {
                //    System.out.print(" LapStartTime: " + lapRecords.get(0).getStartTime());
                //}
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
                    //for (int j = 1; j < field.getNumValues(); j++) {
                    //    System.out.print(", " + field.getValue(j));
                    //}
                    //System.out.println();
                }
                // for (Field field : record.getFields()) {
                //     System.out.print(", " + field.getName() + ":" + field.getStringValue());
                //     //System.out.print(", " + field.getAppId());
                //     //System.out.print(", " + field.getAppUUID());
                //     //System.out.print(", " + field.getDeveloperDataIndex());
                //     System.out.print(", " + field.getNum());
                //     for (int j = 1; j < field.getNumValues(); j++) {
                //         System.out.print(", " + field.getValue(j));
                //     }
                //     System.out.println();
                // }
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
                // if (lapRecords.get(0).getStartTime() != null) {
                //     System.out.print(" LapStartTime: " + lapRecords.get(12).getStartTime());
                // }
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
                // if (secExtraRecords.get(i).lapNo != 0) {
                //     System.out.print(" LapNo: " + secExtraRecords.get(i).lapNo);
                // }
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
    */
}