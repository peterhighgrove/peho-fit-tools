package se.peho.fittools.mergeanalyze;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import se.peho.fittools.core.Conf;
import se.peho.fittools.core.FitDateTime;
import se.peho.fittools.core.FitFilePerMesgType;
import se.peho.fittools.core.PehoUtils;
import se.peho.fittools.core.TextLapFile;
 
public class Main {

    public static void main(String[] args) {
        SimpleDateFormat sweDateTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        System.out.println(" ========================= START OF PROGRAM fit-wkt-merge-analyze / mergeanalyze ===========================" + sweDateTime.format(Calendar.getInstance().getTime()));
        
        
        //int conf.Integer.parseInt( = 1; // 1 = winter time in swe, used for string conv to filename
        boolean hasC2Fit = false;
        boolean hasManualLapsTxt = false;
        boolean encodeWorkoutRecords = true;
        String outputFilePath = "";
        
        // Reading CONF FILE
        Conf conf = new Conf(args);
        
        System.out.println("ArgsLen: " + args.length);

        /*if (args.length < 4) {
            System.out.println("============= NO ARGS =============");
            System.out.println("------------- USES ONLY CONF FILE ------");
        }
        if (args.length == 4) {
            System.out.println("============= 1 ARGS ============= ");
            conf.setInputFilePath(args[0]);
            conf.setProfileNameSuffix("");
            conf.setTimeOffsetSec(3); // 3 seconds (if you mean minutes, use 3 * 60)        }
        }
        if (args.length == 5) {
            System.out.println("============= 2 ARGS ============= ");
            conf.setInputFilePath(args[0]);
            conf.setProfileNameSuffix(args[1]);
            conf.setTimeOffsetSec(3); // 3 minutes in seconds (should be 3 * 60 if minutes)
        }

        if (args.length == 6) {
            System.out.println("============= 3 ARGS ============= ");
            conf.setInputFilePath(args[0]);
            conf.setProfileNameSuffix(args[1]);
            conf.setTimeOffsetSec(Integer.parseInt(args[2]) * 60); // 3 minutes in seconds
        }

        if (args.length == 7) {
            System.out.println("============= 4 ARGS ============= ");
            conf.setInputFilePath(args[0]);
            conf.setProfileNameSuffix(args[1]);
            conf.setTimeOffsetSec(Integer.parseInt(args[2]) * 60);
            conf.setExtraFilename(args[3]);
        }

        if (args.length == 9 || args.length == 10) {
            System.out.println("============= 6-7 ARGS ============= ");
            conf.setInputFilePath(args[0]);
            conf.setProfileNameSuffix(args[1]);
            conf.setTimeOffsetSec(Integer.parseInt(args[2]) * 60);
            conf.setExtraFilename(args[3]);

            conf.setCommand(args[4]);
            System.out.println("============= " + conf.getCommand() + " " + args[5]);

            if (conf.getCommand().toLowerCase().equals("corr")) {
                conf.setC2FitFileDistanceStartCorrection(Integer.valueOf(args[5]));

            } else if (conf.getCommand().toLowerCase().equals("wkt")) {
                conf.setStartWithWktStep(args[5]);
                conf.setNewWktName(args[6]);
            }
        }

        /* System.out.println("+++++++++++++++++++++++++++++++++++");
        System.out.println("filePathPrefix: " + conf.getFilePathPrefix());
        System.out.println("profileNameSuffix: " + conf.getProfileNameSuffix());
        System.out.println("inputFilePath: " + conf.getInputFilePath());
        System.out.println("extraFilename: " + conf.getExtraFilename());
        System.out.println("timeOffsetSec: " + conf.getTimeOffsetSec());
        System.out.println("command: " + conf.getCommand());
        System.out.println("startWithWktStep: " + conf.getStartWithWktStep());
        System.out.println("newWktName: " + conf.getNewWktName());
        System.out.println("C2FitFileDistanceStartCorrection: " + conf.getC2FitFileDistanceStartCorrection());
        System.out.println("useManualC2SyncSeconds: " + conf.getUseManualC2SyncSeconds());
        System.out.println("c2SyncSecondsC2File: " + conf.getC2SyncSecondsC2File());
        System.out.println("c2SyncSecondsLapDistCalc: " + conf.getC2SyncSecondsLapDistCalc());
        System.out.println("+++++++++++++++++++++++++++++++++++");

        System.out.println("Input file path: " + conf.getInputFilePath()); */

        // ================================
        // START
        // ================================

        // Use getters/setters instead of direct field access:
        // conf.setInputFilePath(conf.getFilePathPrefix() + conf.getInputFilePath());
        // conf.setInputFilePath(PehoUtils.checkFile(conf.getInputFilePath()));

        /* if (conf.getCommand().toLowerCase().equals("sportprofile")) {
            SportProfileFitFile sportsFile = new SportProfileFitFile();

            // Use getter for filePathPrefix and extraFilename
            sportsFile.readFitFileExtra(conf.getFilePathPrefix() + conf.getExtraFilename());

            sportsFile.mesgSave();
            sportsFile.readFitFile(conf.getInputFilePath());
            sportsFile.mesgInsert();
            sportsFile.mesgPrinter();
            sportsFile.encodeNewFit(conf.getFilePathPrefix() + "SettingsNew.fit");

            System.exit(0);
        } */

        FitFilePerMesgType watchFitFile = new FitFilePerMesgType(conf.getC2SyncSecondsC2File(), conf.getC2SyncSecondsLapDistCalc());

        /* if (conf.getCommand().toLowerCase().equals("fixpauses")) {
            watchFitFile.allMesgFlag = true;
        } */

        // READING FIT FILE
        watchFitFile.readFitFile (conf.getInputFilePath());

        watchFitFile.createFileSummary();
        //watchFitFile.printFileIdInfo();
        //watchFitFile.printDeviceInfo();
        //watchFitFile.printWktInfo();
        watchFitFile.printWktSessionInfo();
        watchFitFile.printWktStepInfo();
        //watchFitFile.printSessionInfo();
        //watchFitFile.printDevDataId();
        //watchFitFile.printFieldDescr();
        //watchFitFile.printDevFieldDescr();
        //watchFitFile.printCourse();
        //watchFitFile.printLapRecords0();
        //watchFitFile.printSecRecords0();
        //watchFitFile.printSecRecords();
        watchFitFile.debugLapRecords(watchFitFile.getLapMesg(), watchFitFile.getRecordMesg());
        watchFitFile.printSplitRecords();
        watchFitFile.printSplitSumRecords();

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
            if (watchFitFile.isEllipticalFile()) {
                System.out.println("======== isElliptical YES ==========");
                if (conf.getExtraFilename().equals("")) {
                    conf.setExtraFilename("laps.txt");
                }
                conf.setExtraFilename(conf.getFilePathPrefix() + conf.getExtraFilename());

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
            else if (watchFitFile.isSkiErgFile()) {
                System.out.println("======== isSkiErgFile YES ==========");
                if (!conf.getExtraFilename().equals("")) {
                    //conf.setExtraFilename("c2.fit");
                    conf.setExtraFilename(conf.getFilePathPrefix() + conf.getExtraFilename());
                    conf.setExtraFilename(PehoUtils.checkFile(conf.getExtraFilename()));
                }
                hasC2Fit = watchFitFile.hasC2FitFile(conf.getExtraFilename());
                if (hasC2Fit) {
                    watchFitFile.initLapExtraRecords();

                    System.out.println("======== HAS C2 FITFILE ==========");
                    FitFilePerMesgType c2FitFile = new FitFilePerMesgType ();

                    c2FitFile.readFitFile (conf.getExtraFilename());
                    //c2FitFile.changeStartTime(conf.timeOffsetSec);
                    c2FitFile.createFileSummary();
                    //c2FitFile.printLapRecords0();
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
            else if (watchFitFile.isTreadmillFile()) {
                System.out.println("======== isTreadmillFile YES ==========");
                if (conf.getExtraFilename().equals("")) {
                    conf.setExtraFilename("laps.txt");
                }
                conf.setExtraFilename(conf.getFilePathPrefix() + conf.getExtraFilename());

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

        //watchFitFile.renameDevFieldName();
        
        String orgDateTime = FitDateTime.toString(watchFitFile.activityDateTimeLocalOrg);
        String newDateTime = FitDateTime.toString(watchFitFile.activityDateTimeLocal);

        String outputFilenameBase = "";
        outputFilenameBase = watchFitFile.getFilenameAndSetNewSportProfileName(conf.getProfileNameSuffix(), outputFilePath);
        outputFilePath = conf.getFilePathPrefix() + newDateTime + outputFilenameBase + "-mergedJava" + (int)(conf.getTimeOffsetSec()/60) + "min.fit";
        
        watchFitFile.encodeNewFit(outputFilePath, encodeWorkoutRecords);
        
        PehoUtils.renameFile(conf.getInputFilePath(), conf.getFilePathPrefix() + orgDateTime + outputFilenameBase + "-watch.fit");
        
        if (hasC2Fit) {
            PehoUtils.renameFile(conf.getExtraFilename(), conf.getFilePathPrefix() + orgDateTime + outputFilenameBase + "-c2.fit");
        }

        if (hasManualLapsTxt) {
            PehoUtils.renameFile(conf.getExtraFilename(), conf.getFilePathPrefix() + orgDateTime + outputFilenameBase + "-manualLaps.txt");
        }

        watchFitFile.createFileSummary();
        //watchFitFile.printLapRecords();
        //watchFitFile.printSecRecords2();
        //watchFitFile.printLapRecords0();
        //watchFitFile.printLapRecords();
        watchFitFile.printSplitRecords();
        watchFitFile.printSplitSumRecords();

        //watchFitFile.printLapAllSummaryAllMesg2();

        //watchFitFile.printLapLongSummery();
        //watchFitFile.printCourse();
        //watchFitFile.printDevFieldDescr();
        //watchFitFile.printDevDataId();
        //watchFitFile.printFieldDescr();
        watchFitFile.debugLapRecords(watchFitFile.getLapMesg(), watchFitFile.getRecordMesg());
        watchFitFile.printWriteLapSummery(conf.getFilePathPrefix() + newDateTime + outputFilenameBase + "-mergedJava" + (int)(conf.getTimeOffsetSec()/60) + "min-laps.txt");
    }

}


