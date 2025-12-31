package se.peho.fittools.core;
import com.garmin.fit.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import java.util.Comparator;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.Iterator;
import java.time.Duration;
import java.time.Instant;

import javax.annotation.processing.RoundEnvironment;

import jdk.jfr.Description;

//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
public class FitFileForIndoor extends FitFile {

    public static final int FID_CTIME = FileIdMesg.TimeCreatedFieldNum; //long
    public static final int FID_MANU = FileIdMesg.ManufacturerFieldNum; //int
    public static final int FID_PROD = FileIdMesg.ProductFieldNum; //int
    public static final int FID_PRODNAME = FileIdMesg.ProductNameFieldNum; //string
    public static final int DINFO_TIME = DeviceInfoMesg.TimestampFieldNum; //long
    public static final int DINFO_SWVER = DeviceInfoMesg.SoftwareVersionFieldNum; //float
    public static final int ACT_TIME = ActivityMesg.TimestampFieldNum; //long
    public static final int ACT_LOCTIME = ActivityMesg.LocalTimestampFieldNum; //long
    public static final int DEVID_APPID = DeveloperDataIdMesg.ApplicationIdFieldNum; //enum
    public static final int WKT_NAME = WorkoutMesg.WktNameFieldNum; //long
    public static final int WKT_SPORT = WorkoutMesg.SportFieldNum; //long
    public static final int WKT_SUBSPORT = WorkoutMesg.SubSportFieldNum; //long
    public static final int SES_TIME = SessionMesg.TimestampFieldNum; //long
    public static final int SES_STIME = SessionMesg.StartTimeFieldNum; //long
    public static final int SES_PROFILE = SessionMesg.SportProfileNameFieldNum; //string
    public static final int SES_SPORT = SessionMesg.SportFieldNum; //short -> .getByValue -> Sport
    public static final int SES_SUBSPORT = SessionMesg.SubSportFieldNum; //short -> .getByValue -> SubSport
    public static final int SES_TIMER = SessionMesg.TotalTimerTimeFieldNum; //float
    public static final int SES_ETIMER = SessionMesg.TotalElapsedTimeFieldNum; //float
    public static final int SES_MTIMER = SessionMesg.TotalMovingTimeFieldNum; //float
    public static final int SES_DIST = SessionMesg.TotalDistanceFieldNum; //float
    public static final int SES_SPEED = SessionMesg.AvgSpeedFieldNum; //float
    public static final int SES_MSPEED = SessionMesg.MaxSpeedFieldNum; //float
    public static final int SES_ESPEED = SessionMesg.EnhancedAvgSpeedFieldNum; //float
    public static final int SES_EMSPEED = SessionMesg.EnhancedMaxSpeedFieldNum; //float
    public static final int SES_CAD = SessionMesg.AvgCadenceFieldNum; //short
    public static final int SES_MCAD = SessionMesg.MaxCadenceFieldNum; //short
    public static final int SES_POW = SessionMesg.AvgPowerFieldNum; //int
    public static final int SES_MPOW = SessionMesg.MaxPowerFieldNum; //int
    public static final int EVE_TIME = EventMesg.TimestampFieldNum; //long
    public static final int EVE_STIME = EventMesg.StartTimestampFieldNum; //long
    public static final int EVE_EVENT = EventMesg.EventFieldNum; //long
    public static final int EVE_TYPE = EventMesg.EventTypeFieldNum; //long
    public static final int SPL_STIME = SplitMesg.StartTimeFieldNum; //long
    public static final int SPL_ETIME = SplitMesg.EndTimeFieldNum; //long
    public static final int SPL_TIMER = SplitMesg.TotalTimerTimeFieldNum; // float
    public static final int SPL_ETIMER = SplitMesg.TotalElapsedTimeFieldNum; // float
    public static final int SPL_MTIMER = SplitMesg.TotalMovingTimeFieldNum; // float
    public static final int SPL_MESSAGE_INDEX = SplitMesg.MessageIndexFieldNum; // int
    public static final int SPL_TYPE = SplitMesg.SplitTypeFieldNum; // enum
    public static final int SPL_DIST = SplitMesg.TotalDistanceFieldNum; // float
    public static final int SPL_SPEED = SplitMesg.AvgSpeedFieldNum; // float
    public static final int SPL_MSPEED = SplitMesg.MaxSpeedFieldNum; // float
    public static final int SPL_VSPEED = SplitMesg.AvgVertSpeedFieldNum; // float
    public static final int SPL_SELE = SplitMesg.StartElevationFieldNum; // int
    public static final int SPL_ASC = SplitMesg.TotalAscentFieldNum; // int
    public static final int SPL_DESC = SplitMesg.TotalDescentFieldNum; // int
    public static final int SPL_SLAT = SplitMesg.StartPositionLatFieldNum; // int (semicircles)
    public static final int SPL_SLON = SplitMesg.StartPositionLongFieldNum; // int (semicircles)
    public static final int SPL_ELAT = SplitMesg.EndPositionLatFieldNum; // int (semicircles)
    public static final int SPL_ELON = SplitMesg.EndPositionLongFieldNum; // int (semicircles)
    public static final int SPL_CAL = SplitMesg.TotalCaloriesFieldNum; // int
    public static final int SPLSUM_TIMER = SplitSummaryMesg.TotalTimerTimeFieldNum; // float
    public static final int SPLSUM_MTIMER = SplitSummaryMesg.TotalMovingTimeFieldNum; // float
    public static final int SPLSUM_DIST = SplitSummaryMesg.TotalDistanceFieldNum; // float
    public static final int SPLSUM_SPEED = SplitSummaryMesg.AvgSpeedFieldNum; // float
    public static final int SPLSUM_MSPEED = SplitSummaryMesg.MaxSpeedFieldNum; // float
    public static final int SPLSUM_VSPEED = SplitSummaryMesg.AvgVertSpeedFieldNum; // float
    public static final int SPLSUM_ASC = SplitSummaryMesg.TotalAscentFieldNum; // int
    public static final int SPLSUM_DESC = SplitSummaryMesg.TotalDescentFieldNum; // int
    public static final int SPLSUM_CAL = SplitSummaryMesg.TotalCaloriesFieldNum; // int
    public static final int SPLSUM_TYPE = SplitSummaryMesg.SplitTypeFieldNum; // enum


    public static final int LAP_TIME = LapMesg.TimestampFieldNum; //long
    public static final int LAP_STIME = LapMesg.StartTimeFieldNum; //long
    public static final int LAP_TIMER = LapMesg.TotalTimerTimeFieldNum; //float
    public static final int LAP_ETIMER = LapMesg.TotalElapsedTimeFieldNum; //float
    public static final int LAP_MTIMER = LapMesg.TotalMovingTimeFieldNum; //float
    public static final int LAP_DIST = LapMesg.TotalDistanceFieldNum; //float
    public static final int LAP_SPEED = LapMesg.AvgSpeedFieldNum; //float
    public static final int LAP_MSPEED = LapMesg.MaxSpeedFieldNum; // float
    public static final int LAP_ESPEED = LapMesg.EnhancedAvgSpeedFieldNum; //float
    public static final int LAP_EMSPEED = LapMesg.EnhancedMaxSpeedFieldNum; // float
    public static final int LAP_HR = LapMesg.AvgHeartRateFieldNum; // int
    public static final int LAP_MHR = LapMesg.MaxHeartRateFieldNum; // int
    public static final int LAP_CAD = LapMesg.AvgCadenceFieldNum; // short
    public static final int LAP_MCAD = LapMesg.MaxCadenceFieldNum; // short
    public static final int LAP_INTENSITY = LapMesg.IntensityFieldNum; // enum (short) -> Intensity.getByValue()
    public static final int LAP_WKT_STEP_IDX = LapMesg.WktStepIndexFieldNum; // integer
    public static final int LAP_POW = LapMesg.AvgPowerFieldNum; // int
    public static final int LAP_MPOW = LapMesg.MaxPowerFieldNum; // int
    public static final int LAP_ASC = LapMesg.TotalAscentFieldNum; // int
    public static final int LAP_DESC = LapMesg.TotalDescentFieldNum; // int
    public static final int LAP_ALT = LapMesg.AvgAltitudeFieldNum; // float
    public static final int LAP_MALT = LapMesg.MaxAltitudeFieldNum; // float
    public static final int LAP_MINALT = LapMesg.MinAltitudeFieldNum; // float
    public static final int LAP_SLAT = LapMesg.StartPositionLatFieldNum; // int (semicircles)
    public static final int LAP_SLON = LapMesg.StartPositionLongFieldNum; // int (semicircles)
    public static final int LAP_ELAT = LapMesg.EndPositionLatFieldNum; // int (semicircles)
    public static final int LAP_ELON = LapMesg.EndPositionLongFieldNum; // int (semicircles)
    public static final int LAP_TEMP = LapMesg.AvgTemperatureFieldNum; // byte
    public static final int LAP_MTEMP = LapMesg.MaxTemperatureFieldNum; // byte
    public static final int LAP_MINTEMP = LapMesg.MinTemperatureFieldNum; // byte
    public static final int LAP_SPORT = LapMesg.SportFieldNum; //short -> .getByValue -> Sport
    public static final int LAP_SUBSPORT = LapMesg.SubSportFieldNum; //short -> .getByValue -> SubSport
    public static final int REC_TIME = RecordMesg.TimestampFieldNum; //long
    public static final int REC_DIST = RecordMesg.DistanceFieldNum; //float
    public static final int REC_HR = RecordMesg.HeartRateFieldNum; //int
    public static final int REC_SPEED = RecordMesg.SpeedFieldNum; //float
    public static final int REC_ESPEED = RecordMesg.EnhancedSpeedFieldNum; //float
    public static final int REC_CAD = RecordMesg.CadenceFieldNum; //int
    public static final int REC_POW = RecordMesg.PowerFieldNum; //int
    public static final int REC_LAT = RecordMesg.PositionLatFieldNum; //int
    public static final int REC_LON = RecordMesg.PositionLongFieldNum; //int
    public static final int REC_EALT = RecordMesg.EnhancedAltitudeFieldNum; //float
    public static final int SP_SPORT = SportMesg.SportFieldNum; //short -> .getByValue -> Sport
    public static final int SP_SUBSPORT = SportMesg.SubSportFieldNum;
    public static final int SP_NAME = SportMesg.NameFieldNum; //string

    String manufacturer;
    int productNo;
    String product = "";
    Float swVer;
    public Long activityDateTimeUTC;  // Original file
    public Long activityDateTimeLocal; // Original file
    public Long activityDateTimeLocalOrg; // Original file
    Long diffMinutesLocalUTC;

    String wktName;
    Sport sport;
    SubSport subsport;
    String sportProfile;

    private Boolean isSkiErg = false;
    private Boolean isElliptical = false;
    private Boolean isTreadmill = false;
    public Boolean getIsSkiErg() { return isSkiErg; }
    public Boolean getIsElliptical() { return isElliptical; }
    public Boolean getIsTreadmill() { return isTreadmill; }
    public void setIsSkiErg(Boolean isSkiErg) { this.isSkiErg = isSkiErg; }
    public void setIsElliptical(Boolean isElliptical) { this.isElliptical = isElliptical; }
    public void setIsTreadmill(Boolean isTreadmill) { this.isTreadmill = isTreadmill; }

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
    Long timeFirstRecord;
    Long timeFirstRecordOrg;   // Original file
    Long timeLastRecord;
    int numberOfRecords;

    Float activeTime = 0f;
    Float restTime = 0f;
    Float activeDist = 0f;
    Float restDist = 0f;

    public String savedStrOrgFileInfo = "";
    //String savedStrLapsAllInfo = "";
    String savedStrLapsActiveInfoShort = "";
    String savedStrLapsRestInfoShort = "";
    //String savedStrLapsActiveInfoLong = "";
    //String savedStrLapsRestInfoLong = "";


    int numberOfDevFields;
    String devAppToModify = "9a0508b9-0256-4639-88b3-a2690a14ddf9";
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

    private List<Mesg> allMesg = new ArrayList<>();
    private List<Mesg> fileIdMesg = new ArrayList<>();
    private List<Mesg> deviceInfoMesg = new ArrayList<>();
    private List<Mesg> wktSessionMesg = new ArrayList<>();
    private List<Mesg> wktStepMesg = new ArrayList<>();
    private List<Mesg> wktRecordMesg = new ArrayList<>();
    private List<Mesg> activityMesg = new ArrayList<>();
    private List<Mesg> sessionMesg = new ArrayList<>();
    private List<Mesg> lapMesg = new ArrayList<>();
    private List<Mesg> splitMesg = new ArrayList<>();
    private List<Mesg> splitSummaryMesg = new ArrayList<>();
    private List<Mesg> eventMesg = new ArrayList<>();
    private List<Mesg> recordMesg = new ArrayList<>();
    private List<Mesg> devDataIdMesg = new ArrayList<>();
    private List<Mesg> developerDataIdMesg = new ArrayList<>();
    private List<Mesg> fieldDescrMesg = new ArrayList<>();
    private List<Mesg> sportMesg = new ArrayList<>();
    public List<Mesg> getAllMesg() { return allMesg; }
    public List<Mesg> getFileIdMesg() { return fileIdMesg; }
    public List<Mesg> getDeviceInfoMesg() { return deviceInfoMesg; }
    public List<Mesg> getWktSessionMesg() { return wktSessionMesg; }
    public List<Mesg> getWktStepMesg() { return wktStepMesg; }
    public List<Mesg> getWktRecordMesg() { return wktRecordMesg; }
    public List<Mesg> getActivityMesg() { return activityMesg; }
    public List<Mesg> getSessionMesg() { return sessionMesg; }
    public List<Mesg> getLapMesg() { return lapMesg; }
    public List<Mesg> getEventMesg() { return eventMesg; }
    public List<Mesg> getRecordMesg() { return recordMesg; }
    public List<Mesg> getDevDataIdMesg() { return devDataIdMesg; }
    public List<Mesg> getDeveloperDataIdMesg() { return developerDataIdMesg; }
    public List<Mesg> getFieldDescrMesg() { return fieldDescrMesg; }
    public List<Mesg> getSportMesg() { return sportMesg; }
    
    /* List<FileIdMesg> fileIdRecords = new ArrayList<>();
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
    List<PowerZoneMesg> powerZoneRecords = new ArrayList<>(); */

    List<LapExtraMesg> lapExtraRecords = new ArrayList<>(); //Not Garmin SDK
    List<RecordExtraMesg> secExtraRecords = new ArrayList<>(); //Not Garmin SDK

    SimpleDateFormat sweDateTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

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
    public FitFileForIndoor (int syncSecC2File, int syncSecLapDistCalc) {
    	this.c2SyncSecondsC2File = syncSecC2File;
    	this.c2SyncSecondsLapDistCalc = syncSecLapDistCalc;
    }
    public FitFileForIndoor () {
    	
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    class RecordExtraMesg {
        private int lapNo;
        private Long C2DateTime;

        public RecordExtraMesg(int lapNo, Long C2DateTime) {
            this.lapNo = lapNo;
            this.C2DateTime = C2DateTime;
        }

        public int getLapNo() { return lapNo; }
        public void setLapNo(int lapNo) { this.lapNo = lapNo; }
        public Long getC2DateTime() { return C2DateTime; }
        public void setC2DateTime(Long C2DateTime) { this.C2DateTime = C2DateTime; }

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    class LapExtraMesg {
        private int hrStart;
        private int hrEnd;
        private int hrMin;
        private Long timeEnd;
        private int lapNo;
        private int recordIxStart;
        private int recordIxEnd;
        private Float stepLen;
        private Float level;
        private Float avgStrokeLen;
        private Float maxStrokeLen;
        private Float avgDragFactor;
        private Float maxDragFactor;
        private Float speedLapSum;
        private Float cadLapSum;

        public LapExtraMesg(int hrStart, int hrEnd, int hrMin, Long timeEnd, int lapNo, int recordIxStart, 
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

        public int getHrStart() { return hrStart; }
        public void setHrStart(int hrStart) { this.hrStart = hrStart; }
        public int getHrEnd() { return hrEnd; }
        public void setHrEnd(int hrEnd) { this.hrEnd = hrEnd; }
        public int getHrMin() { return hrMin; }
        public void setHrMin(int hrMin) { this.hrMin = hrMin; }
        public Long getTimeEnd() { return timeEnd; }
        public void setTimeEnd(Long timeEnd) { this.timeEnd = timeEnd; }
        public int getLapNo() { return lapNo; }
        public void setLapNo(int lapNo) { this.lapNo = lapNo; }
        public int getRecordIxStart() { return recordIxStart; }
        public void setRecordIxStart(int recordIxStart) { this.recordIxStart = recordIxStart; }
        public int getRecordIxEnd() { return recordIxEnd; }
        public void setRecordIxEnd(int recordIxEnd) { this.recordIxEnd = recordIxEnd; }
        public Float getStepLen() { return stepLen; }
        public void setStepLen(Float stepLen) { this.stepLen = stepLen; }
        public Float getLevel() { return level; }
        public void setLevel(Float level) { this.level = level; }
        public Float getAvgStrokeLen() { return avgStrokeLen; }
        public void setAvgStrokeLen(Float avgStrokeLen) { this.avgStrokeLen = avgStrokeLen; }
        public Float getMaxStrokeLen() { return maxStrokeLen; }
        public void setMaxStrokeLen(Float maxStrokeLen) { this.maxStrokeLen = maxStrokeLen; }
        public Float getAvgDragFactor() { return avgDragFactor; }
        public void setAvgDragFactor(Float avgDragFactor) { this.avgDragFactor = avgDragFactor; }
        public Float getMaxDragFactor() { return maxDragFactor; }
        public void setMaxDragFactor(Float maxDragFactor) { this.maxDragFactor = maxDragFactor; }
        public Float getSpeedLapSum() { return speedLapSum; }
        public void setSpeedLapSum(Float speedLapSum) { this.speedLapSum = speedLapSum; }
        public Float getCadLapSum() { return cadLapSum; }
        public void setCadLapSum(Float cadLapSum) { this.cadLapSum = cadLapSum; }

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
    public void initLapExtraRecords() {

        int hrStart = 0;
        int hrEnd = 0;
        int hrMin = 9999;
        Long timeEnd = null;
        int lapNo = 0;
        int recordIxStart = 0;
        int recordIxEnd = 0;
        Float stepLen = null;
        Float level = 0f;
        Float avgStrokeLen = 0f;
        Float maxStrokeLen = 0f;
        Float avgDragFactor = 0f;
        Float maxDragFactor = 0f;

        System.out.println("----- INIT LapExtra Records for ALL MESG -----");
        for (Mesg record : lapMesg) {
            LapExtraMesg newLapExtra = new LapExtraMesg(hrStart, hrEnd, hrMin, timeEnd, recordIxStart, recordIxEnd, lapNo, stepLen, level, avgStrokeLen, maxStrokeLen, avgDragFactor, maxDragFactor);
            newLapExtra.setSpeedLapSum(0f);
            newLapExtra.setCadLapSum(0f);
            lapExtraRecords.add(newLapExtra);
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void wktAddSteps(String wktSteps, String wktName) {

        System.out.println("---------> WKT COMMAND MADE!");

        // Find The place to add WKT messages
        //----------------------
        int insertIx = 0;
        for (Mesg mesg : allMesg) {
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
            wktRecordMesg.get(0).setFieldValue(WKT_NAME, wktName);
        }

        // ADD wktSession Record if EMPTY
        //----------------------
        /* if (wktSessionMesg.isEmpty()) {
            Mesg wktSessionRecord = new WorkoutSessionMesg();
            wktSessionMesg.add(wktSessionRecord);
            allMesg.add(insertIx, wktSessionRecord);
            insertIx++;
            System.out.println("---------> NO wktSession, ADDING!");
        } */

        // ADD wktStepRecords if EMPTY
        //----------------------
        if (wktStepMesg.isEmpty()) {
            for (Mesg lap : lapMesg) {
                System.out.println("---------> Adding wktStep for lap!");
                Mesg wktStepRecord = new WorkoutStepMesg();
                wktStepMesg.add(wktStepRecord);
                allMesg.add(insertIx, wktStepRecord);
                insertIx++;
            }
            System.out.println("---------> NO wktSteps, ADDING!");
        }

        System.out.println("---------> StepIntensity changing to: " + wktSteps);

        int recordIx = 0;
        for (Mesg lap : lapMesg) {
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
                    if (recordIx+1 == lapMesg.size()) {
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
                    if ((recordIx+1) == lapMesg.size()) {
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
                    if ((recordIx+1) == lapMesg.size()) {
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
                    if ((recordIx+1) == lapMesg.size()) {
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
    public String getFilenameAndSetNewSportProfileName(String suffix, String outputFilenameBase) {
        
        String newProfileName = sportProfile;

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

        newProfileName = newProfileName.replace("Treadmill","Löpband");

        if (wktRecordMesg.isEmpty()) {
            System.out.println("========> NO wkt RECORDS");

            // Set sport profile name in the first session message
            Mesg session = sessionMesg.get(0);
            String profileText = newProfileName + " " + ((float) (Math.round(totalDistance / 100)) / 10) + "km " + suffix;
            session.setFieldValue(SES_PROFILE, profileText);

        } else {
            if (wktStepMesg.isEmpty()) {
                System.out.println("========> NO wkt STEP RECORDS");
            } else {

                Mesg workout = wktRecordMesg.get(0);

                // Get workout name safely
                String wktName = workout.getFieldStringValue(WKT_NAME);
                if (wktName == null || wktName.isEmpty()) {
                    System.out.println("================ wktName == NULL");
                    wktName = "";
                }

                // Clean up workout name
                String newWktName = wktName
                        .replace("Bike ", "")
                        .replace("Run ", "")
                        .replace(" (bike)", "")
                        .replace("HR", "");

                // Compose new session profile name
                newProfileName += " " + newWktName + " " + ((float) (Math.round(totalDistance / 100)) / 10) + "km " + suffix;
                System.out.println("----> New SportProfile:  " + newProfileName);

                // Update first session message
                getSessionMesg().get(0).setFieldValue(SES_PROFILE, newProfileName);
                getSportMesg().get(0).setFieldValue(SP_NAME, newProfileName);
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
    /* public void addDevFieldDescr() {
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

    } */
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    /* public void renameDevFieldName() {
        for (FieldDescriptionMesg record : fieldDescrRecords) {
            if (devFieldNamesToUpdate.contains(record.getFieldName())) {
                record.setFieldName(0, "ActiveTime");
            }
        }
    } */
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

        System.out.println("----- REMOVE DEV FIELDS for ALL MESG -----");
        System.out.println("--- No of Dev Fields: " + fieldDescrMesg.size());

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

                    System.out.println("   AppId (hex): " + appIdHex);
                    System.out.println("   Compare to  : " + targetId);

                    // Compare the App IDs
                    if (appIdHex.equals(targetId)) {
                        devIndex = devId.getDeveloperDataIndex();
                        System.out.println("Match! Developer index for app: " + devIndex);
                        break; // Stop when matched
                    }
                }
            }
        }

        if (devIndex == null) {
            System.out.println("No developer data found for app " + devAppToModify);
            return;
        }

        // --- Step 2: remove matching FieldDescriptionMesg entries
        Iterator<Mesg> it = allMesg.iterator();
        while (it.hasNext()) {
            Mesg m = it.next();
            if (m.getNum() == MesgNum.FIELD_DESCRIPTION) {
                FieldDescriptionMesg f = new FieldDescriptionMesg(m);

                if (f.getDeveloperDataIndex() == devIndex &&
                        devFieldsToRemove.contains((int) f.getFieldDefinitionNumber())) {

                    System.out.println("Removing dev field def #" + f.getFieldDefinitionNumber() +
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

        System.out.println("--- Remaining dev fields: " + numberOfDevFields);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void removeDevFieldDescr() {

        removeDeveloperFieldDefinitions(
            allMesg,
            fieldDescrMesg,
            devAppToModify,
            devFieldsToRemove
        );

        System.out.println("--- No of Dev Fields: " + numberOfDevFields);
        System.out.println("--------------------------------------------------");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    /* public void addDeveloperfields() {

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
    } */
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    /* public void changeDeveloper() {
        int recordIx = 0;
        for (FieldDescriptionMesg record : fieldDescrRecords){

            if (record.getDeveloperDataIndex() == 1) {
                record.setDeveloperDataIndex((short) (0));
            }
            recordIx++;
        }
    } */
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public Boolean checkIfSkiErgFile() {
        //System.out.println("======== isSkiErgFile TEST ==========");
        Boolean isTrue = false;
        if (sportProfile.toLowerCase().contains("skierg")
            || sportProfile.toLowerCase().contains("row")
            ) {
                isTrue = true;
                setIsSkiErg(true);
         }
        return isTrue;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public Boolean checkIfTreadmillFile() {
        //System.out.println("======== isSkiErgFile TEST ==========");
        Boolean isTrue = false;
        if (sportProfile.toLowerCase().contains("löpband") 
            || sportProfile.toLowerCase().contains("pband")
            || sportProfile.toLowerCase().contains("mill")
            || sportProfile.toLowerCase().contains("tread")
            ) {
                isTrue = true;
                setIsTreadmill(true);
        }
        return isTrue;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public Boolean checkIfEllipticalFile() {
        //System.out.println("======== isEllipticalFile TEST ==========");
        Boolean isTrue = false;
        if (sportProfile.toLowerCase().contains("ellipt")
            || sportProfile.toLowerCase().contains("gymbike")
            || sportProfile.toLowerCase().contains("spinbike")
            || sportProfile.toLowerCase().contains("ct")
            ) {
                isTrue = true;
                setIsElliptical(true);
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

        if (!wktRecordMesg.isEmpty()) {
            wktRecordMesg.get(0).setFieldValue(WKT_SPORT, sport.getValue());
            wktRecordMesg.get(0).setFieldValue(WKT_SUBSPORT, subsport.getValue());
        }

        sessionMesg.get(0).setFieldValue(SES_SPORT, sport.getValue());
        sessionMesg.get(0).setFieldValue(SES_SUBSPORT, subsport.getValue());

        for (Mesg lap : lapMesg) {
            lap.setFieldValue(LAP_SPORT, sport.getValue());
            lap.setFieldValue(LAP_SUBSPORT, subsport.getValue());
        }

        // --- NEW: also update "Activity Metrics" message if present ---
        Field sportField;
        for (Mesg mesg : allMesg) {
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
        
        if (numberOfRecords < maxIxFixEmptyBeginning) {
            maxIxFixEmptyBeginning = numberOfRecords - 1;
        }

        for (Mesg record : recordMesg) {

            Float distFromDevField = 0f;
            Float speedFromDevField = 0f;
            Float strokeLengthFromDevField = 0f;
            Float dragFactorFromDevField = 0f;
            Float trainingSessionFromDevField = 0f;

            //--------------
            // Initiate secExtraRecords
            int lapNo = 1; // only for INIT of secExtraRecords for now
            secExtraRecords.add(new RecordExtraMesg(lapNo, C2DateTime));
            
            //--------------
            // Look for HR drop outs
            Short hr = record.getFieldShortValue(REC_HR);
            if (hr == null) {
                System.out.println(">>>>>>> HR EMPTY.  recordIx:" + recordIx);
                if (recordIx > 0) {
                    Short hrPrev = recordMesg.get(recordIx - 1).getFieldShortValue(REC_HR);
                    if (hrPrev != null) {
                        record.setFieldValue(REC_HR, hrPrev);
                    }
                }
            }

            // =========== MERGE/Import CIQ developer fields to native (generic access) ============
            for (DeveloperField field : record.getDeveloperFields()) {

                String name = field.getName();
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
            if (recordIx >= 3 && recordIx < numberOfRecords-2) {
                Float distCurrent = record.getFieldFloatValue(REC_DIST);
                Float distPrev = recordMesg.get(recordIx-1).getFieldFloatValue(REC_DIST);

                if (distCurrent != null && distPrev != null && distCurrent.equals(distPrev)) {

                    Mesg recordNext = recordMesg.get(recordIx+1);
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
                    
                    Mesg recordPrev1 = recordMesg.get(recordIx-1);
                    Mesg recordPrev2 = recordMesg.get(recordIx-2);
                    Float distPrev2 = recordPrev2.getFieldFloatValue(REC_DIST);
                    Float distStepNew = (distNextFromDevField - distPrev2) / 3;
                    
                    recordPrev1.setFieldValue(REC_DIST, distPrev2 + distStepNew);
                    record.setFieldValue(REC_DIST, distPrev2 + distStepNew*2);
                    //System.out.println("-------->" + recordMesg.get(recordIx-2).getFieldFloatValue(REC_DIST) + " " + currentDistBack1 + "->" + recordMesg.get(recordIx-1).getFieldFloatValue(REC_DIST) + " " + currentDist + "->" + record.getFieldFloatValue(REC_DIST) + " " + currentDistNext);
                    sameDistCounter++;
                }
                else {
                    sameDistCounter = 1;
                }
            }

            // =========== MERGE/Import C2 fitfile =============
            // =================================================
            while (c2FitFile.recordMesg.get(c2RecordIx).getFieldFloatValue(REC_DIST) - 0.5 <= record.getFieldFloatValue(REC_DIST) - C2FitFileDistanceStartCorrection) {
                record.setFieldValue(REC_CAD, c2FitFile.recordMesg.get(c2RecordIx).getFieldShortValue(REC_CAD));
                //record.setFieldValue(REC_POW, c2FitFile.recordMesg.get(c2RecordIx).getFieldIntegerValue(REC_POW));
                secExtraRecords.get(recordIx).C2DateTime = c2FitFile.recordMesg.get(c2RecordIx).getFieldLongValue(REC_TIME);
                c2RecordIx++;
                if (c2RecordIx > c2FitFile.numberOfRecords - 1) {
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
                        System.err.println("========= FIXED Beginning SPEED, first value: " + speed + " @ " + recordIx);
                        for (int i = recordIx-1; i >= 0; i--) {
                            Mesg recordToFix = recordMesg.get(i);
                            System.out.println("========= FIXING SPEED, value: " + recordToFix.getFieldFloatValue(REC_ESPEED) + "->" + speed + " @" + i);
                            recordToFix.setFieldValue(REC_SPEED, speed);
                            recordToFix.setFieldValue(REC_ESPEED, speed);
                        }
                        lookingInBeginningForEmptySpeed = false;
                    }
                }
                // FIX CADENCE
                if (lookingInBeginningForEmptyCadence) {
                    Short cad = record.getFieldShortValue(REC_CAD);
                    if (cad != null && cad != 0) {
                        System.out.println("========= FIXED Beginning CADENCE, first value: " + cad + " @ " + recordIx);
                        for (int i = recordIx-1; i >= 0; i--) {
                            Mesg recordToFix = recordMesg.get(i);
                            System.out.println("========= FIXING CADENCE, value: " + recordToFix.getFieldShortValue(REC_CAD) + "->" + cad + " @" + i);
                            recordToFix.setFieldValue(REC_CAD, cad);
                        }
                        lookingInBeginningForEmptyCadence = false;
                    }
                }
                // FIX POWER
                if (lookingInBeginningForEmptyPower) {
                    Integer power = record.getFieldIntegerValue(REC_POW);
                    if (power != null && power != 0) {
                        System.out.println("========= FIXED Beginning POWER, first value: " + power + " @ " + recordIx);
                        for (int i = recordIx-1; i >= 0; i--) {
                            Mesg recordToFix = recordMesg.get(i);
                            System.out.println("========= FIXING POWER, value: " + recordToFix.getFieldIntegerValue(REC_POW) + "->" + power + " @" + i);
                            recordToFix.setFieldValue(REC_POW, power);
                        }
                        lookingInBeginningForEmptyPower = false;
                    }
                }
                // FIX STROKE LENGTH
                if (lookingInBeginningForEmptyStrokeLength) {
                    if ((strokeLengthFromDevField!=null && strokeLengthFromDevField!=0)) {
                        System.out.println("========= FIXED Beginning STROKE LENGTH, first value: " + strokeLengthFromDevField + " @ " + recordIx);
                        for (int i = recordIx-1; i >= 0; i--) {
                            Mesg recordToFix = recordMesg.get(i);
                            for (DeveloperField field : recordToFix.getDeveloperFields()) {
                                if ("StrokeLength".equals(field.getName())) {
                                    System.out.println("========= FIXING STROKE LENGTH, value: " + field.getValue() + "->" + strokeLengthFromDevField + " @" + i);
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
                        System.out.println("========= FIXED Beginning DRAG FACTOR, first value: " + dragFactorFromDevField + " @ " + recordIx);
                        for (int i = recordIx-1; i >= 0; i--) {
                            Mesg recordToFix = recordMesg.get(i);
                            for (DeveloperField field : recordToFix.getDeveloperFields()) {
                                if ("DragFactor".equals(field.getName())) {
                                    System.out.println("========= FIXING DRAG FACTOR, value: " + field.getValue() + "->" + dragFactorFromDevField + " @" + i);
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
                        System.out.println("========= FIXED Beginning TRAINING_SESSION, first value: " + trainingSessionFromDevField + " @ " + recordIx);
                        for (int i = recordIx-1; i >= 0; i--) {
                            Mesg recordToFix = recordMesg.get(i);
                            for (DeveloperField field : recordToFix.getDeveloperFields()) {
                                if ("Training_session".equals(field.getName())) {
                                    System.out.println("========= FIXING TRAINING_SESSION, value: " + field.getValue() + "->" + trainingSessionFromDevField + " @" + i);
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
                            Mesg recordPrev1 = recordMesg.get(recordIx-1);
                            recordPrev1.setFieldValue(REC_SPEED, 0f);
                            recordPrev1.setFieldValue(REC_ESPEED, 0f);
                            recordPrev1.setFieldValue(REC_CAD, (short)0);
                            recordPrev1.setFieldValue(REC_POW, 0);
                            Mesg recordPrev2 = recordMesg.get(recordIx-2);
                            recordPrev2.setFieldValue(REC_SPEED, 0f);
                            recordPrev2.setFieldValue(REC_ESPEED, 0f);
                            recordPrev2.setFieldValue(REC_CAD, (short)0);
                            recordPrev2.setFieldValue(REC_POW, 0);
                            Mesg recordPrev3 = recordMesg.get(recordIx-3);
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
                Short prevCad = recordIx > 0 ? recordMesg.get(recordIx-1).getFieldShortValue(REC_CAD) : null;
                if (prevCad != null) {
                    record.setFieldValue(REC_CAD, prevCad);
                }
            }
            Integer pwr = record.getFieldIntegerValue(REC_POW);
            if (pwr == null) {
                Integer prevPow = recordIx > 0 ? recordMesg.get(recordIx-1).getFieldIntegerValue(REC_POW) : null;
                if (prevPow != null) {
                    record.setFieldValue(REC_POW, prevPow);
                }
            }
            // =========== Fix BAD PEAK/SPIKE data ==========
            // ==============================================
            if (recordIx>0) {
                Short cadFixSpike = record.getFieldShortValue(REC_CAD);
                Short cadLastFixSpike = recordMesg.get(recordIx-1).getFieldShortValue(REC_CAD);

                if ((cadFixSpike > maxCadenceValue) || (((cadFixSpike - cadLastFixSpike) > 9) && (cadLastFixSpike > 45))) {
                    System.out.println("=======>>> Fixed Cadence PEAK from: " + cadFixSpike + "->" + cadLastFixSpike);
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

                if (field.getName().equals("Distance")) {
                    distFromDevField = field.getFloatValue();
                    //if (currentDist == null)  currentDist = 0f;
                    // set native distance field
                    record.setFieldValue(REC_DIST, distFromDevField);
                    field.setValue(0f);
                }
                if (field.getName().equals("Cadence")) {
                    cadFromDevField = field.getShortValue();
                    //if (currentCadence == null) currentCadence = 0;
                    record.setFieldValue(REC_CAD, cadFromDevField);
                    field.setValue(0f);
                }
                if (field.getName().equals("Power")) {
                    powerFromDevField = field.getIntegerValue();
                    //if (currentPower == null) currentPower = 0;
                    record.setFieldValue(REC_POW, powerFromDevField);
                    field.setValue(0f);
                }
                if (field.getName().equals("Speed")) {
                    speedFromDevField = field.getFloatValue();
                    //if (currentSpeed == null) currentSpeed = 0f;
                    record.setFieldValue(REC_SPEED, speedFromDevField);
                    record.setFieldValue(REC_ESPEED, speedFromDevField);
                    field.setValue(0f);
                }
                if (field.getName().equals("StrokeLength")) {
                    strokeLengthFromDevField = field.getFloatValue();
                }
                if (field.getName().equals("DragFactor")) {
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
                        if (fieldRecordNext.getName().equals("Distance")) {
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
                        System.out.println("========= FIXED Beginning SPEED, first value: " + speed + " @ " + recordIx);
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
                        System.out.println("========= FIXED Beginning CADENCE, first value: " + record.getCadence() + " @ " + recordIx);
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
                        System.out.println("========= FIXED Beginning POWER, first value: " + record.getPower() + " @ " + recordIx);
                        lookingInBeginningForEmptyPower = false;
                    }
                }
                // FIX STROKE LENGTH
                if (lookingInBeginningForEmptyStrokeLength) {
                    if ((strokeLengthFromDevField!=null && strokeLengthFromDevField!=0)) {
                        for (int i = recordIx-1; i >= 0; i--) {
                            Mesg recordToFix = recordMesg.get(i);
                            for (DeveloperField field : recordToFix.getDeveloperFields()) {
                                if (field.getName().equals("StrokeLength")) {
                                    field.setValue(strokeLengthFromDevField);
                                }
                            }
                        }
                        System.out.println("========= FIXED Beginning STROKE LENGTH, first value: " + strokeLengthFromDevField + " @ " + recordIx);
                        lookingInBeginningForEmptyStrokeLength = false;
                    }
                }
                // FIX DRAG FACTOR
                if (lookingInBeginningForEmptyDragFactor) {
                    if ((dragFactorFromDevField!=null && (dragFactorFromDevField!=1 && dragFactorFromDevField!=0))) {
                        for (int i = recordIx-1; i >= 0; i--) {
                            Mesg recordToFix = recordMesg.get(i);
                            for (DeveloperField field : recordToFix.getDeveloperFields()) {
                                if (field.getName().equals("DragFactor")) {
                                    field.setValue(dragFactorFromDevField);
                                }
                            }
                        }
                        System.out.println("========= FIXED Beginning DRAG FACTOR, first value: " + dragFactorFromDevField + " @ " + recordIx);
                        lookingInBeginningForEmptyDragFactor = false;
                    }
                }
                // FIX TRAINING_SESSION
                if (lookingInBeginningForEmptyTrainingSession) {
                    if ((currentTrainingSession!=null && currentTrainingSession!=1)) {
                        for (int i = recordIx-1; i >= 0; i--) {
                            Mesg recordToFix = recordMesg.get(i);
                            for (DeveloperField field : recordToFix.getDeveloperFields()) {
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
         
        Float lastActiveFakeSumSpeed = 0f;
        Float lastActiveFakeSumCad = 0f;
        Float lastActiveFakeSumPower = 0f;

        Float maxActiveLapAvgPower = 0f;
        
        System.out.println("----- CALC AUTO SYNC IN TIME FOR SHIFTING VALUES iÍN TIME -----");

        // TEST RUN FOR 10 TIMES TO SEE BEST/HIGHEST VALUE FOR ACTIVE LAPS
        // ================================================================
        for (i = 0; i < 10; i++) {

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
            nextLapStartTime = lapMesg.get(0).getFieldLongValue(LAP_STIME);

            // Init session "max" fields (mapped to average constants if dedicated max constants not present)
            sessionMesg.get(0).setFieldValue(SES_MSPEED, 0f);    // used as max speed placeholder
            sessionMesg.get(0).setFieldValue(SES_EMSPEED, 0f);   // used as enhanced max speed placeholder
            sessionMesg.get(0).setFieldValue(SES_MCAD, (short) 0); // used as max cadence placeholder
            sessionMesg.get(0).setFieldValue(SES_MPOW, 0);      // used as max power placeholder

            for (Mesg record : recordMesg) {

                // --------------
                // IF LAP START
                currentTimeStamp = record.getFieldLongValue(REC_TIME);
                if (currentTimeStamp.equals(nextLapStartTime)) {

                    // Initiate lap max fields
                    lapMesg.get(lapIx).setFieldValue(LAP_MSPEED, 0f);
                    lapMesg.get(lapIx).setFieldValue(LAP_EMSPEED, 0f);
                    lapMesg.get(lapIx).setFieldValue(LAP_MCAD, (short) 0);
                    lapMesg.get(lapIx).setFieldValue(LAP_MPOW, 0);

                    // Save HR and recordIx START
                    Short hrStart = record.getFieldShortValue(REC_HR);
                    lapExtraRecords.get(lapIx).hrStart = hrStart;
                    lapExtraRecords.get(lapIx).recordIxStart = recordIx;

                    // Get LAP DATA to be used to find lap-start-end
                    Float lapTotalTimer = lapMesg.get(lapIx).getFieldFloatValue(LAP_TIMER);
                    currentLapTime = (lapTotalTimer == null) ? 0f : lapTotalTimer; // in sec

                    Short lapIntensityShort = lapMesg.get(lapIx).getFieldShortValue(LAP_INTENSITY);
                    Intensity lapIntensity = (lapIntensityShort == null) ? Intensity.INVALID : Intensity.getByValue(lapIntensityShort);
                    currentLapIntensity = Intensity.getStringFromValue(lapIntensity);

                    if (lapNo < numberOfLaps) {
                        currentLapTimeEnd = lapMesg.get(lapIx + 1).getFieldLongValue(LAP_STIME) - 1;
                        nextLapStartTime = lapMesg.get(lapIx + 1).getFieldLongValue(LAP_STIME);
                    } else {
                        currentLapTimeEnd = timeLastRecord;
                    }
                    // Save LAP END to table (DateTime)
                    lapExtraRecords.get(lapIx).timeEnd = currentLapTimeEnd;
                }

                // Calc LAP HR min
                Short recHr = record.getFieldShortValue(REC_HR);
                if (recHr == null) {
                    if (recordIx > 0) {
                        Short prevHr = recordMesg.get(recordIx - 1).getFieldShortValue(REC_HR);
                        record.setFieldValue(REC_HR, (prevHr == null) ? (short) 60 : prevHr);
                        recHr = record.getFieldShortValue(REC_HR);
                    } else {
                        record.setFieldValue(REC_HR, (short) 60);
                        recHr = record.getFieldShortValue(REC_HR);
                    }
                } else if (recHr < lapExtraRecords.get(lapIx).hrMin) {
                    lapExtraRecords.get(lapIx).hrMin = recHr;
                }

                // --------------
                // Calculate LAP MAX - initialize if null
                Float recEnhancedSpeed = record.getFieldFloatValue(REC_ESPEED);
                Float lapEnhancedMaxSpeed = lapMesg.get(lapIx).getFieldFloatValue(LAP_EMSPEED);
                if (recEnhancedSpeed > lapEnhancedMaxSpeed) {
                    lapMesg.get(lapIx).setFieldValue(LAP_EMSPEED, recEnhancedSpeed);
                    lapMesg.get(lapIx).setFieldValue(LAP_MSPEED, recEnhancedSpeed);
                }

                /* Float lapEnhancedMaxSpeed = lapMesg.get(lapIx).getFieldFloatValue(LAP_EMSPEED);
                if (lapEnhancedMaxSpeed == null) {
                    lapMesg.get(lapIx).setFieldValue(LAP_EMSPEED, 0f);
                    lapEnhancedMaxSpeed = 0f;
                }
                Float lapMaxSpeed = lapMesg.get(lapIx).getFieldFloatValue(LAP_MSPEED);
                if (lapMaxSpeed == null) {
                    lapMesg.get(lapIx).setFieldValue(LAP_MSPEED, 0f);
                    lapMaxSpeed = 0f;
                }

                Float recEnhancedSpeed = record.getFieldFloatValue(REC_ESPEED);
                if (recEnhancedSpeed != null && recEnhancedSpeed > lapEnhancedMaxSpeed) {
                    lapMesg.get(lapIx).setFieldValue(LAP_EMSPEED, recEnhancedSpeed);
                    lapMesg.get(lapIx).setFieldValue(LAP_MSPEED, recEnhancedSpeed);
                } */

                // --------------
                // Calc LAPSUM MAX CAD POWER (take values from a shifted sec/record index if available)
                if (recordIx < (numberOfRecords - tempC2SyncSecondsC2File - 1)) {
                    Short shiftedCad = recordMesg.get(recordIx + tempC2SyncSecondsC2File).getFieldShortValue(REC_CAD);
                    Integer shiftedPow = recordMesg.get(recordIx + tempC2SyncSecondsC2File).getFieldIntegerValue(REC_POW);
                    if (shiftedCad != null) currentLapSumCadence += shiftedCad;
                    if (shiftedPow != null) currentLapSumPower += shiftedPow;
                }

                // ensure lap max cadence is initialized
                Short lapMaxCad = lapMesg.get(lapIx).getFieldShortValue(LAP_MCAD);
                if (lapMaxCad == null) {
                    lapMesg.get(lapIx).setFieldValue(LAP_MCAD, (short) 0);
                    lapMaxCad = 0;
                }
                Short recCad = record.getFieldShortValue(REC_CAD);
                if (recCad != null && recCad > lapMaxCad) {
                    lapMesg.get(lapIx).setFieldValue(LAP_MCAD, recCad);
                }

                // ensure lap max power initialized
                Integer lapMaxPow = lapMesg.get(lapIx).getFieldIntegerValue(LAP_MPOW);
                if (lapMaxPow == null) {
                    lapMesg.get(lapIx).setFieldValue(LAP_MPOW, 0);
                    lapMaxPow = 0;
                }
                Integer recPow = record.getFieldIntegerValue(REC_POW);
                if (recPow != null && recPow > lapMaxPow) {
                    lapMesg.get(lapIx).setFieldValue(LAP_MPOW, recPow);
                }

                // Developer fields for stroke length / drag factor (reading from recordMesg at recordIx)
                for (DeveloperField field : recordMesg.get(recordIx).getDeveloperFields()) {
                    if (field.getName().equals("StrokeLength")) {
                        Float f = field.getFloatValue();
                        if (f != null) {
                            currentLapSumStrokeLen += f;
                            if (f > currentLapMaxStrokeLen) currentLapMaxStrokeLen = f;
                        }
                    }
                    if (field.getName().equals("DragFactor")) {
                        Float f = field.getFloatValue();
                        if (f != null) {
                            currentLapSumDragFactor += f;
                            if (f > currentLapMaxDragFactor) currentLapMaxDragFactor = f;
                        }
                    }
                }

                // --------------
                // Calculate SESSION SUM & MAX
                Float sessionEnhancedMaxSpeed = sessionMesg.get(0).getFieldFloatValue(SES_ESPEED);
                if (sessionEnhancedMaxSpeed == null) sessionEnhancedMaxSpeed = 0f;
                if (recEnhancedSpeed != null && recEnhancedSpeed > sessionEnhancedMaxSpeed) {
                    sessionMesg.get(0).setFieldValue(SES_ESPEED, recEnhancedSpeed);
                    sessionMesg.get(0).setFieldValue(SES_SPEED, recEnhancedSpeed); // also set "max" speed placeholder
                }

                if (recCad != null) currentSessionSumCadence += recCad;
                Short sessionMaxCadence = sessionMesg.get(0).getFieldShortValue(SES_CAD);
                if (sessionMaxCadence == null) sessionMaxCadence = 0;
                if (recCad != null && recCad > sessionMaxCadence) {
                    sessionMesg.get(0).setFieldValue(SES_CAD, recCad); // used as max cadence placeholder
                }

                if (recPow != null) currentSessionSumPower += recPow;
                Integer sessionMaxPow = sessionMesg.get(0).getFieldIntegerValue(SES_POW);
                if (sessionMaxPow == null) sessionMaxPow = 0;
                if (recPow != null && recPow > sessionMaxPow) {
                    sessionMesg.get(0).setFieldValue(SES_POW, recPow); // used as max power placeholder
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
                    lapExtraRecords.get(lapIx).hrEnd = record.getFieldShortValue(REC_HR);
                    lapExtraRecords.get(lapIx).recordIxEnd = recordIx;
                    lapExtraRecords.get(lapIx).timeEnd = record.getFieldLongValue(REC_TIME);

                    // Calc LAP DISTANCE & AVG SPEED
                    if (lapNo == numberOfLaps) {
                        tempC2SyncSecondsLapDistCalc = 0;
                    }
                    Float shiftedDistance = recordMesg.get(recordIx + tempC2SyncSecondsLapDistCalc).getFieldFloatValue(REC_DIST);
                    if (shiftedDistance == null) shiftedDistance = 0f;
                    Float lapTotalDistance = shiftedDistance - lastLapTotalDistance;
                    lapMesg.get(lapIx).setFieldValue(LAP_DIST, lapTotalDistance);
                    lastLapTotalDistance = shiftedDistance;

                    // set avg speed = distance / timer
                    Float lapTimer = lapMesg.get(lapIx).getFieldFloatValue(LAP_TIMER);
                    Float avgSpeedVal = 0f;
                    if (lapTimer != null && lapTimer != 0f) {
                        avgSpeedVal = lapTotalDistance / lapTimer;
                    }
                    lapMesg.get(lapIx).setFieldValue(LAP_SPEED, avgSpeedVal);
                    lapMesg.get(lapIx).setFieldValue(LAP_ESPEED, avgSpeedVal);
                    System.out.println("Lap " + lapNo + " Distance: " + lapTotalDistance 
                    + ", Timer: " + lapMesg.get(lapIx).getFieldFloatValue(LAP_TIMER) 
                    + ", AvgSpeed: " + lapMesg.get(lapIx).getFieldFloatValue(LAP_SPEED) 
                    + ", avgEnhanced: " + lapMesg.get(lapIx).getFieldFloatValue(LAP_ESPEED));

                    // Calc LAP SUM & LAP MAX
                    int denom = (recordIx - lapExtraRecords.get(lapIx).recordIxStart + 1);
                    if (denom <= 0) denom = 1;
                    short avgCad = (short) Math.round((float) currentLapSumCadence / denom);
                    lapMesg.get(lapIx).setFieldValue(LAP_CAD, avgCad);
                    currentLapSumCadence = 0;

                    int avgPow = Math.round((float) currentLapSumPower / denom);
                    lapMesg.get(lapIx).setFieldValue(LAP_POW, avgPow);
                    currentLapSumPower = 0;

                    // Developer fields for last record of lap - update lapExtraRecords stroke/drag
                    for (DeveloperField field : recordMesg.get(recordIx).getDeveloperFields()) {
                        if (field.getName().equals("StrokeLength")) {
                            lapExtraRecords.get(lapIx).avgStrokeLen = (float) Math.round(100 * currentLapSumStrokeLen / denom) / 100;
                            lapExtraRecords.get(lapIx).maxStrokeLen = currentLapMaxStrokeLen;
                            currentLapSumStrokeLen = 0f;
                            currentLapMaxStrokeLen = 0f;
                        }
                        if (field.getName().equals("DragFactor")) {
                            lapExtraRecords.get(lapIx).avgDragFactor = (float) Math.round(100 * currentLapSumDragFactor / denom) / 100;
                            lapExtraRecords.get(lapIx).maxDragFactor = currentLapMaxDragFactor;
                            currentLapSumDragFactor = 0f;
                            currentLapMaxDragFactor = 0f;
                        }
                    }

                    // --------------
                    // Calculate ACTIVE LAP SUM & MAX
                    if ("ACTIVE".equals(currentLapIntensity)) {
                        Float lapTotalTimerTime = lapMesg.get(lapIx).getFieldFloatValue(LAP_TIMER);
                        Float lapTotDist = lapMesg.get(lapIx).getFieldFloatValue(LAP_DIST);
                        Short lapAvgCad = lapMesg.get(lapIx).getFieldShortValue(LAP_CAD);
                        Integer lapAvgPow = lapMesg.get(lapIx).getFieldIntegerValue(LAP_POW);
                        Float lapAvgSpeedF = lapMesg.get(lapIx).getFieldFloatValue(LAP_SPEED);

                        if (lapTotalTimerTime != null) activeTime += lapTotalTimerTime;
                        if (lapTotDist != null) activeDist += lapTotDist;
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
                        Float lapTotalTimerTime = lapMesg.get(lapIx).getFieldFloatValue(LAP_TIMER);
                        Float lapTotDist = lapMesg.get(lapIx).getFieldFloatValue(LAP_DIST);
                        Short lapAvgCad = lapMesg.get(lapIx).getFieldShortValue(LAP_CAD);
                        Integer lapAvgPow = lapMesg.get(lapIx).getFieldIntegerValue(LAP_POW);
                        Float lapAvgSpeedF = lapMesg.get(lapIx).getFieldFloatValue(LAP_SPEED);

                        if (lapTotalTimerTime != null) restTime += lapTotalTimerTime;
                        if (lapTotDist != null) restDist += lapTotDist;
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
            recordMesg.get(0).setFieldValue(REC_DIST, 0f);

            // TOTAL DISTANCE activity
            Float totalDistanceF = (Float) recordMesg.get(recordIx).getFieldFloatValue(REC_DIST);
            if (totalDistanceF == null) totalDistanceF = 0f;
            totalDistance = totalDistanceF;
            sessionMesg.get(0).setFieldValue(SES_DIST, totalDistance);

            // TOTAL AVG SPEED activity
            avgSpeed = (float) (totalDistance / totalTimerTime);
            sessionMesg.get(0).setFieldValue(SES_SPEED, avgSpeed);
            sessionMesg.get(0).setFieldValue(SES_ESPEED, avgSpeed);

            // TOTAL AVG CADENCE activity
            avgCadence = Math.round((float) currentSessionSumCadence / (recordIx));
            sessionMesg.get(0).setFieldValue(SES_CAD, (short) avgCadence);

            // TOTAL AVG POWER activity
            avgPower = Math.round((float) currentSessionSumPower / (recordIx));
            sessionMesg.get(0).setFieldValue(SES_POW, avgPower);

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

            System.out.println("_____ i: " + i + " MAXsp: " + activeFakeSumSpeed + " cad: " + activeFakeSumCad + " pow: " + activeFakeSumPower);

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

        for (Mesg record : recordMesg) {

            if (hasC2Fit) {
                if (recordIx < (numberOfRecords-1-c2SyncSecondsLapDistCalc)) {
                    Mesg recordToUpdate = recordMesg.get(recordIx + c2SyncSecondsLapDistCalc);
                    record.setFieldValue(REC_DIST, recordToUpdate.getFieldFloatValue(REC_DIST));
                    record.setFieldValue(REC_SPEED, recordToUpdate.getFieldFloatValue(REC_SPEED));
                    record.setFieldValue(REC_ESPEED, recordToUpdate.getFieldFloatValue(REC_ESPEED));
                    record.setFieldValue(REC_POW, recordToUpdate.getFieldIntegerValue(REC_POW));
                } else {
                    Mesg recordLast = recordMesg.get(numberOfRecords-1);
                    record.setFieldValue(REC_DIST, recordLast.getFieldFloatValue(REC_DIST));
                    record.setFieldValue(REC_SPEED, recordLast.getFieldFloatValue(REC_SPEED));
                    record.setFieldValue(REC_ESPEED, recordLast.getFieldFloatValue(REC_ESPEED));
                    record.setFieldValue(REC_POW, recordLast.getFieldIntegerValue(REC_POW));
                }

                if (recordIx < (numberOfRecords-1-c2SyncSecondsC2File)) {
                    Mesg recordToUpdate = recordMesg.get(recordIx + c2SyncSecondsC2File);
                    record.setFieldValue(REC_CAD, recordToUpdate.getFieldIntegerValue(REC_CAD));
                    //record.setFieldValue(REC_POW, recordToUpdate.getFieldIntegerValue(REC_POW));
                } else {
                    Mesg recordLast = recordMesg.get(numberOfRecords-1);
                    record.setFieldValue(REC_CAD, recordLast.getFieldIntegerValue(REC_CAD));
                    //record.setFieldValue(REC_POW, recordLast.getFieldIntegerValue(REC_POW));
                }
            } else {
                // NO C2fit
                if (recordIx<(numberOfRecords-1-c2SyncSecondsLapDistCalc)) {
                    Mesg recordToUpdate = recordMesg.get(recordIx + c2SyncSecondsLapDistCalc);
                    record.setFieldValue(REC_DIST, recordToUpdate.getFieldFloatValue(REC_DIST));
                    record.setFieldValue(REC_SPEED, recordToUpdate.getFieldFloatValue(REC_SPEED));
                    record.setFieldValue(REC_ESPEED, recordToUpdate.getFieldFloatValue(REC_ESPEED));
                    record.setFieldValue(REC_POW, recordToUpdate.getFieldIntegerValue(REC_POW));
                    //record.setFieldValue(REC_POW, recordMesg.get(recordIx+c2SyncSecondsC2File).getFieldIntegerValue(REC_POW));
                } else {
                    Mesg recordLast = recordMesg.get(numberOfRecords-1);
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
        
        activeFakeSumSpeed = 0f;
        activeFakeSumCad = 0f;
        activeFakeSumPower = 0f;
        
        activeDist = 0f;
        restDist = 0f;

        // Get first lap start time (safe)
        nextLapStartTime = lapMesg.get(0).getFieldLongValue(LAP_STIME);

        // Initialize session max metrics to zero safely
        Mesg session = sessionMesg.get(0);
        session.setFieldValue(SES_MSPEED, 0f);
        session.setFieldValue(SES_EMSPEED, 0f);
        session.setFieldValue(SES_MCAD, (short) 0);
        session.setFieldValue(SES_MPOW, 0);
        
        System.out.println("______ before calcLapData - syncSeconds C2 (paw, cad): "+c2SyncSecondsC2File+" dist (speed): "+c2SyncSecondsLapDistCalc);

        System.out.println("----- CALC LAP DATA FROM SEC RECORDS - TOTAL RECS: " + numberOfRecords + " LAPS: " + numberOfLaps);
        for (Mesg record : recordMesg) {

            //--------------
            // IF LAP START
            Long ts = record.getFieldLongValue(REC_TIME);
            currentTimeStamp = ts != null ? ts : 0L;

            if ( currentTimeStamp.equals(nextLapStartTime) ) {

                // Initiate maxSpeed with 0 to be able to compare later
                lapMesg.get(lapIx).setFieldValue(LAP_SPEED, 0f);
                lapMesg.get(lapIx).setFieldValue(LAP_ESPEED, 0f);
                lapMesg.get(lapIx).setFieldValue(LapMesg.MaxCadenceFieldNum, (short)0);
                lapMesg.get(lapIx).setFieldValue(LapMesg.MaxPowerFieldNum, 0);
                
                // Save HR and recordIx START
                Short hrStart = record.getFieldShortValue(REC_HR);
                lapExtraRecords.get(lapIx).hrStart = hrStart != null ? hrStart : 0;
                lapExtraRecords.get(lapIx).recordIxStart = recordIx;

                // Get LAP DATA to be used to find lap-start-end
                Float lapTimerF = lapMesg.get(lapIx).getFieldFloatValue(LAP_TIMER);
                currentLapTime = lapTimerF != null ? lapTimerF : 0f; // in sec

                Short intensityVal = lapMesg.get(lapIx).getFieldShortValue(LapMesg.IntensityFieldNum);
                currentLapIntensity = Intensity.getStringFromValue(Intensity.getByValue(intensityVal != null ? intensityVal : 0));

                if (lapNo < numberOfLaps) {
                    Long nextStart = lapMesg.get(lapIx+1).getFieldLongValue(LAP_STIME);
                    currentLapTimeEnd = nextStart != null ? nextStart - 1 : timeLastRecord;
                    nextLapStartTime = nextStart != null ? nextStart : nextLapStartTime;
                } else {
                    currentLapTimeEnd = timeLastRecord;
                }

                // Save LAP END to table
                lapExtraRecords.get(lapIx).timeEnd = currentLapTimeEnd;
            }

            // Calc LAP HR min
            Short recHr = record.getFieldShortValue(REC_HR);
            if (recHr == null) {
                if (recordIx > 0) {
                    Short prevHr = recordMesg.get(recordIx-1).getFieldShortValue(REC_HR);
                    record.setFieldValue(REC_HR, prevHr != null ? prevHr : (short)60);
                    recHr = record.getFieldShortValue(REC_HR);
                } else {
                    record.setFieldValue(REC_HR, (short)60);
                    recHr = 60;
                }
            }
            if ( recHr < lapExtraRecords.get(lapIx).hrMin ) {
                lapExtraRecords.get(lapIx).hrMin = recHr;
            }

            //--------------
            // Calculate LAP MAX
            Float recEnhSp = record.getFieldFloatValue(REC_ESPEED);
            Float lapEnhMax = lapMesg.get(lapIx).getFieldFloatValue(LAP_EMSPEED);
            if (lapEnhMax == null) lapEnhMax = 0f;
            if (recEnhSp != null && recEnhSp > lapEnhMax) {
                lapMesg.get(lapIx).setFieldValue(LAP_EMSPEED, recEnhSp);
                lapMesg.get(lapIx).setFieldValue(LAP_MSPEED, recEnhSp);
            }
            
            //--------------
            // Calc LAPSUM CAD POWER
            // SHIFTED
            int idxForC2Cad = recordIx + c2SyncSecondsC2File;
            if (idxForC2Cad < recordMesg.size()) {
                Short cadShift = recordMesg.get(idxForC2Cad).getFieldShortValue(REC_CAD);
                Integer powShift = recordMesg.get(idxForC2Cad).getFieldIntegerValue(REC_POW);
                currentLapSumCadence += cadShift != null ? cadShift : 0;
                currentLapSumPower += powShift != null ? powShift : 0;
            }
            // Calc MAX CAD POWER
            // NON-SHIFTED
            Short recCad = record.getFieldShortValue(REC_CAD);
            if (recCad != null) {
                Short lapMaxCad = lapMesg.get(lapIx).getFieldShortValue(LAP_MCAD);
                if (lapMaxCad == null) lapMaxCad = 0;
                if (recCad > lapMaxCad) {
                    lapMesg.get(lapIx).setFieldValue(LAP_MCAD, recCad);
                }
            }
            Integer recPow = record.getFieldIntegerValue(REC_POW);
            if (recPow != null) {
                Integer lapMaxPow = lapMesg.get(lapIx).getFieldIntegerValue(LAP_MPOW);
                if (lapMaxPow == null) lapMaxPow = 0;
                if (recPow > lapMaxPow) {
                    lapMesg.get(lapIx).setFieldValue(LAP_MPOW, recPow);
                }
            }

            // Calc LAPSUM & MLAPMAX for Developer fields from current record
            // NON-SHIFTED
            Mesg secRecForDev = recordMesg.get(recordIx); // Why not just use 'record'???
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
            Float sessEnhMax = sessionMesg.get(0).getFieldFloatValue(SES_EMSPEED);
            if (sessEnhMax == null) sessEnhMax = 0f;
            if (recEnhSp != null && recEnhSp > sessEnhMax) {
                sessionMesg.get(0).setFieldValue(SES_EMSPEED, recEnhSp);
                sessionMesg.get(0).setFieldValue(SES_MSPEED, recEnhSp);
            }
            currentSessionSumCadence += recCad != null ? recCad : 0;
            if (recCad != null && recCad > sessionMesg.get(0).getFieldShortValue(SES_MCAD)) {
                sessionMesg.get(0).setFieldValue(SES_MCAD, recCad);
            }
            currentSessionSumPower += recPow != null ? recPow : 0;
            if (recPow != null && recPow > sessionMesg.get(0).getFieldIntegerValue(SES_MPOW)) {
                sessionMesg.get(0).setFieldValue(SES_MPOW, recPow);
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
                lapExtraRecords.get(lapIx).hrEnd = record.getFieldShortValue(REC_HR) != null ? record.getFieldShortValue(REC_HR) : 0;
                lapExtraRecords.get(lapIx).recordIxEnd = recordIx;
                lapExtraRecords.get(lapIx).timeEnd = currentTimeStamp;

                // Calc LAP DISTANCE & AVG SPEED
                // SHIFTED
                int idxForLapDist = recordIx + c2SyncSecondsLapDistCalc;
                Float lapTotalDist = idxForLapDist < recordMesg.size() ? 
                    recordMesg.get(idxForLapDist).getFieldFloatValue(REC_DIST) : 
                    recordMesg.get(recordMesg.size()-1).getFieldFloatValue(REC_DIST);
                //Float lastLapTotalDistanceF = lastLapTotalDistance;
                Float lapDist = (lapTotalDist != null ? lapTotalDist : 0f) - lastLapTotalDist;
                lapMesg.get(lapIx).setFieldValue(LAP_DIST, lapDist);
                lastLapTotalDist = lapTotalDist != null ? lapTotalDist : lastLapTotalDist;

                Float lapTimer = lapMesg.get(lapIx).getFieldFloatValue(LAP_TIMER);
                //lapMesg.get(lapIx).setFieldValue(LapMesg.TotalMovingTimeFieldNum, lapTimer);
                Float avgSp = lapTimer != null && lapTimer != 0 ? (float) (lapDist / lapTimer) : 0f;
                lapMesg.get(lapIx).setFieldValue(LAP_SPEED, avgSp);
                lapMesg.get(lapIx).setFieldValue(LAP_ESPEED, avgSp);
                
                // Calc LAP SUM & LAP MAX
                int lapRecCount = recordIx - lapExtraRecords.get(lapIx).recordIxStart + 1;
                short avgCad = lapRecCount > 0 ? (short) Math.round((float) currentLapSumCadence / lapRecCount) : 0;
                lapMesg.get(lapIx).setFieldValue(LAP_CAD, avgCad);
                currentLapSumCadence = 0;

                int avgPow = lapRecCount > 0 ? Math.round((float) currentLapSumPower / lapRecCount) : 0;
                lapMesg.get(lapIx).setFieldValue(LAP_POW, avgPow);
                currentLapSumPower = 0;

                for (DeveloperField field : recordMesg.get(recordIx).getDeveloperFields()) {
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
                    activeTime = activeTime + (lapTimer != null ? lapTimer : 0f);
                    activeDist = activeDist + lapDist;
                    activeSumSpeed = activeSumSpeed + (lapMesg.get(lapIx).getFieldFloatValue(LAP_SPEED) != null ? lapMesg.get(lapIx).getFieldFloatValue(LAP_SPEED) * (lapTimer != null ? lapTimer : 0f) : 0f);
                    activeSumCad = activeSumCad + (float) ((lapMesg.get(lapIx).getFieldShortValue(LAP_CAD) != null ? lapMesg.get(lapIx).getFieldShortValue(LAP_CAD) : 0) * (lapTimer != null ? lapTimer : 0f));
                    activeSumPower = activeSumPower + (lapMesg.get(lapIx).getFieldIntegerValue(LAP_POW) != null ? lapMesg.get(lapIx).getFieldIntegerValue(LAP_POW) * (lapTimer != null ? lapTimer : 0f) : 0f);

                    activeFakeSumSpeed = activeFakeSumSpeed + (lapMesg.get(lapIx).getFieldFloatValue(LAP_SPEED) != null ? lapMesg.get(lapIx).getFieldFloatValue(LAP_SPEED) : 0f);
                    activeFakeSumCad = activeFakeSumCad + (lapMesg.get(lapIx).getFieldShortValue(LAP_CAD) != null ? lapMesg.get(lapIx).getFieldShortValue(LAP_CAD) : 0);
                    activeFakeSumPower = activeFakeSumPower + (lapMesg.get(lapIx).getFieldIntegerValue(LAP_POW) != null ? lapMesg.get(lapIx).getFieldIntegerValue(LAP_POW) : 0);
                }
                // Calculate REST LAP SUM & MAX
                if (currentLapIntensity.equals("REST") || currentLapIntensity.equals("RECOVERY")) {
                    restTime = restTime + (lapTimer != null ? lapTimer : 0f);
                    restDist = restDist + (lapMesg.get(lapIx).getFieldFloatValue(LAP_DIST) != null ? lapMesg.get(lapIx).getFieldFloatValue(LAP_DIST) : 0f);
                    restSumSpeed = restSumSpeed + (lapMesg.get(lapIx).getFieldFloatValue(LAP_SPEED) != null ? lapMesg.get(lapIx).getFieldFloatValue(LAP_SPEED) * (lapTimer != null ? lapTimer : 0f) : 0f);
                    restSumCad = restSumCad + (lapMesg.get(lapIx).getFieldShortValue(LAP_CAD) != null ? lapMesg.get(lapIx).getFieldShortValue(LAP_CAD) * (lapTimer != null ? lapTimer : 0f) : 0f);
                    restSumPower = restSumPower + (lapMesg.get(lapIx).getFieldIntegerValue(LAP_POW) != null ? lapMesg.get(lapIx).getFieldIntegerValue(LAP_POW) * (lapTimer != null ? lapTimer : 0f) : 0f);
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
        recordMesg.get(0).setFieldValue(REC_DIST, 0f);

        // TOTAL DISTANCE activity
        Float distF = recordMesg.get(recordIx).getFieldFloatValue(REC_DIST);
        totalDistance = distF != null ? distF : 0f;
        sessionMesg.get(0).setFieldValue(SES_DIST, totalDistance);

        // TOTAL AVG SPEED activity
        avgSpeed = (float) (totalDistance / totalTimerTime);
        sessionMesg.get(0).setFieldValue(SES_SPEED, avgSpeed);
        sessionMesg.get(0).setFieldValue(SES_ESPEED, avgSpeed);

        // TOTAL AVG CADENCE activity
        avgCadence = Math.round((float) currentSessionSumCadence / (recordIx));
        sessionMesg.get(0).setFieldValue(SES_CAD, (short) avgCadence);

        // TOTAL AVG POWER activity
        avgPower = Math.round((float) currentSessionSumPower / (recordIx));
        sessionMesg.get(0).setFieldValue(SES_POW, avgPower);

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
    public void calcSplitRecordsBasedOnLaps() {

        Integer lapIx = 0;
        Integer lapIxInSplitMesg = 0;

        for (Mesg lap : lapMesg) {
            for (int i = 0; i < splitMesg.size(); i++) {
                lapIxInSplitMesg = splitMesg.get(i).getFieldIntegerValue(67);
                if (lapIxInSplitMesg != null && lapIxInSplitMesg.equals(lapIx)) {
                    System.out.println("----- Link   SPLIT index " + lapIxInSplitMesg + " to LAP index " + lapIx);
                    break;
                }
            }
            Float lapDist = lap.getFieldFloatValue(LAP_DIST);
            Float lapSpeed = lap.getFieldFloatValue(LAP_ESPEED);
            Float lapMaxSpeed = lap.getFieldFloatValue(LAP_EMSPEED);
            Float distAtLapStart = recordMesg.get(lapExtraRecords.get(lapIx).recordIxStart).getFieldFloatValue(REC_DIST);
            Integer lapPow = lap.getFieldIntegerValue(LAP_POW);
            Integer lapMaxPow = lap.getFieldIntegerValue(LAP_MPOW);

            splitMesg.get(lapIxInSplitMesg).setFieldValue(SPL_DIST, lapDist!= null ? lapDist : 0f);
            splitMesg.get(lapIxInSplitMesg).setFieldValue(SPL_SPEED, lapSpeed != null ? lapSpeed : 0f);
            splitMesg.get(lapIxInSplitMesg).setFieldValue(SPL_MSPEED, lapMaxSpeed != null ? lapMaxSpeed : 0f);
            splitMesg.get(lapIxInSplitMesg).setFieldValue(7, distAtLapStart != null ? distAtLapStart : 0f);
            
            /* splitMesg.get(splitIx).setFieldValue(40, lapPow != null ? lapPow : 0);
            splitMesg.get(splitIx).setFieldValue(41, lapMaxPow != null ? lapMaxPow : 0); */

            lapIx++;
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void calcSplitSummaryBasedOnSplits() {
        // NOT IMPLEMENTED YET
        for (Mesg splitSumMesg : splitSummaryMesg) {
            Short splitType = splitSumMesg.getFieldShortValue(SPLSUM_TYPE);
            for (Mesg splitMesg : splitMesg) {
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
        
        if (numberOfRecords < maxIxFixEmptyBeginning) {
            maxIxFixEmptyBeginning = numberOfRecords - 1;
        }

        for (Mesg record : recordMesg) {
            if (recordIx <= maxIxFixEmptyBeginning) {
                // FIX CADENCE
                if (lookingInBeginningForEmptyCadence) {
                    Short cad = record.getFieldShortValue(REC_CAD);
                    if ((cad!=null && cad!=0 && cad>20)) {
                        System.out.println("========= FIXED Beginning CADENCE, first real value: " + cad + " @ " + recordIx);
                        for (int i = recordIx-1; i >= 0; i--) {
                            System.out.println("========= FIXING CADENCE, value: " + recordMesg.get(i).getFieldValue(REC_CAD) + "->" + cad + " @" + i);
                            recordMesg.get(i).setFieldValue(REC_CAD, cad);
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
        
        if (numberOfRecords < maxIxFixEmptyBeginning) {
            maxIxFixEmptyBeginning = numberOfRecords - 1;
        }

        for (Mesg record : recordMesg) {
            if (recordIx <= maxIxFixEmptyBeginning) {
                // FIX SPEED
                if (lookingInBeginningForEmptySpeed) {
                    Float speed = record.getFieldFloatValue(REC_ESPEED);
                    if (speed!=null && speed!=0f && speed!=1 && speed<100f) {
                        System.out.println("========= FIXED Beginning SPEED, first value: " + speed + " @ " + recordIx);
                        for (int i = recordIx-1; i >= 0; i--) {
                            System.out.println("========= FIXING SPEED, value: " + recordMesg.get(i).getFieldValue(REC_ESPEED) + "->" + speed + " @" + i);
                            recordMesg.get(i).setFieldValue(REC_ESPEED, speed);
                            recordMesg.get(i).setFieldValue(REC_SPEED, speed);
                        }
                        lookingInBeginningForEmptySpeed = false;
                    }
                }
                // FIX CADENCE
                if (lookingInBeginningForEmptyCadence) {
                    Short cad = record.getFieldShortValue(REC_CAD);
                    if (cad!=null && cad!=0 && cad!=1 && cad<100) {
                        System.out.println("========= FIXED Beginning CADENCE, first value: " + cad + " @ " + recordIx);
                        for (int i = recordIx-1; i >= 0; i--) {
                            System.out.println("========= FIXING CADENCE, value: " + recordMesg.get(i).getFieldValue(REC_CAD) + "->" + cad + " @" + i);
                            recordMesg.get(i).setFieldValue(REC_CAD, cad);
                        }
                        lookingInBeginningForEmptyCadence = false;
                    }
                }
                // FIX POWER
                if (lookingInBeginningForEmptyPower) {
                    Integer power = record.getFieldIntegerValue(REC_POW);
                    if ((power!=null && power!=0)) {
                        System.out.println("========= FIXED Beginning POWER, first value: " + power + " @ " + recordIx);
                        for (int i = recordIx-1; i >= 0; i--) {
                            System.out.println("========= FIXING POWER, value: " + recordMesg.get(i).getFieldValue(REC_POW) + "->" + power + " @" + i);
                            recordMesg.get(i).setFieldValue(REC_POW, power);
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

        nextLapStartTime = lapMesg.get(0).getFieldLongValue(LAP_STIME);

        for (Mesg record : recordMesg) {

            //--------------
            // Initiate secExtraRecords
            secExtraRecords.add(new RecordExtraMesg(lapNo, C2DateTime));

            //--------------
            // IF LAP START
            currentTimeStamp = record.getFieldLongValue(REC_TIME);
            if ( currentTimeStamp.equals(nextLapStartTime) ) {
                // Get LAP DATA to be used to find lap-start-end
                Float lapTotalTimer = lapMesg.get(lapIx).getFieldFloatValue(LAP_TIMER);
                currentLapTime = (lapTotalTimer == null) ? 0f : lapTotalTimer; // in sec

                if (lapNo < numberOfLaps) {
                    currentLapTimeEnd = lapMesg.get(lapIx + 1).getFieldLongValue(LAP_STIME) - 1;
                    nextLapStartTime = lapMesg.get(lapIx + 1).getFieldLongValue(LAP_STIME);
                } else {
                    currentLapTimeEnd = timeLastRecord;
                }
                // Save LAP END to table
                lapExtraRecords.get(lapIx).timeEnd = currentLapTimeEnd;
            }

            //--------------
            // FIX EMPTY CADENCE
            Short cadFixEmpty = record.getFieldShortValue(REC_CAD);
            if (cadFixEmpty == null) {
                if (recordIx > 0) {
                    Short cadPrevFixEmpty = recordMesg.get(recordIx-1).getFieldShortValue(REC_CAD);
                    record.setFieldValue(REC_CAD, cadPrevFixEmpty != null ? cadPrevFixEmpty : (short) 60);
                } else {
                    record.setFieldValue(REC_CAD, (short) 60);
                }
            }

            //--------------
            // Calc LAP SUM SPEED or CADENCE
            LapExtraMesg lapExtra = lapExtraRecords.get(lapIx);
            if (getIsTreadmill() == true) {
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
                lapExtraRecords.get(lapIx).recordIxEnd = recordIx;
                lapExtraRecords.get(lapIx).timeEnd = record.getFieldLongValue(REC_TIME);

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

        nextLapStartTime = lapMesg.get(0).getFieldLongValue(LAP_STIME);

        sessionMesg.get(0).setFieldValue(SES_MSPEED, 0f);
        sessionMesg.get(0).setFieldValue(SES_EMSPEED, 0f);
        sessionMesg.get(0).setFieldValue(SES_MCAD, (short) 0);
        sessionMesg.get(0).setFieldValue(SES_MPOW, 0);

        for (Mesg record : recordMesg) {

            //--------------
            // Initiate secExtraRecords
            secExtraRecords.add(new RecordExtraMesg(lapNo, C2DateTime));

            //--------------
            // IF LAP START
            currentTimeStamp = record.getFieldLongValue(REC_TIME);
            if ( currentTimeStamp.equals(nextLapStartTime) ) {
                //System.out.println("Lapstart lapIx: " + lapIx + " recordIxStart: " + lapExtraRecords.get(lapIx).recordIxStart + " recordIx: " + recordIx);

                // Initiate maxSpeed with 0 to be able to compare later
                lapMesg.get(lapIx).setFieldValue(LAP_MSPEED, 0f);
                lapMesg.get(lapIx).setFieldValue(LAP_EMSPEED, 0f);
                
                // Save HR and recordIx START
                lapExtraRecords.get(lapIx).hrStart = record.getFieldIntegerValue(REC_HR);
                lapExtraRecords.get(lapIx).recordIxStart = recordIx;

                // Get LAP DATA to be used to find lap-start-end
                Float lapTotalTimer = lapMesg.get(lapIx).getFieldFloatValue(LAP_TIMER);
                currentLapTime = (lapTotalTimer == null) ? 0f : lapTotalTimer; // in sec

                Short lapIntensityShort = lapMesg.get(lapIx).getFieldShortValue(LAP_INTENSITY);
                Intensity lapIntensity = (lapIntensityShort == null) ? Intensity.INVALID : Intensity.getByValue(lapIntensityShort);
                currentLapIntensity = Intensity.getStringFromValue(lapIntensity);

                if (lapNo < numberOfLaps) {
                    currentLapTimeEnd = lapMesg.get(lapIx + 1).getFieldLongValue(LAP_STIME) - 1;
                    nextLapStartTime = lapMesg.get(lapIx + 1).getFieldLongValue(LAP_STIME);
                } else {
                    currentLapTimeEnd = timeLastRecord;
                }
                // Save LAP END to table
                lapExtraRecords.get(lapIx).timeEnd = currentLapTimeEnd;
            }

            //--------------
            // FIX EMPTY CADENCE
            Short cad = record.getFieldShortValue(REC_CAD);
            if (cad == null) {
                if (recordIx > 0) {
                    Short cadPrev = recordMesg.get(recordIx-1).getFieldShortValue(REC_CAD);
                    record.setFieldValue(REC_CAD, cadPrev != null ? cadPrev : (short) 60);
                } else {
                    record.setFieldValue(REC_CAD, (short) 60);
                }
            }
            //--------------
            // Calculate DIST between RECORDS based on Cadence
            //recordDist = lapExtraRecords.get(lapIx).stepLen * cad / 60;
            if (getIsTreadmill() == true) {
                // TREADMILL
                Float speed = record.getFieldFloatValue(REC_ESPEED);
                Float speedLapSum = lapExtraRecords.get(lapIx).getSpeedLapSum();
                recordDist = (speed == null) ? 0f : lapMesg.get(lapIx).getFieldFloatValue(LAP_DIST) * (speed / speedLapSum);
                //--System.out.println("RecordIx: " + recordIx + " Speed: " + speed + " SpeedLapSum: " + speedLapSum + " Dist: " + recordDist + " lapNo: " + lapNo);
            } else {
                // ELLIPTICAL
                recordDist = lapExtraRecords.get(lapIx).stepLen * cad / 60;
            }
            lapSumOfRecordDist += recordDist;
            sumOfRecordDist += recordDist;

            //--------------
            // Calc LAP HR min
            Short recHr = record.getFieldShortValue(REC_HR);
            if (recHr == null) {
                if (recordIx > 0) {
                    Short prevHr = recordMesg.get(recordIx - 1).getFieldShortValue(REC_HR);
                    record.setFieldValue(REC_HR, (prevHr == null) ? (short) 60 : prevHr);
                    recHr = record.getFieldShortValue(REC_HR);
                } else {
                    record.setFieldValue(REC_HR, (short) 60);
                    recHr = record.getFieldShortValue(REC_HR);
                }
            } else if (recHr < lapExtraRecords.get(lapIx).hrMin) {
                lapExtraRecords.get(lapIx).hrMin = recHr;
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
            if ( currentTimeStamp.equals(currentLapTimeEnd) ) {
                System.out.println("LapEND lapIx: " + lapIx + " recordIxStart: " + lapExtraRecords.get(lapIx).recordIxStart + " recordIx: " + recordIx);

                // Save HR and recordIx END
                lapExtraRecords.get(lapIx).hrEnd = record.getFieldShortValue(REC_HR);
                lapExtraRecords.get(lapIx).recordIxEnd = recordIx;
                lapExtraRecords.get(lapIx).timeEnd = record.getFieldLongValue(REC_TIME);

                //--------------
                // CORRECTION
                // INIT of Variables
                corrPerMeter = (lapSumOfRecordDist - lapMesg.get(lapIx).getFieldFloatValue(LAP_DIST)) / lapSumOfRecordDist;
                System.out.println("---- Before CORRECTION1 LapNo:" + lapNo + " LapDist:" + lapMesg.get(lapIx).getFieldFloatValue(LAP_DIST) + " lapSumOfRecordDist:" + lapSumOfRecordDist + " CorrPerMeter:" + corrPerMeter + " sumOfRecordDist:" + sumOfRecordDist);
                sumOfRecordDist = sumOfRecordDist - lapSumOfRecordDist;
                System.out.println("---- Before CORRECTION2 LapNo:" + lapNo + " LapDist:" + lapMesg.get(lapIx).getFieldFloatValue(LAP_DIST) + " lapSumOfRecordDist:" + lapSumOfRecordDist + " CorrPerMeter:" + corrPerMeter + " sumOfRecordDist:" + sumOfRecordDist);
                lapSumOfRecordDist = 0f;
                System.out.println("---- Before CORRECTION3 LapNo:" + lapNo + " LapDist:" + lapMesg.get(lapIx).getFieldFloatValue(LAP_DIST) + " lapSumOfRecordDist:" + lapSumOfRecordDist + " CorrPerMeter:" + corrPerMeter + " sumOfRecordDist:" + sumOfRecordDist);

                // CORRECTION RECAP LAP
                for (int j=lapExtraRecords.get(lapIx).recordIxStart; j<=lapExtraRecords.get(lapIx).recordIxEnd; j++) {
                    Mesg jRecord = recordMesg.get(j);
                    //System.out.println("   j:"+j+" lapix:"+lapIx);
                    //recordDist = lapExtraRecords.get(lapIx).stepLen * recordMesg.get(j).getFieldShortValue(REC_CAD) / 60;
                    //--------------
                    // Calculate DIST between RECORDS based on Cadence
                    //recordDist = lapExtraRecords.get(lapIx).stepLen * cad / 60;
                    if (getIsTreadmill() == true) {
                        // TREADMILL
                        Float speed = jRecord.getFieldFloatValue(REC_ESPEED);
                        Float speedLapSum = lapExtraRecords.get(lapIx).getSpeedLapSum();
                        recordDist = (speed == null) ? 0f : lapMesg.get(lapIx).getFieldFloatValue(LAP_DIST) * (speed / speedLapSum);
                        //--System.out.println("RecordIx: " + recordIx + " Speed: " + speed + " SpeedLapSum: " + speedLapSum + " Dist: " + recordDist + " lapNo: " + lapNo);
                    } else {
                        // ELLIPTICAL
                        recordDist = lapExtraRecords.get(lapIx).stepLen * jRecord.getFieldShortValue(REC_CAD) / 60;
                    }
                    recordDist = recordDist - recordDist * corrPerMeter;
                    lapSumOfRecordDist += recordDist;
                    sumOfRecordDist += recordDist;
                    jRecord.setFieldValue(REC_DIST, sumOfRecordDist);

                    if (j>0) {
                        Float recDistPrev = recordMesg.get(j-1).getFieldFloatValue(REC_DIST);
                        if (recDistPrev == null) recDistPrev = 0f;
                        //--------------
                        // Calculate SPEED
                        Float newSpeed = (sumOfRecordDist - recDistPrev) / 1; // 1sec requirement
                        jRecord.setFieldValue(REC_SPEED, newSpeed); // 1sec requirement
                        jRecord.setFieldValue(REC_ESPEED, newSpeed); // 1sec requirement

                        //--------------
                        // Calculate LAP MAX
                        if(newSpeed == null) newSpeed = 0f;
                        Float lapEMSpeed = lapMesg.get(lapIx).getFieldFloatValue(LAP_EMSPEED);
                        if (lapEMSpeed == null) lapEMSpeed = 0f;

                        if (newSpeed > lapEMSpeed) {
                            lapMesg.get(lapIx).setFieldValue(LAP_MSPEED, newSpeed);
                            lapMesg.get(lapIx).setFieldValue(LAP_EMSPEED, newSpeed);
                            //System.out.println("-----recIx:"+recordIx+" lapIx:"+lapIx+" Sp:"+record.getEnhancedSpeed()+"m/s "+mps2kmph(record.getEnhancedSpeed())+"km/h");
                        }
                        //--------------
                        // Calculate SESSION MAX
                        Float sesEMSpeed = sessionMesg.get(0).getFieldFloatValue(SES_EMSPEED);
                        if (sesEMSpeed == null) sesEMSpeed = 0f;
                        
                        if (newSpeed > sesEMSpeed) {
                            sessionMesg.get(0).setFieldValue(SES_MSPEED, newSpeed);
                            sessionMesg.get(0).setFieldValue(SES_EMSPEED, newSpeed);
                            //System.out.println("-----recIx:"+recordIx+" 0:"+0+" Sp:"+record.getEnhancedSpeed()+"m/s "+mps2kmph(record.getEnhancedSpeed())+"km/h");
                        }
                    }

                }
                System.out.println("---- After CORRECTION1 LapNo:" + lapNo + " LapDist:" + lapMesg.get(lapIx).getFieldFloatValue(LAP_DIST) + " lapSumOfRecordDist:" + lapSumOfRecordDist + " CorrPerMeter:" + corrPerMeter + " sumOfRecordDist:" + sumOfRecordDist);

                lapSumOfRecordDist = (float) Math.round(lapSumOfRecordDist);
                sumOfRecordDist = (float) Math.round(sumOfRecordDist);
                System.out.println("---- After CORRECTION2 LapNo:" + lapNo + " LapDist:" + lapMesg.get(lapIx).getFieldFloatValue(LAP_DIST) + " lapSumOfRecordDist:" + lapSumOfRecordDist + " CorrPerMeter:" + corrPerMeter + " sumOfRecordDist:" + sumOfRecordDist);

                //--------------
                // Calculate ACTIVE LAP SUM & MAX
                Float lapTotalTimer = lapMesg.get(lapIx).getFieldFloatValue(LAP_TIMER);
                Float lapTotalDist = lapMesg.get(lapIx).getFieldFloatValue(LAP_DIST);
                Float lapAvgSpeed = lapMesg.get(lapIx).getFieldFloatValue(LAP_ESPEED);
                Short lapAvgCad = lapMesg.get(lapIx).getFieldShortValue(LAP_CAD);

                if (currentLapIntensity.equals("ACTIVE")) {
                    activeTime = activeTime + lapTotalTimer;
                    activeDist = activeDist + lapTotalDist;
                    activeSumSpeed = activeSumSpeed + lapAvgSpeed * lapTotalTimer;
                    activeSumCad = activeSumCad + (int) (lapAvgCad * lapTotalTimer);
                }
                // Calculate REST LAP SUM & MAX
                if (currentLapIntensity.equals("REST") || currentLapIntensity.equals("RECOVERY")) {
                    restTime = restTime + lapTotalTimer;
                    restDist = restDist + lapTotalDist;
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
        recordMesg.get(0).setFieldValue(REC_DIST, 0f);
        // First SPEED to same value as second
        recordMesg.get(0).setFieldValue(REC_SPEED, recordMesg.get(1).getFieldFloatValue(REC_SPEED));
        recordMesg.get(0).setFieldValue(REC_ESPEED, recordMesg.get(1).getFieldFloatValue(REC_ESPEED));

        // Calculate ACTIVE LAPS AVG
        activeAvgSpeed = activeSumSpeed / activeTime;
        activeAvgCad = activeSumCad / activeTime;

        // Calculate REST LAPS AVG
        restAvgSpeed = restSumSpeed / restTime;
        restAvgCad = restSumCad / restTime;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void mergeLapDataFromTextFile(TextLapFile textLapFile) {
        int lapIx= 0;
        for (Mesg lap : lapMesg) {

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
                lapExtraRecords.get(lapIx-1).timeEnd = lapStartTime - 1; // 1 SEC
            }
            //System.err.println(" === lapDist: " + record.getTotalDistance() + " lapTime: " + record.getTotalTimerTime() +" speed: " + mps2kmph3(record.getEnhancedAvgSpeed()));
            lapExtraRecords.get(lapIx).setStepLen(textLapDist / ( lapCad * lapTimer / 60 )); // step length acc to FFRT
            lapExtraRecords.get(lapIx).setLevel(textLapFile.lapRecords.get(lapIx).getLevel());
            lapIx++;
        }
        // Set lapIx to last lap
        lapIx--;

        // Set Values for last lap
        lapExtraRecords.get(lapIx).setTimeEnd(timeLastRecord);

        // Set values for session
        totalDistance = textLapFile.lapRecords.get(lapIx).getDistance();
        sessionMesg.get(0).setFieldValue(SES_DIST, totalDistance);

        avgSpeed = totalDistance / totalTimerTime;
        sessionMesg.get(0).setFieldValue(SES_SPEED, avgSpeed);
        sessionMesg.get(0).setFieldValue(SES_ESPEED, avgSpeed);
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
                        case MesgNum.SPLIT:
                            splitMesg.add(mesg);
                            break;
                        case MesgNum.SPLIT_SUMMARY:
                            splitSummaryMesg.add(mesg);
                            break;
                        case MesgNum.EVENT:
                            eventMesg.add(mesg);
                            break;
                        case MesgNum.RECORD:
                            recordMesg.add(mesg);
                            break;
                        case MesgNum.DEVELOPER_DATA_ID:
                            devDataIdMesg.add(mesg);
                            break;
                        case MesgNum.FIELD_DESCRIPTION:
                            fieldDescrMesg.add(mesg);
                            break;
                        case MesgNum.SPORT:
                            sportMesg.add(mesg);
                            break;
                        default:
                            break;
                    }
                }
            });

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
            timeFirstRecord = recordMesg.get(0).getFieldLongValue(REC_TIME);

            if (activityMesg.get(0).getFieldLongValue(ACT_TIME) == null) {
                activityMesg.get(0).setFieldValue(ACT_TIME, timeFirstRecord);
            }
            activityDateTimeUTC = activityMesg.get(0).getFieldLongValue(ACT_TIME);
            if (activityMesg.get(0).getFieldLongValue(ACT_LOCTIME) == null) {
                activityMesg.get(0).setFieldValue(ACT_LOCTIME, timeFirstRecord);
            }
            activityDateTimeLocal = activityMesg.get(0).getFieldLongValue(ACT_LOCTIME);
            diffMinutesLocalUTC = (activityDateTimeLocal - activityDateTimeUTC) / 60;
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
            timeLastRecord = recordMesg.get(recordMesg.size() - 1).getFieldLongValue(REC_TIME);
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
                /* if (record.getNum() != MesgNum.SPLIT &&
                    record.getNum() != MesgNum.SPLIT_SUMMARY) { */
                if (record.getNum() != MesgNum.SPLIT) {
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
    public void changeStartTime (int changeSeconds) {

        for (Mesg mesg : allMesg) {
            int mesgNum = mesg.getNum();
            String mesgName = MesgNum.getStringFromValue(mesgNum);

            // Always shift the main timestamp (field 253)
            shiftTimeFieldIfPresent(mesg, 253, changeSeconds);

            switch (mesgNum) {
                case 140:
                    shiftTimeFieldIfPresent(mesg, 48, changeSeconds);
                    break;
                case MesgNum.FILE_ID:
                    shiftTimeFieldIfPresent(mesg, FID_CTIME, changeSeconds);
                    break;
                case MesgNum.ACTIVITY:
                    shiftTimeFieldIfPresent(mesg, ACT_LOCTIME, changeSeconds);
                    break;
                case MesgNum.SESSION:
                    shiftTimeFieldIfPresent(mesg, SES_STIME, changeSeconds);
                    break;
                case MesgNum.LAP:
                    shiftTimeFieldIfPresent(mesg, LAP_STIME, changeSeconds);
                    break;
                case MesgNum.SPLIT:
                    shiftTimeFieldIfPresent(mesg, SPL_STIME, changeSeconds);
                    shiftTimeFieldIfPresent(mesg, SPL_ETIME, changeSeconds);
                    break;
                case MesgNum.EVENT:
                    shiftTimeFieldIfPresent(mesg, EVE_STIME, changeSeconds);
                    break;
            }
        }

        // Update first and last record times for reference
        if (!recordMesg.isEmpty()) {
            Long firstRecTime = recordMesg.get(0).getFieldLongValue(REC_TIME);
            Long lastRecTime = recordMesg.get(recordMesg.size() - 1).getFieldLongValue(REC_TIME);
            if (firstRecTime != null) timeFirstRecord = firstRecTime;
            if (lastRecTime != null) timeLastRecord = lastRecTime;
        }

        if (!activityMesg.isEmpty()) {
            Long actLocalTime = activityMesg.get(0).getFieldLongValue(ACT_LOCTIME);
            if (actLocalTime != null) activityDateTimeLocal = actLocalTime;
        }
        /* timeFirstRecord = recordMesg.get(0).getFieldLongValue(REC_TIME);
        timeLastRecord = recordMesg.get(recordMesg.size() - 1).getFieldLongValue(REC_TIME);
        activityDateTimeLocal = activityMesg.get(0).getFieldLongValue(ACT_LOCTIME); */

    }
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void createFileSummaryIndoor() {
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


    }
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printFileIdInfo() {
        int i = 0;
        System.out.println();
        System.out.println("==================================================");
        System.out.println("====FileIdInfoMesg----------------------------------------------");
        for (Mesg mesg : fileIdMesg) {
            i++;
            System.out.println("File ID: " + i);

            Long timeCreated = mesg.getFieldLongValue(FID_CTIME);
            if (timeCreated != null) {
                System.out.print(" Time: " + FitDateTime.toString(timeCreated));
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
        System.out.println();
        System.out.println("==================================================");
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
        System.out.println();
        System.out.println("==================================================");
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

            Long capField = mesg.getFieldLongValue(WorkoutMesg.CapabilitiesFieldNum);

            if (capField != null) {
                System.out.print(" Capabilities: " + PehoUtils.getLabelWithValue(WorkoutCapabilities.class, capField));
            }
            /* Field capField = mesg.getField(WorkoutMesg.CapabilitiesFieldNum);
            Long raw = capField != null ? capField.getLongValue() : null;

            if (raw != null) {
                List<String> caps = decodeWorkoutCapabilities(raw);
                System.out.print(" Capabilities: " + String.join(", ", caps));
            } */

            System.out.println();
        }
        System.out.println("--------------------------------------------------");
    }
    public static List<String> decodeWorkoutCapabilities(long value) {
        List<String> result = new ArrayList<>();
        try {
            java.lang.reflect.Field mapField = WorkoutCapabilities.class.getDeclaredField("stringMap");
            mapField.setAccessible(true);
            Map<Long, String> stringMap = (Map<Long, String>) mapField.get(null);

            for (Map.Entry<Long, String> entry : stringMap.entrySet()) {
                long flag = entry.getKey();
                if ((value & flag) != 0 && flag != WorkoutCapabilities.INVALID) {
                    result.add(entry.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printWktSessionInfo() {
        int i = 0;
        System.out.println();
        System.out.println("==================================================");
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
        }
        System.out.println("--------------------------------------------------");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printWktStepInfo() {
        int i = 0;
        System.out.println();
        System.out.println("==================================================");
        System.out.println("====WktStepInfoMesg----------------------------------------------");
        for (Mesg mesg : wktStepMesg) {
            i++;
            System.out.print("WorkoutStep: " + i);

            Integer msgIx = mesg.getFieldIntegerValue(WorkoutStepMesg.MessageIndexFieldNum);
            if (msgIx != null) {
                System.out.print(" StepIx:");
                System.out.print(msgIx);
            }

            Short intensity = mesg.getFieldShortValue(WorkoutStepMesg.IntensityFieldNum);
            if (intensity != null) {
                System.out.print(" Intensity:");
                System.out.print(Intensity.getByValue(intensity) + "(" + intensity + ")");
            }

            WktStepDuration durType = WktStepDuration.getByValue(
                mesg.getFieldShortValue(WorkoutStepMesg.DurationTypeFieldNum));
            if (durType != null) {
                System.out.print("  Type:" + durType + "(" + mesg.getFieldShortValue(WorkoutStepMesg.DurationTypeFieldNum) + ")");

                Integer value = mesg.getFieldIntegerValue(WorkoutStepMesg.DurationValueFieldNum);
                if (value != null) {
                    switch (durType) {
                        case TIME:
                            System.out.print("  Tid:" + value + "sec");
                            break;
                        case DISTANCE:
                            System.out.print("  Dist:" + value + "m");
                            break;
                        case REPS:
                            System.out.print("  Reps:" + value);
                            break;
                        case REPETITION_TIME:
                            System.out.print("  RepTime:" + value);
                            break;
                        case CALORIES:
                            System.out.print("  Cal:" + value);
                            break;
                        case HR_LESS_THAN:
                            System.out.print("  HRmin:" + value + "bpm");
                            break;
                        case HR_GREATER_THAN:
                            System.out.print("  HRmax:" + value + "bpm");
                            break;
                        default:
                            break;
                    }
                }
            }
            if (mesg.getFieldShortValue(WorkoutStepMesg.TargetTypeFieldNum) != null) {
                WktStepTarget targetType = WktStepTarget.getByValue(
                    mesg.getFieldShortValue(WorkoutStepMesg.TargetTypeFieldNum));
                if (targetType != null) {
                    System.out.print("  Target:" + targetType);

                    Float value = mesg.getFieldFloatValue(WorkoutStepMesg.TargetValueFieldNum);
                    Float customTargetValueLow = mesg.getFieldFloatValue(WorkoutStepMesg.CustomTargetValueLowFieldNum);
                    Float customTargetValueHigh = mesg.getFieldFloatValue(WorkoutStepMesg.CustomTargetValueHighFieldNum);

                    if (customTargetValueLow != null && customTargetValueHigh != null) {
                        switch (targetType) {
                            case SPEED:
                                System.out.print("  Low:" + PehoUtils.mps2minpkm(customTargetValueLow) + "min/km");
                                System.out.print("  High:" + PehoUtils.mps2minpkm(customTargetValueHigh) + "min/km");
                                break;
                            case HEART_RATE:
                                System.out.print("  Low:" + Math.round(customTargetValueLow) + "bpm");
                                System.out.print("  High:" + Math.round(customTargetValueHigh) + "bpm");
                                break;
                            case CADENCE:
                                System.out.print("  Low:" + customTargetValueLow + "rpm");
                                System.out.print("  High:" + customTargetValueHigh + "rpm");
                                break;
                            case POWER:
                                System.out.print("  Low:" + customTargetValueLow + "W");
                                System.out.print("  High:" + customTargetValueHigh + "W");
                                break;
                            case GRADE:
                                System.out.print("  Low:" + customTargetValueLow + "%");
                                System.out.print("  High:" + customTargetValueHigh + "%");
                                break;
                            case RESISTANCE:
                                System.out.print("  Low:" + customTargetValueLow);
                                System.out.print("  High:" + customTargetValueHigh);
                                break;
                            case POWER_3S:
                                System.out.print("  Low:" + customTargetValueLow + "W");
                                System.out.print("  High:" + customTargetValueHigh + "W");
                                break;
                            case POWER_10S:
                                System.out.print("  Low:" + customTargetValueLow + "W");
                                System.out.print("  High:" + customTargetValueHigh + "W");
                                break;
                            case POWER_30S:
                                System.out.print("  Low:" + customTargetValueLow + "W");
                                System.out.print("  High:" + customTargetValueHigh + "W");
                                break;
                            default:
                                break;
                        }
                    }
                    if (value != null && value != 0) {
                        switch (targetType) {
                            case SPEED:
                                System.out.print("  Speed:" + value + "m/s");
                                break;
                            case HEART_RATE:
                                System.out.print("  HR:" + value + "bpm");
                                break;
                            case CADENCE:
                                System.out.print("  Cadence:" + value + "rpm");
                                break;
                            case POWER:
                                System.out.print("  Power:" + value + "W");
                                break;
                            case GRADE:
                                System.out.print("  Grade:" + value + "%");
                                break;
                            case RESISTANCE:
                                System.out.print("  Resistance:" + value);
                                break;
                            case POWER_3S:
                                System.out.print("  Power3s:" + value + "W");
                                break;
                            case POWER_10S:
                                System.out.print("  Power10s:" + value + "W");
                                break;
                            case POWER_30S:
                                System.out.print("  Power30s:" + value + "W");
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            WorkoutStepMesg stepMesg = new WorkoutStepMesg(mesg);
            Long repeatSteps = stepMesg.getRepeatSteps();
            if (repeatSteps != null) {
                System.out.print(" ");
                System.out.print(repeatSteps);
                System.out.print("x");
            Long step = stepMesg.getDurationStep();
            if (step != null) {
                System.out.print(" from step:");
                System.out.print(step+1);
            }
            }
            Integer stepName = mesg.getFieldIntegerValue(WorkoutStepMesg.WktStepNameFieldNum);
            if (stepName != null) {
                System.out.print(" Name:");
                System.out.print(stepName);
            }
            String stepNotes = mesg.getFieldStringValue(WorkoutStepMesg.NotesFieldNum);
            if (stepNotes != null) {
                System.out.print(" Notes:");
                System.out.print(stepNotes);
            }
            



            System.out.println();
        }
        System.out.println("--------------------------------------------------");
    }
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printSessionInfo () {
        int i = 0;
        System.out.println();
        System.out.println("==================================================");
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
                    System.out.print(FitDateTime.toString(startTime, diffMinutesLocalUTC));
                }
                if (timestamp != null) {
                    System.out.print(" - ");
                    System.out.print(FitDateTime.toString(timestamp, diffMinutesLocalUTC));
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
        System.out.println();
        System.out.println("==================================================");
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
        System.out.println();
        System.out.println("==================================================");
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
        System.out.println();
        System.out.println("==================================================");
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
        System.out.println();
        System.out.println("================================================");
        System.out.println("====LAPS IN FILE (lap1)");
            for (Mesg mesg : lapMesg) {
                System.out.print("Lap:" + lapNo);

                Long startTime = mesg.getFieldLongValue(LAP_STIME);
                if (startTime != null) System.out.print(" StartTime: " + FitDateTime.toString(startTime, diffMinutesLocalUTC));

                Long timestamp = mesg.getFieldLongValue(LAP_TIME);
                if (timestamp != null) System.out.print(" Timestamp: " + FitDateTime.toString(timestamp, diffMinutesLocalUTC));

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

                Long intensity = mesg.getFieldLongValue(LapMesg.IntensityFieldNum);
                if (intensity != null) System.out.print(" WktIntensity: " + PehoUtils.getLabel(Intensity.class, intensity));

                Integer wktStepIx = mesg.getFieldIntegerValue(LapMesg.WktStepIndexFieldNum);
                if (wktStepIx != null) System.out.print(" LapWktStepIx: " + wktStepIx);

                System.out.println();
                i++;
                lapNo++;
            }
        System.out.println("------------------------------------------------");
        }
        catch (FitRuntimeException e) {
            System.out.println("LAP ERROR!!!!");
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printLapRecord(int ix) {
        Mesg lapRecord = lapMesg.get(ix);
        int lapNo = ix + 1;
        System.out.print("Lap:" + lapNo);

        // Level from extra records
        // if (lapExtraRecords.get(i).level != null) {
        //     System.out.print(" lv" + lapExtraRecords.get(i).level);
        // }

        // Timer
        Float totalTimer = lapRecord.getFieldFloatValue(LAP_TIMER);
        if (totalTimer != null) System.out.print(" " + PehoUtils.sec2minSecShort(totalTimer) + "min");

        // Distance
        Float lapDist = lapRecord.getFieldFloatValue(LAP_DIST);
        if (lapDist != null) System.out.print(" " + PehoUtils.m2km2(lapDist) + "km");

        // DistFrom / DistTo from secRecords
        //System.out.print(" DistFrom: " + secRecords.get(lapExtraRecords.get(i).recordIxStart).getDistance());
        //System.out.print(" DistTo: " + secRecords.get(lapExtraRecords.get(i).recordIxEnd).getDistance());

        // Enhanced average speed
        Float enhAvgSpeed = lapRecord.getFieldFloatValue(LAP_ESPEED);
        if (enhAvgSpeed != null) System.out.print(" " + PehoUtils.mps2minpkm(enhAvgSpeed)+ "min/km");

        // Cadence
        Short avgCadence = lapRecord.getFieldShortValue(LAP_CAD);
        if (avgCadence != null) System.out.print(" " + avgCadence + "spm");

        // Intensity
        Short intensityRaw = lapRecord.getFieldShortValue(LAP_INTENSITY);
        if (intensityRaw != null) {
            Intensity intensityEnum = Intensity.getByValue(intensityRaw.shortValue());
            String intensityLabel = intensityEnum != null ? Intensity.getStringFromValue(intensityEnum) : "unknown";
            System.out.print(" WktIntensity:" + intensityLabel);
        }

        // Workout Step Index
        Integer wktStepIx = lapRecord.getFieldIntegerValue(LAP_WKT_STEP_IDX);
        if (wktStepIx != null) System.out.print(" WktStepIx:" + wktStepIx);

        // Start Time
        // Long startTime = lapRecord.getFieldLongValue(LAP_STIME);
        // if (startTime != null) {
        //     System.out.print(" start@:" + PehoUtils.sec2minSecLong(findTimerBasedOnTime(startTime)) + ", " + FitDateTime.toString(startTime, diffMinutesLocalUTC));
        // }

        // Start Timer WRONG VALUE!!!!!
        Long startTimer = lapRecord.getFieldLongValue(LAP_TIMER);
        if (startTimer != null) {
            //System.out.print(" " + FitDateTime.toTimerString(startTimer));
        }

        /* // Timestamp
        Long timestamp = mesg.getFieldLongValue(LAP_TIME);
        if (timestamp != null) {
            System.out.print(" Timestamp: " + FitDateTime.toString(timestamp, diffMinutesLocalUTC));
        } */

        /*/ Extra record fields
        if (lapExtraRecords.get(i).timeEnd != null) System.out.print(" TimeEnd: " + lapExtraRecords.get(i).timeEnd);
        if (lapExtraRecords.get(i).stepLen != null) System.out.print(" StepLen: " + lapExtraRecords.get(i).stepLen);
        if (lapExtraRecords.get(i).hrStart != 0) System.out.print(" hrStart: " + lapExtraRecords.get(i).hrStart);
        if (lapExtraRecords.get(i).hrEnd != 0) System.out.print(" hrEnd: " + lapExtraRecords.get(i).hrEnd);
        if (lapExtraRecords.get(i).recordIxEnd != 0) System.out.print(" recordIxEnd: " + lapExtraRecords.get(i).recordIxEnd);
        if (lapExtraRecords.get(i).hrMin != 0) System.out.print(" hrMin: " + lapExtraRecords.get(i).hrMin);
        */
        System.out.println();
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printLapRecords() {
        int ix = 0;

        try {
        System.out.println();
        System.out.println("================================================");
        System.out.println("====LAPS IN FILE (lap2)");

            for (Mesg mesg : lapMesg) {
                printLapRecord(ix);
                ix++;
            }

        System.out.println("------------------------------------------------");
        } catch (FitRuntimeException e) {
            System.out.println("LAP ERROR!!!!");
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printLapAvgMaxSpeed (Float avgSpeed, Float maxSpeed) {
        if (avgSpeed != null) {
            if (getIsSkiErg()) {
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
            if (getIsSkiErg()) {
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
            if (getIsSkiErg()) {
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
        if (getIsSkiErg()) {
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
        System.out.println();
        System.out.println("================================================");
        System.out.println("====LAPS IN FILE (lap4-LapAllSummary)");
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
            Short intensityVal = (Short) mesg.getFieldValue(LAP_INTENSITY);
            String intensity = intensityVal != null ? Intensity.getStringFromValue(Intensity.getByValue(intensityVal)) : "UNKNOWN";

            System.out.print(" WktIntensity: " + intensity);

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
            Float enhMaxSpeed = mesg.getFieldFloatValue(LAP_EMSPEED);
            printLapAvgMaxSpeed(enhAvgSpeed, enhMaxSpeed);

            // Cadence
            Short avgCadence = mesg.getFieldShortValue(LAP_CAD);
            Short maxCadence = mesg.getFieldShortValue(LAP_MCAD);
            if (avgCadence != null) {
                System.out.print("--Cad avg:" + avgCadence);
                System.out.print(" max:" + (maxCadence != null ? maxCadence : "N/A"));
            }

            // Power
            Integer avgPower = mesg.getFieldIntegerValue(LAP_POW);
            Integer maxPower = mesg.getFieldIntegerValue(LAP_MPOW);
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
        System.out.println();
        System.out.println("================================================");
        System.out.println("====LAPS IN FILE (lap3-LapLongSummary)");
        System.out.println("---- ACTIVE LAPS ----");
        int i = 0;
        int lapNo = 1;

        // ACTIVE laps
        for (Mesg mesg : lapMesg) {
            Short intensityVal = (Short) mesg.getFieldValue(LAP_INTENSITY);
            String intensity = intensityVal != null ? Intensity.getStringFromValue(Intensity.getByValue(intensityVal)) : "UNKNOWN";

            if ("ACTIVE".equals(intensity)) {
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
                Float enhMaxSpeed = mesg.getFieldFloatValue(LAP_EMSPEED);
                printLapAvgMaxSpeed(enhAvgSpeed, enhMaxSpeed);

                Short avgCadence = mesg.getFieldShortValue(LAP_CAD);
                Short maxCadence = mesg.getFieldShortValue(LAP_MCAD);
                if (avgCadence != null) {
                    System.out.print("--Cad avg:" + avgCadence);
                    System.out.print(" max:" + (maxCadence != null ? maxCadence : "N/A"));
                }

                Integer avgPower = mesg.getFieldIntegerValue(LAP_POW);
                Integer maxPower = mesg.getFieldIntegerValue(LAP_MPOW);
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
                Short intensityVal = (Short) mesg.getFieldValue(LAP_INTENSITY);
                String intensity = intensityVal != null ? Intensity.getStringFromValue(Intensity.getByValue(intensityVal)) : "UNKNOWN";

            if ("REST".equals(intensity) || "RECOVERY".equals(intensity)) {
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
                Float enhMaxSpeed = mesg.getFieldFloatValue(LAP_EMSPEED);
                printLapAvgMaxSpeed(enhAvgSpeed, enhMaxSpeed);

                Short avgCadence = mesg.getFieldShortValue(LAP_CAD);
                Short maxCadence = mesg.getFieldShortValue(LAP_MCAD);
                if (avgCadence != null) {
                    System.out.print("--Cad avg:" + avgCadence);
                    System.out.print(" max:" + (maxCadence != null ? maxCadence : "N/A"));
                }

                Integer avgPower = mesg.getFieldIntegerValue(LAP_POW);
                Integer maxPower = mesg.getFieldIntegerValue(LAP_MPOW);
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
    public void printWriteLapSummery (String filename) {
        
        try {
            savedStrLapsActiveInfoShort += "---- ACTIVE LAPS ----" + System.lineSeparator();
            int i = 0;
            int lapNo = 1;
            for (Mesg record : lapMesg) { // Generic Mesg type
                Short intensityVal = record.getFieldShortValue(LAP_INTENSITY);
                String intensity = intensityVal != null ? Intensity.getStringFromValue(Intensity.getByValue(intensityVal)) : "";

                if ("ACTIVE".equals(intensity)) {
                    savedStrLapsActiveInfoShort += "Lap" + lapNo;

                    if (lapExtraRecords.get(i).level != null && !getIsSkiErg()) {
                        if (getIsTreadmill()) {
                            savedStrLapsActiveInfoShort += " " + lapExtraRecords.get(i).level.intValue() + "%";
                        } else {
                            savedStrLapsActiveInfoShort += " lv" + lapExtraRecords.get(i).level.intValue();
                        }
                    }

                    Integer hrMin = 0;
                    if (i > 0) {
                        hrMin = lapExtraRecords.get(i - 1).hrMin;
                        savedStrLapsActiveInfoShort += " HRmin" + hrMin;
                    } else {
                        savedStrLapsActiveInfoShort += " HR";
                    }

                    Integer hrStart = lapExtraRecords.get(i).hrStart;
                    savedStrLapsActiveInfoShort += ">st" + hrStart;
                    if ((hrStart - hrMin) > 20) {
                        hrMin = hrStart;;
                    }

                    Integer maxHr = record.getFieldIntegerValue(LAP_MHR);
                    if (maxHr != null) {
                        savedStrLapsActiveInfoShort += "+" + (maxHr - hrMin);
                        savedStrLapsActiveInfoShort += "->max" + maxHr;
                    }

                    savedStrLapsActiveInfoShort += " end" + lapExtraRecords.get(i).hrEnd;

                    Float totalTime = record.getFieldFloatValue(LAP_TIMER);
                    if (totalTime != null) {
                        savedStrLapsActiveInfoShort += " " + PehoUtils.sec2minSecShort(totalTime) + "min";
                    }

                    Short avgCad = record.getFieldShortValue(LAP_CAD);
                    if (avgCad != null) {
                        savedStrLapsActiveInfoShort += " " + avgCad + "spm";
                    }

                    Float avgSpeed = record.getFieldFloatValue(LAP_ESPEED);
                    if (avgSpeed != null) {
                        if (getIsSkiErg()) {
                            savedStrLapsActiveInfoShort += " " + PehoUtils.sec2minSecLong(500 / avgSpeed) + "min/500m";
                        } else {
                            savedStrLapsActiveInfoShort += " " + PehoUtils.sec2minSecLong(1000 / avgSpeed) + "min/km";
                            savedStrLapsActiveInfoShort += " " + String.format("%.1fkm/h", avgSpeed * 3.60);
                        }
                    }

                    Integer avgPower = record.getFieldIntegerValue(LAP_POW);
                    if (avgPower != null) {
                        savedStrLapsActiveInfoShort += " " + avgPower + "W";
                    }

                    Double dist = record.getFieldDoubleValue(LAP_DIST);
                    if (dist != null) {
                        savedStrLapsActiveInfoShort += " " + String.format("%.1fkm", dist / 1000);
                    }

                    if (lapExtraRecords.get(i).avgDragFactor != null && getIsSkiErg()) {
                        savedStrLapsActiveInfoShort += " df" + Math.round(lapExtraRecords.get(i).avgDragFactor);
                    }
                    if (lapExtraRecords.get(i).avgStrokeLen != null && getIsSkiErg()) {
                        savedStrLapsActiveInfoShort += " sl" + lapExtraRecords.get(i).avgStrokeLen;
                    }
                    if (lapExtraRecords.get(i).stepLen != null && !getIsSkiErg()) {
                        savedStrLapsActiveInfoShort += " step" + (int) (lapExtraRecords.get(i).stepLen * 100) + "cm";
                    }

                    savedStrLapsActiveInfoShort += System.lineSeparator();
                }
                i++;
                lapNo++;
            }

            savedStrLapsActiveInfoShort += lapEndSum2String(activeAvgCad, activeAvgSpeed, activeAvgPower, activeDist);

            // ================= REST LAPS =================
            savedStrLapsRestInfoShort += "---- REST LAPS ----" + System.lineSeparator();
            i = 0;
            lapNo = 1;
            for (Mesg record : lapMesg) {
                Short intensityVal = record.getFieldShortValue(LAP_INTENSITY);
                String intensity = intensityVal != null ? Intensity.getStringFromValue(Intensity.getByValue(intensityVal)) : "";

                if ("REST".equals(intensity) || "RECOVERY".equals(intensity)) {
                    savedStrLapsRestInfoShort += "Lap" + lapNo;

                    if (lapExtraRecords.get(i).level != null && !getIsSkiErg()) {
                        if (getIsTreadmill()) {
                            savedStrLapsRestInfoShort += " " + lapExtraRecords.get(i).level.intValue() + "%";
                        } else {
                            savedStrLapsRestInfoShort += " lv" + lapExtraRecords.get(i).level.intValue();
                        }
                    }

                    savedStrLapsRestInfoShort += " HRst" + lapExtraRecords.get(i).hrStart;

                    Integer maxHr = record.getFieldIntegerValue(LAP_MHR);
                    if (maxHr != null) {
                        savedStrLapsRestInfoShort += ">max" + maxHr;
                        savedStrLapsRestInfoShort += "" + (lapExtraRecords.get(i).hrMin - maxHr);
                        savedStrLapsRestInfoShort += "->min" + lapExtraRecords.get(i).hrMin;
                    }

                    savedStrLapsRestInfoShort += " end" + lapExtraRecords.get(i).hrEnd;

                    Float totalTime = record.getFieldFloatValue(LAP_TIMER);
                    if (totalTime != null) {
                        savedStrLapsRestInfoShort += " " + PehoUtils.sec2minSecShort(totalTime) + "min";
                    }

                    Short avgCad = record.getFieldShortValue(LAP_CAD);
                    if (avgCad != null) {
                        savedStrLapsRestInfoShort += " " + avgCad + "spm";
                    }

                    Float avgSpeed = record.getFieldFloatValue(LAP_ESPEED);
                    if (avgSpeed != null) {
                        if (getIsSkiErg()) {
                            savedStrLapsRestInfoShort += " " + PehoUtils.sec2minSecLong(500 / avgSpeed) + "min/500m";
                        } else {
                            savedStrLapsRestInfoShort += " " + PehoUtils.sec2minSecLong(1000 / avgSpeed) + "min/km";
                            savedStrLapsRestInfoShort += " " + String.format("%.1fkm/h", avgSpeed * 3.60);
                        }
                    }

                    Integer avgPower = record.getFieldIntegerValue(LAP_POW);
                    if (avgPower != null) {
                        savedStrLapsRestInfoShort += " " + avgPower + "W";
                    }

                    Double dist = record.getFieldDoubleValue(LAP_DIST);
                    if (dist != null) {
                        savedStrLapsRestInfoShort += " " + String.format("%.1fkm", dist / 1000);
                    }

                    if (lapExtraRecords.get(i).stepLen != null && !getIsSkiErg()) {
                        savedStrLapsRestInfoShort += " step" + (int) (lapExtraRecords.get(i).stepLen * 100) + "cm";
                    }

                    savedStrLapsRestInfoShort += System.lineSeparator();
                }
                i++;
                lapNo++;
            }

            savedStrLapsRestInfoShort += lapEndSum2String(restAvgCad, restAvgSpeed, restAvgPower, restDist);

        } catch (FitRuntimeException e) {
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
            System.out.print(FitDateTime.toString(recordMesg.get(i).getFieldLongValue(REC_TIME)));
            System.out.print(((recordMesg.get(i).getFieldLongValue(REC_LAT))));
            System.out.print(((recordMesg.get(i).getFieldLongValue(REC_LON))));
            System.out.println();
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    /* public void printSecRecords0 () {
        int i = 0;
        System.out.println("--------------------------------------------------");
        for (RecordMesg record : secRecords) {
            i++;
            if (i<11 || i>numberOfRecords-10 || i>3012 && i<3020) {
                System.out.print("Record:" + i);
                if (record.getTimestamp() != null) {
                    System.out.print(" Timestamp: " + record.getTimestamp());
                }
                // if (lapRecords.get(0).getStartTime() != null) {
                //     System.out.print(" LapStartTime: " + lapRecords.get(0).getStartTime());
                // }
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
                    // for (int j = 1; j < field.getNumValues(); j++) {
                    //     System.out.print(", " + field.getValue(j));
                    // }
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
    } */
    //=============================================================================
public void printSecRecords2() {
    System.out.println("-----printSecRecords2---------------------------------------------");
    for (int i = 0; i < recordMesg.size(); i++) {
        Mesg rec = recordMesg.get(i);
        if (i < 11 || i > numberOfRecords - 10 || (i > 3012 && i < 3020)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Record:").append(i);

            // Timestamp
            Long ts = rec.getFieldLongValue(REC_TIME);
            if (ts != null) sb.append(" Timestamp:").append(ts);

            // Heart rate
            appendIfNotNull(sb, " HR:", rec.getFieldIntegerValue(REC_HR));

            // Speed / enhanced speed
            appendIfNotNull(sb, " Speed:", rec.getFieldFloatValue(REC_SPEED));
            appendIfNotNull(sb, " EnhSp:", rec.getFieldFloatValue(REC_ESPEED));

            // Distance
            appendIfNotNull(sb, " Dist:", rec.getFieldFloatValue(REC_DIST));

            // Cadence
            appendIfNotNull(sb, " Cad:", rec.getFieldIntegerValue(REC_CAD));

            // Power
            appendIfNotNull(sb, " Pow:", rec.getFieldIntegerValue(REC_POW));

            // Extra data (your custom structure)
            if (secExtraRecords.get(i).C2DateTime != null) {
                sb.append(" C2time:").append(secExtraRecords.get(i).C2DateTime);
            }

            // Position (lat/lon)
            Integer lat = rec.getFieldIntegerValue(REC_LAT);
            Integer lon = rec.getFieldIntegerValue(REC_LON);
            if (lat != null && lon != null) {
                sb.append(" Position:(").append(lat).append(", ").append(lon).append(")");
            }

            // Developer fields
            sb.append(" DEV:");
            for (DeveloperField field : rec.getDeveloperFields()) {
                sb.append(" ").append(field.getName()).append(":");
                for (int j = 0; j < field.getNumValues(); j++) {
                    sb.append(field.getValue(j));
                    if (j < field.getNumValues() - 1) sb.append(",");
                }
            }

            System.out.println(sb);
        }
    }
    System.out.println("---end-----------------------------------------------");
}

// ==============================================================================
// Utility methods for printing and formatting lap and record summaries
// ==============================================================================

public void printLapAllSummaryAllMesg2() {
    System.out.println("---printLapAllSummary-----------------------------------------------");

    int lapNo = 1;
    for (int i = 0; i < lapMesg.size(); i++) {
        Mesg lap = lapMesg.get(i);
        var extra = lapExtraRecords.get(i);

        StringBuilder sb = new StringBuilder();
        sb.append("Lap:").append(lapNo);

        // Start time
        Long startTime = lap.getFieldLongValue(LAP_STIME);
        if (startTime != null) {
            sb.append(" StartTime:")
              .append(FitDateTime.toString(startTime, diffMinutesLocalUTC));
        }

        // Level and step length (extra)
        if (extra.level != null && !getIsSkiErg()) {
            sb.append(" lv").append(extra.level.intValue());
        }
        if (extra.stepLen != null && !getIsSkiErg()) {
            sb.append(" steplen").append((int)(extra.stepLen * 100)).append("cm");
        }

        // Timer
        appendIfNotNull(sb, " LapTime:", lap.getFieldFloatValue(LAP_TIMER));

        // Intensity and heart rate summary
        Short intensityVal = lap.getFieldShortValue(LAP_INTENSITY);
        if (intensityVal != null) {
            String intensityStr = Intensity.getStringFromValue(Intensity.getByValue(intensityVal));
            sb.append(" WktIntensity:").append(intensityStr);

            boolean active = intensityStr.equals("ACTIVE") || intensityStr.equals("WARMUP");
            int hrMax = safeInt(lap.getFieldIntegerValue(LAP_MHR));
            int hrMin = safeInt(extra.hrMin);
            int hrStart = safeInt(extra.hrStart);
            int hrEnd = safeInt(extra.hrEnd);

            if (active) {
                sb.append(String.format(" HR start:%d min:%d +%d -->max:%d end:%d",
                        hrStart, hrMin, hrMax - hrMin, hrMax, hrEnd));
            } else {
                sb.append(String.format(" HR start:%d max:%d %d -->min:%d end:%d",
                        hrStart, hrMax, hrMin - hrMax, hrMin, hrEnd));
            }
        }

        // Distance
        appendIfNotNull(sb, "--Dist:", lap.getFieldFloatValue(LAP_DIST));

        // Speed
        Float avgSpeed = lap.getFieldFloatValue(LAP_SPEED);
        Float avgESpeed = lap.getFieldFloatValue(LAP_ESPEED);
        Float maxSpeed = lap.getFieldFloatValue(LAP_EMSPEED);
        sb.append("Speed: avg:" + avgSpeed + " avgE:" + avgESpeed + " max:" + maxSpeed);
        //printLapAvgMaxSpeed(avgSpeed, maxSpeed);

        // Cadence
        appendIfBothNotNull(sb, "--Cad avg:", lap.getFieldShortValue(LAP_CAD),
                " max:", lap.getFieldShortValue(LAP_MCAD));

        // Power
        appendIfBothNotNull(sb, "--Pow avg:", lap.getFieldIntegerValue(LAP_POW),
                " max:", lap.getFieldIntegerValue(LAP_MPOW));

        // Drag factor & stroke length
        if (extra.avgDragFactor != null) {
            sb.append(String.format("--DFavg:%d max:%d",
                    Math.round(extra.avgDragFactor), Math.round(extra.maxDragFactor)));
        }
        if (extra.avgStrokeLen != null) {
            sb.append(String.format("--SLavg:%.2f max:%.2f",
                    extra.avgStrokeLen, extra.maxStrokeLen));
        }

        System.out.println(sb);
        lapNo++;
    }

    System.out.println("--------------------------------------------------");
}

private void appendIfNotNull(StringBuilder sb, String label, Object value) {
    if (value != null) sb.append(label).append(value);
}

private void appendIfBothNotNull(StringBuilder sb, String label1, Object val1, String label2, Object val2) {
    if (val1 != null) sb.append(label1).append(val1);
    if (val2 != null) sb.append(label2).append(val2);
}

private int safeInt(Number n) {
    return n == null ? 0 : n.intValue();
}
//=============================================================================
// Debug method to print lap and record details for verification
//=============================================================================
public void debugLapRecords(List<Mesg> lapMesgs, List<Mesg> recordMesgs) {
    System.out.println("-------------------------------------------");
    System.out.println("----- L A P   R E C O R D   D E B U G -----");
    System.out.printf("Laps: %d  Records: %d%n%n", lapMesgs.size(), recordMesgs.size());

    for (int i = 0; i < lapMesgs.size(); i++) {
        Mesg lap = lapMesgs.get(i);

        // --- basic lap info
        Long startTimeL = getLongField(lap, "start_time", null);
        Long timestampL = getLongField(lap, "timestamp", null);
        if (startTimeL == null) startTimeL = timestampL;
        if (startTimeL == null) {
            System.out.printf("%n---- LAP %d ---- (no start_time, skipping)%n", i + 1);
            continue;
        }

        Float totalElapsed = getFloatField(lap, "total_elapsed_time", null);
        Float totalTimer = getFloatField(lap, "total_timer_time", null);

        Long endTimeL = null;
        if (totalElapsed != null && totalElapsed > 0f) {
            endTimeL = startTimeL + Math.round(totalElapsed);
        } else if (totalTimer != null && totalTimer > 0f) {
            endTimeL = startTimeL + Math.round(totalTimer);
        } else if (i + 1 < lapMesgs.size()) {
            Long nextStart = getLongField(lapMesgs.get(i + 1), "start_time", null);
            if (nextStart == null)
                nextStart = getLongField(lapMesgs.get(i + 1), "timestamp", null);
            if (nextStart != null && nextStart > startTimeL) endTimeL = nextStart;
        }

        // fallback: find last record after lap start
        if (endTimeL == null) {
            Long lastAfter = null;
            for (Mesg r : recordMesgs) {
                Long rts = getLongField(r, "timestamp", null);
                if (rts != null && rts >= startTimeL) lastAfter = rts;
            }
            if (lastAfter != null && lastAfter > startTimeL) endTimeL = lastAfter;
        }

        if (endTimeL == null) endTimeL = startTimeL + 1;
        endTimeL++; // +1s inclusive

        long lapStart = startTimeL;
        long lapEnd = endTimeL;

        double lapDist = getFloatField(lap, "total_distance", 0f);
        double lapAvgSpd = getFloatField(lap, "avg_speed", 0f);

        long messageIndexL = getLongField(lap, "message_index", 0L);
        long eventL = getLongField(lap, "event", 0L);
        long eventTypeL = getLongField(lap, "event_type", 0L);
        long lapTriggerL = getLongField(lap, "lap_trigger", 0L);

        System.out.printf("---- LAP %d ----%n", i + 1);
        System.out.printf(
            "Start: %d  End: %d  Dur: %.1fs  LapMesg Dist: %.2fm  LapMesg AvgSpd: %.3f m/s%n",
            lapStart, lapEnd, (double) (lapEnd - lapStart), lapDist, lapAvgSpd);
        System.out.printf(
            "message_index=%d  event=%d  event_type=%d  lap_trigger=%d%n",
            messageIndexL, eventL, eventTypeL, lapTriggerL);

        // --- Records immediately after lap start
        System.out.println("10 Records after lap start:");
        int count = 0;
        for (Mesg r : recordMesgs) {
            Long ts = getLongField(r, "timestamp", null);
            if (ts != null && ts >= lapStart && ts < lapStart + 10) {
                printRecord(r, lapStart);
                count++;
                if (count >= 10) break;
            }
        }

        // --- Records immediately before lap end
        System.out.println("10 Records before lap end:");
        count = 0;
        for (Mesg r : recordMesgs) {
            Long ts = getLongField(r, "timestamp", null);
            if (ts != null && ts >= lapEnd - 10 && ts <= lapEnd) {
                printRecord(r, lapStart);
                count++;
                if (count >= 10) break;
            }
        }

        // --- Compute totals from records within the lap
        List<Mesg> lapRecords = new ArrayList<>();
        for (Mesg r : recordMesgs) {
            Long ts = getLongField(r, "timestamp", null);
            if (ts != null && ts >= lapStart && ts <= lapEnd) {
                lapRecords.add(r);
            }
        }

        double firstDist = lapRecords.isEmpty() ? 0.0 : getFloatField(lapRecords.get(0), "distance", 0f);
        double lastDist = lapRecords.isEmpty() ? 0.0 : getFloatField(lapRecords.get(lapRecords.size() - 1), "distance", 0f);
        double distDelta = lastDist - firstDist;
        double timeDelta = (double) (lapEnd - lapStart);
        double avgSpeed = timeDelta > 0 ? distDelta / timeDelta : 0.0;

        System.out.printf(
            "=> Records total: Dist=%.2fm  Time=%.1fs  AvgSpeed=%.3f m/s%n",
            distDelta, timeDelta, avgSpeed);
        System.out.printf(
            "Compare LapMesg vs Records: LapMesgDist=%.2f  RecordsDist=%.2f  LapMesgAvgSpd=%.3f  RecAvgSpd=%.3f%n%n",
            lapDist, distDelta, lapAvgSpd, avgSpeed);
    }
}

private static void printRecord(Mesg r, long lapStart) {
    Long ts = getLongField(r, "timestamp", null);
    double dist = getFloatField(r, "distance", 0f);
    double spd = getFloatField(r, "speed", 0f);
    double enhSpd = getFloatField(r, "enhanced_speed", 0f);
    if (ts == null) return;
    System.out.printf(
        "  ?t=%6ds  ts=%d  dist=%.2f  spd=%.3f  enhSpd=%.3f%n",
        (ts - lapStart), ts, dist, spd, enhSpd);
}

// safe field helpers
private static Long getLongField(Mesg m, String name, Long defVal) {
    if (m == null) return defVal;
    try {
        Long v = m.getFieldLongValue(name);
        return v != null ? v : defVal;
    } catch (Exception e) { return defVal; }
}

private static Float getFloatField(Mesg m, String name, Float defVal) {
    if (m == null) return defVal;
    try {
        Float v = m.getFieldFloatValue(name);
        return v != null ? v : defVal;
    } catch (Exception e) { return defVal; }
}

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printSplitRecords() {
        int i = 0;
        System.out.println();
        System.out.println("==================================================");
        System.out.println("SPLITS IN FILE");
        System.out.println(" File  between " + FitDateTime.toString(timeFirstRecord,diffMinutesLocalUTC) + " >>>> " + FitDateTime.toString(timeLastRecord,diffMinutesLocalUTC));
        System.out.println(String.format(" TotalTime:%1$smin Dist:%2$skm", PehoUtils.sec2minSecLong(totalTimerTime), PehoUtils.m2km2(totalDistance)));
        System.out.println("--------------------------------------------------");
        for (Mesg mesg : splitMesg) {
            // Display split number as one-based (i + 1) for user clarity
            System.out.print("No:" + (i + 1));

            Short splitType = mesg.getFieldShortValue(SPL_TYPE);
            if (splitType != null) {
                if (splitType != null) System.out.print(" Type:" + SplitType.getByValue(splitType));
            }

            Long startTime = mesg.getFieldLongValue(SPL_STIME);
            if (startTime != null) System.out.print(" Time:" + FitDateTime.toString(startTime, diffMinutesLocalUTC));

            Long endTime = mesg.getFieldLongValue(SPL_ETIME);
            if (endTime != null) System.out.print("->" + FitDateTime.toString(endTime, diffMinutesLocalUTC));

            Float totalTimer = mesg.getFieldFloatValue(SPL_TIMER);
            if (totalTimer != null)  System.out.print(" SplTime:" + PehoUtils.sec2minSecLong(totalTimer) + "min");

            Float totalDistance = mesg.getFieldFloatValue(SPL_DIST);
            if (totalDistance != null) System.out.print(" Dist:" + PehoUtils.m2km2(totalDistance) + "km");

            Float avgPace = mesg.getFieldFloatValue(SPL_SPEED);
            if (avgPace != null) System.out.print(" AvgPace:" + PehoUtils.mps2minpkm(avgPace));

            Float maxPace = mesg.getFieldFloatValue(SPL_MSPEED);
            if (maxPace != null) System.out.print(" MaxPace:" + PehoUtils.mps2minpkm(maxPace));

            Integer ascent = mesg.getFieldIntegerValue(SPL_ASC);
            if (ascent != null) System.out.print(" Asc:" + ascent + "m");

            Integer descent = mesg.getFieldIntegerValue(SPL_DESC);
            if (descent != null) System.out.print(" Desc:" + descent + "m");

            /* Integer startLat = mesg.getFieldIntegerValue(SPL_START_LAT);
            Integer startLon = mesg.getFieldIntegerValue(SPL_START_LON);
            Integer endLat = mesg.getFieldIntegerValue(SPL_END_LAT);
            Integer endLon = mesg.getFieldIntegerValue(SPL_END_LON);
            if (startLat != null && startLon != null && endLat != null && endLon != null) {
                System.out.print(" StartPos: " + startLat + "/" + startLon);
                System.out.print(" EndPos: " + endLat + "/" + endLon);
            } */

            /* Float vertSpeed = mesg.getFieldFloatValue(SPL_AVG_VERT_SPEED);
            if (vertSpeed != null) System.out.print(" AvgVertSpeed: " + vertSpeed + " m/s"); */

            Integer startElev = mesg.getFieldIntegerValue(SPL_SELE);
            if (startElev != null) System.out.print(" StartEle: " + startElev + "m");

            Float movingTime = mesg.getFieldFloatValue(SPL_MTIMER);
            if (movingTime != null) System.out.print(" MovingTime: " + PehoUtils.sec2minSecShort(movingTime));

            System.out.println();
            i++;
        }
        System.out.println("---- END SPLITS ----");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printSplitSumRecords() {
        int i = 0;
        System.out.println();
        System.out.println("==================================================");
        System.out.println("SPLIT SUMMARIES IN FILE");
        System.out.println(" File  between " + FitDateTime.toString(timeFirstRecord,diffMinutesLocalUTC) + " >>>> " + FitDateTime.toString(timeLastRecord,diffMinutesLocalUTC));
        System.out.println(String.format(" TotalTime:%1$smin Dist:%2$skm", PehoUtils.sec2minSecLong(totalTimerTime), PehoUtils.m2km2(totalDistance)));
        System.out.println("--------------------------------------------------");
        for (Mesg mesg : splitSummaryMesg) {
            // Display split number as one-based (i + 1) for user clarity
            System.out.print("No:" + (i + 1));

            Short splitType = mesg.getFieldShortValue(SPLSUM_TYPE);
            if (splitType != null) {
                if (splitType != null) System.out.print(" Type:" + SplitType.getByValue(splitType));
            }

            Float totalTimer = mesg.getFieldFloatValue(SPLSUM_TIMER);
            if (totalTimer != null)  System.out.print(" SplTime:" + PehoUtils.sec2minSecLong(totalTimer) + "min");

            Float totalDistance = mesg.getFieldFloatValue(SPLSUM_DIST);
            if (totalDistance != null) System.out.print(" Dist:" + PehoUtils.m2km2(totalDistance) + "km");

            Float avgPace = mesg.getFieldFloatValue(SPLSUM_SPEED);
            if (avgPace != null) System.out.print(" AvgPace:" + PehoUtils.mps2minpkm(avgPace));

            Float maxPace = mesg.getFieldFloatValue(SPLSUM_MSPEED);
            if (maxPace != null) System.out.print(" MaxPace:" + PehoUtils.mps2minpkm(maxPace));

            Integer ascent = mesg.getFieldIntegerValue(SPLSUM_ASC);
            if (ascent != null) System.out.print(" Asc:" + ascent + "m");

            Integer descent = mesg.getFieldIntegerValue(SPL_DESC);
            if (descent != null) System.out.print(" Desc:" + descent + "m");

            Float vertSpeed = mesg.getFieldFloatValue(SPL_VSPEED);
            if (vertSpeed != null) System.out.print(" AvgVertSpeed: " + vertSpeed + " m/s");

            Float movingTime = mesg.getFieldFloatValue(SPLSUM_MTIMER);
            if (movingTime != null) System.out.print(" MovingTime: " + PehoUtils.sec2minSecShort(movingTime));

            System.out.println();
            i++;
        }
        System.out.println("---- END SPLITS ----");
    }


}