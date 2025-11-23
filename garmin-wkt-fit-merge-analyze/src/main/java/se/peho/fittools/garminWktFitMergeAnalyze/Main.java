package se.peho.fittools.garminWktFitMergeAnalyze;

import java.text.SimpleDateFormat;
import java.util.Calendar;


 
public class Main {

    public static void main(String[] args) {
        SimpleDateFormat sweDateTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        System.out.println(" ========================= START OF PROGRAM ===========================" + sweDateTime.format(Calendar.getInstance().getTime()));
        
        
        //int conf.Integer.parseInt( = 1; // 1 = winter time in swe, used for string conv to filename
        boolean hasC2Fit = false;
        boolean hasManualLapsTxt = false;
        boolean encodeWorkoutRecords = true;
        String outputFilePath = "";
        
        // Reading CONF FILE
        Conf conf = new Conf();
        
        System.out.println("ArgsLen: " + args.length);

        if (args.length < 1) {
            System.out.println("============= NO ARGS =============");
            System.out.println("------------- USES ONLY CONF FILE ------");
        }
        if (args.length == 1) {
            System.out.println("============= 1 ARGS ============= ");
            conf.inputFilePath = args[0];
            conf.profileNameSuffix = "";
            conf.timeOffsetSec = 3; // 3 minutes in seconds
        }
        if (args.length == 2) {
            System.out.println("============= 2 ARGS ============= ");
            conf.inputFilePath = args[0];
            conf.profileNameSuffix = args[1];
            conf.timeOffsetSec = 3; // 3 minutes in seconds
        }
        if (args.length == 3) {
            System.out.println("============= 3 ARGS ============= ");
            conf.inputFilePath = args[0];
            conf.profileNameSuffix = args[1];
            conf.timeOffsetSec = (Integer.parseInt(args[2]) * 60); // 3 minutes in seconds
        }
        if (args.length == 4) {
            System.out.println("============= 4 ARGS ============= ");
            conf.inputFilePath = args[0];
            conf.profileNameSuffix = args[1];
            conf.timeOffsetSec = (Integer.parseInt(args[2]) * 60); // 3 minutes in seconds
            conf.extraFilename = args[3];
        }
        if (args.length == 6 || args.length == 7) {
            System.out.println("============= 6-7 ARGS ============= ");
            conf.inputFilePath = args[0];
            conf.profileNameSuffix = args[1];
            conf.timeOffsetSec = (Integer.parseInt(args[2]) * 60); // 3 minutes in seconds
            conf.extraFilename = args[3];

            conf.command = args[4];
            System.out.println("============= " + conf.command + " " + args[5]);

            if (conf.command.toLowerCase().equals("corr")) {
                conf.C2FitFileDistanceStartCorrection = Integer.valueOf(args[5]);

            } else if (conf.command.toLowerCase().equals("wkt")) {
                conf.startWithWktStep = args[5];
                conf.newWktName = args[6];
            }
        } 

        System.out.println("+++++++++++++++++++++++++++++++++++");
        System.out.println("filePathPrefix: "+conf.filePathPrefix);
        System.out.println("profileNameSuffix: "+conf.profileNameSuffix);
        System.out.println("inputFilePath: "+conf.inputFilePath);
        System.out.println("extraFilename: "+conf.extraFilename);
        System.out.println("timeOffsetSec: "+conf.timeOffsetSec);
        System.out.println("command: "+conf.command);
        System.out.println("startWithWktStep: "+conf.startWithWktStep);
        System.out.println("newWktName: "+conf.newWktName);
        System.out.println("C2FitFileDistanceStartCorrection: "+conf.C2FitFileDistanceStartCorrection);
        System.out.println("useManualC2SyncSeconds "+conf.useManualC2SyncSeconds);
        System.out.println("c2SyncSecondsC2File "+conf.c2SyncSecondsC2File);
        System.out.println("c2SyncSecondsLapDistCalc "+conf.c2SyncSecondsLapDistCalc);
        System.out.println("+++++++++++++++++++++++++++++++++++");


        // ================================
        // START
        // ================================

		conf.inputFilePath = conf.filePathPrefix + conf.inputFilePath;
        conf.inputFilePath = FitFile.checkFile(conf.inputFilePath);
        FitFile watchFitFile = new FitFile (conf.c2SyncSecondsC2File, conf.c2SyncSecondsLapDistCalc);
                
        // READING FIT FILE
        watchFitFile.readFitFile (conf.inputFilePath);

        // Changes STARTTIME
        watchFitFile.changeStartTime(conf.timeOffsetSec);
        
        // Fix WKT STEPS if WKT COMMAND
        if (conf.command.toLowerCase().equals("wkt")) {
            watchFitFile.wktAddSteps(conf.startWithWktStep, conf.newWktName);
        }
        if (conf.command.toLowerCase().equals("cad")) {
            watchFitFile.addFixedValueForCadence((short) 75);
        }
        
        watchFitFile.printFileSummary();
        //watchFitFile.printFileIdInfo();
        //watchFitFile.printDeviceInfo();
        //watchFitFile.printWktInfo();
        //watchFitFile.printWktSessionInfo();
        watchFitFile.printWktStepInfo();
        //watchFitFile.printSessionInfo();
        //watchFitFile.printDevFieldDescr();
        //watchFitFile.printDevDataId();
        //watchFitFile.printFieldDescr();
        //watchFitFile.printLapRecords0();
        //watchFitFile.printSecRecords0();

        // ================================
        // ELLIPTICAL
        // ================================
        if (watchFitFile.isEllipticalFile()) {
            System.out.println("======== isElliptical YES ==========");
            if (conf.extraFilename == "") {
                conf.extraFilename = "laps.txt";
            }
            conf.extraFilename = conf.filePathPrefix + conf.extraFilename;

			hasManualLapsTxt = watchFitFile.hasManualLapsFile(conf.extraFilename);
            if (hasManualLapsTxt) {
                System.out.println("======== HAS MANUAL FILE ==========");
                TextLapFile manualLapsFile = new TextLapFile ();

                watchFitFile.initLapExtraRecords();
                manualLapsFile.parseTextLapFile(conf.extraFilename);
                watchFitFile.fixEmptyBeginningElliptical();

                //manualLapsFile.printToConsole ();

                if (manualLapsFile.isNotNumberOfLapsEqual(watchFitFile)) {
                    System.exit(0);
                }

                //manualLapsFile.mergeLapDataInFitFile(watchFitFile);
                watchFitFile.mergeLapDataFromTextFile(manualLapsFile);
                watchFitFile.calcLapDataFromSecRecordsElliptical();
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
            if (conf.extraFilename == "") {
                conf.extraFilename = "c2.fit";
            }
            conf.extraFilename = conf.filePathPrefix + conf.extraFilename;
            conf.extraFilename = FitFile.checkFile(conf.extraFilename);
            hasC2Fit = watchFitFile.hasC2FitFile(conf.extraFilename);
            if (hasC2Fit) {
                watchFitFile.initLapExtraRecords();

                System.out.println("======== HAS C2 FITFILE ==========");
                FitFile c2FitFile = new FitFile ();

                c2FitFile.readFitFile (conf.extraFilename);
                //c2FitFile.changeStartTime(conf.timeOffsetSec);
                c2FitFile.printFileSummary();
                //c2FitFile.printLapRecords0();
                //c2FitFile.printSecRecords0();

                //watchFitFile.addDevFieldDescr();

                watchFitFile.mergeC2CiqAndFitData(c2FitFile, conf.C2FitFileDistanceStartCorrection);
                //watchFitFile.printSecRecords();

                if (!conf.useManualC2SyncSeconds.toLowerCase().equals("yes")) {
                    watchFitFile.SyncDataInTimeFromSkiErg();
                }
                
                watchFitFile.calcLapDataFromSecRecordsSkiErg();
                watchFitFile.setNewSportSkiErg();
                //watchFitFile.changeDeveloper(watchFitFile);
                watchFitFile.removeDevFieldDescr();
                
                //watchFitFile.addDeveloperfieldsSkiErg();;
            }
        }

        // ================================
        // TREADMILL
        // ================================
        else if (watchFitFile.isTreadmillFile()) {
            System.out.println("======== isTreadmillFile YES ==========");
            if (conf.extraFilename == "") {
                conf.extraFilename = "laps.txt";
            }
            conf.extraFilename = conf.filePathPrefix + conf.extraFilename;

            hasManualLapsTxt = watchFitFile.hasManualLapsFile(conf.extraFilename);
            if (hasManualLapsTxt) {
                System.out.println("======== HAS MANUAL FILE ==========");
                TextLapFile manualLapsFile = new TextLapFile ();

                watchFitFile.initLapExtraRecords();
                manualLapsFile.parseTextLapFile(conf.extraFilename);
                watchFitFile.fixEmptyBeginningTreadmill();

                //manualLapsFile.printToConsole ();

                if (manualLapsFile.isNotNumberOfLapsEqual(watchFitFile)) {
                    System.exit(0);
                }

                //manualLapsFile.mergeLapDataInFitFile(watchFitFile);
                watchFitFile.mergeLapDataFromTextFile(manualLapsFile);
                watchFitFile.calcLapDataFromSecRecordsElliptical();
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

        //watchFitFile.renameDevFieldName();
        
        String outputFilenameBase = "";
        outputFilenameBase = watchFitFile.getFilenameAndSetNewSportProfileName(conf.profileNameSuffix, outputFilePath, conf.hoursToAdd);
        outputFilePath = conf.filePathPrefix + outputFilenameBase + "-mergedJava.fit";
        
		watchFitFile.encodeNewFit(outputFilePath, encodeWorkoutRecords);
        
		FitFile.renameFile(conf.inputFilePath, conf.filePathPrefix + outputFilenameBase + "-watch.fit");
		
		if (hasC2Fit) {
			FitFile.renameFile(conf.extraFilename, conf.filePathPrefix + outputFilenameBase + "-c2.fit");
		}

		if (hasManualLapsTxt) {
			FitFile.renameFile(conf.extraFilename, conf.filePathPrefix + outputFilenameBase + "-manualLaps.txt");
		}

        //watchFitFile.printFileSummary();
        //watchFitFile.printLapRecords();
        //watchFitFile.printSecRecords();
        watchFitFile.printLapSummery();
        watchFitFile.writeFileLapSummary(conf.filePathPrefix + outputFilenameBase + "-mergedJava-laps.txt");
        //watchFitFile.printDevFieldDescr();
        //watchFitFile.printDevDataId();
        //watchFitFile.printFieldDescr();
    }
}


