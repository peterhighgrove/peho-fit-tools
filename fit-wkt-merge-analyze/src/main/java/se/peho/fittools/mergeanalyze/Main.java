package se.peho.fittools.mergeanalyze;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import se.peho.fittools.core.Conf;
import se.peho.fittools.core.FitFileForIndoor;
import se.peho.fittools.core.PehoUtils;
import se.peho.fittools.core.TextLapFile;
import se.peho.fittools.core.strings.*;
 
public class Main {

    public static void main(String[] args) {
        SimpleDateFormat sweDateTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        System.out.println(" ========================= START OF PROGRAM fit-wkt-merge-analyze / mergeanalyze ===========================" + sweDateTime.format(Calendar.getInstance().getTime()));
        
        boolean hasC2Fit = false;
        boolean hasManualLapsTxt = false;
        boolean encodeWorkoutRecords = true;
        String outputFilePath = "";
        
        // Reading CONF FILE
        Conf conf = new Conf(args);

        System.out.println("ArgsLen: " + args.length);

        // ================================
        // START
        // ================================

        FitFileForIndoor watchFitFile = new FitFileForIndoor(conf.getC2SyncSecondsC2File(), conf.getC2SyncSecondsLapDistCalc());
        FitFileForIndoor c2FitFile = new FitFileForIndoor ();
        watchFitFile.setDebugFlags(conf);
        c2FitFile.setDebugFlags(conf);

        watchFitFile.setActivityNameSuffix(conf.getProfileNameSuffix());

        // READING FIT FILE
        watchFitFile.readFitFile (conf.getInputFilePath());

        watchFitFile.createFileSummaryIndoor();
        //watchFitFile.printFileIdInfo();
        //watchFitFile.printDeviceInfo();
        if (conf.isDebugWkt()) watchFitFile.printWktInfo();
        if (conf.isDebugWkt()) watchFitFile.printWktSessionInfo();
        if (conf.isDebugWkt()) watchFitFile.printWktStepInfo();
        //watchFitFile.printSessionInfo();
        if (conf.isDebugDevFields()) watchFitFile.printDevDataId();
        if (conf.isDebugDevFields()) watchFitFile.printFieldDescr();
        //watchFitFile.printCourse();
        if (conf.isDebugLaps()) watchFitFile.getLapReportGenerator().printLapReport1();
        //watchFitFile.printSecRecords0();
        //watchFitFile.printSecRecords();
        if (conf.isDebugLaps()) watchFitFile.getLapReportGenerator().debugLapRecords(watchFitFile.getLapMesg(), watchFitFile.getRecordMesg());
        //watchFitFile.printSplitSummary();

        // STOP program after print methods if INFO COMMAND
        if (conf.getCommand().toLowerCase().equals("info")) {
            System.exit(0);
        }
        
        // Changes STARTTIME
        watchFitFile.changeStartTime(conf.getTimeOffsetSec());
        
        // Fix WKT STEPS if WKT COMMAND
        if (conf.getCommand().toLowerCase().equals("wkt")) {
            watchFitFile.wktAddSteps(conf.getStartWithWktStep(), conf.getNewWktName());
        }
        
        // Fix PAUSES MODE
        if (conf.getCommand().toLowerCase().equals("fixpauses")) {
            
        // Analyze and Merge WORKOUT FIT Files
        } else {
            
            // ================================
            // ELLIPTICAL
            // ================================
            if (watchFitFile.getMySport() == FitFileForIndoor.MySport.ELLIPTICAL) {
                System.out.println("======== isElliptical YES ==========");

                hasManualLapsTxt = watchFitFile.hasManualLapsFile(conf.getExtraFilename());
                if (hasManualLapsTxt) {
                    System.out.println("======== HAS MANUAL FILE ==========");
                    TextLapFile manualLapsFile = new TextLapFile ();

                    watchFitFile.initLapExtraRecords();
                    manualLapsFile.parseTextLapFile(conf.getExtraFilename());
                    watchFitFile.fixEmptyBeginningElliptical();

                    //manualLapsFile.printToConsole ();

                    if (manualLapsFile.isNotNumberOfLapsEqual(watchFitFile)) {
                        System.exit(0);
                    }

                    //manualLapsFile.mergeLapDataInFitFile(watchFitFile);
                    watchFitFile.mergeLapDataFromTextFile(manualLapsFile);
                    watchFitFile.calcLapSumFromRecordMesgElliptical();
                    watchFitFile.calcLapDataFromRecordMesgElliptical();
                    watchFitFile.setNewSportElliptical();
                }
                else {
                    System.out.println("======== ELLIPTICAL, BUT NO manualLapsFile ==========");
                    System.exit(0);
                }
            }

            // ================================
            // SKIERG
            // ================================
            else if (watchFitFile.getMySport() == FitFileForIndoor.MySport.SKIERG) {

                System.out.println("======== isSkiErgFile YES ==========");

                hasC2Fit = watchFitFile.hasC2FitFile(conf.getExtraFilename());
                if (hasC2Fit) {
                    watchFitFile.initLapExtraRecords();

                    System.out.println("======== HAS C2 FITFILE ==========");

                    c2FitFile.readFitFile (conf.getExtraFilename());
                    c2FitFile.changeStartTime((int) (watchFitFile.getActivityDateTimeLocalOrg() - c2FitFile.getActivityDateTimeLocalOrg()));
                    
                    c2FitFile.createFileSummaryIndoor();
                    
                    if (conf.isDebugLaps()) c2FitFile.getLapReportGenerator().printLapReport1();
                    //c2FitFile.printSecRecords0();

                    //watchFitFile.addDevFieldDescr();

                    watchFitFile.mergeC2CiqAndFitData(c2FitFile, conf.getC2FitFileDistanceStartCorrection());
                    //watchFitFile.printSecRecords();
                    
                    if (!conf.getUseManualC2SyncSeconds().toLowerCase().equals("yes")) {
                        // Use AUTO SYNC
                        watchFitFile.SyncDataInTimeFromSkiErg(conf.getUseManualC2SyncSeconds(), hasC2Fit);
                    }
                    
                    watchFitFile.calcLapDataFromSecRecordsSkiErg();
                    watchFitFile.setNewSportSkiErg();
                    //watchFitFile.changeDeveloper(watchFitFile);
                    watchFitFile.removeDevFieldDescr();
                    
                    //watchFitFile.addDeveloperfieldsSkiErg();;

                } else {
                    watchFitFile.initLapExtraRecords();

                    System.out.println("======== NO C2 FITFILE FOUND, USE DEV DATA ONLY ==========");
                    System.out.println("======== NEED C2 FITFILE UNTIL THIS METHOD IS FIXED watchFitFile.mergeCiqAndFitData() ==========");
                    System.out.println("======== NEED C2 FITFILE ==========");
                    System.out.println("======== NEED C2 FITFILE ==========");
                    // THIS METHOD NEED TO BE FIXED -------> watchFitFile.mergeCiqAndFitData();
                    //watchFitFile.printSecRecords();

                    if (!conf.getUseManualC2SyncSeconds().toLowerCase().equals("yes")) {
                        watchFitFile.SyncDataInTimeFromSkiErg(conf.getUseManualC2SyncSeconds(), hasC2Fit);
                    }
                    
                    watchFitFile.calcLapDataFromSecRecordsSkiErg();
                    watchFitFile.setNewSportSkiErg();
                    //watchFitFile.changeDeveloper(watchFitFile);
                    watchFitFile.removeDevFieldDescr();
                    
                    //watchFitFile.addDeveloperfieldsSkiErg();;
                }
                watchFitFile.calcSplitRecordsBasedOnLaps();
                watchFitFile.calcSplitSummaryBasedOnSplits();
            }

            // ================================
            // TREADMILL
            // ================================
            else if (watchFitFile.getMySport() == FitFileForIndoor.MySport.TREADMILL) {
                System.out.println("======== isTreadmillFile YES ==========");

                hasManualLapsTxt = watchFitFile.hasManualLapsFile(conf.getExtraFilename());
                if (hasManualLapsTxt) {
                    System.out.println("======== HAS MANUAL FILE ==========");
                    TextLapFile manualLapsFile = new TextLapFile ();

                    watchFitFile.initLapExtraRecords();
                    manualLapsFile.parseTextLapFile(conf.getExtraFilename());
                    watchFitFile.fixEmptyBeginningTreadmill();

                    //manualLapsFile.printToConsole ();

                    if (manualLapsFile.isNotNumberOfLapsEqual(watchFitFile)) {
                        System.exit(0);
                    }

                    //manualLapsFile.mergeLapDataInFitFile(watchFitFile);
                    watchFitFile.mergeLapDataFromTextFile(manualLapsFile);
                    watchFitFile.calcLapSumFromRecordMesgElliptical();
                    watchFitFile.calcLapDataFromRecordMesgElliptical();
                    // NO NEW SportProfile watchFitFile.setNewSportElliptical();
                }
                else {
                    System.out.println("======== TREADMILL, BUT NO manualLapsFile ==========");
                    System.exit(0);
                }
            }
            else {
                System.out.println("======== NO MATCHING SPORT PROFILE ==========");
                System.exit(0);
            }
            
            // ================================
            // END
            // ================================
        }

        // CREATE OUTPUT FILENAME BASE W/O DATETIME
        String watchFilenameBaseStr = new MergedFileBaseStr(
            new ProfileStr(watchFitFile.getSportProfile(), 
                watchFitFile.getSport(), 
                watchFitFile.getSubsport()
                ).get(),
            new WorkoutStr(watchFitFile.getWktName()).get(),
            new DistStr(watchFitFile.getTotalDistance()).get(),
            new TimeStr(watchFitFile.getTotalTimerTime()).get(),
            new ProductStr(watchFitFile.getManufacturerNo(), 
                watchFitFile.getProductNo(), 
                watchFitFile.getSwVer()
                ).get(),
            watchFitFile.getActivityNameSuffix()
            ).get();

        // CREATE OUTPUT *ORG* FILENAME BASE WITH DATETIME
        String watchFilenameWithOrgTime = ""
            + new DTstr(watchFitFile.getActivityDateTimeLocalOrg()).get()
            + (watchFilenameBaseStr != null && !watchFilenameBaseStr.isEmpty() ? "-" + watchFilenameBaseStr : "")
            ;
        String watchFilePathWithOrgTime = ""
            + conf.getFilePathPrefix() 
            + new SanitizedFilename(watchFilenameWithOrgTime).get();

        // CREATE OUTPUT *NEW* FILENAME BASE WITH DATETIME
        String watchFilenameWithNewTime = ""
            + new DTstr(watchFitFile.getActivityDateTimeLocal()).get()
            + (watchFilenameBaseStr != null && !watchFilenameBaseStr.isEmpty() ? "-" + watchFilenameBaseStr : "")
            ;
        String watchFilePathWithNewTime = ""
            + conf.getFilePathPrefix() 
            + new SanitizedFilename(watchFilenameWithNewTime).get();
            
        System.out.println("---> Output watch filename base org time: " + watchFilePathWithOrgTime);
        System.out.println("---> Output watch filename base new time: " + watchFilePathWithNewTime);

        String extraFilePathWithTime = "";
        if (hasC2Fit) {
            String extraFilenameBaseStr = new MergedFileBaseStr(
                new ProfileStr(c2FitFile.getSportProfile(), 
                    c2FitFile.getSport(), 
                    c2FitFile.getSubsport()
                    ).get(),
                new WorkoutStr(c2FitFile.getWktName()).get(),
                new DistStr(c2FitFile.getTotalDistance()).get(),
                new TimeStr(c2FitFile.getTotalTimerTime()).get(),
                new ProductStr(c2FitFile.getManufacturerNo(), 
                    c2FitFile.getProductNo(), 
                    c2FitFile.getSwVer()
                    ).get(),
                c2FitFile.getActivityNameSuffix()
                ).get();

            String extraFilenameWithTime = ""
                + new DTstr(c2FitFile.getActivityDateTimeLocal()).get()
                + (extraFilenameBaseStr != null && !extraFilenameBaseStr.isEmpty() ? "-" + extraFilenameBaseStr : "")
                ;

            extraFilePathWithTime = ""
                 + conf.getFilePathPrefix()
                 + new SanitizedFilename(extraFilenameWithTime).get();

            System.out.println("---> Output C2 filename base new time: " + extraFilePathWithTime);
        }

        String newActivityName = new MergedProfileStr(
            new ProfileStr(watchFitFile.getSportProfile(), 
                watchFitFile.getSport(), 
                watchFitFile.getSubsport()
                ).get(),
            new WorkoutStr(watchFitFile.getWktName()).get(),
            new DistStr(watchFitFile.getTotalDistance()).get(),
            watchFitFile.getActivityNameSuffix()
            ).get();

        watchFitFile.setSportProfile(newActivityName);

        watchFitFile.encodeIndoorFit(
            watchFilePathWithNewTime + "-merged" + (int)(conf.getTimeOffsetSec()/60) + "min.fit"
            , encodeWorkoutRecords);
        

        PehoUtils.renameFile(conf.getInputFilePath(), watchFilePathWithOrgTime + "-org.fit");
        
        if (hasC2Fit) {
            PehoUtils.renameFile(conf.getExtraFilename(), extraFilePathWithTime + "-org.fit");
        }

        if (hasManualLapsTxt) {
            PehoUtils.renameFile(conf.getExtraFilename(), watchFilePathWithOrgTime + "-manualLaps.txt");
        }

        watchFitFile.createFileSummaryIndoor();
        //watchFitFile.printSecRecords2();
        if (conf.isDebugLaps()) watchFitFile.getLapReportGenerator().printLapRecords();
        if (conf.isDebugSplit()) watchFitFile.printSplitSummary();

        if (conf.isDebugLaps()) watchFitFile.getLapReportGenerator().printLapAllSummary();

        if (conf.isDebugLaps()) watchFitFile.getLapReportGenerator().printLapLongSummery();
        //watchFitFile.printCourse();
        if (conf.isDebugDevFields()) watchFitFile.printDevDataId();
        if (conf.isDebugDevFields()) watchFitFile.printFieldDescr();
        if (conf.isDebugLaps()) watchFitFile.getLapReportGenerator().debugLapRecords(watchFitFile.getLapMesg(), watchFitFile.getRecordMesg());
        if (conf.isDebugWkt()) watchFitFile.printWktInfo();
        if (conf.isDebugWkt()) watchFitFile.printWktSessionInfo();
        if (conf.isDebugWkt()) watchFitFile.printWktStepInfo();

        System.out.print(watchFitFile.savedStrOrgFileInfo);
        watchFitFile.getLapReportGenerator().printActiveRestLapSummery();
        watchFitFile.saveLapSummery(watchFilePathWithNewTime + "-merged" + (int)(conf.getTimeOffsetSec()/60) + "min-laps.txt");
        //watchFitFile.getLapReportGenerator().printLapReport1();

    }

}


