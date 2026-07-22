package  se.peho.fittools.core;
import com.garmin.fit.*;

import se.peho.fittools.core.strings.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class FitFile {

    public static final int FID_CTIME = FileIdMesg.TimeCreatedFieldNum; //long
    public static final int FID_MANU = FileIdMesg.ManufacturerFieldNum; //int
    public static final int FID_PROD = FileIdMesg.ProductFieldNum; //int
    public static final int FID_PRODNAME = FileIdMesg.ProductNameFieldNum; //string
    public static final int DINFO_TIME = DeviceInfoMesg.TimestampFieldNum; //long
    public static final int DINFO_SWVER = DeviceInfoMesg.SoftwareVersionFieldNum; //float
    public static final int ACT_TIME = ActivityMesg.TimestampFieldNum; //long
    public static final int ACT_LOCTIME = ActivityMesg.LocalTimestampFieldNum; //long
    public static final int ACT_TIMER = ActivityMesg.TotalTimerTimeFieldNum; //long
    public static final int DEVID_APPID = DeveloperDataIdMesg.ApplicationIdFieldNum; //enum
    public static final int WKT_NAME = WorkoutMesg.WktNameFieldNum; //string
    public static final int WKT_SPORT = WorkoutMesg.SportFieldNum; //long
    public static final int WKT_SUBSPORT = WorkoutMesg.SubSportFieldNum; //long
    public static final int WKT_NUMSTEPS = WorkoutMesg.NumValidStepsFieldNum; //int
    public static final int WKTST_DURTYPE = WorkoutStepMesg.DurationTypeFieldNum; //short
    public static final int WKTST_DURVALUE = WorkoutStepMesg.DurationValueFieldNum; //long
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
    public static final int SES_LAPS = SessionMesg.NumLapsFieldNum; //int
    public static final int EVE_TIME = EventMesg.TimestampFieldNum; //long
    public static final int EVE_STIME = EventMesg.StartTimestampFieldNum; //long
    public static final int EVE_EVENT = EventMesg.EventFieldNum; //long
    public static final int EVE_TYPE = EventMesg.EventTypeFieldNum; //long
    public static final int SPL_STIME = SplitMesg.StartTimeFieldNum; //long
    public static final int SPL_ETIME = SplitMesg.EndTimeFieldNum; //long
    public static final int SPL_MESSAGE_INDEX = SplitMesg.MessageIndexFieldNum; // int
    public static final int SPL_TYPE = SplitMesg.SplitTypeFieldNum; // enum
    public static final int SPL_TIMER = SplitMesg.TotalTimerTimeFieldNum; // float
    public static final int SPL_SDIST = 7; // float
    public static final int SPL_DIST = SplitMesg.TotalDistanceFieldNum; // float
    public static final int SPL_SPEED = SplitMesg.AvgSpeedFieldNum; // float
    public static final int SPL_MSPEED = SplitMesg.MaxSpeedFieldNum; // float
    public static final int SPL_VSPEED = SplitMesg.AvgVertSpeedFieldNum; // float
    public static final int SPL_SELE = SplitMesg.StartElevationFieldNum; // int
    public static final int SPL_ASC = SplitMesg.TotalAscentFieldNum; // int
    public static final int SPL_DESC = SplitMesg.TotalDescentFieldNum; // int
    // public static final int SPL_START_LAT = SplitMesg.StartPositionLatFieldNum; // int (semicircles)
    // public static final int SPL_START_LON = SplitMesg.StartPositionLongFieldNum; // int (semicircles)
    // public static final int SPL_END_LAT = SplitMesg.EndPositionLatFieldNum; // int (semicircles)
    // public static final int SPL_END_LON = SplitMesg.EndPositionLongFieldNum; // int (semicircles)
    public static final int SPL_SLAT = SplitMesg.StartPositionLatFieldNum; // int (semicircles)
    public static final int SPL_SLON = SplitMesg.StartPositionLongFieldNum; // int (semicircles)
    public static final int SPL_ELAT = SplitMesg.EndPositionLatFieldNum; // int (semicircles)
    public static final int SPL_ELON = SplitMesg.EndPositionLongFieldNum; // int (semicircles)
    public static final int SPL_ETIMER = SplitMesg.TotalElapsedTimeFieldNum; // float
    public static final int SPL_MTIMER = SplitMesg.TotalMovingTimeFieldNum; // float
    public static final int SPL_TOTAL_CALORIES = SplitMesg.TotalCaloriesFieldNum; // int
    public static final int SPL_START_ELEVATION = SplitMesg.StartElevationFieldNum; // int
    public static final int SPL_TOTAL_MOVING_TIME = SplitMesg.TotalMovingTimeFieldNum; // float
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
    public static final int LAP_IX = LapMesg.MessageIndexFieldNum; //int
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
    public static final int MESG_TIMESTAMP = 253; // standard FIT timestamp field
    public static final int SP_SPORT = SportMesg.SportFieldNum; //short -> .getByValue -> Sport
    public static final int SP_SUBSPORT = SportMesg.SubSportFieldNum;
    public static final int SP_NAME = SportMesg.NameFieldNum; //string
    public static final int TIZ_TIME = TimeInZoneMesg.TimestampFieldNum; //long 253
    public static final int TIZ_REF_MESG = TimeInZoneMesg.ReferenceMesgFieldNum; //int 0
    public static final int TIZ_REF_IX = TimeInZoneMesg.ReferenceIndexFieldNum; //int 1
    public static final int TC_TIME = TimestampCorrelationMesg.TimestampFieldNum; //long 253
    public static final int TC_STIME = TimestampCorrelationMesg.SystemTimestampFieldNum; //long 1
    public static final int TC_LTIME = TimestampCorrelationMesg.LocalTimestampFieldNum; //long 3

    private List<Mesg> allMesg = new ArrayList<>();
    private List<Mesg> fileIdMesg = new ArrayList<>();
    private List<Mesg> deviceInfoMesg = new ArrayList<>();
    private List<Mesg> sportMesg = new ArrayList<>();
    public List<Mesg> getAllMesg() { return allMesg; }
    public List<Mesg> getFileIdMesg() { return fileIdMesg; }
    public List<Mesg> getDeviceInfoMesg() { return deviceInfoMesg; }
    public List<Mesg> getSportMesg() { return sportMesg; }

    private List<Mesg> wktSessionMesg = new ArrayList<>();
    private List<Mesg> wktStepMesg = new ArrayList<>();
    private List<Mesg> wktRecordMesg = new ArrayList<>();
    public List<Mesg> getWktSessionMesg() { return wktSessionMesg; }
    public List<Mesg> getWktStepMesg() { return wktStepMesg; }
    public List<Mesg> getWktRecordMesg() { return wktRecordMesg; }

    private List<Mesg> activityMesg = new ArrayList<>();
    private List<Mesg> sessionMesg = new ArrayList<>();
    public List<Mesg> getActivityMesg() { return activityMesg; }
    public List<Mesg> getSessionMesg() { return sessionMesg; }
    
    private List<Mesg> splitMesg = new ArrayList<>();
    private List<Mesg> splitSummaryMesg = new ArrayList<>();
    private List<Mesg> lapMesg = new ArrayList<>();
    public List<Mesg> getSplitMesg() { return splitMesg; }
    public List<Mesg> getSplitSummaryMesg() { return splitSummaryMesg; }
    public List<Mesg> getLapMesg() { return lapMesg; }

    private List<Mesg> eventMesg = new ArrayList<>();
    private List<Mesg> eventTimerMesg = new ArrayList<>();
    public List<Mesg> getEventMesg() { return eventMesg; }
    public List<Mesg> getEventTimerMesg() { return eventTimerMesg; }

    private List<Mesg> recordMesg = new ArrayList<>();
    private List<Mesg> devDataIdMesg = new ArrayList<>();
    //private List<Mesg> developerDataIdMesg = new ArrayList<>();
    private List<Mesg> fieldDescrMesg = new ArrayList<>();
    public List<Mesg> getRecordMesg() { return recordMesg; }
    public List<Mesg> getDevDataIdMesg() { return devDataIdMesg; }
    public List<Mesg> getFieldDescrMesg() { return fieldDescrMesg; }

    private List<PauseMesg> pauseRecords = new ArrayList<>(); //Not Garmin SDK
    private List<GapMesg> gapRecords = new ArrayList<>(); //Not Garmin SDK
    private List<RecordMesgAddOnRecords> recordMesgAddOnRecords = new ArrayList<>(); //Not Garmin SDK
    private List<RecordExtraMesg> secExtraRecords = new ArrayList<>(); //Not Garmin SDK
    private List<LapExtraMesg> lapExtraRecords = new ArrayList<>(); //Not Garmin SDK
    public List<PauseMesg> getPauseList() { return pauseRecords; }
    public List<GapMesg> getGapList() { return gapRecords; }
    public List<RecordMesgAddOnRecords> getRecordMesgAddOnRecords() { return recordMesgAddOnRecords; }
    public List<RecordExtraMesg> getSecExtraRecords() { return secExtraRecords; }
    public List<LapExtraMesg> getLapExtraRecords() { return lapExtraRecords; }

    private Integer manufacturerNo;
    private String manufacturer;
    private int productNo;
    private String product = "";
    private Float swVer;
    public Integer getManufacturerNo() { return manufacturerNo; }
    public void setManufacturerNo(Integer manufacturerNo) { this.manufacturerNo = manufacturerNo; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public int getProductNo() { return productNo; }
    public void setProductNo(int productNo) { this.productNo = productNo; }
    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }
    public Float getSwVer() { return swVer; }
    public void setSwVer(Float swVer) { this.swVer = swVer; }

    private Long activityDateTimeUTC;  // Original file
    private Long activityDateTimeLocal; // Original file
    private Long activityDateTimeLocalOrg; // Original file
    private Long diffMinutesLocalUTC;
    public Long getActivityDateTimeUTC() { return activityDateTimeUTC; }
    public void setActivityDateTimeUTC(Long activityDateTimeUTC) { this.activityDateTimeUTC = activityDateTimeUTC; }
    public Long getActivityDateTimeLocal() { return activityDateTimeLocal; }
    public void setActivityDateTimeLocal(Long activityDateTimeLocal) { this.activityDateTimeLocal = activityDateTimeLocal; }
    public Long getActivityDateTimeLocalOrg() { return activityDateTimeLocalOrg; }
    public void setActivityDateTimeLocalOrg(Long activityDateTimeLocalOrg) { this.activityDateTimeLocalOrg = activityDateTimeLocalOrg; }

    Long timeFirstRecord;
    Long timeFirstRecordOrg;   // Original file
    Long timeLastRecord;
    public Long getTimeFirstRecord() { return timeFirstRecord; }
    public void setTimeFirstRecord(Long timeFirstRecord) { this.timeFirstRecord = timeFirstRecord; }
    public Long getTimeFirstRecordOrg() { return timeFirstRecordOrg; }
    public void setTimeFirstRecordOrg(Long timeFirstRecordOrg) { this.timeFirstRecordOrg = timeFirstRecordOrg; }
    public Long getTimeLastRecord() { return timeLastRecord; }
    public void setTimeLastRecord(Long timeLastRecord) { this.timeLastRecord = timeLastRecord; }
    public Long getDiffMinutesLocalUTC() { return diffMinutesLocalUTC; }
    public void setDiffMinutesLocalUTC(Long diffMinutesLocalUTC) { this.diffMinutesLocalUTC = diffMinutesLocalUTC; }

    private int numberOfRecords;
    private int numberOfLaps;
    public int getNumberOfRecords() { return numberOfRecords; }
    public void setNumberOfRecords(int numberOfRecords) { this.numberOfRecords = numberOfRecords; }
    public int getNumberOfLaps() { return numberOfLaps; }
    public void setNumberOfLaps(int numberOfLaps) { this.numberOfLaps = numberOfLaps; }

    String wktName;
    Sport sport;
    SubSport subsport;
    String sportProfile;
    public String getWktName() { return wktName; }
    public void setWktName(String wktName) { this.wktName = wktName; }
    public Sport getSport() { return sport; }
    public void setSport(Sport sport) { this.sport = sport; }
    public SubSport getSubsport() { return subsport; }
    public void setSubsport(SubSport subsport) { this.subsport = subsport; }
    public String getSportProfile() { 
        return (sessionMesg.isEmpty() || sessionMesg.get(0).getFieldStringValue(SES_PROFILE) == null) ? 
            "noProfile" : 
            sessionMesg.get(0).getFieldStringValue(SES_PROFILE).trim(); }
    public void setSportProfile(String sportProfile) { 
        this.sportProfile = sportProfile; 
        if (!sessionMesg.isEmpty()) {
            this.sessionMesg.get(0).setFieldValue(SES_PROFILE, sportProfile);
        }
    }

    Float totalTimerTime; //ActivityMesg, excl pauses
    Float totalDistance;
    Float totalDistanceOrg;
    public Float getTotalTimerTime() { return totalTimerTime; }
    public void setTotalTimerTime(Float totalTimerTime) { this.totalTimerTime = totalTimerTime; }
    public Float getTotalDistance() { return totalDistance; }
    public void setTotalDistance(Float totalDistance) { this.totalDistance = totalDistance; }
    public Float getTotalDistanceOrg() { return totalDistanceOrg; }
    public void setTotalDistanceOrg(Float totalDistanceOrg) { this.totalDistanceOrg = totalDistanceOrg; }

    Float avgSpeed; // m/s
    Float maxSpeed; // m/s
    int avgCadence;
    int avgPower;
    public Float getAvgSpeed() { return avgSpeed; }
    public void setAvgSpeed(Float avgSpeed) { this.avgSpeed = avgSpeed; }
    public Float getMaxSpeed() { return maxSpeed; }
    public void setMaxSpeed(Float maxSpeed) { this.maxSpeed = maxSpeed; }
    public int getAvgCadence() { return avgCadence; }
    public void setAvgCadence(int avgCadence) { this.avgCadence = avgCadence; }
    public int getAvgPower() { return avgPower; }
    public void setAvgPower(int avgPower) { this.avgPower = avgPower; }


    // private Boolean isSkiErg = false;
    // private Boolean isElliptical = false;
    // private Boolean isTreadmill = false;
    // public Boolean getIsSkiErg() { return isSkiErg; }
    // public Boolean getIsElliptical() { return isElliptical; }
    // public Boolean getIsTreadmill() { return isTreadmill; }
    // public void setIsSkiErg(Boolean isSkiErg) { this.isSkiErg = isSkiErg; }
    // public void setIsElliptical(Boolean isElliptical) { this.isElliptical = isElliptical; }
    // public void setIsTreadmill(Boolean isTreadmill) { this.isTreadmill = isTreadmill; }

    public enum MySport { SKIERG, ELLIPTICAL, TREADMILL, OTHER }
    private MySport mySport = MySport.OTHER;
    public MySport getMySport() { return mySport; }
    public void setMySport(MySport mySport) { this.mySport = mySport; }

    // SimpleDateFormat sweDateTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    // int maxIxFixEmptyBeginning = 100;
    // int maxCadenceValue = 74;
    // boolean lookingInBeginningForEmptySpeed = true;
    // boolean lookingInBeginningForEmptyCadence = true;
    // boolean lookingInBeginningForEmptyPower = true;
    // boolean lookingInBeginningForEmptyStrokeLength = true;
    // boolean lookingInBeginningForEmptyDragFactor = true;
    // boolean lookingInBeginningForEmptyTrainingSession = true;
    
    private Float activeTime = 0f;
    private Float restTime = 0f;
    private Float activeDist = 0f;
    private Float restDist = 0f;
    public Float getActiveTime() { return activeTime; }
    public void setActiveTime(Float activeTime) { this.activeTime = activeTime; }
    public Float getRestTime() { return restTime; }
    public void setRestTime(Float restTime) { this.restTime = restTime; }
    public Float getActiveDist() { return activeDist; }
    public void setActiveDist(Float activeDist) { this.activeDist = activeDist; }
    public Float getRestDist() { return restDist; }
    public void setRestDist(Float restDist) { this.restDist = restDist; }

    private Float activeAvgSpeed = 0f;
    private Float activeAvgCad = 0f;
    private Float activeAvgPower = 0f;
    private Float restAvgSpeed = 0f;
    private Float restAvgCad = 0f;
    private Float restAvgPower = 0f;
    public Float getActiveAvgSpeed() { return activeAvgSpeed; }
    public void setActiveAvgSpeed(Float activeAvgSpeed) { this.activeAvgSpeed = activeAvgSpeed; }
    public Float getActiveAvgCad() { return activeAvgCad; }
    public void setActiveAvgCad(Float activeAvgCad) { this.activeAvgCad = activeAvgCad; }
    public Float getActiveAvgPower() { return activeAvgPower; }
    public void setActiveAvgPower(Float activeAvgPower) { this.activeAvgPower = activeAvgPower; }
    public Float getRestAvgCad() { return restAvgCad; }
    public void setRestAvgCad(Float restAvgCad) { this.restAvgCad = restAvgCad; }
    public Float getRestAvgSpeed() { return restAvgSpeed; }
    public void setRestAvgSpeed(Float restAvgSpeed) { this.restAvgSpeed = restAvgSpeed; }
    public Float getRestAvgPower() { return restAvgPower; }
    public void setRestAvgPower(Float restAvgPower) { this.restAvgPower = restAvgPower; }

    private String activityNameSuffix;
    public String getActivityNameSuffix() { return activityNameSuffix; }
    public void setActivityNameSuffix(String activityNameSuffix) { this.activityNameSuffix = activityNameSuffix; }

    private String savedFileInfoBefore = "";
    public String getSavedFileInfoBefore() { return savedFileInfoBefore; }
    public void setSavedFileInfoBefore(String savedFileInfoBefore) { this.savedFileInfoBefore = savedFileInfoBefore; }
    public void appendSavedFileInfoBefore(String text) { this.savedFileInfoBefore += text; }
    public void appendSavedFileInfoBeforeLn(String text) { this.savedFileInfoBefore += text + System.lineSeparator(); }
    public void clearSavedFileInfoBefore() { this.savedFileInfoBefore = ""; }
    
    private String savedFileInfoAfter = "";
    public String getSavedFileInfoAfter() { return savedFileInfoAfter; }
    public void setSavedFileInfoAfter(String savedFileInfoAfter) { this.savedFileInfoAfter = savedFileInfoAfter; }
    public void appendSavedFileInfoAfter(String text) { this.savedFileInfoAfter += text; }
    public void appendSavedFileInfoAfterLn(String text) { this.savedFileInfoAfter += text + System.lineSeparator(); }
    public void clearSavedFileInfoAfter() { this.savedFileInfoAfter = ""; }


    private String savedStrLapsActiveInfoShort = "";
    private String savedStrLapsRestInfoShort = "";

    private String updateLog = "";
    public String getUpdateLog() { return updateLog; }
    public void setUpdateLog(String savedFileUpdateLogg) { this.updateLog = savedFileUpdateLogg; }
    public void appendUpdateLog(String text) { this.updateLog += text; }
    public void appendUpdateLogLn(String text) { this.updateLog += text + System.lineSeparator(); }
    public void clearUpdateLog() { this.updateLog = ""; }

    private String tempUpdateLog = "";
    public String getTempUpdateLog() { return tempUpdateLog; }
    public void setTempUpdateLog(String tempUpdateLogg) { this.tempUpdateLog = tempUpdateLogg; }
    public void appendTempUpdateLog(String text) { this.tempUpdateLog += text; }
    public void appendTempUpdateLogLn(String text) { this.tempUpdateLog += text + System.lineSeparator(); }
    public void clearTempUpdateLog() { this.tempUpdateLog = ""; }

    //public int getChangedStartTimeBySec() { return changedStartTimeBySec; }
    //public void setChangedStartTimeBySec(int changedStartTimeBySec) { this.changedStartTimeBySec = changedStartTimeBySec; }
    
    public LapReportGenerator getLapReportGenerator() { return new LapReportGenerator(this); }
    public PauseReportGenerator getPauseReportGenerator() { return new PauseReportGenerator(this); }
    public CPointReportGenerator getCPointReportGenerator() { return new CPointReportGenerator(this); }
    public LapFix getLapFix() { return new LapFix(this); }
    public PauseFix getPauseFix() { return new PauseFix(this); }
    public CPointFix getCPointFix() { return new CPointFix(this); }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public FitFile () {
    }

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
            if (this.altStop != null && this.altStart != null) {
                this.altPause = this.altStop - this.altStart;
            } else {
                this.altPause = 0f;
            }
        }
        

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    class LapExtraMesg {
        private Short hrStart;
        private Short hrEnd;
        private Short hrMin;
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

        public LapExtraMesg() {
        }
        public LapExtraMesg(Short hrStart, Short hrEnd, Short hrMin, Long timeEnd, int lapNo, int recordIxStart, 
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

        public Short getHrStart() { return hrStart; }
        public void setHrStart(Short hrStart) { this.hrStart = hrStart; }
        public Short getHrEnd() { return hrEnd; }
        public void setHrEnd(Short hrEnd) { this.hrEnd = hrEnd; }
        public Short getHrMin() { return hrMin; }
        public void setHrMin(Short hrMin) { this.hrMin = hrMin; }
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
            LapExtraMesg newLapExtra = new LapExtraMesg((short) hrStart, (short) hrEnd, (short) hrMin, timeEnd, lapNo, recordIxStart, recordIxEnd, stepLen, level, avgStrokeLen, maxStrokeLen, avgDragFactor, maxDragFactor);
            newLapExtra.setSpeedLapSum(0f);
            newLapExtra.setCadLapSum(0f);
            lapExtraRecords.add(newLapExtra);
        }
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
    public void fillLapExtraRecords() {
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

        Float activeSumSpeed = 0f;
        Float activeSumCad = 0f;
        Float activeSumPower = 0f;
        Float restSumSpeed = 0f;
        Float restSumCad = 0f;
        Float restSumPower = 0f;
        setActiveTime(0f);
        setRestTime(0f);
        setActiveDist(0f);
        setRestDist(0f);

        lapExtraRecords.clear();

        // nextLapStartTime from lapMesg start time field
        nextLapStartTime = (lapMesg.isEmpty() || lapMesg.get(0).getFieldLongValue(LAP_STIME) == null) ? 
            getTimeFirstRecord() : 
            lapMesg.get(0).getFieldLongValue(LAP_STIME);

        Boolean nextRecordLastInLap = false;

        for (Mesg record : recordMesg) {

            Short currentHr = record.getFieldShortValue(REC_HR) == null ? (short) 60 : record.getFieldShortValue(REC_HR);
            //--------------
            // IF LAP START
            currentTimeStamp = record.getFieldLongValue(REC_TIME);
            if (currentTimeStamp != null && nextLapStartTime != null && currentTimeStamp.equals(nextLapStartTime)) {

                LapExtraMesg newLapExtra = new LapExtraMesg();
                lapExtraRecords.add(newLapExtra);

                // Save HR and recordIx START
                lapExtraRecords.get(lapIx).setHrStart(currentHr);
                lapExtraRecords.get(lapIx).setRecordIxStart(recordIx);

                // Get LAP DATA to be used to find lap-start-end
                Float lapTotalTimer = lapMesg.get(lapIx).getFieldFloatValue(LAP_TIMER);
                currentLapTime = (lapTotalTimer == null) ? 0f : lapTotalTimer;

                Short lapIntensityShort = lapMesg.get(lapIx).getFieldShortValue(LAP_INTENSITY);
                Intensity lapIntensity = (lapIntensityShort == null) ? Intensity.INVALID : Intensity.getByValue(lapIntensityShort);
                currentLapIntensity = Intensity.getStringFromValue(lapIntensity);

                // Save LAP END to table (DateTime)
                lapExtraRecords.get(lapIx).setTimeEnd(currentLapTimeEnd);
                if (lapNo < numberOfLaps) {
                    currentLapTimeEnd = lapMesg.get(lapIx + 1).getFieldLongValue(LAP_STIME) - 1;
                    nextLapStartTime = lapMesg.get(lapIx + 1).getFieldLongValue(LAP_STIME);
                } else {
                    currentLapTimeEnd = timeLastRecord;
                }

                // Save LAP END to table (DateTime)
                lapExtraRecords.get(lapIx).setTimeEnd(currentLapTimeEnd);
            }

            // Find out if next record is first record in next Lap
            if ((recordIx + 1 >= recordMesg.size()) || 
                    (recordMesg.get(recordIx + 1).getFieldLongValue(REC_TIME) > currentLapTimeEnd)) {
                nextRecordLastInLap = true;
            } else {
                nextRecordLastInLap = false;
            }

            // Calc LAP HR min
            if (lapExtraRecords.get(lapIx).getHrMin() == null) {
                lapExtraRecords.get(lapIx).setHrMin(currentHr);
            } else if (currentHr < lapExtraRecords.get(lapIx).getHrMin()) {
                lapExtraRecords.get(lapIx).setHrMin(currentHr);
            }

            // Calc LAPSUM & MLAPMAX for Developer fields from current record
            Mesg secRecForDev = record; // Mesg.get(recordIx); // Why not just use 'record'???
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

            // --------------
            // IF LAP END
            //if (currentTimeStamp != null && currentLapTimeEnd != null && currentTimeStamp.equal(currentLapTimeEnd)) {
            if (currentTimeStamp != null && currentLapTimeEnd != null && nextRecordLastInLap) {

                // Save HR and recordIx END
                lapExtraRecords.get(lapIx).setHrEnd(currentHr);
                lapExtraRecords.get(lapIx).setRecordIxEnd(recordIx);
                lapExtraRecords.get(lapIx).setTimeEnd(record.getFieldLongValue(REC_TIME));

                Float lapCad = lapMesg.get(lapIx).getFieldFloatValue(LAP_CAD);
                Float lapTimer = lapMesg.get(lapIx).getFieldFloatValue(LAP_TIMER);
                Float lapDist = lapMesg.get(lapIx).getFieldFloatValue(LAP_DIST);
                if (lapCad != null && lapCad > 0 && lapTimer != null && lapTimer > 0 && lapDist != null && lapDist > 0) {
                    getLapExtraRecords().get(lapIx).setStepLen(lapDist / ( lapCad * lapTimer / 60 )); // step length acc to FFRT
                }

                // Calc LAP SUM & LAP MAX
                for (DeveloperField field : recordMesg.get(recordIx).getDeveloperFields()) {
                    if ("StrokeLength".equals(field.getName())) {
                        lapExtraRecords.get(lapIx).setAvgStrokeLen(
                            (float) Math.round(100 * currentLapSumStrokeLen 
                                / (recordIx-lapExtraRecords.get(lapIx).getRecordIxStart()+1)) 
                                /100);
                        lapExtraRecords.get(lapIx).setMaxStrokeLen(currentLapMaxStrokeLen);
                        currentLapSumStrokeLen = 0f;
                        currentLapMaxStrokeLen = 0f;
                    }
                    if ("DragFactor".equals(field.getName())) {
                        lapExtraRecords.get(lapIx).setAvgDragFactor(
                            (float) Math.round(100 * currentLapSumDragFactor 
                                / (recordIx-lapExtraRecords.get(lapIx).getRecordIxStart()+1)) 
                                /100);
                        lapExtraRecords.get(lapIx).setMaxDragFactor(currentLapMaxDragFactor);
                        currentLapSumDragFactor = 0f;
                        currentLapMaxDragFactor = 0f;
                    }
                    if ("Level".equals(field.getName())) {
                        getLapExtraRecords().get(lapIx).setLevel(field.getFloatValue());
                    }
                }

                Float lapTotalTimerTime = lapMesg.get(lapIx).getFieldFloatValue(LAP_TIMER);
                Float lapTotDist = lapMesg.get(lapIx).getFieldFloatValue(LAP_DIST);
                Short lapAvgCad = lapMesg.get(lapIx).getFieldShortValue(LAP_CAD);
                Integer lapAvgPow = lapMesg.get(lapIx).getFieldIntegerValue(LAP_POW);
                Float lapAvgSpeedF = lapMesg.get(lapIx).getFieldFloatValue(LAP_SPEED);

                // --------------
                // Calculate ACTIVE LAP SUM & MAX
                if ("ACTIVE".equals(currentLapIntensity)) {
                    if (lapTotalTimerTime != null) setActiveTime(getActiveTime() + lapTotalTimerTime);
                    if (lapTotDist != null) setActiveDist(getActiveDist() + lapTotDist);
                    if (lapAvgSpeedF != null && lapTotalTimerTime != null) activeSumSpeed += lapAvgSpeedF * lapTotalTimerTime;
                    if (lapAvgCad != null && lapTotalTimerTime != null) activeSumCad += lapAvgCad * lapTotalTimerTime;
                    if (lapAvgPow != null && lapTotalTimerTime != null) activeSumPower += lapAvgPow * lapTotalTimerTime;
                }

                // Calculate REST LAP SUM & MAX
                if ("REST".equals(currentLapIntensity) || "RECOVERY".equals(currentLapIntensity)) {
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
        
        // Calculate ACTIVE LAP SUM & MAX
        // activeAvgSpeed = (float) (activeSumSpeed / getActiveTime());
        activeAvgSpeed = (float) (getActiveDist() / getActiveTime());
        activeAvgCad = (float) (activeSumCad / getActiveTime());
        activeAvgPower = (float) (activeSumPower / getActiveTime());

        // Calculate REST LAP SUM & MAX
        restAvgSpeed = (float) (getRestDist() / getRestTime());
        restAvgCad = (float) (restSumCad / getRestTime());
        restAvgPower = (float) (restSumPower / getRestTime());

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void addTimeToLapAndSession(int lapIx, Float timeToAdd) {
        // Updating LAP DATA
        //------------------
        Float lapTime = getLapMesg().get(lapIx).getFieldFloatValue(LAP_TIMER) + timeToAdd;
        getLapMesg().get(lapIx).setFieldValue(LAP_TIMER, (lapTime));
        //getLapMesg().get(pauseToShorten.getIxLap()).setFieldValue(LAP_ETIMER, (lapTime - secondsToPutIntoPause));
        
        // Get new LAP dist because updated in addDistToRecords 
        Float lapDist = getLapMesg().get(lapIx).getFieldFloatValue(LAP_DIST);
        getLapMesg().get(lapIx).setFieldValue(LAP_SPEED, (lapDist / lapTime));
        getLapMesg().get(lapIx).setFieldValue(LAP_ESPEED, (lapDist / lapTime));

        //LAP dist and speed is updated in addDistToRecords
        //getLapMesg().get().setFieldValue(LAP_DIST, (lapDist));

        // Updating SESSION DATA
        //----------------------
        setTotalTimerTime(getTotalTimerTime() + (float) timeToAdd);
        getSessionMesg().get(0).setFieldValue(SES_TIMER, getTotalTimerTime());
        getActivityMesg().get(0).setFieldValue(ACT_TIMER, getTotalTimerTime());
        Float sesMTimer = getSessionMesg().get(0).getFieldFloatValue(SES_MTIMER);
        if (sesMTimer != null) {
            getSessionMesg().get(0).setFieldValue(SES_MTIMER, sesMTimer + timeToAdd);
        }
        //elapsedTimerTime -= (float) secondsToPutIntoPause;
        //getSessionMesg().get(0).setFieldValue(SES_ETIMER, elapsedTimerTime);

        //SESSION dist and speed is updated in addDistToRecords
        //setTotalDistance(getRecordMesg().get(getNumberOfRecords()-1).getFieldFloatValue(RecordMesg.DistanceFieldNum));
        //getSessionMesg().get(0).setFieldValue(SES_DIST, getTotalDistance());

        // Updating SESS speed again, even if updated in addDistToRecords 
        Float oldAvgSpeed = getAvgSpeed();
        setAvgSpeed(getTotalDistance() / getTotalTimerTime());
        getSessionMesg().get(0).setFieldValue(SES_SPEED, getAvgSpeed());
        getSessionMesg().get(0).setFieldValue(SES_ESPEED, getAvgSpeed());
        appendTempUpdateLog("Increasing SESSION_SPEED from " + oldAvgSpeed + "m/s" 
            + " / " + PehoUtils.mps2minpkm(oldAvgSpeed) + "min/km");
        appendTempUpdateLogLn(" to " + getAvgSpeed() + "m/s" 
            + " / " + PehoUtils.mps2minpkm(getAvgSpeed()) + "min/km");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void addDistToRecords(int fromRecordIx, Float distToAdd) {
        Float fromDist = null;
        if (fromRecordIx >= 0 && fromRecordIx < getNumberOfRecords()) {
            fromDist = recordMesg.get(fromRecordIx).getFieldFloatValue(REC_DIST);
        }

        Float recordDist;
        //  +1 cause not to update 3 new records
        int ix = 0;
        for (ix = fromRecordIx; ix < getNumberOfRecords(); ix++) {
            recordDist = recordMesg.get(ix).getFieldFloatValue(REC_DIST);
            recordMesg.get(ix).setFieldValue(REC_DIST, (recordDist + distToAdd));
        }

        /* for (int lapIdx = 0; lapIdx < lapMesg.size(); lapIdx++) {
            Mesg lap = lapMesg.get(lapIdx);
            System.out.println("LAP " + lapIdx
                    + " ix=" + lap.getFieldIntegerValue(LAP_IX)
                    + " dist=" + PehoUtils.m2km2(lap.getFieldFloatValue(LAP_DIST))
                    + " avgSpd=" + PehoUtils.mps2minpkm(lap.getFieldFloatValue(LAP_SPEED))
                    + " enhAvgSpd=" + PehoUtils.mps2minpkm(lap.getFieldFloatValue(LAP_ESPEED)));
        } */

        // Update the lap that contains fromRecordIx
        if (fromRecordIx < getNumberOfRecords() && !lapMesg.isEmpty()) {
            Long fromRecordTime = recordMesg.get(fromRecordIx).getFieldLongValue(REC_TIME);
            int affectedLapIx = 0;
            for (int lapIdx = 0; lapIdx < lapMesg.size(); lapIdx++) {
                Long lapSTime = lapMesg.get(lapIdx).getFieldLongValue(LAP_STIME);
                if (lapSTime != null && fromRecordTime != null && lapSTime <= fromRecordTime) {
                    affectedLapIx = lapIdx;
                } else {
                    break;
                }
            }
            Float lapDist = lapMesg.get(affectedLapIx).getFieldFloatValue(LAP_DIST);
            Float newLapDist = lapDist + distToAdd;
            Float lapTimer = lapMesg.get(affectedLapIx).getFieldFloatValue(LAP_TIMER);
            lapMesg.get(affectedLapIx).setFieldValue(LAP_DIST, newLapDist);
            //lapMesg.get(affectedLapIx).setFieldValue(LAP_SPEED, newLapDist / lapTimer);
            lapMesg.get(affectedLapIx).setFieldValue(LAP_ESPEED, newLapDist / lapTimer);
            appendTempUpdateLog("Updating LAP Ix: " + affectedLapIx + " DIST from " + lapDist);
            appendTempUpdateLogLn(" to " + newLapDist + "m, speed: " + PehoUtils.mps2minpkm(newLapDist / lapTimer) + "min/km");
        }

        /* System.out.println("FromDist: " + PehoUtils.m2km2(fromDist) + ", distToAdd: " + PehoUtils.m2km2(distToAdd));
        for (int lapIdx = 0; lapIdx < lapMesg.size(); lapIdx++) {
            Mesg lap = lapMesg.get(lapIdx);
            System.out.println("LAP " + lapIdx
                    + " ix=" + lap.getFieldIntegerValue(LAP_IX)
                    + " dist=" + PehoUtils.m2km2(lap.getFieldFloatValue(LAP_DIST))
                    + " avgSpd=" + PehoUtils.mps2minpkm(lap.getFieldFloatValue(LAP_SPEED))
                    + " enhAvgSpd=" + PehoUtils.mps2minpkm(lap.getFieldFloatValue(LAP_ESPEED)));
        } */

        // Update split distances using fromDist reference:
        // 1) if fromDist is inside split [startDist, totalDist], update totalDist.
        // 2) if split startDist is after fromDist, shift startDist.
        if (fromDist != null) {
            //int splitStartDistFieldNum = -1;
            int updatedSplitTotals = 0;
            int updatedSplitStarts = 0;
            int updatedSplitSummaries = 0;

            // Read split summaries and group by split type for easier access when updating summaries.
            // ------------------------------------------------------------------
            java.util.Map<Short, java.util.List<Mesg>> splitSummaryByType = new java.util.HashMap<>();
            for (Mesg mesg : allMesg) {
                if (mesg.getNum() == MesgNum.SPLIT_SUMMARY) {
                    Short summaryType = mesg.getFieldShortValue(SPLSUM_TYPE);
                    if (summaryType != null) {
                        splitSummaryByType.computeIfAbsent(summaryType, k -> new java.util.ArrayList<>()).add(mesg);
                    }
                }
            }
            java.util.Map<Short, Integer> splitTypeOccurrence = new java.util.HashMap<>();

            /* for (Mesg split : splitMesg) {
                Short splitType = split.getFieldShortValue(SPL_TYPE);
                System.out.println("SPLIT"
                        + " startTime=" + FitDateTime.toStringTime(split.getFieldLongValue(SPL_STIME), diffMinutesLocalUTC)
                        + " totalTime=" + FitDateTime.toTimerString(split.getFieldLongValue(SPL_TIMER))
                        + " startDist=" + PehoUtils.m2km2(split.getFieldFloatValue(SPL_SDIST)/100)
                        + " totalDist=" + PehoUtils.m2km2(split.getFieldFloatValue(SPL_DIST))
                    + " type=" + splitType + "(" + (splitType != null ? SplitType.getByValue(splitType) : "unknown") + ")"
                    );
            } */

            for (Mesg split : splitMesg) {
                /* if (splitStartDistFieldNum < 0) {
                    for (Field field : split.getFields()) {
                        if (field != null && field.getName() != null) {
                            String normalized = field.getName().replaceAll("[^A-Za-z0-9]", "").toLowerCase();
                            //System.out.println("Checking split field: " + field.getName() + " normalized: " + normalized);
                            if (normalized.contains("start") && normalized.contains("dist")) {
                                splitStartDistFieldNum = field.getNum();
                                break;
                            }
                        }
                    }
                } */

                Float splitStartDist = split.getFieldFloatValue(SPL_SDIST) / 100;
                /* Float splitStartDist = null;
                 if (splitStartDistFieldNum >= 0) {
                    Object startDistObj = split.getFieldValue(splitStartDistFieldNum);
                    if (startDistObj instanceof Number) {
                        splitStartDist = ((Number) startDistObj).floatValue();
                    }
                } */

                Float splitTotalDist = split.getFieldFloatValue(SPL_DIST);
                /* Float splitTotalDist = null;
                Object splitTotalDistObj = split.getFieldValue(SPL_DIST);
                if (splitTotalDistObj instanceof Number) {
                    splitTotalDist = ((Number) splitTotalDistObj).floatValue();
                } */


                Short splitType = split.getFieldShortValue(SPL_TYPE);
                if (splitType != null) {
                    splitTypeOccurrence.putIfAbsent(splitType, 0);
                }

                // When split total distance is affected by the distance addition, update total distance and corresponding split summary. 
                // ------------------------------------------------------------------
                if (splitStartDist != null && splitTotalDist != null
                        && fromDist >= splitStartDist && fromDist <= (splitStartDist + splitTotalDist)) {
                    Float newSplitTotalDist = splitTotalDist + distToAdd;
                    Float splitTimer = split.getFieldFloatValue(SPL_TIMER);

                    split.setFieldValue(SPL_DIST, newSplitTotalDist);
                    split.setFieldValue(SPL_SPEED, newSplitTotalDist / splitTimer);
                    appendTempUpdateLog("Updating SPLIT w start dist " + splitStartDist + " DIST from " + splitTotalDist);
                    appendTempUpdateLogLn(" to " + newSplitTotalDist + "m, speed: " 
                        + PehoUtils.mps2minpkm(newSplitTotalDist / splitTimer) + "min/km"
                        + " type=" + (splitType != null ? SplitType.getByValue(splitType) : "unknown") + "(" + splitType + ")");

                    updatedSplitTotals++;

                    if (splitType != null) {
                        int occurrenceIx = splitTypeOccurrence.getOrDefault(splitType, 0);
                        java.util.List<Mesg> summaries = splitSummaryByType.get(splitType);
                        if (summaries != null && occurrenceIx < summaries.size()) {
                            Mesg summaryMesg = summaries.get(occurrenceIx);
                            Float summaryDist = summaryMesg.getFieldFloatValue(SPLSUM_DIST);
                            Float newSummaryDist = summaryDist + distToAdd;
                            Float summaryTimer = summaryMesg.getFieldFloatValue(SPLSUM_TIMER);

                            summaryMesg.setFieldValue(SPLSUM_DIST, newSummaryDist);
                            summaryMesg.setFieldValue(SPLSUM_SPEED, newSummaryDist / summaryTimer);
                            updatedSplitSummaries++;
                            appendTempUpdateLog("Updating SPLITSUM w type " + splitType + " DIST from " + summaryDist);
                            appendTempUpdateLogLn(" to " + newSummaryDist + "m, speed: " 
                                + PehoUtils.mps2minpkm(newSummaryDist / summaryTimer) + "min/km");
                        }
                        splitTypeOccurrence.put(splitType, occurrenceIx + 1);
                    }
                }

                // When split start distance is affected by the distance addition.
                // When split start is after fromDist, shift start distance.
                // ------------------------------------------------------------------
                if (splitStartDist != null && splitStartDist > fromDist) {
                    split.setFieldValue(SPL_SDIST, (splitStartDist + distToAdd) * 100);
                    updatedSplitStarts++;
                }
            } // end forloop split 

            appendTempUpdateLogLn("Split updates: "
                    + "totalUpdated=" + updatedSplitTotals
                    + ", summaryUpdated=" + updatedSplitSummaries
                    + ", startShifted=" + updatedSplitStarts);
        } // end if fromDist != null

        /* for (Mesg split : splitMesg) {
            Short splitType = split.getFieldShortValue(SPL_TYPE);
            System.out.println("SPLIT"
                    + " startTime=" + FitDateTime.toStringTime(split.getFieldLongValue(SPL_STIME), diffMinutesLocalUTC)
                    + " totalTime=" + FitDateTime.toTimerString(split.getFieldLongValue(SPL_TIMER))
                    + " startDist=" + PehoUtils.m2km2(split.getFieldFloatValue(SPL_SDIST)/100)
                    + " totalDist=" + PehoUtils.m2km2(split.getFieldFloatValue(SPL_DIST))
                + " type=" + splitType + "(" + (splitType != null ? SplitType.getByValue(splitType) : "unknown") + ")"
                );
        } */

        // Update session total distance and average speed
        // ------------------------------------------------------------------
        Float oldTotalDist = getTotalDistance();
        setTotalDistance(oldTotalDist + distToAdd);
        sessionMesg.get(0).setFieldValue(SES_DIST, getTotalDistance());

        Float oldAvgSpeed = getAvgSpeed();
        setAvgSpeed(getTotalDistance() / getTotalTimerTime());
        sessionMesg.get(0).setFieldValue(SES_SPEED, getAvgSpeed());
        sessionMesg.get(0).setFieldValue(SES_ESPEED, getAvgSpeed());

        appendTempUpdateLog("Increasing SESSION_DIST from " + oldTotalDist + "m");
        appendTempUpdateLogLn(" to " + getTotalDistance() + "m");

        appendTempUpdateLog("Increasing SESSION_SPEED from " + oldAvgSpeed + "m/s" 
            + " / " + PehoUtils.mps2minpkm(oldAvgSpeed) + "min/km");
        appendTempUpdateLogLn(" to " + getAvgSpeed() + "m/s" 
            + " / " + PehoUtils.mps2minpkm(getAvgSpeed()) + "min/km");
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
        if (timeValueToSearchFor == null) {
            return null;
        }
        if (recordMesg == null || recordMesg.isEmpty() || recordMesgAddOnRecords == null || recordMesgAddOnRecords.isEmpty()) {
            return null;
        }

        int ix = 0;
        // FIND IX in recordMesg list
        for (Mesg record : recordMesg) {
            Long recordTime = record.getFieldLongValue(REC_TIME);
            if (recordTime != null && recordTime >= timeValueToSearchFor) {
                break;
            }
            ix += 1;
        }

        // Clamp so timestamp after last record maps to the last known timer value.
        if (ix >= recordMesgAddOnRecords.size()) {
            ix = recordMesgAddOnRecords.size() - 1;
        }

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
    public void listStops(Long durationForStopToBeSearched, Float distanceForStopToBeSearched) {

        Long durationCounter = 0L;
        Float distanceCounter = 0F;

        for (Mesg mesg : allMesg) {
            Long stopDuration = mesg.getFieldLongValue(REC_TIME);
            Float stopDistance = mesg.getFieldFloatValue(REC_DIST);
            durationCounter += stopDuration != null ? stopDuration : 0L;
            distanceCounter += stopDistance != null ? stopDistance : 0F;

            if (stopDuration != null && stopDuration.equals(durationForStopToBeSearched) &&
                stopDistance != null && stopDistance.equals(distanceForStopToBeSearched)) {
                printStopRecord(mesg);
            }
        }
    }

    public void printStopRecord(Mesg mesg) {
        System.out.println("Stop Record:");
        System.out.println("  Duration: " + mesg.getFieldLongValue(REC_TIME));
        System.out.println("  Distance: " + mesg.getFieldFloatValue(REC_DIST));
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public Boolean checkForPausesAndGivePrintedResultBasedOnTimer(Long fromTimer, Long toTimer) {
        return (checkForPausesAndGivePrintedResultBasedOnTime(findTimeBasedOnTimer(fromTimer), findTimeBasedOnTimer(toTimer)));
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
// ...existing code...
    /**
     * Print RECORD and EVENT messages between two timer values (inclusive).
     * Columns: timer (sec/human), timestamp (local), lapNo, type, distance (m), altitude (m), pace(min/km), cadence(rpm), info
     * Requires recordMesgAddOnRecords (createTimerList must have been run).
     */
    public void printMessagesBetweenTimers(Long fromTimer, Long toTimer) {
        if (recordMesgAddOnRecords == null || recordMesgAddOnRecords.isEmpty()) {
            System.out.println("Timer list empty - run createTimerList() first.");
            return;
        }
        if (fromTimer == null || toTimer == null || fromTimer > toTimer) {
            System.out.println("Invalid timer range.");
            return;
        }

        Long timeStart = findTimeBasedOnTimer(fromTimer);
        Long timeEnd = findTimeBasedOnTimer(toTimer);

        int allStartIx = findIxInAllMesgBasedOnTime(timeStart);
        int allEndIx = findIxInAllMesgBasedOnTime(timeEnd);

        if (allStartIx < 0 || allEndIx < 0 || allStartIx >= allMesg.size()) {
            System.out.println("No messages found in the requested timer range.");
            return;
        }
        if (allEndIx >= allMesg.size()) allEndIx = allMesg.size() - 1;
        if (allStartIx > allEndIx) {
            System.out.println("No messages in range (start after end).");
            return;
        }

        // Prebuild lap start times (if any) for quick lookup
        List<Long> lapStarts = new ArrayList<>();
        if (lapMesg != null) {
            for (Mesg l : lapMesg) {
                Long s = l.getFieldLongValue(LAP_STIME);
                if (s == null) s = l.getFieldLongValue(LAP_TIME);
                if (s != null) lapStarts.add(s);
            }
        }

        // helper to find 1-based lap number for a timestamp, 0 if unknown
        java.util.function.Function<Long,Integer> lapForTime = (t) -> {
            if (t == null || lapStarts.isEmpty()) return 0;
            int last = 0;
            for (int i = 0; i < lapStarts.size(); i++) {
                Long st = lapStarts.get(i);
                if (st != null && st <= t) last = i + 1; // 1-based
            }
            return last;
        };

        // compute recordCounter at allStartIx (0-based index into recordMesg / recordMesgAddOnRecords)
        int recordCounter = 0;
        for (int i = 0; i < allStartIx && i < allMesg.size(); i++) {
            if (allMesg.get(i).getNum() == MesgNum.RECORD) recordCounter++;
        }

        System.out.println("timer   time       lap  type    dist(m)   alt(m)   pace    cad   info");
        System.out.println("-------------------------------------------------------------");

        for (int i = allStartIx; i <= allEndIx && i < allMesg.size(); i++) {
            Mesg m = allMesg.get(i);

            if (m.getNum() == MesgNum.RECORD) {
                Long timer = null;
                if (recordCounter >= 0 && recordCounter < recordMesgAddOnRecords.size()) {
                    timer = recordMesgAddOnRecords.get(recordCounter).getTimer();
                }
                Long time = m.getFieldLongValue(REC_TIME);
                Float dist = m.getFieldFloatValue(REC_DIST);
                Float alt = m.getFieldFloatValue(REC_EALT);

                // pace (min/km) — prefer enhanced speed then speed
                Float speed = m.getFieldFloatValue(REC_ESPEED);
                if (speed == null) speed = m.getFieldFloatValue(REC_SPEED);
                String paceStr = (speed != null && speed > 0f) ? PehoUtils.mps2minpkm(speed) : "-";

                // cadence
                Short cad = m.getFieldShortValue(RecordMesg.CadenceFieldNum);
                String cadStr = cad != null ? String.valueOf(cad) : "-";

                String timeStr = time != null ? FitDateTime.toStringTime(time, diffMinutesLocalUTC) : "unknown";
                String timerStr = timer != null ? PehoUtils.sec2minSecShort(timer) : "N/A";
                int lapNo = lapForTime.apply(time);
                System.out.println(String.format("%-7s %-10s %-4s %-7s %-9s %-8s %-7s %-5s",
                        timerStr,
                        timeStr,
                        (lapNo > 0 ? String.valueOf(lapNo) : "-"),
                        "RECORD",
                        dist != null ? String.format("%.1f", dist) : "-",
                        alt != null ? String.format("%.1f", alt) : "-",
                        paceStr,
                        cadStr,
                        "recIdx=" + recordCounter + " allIx=" + i));
                recordCounter++;
            } else if (m.getNum() == MesgNum.EVENT) {
                Long evTime = m.getFieldLongValue(EVE_TIME);
                Short rawEvent = m.getFieldShortValue(EVE_EVENT);
                Short rawType = m.getFieldShortValue(EVE_TYPE);
                String evName = rawEvent != null ? String.valueOf(Event.getByValue(rawEvent)) : "unknown";
                String evType = rawType != null ? String.valueOf(EventType.getByValue(rawType)) : "unknown";
                Long timerForEvent = evTime != null ? findTimerBasedOnTime(evTime) : null;
                String timerStr = timerForEvent != null ? PehoUtils.sec2minSecShort(timerForEvent) : "-";
                String timeStr = evTime != null ? FitDateTime.toStringTime(evTime, diffMinutesLocalUTC) : "unknown";
                int lapNo = lapForTime.apply(evTime);
                // Events don't have dist/alt/pace/cad
                System.out.println(String.format("%-7s %-10s %-3s %-7s %-9s %-8s %-7s %-5s %s",
                        timerStr,
                        timeStr,
                        (lapNo > 0 ? String.valueOf(lapNo) : "-"),
                        "EVENT",
                        "-",
                        "-",
                        "-",
                        "-",
                        evName + "/" + evType));
            }
        }
        System.out.println("-------------------------------------------------------------");
    }
// ...existing code...

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

// ...existing code...
    /**
     * Combine low-movement intervals, pauses and gaps into one time-sorted list and print them.
     * Output columns:
     * TYPE No   timerStart->timerEnd      timeStart -> timeEnd                 dur(s)   distStart->distStop (Δm)   altStart->altStop (Δm)   note
     *
     * low-movement detection (forward) uses a sliding window of the given size and treats
     * <code>thresholdMeters</code> as the **maximum** accumulated distance inside the window.
     *
     * When evaluating pauses, only <code>thresholdMeters</code> is used as a distance goal: the code
     * walks records backwards from each pause start until at least that many metres have been
     * covered and reports how much time before the pause the threshold was reached.
     *
     * Duration printed in seconds. Timer printed as min:sec (no "(sec)" suffix).
     */
    public void printCombinedStopsPausesLowMovement(int windowSize, double thresholdMeters) {
        if (allMesg.isEmpty() || windowSize <= 0) return;

        class CombinedEntry {
            enum Type { LOW, PAUSE, GAP }
            Type type;
            int no = -1;
            Long startTime;
            Long endTime;
            Long startTimer;
            Long endTimer;
            Float distStart;
            Float distStop;
            Float altStart;
            Float altStop;
            String note;
            int recIxStart = -1;
            int recIxEnd = -1;

            CombinedEntry(Type t) { type = t; }
        }

        List<CombinedEntry> combined = new ArrayList<>();

        // ---------- Detect low-movement intervals ----------
        double[] window = new double[windowSize];
        long[] times = new long[windowSize];
        int[] allIdxs = new int[windowSize];

        int idx = 0, count = 0;
        double sum = 0.0;

        Mesg prevRec = null;
        boolean inLow = false;

        Long lowStartTime = null;
        int lowStartRecIdx = -1;
        int recordIndex = 0; // 1-based
        int allIx = 0;

        int pauseIx = 0;
        Long lastPauseEndedTime = null;
        int lastPauseNo = -1;
        boolean pauseRestartHandled = true;
        boolean recentlyRestartedByPause = false;
        int restartPauseNo = -1;

        final long TOL = 3;

        for (Mesg m : allMesg) {
            if (m.getNum() != MesgNum.RECORD) { allIx++; continue; }

            recordIndex++;
            int thisAllIx = allIx;

            Float distF = m.getFieldFloatValue(REC_DIST);
            Long timeL = m.getFieldLongValue(REC_TIME);

            // advance pauseIx
            while (pauseIx < pauseRecords.size()
                    && pauseRecords.get(pauseIx).getTimeStop() != null
                    && timeL != null
                    && pauseRecords.get(pauseIx).getTimeStop() < timeL) {
                pauseIx++;
            }

            // skip records inside pause
            if (timeL != null && pauseIx < pauseRecords.size()) {
                PauseMesg p = pauseRecords.get(pauseIx);
                Long pStart = p.getTimeStart();
                Long pStop = p.getTimeStop();
                if (pStart != null && pStop != null && timeL >= pStart && timeL <= pStop) {
                    lastPauseEndedTime = pStop;
                    lastPauseNo = p.getNo();
                    pauseRestartHandled = false;
                    count = 0; sum = 0.0; idx = 0; inLow = false; prevRec = null;
                    recentlyRestartedByPause = false; restartPauseNo = -1;
                    allIx++; continue;
                }
            }
            if (lastPauseEndedTime != null && !pauseRestartHandled && timeL != null && timeL >= lastPauseEndedTime) {
                count = 0; sum = 0.0; idx = 0; inLow = false; prevRec = null;
                recentlyRestartedByPause = true;
                restartPauseNo = lastPauseNo;
                pauseRestartHandled = true;
            }

            if (distF == null || timeL == null) { prevRec = m; allIx++; continue; }
            double dist = distF;

            double delta = 0.0;
            if (prevRec != null) {
                Float prevDistF = prevRec.getFieldFloatValue(REC_DIST);
                if (prevDistF != null) { delta = dist - prevDistF; if (delta < 0) delta = 0; }
            }

            if (count < windowSize) {
                window[idx] = delta; times[idx] = timeL; allIdxs[idx] = thisAllIx; sum += delta; count++;
            } else {
                sum -= window[idx];
                window[idx] = delta; times[idx] = timeL; allIdxs[idx] = thisAllIx; sum += delta;
            }

            idx++; if (idx == windowSize) idx = 0;

            if (count == windowSize) {
                // thresholdMeters is the **maximum** distance for low movement
                if (sum < thresholdMeters) {
                    if (!inLow) {
                        inLow = true;
                        lowStartTime = times[idx];
                        lowStartRecIdx = recordIndex - windowSize + 1; // 1-based
                        if (recentlyRestartedByPause) {
                            restartPauseNo = restartPauseNo;
                            recentlyRestartedByPause = false;
                        } else {
                            restartPauseNo = -1;
                        }
                    }
                } else {
                    if (inLow) {
                        CombinedEntry e = new CombinedEntry(CombinedEntry.Type.LOW);
                        e.startTime = lowStartTime;
                        e.endTime = timeL;
                        e.recIxStart = lowStartRecIdx - 1;
                        e.recIxEnd = recordIndex - 1;
                        if (recordMesgAddOnRecords != null && e.recIxStart >= 0 && e.recIxEnd >= 0
                                && recordMesgAddOnRecords.size() > Math.max(e.recIxStart, e.recIxEnd)) {
                            e.startTimer = recordMesgAddOnRecords.get(e.recIxStart).getTimer();
                            e.endTimer = recordMesgAddOnRecords.get(e.recIxEnd).getTimer();
                        }
                        if (e.recIxStart >= 0 && e.recIxStart < recordMesg.size()) {
                            e.distStart = recordMesg.get(e.recIxStart).getFieldFloatValue(REC_DIST);
                            e.altStart = recordMesg.get(e.recIxStart).getFieldFloatValue(REC_EALT);
                        }
                        if (e.recIxEnd >= 0 && e.recIxEnd < recordMesg.size()) {
                            e.distStop = recordMesg.get(e.recIxEnd).getFieldFloatValue(REC_DIST);
                            e.altStop = recordMesg.get(e.recIxEnd).getFieldFloatValue(REC_EALT);
                        }
                        e.no = combined.size() + 1;
                        e.note = String.format("lowDistSum=%.2fm", sum);
                        combined.add(e);

                        inLow = false; lowStartTime = null; lowStartRecIdx = -1; restartPauseNo = -1;
                    }
                }
            }

            prevRec = m;
            allIx++;
        }

        // ongoing low at EOF
        if (inLow && lowStartTime != null) {
            CombinedEntry e = new CombinedEntry(CombinedEntry.Type.LOW);
            e.startTime = lowStartTime;
            e.endTime = null;
            e.recIxStart = lowStartRecIdx - 1;
            e.recIxEnd = recordIndex - 1;
            if (recordMesgAddOnRecords != null && e.recIxStart >= 0 && e.recIxEnd >= 0
                    && recordMesgAddOnRecords.size() > Math.max(e.recIxStart, e.recIxEnd)) {
                e.startTimer = recordMesgAddOnRecords.get(e.recIxStart).getTimer();
                e.endTimer = recordMesgAddOnRecords.get(e.recIxEnd).getTimer();
            }
            if (e.recIxStart >= 0 && e.recIxStart < recordMesg.size()) {
                e.distStart = recordMesg.get(e.recIxStart).getFieldFloatValue(REC_DIST);
                e.altStart = recordMesg.get(e.recIxStart).getFieldFloatValue(REC_EALT);
            }
            if (e.recIxEnd >= 0 && e.recIxEnd < recordMesg.size()) {
                e.distStop = recordMesg.get(e.recIxEnd).getFieldFloatValue(REC_DIST);
                e.altStop = recordMesg.get(e.recIxEnd).getFieldFloatValue(REC_EALT);
            }
            e.no = combined.size() + 1;
            e.note = "ONGOING";
            combined.add(e);
        }

        // ---------- Add pauses and compute time where distance before pause reaches threshold ----------
        for (PauseMesg p : pauseRecords) {
            Long pauseStartTime = p.getTimeStart();
            int pauseStartRecIdx = p.getIxStart();

            if (pauseStartTime != null && pauseStartRecIdx > 0) {
                double backSum = 0.0;
                Long thresholdTime = null;
                int thresholdRecIdx = -1;
                Float thresholdDist = null;

                // accumulate backward until sum >= thresholdMeters
                for (int i = pauseStartRecIdx - 1; i >= 0; i--) {
                    Mesg rec = recordMesg.get(i);
                    Float distF = rec.getFieldFloatValue(REC_DIST);
                    if (distF == null) continue;

                    double delta = 0.0;
                    if (i + 1 < recordMesg.size()) {
                        Mesg nextRec = recordMesg.get(i + 1);
                        Float nextDistF = nextRec.getFieldFloatValue(REC_DIST);
                        if (nextDistF != null) {
                            delta = nextDistF - distF;
                            if (delta < 0) delta = 0;
                        }
                    }

                    backSum += delta;
                    if (backSum >= thresholdMeters) {
                        thresholdTime = rec.getFieldLongValue(REC_TIME);
                        thresholdRecIdx = i;
                        thresholdDist = distF;
                        break;
                    }
                }

                if (thresholdTime != null) {
                    // span from the moment the threshold distance was reached up to the pause start
                    long durBefore = pauseStartTime - thresholdTime;
                    if (durBefore > windowSize) {
                        CombinedEntry e = new CombinedEntry(CombinedEntry.Type.LOW);
                        e.no = -1;
                        e.startTime = thresholdTime;
                        e.endTime = pauseStartTime;
                        e.startTimer = findTimerBasedOnTime(thresholdTime);
                        e.endTimer = findTimerBasedOnTime(pauseStartTime);
                        e.note = String.format("BEFORE PAUSE #%d: %.2fm in %ds", p.getNo(), thresholdMeters, durBefore);

                        // record distances for the entry
                        e.distStart = thresholdDist;
                        // distStop can use distance just before pause
                        Mesg stopRec = recordMesg.get(pauseStartRecIdx - 1);
                        e.distStop = stopRec != null ? stopRec.getFieldFloatValue(REC_DIST) : null;
                        combined.add(e);
                    }
                }
                // if thresholdTime was null or duration not exceeding windowSize, no low entry is printed
            }

            // Then add the pause itself
            CombinedEntry e = new CombinedEntry(CombinedEntry.Type.PAUSE);
            e.no = p.getNo();
            e.startTime = p.getTimeStart();
            e.endTime = p.getTimeStop();
            e.startTimer = e.startTime != null ? findTimerBasedOnTime(e.startTime) : null;
            e.endTimer = e.endTime != null ? findTimerBasedOnTime(e.endTime) : null;
            e.distStart = p.getDistStart();
            e.distStop = p.getDistStart() != null && p.getDistPause() != null ? p.getDistStart() + p.getDistPause() : null;
            e.altStart = p.getAltStart();
            e.altStop = p.getAltStop();
            e.note = String.format("pauseDur=%ds", (p.getTimeStop() != null && p.getTimeStart() != null) ? (int)(p.getTimeStop() - p.getTimeStart()) : 0);
            combined.add(e);
        }

        // ---------- Add gaps ----------
        for (GapMesg g : gapRecords) {
            CombinedEntry e = new CombinedEntry(CombinedEntry.Type.GAP);
            e.no = g.getNo();
            e.startTime = g.getTimeStart();
            e.endTime = g.getTimeStop();
            e.startTimer = e.startTime != null ? findTimerBasedOnTime(e.startTime) : null;
            e.endTimer = e.endTime != null ? findTimerBasedOnTime(e.endTime) : null;
            e.distStart = g.getDistStart();
            e.distStop = g.getDistStop();
            e.altStart = g.getAltStart();
            e.altStop = g.getAltStop();
            e.note = String.format("gapDur=%ds", g.getTimeGap() != null ? g.getTimeGap().intValue() : 0);
            combined.add(e);
        }

        // ---------- Sort ----------
        combined.sort((a, b) -> {
            Long at = a.startTime == null ? Long.MAX_VALUE : a.startTime;
            Long bt = b.startTime == null ? Long.MAX_VALUE : b.startTime;
            return at.compareTo(bt);
        });

        // ---------- Print ----------
        System.out.println("TYPE.No   timer(total)     timer(elapsed)     time                 dur   distance              altitude           note");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");

        int lowSeq = 0;
        for (CombinedEntry ce : combined) {
            String tStartTimer = ce.startTimer != null ? new Hmmss(ce.startTimer).get().replace("h","").replace("min","") : "N/A";
            String tEndTimer = ce.endTimer != null ? new Hmmss(ce.endTimer).get().replace("h","").replace("min","") : "N/A";
            String tStartElapsed = (ce.startTime != null && getTimeFirstRecord() != null)
                    ? new Hmmss(ce.startTime - getTimeFirstRecord()).get().replace("h", "").replace("min", "") : "N/A";
            String tEndElapsed = (ce.endTime != null && getTimeFirstRecord() != null)
                    ? new Hmmss(ce.endTime - getTimeFirstRecord()).get().replace("h", "").replace("min", "") : "N/A";
            String tStartTime = ce.startTime != null ? FitDateTime.toStringTime(ce.startTime, diffMinutesLocalUTC) : "unknown";
            String tEndTime = ce.endTime != null ? FitDateTime.toStringTime(ce.endTime, diffMinutesLocalUTC) : "unknown";

            long dur = -1;
            if (ce.startTime != null && ce.endTime != null) {
                dur = ce.endTime - ce.startTime;
            } else if (ce.startTimer != null && ce.endTimer != null) {
                dur = ce.endTimer - ce.startTimer;
            }

            String durStr = dur >= 0 ? String.valueOf(dur) + "s" : "ONGOING";

            Float ds = ce.distStart;
            Float de = ce.distStop;
            String distStr = "N/A";
            if (ds != null || de != null) {
                String s = ds != null ? String.format("%.0fm", ds) : "N/A";
                String e = de != null ? String.format("%.0fm", de) : "N/A";
                String d = (ds != null && de != null) ? String.format("%.1fm", (de - ds)) : "N/A";
                distStr = s + "->" + e + "=" + d + "";
            }

            Float as = ce.altStart;
            Float ae = ce.altStop;
            String altStr = "N/A";
            if (as != null || ae != null) {
                String s = as != null ? String.format("%.0fm", as) : "N/A";
                String e = ae != null ? String.format("%.0fm", ae) : "N/A";
                String d = (as != null && ae != null) ? String.format("%.1fm", (ae - as)) : "N/A";
                altStr = s + "->" + e + "=" + d + "";
            }

            String typeNo;
            if (ce.type == CombinedEntry.Type.LOW) {
                lowSeq++;
                typeNo = "LOW." + lowSeq;
            } else if (ce.type == CombinedEntry.Type.PAUSE) {
                typeNo = "PAUSE." + ce.no;
            } else {
                typeNo = "GAP." + ce.no;
            }

            System.out.println(String.format("%-9s %-16s %-16s %-20s %-5s %-21s %-18s %s",
                    typeNo,
                    (tStartTimer + "->" + tEndTimer),
                    (tStartElapsed + "->" + tEndElapsed),
                    (tStartTime + "->" + tEndTime),
                    durStr,
                    distStr,
                    altStr,
                    ce.note != null ? ce.note : ""));
        }

        System.out.println("-------------------------------------------------------------------------------------------------------------------------");
    }
// ...existing code...

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    
    public Boolean checkForPausesAndGivePrintedResultBasedOnTime(Long fromTime, Long toTime) {

        Boolean isPauses = false;
        CheckForPausesResult result = checkForPausesByTime(fromTime, toTime);

        if (result.hasCompletePauses() || result.hasUnmatchedStart() || result.hasUnmatchedEnd()) {
            isPauses = true;
            System.out.println();
            System.out.println("==XX> There is at least one PAUSE between start "+FitDateTime.toString(fromTime)+" and stop "+FitDateTime.toString(toTime)+".");
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
    public void updateActivityInfoWhenDeletingPauseToGap(int pauseIndex) {

        PauseMesg pause = getPauseList().get(pauseIndex);

        // Increase distance after the shortened pause, starting from 1 after pause stop
        // ------------------------------------------------------
        addDistToRecords(pause.getIxStop()+1, pause.getDistPause());

        // Updating LAP DATA
        //------------------
        Float lapTime = lapMesg.get(pause.getIxLap()).getFieldFloatValue(LAP_TIMER);
        appendTempUpdateLog("Increasing LAP_TIMER for lap " + pause.getIxLap() + " from " + lapTime);
        lapTime += pause.getTimePause();
        appendTempUpdateLogLn(" to " + lapTime + "s");
        
        lapMesg.get(pause.getIxLap()).setFieldValue(LAP_TIMER, (lapTime));
        //lapMesg.get(pause.getIxLap()).setFieldValue(LAP_ETIMER, (lapTime));

        // Updating SESSION DATA
        //----------------------
        appendTempUpdateLog("Increasing SESSION_TIMER from " + totalTimerTime);
        totalTimerTime += (float) pause.getTimePause();
        appendTempUpdateLogLn(" to " + totalTimerTime + "s");

        sessionMesg.get(0).setFieldValue(SES_TIMER, totalTimerTime);
        activityMesg.get(0).setFieldValue(ACT_TIMER, totalTimerTime);

        //elapsedTimerTime += (float) pause.getTimePause() - newPauseTime;
        //sessionMesg.get(0).setFieldValue(SES_ETIMER, elapsedTimerTime);

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void updateActivityInfoWhenDeletingGapToPause(int gapIndex) {

        GapMesg gap = getGapList().get(gapIndex);

        // Increase distance after the shortened pause, starting from 1 after pause stop
        // ------------------------------------------------------
        addDistToRecords(gap.getIxStop(), -gap.getDistGap());

        // Updating LAP DATA
        //------------------
        appendTempUpdateLog("Decreasing LAP_TIMER for lap " + gap.getIxLap() + " from " + lapMesg.get(gap.getIxLap()).getFieldFloatValue(LAP_TIMER));
        Float lapTime = lapMesg.get(gap.getIxLap()).getFieldFloatValue(LAP_TIMER) - gap.getTimeGap();
        appendTempUpdateLogLn(" to " + lapTime + "s");

        lapMesg.get(gap.getIxLap()).setFieldValue(LAP_TIMER, (lapTime));
        //lapMesg.get(gap.getIxLap()).setFieldValue(LAP_ETIMER, (lapTime));

        // Updating SESSION DATA
        //----------------------
        appendTempUpdateLog("Decreasing SESSION_TIMER from " + getTotalTimerTime());
        totalTimerTime -= (float) gap.getTimeGap();
        appendTempUpdateLogLn(" to " + getTotalTimerTime() + "s");

        sessionMesg.get(0).setFieldValue(SES_TIMER, getTotalTimerTime());
        activityMesg.get(0).setFieldValue(ACT_TIMER, getTotalTimerTime());
        //elapsedTimerTime -= (float) gap.getTimePause();
        //sessionMesg.get(0).setFieldValue(SES_ETIMER, elapsedTimerTime);

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
    public void checkAndFixNullRecordTimes() {
        clearTempUpdateLog();
        appendTempUpdateLogLn("CHECK/FIX NULL RECORD TIMES");
        appendTempUpdateLogLn("---------------------------");

        if (recordMesg == null || recordMesg.isEmpty()) {
            logTimeFix("No records found. Nothing to check.");
            appendUpdateLog(getTempUpdateLog());
            return;
        }

        Long prevNonNullTime = null;
        int fixedCount = 0;
        int duplicateCount = 0;
        int outOfOrderCount = 0;

        int i = 0;
        while (i < recordMesg.size()) {
            Long currentTime = recordMesg.get(i).getFieldLongValue(REC_TIME);

            if (currentTime != null && prevNonNullTime != null && currentTime.equals(prevNonNullTime)) {
                duplicateCount++;
                logTimeFix("WARNING: Duplicate REC_TIME found at recIx=" + i
                        + " time=" + currentTime
                        + " (" + FitDateTime.toStringTime(currentTime, diffMinutesLocalUTC) + ")"
                        + ". Keeping duplicate (no delete in startup check).");
                prevNonNullTime = currentTime;
                i++;
                continue;
            }

            if (currentTime != null && prevNonNullTime != null && currentTime < prevNonNullTime) {
                outOfOrderCount++;
                logTimeFix("WARNING: REC_TIME out of order at recIx=" + i
                        + " prev=" + prevNonNullTime
                        + " curr=" + currentTime
                        + " (" + FitDateTime.toStringTime(currentTime, diffMinutesLocalUTC) + ")"
                        + ". Keeping value unchanged.");
                prevNonNullTime = currentTime;
                i++;
                continue;
            }

            if (currentTime != null) {
                prevNonNullTime = currentTime;
                i++;
                continue;
            }

            int nullStart = i;
            while (i < recordMesg.size() && recordMesg.get(i).getFieldLongValue(REC_TIME) == null) {
                i++;
            }
            int nullEnd = i - 1;
            int nullCount = nullEnd - nullStart + 1;

            if (nullCount == 1) {
                logTimeFix("Found 1 null REC_TIME at recIx=" + nullStart);
            } else {
                logTimeFix("Found " + nullCount + " null REC_TIME in a row at recIx="
                        + nullStart + ".." + nullEnd);
            }

            int beforeIx = nullStart - 1;
            int afterIx = i;

            if (beforeIx < 0 && afterIx < recordMesg.size()) {
                Long afterTime = recordMesg.get(afterIx).getFieldLongValue(REC_TIME);
                if (afterTime == null) {
                    logTimeFix("Cannot fill leading null REC_TIME range at recIx=" + nullStart + ".." + nullEnd
                            + " (missing next non-null REC_TIME).");
                    continue;
                }

                long startCandidate = afterTime - nullCount;
                if (startCandidate < 1L) {
                    startCandidate = 1L;
                }

                logTimeFix("Filling leading null REC_TIME range recIx=" + nullStart + ".." + nullEnd
                        + " using even +1s spread before next record.");

                long lastAssigned = startCandidate - 1;
                for (int j = 0; j < nullCount; j++) {
                    int recIx = nullStart + j;
                    long candidate = startCandidate + j;
                    if (candidate <= lastAssigned) {
                        candidate = lastAssigned + 1;
                    }

                    recordMesg.get(recIx).setFieldValue(REC_TIME, candidate);
                    fixedCount++;
                    lastAssigned = candidate;

                    logTimeFix("  recIx=" + recIx + " REC_TIME set to " + candidate
                            + " (" + FitDateTime.toStringTime(candidate, diffMinutesLocalUTC) + ")");
                }

                // Keep previous time on the last value written in the null range.
                // The next loop iteration starts at afterIx, so using afterTime here can self-match and create false duplicate warnings.
                prevNonNullTime = lastAssigned;
                continue;
            }

            if (afterIx >= recordMesg.size()) {
                Long beforeTime = beforeIx >= 0 ? recordMesg.get(beforeIx).getFieldLongValue(REC_TIME) : null;
                Mesg lastStopEventMesg = findLastTimerStopEventMesg();
                Long stopEventTime = lastStopEventMesg != null ? lastStopEventMesg.getFieldLongValue(EVE_TIME) : null;

                if (beforeTime == null) {
                    logTimeFix("Trailing null REC_TIME range without previous boundary at recIx="
                            + nullStart + ".." + nullEnd + ". Filling from +1s.");
                    for (int j = 1; j <= nullCount; j++) {
                        int recIx = nullStart + (j - 1);
                        long candidate = j;
                        recordMesg.get(recIx).setFieldValue(REC_TIME, candidate);
                        fixedCount++;
                        logTimeFix("  recIx=" + recIx + " REC_TIME set to " + candidate
                                + " (" + FitDateTime.toStringTime(candidate, diffMinutesLocalUTC) + ")");
                    }
                    prevNonNullTime = (long) nullCount;
                    break;
                }

                long trailingLastTime;
                boolean useStopEventAsLastRecord = (stopEventTime != null && stopEventTime > beforeTime);
                if (useStopEventAsLastRecord) {
                    trailingLastTime = stopEventTime;
                    logTimeFix("Trailing null REC_TIME range uses last STOP event time=" + stopEventTime
                            + " (" + FitDateTime.toStringTime(stopEventTime, diffMinutesLocalUTC) + ")");
                } else {
                    if (stopEventTime == null) {
                        logTimeFix("Last STOP event has empty timestamp. Filling trailing null REC_TIME using +1s steps.");
                    } else {
                        logTimeFix("Last STOP event time is not after last valid REC_TIME (stop=" + stopEventTime
                                + ", prev=" + beforeTime + "). Filling trailing null REC_TIME using +1s steps.");
                    }

                    trailingLastTime = beforeTime + nullCount;
                    if (lastStopEventMesg != null) {
                        lastStopEventMesg.setFieldValue(EVE_TIME, trailingLastTime);
                        logTimeFix("Updated last STOP event REC_TIME to " + trailingLastTime
                                + " (" + FitDateTime.toStringTime(trailingLastTime, diffMinutesLocalUTC) + ")");
                    } else {
                        logTimeFix("No STOP event found to update with trailing REC_TIME.");
                    }
                }

                boolean canUseDistTrailing = false;
                if (useStopEventAsLastRecord && nullCount > 1) {
                    Float beforeDist = recordMesg.get(beforeIx).getFieldFloatValue(REC_DIST);
                    Float lastDist = recordMesg.get(nullEnd).getFieldFloatValue(REC_DIST);
                    canUseDistTrailing = beforeDist != null && lastDist != null && lastDist > beforeDist;
                    if (canUseDistTrailing) {
                        for (int j = nullStart; j <= nullEnd; j++) {
                            if (recordMesg.get(j).getFieldFloatValue(REC_DIST) == null) {
                                canUseDistTrailing = false;
                                break;
                            }
                        }
                    }
                }

                long span = trailingLastTime - beforeTime;
                long lastAssigned = beforeTime;
                for (int j = 1; j <= nullCount; j++) {
                    int recIx = nullStart + (j - 1);
                    long candidate;

                    if (canUseDistTrailing) {
                        float beforeDistVal = recordMesg.get(beforeIx).getFieldFloatValue(REC_DIST);
                        float lastDistVal = recordMesg.get(nullEnd).getFieldFloatValue(REC_DIST);
                        float recDistVal = recordMesg.get(recIx).getFieldFloatValue(REC_DIST);
                        double ratio = (recDistVal - beforeDistVal) / (lastDistVal - beforeDistVal);
                        if (ratio < 0d) ratio = 0d;
                        if (ratio > 1d) ratio = 1d;
                        candidate = beforeTime + Math.round(span * ratio);
                    } else {
                        candidate = beforeTime + Math.round((double) span * j / nullCount);
                    }

                    long minAllowed = lastAssigned + 1;
                    long maxAllowed = trailingLastTime - (nullCount - j);
                    if (candidate < minAllowed) candidate = minAllowed;
                    if (candidate > maxAllowed) candidate = maxAllowed;

                    recordMesg.get(recIx).setFieldValue(REC_TIME, candidate);
                    fixedCount++;
                    lastAssigned = candidate;

                    logTimeFix("  recIx=" + recIx + " REC_TIME set to " + candidate
                            + " (" + FitDateTime.toStringTime(candidate, diffMinutesLocalUTC) + ")");
                }

                prevNonNullTime = trailingLastTime;
                break;
            }

            if (beforeIx < 0 || afterIx >= recordMesg.size()) {
                logTimeFix("Cannot interpolate null REC_TIME range at recIx=" + nullStart + ".." + nullEnd
                        + " is missing boundary record before/after.");
                continue;
            }

            Long beforeTime = recordMesg.get(beforeIx).getFieldLongValue(REC_TIME);
            Long afterTime = recordMesg.get(afterIx).getFieldLongValue(REC_TIME);
            if (beforeTime == null || afterTime == null) {
                logTimeFix("Cannot interpolate null REC_TIME range at recIx=" + nullStart + ".." + nullEnd
                        + " (boundary REC_TIME missing).");
                continue;
            }

            if (afterTime <= beforeTime) {
                logTimeFix("WARNING: Invalid boundary order for null range recIx=" + nullStart + ".." + nullEnd
                        + " (before=" + beforeTime + ", after=" + afterTime + ")."
                        + " Filling with +1s from before boundary.");

                long lastAssigned = beforeTime;
                for (int j = 1; j <= nullCount; j++) {
                    int recIx = nullStart + (j - 1);
                    long candidate = lastAssigned + 1;
                    recordMesg.get(recIx).setFieldValue(REC_TIME, candidate);
                    fixedCount++;
                    lastAssigned = candidate;
                    logTimeFix("  recIx=" + recIx + " REC_TIME set to " + candidate
                            + " (" + FitDateTime.toStringTime(candidate, diffMinutesLocalUTC) + ")");
                }

                prevNonNullTime = lastAssigned;
                continue;
            }

            boolean canUseDist = true;
            Float beforeDist = recordMesg.get(beforeIx).getFieldFloatValue(REC_DIST);
            Float afterDist = recordMesg.get(afterIx).getFieldFloatValue(REC_DIST);
            if (beforeDist == null || afterDist == null) {
                canUseDist = false;
            }
            if (canUseDist) {
                for (int j = nullStart; j <= nullEnd; j++) {
                    if (recordMesg.get(j).getFieldFloatValue(REC_DIST) == null) {
                        canUseDist = false;
                        break;
                    }
                }
            }

            long totalTimeSpan = afterTime - beforeTime;
            int pointsToFill = nullCount;

            if (canUseDist) {
                float totalDistSpan = afterDist - beforeDist;
                if (totalDistSpan <= 0f) {
                    canUseDist = false;
                }
            }

            String method = canUseDist ? "distance-proportional" : "even";
            logTimeFix("Interpolating null REC_TIME range recIx=" + nullStart + ".." + nullEnd
                    + " using " + method + " spread.");

            long lastAssigned = beforeTime;
            for (int j = 1; j <= pointsToFill; j++) {
                int recIx = nullStart + (j - 1);
                long candidate;

                if (canUseDist) {
                    float beforeDistVal = recordMesg.get(beforeIx).getFieldFloatValue(REC_DIST);
                    float afterDistVal = recordMesg.get(afterIx).getFieldFloatValue(REC_DIST);
                    float recDistVal = recordMesg.get(recIx).getFieldFloatValue(REC_DIST);

                    double ratio = (recDistVal - beforeDistVal) / (afterDistVal - beforeDistVal);
                    if (ratio < 0d) ratio = 0d;
                    if (ratio > 1d) ratio = 1d;
                    candidate = beforeTime + Math.round(totalTimeSpan * ratio);
                } else {
                    candidate = beforeTime + Math.round((double) totalTimeSpan * j / (pointsToFill + 1));
                }

                long minAllowed = lastAssigned + 1;
                long maxAllowed = afterTime - (pointsToFill - j + 1);
                if (candidate < minAllowed) candidate = minAllowed;
                if (candidate > maxAllowed) candidate = maxAllowed;

                recordMesg.get(recIx).setFieldValue(REC_TIME, candidate);
                fixedCount++;
                lastAssigned = candidate;

                logTimeFix("  recIx=" + recIx + " REC_TIME set to " + candidate
                        + " (" + FitDateTime.toStringTime(candidate, diffMinutesLocalUTC) + ")");
            }

            // The next iteration starts at afterIx, so keep the previous timestamp at last interpolated point.
            prevNonNullTime = lastAssigned;
        }

        logTimeFix("Completed REC_TIME startup check/fix. Filled " + fixedCount + " null REC_TIME value(s)."
                + " Duplicates kept=" + duplicateCount
                + ", out-of-order points detected=" + outOfOrderCount + ".");

        appendUpdateLog(getTempUpdateLog());
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void fixNullRecordTimes() {
        clearTempUpdateLog();
        appendTempUpdateLogLn("CHECK/FIX RECORD TIMES");
        appendTempUpdateLogLn("----------------------");

        if (recordMesg == null || recordMesg.isEmpty()) {
            logTimeFix("No records found. Nothing to check.");
            appendUpdateLog(getTempUpdateLog());
            return;
        }

        Long prevNonNullTime = null;
        int fixedCount = 0;
        int duplicateRemovedCount = 0;
        int outOfOrderCount = 0;

        int i = 0;
        while (i < recordMesg.size()) {
            Long currentTime = recordMesg.get(i).getFieldLongValue(REC_TIME);

            // Duplicate timestamps are resolved by deleting the second record.
            if (currentTime != null && prevNonNullTime != null && currentTime.equals(prevNonNullTime)) {
                logTimeFix("Duplicate REC_TIME found at recIx=" + i
                        + " time=" + currentTime
                        + " (" + FitDateTime.toStringTime(currentTime, diffMinutesLocalUTC) + ")"
                        + ". Deleting second record.");
                removeRecordMesgAtIndex(i);
                duplicateRemovedCount++;
                continue;
            }

            // Out-of-order timestamps are reported and processing stops.
            if (currentTime != null && prevNonNullTime != null && currentTime < prevNonNullTime) {
                outOfOrderCount++;
                logTimeFix("ERROR: REC_TIME out of order at recIx=" + i
                        + " prev=" + prevNonNullTime
                        + " curr=" + currentTime
                        + " (" + FitDateTime.toStringTime(currentTime, diffMinutesLocalUTC) + ")");
                logTimeFix("Stopping without changing more records.");
                appendUpdateLog(getTempUpdateLog());
                return;
            }

            if (currentTime != null) {
                prevNonNullTime = currentTime;
                i++;
                continue;
            }

            int nullStart = i;
            while (i < recordMesg.size() && recordMesg.get(i).getFieldLongValue(REC_TIME) == null) {
                i++;
            }
            int nullEnd = i - 1;
            int nullCount = nullEnd - nullStart + 1;

            if (nullCount == 1) {
                logTimeFix("Found 1 null REC_TIME at recIx=" + nullStart);
            } else {
                logTimeFix("Found " + nullCount + " null REC_TIME in a row at recIx=" + nullStart + ".." + nullEnd);
            }

            int beforeIx = nullStart - 1;
            int afterIx = i;

            // Trailing null timestamps at end-of-file are handled against the last STOP event.
            if (afterIx >= recordMesg.size()) {
                Long beforeTime = beforeIx >= 0 ? recordMesg.get(beforeIx).getFieldLongValue(REC_TIME) : null;
                if (beforeTime == null) {
                    logTimeFix("Cannot fill trailing null REC_TIME range at recIx=" + nullStart + ".." + nullEnd
                            + " (missing previous non-null REC_TIME).");
                    break;
                }

                Mesg lastStopEventMesg = findLastTimerStopEventMesg();
                Long stopEventTime = lastStopEventMesg != null ? lastStopEventMesg.getFieldLongValue(EVE_TIME) : null;

                long trailingLastTime;
                boolean useStopEventAsLastRecord = (stopEventTime != null && stopEventTime > beforeTime);
                if (useStopEventAsLastRecord) {
                    trailingLastTime = stopEventTime;
                    logTimeFix("Trailing null REC_TIME range uses last STOP event time=" + stopEventTime
                            + " (" + FitDateTime.toStringTime(stopEventTime, diffMinutesLocalUTC) + ")");
                } else {
                    if (stopEventTime == null) {
                        logTimeFix("Last STOP event has empty timestamp. Filling trailing null REC_TIME using +1s steps.");
                    } else {
                        logTimeFix("Last STOP event time is not after last valid REC_TIME (stop=" + stopEventTime
                                + ", prev=" + beforeTime + "). Filling trailing null REC_TIME using +1s steps.");
                    }

                    trailingLastTime = beforeTime + nullCount;
                    if (lastStopEventMesg != null) {
                        lastStopEventMesg.setFieldValue(EVE_TIME, trailingLastTime);
                        logTimeFix("Updated last STOP event REC_TIME to " + trailingLastTime
                                + " (" + FitDateTime.toStringTime(trailingLastTime, diffMinutesLocalUTC) + ")");
                    } else {
                        logTimeFix("No STOP event found to update with trailing REC_TIME.");
                    }
                }

                boolean canUseDistTrailing = false;
                if (useStopEventAsLastRecord && nullCount > 1) {
                    Float beforeDist = recordMesg.get(beforeIx).getFieldFloatValue(REC_DIST);
                    Float lastDist = recordMesg.get(nullEnd).getFieldFloatValue(REC_DIST);
                    canUseDistTrailing = beforeDist != null && lastDist != null && lastDist > beforeDist;
                    if (canUseDistTrailing) {
                        for (int j = nullStart; j <= nullEnd; j++) {
                            if (recordMesg.get(j).getFieldFloatValue(REC_DIST) == null) {
                                canUseDistTrailing = false;
                                break;
                            }
                        }
                    }
                }

                long span = trailingLastTime - beforeTime;
                long lastAssigned = beforeTime;
                for (int j = 1; j <= nullCount; j++) {
                    int recIx = nullStart + (j - 1);
                    long candidate;

                    if (canUseDistTrailing) {
                        float beforeDistVal = recordMesg.get(beforeIx).getFieldFloatValue(REC_DIST);
                        float lastDistVal = recordMesg.get(nullEnd).getFieldFloatValue(REC_DIST);
                        float recDistVal = recordMesg.get(recIx).getFieldFloatValue(REC_DIST);
                        double ratio = (recDistVal - beforeDistVal) / (lastDistVal - beforeDistVal);
                        if (ratio < 0d) ratio = 0d;
                        if (ratio > 1d) ratio = 1d;
                        candidate = beforeTime + Math.round(span * ratio);
                    } else {
                        candidate = beforeTime + Math.round((double) span * j / nullCount);
                    }

                    long minAllowed = lastAssigned + 1;
                    long maxAllowed = trailingLastTime - (nullCount - j);
                    if (candidate < minAllowed) candidate = minAllowed;
                    if (candidate > maxAllowed) candidate = maxAllowed;

                    recordMesg.get(recIx).setFieldValue(REC_TIME, candidate);
                    fixedCount++;
                    lastAssigned = candidate;

                    logTimeFix("  recIx=" + recIx + " REC_TIME set to " + candidate
                            + " (" + FitDateTime.toStringTime(candidate, diffMinutesLocalUTC) + ")");
                }

                prevNonNullTime = trailingLastTime;
                break;
            }

            if (beforeIx < 0 || afterIx >= recordMesg.size()) {
                logTimeFix("Cannot interpolate null REC_TIME range at recIx=" + nullStart + ".." + nullEnd
                        + " (missing boundary record before/after).");
                continue;
            }

            Long beforeTime = recordMesg.get(beforeIx).getFieldLongValue(REC_TIME);
            Long afterTime = recordMesg.get(afterIx).getFieldLongValue(REC_TIME);
            if (beforeTime == null || afterTime == null) {
                logTimeFix("Cannot interpolate null REC_TIME range at recIx=" + nullStart + ".." + nullEnd
                        + " (boundary REC_TIME missing).");
                continue;
            }
            if (afterTime <= beforeTime) {
                logTimeFix("ERROR: Boundary REC_TIME invalid for null range recIx=" + nullStart + ".." + nullEnd
                        + " before=" + beforeTime + " after=" + afterTime);
                logTimeFix("Stopping without changing more records.");
                appendUpdateLog(getTempUpdateLog());
                return;
            }

            boolean canUseDist = true;
            Float beforeDist = recordMesg.get(beforeIx).getFieldFloatValue(REC_DIST);
            Float afterDist = recordMesg.get(afterIx).getFieldFloatValue(REC_DIST);
            if (beforeDist == null || afterDist == null) {
                canUseDist = false;
            }
            if (canUseDist) {
                for (int j = nullStart; j <= nullEnd; j++) {
                    if (recordMesg.get(j).getFieldFloatValue(REC_DIST) == null) {
                        canUseDist = false;
                        break;
                    }
                }
            }

            long totalTimeSpan = afterTime - beforeTime;
            int pointsToFill = nullCount;

            if (canUseDist) {
                float totalDistSpan = afterDist - beforeDist;
                if (totalDistSpan <= 0f) {
                    canUseDist = false;
                }
            }

            String method = canUseDist ? "distance-proportional" : "even";
            logTimeFix("Interpolating null REC_TIME range recIx=" + nullStart + ".." + nullEnd
                    + " using " + method + " spread.");

            long lastAssigned = beforeTime;
            for (int j = 1; j <= pointsToFill; j++) {
                int recIx = nullStart + (j - 1);
                long candidate;

                if (canUseDist) {
                    float beforeDistVal = recordMesg.get(beforeIx).getFieldFloatValue(REC_DIST);
                    float afterDistVal = recordMesg.get(afterIx).getFieldFloatValue(REC_DIST);
                    float recDistVal = recordMesg.get(recIx).getFieldFloatValue(REC_DIST);

                    double ratio = (recDistVal - beforeDistVal) / (afterDistVal - beforeDistVal);
                    if (ratio < 0d) ratio = 0d;
                    if (ratio > 1d) ratio = 1d;
                    candidate = beforeTime + Math.round(totalTimeSpan * ratio);
                } else {
                    candidate = beforeTime + Math.round((double) totalTimeSpan * j / (pointsToFill + 1));
                }

                // Keep strict order after rounding so REC_TIME never regresses or duplicates in this segment.
                long minAllowed = lastAssigned + 1;
                long maxAllowed = afterTime - (pointsToFill - j + 1);
                if (candidate < minAllowed) candidate = minAllowed;
                if (candidate > maxAllowed) candidate = maxAllowed;

                recordMesg.get(recIx).setFieldValue(REC_TIME, candidate);
                fixedCount++;
                lastAssigned = candidate;

                logTimeFix("  recIx=" + recIx + " REC_TIME set to " + candidate
                        + " (" + FitDateTime.toStringTime(candidate, diffMinutesLocalUTC) + ")");
            }

            // The next iteration starts at afterIx, so keep the previous timestamp at last interpolated point.
            prevNonNullTime = lastAssigned;
        }

        logTimeFix("Completed REC_TIME check/fix. Updated " + fixedCount + " null value(s)."
            + " Duplicates removed=" + duplicateRemovedCount
            + ", out-of-order points detected=" + outOfOrderCount + ".");
        appendUpdateLog(getTempUpdateLog());
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void applyConstantPaceToRecordTimesFromGps(String paceMinPerKm) {
        clearTempUpdateLog();
        appendTempUpdateLogLn("RECALCULATE RECORD TIMES FROM GPS PACE");
        appendTempUpdateLogLn("-------------------------------------");

        if (recordMesg == null || recordMesg.size() < 2) {
            appendTempUpdateLogLn("Not enough records found. Need at least 2 records.");
            System.out.println(getTempUpdateLog());
            appendUpdateLog(getTempUpdateLog());
            return;
        }

        String paceInput = paceMinPerKm == null ? "" : paceMinPerKm.trim();
        if (!paceInput.matches("\\d{1,3}:[0-5]\\d")) {
            appendTempUpdateLogLn("Invalid pace format '" + paceMinPerKm + "'. Expected mm:ss.");
            System.out.println(getTempUpdateLog());
            appendUpdateLog(getTempUpdateLog());
            return;
        }

        String[] paceParts = paceInput.split(":");
        int paceMinutes = Integer.parseInt(paceParts[0]);
        int paceSecondsPart = Integer.parseInt(paceParts[1]);
        int paceSecondsPerKm = paceMinutes * 60 + paceSecondsPart;

        if (paceSecondsPerKm <= 0) {
            appendTempUpdateLogLn("Pace must be greater than 00:00 min/km.");
            System.out.println(getTempUpdateLog());
            appendUpdateLog(getTempUpdateLog());
            return;
        }

        Long startTime = recordMesg.get(0).getFieldLongValue(REC_TIME);
        if (startTime == null) {
            appendTempUpdateLogLn("First record has null REC_TIME. Run recfix first.");
            System.out.println(getTempUpdateLog());
            appendUpdateLog(getTempUpdateLog());
            return;
        }

        double secondsPerMeter = paceSecondsPerKm / 1000.0;
        double cumulativeSeconds = 0.0;
        double cumulativeGpsMeters = 0.0;
        Long lastAssignedTime = startTime;

        int updatedRecords = 0;
        int gpsSegmentCount = 0;
        int fallbackDistSegmentCount = 0;
        int missingDistanceSegmentCount = 0;
        int checkedDistanceSegments = 0;
        int distanceMismatchCount = 0;
        double maxDistanceDiffMeters = 0.0;

        appendTempUpdateLogLn("Target pace: " + paceInput + " min/km (" + paceSecondsPerKm + " s/km)");
        appendTempUpdateLogLn("Keeping first REC_TIME unchanged at " + startTime
                + " (" + FitDateTime.toStringTime(startTime, diffMinutesLocalUTC) + ")");

        for (int i = 1; i < recordMesg.size(); i++) {
            Mesg prev = recordMesg.get(i - 1);
            Mesg curr = recordMesg.get(i);

            Integer prevLat = prev.getFieldIntegerValue(REC_LAT);
            Integer prevLon = prev.getFieldIntegerValue(REC_LON);
            Integer currLat = curr.getFieldIntegerValue(REC_LAT);
            Integer currLon = curr.getFieldIntegerValue(REC_LON);

            Double segmentMeters = null;
            if (prevLat != null && prevLon != null && currLat != null && currLon != null) {
                double gpsMeters = GeoUtils.distCalc(prevLat, prevLon, currLat, currLon);
                if (!Double.isNaN(gpsMeters) && !Double.isInfinite(gpsMeters) && gpsMeters >= 0d) {
                    segmentMeters = gpsMeters;
                    gpsSegmentCount++;
                }
            }

            if (segmentMeters == null) {
                Float prevDist = prev.getFieldFloatValue(REC_DIST);
                Float currDist = curr.getFieldFloatValue(REC_DIST);
                if (prevDist != null && currDist != null) {
                    double distDelta = currDist - prevDist;
                    if (distDelta < 0d) {
                        distDelta = 0d;
                    }
                    segmentMeters = distDelta;
                    fallbackDistSegmentCount++;
                } else {
                    segmentMeters = 0d;
                    missingDistanceSegmentCount++;
                }
            }

            cumulativeGpsMeters += segmentMeters;
            cumulativeSeconds += segmentMeters * secondsPerMeter;

            Long oldTime = curr.getFieldLongValue(REC_TIME);
            long candidateTime = startTime + Math.round(cumulativeSeconds);

            if (candidateTime <= lastAssignedTime) {
                candidateTime = lastAssignedTime + 1;
            }

            if (oldTime == null || oldTime.longValue() != candidateTime) {
                curr.setFieldValue(REC_TIME, candidateTime);
                updatedRecords++;
            }
            lastAssignedTime = candidateTime;

            Float prevDist = prev.getFieldFloatValue(REC_DIST);
            Float currDist = curr.getFieldFloatValue(REC_DIST);
            if (prevDist != null && currDist != null) {
                checkedDistanceSegments++;
                double distDelta = currDist - prevDist;
                double diff = Math.abs(distDelta - segmentMeters);
                if (diff > maxDistanceDiffMeters) {
                    maxDistanceDiffMeters = diff;
                }
                if (diff > 5.0d) {
                    distanceMismatchCount++;
                }
            }
        }

        appendTempUpdateLogLn("Updated REC_TIME on " + updatedRecords + " record(s). Total records: " + recordMesg.size());
        appendTempUpdateLogLn("Distance source used: gpsSegments=" + gpsSegmentCount
                + ", fallbackRecDistSegments=" + fallbackDistSegmentCount
                + ", missingDistanceSegments=" + missingDistanceSegmentCount);
        appendTempUpdateLogLn("Distance check: checkedSegments=" + checkedDistanceSegments
                + ", mismatches(>5m)=" + distanceMismatchCount
                + ", maxDiff=" + String.format("%.2f", maxDistanceDiffMeters) + "m");
        appendTempUpdateLogLn("Computed path distance=" + String.format("%.1f", cumulativeGpsMeters) + "m, total time="
                + PehoUtils.sec2minSecLong(Math.round(cumulativeSeconds)));

        // Update start event time
        Mesg firstStartEventMesg = findFirstTimerStartEventMesg();
        if (firstStartEventMesg != null) {
            Long startEventTime = firstStartEventMesg.getFieldLongValue(EVE_TIME);
            if (startEventTime == null || !startEventTime.equals(startTime)) {
                firstStartEventMesg.setFieldValue(EVE_TIME, startTime);
                appendTempUpdateLogLn("Updated START event time from "
                        + (startEventTime != null ? FitDateTime.toStringTime(startEventTime, diffMinutesLocalUTC) : "null")
                        + " to " + FitDateTime.toStringTime(startTime, diffMinutesLocalUTC));
            }
        } else {
            appendTempUpdateLogLn("No START event found to update.");
        }

        // Update stop event time
        Long expectedStopTime = lastAssignedTime;
        Mesg lastStopEventMesg = findLastTimerStopEventMesg();
        if (lastStopEventMesg != null) {
            Long stopEventTime = lastStopEventMesg.getFieldLongValue(EVE_TIME);
            if (stopEventTime == null || !stopEventTime.equals(expectedStopTime)) {
                lastStopEventMesg.setFieldValue(EVE_TIME, expectedStopTime);
                appendTempUpdateLogLn("Updated STOP event time from "
                        + (stopEventTime != null ? FitDateTime.toStringTime(stopEventTime, diffMinutesLocalUTC) : "null")
                        + " to " + FitDateTime.toStringTime(expectedStopTime, diffMinutesLocalUTC));
            }
        } else {
            appendTempUpdateLogLn("No STOP event found to update.");
        }

        System.out.println(getTempUpdateLog());
        appendUpdateLog(getTempUpdateLog());
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void logTimeFix(String text) {
        appendTempUpdateLogLn(text);
        System.out.println(text);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private Mesg findFirstTimerStartEventMesg() {
        for (int i = 0; i < eventTimerMesg.size(); i++) {
            Mesg eventMesg = eventTimerMesg.get(i);
            if (eventMesg.getFieldValue(EVE_TYPE) != null
                    && eventMesg.getFieldValue(EVE_TYPE).equals(EventType.START.getValue())) {
                return eventMesg;
            }
        }
        return null;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    int findLastTimerStopEventIndex(List<Mesg> mesgs) {
        for (int i = mesgs.size() - 1; i >= 0; i--) {
            Mesg mesg = mesgs.get(i);
            if (mesg.getNum() != MesgNum.EVENT) {
                continue;
            }
            Short eventValue = mesg.getFieldShortValue(EventMesg.EventFieldNum);
            Short eventTypeValue = mesg.getFieldShortValue(EventMesg.EventTypeFieldNum);
            if (eventValue == null || eventTypeValue == null) {
                continue;
            }
            if (!eventValue.equals(Event.TIMER.getValue())) {
                continue;
            }
            EventType eventType = EventType.getByValue(eventTypeValue);
            String eventTypeName = eventType != null ? String.valueOf(eventType) : "";
            if ("STOP_ALL".equals(eventTypeName) || "STOP_DISABLE_ALL".equals(eventTypeName)) {
                return i;
            }
        }
        return -1;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private Mesg findLastTimerStopEventMesg() {
        int ix = findLastTimerStopEventIndex(eventTimerMesg);
        return ix >= 0 ? eventTimerMesg.get(ix) : null;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void removeRecordMesgAtIndex(int recordIx) {
        if (recordIx < 0 || recordIx >= recordMesg.size()) {
            return;
        }

        Mesg mesgToRemove = recordMesg.get(recordIx);
        recordMesg.remove(recordIx);

        boolean removedFromAllMesg = false;
        for (int i = 0; i < allMesg.size(); i++) {
            if (allMesg.get(i) == mesgToRemove) {
                allMesg.remove(i);
                removedFromAllMesg = true;
                break;
            }
        }

        if (!removedFromAllMesg) {
            logTimeFix("WARNING: Deleted duplicate record from recordMesg, but matching Mesg instance was not found in allMesg.");
        }

        if (numberOfRecords > 0) {
            numberOfRecords--;
        }
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

            if (eventTimerMesg.size() > 0 ) { // Not empty eventTimerMesg list

                if (eventTimerMesg.size() > 2 ) { // More than First START and last STOP
                        
                    // If record time is the same or more than NEXT eventTimer mesg
                    // ------------------------------------------------------------
                    if (eventTimerIx < eventTimerMesg.size()
                        && record.getFieldLongValue(REC_TIME)
                        >= eventTimerMesg.get(eventTimerIx).getFieldLongValue(EVE_TIME)) {
                        isEventTImerTime = true;
                                /* System.out.println();
                                System.out.println("==> EVENT TIMER MESG Ix:" + eventTimerIx + " @"
                                    + EventType.getByValue(eventTimerMesg.get(eventTimerIx).getFieldShortValue(EVE_TYPE)) + " @time: "
                                    + FitDateTime.toString(record.getFieldLongValue(EVE_TIME),diffMinutesLocalUTC)); */

                        if (eventTimerMesg.get(eventTimerIx).getFieldValue(EVE_TYPE).equals(EventType.STOP_ALL.getValue())) {
                            // If inPause - warning
                            if (inPause) {
                                /* System.out.println("==> WARNING - STOP AGAIN when already in pause, STOP event w/o Starting first @"
                                    + eventTimerIx + " @time: "
                                    + FitDateTime.toString(record.getFieldLongValue(EVE_TIME),diffMinutesLocalUTC)); */
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
        System.out.println("======== TotalTimerTime: " + PehoUtils.sec2minSecLong(totalTimerTime)
             + " last timer value: "
             + PehoUtils.sec2minSecLong(recordMesgAddOnRecords.get(recordMesgAddOnRecords.size()-1).getTimer()));
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
        System.out.println("================================================");
        System.out.println("====GAPS IN FILE");
        System.out.println(" File  between " + FitDateTime.toString(timeFirstRecord,diffMinutesLocalUTC) + " >>>> " + FitDateTime.toString(timeLastRecord,diffMinutesLocalUTC));
        System.out.println(String.format(" TotalTime:%1$.0fsec Dist:%2$.0fm", totalTimerTime, totalDistance));
        System.out.println("------------------------------------------------");
        //System.out.print(" Event:" + record.getEvent());
        //System.out.print(" No:" + record.getEvent().getValue());

        for (GapMesg record : gapRecords) {
            if (record.distGap >= minDistToShow) {
                System.out.print("   Gap (" + record.no + ")");
                System.out.print(String.format(" %1$dsec %2$.0fm ele%3$.1fm", record.timeGap, record.distGap, record.altGap));
                Integer startHr = recordMesg.get(record.ixStart).getFieldIntegerValue(REC_HR);
                Integer stopHr = recordMesg.get(record.ixStop).getFieldIntegerValue(REC_HR);
                if (startHr != null && stopHr != null) {
                    hrDiff = stopHr - startHr;
                    hrSign = hrDiff >= 0 ? "+" : "";
                    System.out.print(String.format(" HR:%1$d%2$s%3$d", startHr, hrSign, hrDiff));
                } else {
                    System.out.print(" HR:-");
                }

                Long timerValue = null;
                if (record.getIxStart() >= 0 && record.getIxStart() < recordMesgAddOnRecords.size()) {
                    timerValue = recordMesgAddOnRecords.get(record.getIxStart()).getTimer();
                }
                System.out.print(" @time:" + (timerValue == null ? "-" : PehoUtils.sec2minSecShort(timerValue)));
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
        System.out.println("------------------------------------------------");
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
                System.out.println("==> WARNING - START Event w/o Stopping first (inCreatePause) @"
                     + FitDateTime.toString(record.getFieldLongValue(EVE_TIME),diffMinutesLocalUTC));

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
                int ixGpsStart = findClosestRecordWithGps(ixRecordStart);
                int ixGpsStop = findClosestRecordWithGps(ixRecordStop);
                boolean hasGpsStart = ixGpsStart >= 0;
                boolean hasGpsStop = ixGpsStop >= 0;

                if (hasGpsStart) {
                    latStart = recordMesg.get(ixGpsStart).getFieldIntegerValue(REC_LAT);
                    lonStart = recordMesg.get(ixGpsStart).getFieldIntegerValue(REC_LON);
                } else {
                    latStart = 0;
                    lonStart = 0;
                }

                if (hasGpsStop) {
                    latStop = recordMesg.get(ixGpsStop).getFieldIntegerValue(REC_LAT);
                    lonStop = recordMesg.get(ixGpsStop).getFieldIntegerValue(REC_LON);
                } else {
                    latStop = 0;
                    lonStop = 0;
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
                if (hasGpsStart && hasGpsStop) {
                    newPause.calcDistPause();
                } else {
                    // Indoor files may not carry GPS coordinates for pause points.
                    newPause.setDistPause(0f);
                }

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

    private int findClosestRecordWithGps(int preferredIndex) {
        if (recordMesg.isEmpty()) {
            return -1;
        }

        int boundedIndex = Math.max(0, Math.min(preferredIndex, recordMesg.size() - 1));

        int backwardIndex = findRecordWithGpsBackward(boundedIndex);
        if (backwardIndex >= 0) {
            return backwardIndex;
        }

        return findRecordWithGpsForward(boundedIndex + 1);
    }

    private int findRecordWithGpsBackward(int startIndex) {
        for (int i = startIndex; i >= 0; i--) {
            if (recordMesg.get(i).getFieldIntegerValue(REC_LAT) != null
                    && recordMesg.get(i).getFieldIntegerValue(REC_LON) != null) {
                return i;
            }
        }
        return -1;
    }

    private int findRecordWithGpsForward(int startIndex) {
        for (int i = startIndex; i < recordMesg.size(); i++) {
            if (recordMesg.get(i).getFieldIntegerValue(REC_LAT) != null
                    && recordMesg.get(i).getFieldIntegerValue(REC_LON) != null) {
                return i;
            }
        }
        return -1;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printPause(int ix) {
        getPauseReportGenerator().printPause(ix);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printPauseList(String pauseCommandInput, Integer minDistToShow) {
        getPauseReportGenerator().printPauseList(pauseCommandInput, minDistToShow);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void fillRecordsInGap() {

            updateLog += "Filling gaps with 1sec records" + System.lineSeparator();

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
            updateLog += "-- GapNo: "+record.getNo()+", dist: "+record.getDistGap()+"m, time: "+record.getTimeGap()+"sec, @Dist: "
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

    public void addActivityFromAnotherFile(FitFile fileToAdd) {
        clearTempUpdateLog();
        appendTempUpdateLogLn("Adding activity from another file");

        // Keep inserted LAP/TIME_IN_ZONE/WORKOUT/WORKOUT_STEP timestamps aligned
        // with this activity's start timestamp.
        Long baseStartTimestamp = getTimeFirstRecord();
        if (baseStartTimestamp == null && !recordMesg.isEmpty()) {
            baseStartTimestamp = recordMesg.get(0).getFieldLongValue(REC_TIME);
        }
        if (baseStartTimestamp == null && !sessionMesg.isEmpty()) {
            baseStartTimestamp = sessionMesg.get(0).getFieldLongValue(SES_STIME);
        }
        if (baseStartTimestamp == null && !lapMesg.isEmpty()) {
            baseStartTimestamp = lapMesg.get(0).getFieldLongValue(LAP_STIME);
        }

        // Append the merged activity after all messages already in the base file.
        int insertIndex = allMesg.size();

        // Find the segment to add: from first START event to the physical end of the file to add.
        int startIx = -1;
        int stopIx = -1;
        Long firstStartTime = null;
        for (int i = 0; i < fileToAdd.allMesg.size(); i++) {
            Mesg mesg = fileToAdd.allMesg.get(i);
            if (mesg.getNum() != MesgNum.EVENT) continue;
            Short rawEvent = mesg.getFieldShortValue(EVE_EVENT);
            Short rawType = mesg.getFieldShortValue(EVE_TYPE);
            if (rawEvent == null || rawType == null) continue;
            Event event = Event.getByValue(rawEvent);
            EventType type = EventType.getByValue(rawType);

            if (event.equals(Event.TIMER) && type.equals(EventType.START) && startIx < 0) {
                startIx = i;
                firstStartTime = mesg.getFieldLongValue(EVE_TIME);
            }
        }
        if (!fileToAdd.allMesg.isEmpty()) {
            stopIx = fileToAdd.allMesg.size() - 1;
        }

        if (startIx < 0 || stopIx < 0 || stopIx < startIx) {
            appendTempUpdateLogLn("Could not find a valid START/end-of-file segment in file to add. No changes made.");
            return;
        }

        appendTempUpdateLogLn("Selected segment in second file: ix " + startIx + " to " + stopIx
                + " (" + (stopIx - startIx + 1) + " messages)");

        // Collect lap segment from fileToAdd: LAP messages and TIME_IN_ZONE messages that directly follow them
        // and shift TIME_IN_ZONE lap references to destination lap index space.
        int lapIndexOffset = lapMesg.size();
        int adjustedTizRefCount = 0;
        List<Mesg> lapSegment = new ArrayList<>();
        for (int i = 0; i < fileToAdd.allMesg.size(); i++) {
            Mesg mesg = fileToAdd.allMesg.get(i);
            if (mesg.getNum() == MesgNum.LAP) {
                Mesg lapMesgToAdd = new Mesg(mesg);
                if (baseStartTimestamp != null) {
                    lapMesgToAdd.setFieldValue(MESG_TIMESTAMP, baseStartTimestamp);
                }
                lapSegment.add(lapMesgToAdd);
                // Check if the next message is TIME_IN_ZONE (216)
                if (i + 1 < fileToAdd.allMesg.size() && fileToAdd.allMesg.get(i + 1).getNum() == 216) {
                    Mesg tizMesg = new Mesg(fileToAdd.allMesg.get(i + 1));
                    if (baseStartTimestamp != null) {
                        tizMesg.setFieldValue(MESG_TIMESTAMP, baseStartTimestamp);
                    }
                    Integer tizRefMesg = tizMesg.getFieldIntegerValue(TIZ_REF_MESG);
                    Integer tizRefIx = tizMesg.getFieldIntegerValue(TIZ_REF_IX);
                    if (tizRefIx != null && (tizRefMesg == null || tizRefMesg == MesgNum.LAP)) {
                        tizMesg.setFieldValue(TIZ_REF_IX, tizRefIx + lapIndexOffset);
                        adjustedTizRefCount++;
                    }
                    lapSegment.add(tizMesg);
                    i++; // Skip the TIME_IN_ZONE message in the loop
                }
            }
        }
        if (adjustedTizRefCount > 0) {
            appendTempUpdateLogLn("Adjusted TIME_IN_ZONE reference_index by +" + lapIndexOffset
                    + " for " + adjustedTizRefCount + " message(s).");
        }

        Mesg existingWorkoutMesg = null;
        int existingWorkoutStepsCount = 0;
        for (Mesg mesg : allMesg) {
            if (mesg.getNum() == MesgNum.WORKOUT && existingWorkoutMesg == null) {
                existingWorkoutMesg = mesg;
            }
            if (mesg.getNum() == MesgNum.WORKOUT_STEP) {
                existingWorkoutStepsCount++;
            }
        }

        Mesg addedWorkoutMesg = null;
        List<Mesg> addedWorkoutSteps = new ArrayList<>();
        for (Mesg mesg : fileToAdd.allMesg) {
            if (mesg.getNum() == MesgNum.WORKOUT && addedWorkoutMesg == null) {
                addedWorkoutMesg = new Mesg(mesg);
                if (baseStartTimestamp != null) {
                    addedWorkoutMesg.setFieldValue(MESG_TIMESTAMP, baseStartTimestamp);
                }
            }
            if (mesg.getNum() == MesgNum.WORKOUT_STEP) {
                Mesg stepMesg = new Mesg(mesg);
                if (baseStartTimestamp != null) {
                    stepMesg.setFieldValue(MESG_TIMESTAMP, baseStartTimestamp);
                }
                addedWorkoutSteps.add(stepMesg);
            }
        }

        if (baseStartTimestamp != null) {
            appendTempUpdateLogLn("Set timestamp(253) for inserted LAP/TIME_IN_ZONE/WORKOUT/WORKOUT_STEP to "
                    + FitDateTime.toString(baseStartTimestamp, diffMinutesLocalUTC));
        } else {
            appendTempUpdateLogLn("Could not resolve base start timestamp; left inserted LAP/TIME_IN_ZONE/WORKOUT/WORKOUT_STEP timestamps unchanged.");
        }

        Integer originalNumValidSteps = null;
        if (existingWorkoutMesg != null) {
            originalNumValidSteps = existingWorkoutMesg.getFieldIntegerValue(WorkoutMesg.NumValidStepsFieldNum);
        }
        if (originalNumValidSteps == null) {
            originalNumValidSteps = existingWorkoutStepsCount;
        }

        boolean mergeWorkoutIntoExisting = existingWorkoutMesg != null && addedWorkoutMesg != null;
        if (mergeWorkoutIntoExisting && originalNumValidSteps != null && originalNumValidSteps > 0) {
            for (Mesg lapMesgToAdjust : lapSegment) {
                if (lapMesgToAdjust.getNum() != MesgNum.LAP) {
                    continue;
                }
                Integer lapWktStepIx = lapMesgToAdjust.getFieldIntegerValue(LAP_WKT_STEP_IDX);
                if (lapWktStepIx != null) {
                    lapMesgToAdjust.setFieldValue(LAP_WKT_STEP_IDX, lapWktStepIx + originalNumValidSteps);
                }
            }
            appendTempUpdateLogLn("Adjusted appended LAP wkt_step_index by +" + originalNumValidSteps + " (existing workout steps).");
        }

        if (existingWorkoutMesg == null && addedWorkoutMesg != null) {
            // Keep original WORKOUT message_index as-is (no global 254 rewrite).
        } else if (existingWorkoutMesg != null && addedWorkoutMesg != null) {
            Integer existingNumValidSteps = existingWorkoutMesg.getFieldIntegerValue(WKT_NUMSTEPS);
            if (existingNumValidSteps == null) {
                existingNumValidSteps = 0;
            }
            Integer addedNumValidSteps = addedWorkoutMesg.getFieldIntegerValue(WKT_NUMSTEPS);
            if (addedNumValidSteps == null) {
                addedNumValidSteps = 0;
            }
            existingWorkoutMesg.setFieldValue(WKT_NUMSTEPS, existingNumValidSteps + addedNumValidSteps);

            String existingWktName = existingWorkoutMesg.getFieldStringValue(WKT_NAME);
            String addedWktName = addedWorkoutMesg.getFieldStringValue(WKT_NAME);
            if (addedWktName != null && !addedWktName.isBlank()) {
                if (existingWktName == null || existingWktName.isBlank()) {
                    existingWorkoutMesg.setFieldValue(WKT_NAME, addedWktName);
                    setWktName(addedWktName);
                } else if (!existingWktName.contains(addedWktName)) {
                    existingWorkoutMesg.setFieldValue(WKT_NAME, existingWktName + " + " + addedWktName);
                    setWktName(existingWktName + " + " + addedWktName);
                }
            }
            appendTempUpdateLogLn("Merged WORKOUT(26): num_valid_steps +" + addedNumValidSteps);
        }

        boolean hasOriginalWorkoutSteps = existingWorkoutStepsCount > 0;
        for (Mesg stepMesg : addedWorkoutSteps) {
            if (hasOriginalWorkoutSteps) {
                Short durationType = stepMesg.getFieldShortValue(WKTST_DURTYPE);
                if (durationType != null && durationType == 6 && originalNumValidSteps != null && originalNumValidSteps > 0) {
                    Long durationValue = stepMesg.getFieldLongValue(WKTST_DURVALUE);
                    if (durationValue != null) {
                        stepMesg.setFieldValue(WKTST_DURVALUE, durationValue + originalNumValidSteps.longValue());
                    }
                }
            }
        }

        int workoutInsertIndex = 0;
        for (int i = 0; i < allMesg.size(); i++) {
            int mesgNum = allMesg.get(i).getNum();
            if (mesgNum == MesgNum.WORKOUT_SESSION || mesgNum == MesgNum.WORKOUT || mesgNum == MesgNum.WORKOUT_STEP) {
                workoutInsertIndex = i + 1;
            }
        }
        if (workoutInsertIndex == 0) {
            for (int i = 0; i < allMesg.size(); i++) {
                int mesgNum = allMesg.get(i).getNum();
                if (mesgNum == MesgNum.LAP || mesgNum == MesgNum.SESSION || mesgNum == MesgNum.EVENT || mesgNum == MesgNum.RECORD) {
                    workoutInsertIndex = i;
                    break;
                }
            }
        }

        int insertedWorkoutMesgCount = (existingWorkoutMesg == null && addedWorkoutMesg != null ? 1 : 0) + addedWorkoutSteps.size();
        int workoutInsertStartIndex = workoutInsertIndex;

        if (existingWorkoutMesg == null && addedWorkoutMesg != null) {
            allMesg.add(workoutInsertIndex, addedWorkoutMesg);
            workoutInsertIndex++;
            appendTempUpdateLogLn("Inserted WORKOUT(26) from second file.");
        }
        for (Mesg stepMesg : addedWorkoutSteps) {
            allMesg.add(workoutInsertIndex, stepMesg);
            workoutInsertIndex++;
        }
        if (!addedWorkoutSteps.isEmpty()) {
            appendTempUpdateLogLn("Inserted WORKOUT_STEP(27) messages from second file: " + addedWorkoutSteps.size());
        }
        if (insertedWorkoutMesgCount > 0 && workoutInsertStartIndex <= insertIndex) {
            insertIndex += insertedWorkoutMesgCount;
        }

        // Find insertion index for laps in this file: after the last LAP or TIME_IN_ZONE message
        int lapInsertIndex = 0;
        for (int i = 0; i < allMesg.size(); i++) {
            Mesg mesg = allMesg.get(i);
            if (mesg.getNum() == MesgNum.LAP || mesg.getNum() == MesgNum.TIME_IN_ZONE) {
                lapInsertIndex = i + 1;
            }
        }

        int lapInsertStartIndex = lapInsertIndex;

        // Insert lap segment
        for (Mesg mesg : lapSegment) {
            allMesg.add(lapInsertIndex, mesg);
            lapInsertIndex++;
        }
        appendTempUpdateLogLn("Inserted " + lapSegment.size() + " lap-related messages from second file." + System.lineSeparator()
            + "Lap messages inserted at index: " + (lapInsertIndex - lapSegment.size()) + " to " + (lapInsertIndex - 1) + ".");

        // Adjust insertIndex for activity segment if laps were inserted before it
        if (!lapSegment.isEmpty() && lapInsertStartIndex <= insertIndex) {
            insertIndex += lapSegment.size();
        }

        Long pauseSeconds = 0L;
        // Calculate pause between activities
        Long lastTimeInOrgFile = timeLastRecord;
        if (lastTimeInOrgFile == null && !recordMesg.isEmpty()) {
            lastTimeInOrgFile = recordMesg.get(recordMesg.size() - 1).getFieldLongValue(REC_TIME);
        }
        if (lastTimeInOrgFile != null && firstStartTime != null) {
            pauseSeconds = firstStartTime - lastTimeInOrgFile;
            appendTempUpdateLogLn("Pause between activities: " + PehoUtils.sec2minSecLong(pauseSeconds) + System.lineSeparator()
                + "Last time in first file: " + FitDateTime.toString(lastTimeInOrgFile, diffMinutesLocalUTC) + System.lineSeparator()
                + "First START time in second file: " + FitDateTime.toString(firstStartTime, diffMinutesLocalUTC));
        }

        // Calculate elevation difference
        Float lastAltThis = null;
        if (!recordMesg.isEmpty()) {
            lastAltThis = recordMesg.get(recordMesg.size() - 1).getFieldFloatValue(REC_EALT);
        }
        Float lastDistThis = null;
        if (!recordMesg.isEmpty()) {
            lastDistThis = recordMesg.get(recordMesg.size() - 1).getFieldFloatValue(REC_DIST);
        }
        Float firstAltSegment = null;
        for (int i = startIx; i <= stopIx; i++) {
            Mesg mesg = fileToAdd.allMesg.get(i);
            if (mesg.getNum() == MesgNum.RECORD) {
                firstAltSegment = mesg.getFieldFloatValue(REC_EALT);
                break;
            }
        }
        Float elevationDiff = 0f;
        if (lastAltThis != null && firstAltSegment != null) {
            elevationDiff = lastAltThis - firstAltSegment;
            appendTempUpdateLogLn("Elevation difference: " + elevationDiff + "m (last of first file: " + lastAltThis + "m, first of second file: " + firstAltSegment + "m)" + System.lineSeparator());
        } else {
            appendTempUpdateLogLn("Elevation difference could not be calculated (missing data).");
        }

        // Insert selected message types from the segment into this file (keep original order, no time changes)
        // Exclude list: skip these message types during merge
        List<Mesg> segment = new ArrayList<>();
        int skippedMesgInSegment = 0;
        for (int i = startIx; i <= stopIx; i++) {
            Mesg sourceMesg = fileToAdd.allMesg.get(i);
            if (
                       sourceMesg.getNum() == 0    // FILE_ID(0)
                    || sourceMesg.getNum() == 49   // FILE_CREATOR(49)
                    || sourceMesg.getNum() == 34   // ACTIVITY(34)
                    || sourceMesg.getNum() == 207  // DEVELOPER_DATA_ID(207)
                    || sourceMesg.getNum() == 140  // activity_metrics(140)
                    || sourceMesg.getNum() == 18   // SESSION(18)
                    || sourceMesg.getNum() == 216  // TIME_IN_ZONE(216)
                    || sourceMesg.getNum() == 19   // LAP(19)
                    || sourceMesg.getNum() == 312  // SPLIT
                    || sourceMesg.getNum() == 313  // SPLIT_SUM
                    // Above usually before first START event and are then not incuded in segment added
                    || sourceMesg.getNum() == 141  // epo_status
                    || sourceMesg.getNum() == 394  // cpe_status
                    || sourceMesg.getNum() == 2    // DEVICE_SETTINGS
                    || sourceMesg.getNum() == 3    // USER_PROFILE
                    || sourceMesg.getNum() == 147  // SENSOR_SETTINGS
                    || sourceMesg.getNum() == 79   // user_metrics
                    || sourceMesg.getNum() == 12   // SPORT
                    || sourceMesg.getNum() == 7    // ZONES_TARGET
                    || sourceMesg.getNum() == 26   // WORKOUT
                    || sourceMesg.getNum() == 27   // WORKOUT_STEP
                    || sourceMesg.getNum() == 206  // FIELD_DESCRIPTION(206)
                    || sourceMesg.getNum() == 13   // TRAINING_SETTINGS
                    ) {
                skippedMesgInSegment++;
                continue;
            }

            Mesg mesg = new Mesg(sourceMesg);
            if (mesg.getNum() == MesgNum.RECORD) {
                if (elevationDiff != 0f) {
                    Float alt = mesg.getFieldFloatValue(REC_EALT);
                    if (alt != null) {
                        mesg.setFieldValue(REC_EALT, alt + elevationDiff);
                    }
                }
                if (lastDistThis != null) {
                    Float dist = mesg.getFieldFloatValue(REC_DIST);
                    if (dist != null) {
                        mesg.setFieldValue(REC_DIST, dist + lastDistThis);
                    }
                }
            }
            segment.add(mesg);
        }
        appendTempUpdateLogLn("Filtered segment (excluded: 141/394/3/79/12/13/207/206/72). Added: " + segment.size() + ", skipped: " + skippedMesgInSegment);

        for (Mesg mesg : segment) {
            allMesg.add(insertIndex, mesg);
            insertIndex++;
        }

        // Rebuild internal message lists so they reflect the appended segment
        rebuildMessageListsFromAllMesg();

        // WORKOUT_STEP message index (field 254) is per-step sequence and should be 0..N-1.
        rewriteWorkoutStepMessageIndexesFromZero();

        // Update number of laps field
        this.numberOfLaps = lapMesg.size();

        // Update overall metadata
        this.timeFirstRecord = Math.min(this.timeFirstRecord, fileToAdd.timeFirstRecord);
        this.timeLastRecord = Math.max(this.timeLastRecord, fileToAdd.timeLastRecord);
        this.totalTimerTime += fileToAdd.getTotalTimerTime();
        this.totalDistance += fileToAdd.totalDistance;

        if (!sessionMesg.isEmpty()) {
            appendTempUpdateLogLn("Updating/resetting Profilename to Sport Profile: " 
                + sessionMesg.get(0).getFieldStringValue(SES_PROFILE) + " -> "
                + sportMesg.get(0).getFieldStringValue(SP_NAME));

            setSportProfile(sportMesg.get(0).getFieldStringValue(SP_NAME));

            appendTempUpdateLogLn("Updating session and activity messages with new totals");
            appendTempUpdateLogLn("TotalTimerTime:" + PehoUtils.sec2minSecLong(totalTimerTime));
            appendTempUpdateLogLn("TotalDistance:" + totalDistance);
            appendTempUpdateLogLn("TotalElapsedTimer first file:" + PehoUtils.sec2minSecLong(sessionMesg.get(0).getFieldLongValue(SES_ETIMER)));
            appendTempUpdateLogLn("TotalElapsedTimer second file:" + PehoUtils.sec2minSecLong(fileToAdd.sessionMesg.get(0).getFieldLongValue(SES_ETIMER)));
            sessionMesg.get(0).setFieldValue(SES_DIST, totalDistance);
            sessionMesg.get(0).setFieldValue(SES_TIMER, getTotalTimerTime());
            activityMesg.get(0).setFieldValue(ACT_TIMER, getTotalTimerTime());

            sessionMesg.get(0).setFieldValue(SES_ETIMER,
                    sessionMesg.get(0).getFieldLongValue(SES_ETIMER)
                    + fileToAdd.sessionMesg.get(0).getFieldLongValue(SES_ETIMER) 
                    + pauseSeconds);

            if (sessionMesg.get(0).getFieldLongValue(SES_MTIMER) != null 
                    || fileToAdd.sessionMesg.get(0).getFieldLongValue(SES_MTIMER) != null) {
                sessionMesg.get(0).setFieldValue(SES_MTIMER, 
                        sessionMesg.get(0).getFieldLongValue(SES_MTIMER)
                        + fileToAdd.sessionMesg.get(0).getFieldLongValue(SES_MTIMER));
            }

            if (totalTimerTime > 0) {
                Float avg = totalDistance / getTotalTimerTime();
                sessionMesg.get(0).setFieldValue(SES_SPEED, avg);
                sessionMesg.get(0).setFieldValue(SES_ESPEED, avg);
            }

            // Update number of laps
            int secondFileLaps = 0;
            for (Mesg m : lapSegment) {
                if (m.getNum() == MesgNum.LAP) {
                    secondFileLaps++;
                }
            }
            Integer currentLaps = sessionMesg.get(0).getFieldIntegerValue(SES_LAPS);
            if (currentLaps == null) currentLaps = 0;
            int totalLaps = currentLaps + secondFileLaps;
            sessionMesg.get(0).setFieldValue(SES_LAPS, totalLaps);
        }

        // Update LAP_IX for all laps
        for (int i = 0; i < lapMesg.size(); i++) {
            lapMesg.get(i).setFieldValue(FitFile.LAP_IX, i);
        }

        // Update SES_LAPS
        if (!sessionMesg.isEmpty()) {
            sessionMesg.get(0).setFieldValue(FitFile.SES_LAPS, numberOfLaps);
        }

        System.out.println(getTempUpdateLog());
        appendUpdateLog(getTempUpdateLog());
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void rewriteWorkoutStepMessageIndexesFromZero() {
        int stepIx = 0;
        for (Mesg stepMesg : wktStepMesg) {
            stepMesg.setFieldValue(WorkoutStepMesg.MessageIndexFieldNum, stepIx);
            stepIx++;
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void rebuildMessageListsFromAllMesg() {
        // Keep ordering from allMesg, but rebuild per-type caches
        fileIdMesg.clear();
        deviceInfoMesg.clear();
        wktSessionMesg.clear();
        wktStepMesg.clear();
        wktRecordMesg.clear();
        activityMesg.clear();
        sessionMesg.clear();
        splitMesg.clear();
        lapMesg.clear();
        eventMesg.clear();
        eventTimerMesg.clear();
        recordMesg.clear();

        for (Mesg mesg : allMesg) {
            switch (mesg.getNum()) {
                case MesgNum.FILE_ID:
                    fileIdMesg.add(mesg);
                    break;
                case MesgNum.DEVICE_INFO:
                    deviceInfoMesg.add(mesg);
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
                case MesgNum.ACTIVITY:
                    activityMesg.add(mesg);
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
                    Short rawEvent = mesg.getFieldShortValue(EVE_EVENT);
                    if (rawEvent != null && Event.getByValue(rawEvent).equals(Event.TIMER)) {
                        eventTimerMesg.add(mesg);
                    }
                    break;
                case MesgNum.RECORD:
                    recordMesg.add(mesg);
                    break;
                default:
                    // Ignore other types
                    break;
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
        updateLog += info;
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
        lapMesg.get(0).setFieldValue(LAP_TIMER, lapTimer);
        lapMesg.get(0).setFieldValue(LAP_ETIMER, lapETimer);

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

        //----------------------
        // Updating ACTIVITY DATA
        //----------------------
        activityMesg.get(0).setFieldValue(ACT_TIMER, getTotalTimerTime());
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
        updateLog += info2;

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void deleteRecordsCreateGap(Long fromTimer, Long toTimer) {
        Long fromTime = recordMesg.get(findIxInRecordMesgBasedOnTimer(fromTimer)).getFieldLongValue(REC_TIME);
        Long toTime = recordMesg.get(findIxInRecordMesgBasedOnTimer(toTimer)).getFieldLongValue(REC_TIME);

        int ixInRecordMesgStart = findIxInRecordMesgBasedOnTime(fromTime);
        int ixInRecordMesgStop = findIxInRecordMesgBasedOnTime(toTime);
        Float distDiffToApply = null;

        Mesg startRecord = recordMesg.get(ixInRecordMesgStart - 1);
        Mesg stopRecord = recordMesg.get(ixInRecordMesgStop);
        Float startRecordDist = startRecord.getFieldFloatValue(REC_DIST);
        Float stopRecordDist = stopRecord.getFieldFloatValue(REC_DIST);
        Integer startLat = startRecord.getFieldIntegerValue(REC_LAT);
        Integer startLon = startRecord.getFieldIntegerValue(REC_LON);
        Integer stopLat = stopRecord.getFieldIntegerValue(REC_LAT);
        Integer stopLon = stopRecord.getFieldIntegerValue(REC_LON);

        if (startRecordDist != null && stopRecordDist != null && startLat != null && startLon != null && stopLat != null && stopLon != null) {
            float originalDistFromRecords = stopRecordDist - startRecordDist;
            double geoUtilsDist = GeoUtils.distCalc(startLat, startLon, stopLat, stopLon);
            double diff = geoUtilsDist - originalDistFromRecords;
            distDiffToApply = (float) diff;

            System.out.printf(
                "===> Dist start-stop ix %d-%d: recordDist=%.2fm, geoDist=%.2fm, diff=%.2fm%n",
                ixInRecordMesgStart - 1,
                ixInRecordMesgStop,
                originalDistFromRecords,
                geoUtilsDist,
                diff
            );
        } else {
            System.out.printf(
                "===> Dist start-stop ix %d-%d: cannot calculate (missing record distance and/or GPS fields)%n",
                ixInRecordMesgStart - 1,
                ixInRecordMesgStop
            );
        }

        if (distDiffToApply != null) {
            addDistToRecords(ixInRecordMesgStop, distDiffToApply);
        }

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
        setNumberOfRecords(recordMesg.size());

        appendTempUpdateLog("===> Input values: " + FitDateTime.toTimerString(fromTimer) + ", " + FitDateTime.toTimerString(toTimer) + System.lineSeparator());
        appendTempUpdateLog("===> Adjusted record distances from ix " + ixInRecordMesgStop + " with diff " + String.format("%.2f", distDiffToApply) + "m" + System.lineSeparator());
        appendTempUpdateLog("===> RecordMesgIx to delete from-to: " + ixInRecordMesgStart + " - " + ixInRecordMesgStop + System.lineSeparator());
        appendTempUpdateLog("===> AllMesgIx to delete from: " + ixInAllMesgStart + " and number of records: " + numberOfRecordsToDelete + System.lineSeparator());
        appendTempUpdateLog("===> Deleting records to create gap of " + FitDateTime.toTimerString(toTimer-fromTimer+3) //+3 because to and from should be included
             + ", from " + FitDateTime.toTimerString(fromTimer) + " into the activity" + System.lineSeparator());
        appendTempUpdateLog("===> Deleting records from " + FitDateTime.toString(fromTime,0)
             + " to " + FitDateTime.toString(toTime,0) + System.lineSeparator());

        appendUpdateLog(getTempUpdateLog());
        System.out.print(getTempUpdateLog());

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
        updateLog += info;
        System.out.print(info);

        allMesg.add(findIxInAllMesgBasedOnTime(stopTime), newGapRecord);
        recordMesg.add(gapToChange.ixStop, newGapRecord);
        numberOfRecords++;

        // Updating SESSION DATA
        //----------------------
        //totalTimerTime += (float) gapToChange.timeGap - newPauseTime;
        //sessionMesg.get(0).setFieldValue(SES_TIMER, getTotalTimerTime());
        //elapsedTimerTime += (float) gapToChange.timeGap - newPauseTime;
        //sessionMesg.get(0).setFieldValue(SES_ETIMER, elapsedTimerTime);

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printEvents(Long eventTimeStartToPrint, Long eventTimeEndToPrint, Event eventToPrint, EventType eventTypeToPrint) {
        getPauseReportGenerator().printEvents(eventTimeStartToPrint, eventTimeEndToPrint, eventToPrint, eventTypeToPrint);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void createEvent(Long eventTime, Event eventToCreate, EventType eventTypeToCreate) {

        int i = 0;
        int latestRecordIndex = 0;
        int addEventHereIndex = 0;
        int addOneToIndexIfToPutAfter = 0;
        Boolean firstMatchingEventFound = false;
        Mesg eventMesgToAdd = null;


        // if STOP_ALL then the event should be after RECORD
        if (eventTypeToCreate == EventType.STOP_ALL) {
            addOneToIndexIfToPutAfter = 1;
            appendTempUpdateLog("Event Type is a STOP_ALL. Insert of Event record will be AFTER data record with the same time.");
        } else {
            appendTempUpdateLog("Event Type is NOT STOP_ALL. Insert of Event record will be BEFORE data record with the same time.");
        }

        for (Mesg mesg : allMesg) {

            if (!firstMatchingEventFound && mesg.getNum() == MesgNum.EVENT) {
                Short rawEvent = mesg.getFieldShortValue(EVE_EVENT);
                Event mesgEvent = Event.getByValue(rawEvent);

                if (mesgEvent.equals(eventToCreate)) {
                    System.out.println("Found first matching event @ix: " + i + " / " + mesgEvent);
                    eventMesgToAdd = new Mesg(mesg);
                    firstMatchingEventFound = true;
                }
            }

            if (mesg.getNum() == MesgNum.RECORD) {

                Long recordTime = mesg.getFieldLongValue(REC_TIME);
                if (recordTime != null && recordTime >= eventTime) {
                    System.out.println("Found matching record @ix: " + i + " / " + FitDateTime.toString(recordTime,diffMinutesLocalUTC));
                    if (recordTime.equals(eventTime)) {
                        // If we found a matching record time, we can insert the event here
                        addEventHereIndex = i + addOneToIndexIfToPutAfter;
                        appendTempUpdateLogLn("Data record was found at wanted event time.");
                    } else {
                        // record time is greater than event time
                        // insert at latest record
                        addEventHereIndex = latestRecordIndex + addOneToIndexIfToPutAfter;
                        appendTempUpdateLogLn("Data record was NOT found at wanted event time. Insert at latest record.");
                    }
                    break;
                }

                latestRecordIndex = i;
            }
            i++;
        }
        eventMesgToAdd.setFieldValue(EVE_TIME, eventTime);
        eventMesgToAdd.setFieldValue(EVE_EVENT, eventToCreate.getValue());
        eventMesgToAdd.setFieldValue(EVE_TYPE, eventTypeToCreate.getValue());
        allMesg.add(addEventHereIndex, eventMesgToAdd);
        reCreateEventMesg();

        appendTempUpdateLogLn("Created event @ix: " + addEventHereIndex);
        System.out.println(getTempUpdateLog());
        appendUpdateLog(getTempUpdateLog());
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void deleteEvents(Long eventTimeStartToDelete, Long eventTimeStopToDelete, Event eventToDelete, EventType eventTypeToDelete) {

        // Use EventType EventType.INVALID for Event independent of type

        // Find EVENT messages to delete in mesg list
        // ========================================
        int mesgIx = 0;
        int eventCounter = 0;
        List<Integer> mesgToDelete = new ArrayList<>();
        appendTempUpdateLogLn("START - Deleting events" + System.lineSeparator() + "------------------------------");
        appendTempUpdateLogLn("Input values to delete events between " + FitDateTime.toString(new DateTime(eventTimeStartToDelete),diffMinutesLocalUTC) + " and " + FitDateTime.toString(new DateTime(eventTimeStopToDelete),diffMinutesLocalUTC));
        appendTempUpdateLogLn("Event to delete: " + (eventToDelete.equals(Event.INVALID) ? "ALL" : eventToDelete));
        appendTempUpdateLogLn("Event type to delete: " + (eventTypeToDelete.equals(EventType.INVALID) ? "ALL" : eventTypeToDelete));


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
                    if (mesgEvent.equals(eventToDelete) || (eventToDelete.equals(Event.INVALID) && !mesgEvent.equals(Event.TIMER))) {

                        // If event is a TIMER event
                        if (eventTypeToDelete.equals(EventType.INVALID)) {
                            mesgToDelete.add(mesgIx);
                                appendTempUpdateLogLn("Found matching EVENT to delete in allMesg: " + 
                                    Event.getByValue(mesg.getFieldShortValue(EVE_EVENT)) + 
                                    EventType.getByValue(mesg.getFieldShortValue(EVE_TYPE)) + 
                                    " @ix:" + mesgIx);

                        // If event is not a TIMER event
                        } else {
                            if (mesgEventType.equals(eventTypeToDelete)) {
                                mesgToDelete.add(mesgIx);
                                appendTempUpdateLogLn("Found matching EVENT and eventTYPE to delete in allMesg: " + 
                                    Event.getByValue(mesg.getFieldShortValue(EVE_EVENT)) + 
                                    EventType.getByValue(mesg.getFieldShortValue(EVE_TYPE)) + 
                                    " @ix:" + mesgIx);
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
        updateLog += tempLog;

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
        getPauseFix().pauseIncrease(pauseNo, secondsToPutIntoPause);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void LapMerge(int fromLap, int toLap) {
        getLapFix().lapMerge(fromLap, toLap);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void shortenPause(int pauseNo, Long newPauseTime) {
        getPauseFix().pauseShorten(pauseNo, newPauseTime);
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
/*     public String getFilenameAndSetNewSportProfileName(String suffix, String outputFilenameBase) {
        
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
//          if (!wktRecords.isEmpty()) {
//             if (wktRecords.get(0).getWktName() != null) {
//                 newProfileName = wktRecords.get(0).getWktName();
//             }
//         }
 
        newProfileName = newProfileName + " " + PehoUtils.m2km1(totalDistance) + "km";
        sessionMesg.get(0).setFieldValue(SES_PROFILE, newProfileName);

        System.out.println("----> New SportProfile:  " + newProfileName);

        outputFilenameBase = "-" + newProfileName;
        outputFilenameBase = outputFilenameBase.replace("/","!");
        outputFilenameBase = outputFilenameBase.replace("×","x");

        System.out.println("----> New FilenameBase: " + outputFilenameBase);
        return outputFilenameBase;
    } */
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void detectAndSetMySport() {
        String profile = sportProfile != null ? sportProfile.toLowerCase() : "";
        if     (profile.contains("skierg")
             || profile.contains("row")
            ) {
            mySport = MySport.SKIERG;
        } else if (profile.contains("löpband")
                || profile.contains("pband")
                || profile.contains("mill")
                || profile.contains("tread")
            ) {
            mySport = MySport.TREADMILL;
        } else if (profile.contains("ellipt")
                || profile.contains("gymbike")
                || profile.contains("spinbike")
                || profile.contains("ct")
            ) {
            mySport = MySport.ELLIPTICAL;
        } else {
            mySport = MySport.OTHER;
        }
        System.out.println("======== detectAndSetMySport: " + mySport + " (profile: '" + sportProfile + "')");
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // public Boolean isSkiErgFile() {
    //     System.out.println("======== isSkiErgFile TEST ==========");
    //     Boolean isTrue = false;
    //     if (sportProfile != null && (sportProfile.toLowerCase().contains("skierg")
    //         )) {
    //             isTrue = true;
    //     }
    //     appendTempUpdateLogg("======== isSkiErgFile TEST RESULT: " + isTrue);
    //     System.out.println("======== isSkiErgFile TEST RESULT: " + isTrue);
    //     return isTrue;
    // }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // public Boolean isTreadmillFile() {
    //     System.out.println("======== isTreadmillFile TEST ==========");
    //     Boolean isTrue = false;
    //     if (sportProfile != null && (sportProfile.toLowerCase().contains("löpband") 
    //         || sportProfile.toLowerCase().contains("treadmill")
    //         )) {
    //             isTrue = true;
    //     }
    //     return isTrue;
    // }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // public Boolean isEllipticalFile() {
    //     System.out.println("======== isEllipticalFile TEST ==========");
    //     Boolean isTrue = false;
    //     if (sportProfile != null && (sportProfile.toLowerCase().contains("ellipt")
    //         || sportProfile.toLowerCase().contains("gymbike")
    //         || sportProfile.toLowerCase().contains("spinbike")
    //         || sportProfile.toLowerCase().contains("ct")
    //     )) {
    //             isTrue = true;
    //     }
    //     return isTrue;
    // }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void readFitFile (String inputFilePath) {

        clearTempUpdateLog();

        FileInputStream in;
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
            Decode decode;
            decode = new Decode();

            appendTempUpdateLogLn("FIT SDK profile version: "
                    + Fit.PROFILE_VERSION_MAJOR + "." + Fit.PROFILE_VERSION_MINOR
                    + " " + Fit.PROFILE_TYPE
                    + " (" + Fit.PROFILE_VERSION + ")");

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
                        case MesgNum.SPORT:
                            sportMesg.add(mesg);
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
                        case MesgNum.SPLIT_SUMMARY:
                            splitSummaryMesg.add(mesg);
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
                        case MesgNum.DEVELOPER_DATA_ID:
                            devDataIdMesg.add(mesg);
                            break;
                        case MesgNum.FIELD_DESCRIPTION:
                            fieldDescrMesg.add(mesg);
                            break;
                        case 132:
                            appendTempUpdateLogLn("Heart Rate Mesg found, but not added to list. Mesg ix: " + mesg.getFieldLongValue(253));
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
                setManufacturerNo(fileIdMesg.get(0).getFieldIntegerValue(FID_MANU));
                setManufacturer(Manufacturer.getStringFromValue(getManufacturerNo()));
                if ("GARMIN".equals(getManufacturer())) {
                    if (fileIdMesg.get(0).getFieldIntegerValue(FID_MANU) != null) {
                        setProductNo(fileIdMesg.get(0).getFieldIntegerValue(FID_PROD));
                        setProduct(GarminProduct.getStringFromValue(fileIdMesg.get(0).getFieldIntegerValue(FID_PROD)));
                    }
                }
            }

            if (!deviceInfoMesg.isEmpty()) {
                Float swVer = deviceInfoMesg.get(0).getFieldFloatValue(DINFO_SWVER);
                setSwVer(swVer == null ? 0f : swVer);
            } else {
                setSwVer(0f);
            }
            
            setTimeFirstRecord(recordMesg.get(0).getFieldLongValue(REC_TIME));

            if (!activityMesg.isEmpty()) {
                if (activityMesg.get(0).getFieldLongValue(ACT_TIME) == null) {
                    activityMesg.get(0).setFieldValue(ACT_TIME, getTimeFirstRecord());
                }
                setActivityDateTimeUTC(activityMesg.get(0).getFieldLongValue(ACT_TIME));
                if (activityMesg.get(0).getFieldLongValue(ACT_LOCTIME) == null) {
                    activityMesg.get(0).setFieldValue(ACT_LOCTIME, getTimeFirstRecord());
                }
                setActivityDateTimeLocal(activityMesg.get(0).getFieldLongValue(ACT_LOCTIME));
                setDiffMinutesLocalUTC((getActivityDateTimeLocal() - getActivityDateTimeUTC()) / 60);
                setActivityDateTimeLocalOrg(getActivityDateTimeLocal());
            } else {
                setActivityDateTimeUTC(getTimeFirstRecord());
                setActivityDateTimeLocal(getTimeFirstRecord());
                setDiffMinutesLocalUTC(0L);
                setActivityDateTimeLocalOrg(getActivityDateTimeLocal());
            }

            if (!wktRecordMesg.isEmpty()) {
                if (wktRecordMesg.get(0).getFieldStringValue(WKT_NAME) != null) {
                    setWktName(wktRecordMesg.get(0).getFieldStringValue(WKT_NAME));
                }
            } else {
                setWktName("");
            }

            if (!sessionMesg.isEmpty()) {
                if (sessionMesg.get(0).getFieldValue(SES_SPORT) != null) {
                    setSport(Sport.getByValue(sessionMesg.get(0).getFieldShortValue(SES_SPORT)));
                }
                if (sessionMesg.get(0).getFieldValue(SES_SUBSPORT) != null) {
                    setSubsport(SubSport.getByValue(sessionMesg.get(0).getFieldShortValue(SES_SUBSPORT)));
                }
                if (sessionMesg.get(0).getFieldStringValue(SES_PROFILE) == null) {
                    sessionMesg.get(0).setFieldValue(SES_PROFILE, "noProfile");
                } else {
                    setSportProfile(sessionMesg.get(0).getFieldStringValue(SES_PROFILE).trim());
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
            } else {
                setSport(Sport.INVALID);
                setSubsport(SubSport.INVALID);
                setSportProfile("noProfile");
                setTotalTimerTime(0f);
                setTotalDistance(0f);
                setTotalDistanceOrg(0f);
                setAvgSpeed(0f);
            }

            setNumberOfLaps(lapMesg.size());

            setTimeFirstRecordOrg(timeFirstRecord);
            setTimeLastRecord(recordMesg.get(recordMesg.size() - 1).getFieldLongValue(REC_TIME));
            setNumberOfRecords(recordMesg.size());
            detectAndSetMySport();

            appendTempUpdateLogLn("FIT file successfully read. Total records: "
              + numberOfRecords
              + ", Time between: "
              + FitDateTime.toString(getTimeFirstRecord(), getDiffMinutesLocalUTC())
              + " -- "
              + FitDateTime.toString(getTimeLastRecord(), getDiffMinutesLocalUTC())
            );

        } catch (FitRuntimeException e) {
            System.err.println("Error processing FIT file: " + e.getMessage());
        }
        System.out.println(getTempUpdateLog());
        appendUpdateLog(getTempUpdateLog());
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
            boolean handledTimestamp = false;
            switch (mesg.getNum()) {
                case MesgNum.FILE_ID:
                    handledTimestamp = true;
                    timeToChange = mesg.getFieldLongValue(FID_CTIME);
                    if (timeToChange != null) {
                        mesg.setFieldValue(FID_CTIME, timeToChange + changeSeconds);
                    }
                    break;
                case MesgNum.ACTIVITY:
                    handledTimestamp = true;
                    timeToChange = mesg.getFieldLongValue(ACT_TIME);
                    if (timeToChange != null) {
                        mesg.setFieldValue(ACT_TIME, timeToChange + changeSeconds);
                    }
                    timeToChange = mesg.getFieldLongValue(ACT_LOCTIME);
                    if (timeToChange != null) {
                        mesg.setFieldValue(ACT_LOCTIME, timeToChange + changeSeconds);
                    }
                    break;
                case 140:  // ActivityMetrics
                    handledTimestamp = true;
                    timeToChange = mesg.getFieldLongValue(MESG_TIMESTAMP);
                    if (timeToChange != null) {
                        mesg.setFieldValue(MESG_TIMESTAMP, timeToChange + changeSeconds);
                    }
                    timeToChange = mesg.getFieldLongValue(48); //Local timestamp
                    if (timeToChange != null) {
                        mesg.setFieldValue(48, timeToChange + changeSeconds);
                    }
                    break;
                case 79:  //UserMetrics
                    handledTimestamp = true;
                    timeToChange = mesg.getFieldLongValue(MESG_TIMESTAMP);
                    if (timeToChange != null) {
                        mesg.setFieldValue(MESG_TIMESTAMP, timeToChange + changeSeconds);
                    }
                    timeToChange = mesg.getFieldLongValue(16); // Start of activity
                    if (timeToChange != null) {
                        mesg.setFieldValue(16, timeToChange + changeSeconds);
                    }
                    break;
                case MesgNum.DEVICE_INFO:
                    handledTimestamp = true;
                    timeToChange = mesg.getFieldLongValue(DINFO_TIME);
                    if (timeToChange != null) {
                        mesg.setFieldValue(DINFO_TIME, timeToChange + changeSeconds);
                    }
                    break;
                case MesgNum.EVENT:
                    handledTimestamp = true;
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
                    handledTimestamp = true;
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
                    handledTimestamp = true;
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
                    handledTimestamp = true;
                    timeToChange = mesg.getFieldLongValue(SPL_STIME);
                    if (timeToChange != null) {
                        mesg.setFieldValue(SPL_STIME, timeToChange + changeSeconds);
                    }
                    timeToChange = mesg.getFieldLongValue(SPL_ETIME);
                    if (timeToChange != null) {
                        mesg.setFieldValue(SPL_ETIME, timeToChange + changeSeconds);
                    }
                    break;
                case MesgNum.TIME_IN_ZONE:
                    handledTimestamp = true;
                    timeToChange = mesg.getFieldLongValue(TIZ_TIME);
                    if (timeToChange != null) {
                        mesg.setFieldValue(TIZ_TIME, timeToChange + changeSeconds);
                    }
                    break;
                case MesgNum.RECORD:
                    handledTimestamp = true;
                    timeToChange = mesg.getFieldLongValue(REC_TIME);
                    if (timeToChange != null) {
                        mesg.setFieldValue(REC_TIME, timeToChange + changeSeconds);
                    }
                    break;
                case MesgNum.TIMESTAMP_CORRELATION:
                    handledTimestamp = true;
                    timeToChange = mesg.getFieldLongValue(TC_TIME);
                    if (timeToChange != null) {
                        mesg.setFieldValue(TC_TIME, timeToChange + changeSeconds);
                    }
                    timeToChange = mesg.getFieldLongValue(TC_STIME);
                    if (timeToChange != null) {
                        mesg.setFieldValue(TC_STIME, timeToChange + changeSeconds);
                    }
                    timeToChange = mesg.getFieldLongValue(TC_LTIME);
                    if (timeToChange != null) {
                        mesg.setFieldValue(TC_LTIME, timeToChange + changeSeconds);
                    }
                    break;
                default:
                    break;
                
            }
            if (!handledTimestamp) {
                timeToChange = mesg.getFieldLongValue(MESG_TIMESTAMP);
                if (timeToChange != null) {
                    mesg.setFieldValue(MESG_TIMESTAMP, timeToChange + changeSeconds);
                }
            }
        }
        setTimeFirstRecord(recordMesg.get(0).getFieldLongValue(REC_TIME));
        setTimeLastRecord(recordMesg.get(recordMesg.size() - 1).getFieldLongValue(REC_TIME));

        if (!activityMesg.isEmpty()) {
            setActivityDateTimeLocal(activityMesg.get(0).getFieldLongValue(ACT_LOCTIME));
        }
        //setChangedStartTimeBySec(getChangedStartTimeBySec() + changeSeconds);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void changeActivityTimeUTCandLocalToFirstTimeRecordDiff() {
        Long timeToChange;
        Long newTime = 0L;
        clearTempUpdateLog();
        appendTempUpdateLogLn("--------------------------------------------------");
        appendTempUpdateLogLn("Changing ACTIVITY time to first record time:");
        appendTempUpdateLogLn("--------------------------------------------------");
        for (Mesg mesg : allMesg) {
            switch (mesg.getNum()) {
                case MesgNum.ACTIVITY:
                    timeToChange = mesg.getFieldLongValue(ACT_TIME);
                    if (timeToChange != null) {
                        newTime = timeToChange + getTimeFirstRecord() - timeToChange;
                        appendTempUpdateLogLn("Changing Activity UTC time:   "
                         + FitDateTime.toString(timeToChange) + " to "
                         + FitDateTime.toString(newTime));
                        mesg.setFieldValue(ACT_TIME, newTime);
                        setActivityDateTimeUTC(newTime);
                        setActivityDateTimeUTC(newTime);
                    }
                    timeToChange = mesg.getFieldLongValue(ACT_LOCTIME);
                    if (timeToChange != null) {
                        appendTempUpdateLogLn("Changing Activity Local time: "
                         + FitDateTime.toString(timeToChange) + " to "
                         + FitDateTime.toString(newTime));
                        mesg.setFieldValue(ACT_LOCTIME, newTime);
                        setActivityDateTimeLocal(newTime);
                        setActivityDateTimeLocalOrg(newTime);
                        appendTempUpdateLogLn("--------------------------------------------------");
                    }
                    break;
            }
        }
        setDiffMinutesLocalUTC((getActivityDateTimeLocal() - getActivityDateTimeUTC()) / 60);
        System.out.print(getTempUpdateLog());
        appendUpdateLog(getTempUpdateLog());
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void changeActivityTimeUTCToDateString(String dateTimeStr) {
        Long timeToChange;
        Long newTime = 0L;
        clearTempUpdateLog();
        appendTempUpdateLogLn("--------------------------------------------------");
        appendTempUpdateLogLn("Changing ACTIVITY UTC time to DateTime string:");
        appendTempUpdateLogLn("--------------------------------------------------");
        for (Mesg mesg : allMesg) {
            switch (mesg.getNum()) {
                case MesgNum.ACTIVITY:
                    timeToChange = mesg.getFieldLongValue(ACT_TIME);
                    if (timeToChange != null) {
                        newTime = FitDateTime.parseFitDateTime(dateTimeStr);
                        appendTempUpdateLogLn("Changing Activity UTC time:   "
                         + FitDateTime.toString(timeToChange) + " to "
                         + FitDateTime.toString(newTime));
                        mesg.setFieldValue(ACT_TIME, newTime);
                        setActivityDateTimeUTC(newTime);
                        appendTempUpdateLogLn("--------------------------------------------------");
                    }
                    break;
            }
        }
        setDiffMinutesLocalUTC((getActivityDateTimeLocal() - getActivityDateTimeUTC()) / 60);
        System.out.print(getTempUpdateLog());
        appendUpdateLog(getTempUpdateLog());
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void changeActivityTimeLocalToDateString(String dateTimeStr) {
        Long timeToChange;
        Long newTime = 0L;
        clearTempUpdateLog();
        appendTempUpdateLogLn("--------------------------------------------------");
        appendTempUpdateLogLn("Changing ACTIVITY LOCAL time to DateTime string:");
        appendTempUpdateLogLn("--------------------------------------------------");
        for (Mesg mesg : allMesg) {
            switch (mesg.getNum()) {
                case MesgNum.ACTIVITY:
                    timeToChange = mesg.getFieldLongValue(ACT_LOCTIME);
                    if (timeToChange != null) {
                        newTime = FitDateTime.parseFitDateTime(dateTimeStr);
                        appendTempUpdateLogLn("Changing Activity Local Time: "
                         + FitDateTime.toString(timeToChange) + " to "
                         + FitDateTime.toString(newTime));
                        mesg.setFieldValue(ACT_LOCTIME, newTime);
                        setActivityDateTimeLocal(newTime);
                        setActivityDateTimeLocalOrg(newTime);
                        appendTempUpdateLogLn("--------------------------------------------------");
                    }
                    break;
            }
        }
        setDiffMinutesLocalUTC((getActivityDateTimeLocal() - getActivityDateTimeUTC()) / 60);
        System.out.print(getTempUpdateLog());
        appendUpdateLog(getTempUpdateLog());
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void changeSessionTimeToFirstTimeRecordDiff() {
        Long timeToChange;
        Long diffFirstRecordTime = 0L;
        clearTempUpdateLog();
        appendTempUpdateLogLn("--------------------------------------------------");
        appendTempUpdateLogLn("Changing SESSION time to first record time:");
        appendTempUpdateLogLn("--------------------------------------------------");
        for (Mesg mesg : allMesg) {
            switch (mesg.getNum()) {
                case MesgNum.SESSION:
                    timeToChange = mesg.getFieldLongValue(SES_TIME);
                    if (timeToChange != null) {
                        diffFirstRecordTime = getTimeFirstRecord() - timeToChange;
                        appendTempUpdateLogLn("Changing SessionTimeUTC:   "
                         + FitDateTime.toString(timeToChange) + " to "
                         + FitDateTime.toString(timeToChange + diffFirstRecordTime));
                        mesg.setFieldValue(SES_TIME, timeToChange + diffFirstRecordTime);
                        appendTempUpdateLogLn("--------------------------------------------------");
                    }
                    break;
            }
        }
        System.out.print(getTempUpdateLog());
        appendUpdateLog(getTempUpdateLog());
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void changeSessionStartTimeToFirstTimeRecordDiff() {
        Long timeToChange;
        Long diffFirstRecordTime = 0L;
        clearTempUpdateLog();
        appendTempUpdateLogLn("--------------------------------------------------");
        appendTempUpdateLogLn("Changing SESSION START time to first record time:");
        appendTempUpdateLogLn("--------------------------------------------------");
        for (Mesg mesg : allMesg) {
            switch (mesg.getNum()) {
                case MesgNum.SESSION:
                    timeToChange = mesg.getFieldLongValue(SES_STIME);
                    if (timeToChange != null) {
                        diffFirstRecordTime = getTimeFirstRecord() - timeToChange;
                        appendTempUpdateLogLn("Changing SessionStartTimeUTC:   "
                         + FitDateTime.toString(timeToChange) + " to "
                         + FitDateTime.toString(timeToChange + diffFirstRecordTime));
                        mesg.setFieldValue(SES_TIME, timeToChange + diffFirstRecordTime);
                        appendTempUpdateLogLn("--------------------------------------------------");
                    }
                    break;
            }
        }
        System.out.print(getTempUpdateLog());
        appendUpdateLog(getTempUpdateLog());
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String printActivitySessionTimes() {
        Long timeToChange;
        clearTempUpdateLog();

        appendTempUpdateLogLn("==================================================");
        appendTempUpdateLogLn("Activity times saved when reading file:");
        appendTempUpdateLogLn("--------------------------------------------------");
        appendTempUpdateLogLn(" Activity dateTime UTC:  " + FitDateTime.toString(getActivityDateTimeUTC()));
        appendTempUpdateLogLn(" Activity dateTime Local:" + FitDateTime.toString(getActivityDateTimeLocal()));
        appendTempUpdateLogLn(" First record time UTC:  " + FitDateTime.toString(getTimeFirstRecord()));
        appendTempUpdateLogLn(" Last record time UTC:   " + FitDateTime.toString(getTimeLastRecord()));
        appendTempUpdateLogLn("--------------------------------------------------");
        appendTempUpdateLogLn("Activity times again now:");
        appendTempUpdateLogLn("--------------------------------------------------");
        for (Mesg mesg : allMesg) {
            switch (mesg.getNum()) {
                case MesgNum.ACTIVITY:
                    timeToChange = mesg.getFieldLongValue(ACT_TIME);
                    if (timeToChange != null) {
                        appendTempUpdateLogLn(" ActivityUTC:            " + FitDateTime.toString(timeToChange));
                    }
                    timeToChange = mesg.getFieldLongValue(ACT_LOCTIME);
                    if (timeToChange != null) {
                        appendTempUpdateLogLn(" ActivityLocal:          " + FitDateTime.toString(timeToChange));
                    }
                    break;
                case MesgNum.SESSION:
                    timeToChange = mesg.getFieldLongValue(SES_TIME);
                    if (timeToChange != null) {
                        appendTempUpdateLogLn(" Session:                " + FitDateTime.toString(timeToChange));
                    }
                    timeToChange = mesg.getFieldLongValue(SES_STIME);
                    if (timeToChange != null) {
                        appendTempUpdateLogLn(" SessionStart:           " + FitDateTime.toString(timeToChange));
                    }
                    break;
                case MesgNum.FILE_ID:
                    timeToChange = mesg.getFieldLongValue(FID_CTIME);
                    if (timeToChange != null) {
                        appendTempUpdateLogLn(" FileId:                 " + FitDateTime.toString(timeToChange));
                    }
                    break;
            }
        }
            
        appendTempUpdateLogLn("--------------------------------------------------");

        System.out.println(getTempUpdateLog());
        return (getTempUpdateLog());
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String printActivityTimes() {
        Long timeToChange;
        Long diffFirstRecordTime = 0L;
        
        clearTempUpdateLog();

        appendTempUpdateLogLn("==================================================");
        appendTempUpdateLogLn("RECORD times saved when reading file:");
        appendTempUpdateLogLn("--------------------------------------------------");
        appendTempUpdateLogLn(" First record time UTC: " + FitDateTime.toString(getTimeFirstRecord()));
        appendTempUpdateLogLn(" Last record time UTC : " + FitDateTime.toString(getTimeLastRecord()));
        appendTempUpdateLogLn("--------------------------------------------------");
        appendTempUpdateLogLn("ACTIVITY times:");
        appendTempUpdateLogLn("--------------------------------------------------");
        for (Mesg mesg : allMesg) {
            switch (mesg.getNum()) {
                case MesgNum.ACTIVITY:
                    timeToChange = mesg.getFieldLongValue(ACT_TIME);
                    if (timeToChange != null) {
                        appendTempUpdateLogLn(" ACTIVITY TIME UTC    : " + FitDateTime.toString(timeToChange));
                        diffFirstRecordTime = getTimeFirstRecord() - timeToChange;
                        appendTempUpdateLogLn(" First record DIFF    : " + 
                            new Hmmss(diffFirstRecordTime).get());
                        appendTempUpdateLogLn(" Proposed new time    : " + FitDateTime.toString(timeToChange + diffFirstRecordTime));
                        appendTempUpdateLogLn("--------------------------------------------------");
                    }
                    timeToChange = mesg.getFieldLongValue(ACT_LOCTIME);
                    if (timeToChange != null) {
                        appendTempUpdateLogLn(" ACTIVITY TIME LOCAL  : " + FitDateTime.toString(timeToChange));
                        appendTempUpdateLogLn(" First record DIFF    : " + 
                            new Hmmss(diffFirstRecordTime).get());
                        appendTempUpdateLogLn(" Proposed new time    : " + FitDateTime.toString(timeToChange + diffFirstRecordTime));
                        appendTempUpdateLogLn("--------------------------------------------------");
                    }
                    break;
            }
        }
            

        System.out.println(getTempUpdateLog());
        return (getTempUpdateLog());
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String printSessionTimes() {
        Long timeToChange;
        Long diffFirstRecordSessionTime = 0L;
        
        clearTempUpdateLog();

        appendTempUpdateLogLn("==================================================");
        appendTempUpdateLogLn("RECORD times saved when reading file:");
        appendTempUpdateLogLn("--------------------------------------------------");
        appendTempUpdateLogLn(" First record time UTC: " + FitDateTime.toString(getTimeFirstRecord()));
        appendTempUpdateLogLn(" Last record time UTC : " + FitDateTime.toString(getTimeLastRecord()));
        appendTempUpdateLogLn("--------------------------------------------------");
        appendTempUpdateLogLn("SESSION times:");
        appendTempUpdateLogLn("--------------------------------------------------");
        for (Mesg mesg : allMesg) {
            switch (mesg.getNum()) {
                case MesgNum.SESSION:
                    timeToChange = mesg.getFieldLongValue(SES_TIME);
                    if (timeToChange != null) {
                        appendTempUpdateLogLn(" Session time UTC     : " + FitDateTime.toString(timeToChange));
                        diffFirstRecordSessionTime = getTimeFirstRecord() - timeToChange;
                        appendTempUpdateLogLn(" First record DIFF    : " + 
                            new Hmmss(diffFirstRecordSessionTime).get());
                        appendTempUpdateLogLn(" Proposed new time    : " + FitDateTime.toString(timeToChange + diffFirstRecordSessionTime));
                        appendTempUpdateLogLn("--------------------------------------------------");
                    }
                    break;
            }
        }
            

        System.out.println(getTempUpdateLog());
        return (getTempUpdateLog());
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String printSessionStartTimes() {
        Long timeToChange;
        Long diffFirstRecordSessionTime = 0L;
        
        clearTempUpdateLog();

        appendTempUpdateLogLn("==================================================");
        appendTempUpdateLogLn("RECORD times saved when reading file:");
        appendTempUpdateLogLn("--------------------------------------------------");
        appendTempUpdateLogLn(" First record time UTC: " + FitDateTime.toString(getTimeFirstRecord()));
        appendTempUpdateLogLn(" Last record time UTC : " + FitDateTime.toString(getTimeLastRecord()));
        appendTempUpdateLogLn("--------------------------------------------------");
        appendTempUpdateLogLn("SESSION START times:");
        appendTempUpdateLogLn("--------------------------------------------------");
        for (Mesg mesg : allMesg) {
            switch (mesg.getNum()) {
                case MesgNum.SESSION:
                    timeToChange = mesg.getFieldLongValue(SES_STIME);
                    if (timeToChange != null) {
                        appendTempUpdateLogLn(" Session START UTC    : " + FitDateTime.toString(timeToChange));
                        diffFirstRecordSessionTime = getTimeFirstRecord() - timeToChange;
                        appendTempUpdateLogLn(" First record DIFF    : " + 
                            new Hmmss(diffFirstRecordSessionTime).get());
                        appendTempUpdateLogLn(" Proposed new time    : " + FitDateTime.toString(timeToChange + diffFirstRecordSessionTime));
                        appendTempUpdateLogLn("--------------------------------------------------");
                    }
                    break;
            }
        }
            

        System.out.println(getTempUpdateLog());
        return (getTempUpdateLog());
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
        int summaryCount = 0;
        Float maxSummaryDistance = null;
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
            if (startTime != null) System.out.print(" Time:" + FitDateTime.toStringTime(startTime, diffMinutesLocalUTC));

            Long endTime = mesg.getFieldLongValue(SPL_ETIME);
            if (endTime != null) System.out.print("->" + FitDateTime.toStringTime(endTime, diffMinutesLocalUTC));

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

            Integer startElev = mesg.getFieldIntegerValue(SPL_START_ELEVATION);
            if (startElev != null) System.out.print(" StartEle: " + startElev + "m");

            Float movingTime = mesg.getFieldFloatValue(SPL_TOTAL_MOVING_TIME);
            if (movingTime != null) System.out.print(" MovingTime: " + PehoUtils.sec2minSecShort(movingTime));

            System.out.println();
            i++;
        }
        System.out.println("---- END SPLITS ---------");
        System.out.println();
        System.out.println("-------------------------");
        System.out.println("---- SPLIT SUMMARIES ----");
        for (Mesg mesg : allMesg) {
            if (mesg.getNum() != MesgNum.SPLIT_SUMMARY) {
                continue;
            }

            summaryCount++;
            System.out.print("SummaryNo:" + summaryCount);

            Short splitSummaryType = mesg.getFieldShortValue(SPLSUM_TYPE);
            if (splitSummaryType != null) {
                System.out.print(" Type:" + SplitType.getByValue(splitSummaryType));
            }

            Float totalTimer = mesg.getFieldFloatValue(SPLSUM_TIMER);
            if (totalTimer != null)  System.out.print(" SplTime:" + PehoUtils.sec2minSecLong(totalTimer) + "min");

            Float summaryDistance = mesg.getFieldFloatValue(SPLSUM_DIST);
            if (summaryDistance != null) {
                System.out.print(" TotalDist:" + PehoUtils.m2km2(summaryDistance) + "km");
                if (maxSummaryDistance == null || summaryDistance > maxSummaryDistance) {
                    maxSummaryDistance = summaryDistance;
                }
            }

            Float summaryAvgSpeed = mesg.getFieldFloatValue(SPLSUM_SPEED);
            if (summaryAvgSpeed != null) {
                System.out.print(" AvgSpeed:" + String.format("%.2f", summaryAvgSpeed) + "m/s");
            }

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
        }

        if (maxSummaryDistance != null) {
            System.out.println("Max split distance: " + PehoUtils.m2km2(maxSummaryDistance) + "km");
        }
        System.out.println("---- END SPLITS ----");
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
    // public void printLapAvgMaxSpeed (Float avgSpeed, Float maxSpeed) {
    //     if (avgSpeed != null) {
    //         if (isSkiErgFile()) {
    //             System.out.print("--Sp avg:" + PehoUtils.mps2minp500m(avgSpeed));
    //             System.out.print(" max:" + PehoUtils.mps2minp500m(maxSpeed));
    //         } else {
    //             System.out.print("--Sp avg:" + PehoUtils.mps2minpkm(avgSpeed));
    //             System.out.print(" max:" + PehoUtils.mps2minpkm(maxSpeed));
    //         }
    //     }
    // }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // public void printLapAvgSpeed (Float avgSpeed) {
    //     if (avgSpeed != null) {
    //         if (isSkiErgFile()) {
    //             System.out.print(" " + PehoUtils.mps2minp500m(avgSpeed) + "min/500m");
    //         } else {
    //             System.out.print(" " + PehoUtils.mps2minpkm(avgSpeed) + "min/km");
    //             System.out.print(" " + PehoUtils.mps2kmph1(avgSpeed) + "km/h");
    //         }
    //     }
    // }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // public String lapAvgSpeed (Float avgSpeed) {
    //     String tempString = "";
    //     if (avgSpeed != null) {
    //         if (isSkiErgFile()) {
    //             tempString += " " + PehoUtils.mps2minp500m(avgSpeed) + "min/500m";
    //         } else {
    //             tempString += " " + PehoUtils.mps2minpkm(avgSpeed) + "min/km";
    //             tempString += " " + PehoUtils.mps2kmph1(avgSpeed) + "km/h";
    //         }
    //     }
    //     return tempString;
    // }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // public String lapEndSum2String (Float avgCad, Float avgSpeed, Float avgPower, Float dist) {
    //     String tempString = "";
    //     tempString += "avgCad: " + (int) Math.round(avgCad) + "spm";
    //     if (isSkiErgFile()) {
    //         tempString += ", avgPace: " + PehoUtils.mps2minp500m(avgSpeed) + "min/500m";
    //         tempString += ", avgPow: " + (int) Math.round(avgPower) + "W";
    //     } else {
    //         tempString += ", avgPace: " + PehoUtils.mps2minpkm(avgSpeed) + "min/km";
    //         tempString += String.format(", avgSp: %.1fkm/h", avgSpeed * 3.60);
    //     }
    //     tempString += String.format(", sumDist: %.1fkm", + dist / 1000);
    //     tempString += System.lineSeparator();
    //     return tempString;
    // }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
/*     public void printLapAllSummary() {
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

+           // Extra fields: level and step length (skip if SkiErg)
            // if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
            //     System.out.print(" lv" + lapExtraRecords.get(i).level.intValue());
            // }
            // if (lapExtraRecords.get(i).stepLen != null && !isSkiErgFile()) {
            //     System.out.print(" steplen" + (int) (lapExtraRecords.get(i).stepLen * 100) + "cm");
            // }

            // Total timer
            Float totalTimer = mesg.getFieldFloatValue(LAP_TIMER);
            if (totalTimer != null) System.out.print(" LapTime: " + totalTimer);

            // Intensity
            Short intensityVal = (Short) mesg.getFieldValue(LAP_INTENSITY);
            String intensity = intensityVal != null ? Intensity.getStringFromValue(Intensity.getByValue(intensityVal)) : "UNKNOWN";

            System.out.print(" WktIntensity: " + intensity);

            // Heart rate logic
            // Integer maxHr = mesg.getFieldIntegerValue(LAP_MAX_HR);
            // if ("ACTIVE".equals(intensityLabel) || "WARMUP".equals(intensityLabel)) {
            //     System.out.print(" HR start:" + lapExtraRecords.get(i).hrStart);
            //     System.out.print(" min:" + lapExtraRecords.get(i).hrMin);
            //     System.out.print("+" + ((maxHr != null ? maxHr : 0) - lapExtraRecords.get(i).hrMin));
            //     System.out.print("-->max:" + (maxHr != null ? maxHr : "N/A"));
            //     System.out.print(" end:" + lapExtraRecords.get(i).hrEnd);
            // } else {
            //     System.out.print(" HR start:" + lapExtraRecords.get(i).hrStart);
            //     System.out.print(" max:" + (maxHr != null ? maxHr : "N/A"));
            //     System.out.print("" + (lapExtraRecords.get(i).hrMin - (maxHr != null ? maxHr : 0)));
            //     System.out.print("-->min:" + lapExtraRecords.get(i).hrMin);
            //     System.out.print(" end:" + lapExtraRecords.get(i).hrEnd);
            // }

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

            // Extra lap info: Drag Factor and Stroke Length
            // if (lapExtraRecords.get(i).avgDragFactor != null) {
            //     System.out.print("--DFavg:" + (int) Math.round(lapExtraRecords.get(i).avgDragFactor));
            //     System.out.print(" max:" + (int) Math.round(lapExtraRecords.get(i).maxDragFactor));
            // }
            // if (lapExtraRecords.get(i).avgStrokeLen != null) {
            //     System.out.print("--SLavg:" + lapExtraRecords.get(i).avgStrokeLen);
            //     System.out.print(" max:" + lapExtraRecords.get(i).maxStrokeLen);
            // }

            System.out.println();
            i++;
            lapNo++;
        }
    }
 */    
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
/*     public void printLapLongSummery() {
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

                System.out.println();
            }
            i++;
            lapNo++;
        }
    }
 */
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
/*     public String createLapSummery() {
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
 */
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String saveFileInfoBefore() {
        clearSavedFileInfoBefore();
        appendSavedFileInfoBeforeLn("==================================================");
        appendSavedFileInfoBeforeLn("Original file info BEFORE update:");
        appendSavedFileInfoBeforeLn("==================================================");
        appendSavedFileInfoBefore(printFileSummary(false));
        appendSavedFileInfoBefore(getLapReportGenerator().printActiveRestLapSummeryWithPrintOption(false));
        appendSavedFileInfoBeforeLn("==================================================");
        return getSavedFileInfoBefore() ;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String saveFileInfoAfter() {
        appendSavedFileInfoAfterLn("==================================================");
        appendSavedFileInfoAfterLn("New file info AFTER update:");
        appendSavedFileInfoAfterLn("==================================================");
        appendSavedFileInfoAfter(printFileSummary(false));
        appendSavedFileInfoAfter(getLapReportGenerator().printActiveRestLapSummeryWithPrintOption(false));
        appendSavedFileInfoAfterLn("==================================================");
        return getSavedFileInfoAfter();
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String printFileSummary() {
        return printFileSummary(true);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String printFileSummary(Boolean printToConsoleAndSaveToLog) {
        clearTempUpdateLog();
        appendTempUpdateLogLn("--------------------------------------------------");
        appendTempUpdateLogLn(" --> Manufacturer:" + getManufacturer()
         + ", " + getProduct() + "(" + getProductNo() + ")"
          + ", SW: v" + getSwVer());
        appendTempUpdateLogLn(" --> Sport:" + getSport()
         + ", SubSport:" + getSubsport()
          + ", SportProfile:" + getSportProfile()
           + ", WktName:" + getWktName());
        appendTempUpdateLogLn(" --> Org activity dateTime Local:" + FitDateTime.toString(getActivityDateTimeLocalOrg()));
        appendTempUpdateLogLn(" --> New activity dateTime Local:" + FitDateTime.toString(getActivityDateTimeLocal()));
        appendTempUpdateLogLn(" --> Org activity DateTime UTC:  " + FitDateTime.toString(getActivityDateTimeUTC()));
        appendTempUpdateLogLn(" --> timeZone:                   "
         + FitDateTime.offsetToTimeZoneString(getDiffMinutesLocalUTC()));
        appendTempUpdateLogLn(" --> Org start datetime UTC:     " + FitDateTime.toString(getTimeFirstRecordOrg()));
        appendTempUpdateLogLn(" --> New start datetime UTC:     " + FitDateTime.toString(getTimeFirstRecord()));
        
        appendTempUpdateLogLn("--------------------------------------------------");

        if (printToConsoleAndSaveToLog) {
            appendUpdateLog(getTempUpdateLog());
            System.out.println(getTempUpdateLog());
        }
        return getTempUpdateLog();
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printActivityTimeInfo() {
        System.out.println("================================================");
        System.out.println("Activity time info:");
        System.out.println("================================================");
        printFileSummary();
        printSessionInfo();
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printDetailedFileInfo() {
        System.out.println("================================================");
        System.out.println("Detailed file info:");
        System.out.println("================================================");
        printFileSummary();
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
        getLapReportGenerator().printLapReport1();
        getLapReportGenerator().printLapAllSummary();
        getLapReportGenerator().printLapLongSummery();
        getLapReportGenerator().printActiveRestLapSummery();
        printSecRecords0();
        printSecRecords();
        printSessionInfo();

        //printLapRecords();
        //printSecRecords();
        //printLapRecords0();
        //printLapAllSummery();
        //printLapLongSummery();
        //printCourse();
        //printDevDataId();
        //printFieldDescr();

        System.out.println("================================================");
    }
    
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void saveChanges (Conf conf) {

        boolean encodeWorkoutRecords = true;
        String outputFilePath = "";

        setActivityNameSuffix(conf.getProfileNameSuffix());

        System.out.println("Profile from beginning: '" + sportProfile + "'");

        // CREATE OUTPUT FILENAME BASE W/O DATETIME
        String watchFilenameBaseStr = new MergedFileBaseStr(
            new ProfileStr(getSportProfile(), 
                getSport(), 
                getSubsport()
                ).get(),
            new WorkoutStr(getWktName()).get(),
            new DistStr(getTotalDistance()).get(),
            new TimeStr(getTotalTimerTime()).get(),
            new ProductStr(getManufacturerNo(), 
                getProductNo(), 
                getSwVer()
                ).get(),
            getActivityNameSuffix()
            ).get();

            // CREATE *ORG* OUTPUT FILENAME BASE WITH DATETIME
            String watchFilenameWithOrgTime = ""
                + new DTstr(getActivityDateTimeLocalOrg()).get()
                + (watchFilenameBaseStr != null && !watchFilenameBaseStr.isEmpty() ? "-" + watchFilenameBaseStr : "")
                ;

            String watchFilePathWithOrgTime = ""
                + conf.getFilePathPrefix() 
                + new SanitizedFilename(watchFilenameWithOrgTime).get();

            // CREATE *NEW* OUTPUT FILENAME BASE WITH DATETIME
            String watchFilenameWithNewTime = ""
                + new DTstr(getActivityDateTimeLocal()).get()
                + (watchFilenameBaseStr != null && !watchFilenameBaseStr.isEmpty() ? "-" + watchFilenameBaseStr : "")
                ;
            String watchFilePathWithNewTime = ""
                + conf.getFilePathPrefix() 
                + new SanitizedFilename(watchFilenameWithNewTime).get();

            if (isCourseFile()) {
                String courseFilenamePrefix = createCourseOutputPrefix(conf.getInputFilePath());
                watchFilePathWithOrgTime = conf.getFilePathPrefix() + courseFilenamePrefix;
                watchFilePathWithNewTime = conf.getFilePathPrefix() + courseFilenamePrefix;
            }
                
            System.out.println("---> Output watch filename base org time: " + watchFilePathWithOrgTime);
            System.out.println("---> Output watch filename base new time: " + watchFilePathWithNewTime);

        String newActivityName = new MergedProfileStr(
            new ProfileStr(getSportProfile(), 
                getSport(), 
                getSubsport()
                ).get(),
            new WorkoutStr(getWktName()).get(),
            new DistStr(getTotalDistance()).get(),
            getActivityNameSuffix()
            ).get();

        setSportProfile(newActivityName);

        saveFileInfoAfter();

        outputFilePath = watchFilePathWithNewTime + "-fixed" + (conf.getTimeOffsetSec()/60) + "min.fit";
        
        encodeNewFit(outputFilePath, encodeWorkoutRecords);
        
        PehoUtils.renameFile(conf.getInputFilePath(), watchFilePathWithOrgTime + "-org.fit");
        
        //createFileSummary();

        try {
            FileWriter myWriter = new FileWriter(watchFilePathWithNewTime + "-before.txt");
            myWriter.write(getSavedFileInfoBefore());
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred saving before file.");
            e.printStackTrace();
        }
        try {
            FileWriter myWriter = new FileWriter(watchFilePathWithNewTime + "-log.txt");
            myWriter.write(getUpdateLog());
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred saving log file.");
            e.printStackTrace();
        }
        try {
            FileWriter myWriter = new FileWriter(watchFilePathWithNewTime + "-after.txt");
            myWriter.write(getSavedFileInfoAfter());
            // myWriter.write(savedStrLapsActiveInfoShort);
            // myWriter.write(savedStrLapsRestInfoShort);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred saving after file.");
            e.printStackTrace();
        }
        
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private boolean isCourseFile() {
        for (Mesg mesg : allMesg) {
            if (mesg.getNum() == MesgNum.COURSE) {
                return true;
            }
        }
        return false;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private String createCourseOutputPrefix(String inputFilePath) {
        String baseName = getCourseName();
        if (baseName == null || baseName.isBlank()) {
            String inputFilename = new File(inputFilePath).getName();
            int dotIx = inputFilename.lastIndexOf('.');
            baseName = dotIx > 0 ? inputFilename.substring(0, dotIx) : inputFilename;
        }
        String nowDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
        return SanitizedFilename.get(baseName + "_now." + nowDateTime);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private String getCourseName() {
        for (Mesg mesg : allMesg) {
            if (mesg.getNum() != MesgNum.COURSE) {
                continue;
            }

            String courseName = mesg.getFieldStringValue(CourseMesg.NameFieldNum);
            if (courseName != null && !courseName.isBlank()) {
                return courseName;
            }
        }
        return null;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void appendIfNotNull(StringBuilder sb, String label, Object value) {
        if (value != null) sb.append(label).append(value);
    }

    public void appendIfBothNotNull(StringBuilder sb, String label1, Object val1, String label2, Object val2) {
        if (val1 != null) sb.append(label1).append(val1);
        if (val2 != null) sb.append(label2).append(val2);
    }

    public int safeInt(Number n) {
        return n == null ? 0 : n.intValue();
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
    }*/
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printRecordMesg (int ix1, int ix2) {
        for (int i=ix1; i<=ix2; i++) {
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
        for (Mesg record : recordMesg) {
            i++;
            if (i<31 || i>numberOfRecords-40 || i>3012 && i<3020) {
                System.out.print("Record:" + i);
                if (record.getFieldLongValue(REC_TIME) != null) {
                    System.out.print(" Timestamp: " + record.getFieldLongValue(REC_TIME));
                }
                //if (lapRecords.get(0).getStartTime() != null) {
                //    System.out.print(" LapStartTime: " + lapRecords.get(0).getStartTime());
                //}
                if (record.getFieldIntegerValue(REC_HR) != null) {
                    System.out.print(" HR: " + record.getFieldIntegerValue(REC_HR));
                }
                if (record.getFieldShortValue(REC_CAD) != null) {
                    System.out.print(" Cad: " + record.getFieldShortValue(REC_CAD));
                }
                if (record.getFieldFloatValue(REC_SPEED) != null) {
                    System.out.print(" Speed: " + record.getFieldFloatValue(REC_SPEED));
                }
                if (record.getFieldFloatValue(REC_DIST) != null) {
                    System.out.print(" Dist: " + record.getFieldFloatValue(REC_DIST));
                }
                if (record.getFieldIntegerValue(REC_LAT) != null && record.getFieldIntegerValue(REC_LON) != null) {
                    System.out.print(" Position: (" + record.getFieldIntegerValue(REC_LAT) + ", " + record.getFieldIntegerValue(REC_LON) + ")");
                }
                //Iterable<DeveloperField> devFields = new ArrayList<>();
                //devFields = record.getDeveloperFields();
                //List<Field> allFields = new ArrayList<>();
                //allFields = record.fields();
                //Iterable devFields2 = record.getDeveloperFields();
                //System.out.println(" GETVALUE2: " + allFields.get(0).getStringValue());
                System.out.print(" DEV:");
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
        for (Mesg record : recordMesg) {
            if (i<31 || i>numberOfRecords-40 || i>3012 && i<3020) {
                System.out.print("Record:" + i);
                if (record.getFieldLongValue(REC_TIME) != null) {
                    System.out.print(" Timestamp: " + record.getFieldLongValue(REC_TIME));
                }
                // if (lapRecords.get(0).getStartTime() != null) {
                //     System.out.print(" LapStartTime: " + lapRecords.get(12).getStartTime());
                // }
                if (record.getFieldIntegerValue(REC_HR) != null) {
                    System.out.print(" Heart Rate: " + record.getFieldIntegerValue(REC_HR));
                }
                if (record.getFieldFloatValue(REC_SPEED) != null) {
                    System.out.print(" Speed: " + record.getFieldFloatValue(REC_SPEED));
                }
                if (record.getFieldFloatValue(REC_ESPEED) != null) {
                    System.out.print(" EnhSp: " + record.getFieldFloatValue(REC_ESPEED));
                }
                if (record.getFieldFloatValue(REC_DIST) != null) {
                    System.out.print(" Dist: " + record.getFieldFloatValue(REC_DIST));
                }
                if (record.getFieldShortValue(REC_CAD) != null) {
                    System.out.print(" Cad: " + record.getFieldShortValue(REC_CAD));
                }
                if (record.getFieldIntegerValue(REC_POW) != null) {
                    System.out.print(" Pow: " + record.getFieldIntegerValue(REC_POW));
                }
                // if (secExtraRecords.get(i).C2DateTime != null) {
                //     System.out.print(" C2time: " + secExtraRecords.get(i).C2DateTime);
                // }
                if (record.getFieldIntegerValue(REC_LAT) != null && record.getFieldIntegerValue(REC_LON) != null) {
                    System.out.print(" Position: (" + record.getFieldIntegerValue(REC_LAT) + ", " + record.getFieldIntegerValue(REC_LON) + ")");
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
                if (secExtraRecords.get(i).getC2DateTime() != null) {
                    sb.append(" C2time:").append(secExtraRecords.get(i).getC2DateTime());
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
}