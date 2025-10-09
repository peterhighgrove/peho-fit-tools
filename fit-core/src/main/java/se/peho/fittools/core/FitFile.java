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
import java.util.Collections;
import java.text.SimpleDateFormat;
import java.time.Duration;

//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
public class FitFile {

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
    public static final int SPL_MESSAGE_INDEX = SplitMesg.MessageIndexFieldNum; // int
    public static final int SPL_TYPE = SplitMesg.SplitTypeFieldNum; // enum
    public static final int SPL_TOTAL_ELAPSED_TIME = SplitMesg.TotalElapsedTimeFieldNum; // float
    public static final int SPL_TOTAL_TIMER = SplitMesg.TotalTimerTimeFieldNum; // float
    public static final int SPL_TOTAL_DISTANCE = SplitMesg.TotalDistanceFieldNum; // float
    public static final int SPL_AVG_SPEED = SplitMesg.AvgSpeedFieldNum; // float
    public static final int SPL_START_TIME = SplitMesg.StartTimeFieldNum; // long
    public static final int SPL_TOTAL_ASCENT = SplitMesg.TotalAscentFieldNum; // int
    public static final int SPL_TOTAL_DESCENT = SplitMesg.TotalDescentFieldNum; // int
    public static final int SPL_START_LAT = SplitMesg.StartPositionLatFieldNum; // int (semicircles)
    public static final int SPL_START_LON = SplitMesg.StartPositionLongFieldNum; // int (semicircles)
    public static final int SPL_END_LAT = SplitMesg.EndPositionLatFieldNum; // int (semicircles)
    public static final int SPL_END_LON = SplitMesg.EndPositionLongFieldNum; // int (semicircles)
    public static final int SPL_MAX_SPEED = SplitMesg.MaxSpeedFieldNum; // float
    public static final int SPL_AVG_VERT_SPEED = SplitMesg.AvgVertSpeedFieldNum; // float
    public static final int SPL_END_TIME = SplitMesg.EndTimeFieldNum; // long
    public static final int SPL_TOTAL_CALORIES = SplitMesg.TotalCaloriesFieldNum; // int
    public static final int SPL_START_ELEVATION = SplitMesg.StartElevationFieldNum; // int
    public static final int SPL_TOTAL_MOVING_TIME = SplitMesg.TotalMovingTimeFieldNum; // float
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
    public static final int REC_TIME = RecordMesg.TimestampFieldNum; //long
    public static final int REC_DIST = RecordMesg.DistanceFieldNum; //float
    public static final int REC_HR = RecordMesg.HeartRateFieldNum; //int
    public static final int REC_SPEED = RecordMesg.SpeedFieldNum; //float
    public static final int REC_ESPEED = RecordMesg.EnhancedSpeedFieldNum; //float
    public static final int REC_POW = RecordMesg.PowerFieldNum; //int
    public static final int REC_LAT = RecordMesg.PositionLatFieldNum; //int
    public static final int REC_LON = RecordMesg.PositionLongFieldNum; //int
    public static final int REC_EALT = RecordMesg.EnhancedAltitudeFieldNum; //float

    private String manufacturer;
    private int productNo;
    private String product = "";
    private Float swVer;
    private Long activityDateTimeUTC;  // Original file
    private Long activityDateTimeLocal; // Original file
    private Long activityDateTimeLocalOrg; // Original file
    private Long diffMinutesLocalUTC;

    Long timeFirstRecord;
    Long timeFirstRecordOrg;   // Original file
    Long timeLastRecord;
    int numberOfRecords;
    int numberOfLaps;
    //int changedStartTimeBySec = 0;

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

    private String savedFileInfoBefore = "";
    private String savedFileInfoAfter = "";
    private String savedFileUpdateLogg = "";
    String savedStrLapsActiveInfoShort = "";
    String savedStrLapsRestInfoShort = "";

    int numberOfDevFields;
    String devAppToRemove = "9a0508b9-0256-4639-88b3-a2690a14ddf9";
    //List <Integer> devFieldsToRemove = Arrays.asList("Strokes", "Calories", "Distance", "Speed", "Power", 2, 6, 7);
    List <Integer> devFieldsToRemove = Arrays.asList(10, 11, 12, 23, 1, 2, 6, 7);
    List <String> devFieldNamesToUpdate = Arrays.asList("Training_session", "MaxHRevenLaps");

    int i;
    FileInputStream in;
    Decode decode;
    MesgBroadcaster broadcaster;

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
    List<Mesg> eventTimerMesg = new ArrayList<>();
    List<Mesg> recordMesg = new ArrayList<>();

    // List<LapExtraMesg> lapExtraRecords = new ArrayList<>(); //Not Garmin SDK
    private List<PauseMesg> pauseRecords = new ArrayList<>(); //Not Garmin SDK
    private List<GapMesg> gapRecords = new ArrayList<>(); //Not Garmin SDK
    private List<RecordMesgAddOnRecords> recordMesgAddOnRecords = new ArrayList<>(); //Not Garmin SDK

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

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public FitFile () {
    }

    public Long getTimeFirstRecord() { return timeFirstRecord; }
    public void setTimeFirstRecord(Long timeFirstRecord) { this.timeFirstRecord = timeFirstRecord; }

    public Long getTimeLastRecord() { return timeLastRecord; }
    public void setTimeLastRecord(Long timeLastRecord) { this.timeLastRecord = timeLastRecord; }

    public Long getTimeFirstRecordOrg() { return timeFirstRecordOrg; }
    public void setTimeFirstRecordOrg(Long timeFirstRecordOrg) { this.timeFirstRecordOrg = timeFirstRecordOrg; }

    public Long getActivityDateTimeUTC() { return activityDateTimeUTC; }
    public void setActivityDateTimeUTC(Long activityDateTimeUTC) { this.activityDateTimeUTC = activityDateTimeUTC; }

    public Long getActivityDateTimeLocal() { return activityDateTimeLocal; }
    public void setActivityDateTimeLocal(Long activityDateTimeLocal) { this.activityDateTimeLocal = activityDateTimeLocal; }

    public Long getActivityDateTimeLocalOrg() { return activityDateTimeLocalOrg; }
    public void setActivityDateTimeLocalOrg(Long activityDateTimeLocalOrg) { this.activityDateTimeLocalOrg = activityDateTimeLocalOrg; }

    public Long getDiffMinutesLocalUTC() { return diffMinutesLocalUTC; }
    public void setDiffMinutesLocalUTC(Long diffMinutesLocalUTC) { this.diffMinutesLocalUTC = diffMinutesLocalUTC; }

    public int getNumberOfRecords() { return numberOfRecords; }
    public void setNumberOfRecords(int numberOfRecords) { this.numberOfRecords = numberOfRecords; }

    public Float getTotalDistance() { return totalDistance; }
    public void setTotalDistance(Float totalDistance) { this.totalDistance = totalDistance; }

    public Float getTotalDistanceOrg() { return totalDistanceOrg; }
    public void setTotalDistanceOrg(Float totalDistanceOrg) { this.totalDistanceOrg = totalDistanceOrg; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public int getProductNo() { return productNo; }
    public void setProductNo(int productNo) { this.productNo = productNo; }

    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }

    public Float getSwVer() { return swVer; }
    public void setSwVer(Float swVer) { this.swVer = swVer; }

    public String getWktName() { return wktName; }
    public void setWktName(String wktName) { this.wktName = wktName; }

    public Sport getSport() { return sport; }
    public void setSport(Sport sport) { this.sport = sport; }

    public SubSport getSubsport() { return subsport; }
    public void setSubsport(SubSport subsport) { this.subsport = subsport; }

    public String getSportProfile() { return sportProfile; }
    public void setSportProfile(String sportProfile) { this.sportProfile = sportProfile; }

    public Float getTotalTimerTime() { return totalTimerTime; }
    public void setTotalTimerTime(Float totalTimerTime) { this.totalTimerTime = totalTimerTime; }

    public Float getAvgSpeed() { return avgSpeed; }
    public void setAvgSpeed(Float avgSpeed) { this.avgSpeed = avgSpeed; }

    public Float getMaxSpeed() { return maxSpeed; }
    public void setMaxSpeed(Float maxSpeed) { this.maxSpeed = maxSpeed; }

    public int getAvgCadence() { return avgCadence; }
    public void setAvgCadence(int avgCadence) { this.avgCadence = avgCadence; }

    public int getAvgPower() { return avgPower; }
    public void setAvgPower(int avgPower) { this.avgPower = avgPower; }

    public int getNumberOfLaps() { return numberOfLaps; }
    public void setNumberOfLaps(int numberOfLaps) { this.numberOfLaps = numberOfLaps; }

    //public int getChangedStartTimeBySec() { return changedStartTimeBySec; }
    //public void setChangedStartTimeBySec(int changedStartTimeBySec) { this.changedStartTimeBySec = changedStartTimeBySec; }
    
    public List<PauseMesg> getPauseList() { return pauseRecords; }
    public List<GapMesg> getGapList() { return gapRecords; }
    public List<RecordMesgAddOnRecords> getRecordMesgAddOnRecords() { return recordMesgAddOnRecords; }
    public List<Mesg> getAllMesg() { return allMesg; }
    public List<Mesg> getFileIdMesg() { return fileIdMesg; }
    public List<Mesg> getDeviceInfoMesg() { return deviceInfoMesg; }
    public List<Mesg> getWktSessionMesg() { return wktSessionMesg; }
    public List<Mesg> getWktStepMesg() { return wktStepMesg; }
    public List<Mesg> getWktRecordMesg() { return wktRecordMesg; }
    public List<Mesg> getActivityMesg() { return activityMesg; }
    public List<Mesg> getSessionMesg() { return sessionMesg; }
    public List<Mesg> getSplitMesg() { return splitMesg; }
    public List<Mesg> getLapMesg() { return lapMesg; }
    public List<Mesg> getEventMesg() { return eventMesg; }
    public List<Mesg> getEventTimerMesg() { return eventTimerMesg; }
    public List<Mesg> getRecordMesg() { return recordMesg; }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public class GapMesg {
        private int no;
        private Long timeStart;
        private Long timeStop;
        private Long timeGap; //seconds
        private int ixStart;
        private int ixStop;
        private int ixLap;
        private Float distStart;
        private Float distStop;
        private int latStart;
        private int lonStart;
        private int latStop;
        private int lonStop;
        private Float distGap;
        private Float distGapGps;
        private Float altStart;
        private Float altStop;
        private Float altGap;

        public GapMesg() {
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
        private int no;
        private Long timeStart;
        private Long timeStop;
        private Long timePause; //seconds
        private int ixStart;
        private int ixStop;
        private int ixEvStart;
        private int ixEvStop;
        private int ixLap;
        private Float distStart;
        private int latStart;
        private int lonStart;
        private int latStop;
        private int lonStop;
        private Float distPause;
        private Float altStart;
        private Float altStop;
        private Float altPause;

        public PauseMesg() {
        }

        public int getNo() { return no; }
        public void setNo(int no) { this.no = no; }

        public Long getTimeStart() { return timeStart; }
        public void setTimeStart(Long timeStart) { this.timeStart = timeStart; }

        public Long getTimeStop() { return timeStop; }
        public void setTimeStop(Long timeStop) { this.timeStop = timeStop; }

        public Long getTimePause() { return timePause; }
        public void setTimePause(Long timePause) { this.timePause = timePause; }

        public int getIxStart() { return ixStart; }
        public void setIxStart(int ixStart) { this.ixStart = ixStart; }

        public int getIxStop() { return ixStop; }
        public void setIxStop(int ixStop) { this.ixStop = ixStop; }

        public int getIxEvStart() { return ixEvStart; }
        public void setIxEvStart(int ixEvStart) { this.ixEvStart = ixEvStart; }

        public int getIxEvStop() { return ixEvStop; }
        public void setIxEvStop(int ixEvStop) { this.ixEvStop = ixEvStop; }

        public int getIxLap() { return ixLap; }
        public void setIxLap(int ixLap) { this.ixLap = ixLap; }

        public Float getDistStart() { return distStart; }
        public void setDistStart(Float distStart) { this.distStart = distStart; }

        public int getLatStart() { return latStart; }
        public void setLatStart(int latStart) { this.latStart = latStart; }

        public int getLonStart() { return lonStart; }
        public void setLonStart(int lonStart) { this.lonStart = lonStart; }

        public int getLatStop() { return latStop; }
        public void setLatStop(int latStop) { this.latStop = latStop; }

        public int getLonStop() { return lonStop; }
        public void setLonStop(int lonStop) { this.lonStop = lonStop; }

        public Float getDistPause() { return distPause; }
        public void setDistPause(Float distPause) { this.distPause = distPause; }

        public Float getAltStart() { return altStart; }
        public void setAltStart(Float altStart) { this.altStart = altStart; }

        public Float getAltStop() { return altStop; }
        public void setAltStop(Float altStop) { this.altStop = altStop; }

        public Float getAltPause() { return altPause; }
        public void setAltPause(Float altPause) { this.altPause = altPause; }

        public void calcDistPause () {
            this.distPause = (float) GeoUtils.distCalc(this.latStart, this.lonStart, this.latStop, this.lonStop);
        }

        public void calcTimePause () {
            this.timePause = this.timeStop - this.timeStart;
        }

        public void calcDistGap () {
            this.altPause = this.altStop - this.altStart;
        }
        

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    class RecordMesgAddOnRecords {
        Long timer;
        int lapNo;

        public RecordMesgAddOnRecords() {
        }
        public RecordMesgAddOnRecords(Long timer) {
            this.timer = timer;
        }
        public RecordMesgAddOnRecords(int lapNo, Long timer) {
            this.lapNo = lapNo;
            this.timer = timer;
        }

        public Long getTimer() { return timer; }
        public void setTimer(Long timer) { this.timer = timer; }

        public int getLapNo() { return lapNo; }
        public void setLapNo(int lapNo) { this.lapNo = lapNo; }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    /* class LapExtraMesg {
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
    } */
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
    public Long getLastTimerInTimerList() {
        return recordMesgAddOnRecords.get(recordMesgAddOnRecords.size() - 1).getTimer();
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public Long findTimeBasedOnTimer(Long timerValueToSearchFor) {
        int ix = 0;
        // FIND IX i allMesg list
        for (RecordMesgAddOnRecords record : recordMesgAddOnRecords) {
            if (record.getTimer() >= (timerValueToSearchFor)) {
                break;
            }
            ix += 1;
        }

        // Get the time of the day for this record
        return recordMesg.get(ix).getFieldLongValue(REC_TIME);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public Long findTimerBasedOnTime(Long timeValueToSearchFor) {
        int ix = 0;
        // FIND IX i allMesg list
        for (Mesg record : recordMesg) {
            if (record.getFieldLongValue(REC_TIME) >= (timeValueToSearchFor)) {
                break;
            }
            ix += 1;
        }

        // Get the time of the day for this record
        return recordMesgAddOnRecords.get(ix).getTimer();
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public int findIxInAllMesgBasedOnTimer(Long timerValueToSearchFor) {
        return findIxInAllMesgBasedOnTime(findTimeBasedOnTimer(timerValueToSearchFor));
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public int findIxInAllMesgBasedOnTime(Long timeToSearchFor) {
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
    public int findIxInRecordMesgBasedOnTimer(Long timerValueToSearchFor) {
        return findIxInRecordMesgBasedOnTime(findTimeBasedOnTimer(timerValueToSearchFor));
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public int findIxInRecordMesgBasedOnTime(Long timeToSearchFor) {
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
    public int countRecordsBetweenTimerValues(Long fromTimer, Long toTimer) {
        return (findIxInRecordMesgBasedOnTimer(toTimer) - findIxInRecordMesgBasedOnTimer(fromTimer) - 1);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public Boolean checkForLapStartsBetweenTimerValues(Long fromTimer, Long toTimer) {
        return (checkForLapStartsBetweenTimeValues(findTimeBasedOnTimer(fromTimer), findTimeBasedOnTimer(toTimer))) ;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public Boolean checkForLapStartsBetweenTimeValues(Long fromTime, Long toTime) {

        Boolean haveLaps = false;            
        List<Integer> includingLaps = findLapStartsBetweenTimeValues(fromTime, toTime);
        if (!includingLaps.isEmpty()) {
            System.out.println("==XX> Cannot delete records in range as it includes laps. These laps need to be merged: " + includingLaps);
            haveLaps = true;
        }

        return (haveLaps);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public List<Integer> findLapStartsBetweenTimerValues(Long fromTimer, Long toTimer) {
        return (findLapStartsBetweenTimeValues(findTimeBasedOnTimer(fromTimer), findTimeBasedOnTimer(toTimer))) ;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public List<Integer> findLapStartsBetweenTimeValues(Long fromTime, Long toTime) {

        List<Integer> lapsBetweenTimeValues = new ArrayList<>();

        // Run through all laps
        int ix = 0;
        Boolean firstLapFound = true;
        for (Mesg mesg : lapMesg) {
            Long lapStartTime = mesg.getFieldLongValue(LAP_STIME);

            // Check if lap start time is within the specified range
            if (lapStartTime >= fromTime && lapStartTime <= toTime) {
                if (firstLapFound) {
                    System.out.println();
                    System.out.println("==================================================");
                    System.out.println("==== LAPS FOUND IN RANGE");
                    firstLapFound = false;
                    lapsBetweenTimeValues.add(ix);
                    printLapRecord(ix - 1);
                }
                lapsBetweenTimeValues.add(ix + 1);
                printLapRecord(ix);
            }
            
            ix += 1;
        }
        return lapsBetweenTimeValues;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public Boolean checkForPausesAndGivePrintedResultBasedOnTimer(Long fromTimer, Long toTimer) {
        return (checkForPausesAndGivePrintedResultBasedOnTime(findTimeBasedOnTimer(fromTimer), findTimeBasedOnTimer(toTimer)));
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public Boolean checkForPausesAndGivePrintedResultBasedOnTime(Long fromTime, Long toTime) {

        Boolean isPauses = false;
        CheckForPausesResult result = checkForPausesByTime(fromTime, toTime);

        if (result.hasCompletePauses() || result.hasUnmatchedStart() || result.hasUnmatchedEnd()) {
            isPauses = true;
            System.out.println();
            System.out.println("==XX> There is at least one PAUSE between start and stop.");
            System.out.println("==XX> You need to DELETE PAUSES to proceeed.");

            if (result.hasCompletePauses()) {
                System.out.println("List of COMPLETED PAUSE(S) between start and stop.");
                for (int ix : result.completePauses()) {
                    printPause(ix);
                    System.out.println();
                }
            }
            if (result.hasUnmatchedStart()) {
                System.out.println("List of UNMATCHED START PAUSE(S), pause started before, but ending in interval.");
                for (int ix : result.unmatchedStartPauses()) {
                    printPause(ix);
                    System.out.println();
                }
            }
            if (result.hasUnmatchedEnd()) {
                System.out.println("List of UNMATCHED END PAUSE(S), pause starting in interval, but ending after.");
                for (int ix : result.unmatchedStopPauses()) {
                    printPause(ix);
                    System.out.println();
                }
            }
            
        }
        return isPauses;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public record CheckForPausesResult(
            List<Integer> unmatchedStopPauses,   // pauses with stop but no start
            List<Integer> completePauses,        // pauses fully inside the interval
            List<Integer> unmatchedStartPauses   // pauses with start but no stop
    ) {
        public boolean hasCompletePauses() {
            return completePauses != null && !completePauses.isEmpty();
        }

        public boolean hasUnmatchedStart() {
            return unmatchedStartPauses != null && !unmatchedStartPauses.isEmpty();
        }

        public boolean hasUnmatchedEnd() {
            return unmatchedStopPauses != null && !unmatchedStopPauses.isEmpty();
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public CheckForPausesResult checkForPausesByTimer(Long fromTimer, Long toTimer) {

        Long fromTime = findTimeBasedOnTimer(fromTimer);
        Long toTime = findTimeBasedOnTimer(toTimer);

        return checkForPausesByTime(fromTime, toTime);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public CheckForPausesResult checkForPausesByTime(Long fromTime, Long toTime) {
        List<Integer> completePauses = new ArrayList<>();
        List<Integer> unmatchedStartPauses = new ArrayList<>();
        List<Integer> unmatchedStopPauses = new ArrayList<>();

        for (PauseMesg pause : pauseRecords) {
            Long start = pause.getTimeStart();
            Long stop = pause.getTimeStop();
            int ix = pause.getNo() - 1;

            if (start < fromTime && stop >= fromTime && stop <= toTime) {
                unmatchedStopPauses.add(ix);
            } else if (start >= fromTime && stop <= toTime) {
                completePauses.add(ix);
            } else if (start >= fromTime && start <= toTime && (stop == null || stop > toTime)) {
                unmatchedStartPauses.add(ix);
            } else {
            }
        }

        return new CheckForPausesResult(unmatchedStopPauses, completePauses, unmatchedStartPauses);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public enum PauseVariant {
        ANY,            // any pause (complete, unmatched start, unmatched end)
        COMPLETE,       // complete pauses inside interval
        UNMATCHED_START,// pauses that start but have no stop
        UNMATCHED_END   // pauses that end but have no start
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public boolean isTherePauseBetweenTimerValues(
            Long startTimer,
            Long endTimer,
            PauseVariant variant
    ) {
        CheckForPausesResult result = checkForPausesByTimer(startTimer, endTimer);

        return switch (variant) {
            case ANY -> result.hasCompletePauses() || result.hasUnmatchedStart() || result.hasUnmatchedEnd();
            case COMPLETE -> result.hasCompletePauses();
            case UNMATCHED_START -> result.hasUnmatchedStart();
            case UNMATCHED_END -> result.hasUnmatchedEnd();
        };
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public boolean isThereAnyPausesBetweenTimerValues(Long startTimer, Long endTimer) {

        // Reuse the checkForPausesTime method
        CheckForPausesResult result = checkForPausesByTimer(startTimer, endTimer);

        // If any of the three variants have a match, return true
        return result.hasCompletePauses() 
            || result.hasUnmatchedStart() 
            || result.hasUnmatchedEnd();
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public boolean isThereAnyCompletedPausesBetweenTimer(Long startTimer, Long endTimer) {

        // Reuse the checkForPausesTime method
        CheckForPausesResult result = checkForPausesByTimer(startTimer, endTimer);

        // If any of the three variants have a match, return true
        return result.hasCompletePauses();
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public boolean isThereAnyUnmatchedStartPauseByTimer(Long startTimer, Long endTimer) {

        // Reuse the checkForPausesTime method
        CheckForPausesResult result = checkForPausesByTimer(startTimer, endTimer);

        // If any of the three variants have a match, return true
        return result.hasUnmatchedStart();
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public boolean isThereAnyUnmatchedEndPauseByTimer(Long startTimer, Long endTimer) {

        // Reuse the checkForPausesTime method
        CheckForPausesResult result = checkForPausesByTimer(startTimer, endTimer);

        // If any of the three variants have a match, return true
        return result.hasUnmatchedEnd();
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void createTimerList() {
        Long timerCounter = -1l;
        Long recordTimerDelta = 0l;
        Long lastRecordTime = recordMesg.get(0).getFieldLongValue(REC_TIME) - 1;

        int eventTimerIx = 1; // Skip first START event, ix = 0
        Boolean inPause = false;
        Boolean increaseTimer = true;
        Boolean isEventTImerTime = false;

        recordMesgAddOnRecords.clear();

        for (Mesg record : recordMesg) {

            increaseTimer = true;

            if (eventTimerMesg.size() > 0) { // More than First START and last STOP

                // If record time is the same or more than NEXT eventTimer mesg
                // ------------------------------------------------------------
                if (record.getFieldLongValue(REC_TIME) >= eventTimerMesg.get(eventTimerIx).getFieldLongValue(EVE_TIME)) {
                    isEventTImerTime = true;
                                /* System.out.println();
                            System.out.println("==> EVENT TIMER MESG @"
                                + EventType.getByValue(eventTimerMesg.get(eventTimerIx).getFieldShortValue(EVE_TYPE)) + " @time: "
                                + FitDateTime.toString(record.getFieldLongValue(EVE_TIME),diffMinutesLocalUTC)); */

                    if (eventTimerMesg.get(eventTimerIx).getFieldValue(EVE_TYPE).equals(EventType.STOP_ALL.getValue())) {
                        // If inPause - warning
                        if (inPause) {
                            System.out.println("==> WARNING - STOP AGAIN when already in pause, STOP event w/o Starting first @"
                                + eventTimerIx + " @time: "
                                + FitDateTime.toString(record.getFieldLongValue(EVE_TIME),diffMinutesLocalUTC));
                        } else {
                            /* System.out.println("==> EVENT TIMER STOP MESG @"
                                + eventTimerIx + " @time: "
                                + FitDateTime.toString(record.getFieldLongValue(EVE_TIME),diffMinutesLocalUTC)); */
                            inPause = true;
                            increaseTimer = true;
                        }
                    }
                    
                    if (eventTimerMesg.get(eventTimerIx).getFieldValue(EVE_TYPE).equals(EventType.START.getValue())) {
                        // If not inPause - warning
                        if (!inPause) {
                            System.out.println("==> WARNING - START when not in pause, START event w/o Stopping first @"
                                + eventTimerIx + " @time: "
                                + FitDateTime.toString(record.getFieldLongValue(EVE_TIME),diffMinutesLocalUTC));
                        } else {
                            /* System.out.println("==> EVENT TIMER START MESG @"
                                + eventTimerIx + " @time: "
                                + FitDateTime.toString(record.getFieldLongValue(EVE_TIME),diffMinutesLocalUTC)); */
                            inPause = false;
                            increaseTimer = false;
                        }
                    } 
                    
                    eventTimerIx += 1;
                }

                // If record not the same as event timer
                if (!isEventTImerTime && inPause) {
                    System.out.println("==> WARNING - Records in pause @"
                        + FitDateTime.toString(record.getFieldLongValue(EVE_TIME),diffMinutesLocalUTC));
                    increaseTimer = false;
                }

                if (increaseTimer) {
                    recordTimerDelta = record.getFieldLongValue(REC_TIME) - lastRecordTime; 
                    timerCounter += recordTimerDelta;
                    //System.out.println("==> Timer INCREASE for Record @" 
                      //  + FitDateTime.toString(record.getFieldLongValue(REC_TIME),diffMinutesLocalUTC));
                } else {
                    /* System.out.println("==> Timer do not increase for Record @" 
                        + FitDateTime.toString(record.getFieldLongValue(REC_TIME),diffMinutesLocalUTC)); */
                }

                RecordMesgAddOnRecords newExtraRecord = new RecordMesgAddOnRecords(timerCounter);
                recordMesgAddOnRecords.add(newExtraRecord);

            }

            lastRecordTime = record.getFieldLongValue(REC_TIME);

        }
        System.out.println("======== Records: " + recordMesg.size() + " extraRecords: " + recordMesgAddOnRecords.size());
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
            if (!inPause && pauseRecords.size() > 0 && (pauseIx <= pauseRecords.size()-1) && (record.getFieldLongValue(REC_TIME) >= pauseRecords.get(pauseIx).getTimeStart())) {
                inPause = true;

            // if in PAUSE, see if pause ends
            } else if (inPause && (pauseIx <= pauseRecords.size()-1) && (record.getFieldLongValue(REC_TIME) >= pauseRecords.get(pauseIx).getTimeStop())) {
                pauseIx += 1;
                inPause = false;

            // if in PAUSE and see records
            } else if (inPause) {
                System.out.println("WARNING - Records in pause (createGap)");

            // Check if GAP
            } else if ((!inPause && (record.getFieldLongValue(REC_TIME) - lastRecordTime) > gapThreshold)) {

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
    public void printGapList(String gapCommandInput, Integer minDistToShow) {

        int hrDiff = 0;
        String hrSign = "";

        System.out.println();
        System.out.println("==================================================");
        System.out.println("====GAPS IN FILE");
        System.out.println(" File  between " + FitDateTime.toString(timeFirstRecord,diffMinutesLocalUTC) + " >>>> " + FitDateTime.toString(timeLastRecord,diffMinutesLocalUTC));
        System.out.println(String.format(" TotalTime:%1$.0fsec Dist:%2$.0fm", totalTimerTime, totalDistance));
        System.out.println("--------------------------------------------------");
        //System.out.print(" Event:" + record.getEvent());
        //System.out.print(" No:" + record.getEvent().getValue());

        for (GapMesg record : gapRecords) {
            if (record.distGap >= minDistToShow) {
                System.out.print("   Gap (" + record.no + ")");
                System.out.print(String.format(" %1$dsec %2$.0fm ele%3$.1fm", record.timeGap, record.distGap, record.altGap));
                hrDiff = recordMesg.get(record.ixStop).getFieldIntegerValue(REC_HR) - recordMesg.get(record.ixStart).getFieldIntegerValue(REC_HR);
                if (hrDiff>=0) {
                    hrSign = "+";
                }
                System.out.print(String.format(" HR:%1$d%2$s%3$d", recordMesg.get(record.ixStart).getFieldIntegerValue(REC_HR), hrSign, hrDiff));
                System.out.print(" @time:" + PehoUtils.sec2minSecShort(recordMesgAddOnRecords.get(record.getIxStart()).getTimer()));
                System.out.print(String.format(" @dist:%1$.0fm", record.getDistStart()));

                if (gapCommandInput == null) {
                    // Show minimal
                } else if (gapCommandInput.equals("d")) {
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

        for (Mesg record : eventTimerMesg){

            // -------------- 
            // STOP event (pause START)
            if (pauseCounter > 0 && !inPause && record.getFieldValue(EVE_TYPE).equals(EventType.START.getValue())) {
                System.out.println("==> WARNING - START Event w/o Stopping first (inCreatePause) @" + FitDateTime.toString(record.getFieldLongValue(EVE_TIME),diffMinutesLocalUTC));

            } else if (!inPause && record.getFieldValue(EVE_TYPE).equals(EventType.STOP_ALL.getValue())) {
                inPause = true;
                pauseCounter += 1;

                if (record.getFieldLongValue(EVE_TIME).equals(timeLastRecord)) {
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

                PauseMesg newPause = new PauseMesg();
                newPause.setNo(pauseCounter);
                newPause.setTimeStart(startPauseTime);
                newPause.setTimeStop(timeStop);
                newPause.setIxStart(ixRecordStart);
                newPause.setIxStop(ixRecordStop);
                newPause.setIxEvStart(ixEvStart);
                newPause.setIxEvStop(ixEvStop);
                newPause.setIxLap(ixLap);
                newPause.setDistStart(distStart);
                newPause.setLatStart(latStart);
                newPause.setLonStart(lonStart);
                newPause.setLatStop(latStop);
                newPause.setLonStop(lonStop);
                newPause.setAltStart(altStart);
                newPause.setAltStop(altStop);
                newPause.calcTimePause();
                newPause.calcDistGap();
                newPause.calcDistPause();   // uses lat/lon values already set

                pauseRecords.add(newPause);

                /* pauseRecords.add(new PauseMesg(pauseCounter, startPauseTime, timeStop,
                    ixRecordStart, ixRecordStop, ixEvStart, ixEvStop, ixLap,
                    distStart, latStart, lonStart, latStop, lonStop,
                    altStart, altStop)); */

                inPause = false;
            } else {
            }
            eventIx += 1;
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printPause(int ix) {
        PauseMesg pauseRecord = pauseRecords.get(ix);
        int hrDiff = 0;
        String hrSign = "";

        System.out.print("   Pause (" + pauseRecord.getNo() + ")");
        System.out.print(String.format(" %1$dsec %2$.0fm ele%3$.1fm", pauseRecord.getTimePause(), pauseRecord.getDistPause(), pauseRecord.getAltPause()));

        hrDiff = recordMesg.get(pauseRecord.getIxStop()).getFieldIntegerValue(REC_HR)
                - recordMesg.get(pauseRecord.getIxStart()).getFieldIntegerValue(REC_HR);
        if (hrDiff > 0) {
            hrSign = "+";
        } else {
            hrSign = "";
        }
        System.out.print(String.format(" HR:%1$d%2$s%3$d",
            recordMesg.get(pauseRecord.getIxStart()).getFieldIntegerValue(REC_HR),
            hrSign,
            hrDiff));
        System.out.print(" @time:" + PehoUtils.sec2minSecShort(recordMesgAddOnRecords.get(pauseRecord.getIxStart()).getTimer()));
        System.out.print(" @dist:" + PehoUtils.m2km2(pauseRecord.getDistStart()) + "km");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printPauseList(String pauseCommandInput, Integer minDistToShow) {

        int hrDiff = 0;
        String hrSign = "";
        int ix = 0;

        System.out.println();
        System.out.println("==================================================");
        System.out.println("PAUSES IN FILE");
        System.out.println(" File  between " + FitDateTime.toString(timeFirstRecord,diffMinutesLocalUTC) + " >>>> " + FitDateTime.toString(timeLastRecord,diffMinutesLocalUTC));
        System.out.println(String.format(" TotalTime:%1$.0fsec Dist:%2$.0fm", totalTimerTime, totalDistance));
        System.out.println("--------------------------------------------------");
        //System.out.print(" Event:" + record.getEvent());
        //System.out.print(" No:" + record.getEvent().getValue());

        for (PauseMesg record : pauseRecords) {
            if ((record.getIxStop() - record.getIxStart()) > 1) {
                System.out.println("==> WARNING - Data Records in pause! Pause no: " + record.getNo());
            }
            if (record.getDistPause() >= minDistToShow) {
                printPause(ix);
                /* System.out.print("   Pause (" + record.getNo() + ")");
                System.out.print(String.format(" %1$dsec %2$.0fm ele%3$.1fm", record.getTimePause(), record.getDistPause(), record.getAltPause()));

                hrDiff = recordMesg.get(record.getIxStop()).getFieldIntegerValue(REC_HR)
                       - recordMesg.get(record.getIxStart()).getFieldIntegerValue(REC_HR);
                if (hrDiff > 0) {
                    hrSign = "+";
                } else {
                    hrSign = "";
                }
                System.out.print(String.format(" HR:%1$d%2$s%3$d",
                    recordMesg.get(record.getIxStart()).getFieldIntegerValue(REC_HR),
                    hrSign,
                    hrDiff));
                System.out.print(" @time:" + PehoUtils.sec2minSecShort(secExtraRecords.get(record.getIxStart()).getTimer()));
                System.out.print(" @dist:" + PehoUtils.m2km2(record.getDistStart()) + "km"); */

                if (pauseCommandInput == null) {
                    // Show minimal
                } else if (pauseCommandInput.equals("d")) {
                    System.out.print(" " + FitDateTime.toString(record.getTimeStart(),diffMinutesLocalUTC));
                    System.out.print(" Ele:" + (record.getAltStart()) + "m");
                    System.out.print(" lapNo:" + (record.getIxLap()+1));
                    System.out.print("   @ix:" + (record.getIxStart()) + "->" + (record.getIxStop()));
                    System.out.print(" @ixEv:" + (record.getIxEvStart()) + "->" + (record.getIxEvStop()));
                    //System.out.print("sec TimerTrigger:" + record.getTimerTrigger());
                }
                System.out.println();
                ix ++;
            }
        }
        System.out.println("--------------------------------------------------");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void fillRecordsInGap() {

            savedFileUpdateLogg += "Filling gaps with 1sec records" + System.lineSeparator();

            int numberOfNewSeconds = 0;
            int numberOfNewRecords = 0;
            int allMesgIxStart = 0;
            int recordMesgIxStart = 0;

            Mesg startGapRecord;
            Long startTime = 0l;
            Float startDist = 0f;
            Float startSpeed = 0f;
            Short startHr = 0;
            Integer startPow = 0;
            Integer startLat = 0;
            Integer startLon = 0;
            Float startAlt = 0f;

            Mesg stopGapRecord;
            Long stopTime = 0l;
            Float stopDist = 0f;
            Float stopSpeed = 0f;
            Short stopHr = 0;
            Integer stopPow = 0;
            Integer stopLat = 0;
            Integer stopLon = 0;
            Float stopAlt = 0f;

            Double distDelta = 0d;
            Double speedDelta = 0d;
            Double hrDelta = 0d;
            Double powDelta = 0d;
            Double altDelta = 0d;

        for (GapMesg record : gapRecords) {
            numberOfNewSeconds = record.getTimeGap().intValue();
            //System.out.println("numberOfNewSeconds: "+numberOfNewSeconds);

            numberOfNewRecords = numberOfNewSeconds - 1;
            //System.out.println("numberOfNewRecords: "+numberOfNewRecords);

            allMesgIxStart = findIxInAllMesgBasedOnTime(record.getTimeStop());
            recordMesgIxStart = findIxInRecordMesgBasedOnTime(record.getTimeStop());

            startGapRecord = recordMesg.get(findIxInRecordMesgBasedOnTime(record.getTimeStart()));
            startTime = startGapRecord.getFieldLongValue(REC_TIME);
            startDist = startGapRecord.getFieldFloatValue(REC_DIST);
            startSpeed = startGapRecord.getFieldFloatValue(REC_ESPEED);
            startHr = startGapRecord.getFieldShortValue(REC_HR);
            startPow = startGapRecord.getFieldIntegerValue(REC_POW);
            if (startPow == null) { startPow = 0; }
            startLat = startGapRecord.getFieldIntegerValue(REC_LAT);
            if (startLat == null) { startLat = 0; }
            startLon = startGapRecord.getFieldIntegerValue(REC_LON);
            if (startLon == null) { startLon = 0; }
            startAlt = startGapRecord.getFieldFloatValue(REC_EALT);

            stopGapRecord = recordMesg.get(recordMesgIxStart);
            stopTime = stopGapRecord.getFieldLongValue(REC_TIME);
            stopDist = stopGapRecord.getFieldFloatValue(REC_DIST);
            stopSpeed = stopGapRecord.getFieldFloatValue(REC_ESPEED);
            stopHr = stopGapRecord.getFieldShortValue(REC_HR);
            stopPow = stopGapRecord.getFieldIntegerValue(REC_POW);
            if (stopPow == null) { stopPow = 0; }
            stopLat = stopGapRecord.getFieldIntegerValue(REC_LAT);
            if (stopLat == null) { stopLat = 0; }
            stopLon = stopGapRecord.getFieldIntegerValue(REC_LON);
            if (stopLon == null) { stopLon = 0; }
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
            savedFileUpdateLogg += "-- GapNo: "+record.getNo()+", dist: "+record.getDistGap()+"m, time: "+record.getTimeGap()+"sec, @Dist: "
                +startDist+"-"+stopDist+"m, time: "+FitDateTime.toString(record.getTimeStart(),diffMinutesLocalUTC) + System.lineSeparator();

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
        setTimeFirstRecord(startTime);

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
        info += "-- New START coordinates:" + coords[0] + ", " + coords[1] + System.lineSeparator();
        info += String.format("    Decimal Degrees: Lat %.8f, Lon %.8f%n", coords[0], coords[1]);
        info += String.format("    Garmin Semicircles: Lat %d, Lon %d%n", newLatSemi, newLonSemi);
        info += String.format("    Back to Decimal: Lat %.8f, Lon %.8f%n", GeoUtils.fromSemicircles(newLatSemi), GeoUtils.fromSemicircles(newLonSemi));
        info += "   >>> Dist/Time from new point:" + Math.round(distFromNew) + "m / " + timeToIncrease + "sec " + PehoUtils.sec2minSecLong(timeToIncrease) + "min" + System.lineSeparator();
        savedFileUpdateLogg += info;
        System.out.print(info);

        // Adding new start record
        allMesg.add(findIxInAllMesgBasedOnTime(orgStartTime), newStartRecord);
        recordMesg.add(0, newStartRecord);
        numberOfRecords++;

        // Editing first TIMER START MESG
        //------------------------------
        eventTimerMesg.get(0).setFieldValue(EVE_TIME, startTime);

        // Setting speed on the original start record
        // ----------------------------------------------
        recordMesg.get(1).setFieldValue(REC_SPEED, distFromNew / timeToIncrease);
        recordMesg.get(1).setFieldValue(REC_ESPEED, distFromNew / timeToIncrease);

        // Adding distance to all records starting from org start record
        // --------------------------------------------------------------
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

        setTotalTimerTime(getTotalTimerTime() + timeToIncrease);
        sessionMesg.get(0).setFieldValue(SES_TIMER, getTotalTimerTime());
        sessionMesg.get(0).setFieldValue(SES_ETIMER, sessionMesg.get(0).getFieldFloatValue(SES_ETIMER) + timeToIncrease);

        setTotalDistance(recordMesg.get(getNumberOfRecords() - 1).getFieldFloatValue(RecordMesg.DistanceFieldNum));
        sessionMesg.get(0).setFieldValue(SES_DIST, getTotalDistance());

        setAvgSpeed(getTotalDistance() / getTotalTimerTime());
        sessionMesg.get(0).setFieldValue(SES_SPEED, getAvgSpeed());
        sessionMesg.get(0).setFieldValue(SES_ESPEED, getAvgSpeed());

        //----------------------
        // Updating ACTIVITY DATA
        //----------------------
        setActivityDateTimeUTC(getActivityDateTimeUTC() - timeToIncrease);
        setActivityDateTimeLocal(getActivityDateTimeLocal() - timeToIncrease);
        activityMesg.get(0).setFieldValue(ACT_TIME, getActivityDateTimeUTC());
        activityMesg.get(0).setFieldValue(ACT_LOCTIME, getActivityDateTimeLocal());

        info2 += "NEW TIMES" + System.lineSeparator();
        info2 += "startTime:" + FitDateTime.toString(new DateTime(startTime)) + System.lineSeparator();
        info2 += "Lap time/stime:" + FitDateTime.toString(new DateTime(lapTime)) + " / " + FitDateTime.toString(new DateTime(lapSTime)) + System.lineSeparator();
        info2 += "Ses time/stime:" + FitDateTime.toString(new DateTime(sesTime)) + " / " + FitDateTime.toString(new DateTime(sesSTime)) + System.lineSeparator();
        info2 += "Act time UTC:" + FitDateTime.toString(getActivityDateTimeUTC()) + System.lineSeparator();
        info2 += "Act time Loc:" + FitDateTime.toString(getActivityDateTimeLocal()) + System.lineSeparator();
        System.out.print(info2);
        savedFileUpdateLogg += info2;

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void deleteRecordsCreateGap(Long fromTimer, Long toTimer) {
        Long fromTime = recordMesg.get(findIxInRecordMesgBasedOnTimer(fromTimer)).getFieldLongValue(REC_TIME);
        Long toTime = recordMesg.get(findIxInRecordMesgBasedOnTimer(toTimer)).getFieldLongValue(REC_TIME);

        int ixInRecordMesgStart = findIxInRecordMesgBasedOnTime(fromTime);
        int ixInRecordMesgStop = findIxInRecordMesgBasedOnTime(toTime);
        int numberOfRecordsToDelete = ixInRecordMesgStop - ixInRecordMesgStart + 1;
        int ixInRecordMesgToDelete = ixInRecordMesgStart;

        int ixInAllMesgStart = findIxInAllMesgBasedOnTime(fromTime);
        int ixInAllMesgToDelete = ixInAllMesgStart;

        for (int i=1; i<numberOfRecordsToDelete; i++) {
            while (allMesg.get(ixInAllMesgToDelete).getNum() != MesgNum.RECORD) {
                ixInAllMesgToDelete++;
            }
            allMesg.remove(ixInAllMesgToDelete);
            recordMesg.remove(ixInRecordMesgToDelete);
        }

        String tempLogg = "";
        tempLogg += "===> Input values: " + FitDateTime.toTimerString(fromTimer) + ", " + FitDateTime.toTimerString(toTimer) + System.lineSeparator();
        tempLogg += "===> RecordMesgIx to delete from-to: " + ixInRecordMesgStart + " - " + ixInRecordMesgStop + System.lineSeparator();
        tempLogg += "===> AllMesgIx to delete from: " + ixInAllMesgStart + " and number of records: " + numberOfRecordsToDelete + System.lineSeparator();
        tempLogg += "===> Deleting records to create gap of " + FitDateTime.toTimerString(toTimer-fromTimer+3) //+3 because to and from should be included
             + ", from " + FitDateTime.toTimerString(fromTimer) + " into the activity" + System.lineSeparator();
        tempLogg += "===> Deleting records from " + FitDateTime.toString(fromTime,0)
             + " to " + FitDateTime.toString(toTime,0) + System.lineSeparator();
        savedFileUpdateLogg += tempLogg;
        System.out.print(tempLogg);

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void addRecordInGap(int gapNo, double[] coords) {

        // Parsing GPS input data
        // ----------------------
        int newLatSemi = GeoUtils.toSemicircles(coords[0]);
        int newLonSemi = GeoUtils.toSemicircles(coords[1]);

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
        info += "----------------------------" + System.lineSeparator();
        info += "-- Adding GPS point in gap: " + gapNo + System.lineSeparator();
        info += "   New gap coordinates: " + coords[0] + ", " + coords[1] + System.lineSeparator();
        info += String.format("    Decimal Degrees: Lat %.8f, Lon %.8f%n", coords[0], coords[1]);
        info += String.format("    Garmin Semicircles: Lat %d, Lon %d%n", newLatSemi, newLonSemi);
        info += String.format("    Back to Decimal: Lat %.8f, Lon %.8f%n", GeoUtils.fromSemicircles(newLatSemi), GeoUtils.fromSemicircles(newLonSemi));

        info += "   >>> ix start-stop:" + gapToChange.ixStart + "-" + gapToChange.ixStop + System.lineSeparator();
        info += "   >>> Calc old dist:" + Math.round(GeoUtils.distCalc(startLat, startLon, stopLat, stopLon)) + "m" + System.lineSeparator();
        info += "   >>> Garmin old Dist/Time" + gapToChange.distGap + "m / " + PehoUtils.sec2minSecLong(gapToChange.timeGap) + "sec" + System.lineSeparator();
        info += "   >>> Dist/Time to new point:" + Math.round(distToNew) + "m / " + Math.round(gapToChange.timeGap * (distToNew / (distToNew + distFromNew))) + "sec" + System.lineSeparator();
        info += "   >>> Pace to new point:" + PehoUtils.mps2minpkm((float) (distToNew/timeToNew)) + "min/km" + System.lineSeparator();
        info += "   >>> Dist/Time from new point:" + Math.round(distFromNew) + "m / " + Math.round(gapToChange.timeGap * (distFromNew / (distToNew + distFromNew))) + "sec" + System.lineSeparator();
        info += "   >>> Pace to from point:" + PehoUtils.mps2minpkm((float) (distFromNew/timeFromNew)) + "min/km" + System.lineSeparator();
        info += "   >>> Dist change:" + Math.round(newTotalDistChange) + "m" + System.lineSeparator();
        savedFileUpdateLogg += info;
        System.out.print(info);

        allMesg.add(findIxInAllMesgBasedOnTime(stopTime), newGapRecord);
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
    public void printEvents(Long eventTimeStartToPrint, Long eventTimeEndToPrint, Event eventToPrint, EventType eventTypeToPrint) {

        // Use EventType EventType.INVALID for Event independent of type

        int eventIx = 0;

        for (Mesg mesg:allMesg) {
            if (mesg.getNum() == MesgNum.EVENT) {
                Short rawEvent = mesg.getFieldShortValue(EVE_EVENT);
                Short rawEventType = mesg.getFieldShortValue(EVE_TYPE);
                Long eventTime = mesg.getFieldLongValue(EVE_TIME);
                if (rawEvent == null || rawEventType == null || eventTime == null)
                    continue;

                Event mesgEvent = Event.getByValue(rawEvent);
                EventType mesgEventType = EventType.getByValue(rawEventType);

                if (eventTime >= eventTimeStartToPrint && eventTime <= eventTimeEndToPrint) {
                    System.out.println("Event ix: " + eventIx + " / " + Event.getByValue(mesg.getFieldShortValue(EVE_EVENT)) + " / " + EventType.getByValue(mesg.getFieldShortValue(EVE_TYPE)) + " / " + FitDateTime.toString(new DateTime(mesg.getFieldLongValue(EVE_TIME)),diffMinutesLocalUTC));
                }
                eventIx++;
            }
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void deleteEvents(Long eventTimeStartToDelete, Long eventTimeStopToDelete, Event eventToDelete, EventType eventTypeToDelete) {

        // Use EventType EventType.INVALID for Event independent of type

        // Find EVENT messages to delete in mesg list
        // ========================================
        int mesgIx = 0;
        int eventCounter = 0;
        List<Integer> mesgToDelete = new ArrayList<>();
        String tempLog = "START - Deleting events" + System.lineSeparator() + "------------------------------" + System.lineSeparator() +
                         "Input values to delete events between " + FitDateTime.toString(new DateTime(eventTimeStartToDelete),diffMinutesLocalUTC) + " and " + FitDateTime.toString(new DateTime(eventTimeStopToDelete),diffMinutesLocalUTC) + System.lineSeparator() +
                         "Event to delete: " + eventToDelete + System.lineSeparator();


        for (Mesg mesg:allMesg) {
            // Find event messages
            if (mesg.getNum() == MesgNum.EVENT) {
                eventCounter++;
                Short rawEvent = mesg.getFieldShortValue(EVE_EVENT);
                Short rawEventType = mesg.getFieldShortValue(EVE_TYPE);
                Long eventTime = mesg.getFieldLongValue(EVE_TIME);
                if (rawEvent == null || rawEventType == null || eventTime == null)
                continue;

                Event mesgEvent = Event.getByValue(rawEvent);
                EventType mesgEventType = EventType.getByValue(rawEventType);

                // Find matching time
                if (eventTime >= eventTimeStartToDelete && eventTime <= eventTimeStopToDelete) {

                    // Find matching event
                        if (mesgEvent.equals(eventToDelete)) {

                        // If event is a TIMER event
                        if (eventTypeToDelete.equals(EventType.INVALID)) {
                            mesgToDelete.add(mesgIx);
                                tempLog += "Found matching TIMER event to delete in allMesg: " + 
                                    Event.getByValue(mesg.getFieldShortValue(EVE_EVENT)) + 
                                    EventType.getByValue(mesg.getFieldShortValue(EVE_TYPE)) + 
                                    " @ix:" + mesgIx +
                                    System.lineSeparator();

                        // If event is not a TIMER event
                        } else {
                            if (mesgEventType.equals(eventTypeToDelete)) {
                                mesgToDelete.add(mesgIx);
                                tempLog += "Found matching TIMER and TYPE event to delete in allMesg: " + 
                                    Event.getByValue(mesg.getFieldShortValue(EVE_EVENT)) + 
                                    EventType.getByValue(mesg.getFieldShortValue(EVE_TYPE)) + 
                                    " @ix:" + mesgIx +
                                    System.lineSeparator();
                            }
                        }
                    }
                }
            }
            mesgIx++;
        }

        // Remove messages from mesg list to delete
        Collections.reverse(mesgToDelete);
        for (int mesgIxToDelete : mesgToDelete) {
            allMesg.remove(mesgIxToDelete);
        }

        // Update event message list and event timer message list
        reCreateEventMesg();

        savedFileUpdateLogg += tempLog;
        System.out.println(tempLog);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void reCreateEventMesg() {

        eventMesg.clear();
        eventTimerMesg.clear();

        for (Mesg mesg:allMesg) {

            if (mesg.getNum() == MesgNum.EVENT) {
                eventMesg.add(mesg);

                Event event = Event.getByValue(mesg.getFieldShortValue(EVE_EVENT));
                if (event.equals(Event.TIMER)) {
                    eventTimerMesg.add(mesg);
                }
            }
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void deletePause(int pauseNoToDelete) {
        int pauseIx = pauseNoToDelete - 1;
        PauseMesg pauseToDelete = getPauseList().get(pauseIx);
        Long timeStart = pauseToDelete.getTimeStart();
        Long timeStop = pauseToDelete.getTimeStop();

        String tempLog = "";
        tempLog += System.lineSeparator();
        tempLog += "PAUSE - DELETE, creating a gap" + System.lineSeparator();
        tempLog += "------------------------------" + System.lineSeparator();
        tempLog += "Pause no to be deleted: " + pauseNoToDelete + System.lineSeparator();
        tempLog += "-- New gap duration is " + PehoUtils.sec2minSecLong(pauseToDelete.getTimePause()) + "min" + System.lineSeparator();
        System.out.println(tempLog);
        savedFileUpdateLogg += tempLog;

        deleteEvents(timeStart, timeStop, Event.TIMER, EventType.INVALID);

        // Search for ix in allMesg
        int mesgCounter = 0;
        int foundCounter = 0;
        int allMesgStopEventToDelete = 0;
        int allMesgStartEventToDelete = 0;
        for (Mesg mesg:allMesg) {
            if (mesg.getNum() == MesgNum.EVENT) {
                if (mesg.getFieldLongValue(EVE_TIME).equals(timeStart)) {
                    if (mesg.getFieldValue(EVE_TYPE).equals(EventType.STOP_ALL.getValue())) {
                        allMesgStopEventToDelete = mesgCounter;
                        foundCounter++;
                    }
                }
                if (mesg.getFieldLongValue(EVE_TIME).equals(timeStop)) {
                    if (mesg.getFieldValue(EVE_TYPE).equals(EventType.START.getValue())) {
                        allMesgStartEventToDelete = mesgCounter;
                        foundCounter++;
                    }
                }
            }
            if (foundCounter >= 2) { break; }
            mesgCounter++;
        }

        // Search for ix in eventMesg
        mesgCounter = 0;
        foundCounter = 0;
        int eventMesgStopEventToDelete = 0;
        int eventMesgStartEventToDelete = 0;
        for (Mesg mesg:eventMesg) {
            if (mesg.getFieldLongValue(EVE_TIME).equals(timeStart)) {
                if (mesg.getFieldValue(EVE_TYPE).equals(EventType.STOP_ALL.getValue())) {
                    eventMesgStopEventToDelete = mesgCounter;
                    foundCounter++;
                }
            }
            if (mesg.getFieldLongValue(EVE_TIME).equals(timeStop)) {
                if (mesg.getFieldValue(EVE_TYPE).equals(EventType.START.getValue())) {
                    eventMesgStartEventToDelete = mesgCounter;
                    foundCounter++;
                }
            }
            if (foundCounter >= 2) { break; }
            mesgCounter++;
        }

        // Delete pause END, START EVENT - Delete of the last record first, otherwise index will change
        allMesg.remove(allMesgStartEventToDelete);
        eventMesg.remove(eventMesgStartEventToDelete);
        eventTimerMesg.remove(pauseToDelete.getIxEvStop()); //Event Ix when pause stop, START event

        // Delete pause START, STOP EVENT
        allMesg.remove(allMesgStopEventToDelete);
        eventMesg.remove(eventMesgStopEventToDelete);
        eventTimerMesg.remove(pauseToDelete.getIxEvStart()); //Event Ix when pause start, STOP event
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void increasePause(int pauseNo, Long secondsToPutIntoPause) {

        PauseMesg pauseToIncrease = pauseRecords.get(pauseNo-1);

        Mesg startPauseEvent = eventTimerMesg.get(pauseToIncrease.getIxEvStart());
        Long orgStartEventTime = startPauseEvent.getFieldLongValue(EVE_TIME);

        Mesg startPauseRecord = recordMesg.get(pauseToIncrease.getIxStart());
        Float orgStartPauseDist = startPauseRecord.getFieldFloatValue(REC_DIST);

        Long newStartEventTime = orgStartEventTime - secondsToPutIntoPause;
        startPauseEvent.setFieldValue(EVE_TIME, newStartEventTime);

        // Delete records between new and old start of pause, inside the pause
        // ----------------------------------------------------------------------
        Long recordToDeleteTime = orgStartEventTime;
        int i = 0;
        int recordToDeleteIx = pauseToIncrease.getIxStart();
        int allMesgToDeleteIx = findIxInAllMesgBasedOnTime(orgStartEventTime);
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

        String tempLog = "";
        tempLog += "PAUSE - INCREASE, forgot to stop before" + System.lineSeparator();
        tempLog += "---------------------------------------" + System.lineSeparator();
        tempLog += "Increased pause no: " + pauseNo + System.lineSeparator();
        tempLog += "-- Pause increased with " + secondsToPutIntoPause + "sec to " + PehoUtils.sec2minSecLong(pauseToIncrease.getTimePause()+secondsToPutIntoPause) + "min" + System.lineSeparator();
        System.out.println(tempLog);
        savedFileUpdateLogg += tempLog;

        // Increase distance after the shortened pause, starting from 1 after pause stop
        // ------------------------------------------------------
        Float newStartPauseDist = recordMesg.get(recordToDeleteIx).getFieldFloatValue(REC_DIST);
        Float distChangeValue = newStartPauseDist-orgStartPauseDist; // Will be negative
        System.out.println("Dist:"+orgStartPauseDist+"-"+newStartPauseDist+"="+distChangeValue);
        addDistToRecords(recordToDeleteIx+1, distChangeValue);

        // Updating LAP DATA
        //------------------
        Float lapTime = lapMesg.get(pauseToIncrease.getIxLap()).getFieldFloatValue(LAP_TIMER) - secondsToPutIntoPause;
        Float lapDist = lapMesg.get(pauseToIncrease.getIxLap()).getFieldFloatValue(LAP_DIST) + distChangeValue;
        lapMesg.get(pauseToIncrease.getIxLap()).setFieldValue(LAP_TIMER, (lapTime));
        //lapMesg.get(pauseToShorten.ixLap).setFieldValue(LAP_ETIMER, (lapTime - secondsToPutIntoPause));
        lapMesg.get(pauseToIncrease.getIxLap()).setFieldValue(LAP_DIST, (lapDist));
        lapMesg.get(pauseToIncrease.getIxLap()).setFieldValue(LAP_SPEED, (lapDist / lapTime));
        lapMesg.get(pauseToIncrease.getIxLap()).setFieldValue(LAP_ESPEED, (lapDist / lapTime));

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
    public void mergeLaps(int fromLap, int toLap) {

        Long timeStart = 0L;
        Long timeEnd = 0L;
        Float timerSumOfLaps = 0f;
        Float elapsedTimerSumOfLaps = 0f;
        Float movingTimerSumOfLaps = 0f;
        Float distSumOfLaps = 0f;
        Float hrAvgFaktorsumOfLaps = 0f;
        Integer hrMaxOfLaps = 0;
        Float speedAvgFaktorsumOfLaps = 0f;
        Float speedMaxOfLaps = 0f;
        Float enhSpeedAvgFaktorsumOfLaps = 0f;
        Float enhSpeedMaxOfLaps = 0f;
        Float powerAvgFaktorsumOfLaps = 0f;
        Integer powerMaxOfLaps = 0;
        Float cadenceAvgFaktorsumOfLaps = 0f;
        Integer cadenceMaxOfLaps = 0;
        Float ascentSumOfLaps = 0f;
        Float descentSumOfLaps = 0f;
        Float altAvgFaktorsumOfLaps = 0f;
        Float altMaxOfLaps = 0f;
        Float altMinOfLaps = 0f;
        Integer latStart = 0;
        Integer lonStart = 0;
        Integer latEnd = 0;
        Integer lonEnd = 0;
        Float tempAvgFaktorsumOfLaps = 0f;
        Byte tempMaxOfLaps = 0;
        Byte tempMinOfLaps = 0;

        for (int lapIxCounter = (fromLap-1); lapIxCounter <= (toLap-1); lapIxCounter++) {

            // Summing up values from laps to be merged
            //-----------------------------------------------
            if (lapIxCounter == fromLap-1) {
                if (lapMesg.get(lapIxCounter).getFieldLongValue(LAP_STIME) != null) {
                    timeStart = lapMesg.get(lapIxCounter).getFieldLongValue(LAP_STIME);
                }
                if (lapMesg.get(lapIxCounter).getFieldIntegerValue(LAP_SLAT) != null) {
                    latStart = lapMesg.get(lapIxCounter).getFieldIntegerValue(LAP_SLAT);
                }
                if (lapMesg.get(lapIxCounter).getFieldIntegerValue(LAP_SLON) != null) {
                    lonStart = lapMesg.get(lapIxCounter).getFieldIntegerValue(LAP_SLON);
                }
            }
            if (lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_TIMER) != null) {
                timerSumOfLaps += lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_TIMER);
            }
            if (lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_ETIMER) != null) {
                elapsedTimerSumOfLaps += lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_ETIMER);
            }
            if (lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_MTIMER) != null) {
                movingTimerSumOfLaps += lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_MTIMER);
            }
            if (lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_DIST) != null) {
                distSumOfLaps += lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_DIST);
            }
            if (lapMesg.get(lapIxCounter).getFieldIntegerValue(LAP_HR) != null) {
                hrAvgFaktorsumOfLaps += lapMesg.get(lapIxCounter).getFieldIntegerValue(LAP_HR)
                    * lapMesg.get(lapIxCounter).getFieldLongValue(LAP_TIME);
            }
            if (lapMesg.get(lapIxCounter).getFieldIntegerValue(LAP_MHR) != null) {
                if (lapMesg.get(lapIxCounter).getFieldIntegerValue(LAP_MHR) > hrMaxOfLaps) {
                    hrMaxOfLaps = lapMesg.get(lapIxCounter).getFieldIntegerValue(LAP_MHR);
                }
            }
            if (lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_SPEED) != null) {
                speedAvgFaktorsumOfLaps += lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_SPEED)
                    * lapMesg.get(lapIxCounter).getFieldLongValue(LAP_TIMER);
            }
            if (lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_MSPEED) != null) {
                if (lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_MSPEED) > speedMaxOfLaps) {
                    speedMaxOfLaps = lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_MSPEED);
                }
            }
            if (lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_ESPEED) != null) {
                enhSpeedAvgFaktorsumOfLaps += lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_ESPEED)
                    * lapMesg.get(lapIxCounter).getFieldLongValue(LAP_TIMER);
            }
            if (lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_EMSPEED) != null) {
                if (lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_EMSPEED) > enhSpeedMaxOfLaps) {
                    enhSpeedMaxOfLaps = lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_EMSPEED);
                }
            }
            if (lapMesg.get(lapIxCounter).getFieldIntegerValue(LAP_POW) != null) {
                powerAvgFaktorsumOfLaps += lapMesg.get(lapIxCounter).getFieldIntegerValue(LAP_POW)
                    * lapMesg.get(lapIxCounter).getFieldLongValue(LAP_TIMER);
            }
            if (lapMesg.get(lapIxCounter).getFieldIntegerValue(LAP_MPOW) != null) {
                if (lapMesg.get(lapIxCounter).getFieldIntegerValue(LAP_MPOW) > powerMaxOfLaps) {
                    powerMaxOfLaps = lapMesg.get(lapIxCounter).getFieldIntegerValue(LAP_MPOW);
                }
            }
            if (lapMesg.get(lapIxCounter).getFieldIntegerValue(LAP_CAD) != null) {
                cadenceAvgFaktorsumOfLaps += lapMesg.get(lapIxCounter).getFieldIntegerValue(LAP_CAD)
                    * lapMesg.get(lapIxCounter).getFieldLongValue(LAP_TIMER);
            }
            if (lapMesg.get(lapIxCounter).getFieldIntegerValue(LAP_MCAD) != null) {
                if (lapMesg.get(lapIxCounter).getFieldIntegerValue(LAP_MCAD) > cadenceMaxOfLaps) {
                    cadenceMaxOfLaps = lapMesg.get(lapIxCounter).getFieldIntegerValue(LAP_MCAD);
                }
            }
            if (lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_ASC) != null) {
                ascentSumOfLaps += lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_ASC);
            }
            if (lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_DESC) != null) {
                descentSumOfLaps += lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_DESC);
            }
            if (lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_ALT) != null) {
                altAvgFaktorsumOfLaps += lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_ALT)
                    * lapMesg.get(lapIxCounter).getFieldLongValue(LAP_TIMER);
            }
            if (lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_MALT) != null) {
                if (lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_MALT) > altMaxOfLaps) {
                    altMaxOfLaps = lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_MALT);
                }
            }
            if (lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_MINALT) != null) {
                if ((lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_MINALT) < altMinOfLaps) || (altMinOfLaps == 0f)) {
                    altMinOfLaps = lapMesg.get(lapIxCounter).getFieldFloatValue(LAP_MINALT);
                }
            }
            if (lapMesg.get(lapIxCounter).getFieldIntegerValue(LAP_TEMP) != null) {
                tempAvgFaktorsumOfLaps += lapMesg.get(lapIxCounter).getFieldIntegerValue(LAP_TEMP)
                    * lapMesg.get(lapIxCounter).getFieldLongValue(LAP_TIMER);
            }
            if (lapMesg.get(lapIxCounter).getFieldByteValue(LAP_MTEMP) != null) {
                if (lapMesg.get(lapIxCounter).getFieldByteValue(LAP_MTEMP) > tempMaxOfLaps) {
                    tempMaxOfLaps = lapMesg.get(lapIxCounter).getFieldByteValue(LAP_MTEMP);
                }
            }
            if (lapMesg.get(lapIxCounter).getFieldByteValue(LAP_MINTEMP) != null) {
                if ((lapMesg.get(lapIxCounter).getFieldByteValue(LAP_MINTEMP) < tempMinOfLaps) || (tempMinOfLaps == 0)) {
                    tempMinOfLaps = lapMesg.get(lapIxCounter).getFieldByteValue(LAP_MINTEMP);
                }
            }
        }

        // Getting values from the "toLap - 1" lap, but will not be used
        //-----------------------------------------------
        if (lapMesg.get(toLap-1).getFieldLongValue(LAP_TIME) != null) {
            timeEnd = lapMesg.get(toLap-1).getFieldLongValue(LAP_TIME);
        }
        if (lapMesg.get(toLap-1).getFieldIntegerValue(LAP_ELAT) != null) {
            latEnd = lapMesg.get(toLap-1).getFieldIntegerValue(LAP_ELAT);
        }
        if (lapMesg.get(toLap-1).getFieldIntegerValue(LAP_ELON) != null) {
            lonEnd = lapMesg.get(toLap-1).getFieldIntegerValue(LAP_ELON);
        }

        // Setting values in the "toLap - 1" lap
        //-----------------------------------------------
        if (lapMesg.get(toLap-1).getFieldLongValue(LAP_STIME) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_STIME, timeStart);
        }
        if (lapMesg.get(toLap-1).getFieldFloatValue(LAP_TIMER) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_TIMER, timerSumOfLaps);
        }
        if (lapMesg.get(toLap-1).getFieldFloatValue(LAP_ETIMER) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_ETIMER, elapsedTimerSumOfLaps);
        }
        if (lapMesg.get(toLap-1).getFieldFloatValue(LAP_MTIMER) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_MTIMER, movingTimerSumOfLaps);
        }
        if (lapMesg.get(toLap-1).getFieldFloatValue(LAP_DIST) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_DIST, distSumOfLaps);
        }
        if (lapMesg.get(toLap-1).getFieldIntegerValue(LAP_HR) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_HR, Math.round(hrAvgFaktorsumOfLaps / timerSumOfLaps));
        }
        if (lapMesg.get(toLap-1).getFieldIntegerValue(LAP_MHR) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_MHR, hrMaxOfLaps);
        }
        if (lapMesg.get(toLap-1).getFieldFloatValue(LAP_SPEED) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_SPEED, speedAvgFaktorsumOfLaps / timerSumOfLaps);
        }
        if (lapMesg.get(toLap-1).getFieldFloatValue(LAP_MSPEED) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_MSPEED, speedMaxOfLaps);
        }
        if (lapMesg.get(toLap-1).getFieldFloatValue(LAP_ESPEED) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_ESPEED, enhSpeedAvgFaktorsumOfLaps / timerSumOfLaps);
        }
        if (lapMesg.get(toLap-1).getFieldFloatValue(LAP_EMSPEED) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_EMSPEED, enhSpeedMaxOfLaps);
        }
        if (lapMesg.get(toLap-1).getFieldIntegerValue(LAP_POW) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_POW, Math.round(powerAvgFaktorsumOfLaps / timerSumOfLaps));
        }
        if (lapMesg.get(toLap-1).getFieldIntegerValue(LAP_MPOW) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_MPOW, powerMaxOfLaps);
        }
        if (lapMesg.get(toLap-1).getFieldIntegerValue(LAP_CAD) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_CAD, Math.round(cadenceAvgFaktorsumOfLaps / timerSumOfLaps));
        }
        if (lapMesg.get(toLap-1).getFieldIntegerValue(LAP_MCAD) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_MCAD, cadenceMaxOfLaps);
        }
        if (lapMesg.get(toLap-1).getFieldFloatValue(LAP_ASC) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_ASC, ascentSumOfLaps);
        }
        if (lapMesg.get(toLap-1).getFieldFloatValue(LAP_DESC) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_DESC, descentSumOfLaps);
        }
        if (lapMesg.get(toLap-1).getFieldFloatValue(LAP_ALT) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_ALT, altAvgFaktorsumOfLaps / timerSumOfLaps);
        }
        if (lapMesg.get(toLap-1).getFieldFloatValue(LAP_MALT) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_MALT, altMaxOfLaps);
        }
        if (lapMesg.get(toLap-1).getFieldFloatValue(LAP_MINALT) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_MINALT, altMinOfLaps);
        }
        if (lapMesg.get(toLap-1).getFieldIntegerValue(LAP_TEMP) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_TEMP, Math.round(tempAvgFaktorsumOfLaps / timerSumOfLaps));
        }
        if (lapMesg.get(toLap-1).getFieldByteValue(LAP_MTEMP) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_MTEMP, tempMaxOfLaps);
        }
        if (lapMesg.get(toLap-1).getFieldByteValue(LAP_MINTEMP) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_MINTEMP, tempMinOfLaps);
        }
        if (lapMesg.get(toLap-1).getFieldIntegerValue(LAP_SLAT) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_SLAT, latStart);
        }
        if (lapMesg.get(toLap-1).getFieldIntegerValue(LAP_SLON) != null) {
            lapMesg.get(toLap-1).setFieldValue(LAP_SLON, lonStart);
        }

        savedFileUpdateLogg += "Merged laps: " + fromLap + " to " + toLap + System.lineSeparator();
        savedFileUpdateLogg += "-- New lap " + (toLap-1) + " time: " + PehoUtils.sec2minSecLong(timerSumOfLaps) + " min, dist: " + Math.round(distSumOfLaps) + " m" + System.lineSeparator();

        // Deleting the merged laps (fromLap to toLap-1)
        //-----------------------------------------------
        for (int lapIxCounter = fromLap-1; lapIxCounter <= toLap-2; lapIxCounter++) {

            Long lapStartTime = lapMesg.get(lapIxCounter).getFieldLongValue(LAP_STIME);
            int mesgIxCounter = 0;
            for (Mesg mesg:allMesg) {
                if (mesg.getNum() == MesgNum.LAP) {
                    if (mesg.getFieldLongValue(LAP_STIME).equals(lapStartTime)) {
                        savedFileUpdateLogg += "-- Deleting lap ix:" + lapIxCounter + " time:" + FitDateTime.toString(mesg.getFieldLongValue(LAP_STIME),0) + System.lineSeparator();
                        break;
                    }
                }
                mesgIxCounter++;
            }
            allMesg.remove(mesgIxCounter);
            lapMesg.remove(fromLap-1);
        }

        numberOfLaps -= (toLap - fromLap);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void shortenPause(int pauseNo, Long newPauseTime) {

        PauseMesg pauseToShorten = pauseRecords.get(pauseNo-1);

        if (newPauseTime > pauseToShorten.getTimePause()) {
            System.out.println("----- NEW Pause time is to large ------");
            System.exit(0);
        }
        if (pauseNo > pauseRecords.size()) {
            System.out.println("----- PauseNo is to large ------");
            System.exit(0);
        }

        Mesg startPauseRecord = recordMesg.get(pauseToShorten.getIxStart());
        Mesg stopGapRecord = recordMesg.get(pauseToShorten.getIxStop()); // Org PAUSE STOP = New GAP STOP

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
        if (recordMesg.get(pauseToShorten.getIxStop()+1).getFieldIntegerValue(REC_POW) != null){
            startGapPow = recordMesg.get(pauseToShorten.ixStop+1).getFieldIntegerValue(REC_POW);
        }

        Long stopGapTime = stopGapRecord.getFieldLongValue(REC_TIME); // New GAP END
        Float stopGapDist = startGapDist + pauseToShorten.getDistPause(); // New GAP END
        Float stopGapAlt = stopGapRecord.getFieldFloatValue(REC_EALT);
        int stopGapLat = stopGapRecord.getFieldIntegerValue(REC_LAT);
        int stopGapLon = stopGapRecord.getFieldIntegerValue(REC_LON);
        int stopGapPow = startGapPow;

        // Speed Vaules
        Float startGapSpeed = (float) (pauseToShorten.getDistPause() / (stopGapTime - startGapTime)) ;
        Float stopGapSpeed = startGapSpeed;

        stopGapRecord.setFieldValue(REC_SPEED, stopGapSpeed);
        stopGapRecord.setFieldValue(REC_ESPEED, stopGapSpeed);

        // Power Value always missing in record after Pause
        stopGapRecord.setFieldValue(REC_POW, stopGapPow);

        String tempLog = "";
        tempLog += "PAUSE - SHORTEN, forgot to start after pause" + System.lineSeparator();
        tempLog += "--------------------------------------------" + System.lineSeparator();
        tempLog += "Shortened pause no: " + pauseNo + System.lineSeparator();
        tempLog += "-- Pause decreased from " + pauseToShorten.getTimePause() + "sec to " + newPauseTime + "sec" + System.lineSeparator();
        tempLog += "--> newSpeed:"+PehoUtils.mps2minpkm(startGapSpeed)+"km/min gpsDist:"+pauseToShorten.getDistPause() +
            "m gapStartDist:"+startGapDist+"m gapEnd:"+stopGapDist +
            "m gapEnd-startTime:"+(stopGapTime - startGapTime) + "s newTime:" + newPauseTime + "s" + System.lineSeparator();
        System.out.println(tempLog);
        savedFileUpdateLogg += tempLog;

        // Updating EVENT-TIMER-START DATA
        //----------------------   
        eventTimerMesg.get(pauseToShorten.getIxEvStop()).setFieldValue(EVE_TIME, startGapTime);

        // Create new PAUSE STOP / GAP START
        // ------------------------------------------------
        Mesg startGapNewRecord = new Mesg(startPauseRecord); // New PAUSE STOP = GAP START
        startGapNewRecord.setFieldValue(REC_TIME, startGapTime);
        startGapNewRecord.setFieldValue(REC_SPEED, startGapSpeed);
        startGapNewRecord.setFieldValue(REC_ESPEED, startGapSpeed);
        startGapNewRecord.setFieldValue(REC_POW, startGapPow);
        allMesg.add(findIxInAllMesgBasedOnTime(stopGapTime), startGapNewRecord);
        recordMesg.add(pauseToShorten.getIxStop(), startGapNewRecord);
        numberOfRecords++;

        // Increase distance after the shortened pause, starting from 1 after pause stop
        // ------------------------------------------------------
        addDistToRecords(pauseToShorten.getIxStop()+1, pauseToShorten.getDistPause());

        // Updating LAP DATA
        //------------------
        Float lapTime = lapMesg.get(pauseToShorten.getIxLap()).getFieldFloatValue(LAP_TIMER) + pauseToShorten.getTimePause() - newPauseTime;
        Float lapDist = lapMesg.get(pauseToShorten.getIxLap()).getFieldFloatValue(LAP_DIST) + pauseToShorten.getDistPause();
        lapMesg.get(pauseToShorten.getIxLap()).setFieldValue(LAP_TIMER, (lapTime));
        //lapMesg.get(pauseToShorten.getIxLap()).setFieldValue(LAP_ETIMER, (lapTime));
        lapMesg.get(pauseToShorten.getIxLap()).setFieldValue(LAP_DIST, (lapDist));
        lapMesg.get(pauseToShorten.getIxLap()).setFieldValue(LAP_SPEED, (lapDist / lapTime));
        lapMesg.get(pauseToShorten.getIxLap()).setFieldValue(LAP_ESPEED, (lapDist / lapTime));

        // Updating SESSION DATA
        //----------------------
        totalTimerTime += (float) pauseToShorten.getTimePause() - newPauseTime;
        sessionMesg.get(0).setFieldValue(SES_TIMER, (totalTimerTime));
        //elapsedTimerTime += (float) pauseToShorten.getTimePause() - newPauseTime;
        //sessionMesg.get(0).setFieldValue(SES_ETIMER, elapsedTimerTime);

        totalDistance = recordMesg.get(numberOfRecords-1).getFieldFloatValue(RecordMesg.DistanceFieldNum);
        sessionMesg.get(0).setFieldValue(SES_DIST, (totalDistance));

        avgSpeed = totalDistance / totalTimerTime;
        sessionMesg.get(0).setFieldValue(SES_SPEED, (avgSpeed));
        sessionMesg.get(0).setFieldValue(SES_ESPEED, (avgSpeed));
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
/*     public void initLapExtraRecords() {

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
 */    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
/*     public void wktAddSteps(String wktSteps, String wktName) {

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
 */    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
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
/*         if (!wktRecords.isEmpty()) {
            if (wktRecords.get(0).getWktName() != null) {
                newProfileName = wktRecords.get(0).getWktName();
            }
        }
 */
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
                        case MesgNum.SPLIT:
                            splitMesg.add(mesg);
                            break;
                        case MesgNum.LAP:
                            lapMesg.add(mesg);
                            break;
                        case MesgNum.EVENT:
                            eventMesg.add(mesg);
                            // If TIMER event add to eventTIMER list
                            if (mesg.getFieldValue(EVE_EVENT).equals(Event.TIMER.getValue())) {
                                eventTimerMesg.add(mesg);
                            }
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
                setManufacturer(Manufacturer.getStringFromValue(fileIdMesg.get(0).getFieldIntegerValue(FID_MANU)));
                if ("GARMIN".equals(getManufacturer())) {
                    if (fileIdMesg.get(0).getFieldIntegerValue(FID_MANU) != null) {
                        setProductNo(fileIdMesg.get(0).getFieldIntegerValue(FID_PROD));
                        setProduct(GarminProduct.getStringFromValue(fileIdMesg.get(0).getFieldIntegerValue(FID_PROD)));
                    }
                }
            }

            setSwVer(deviceInfoMesg.get(0).getFieldFloatValue(DINFO_SWVER));
            setTimeFirstRecord(recordMesg.get(0).getFieldLongValue(REC_TIME));

            if (activityMesg.get(0).getFieldLongValue(ACT_TIME) == null) {
                activityMesg.get(0).setFieldValue(ACT_TIME, timeFirstRecord);
            }
            setActivityDateTimeUTC(activityMesg.get(0).getFieldLongValue(ACT_TIME));
            if (activityMesg.get(0).getFieldLongValue(ACT_LOCTIME) == null) {
                activityMesg.get(0).setFieldValue(ACT_LOCTIME, timeFirstRecord);
            }
            setActivityDateTimeLocal(activityMesg.get(0).getFieldLongValue(ACT_LOCTIME));
            setDiffMinutesLocalUTC((getActivityDateTimeLocal() - getActivityDateTimeUTC()) / 60);
            setActivityDateTimeLocalOrg(getActivityDateTimeLocal());

            if (!wktRecordMesg.isEmpty()) {
                if (wktRecordMesg.get(0).getFieldStringValue(WKT_NAME) != null) {
                    setWktName(wktRecordMesg.get(0).getFieldStringValue(WKT_NAME));
                }
            }

            if (sessionMesg.get(0).getFieldValue(SES_SPORT) != null) {
                setSport(Sport.getByValue(sessionMesg.get(0).getFieldShortValue(SES_SPORT)));
            }
            if (sessionMesg.get(0).getFieldValue(SES_SUBSPORT) != null) {
                setSubsport(SubSport.getByValue(sessionMesg.get(0).getFieldShortValue(SES_SUBSPORT)));
            }
            if (sessionMesg.get(0).getFieldStringValue(SES_PROFILE) == null) {
                sessionMesg.get(0).setFieldValue(SES_PROFILE, "noProfile");
            } else {
                setSportProfile(sessionMesg.get(0).getFieldStringValue(SES_PROFILE));
            }
            if (sessionMesg.get(0).getFieldFloatValue(SES_TIMER) != null) {
                setTotalTimerTime(sessionMesg.get(0).getFieldFloatValue(SES_TIMER));
            }
            if (sessionMesg.get(0).getFieldFloatValue(SES_DIST) != null) {
                setTotalDistance(sessionMesg.get(0).getFieldFloatValue(SES_DIST));
                setTotalDistanceOrg(sessionMesg.get(0).getFieldFloatValue(SES_DIST));
            }
            if (sessionMesg.get(0).getFieldFloatValue(SES_SPEED) != null) {
                setAvgSpeed(sessionMesg.get(0).getFieldFloatValue(SES_SPEED));
            }
            if (sessionMesg.get(0).getFieldFloatValue(SES_ESPEED) != null) {
                setAvgSpeed(sessionMesg.get(0).getFieldFloatValue(SES_ESPEED));
            }

            setNumberOfLaps(lapMesg.size());

            setTimeFirstRecordOrg(timeFirstRecord);
            setTimeLastRecord(recordMesg.get(recordMesg.size() - 1).getFieldLongValue(REC_TIME));
            setNumberOfRecords(recordMesg.size());

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
        setTimeFirstRecord(recordMesg.get(0).getFieldLongValue(REC_TIME));
        setTimeLastRecord(recordMesg.get(recordMesg.size() - 1).getFieldLongValue(REC_TIME));
        setActivityDateTimeLocal(activityMesg.get(0).getFieldLongValue(ACT_LOCTIME));
        //setChangedStartTimeBySec(getChangedStartTimeBySec() + changeSeconds);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String createFileSummary() {
        String tempFileInfo = "";
        tempFileInfo += "--------------------------------------------------" + System.lineSeparator();
        tempFileInfo += " --> Manufacturer:" + getManufacturer() + ", " + getProduct() + "(" + getProductNo() + ")" + ", SW: v" + getSwVer() + System.lineSeparator();
        tempFileInfo += " --> Sport:" + getSport() + ", SubSport:" + getSubsport() + ", SportProfile:" + getSportProfile() + ", WktName:" + getWktName() + System.lineSeparator();
        tempFileInfo += " --> Org activity dateTime Local:" + FitDateTime.toString(getActivityDateTimeLocalOrg()) + System.lineSeparator();
        tempFileInfo += " --> New activity dateTime Local:" + FitDateTime.toString(getActivityDateTimeLocal()) + System.lineSeparator();
        tempFileInfo += " --> Org activity DateTime UTC:  " + FitDateTime.toString(getActivityDateTimeUTC()) + System.lineSeparator();
        tempFileInfo += " --> timeZone:                   " + FitDateTime.offsetToTimeZoneString(getDiffMinutesLocalUTC()) + System.lineSeparator();
        tempFileInfo += " --> Org start datetime UTC:     " + FitDateTime.toString(getTimeFirstRecordOrg()) + System.lineSeparator();
        tempFileInfo += " --> New start datetime UTC:     " + FitDateTime.toString(getTimeFirstRecord()) + System.lineSeparator();
        
        tempFileInfo += "--------------------------------------------------" + System.lineSeparator();

        return tempFileInfo;
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
    public void printSplitSummary() {
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

            Long startTime = mesg.getFieldLongValue(SPL_START_TIME);
            if (startTime != null) System.out.print(" Time:" + FitDateTime.toString(startTime, diffMinutesLocalUTC));

            Long endTime = mesg.getFieldLongValue(SPL_END_TIME);
            if (endTime != null) System.out.print("->" + FitDateTime.toString(endTime, diffMinutesLocalUTC));

            Float totalTimer = mesg.getFieldFloatValue(SPL_TOTAL_TIMER);
            if (totalTimer != null)  System.out.print(" SplTime:" + PehoUtils.sec2minSecLong(totalTimer) + "min");

            Float totalDistance = mesg.getFieldFloatValue(SPL_TOTAL_DISTANCE);
            if (totalDistance != null) System.out.print(" Dist:" + PehoUtils.m2km2(totalDistance) + "km");

            Float avgPace = mesg.getFieldFloatValue(SPL_AVG_SPEED);
            if (avgPace != null) System.out.print(" AvgPace:" + PehoUtils.mps2minpkm(avgPace));

            Float maxPace = mesg.getFieldFloatValue(SPL_MAX_SPEED);
            if (maxPace != null) System.out.print(" MaxPace:" + PehoUtils.mps2minpkm(maxPace));

            Integer ascent = mesg.getFieldIntegerValue(SPL_TOTAL_ASCENT);
            if (ascent != null) System.out.print(" Asc:" + ascent + "m");

            Integer descent = mesg.getFieldIntegerValue(SPL_TOTAL_DESCENT);
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

            Integer startElev = mesg.getFieldIntegerValue(SPL_START_ELEVATION);
            if (startElev != null) System.out.print(" StartEle: " + startElev + "m");

            Float movingTime = mesg.getFieldFloatValue(SPL_TOTAL_MOVING_TIME);
            if (movingTime != null) System.out.print(" MovingTime: " + PehoUtils.sec2minSecShort(movingTime));

            System.out.println();
            i++;
        }
        System.out.println("---- END SPLITS ----");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

    public void printLapRecords0 () {
        int i = 0;
        int lapNo = 1;
        try {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("====LAPS IN FILE (lap1)");
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

                Long intensity = mesg.getFieldLongValue(LapMesg.IntensityFieldNum);
                if (intensity != null) System.out.print(" WktIntensity: " + PehoUtils.getLabel(Intensity.class, intensity));

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
        Long startTime = lapRecord.getFieldLongValue(LAP_STIME);
        if (startTime != null) {
            System.out.print(" start@:" + PehoUtils.sec2minSecLong(findTimerBasedOnTime(startTime)) + ", " + FitDateTime.toString(startTime, diffMinutesLocalUTC));
        }

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
        System.out.println("==================================================");
        System.out.println("====LAPS IN FILE (lap2)");

            for (Mesg mesg : lapMesg) {
                printLapRecord(ix);
                ix++;
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
        System.out.println();
        System.out.println("==================================================");
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
        System.out.println("==================================================");
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
    public String createLapSummery() {
        String tempString = "";
        try {
            tempString += "---- ACTIVE LAPS ----" + System.lineSeparator();
            int i = 0;
            int lapNo = 1;
            for (Mesg mesg : lapMesg) {

                Short intensityVal = (Short) mesg.getFieldValue(LAP_INTENSITY);
                String intensity = intensityVal != null ? Intensity.getStringFromValue(Intensity.getByValue(intensityVal)) : "UNKNOWN";

                if ("ACTIVE".equals(intensity)) {
                    tempString += "Lap" + lapNo;

                    // --- lapExtra commented out ---
                    // if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                    //     if (isTreadmillFile()) {
                    //         tempString += " " + lapExtraRecords.get(i).level.intValue() + "%";
                    //     } else {
                    //         tempString += " lv" + lapExtraRecords.get(i).level.intValue();
                    //     }
                    // }

                    tempString += " HR";
                    // tempString += ">st" + lapExtraRecords.get(i).hrStart;

                    Short hrMax = mesg.getFieldShortValue(LAP_MHR);
                    if (hrMax != null) {
                        tempString += "->max" + hrMax;
                    }
                    // tempString += " end" + lapExtraRecords.get(i).hrEnd;

                    Float timer = mesg.getFieldFloatValue(LAP_TIMER);
                    if (timer != null) {
                        tempString += " " + PehoUtils.sec2minSecShort(timer) + "min";
                    }

                    Short cadence = mesg.getFieldShortValue(LAP_CAD);
                    if (cadence != null) {
                        tempString += " " + cadence + "spm";
                    }

                    Float speed = mesg.getFieldFloatValue(LAP_ESPEED);
                    if (speed != null) {
                        if (isSkiErgFile()) {
                            tempString += " " + PehoUtils.sec2minSecLong(500 / speed) + "min/500m";
                        } else {
                            tempString += " " + PehoUtils.sec2minSecLong(1000 / speed) + "min/km";
                            tempString += " " + String.format("%.1fkm/h", speed * 3.60);
                        }
                    }

                    Integer power = mesg.getFieldIntegerValue(LAP_POW);
                    if (power != null) {
                        tempString += " " + power + "W";
                    }

                    Float dist = mesg.getFieldFloatValue(LAP_DIST);
                    if (dist != null) {
                        tempString += " " + String.format("%.1fkm", dist / 1000);
                    }

                    // --- lapExtra commented out ---
                    // if (lapExtraRecords.get(i).avgDragFactor != null && isSkiErgFile()) {
                    //     tempString += " df" + (int) Math.round(lapExtraRecords.get(i).avgDragFactor);
                    // }

                    tempString += System.lineSeparator();
                }
                i++;
                lapNo++;
            }
            tempString += lapEndSum2String(activeAvgCad, activeAvgSpeed, activeAvgPower, activeDist);

            tempString += "---- REST LAPS ----" + System.lineSeparator();
            i = 0;
            lapNo = 1;
            for (Mesg mesg : lapMesg) {

                Short intensityVal = (Short) mesg.getFieldValue(LAP_INTENSITY);
                String intensity = intensityVal != null ? Intensity.getStringFromValue(Intensity.getByValue(intensityVal)) : "UNKNOWN";

                if ("REST".equals(intensity) || "RECOVERY".equals(intensity)) {
                    tempString += "Lap" + lapNo;

                    // --- lapExtra commented out ---
                    // if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                    //     if (isTreadmillFile()) {
                    //         tempString += " " + lapExtraRecords.get(i).level.intValue() + "%";
                    //     } else {
                    //         tempString += " lv" + lapExtraRecords.get(i).level.intValue();
                    //     }
                    // }

                    tempString += " HRst?";
                    Short hrMax = mesg.getFieldShortValue(LAP_MHR);
                    if (hrMax != null) {
                        tempString += "->max" + hrMax;
                    }

                    Float timer = mesg.getFieldFloatValue(LAP_TIMER);
                    if (timer != null) {
                        tempString += " " + PehoUtils.sec2minSecShort(timer) + "min";
                    }

                    Short cadence = mesg.getFieldShortValue(LAP_CAD);
                    if (cadence != null) {
                        tempString += " " + cadence + "spm";
                    }

                    Float speed = mesg.getFieldFloatValue(LAP_ESPEED);
                    if (speed != null) {
                        if (isSkiErgFile()) {
                            tempString += " " + PehoUtils.sec2minSecLong(500 / speed) + "min/500m";
                        } else {
                            tempString += " " + PehoUtils.sec2minSecLong(1000 / speed) + "min/km";
                            tempString += " " + String.format("%.1fkm/h", speed * 3.60);
                        }
                    }

                    Integer power = mesg.getFieldIntegerValue(LAP_POW);
                    if (power != null) {
                        tempString += " " + power + "W";
                    }

                    Float dist = mesg.getFieldFloatValue(LAP_DIST);
                    if (dist != null) {
                        tempString += " " + String.format("%.1fkm", dist / 1000);
                    }

                    tempString += System.lineSeparator();
                }
                i++;
                lapNo++;
            }
            tempString += lapEndSum2String(restAvgCad, restAvgSpeed, restAvgPower, restDist);
        }
        catch (FitRuntimeException e) {
            System.out.println("LAP ERROR!!!!");
        }
        return tempString;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String saveFileInfoBefore() {
        savedFileInfoBefore += "==================================================" + System.lineSeparator();
        savedFileInfoBefore += "Original file info BEFORE update:" + System.lineSeparator();
        savedFileInfoBefore += "==================================================" + System.lineSeparator();
        savedFileInfoBefore += saveFileInfo();
        savedFileInfoBefore += "==================================================" + System.lineSeparator();
        return savedFileInfoBefore;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String saveFileInfoAfter() {
        savedFileInfoAfter += "==================================================" + System.lineSeparator();
        savedFileInfoAfter += "New file info AFTER update:" + System.lineSeparator();
        savedFileInfoAfter += "==================================================" + System.lineSeparator();
        savedFileInfoAfter += saveFileInfo();
        savedFileInfoAfter += "==================================================" + System.lineSeparator();
        return savedFileInfoAfter;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String saveFileInfo() {
        String tempString = "";
        tempString += createFileSummary();
        tempString += createLapSummery();
        return tempString;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printDetailedFileInfo() {
        System.out.println("==================================================");
        System.out.println("Detailed file info:");
        System.out.println("==================================================");
        System.out.print(createFileSummary());
        printFileIdInfo();
        printDeviceInfo();
        printWktInfo();
        printWktSessionInfo();
        printWktStepInfo();
        printSessionInfo();
        printDevDataId();
        printFieldDescr();
        printCourse();
        printSplitSummary();
        printLapRecords0();
        printLapAllSummary();
        printLapLongSummary();
        System.out.print(createLapSummery());
        //printSecRecords0();
        printSessionInfo();

        //printLapRecords();
        //printSecRecords();
        //printLapRecords0();
        //printLapAllSummery();
        //printLapLongSummery();
        //printCourse();
        //printDevDataId();
        //printFieldDescr();

        System.out.println("==================================================");
    }
    
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void saveChanges (Conf conf) {

        boolean encodeWorkoutRecords = true;
        String outputFilePath = "";
        
        //renameDevFieldName();
        
        String orgDateTime = FitDateTime.toString(activityDateTimeLocalOrg);
        String newDateTime = FitDateTime.toString(activityDateTimeLocal);

        String outputFilenameBase = "";

        saveFileInfoAfter();

        outputFilenameBase = getFilenameAndSetNewSportProfileName(conf.getProfileNameSuffix(), outputFilePath);
        //outputFilePath = conf.getFilePathPrefix() + newDateTime + outputFilenameBase + "-mergedJava" + (changedStartTimeBySec/60) + "min.fit";
        outputFilePath = conf.getFilePathPrefix() + newDateTime + outputFilenameBase + "-mergedJava" + (conf.getTimeOffsetSec()/60) + "min.fit";
        
        encodeNewFit(outputFilePath, encodeWorkoutRecords);
        
        PehoUtils.renameFile(conf.getInputFilePath(), conf.getFilePathPrefix() + orgDateTime + outputFilenameBase + "-watch.fit");
        
        //createFileSummary();

        try {
            FileWriter myWriter = new FileWriter(conf.getFilePathPrefix() + newDateTime + outputFilenameBase + "-before.txt");
            myWriter.write(savedFileInfoBefore);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred saving before file.");
            e.printStackTrace();
        }
        try {
            FileWriter myWriter = new FileWriter(conf.getFilePathPrefix() + newDateTime + outputFilenameBase + "-log.txt");
            myWriter.write(savedFileUpdateLogg);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred saving log file.");
            e.printStackTrace();
        }
        try {
            FileWriter myWriter = new FileWriter(conf.getFilePathPrefix() + newDateTime + outputFilenameBase + "-after.txt");
            myWriter.write(savedFileInfoAfter);
            myWriter.write(savedStrLapsActiveInfoShort);
            myWriter.write(savedStrLapsRestInfoShort);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred saving after file.");
            e.printStackTrace();
        }

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
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