package se.peho.fittools.mergeanalyze;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

import se.peho.fittools.core.Conf;
import se.peho.fittools.core.FitDateTime;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.GeoUtils;
import se.peho.fittools.core.PehoUtils;
import se.peho.fittools.core.SportProfileFitFile;
import se.peho.fittools.core.TextLapFile;
 
public class Main {

    public static void main(String[] args) {
        SimpleDateFormat sweDateTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        System.out.println(" ========================= START OF PROGRAM fi-wkt-merge-analyze / mergeanalyze ===========================" + sweDateTime.format(Calendar.getInstance().getTime()));
        
        
        //int conf.Integer.parseInt( = 1; // 1 = winter time in swe, used for string conv to filename
        boolean hasC2Fit = false;
        boolean hasManualLapsTxt = false;
        boolean encodeWorkoutRecords = true;
        String outputFilePath = "";
        
        // Reading CONF FILE
        Conf conf = new Conf(args);
        
        System.out.println("ArgsLen: " + args.length);

        if (args.length < 4) {
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

        System.out.println("+++++++++++++++++++++++++++++++++++");
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

        System.out.println("Input file path: " + conf.getInputFilePath());

        // ================================
        // START
        // ================================

        // Use getters/setters instead of direct field access:
        conf.setInputFilePath(conf.getFilePathPrefix() + conf.getInputFilePath());
        conf.setInputFilePath(PehoUtils.checkFile(conf.getInputFilePath()));

        if (conf.getCommand().toLowerCase().equals("sportprofile")) {
            SportProfileFitFile sportsFile = new SportProfileFitFile();

            // Use getter for filePathPrefix and extraFilename
            sportsFile.readFitFileExtra(conf.getFilePathPrefix() + conf.getExtraFilename());

            sportsFile.mesgSave();
            sportsFile.readFitFile(conf.getInputFilePath());
            sportsFile.mesgInsert();
            sportsFile.mesgPrinter();
            sportsFile.encodeNewFit(conf.getFilePathPrefix() + "SettingsNew.fit");

            System.exit(0);
        }

        FitFile watchFitFile = new FitFile(conf.getC2SyncSecondsC2File(), conf.getC2SyncSecondsLapDistCalc());

        if (conf.getCommand().toLowerCase().equals("fixpauses")) {
            watchFitFile.allMesgFlag = true;
        }

        // READING FIT FILE
        watchFitFile.readFitFile (conf.getInputFilePath());

        // Changes STARTTIME
        watchFitFile.changeStartTime(conf.getTimeOffsetSec());
        
        // Fix WKT STEPS if WKT COMMAND
        if (conf.getCommand().toLowerCase().equals("wkt")) {
            watchFitFile.wktAddSteps(conf.getStartWithWktStep(), conf.getNewWktName());
        }
        
        watchFitFile.createFileSummary();
        watchFitFile.printFileIdInfo();
        watchFitFile.printDeviceInfo();
        //watchFitFile.printWktInfo();
        //watchFitFile.printWktSessionInfo();
        //watchFitFile.printWktStepInfo();
        //watchFitFile.printSessionInfo();
        //watchFitFile.printDevFieldDescr();
        //watchFitFile.printDevDataId();
        //watchFitFile.printFieldDescr();
        //watchFitFile.printCourse();
        //watchFitFile.printLapRecords0();
        //watchFitFile.printSecRecords0();

        // Fix PAUSES MODE
        if (conf.getCommand().toLowerCase().equals("fixpauses")) {
            //watchFitFile.wktAddSteps(conf.startWithWktStep, conf.newWktName);
            watchFitFile.initLapExtraRecords();

            String listMode = "p"; // p=pause, g=gap, s=stopped -mode
            int listEntryNoToChange = 1;
            Scanner userInputScanner = new Scanner (System.in);
            String listCommandUserInput = null;
            String userInputString;

            long newPauseLen = 10l;

            while (listEntryNoToChange > 0) {
                switch (listMode) {
                    case "p" : {
                        // p=pause, g=gap, s=stopped -mode
                        watchFitFile.createPauseList();
                        watchFitFile.printPauseList(listCommandUserInput);
                        System.out.println("==< PAUSE SHORTEN TIME -- forgot START efter >==");
                        System.out.println("================================================");
                        System.out.print("(d = show details, f = filter on min pause dist)");
                        System.out.println();
                        break;
                    }
                    case "inc" : {
                        // p=pause, g=gap, s=stopped -mode
                        watchFitFile.createPauseList();
                        watchFitFile.printPauseList(listCommandUserInput);
                        System.out.println("==< PAUSE INCREASE TIME -- forgot STOP before >==");
                        System.out.println("=================================================");
                        System.out.print("(d = show details, f = filter on min pause dist)");
                        System.out.println();
                        break;
                    }
                    case "g" : {
                        // p=pause, g=gap, s=stopped -mode
                        watchFitFile.createGapList();
                        watchFitFile.printGapList(listCommandUserInput);
                        System.out.println("==< GAP MODIFICATION >==");
                        System.out.println("===============");
                        System.out.print("(fill = fill gaps with one-sec records)");
                        System.out.println();
                        break;
                    }
                    default : throw new AssertionError();
                }
                listCommandUserInput = null;
                userInputString = null;

                System.out.print("(s = stop-do nothiing,  0 = go on save changes)");
                System.out.println();
                System.out.print("(g = GAP insert gps point or fill)");
                System.out.println();
                System.out.print("(p = PAUSE shorten time, forgot START after)");
                System.out.println();
                System.out.print("(inc = PAUSE INCrease time, forgot STOP before)");
                System.out.println();
                System.out.print("(start = add time in beginnning, forgot to START)");
                System.out.println();
                System.out.print("(enter = show again w/o details)");
                System.out.println();
                System.out.print("Enter LIST ENTRY NUMBER to change (or commands above): ");
                listCommandUserInput = userInputScanner.nextLine();
                System.out.println();

                switch (listCommandUserInput) {
                    case "d" : {
                        break;
                    }
                    case "g" : {
                        listMode = listCommandUserInput;
                        break;
                    }
                    case"p" : {
                        listMode = listCommandUserInput;
                        break;
                    }
                    case"inc" : {
                        listMode = listCommandUserInput;
                        break;
                    }
                    case "f" : {
                        break;
                    }
                    case "s" : {
                        System.out.println("NOTHING DONE! (s command used)");
                        System.out.println("-------------------------------");
                        System.exit(0);
                        break;
                    }
                    case "0" : {
                        listEntryNoToChange = PehoUtils.safeParseInt(listCommandUserInput);
                        break;
                    }
                    case "fill" : {
                        switch (listMode) {
                            case "g" : {
                                watchFitFile.fillRecordsInGap();
                                break;
                            }
                            default : {
                                System.out.println("fill is only used in GAP mode.");
                            }
                        }
                        break;
                    }
                    case "start" : {
                        double[] coords = {0.0, 0.0};
                        while (userInputString == null) {
                            System.out.print("Enter new GPS START point OR s to stop: ");
                            userInputString = userInputScanner.nextLine();
                            System.out.println();
                            if (!userInputString.equals("s")) {
                                try {
                                    coords = GeoUtils.parseCoordinates(userInputString);
                                } catch (Exception e) {
                                    System.out.println("Error parsing '" + userInputString + "': " + e.getMessage());
                                    System.out.println("ENTER NEW GPS String!!!");
                                    listCommandUserInput = null;
                                    userInputString = null;
                                }
                            }
                        }

                        if (!userInputString.equals("s")) {
                            watchFitFile.savedStrOrgFileInfo += "ADD TIME TO START" + System.lineSeparator();
                            watchFitFile.savedStrOrgFileInfo += "   GPS START coords user input: " + userInputString + System.lineSeparator();
                            userInputString = null;
                            while (userInputString == null) {
                                System.out.print("Enter time in seconds ot add at start OR s to stop: ");
                                userInputString = userInputScanner.nextLine();
                                System.out.println();
                                if (!userInputString.equals("s")) {
                                    if (PehoUtils.safeParseInt(userInputString) == null)  {
                                        System.out.println("Not an integer as answer. Redo!");
                                        System.out.println("-------------------------------");
                                        userInputString = null;
                                    } else {
                                        newPauseLen = PehoUtils.safeParseInt(userInputString);
                                        //System.out.println("New pace after pause: "+watchFitFile.mps2minpkm(watchFitFile.pauseRecords.get(listEntryNoToChange-1).distPause/(watchFitFile.pauseRecords.get(listEntryNoToChange-1).timePause-newPauseLen))+"min/km.");
                                        System.out.print("OK? (n=not/enter other=ok)?");
                                        userInputString = userInputScanner.nextLine();
                                        System.out.println();
                                        if (userInputString.equals("n")) {
                                            userInputString = null;
                                        }
                                    }
                                } 

                            }
                            if (!userInputString.equals("s")) {
                                watchFitFile.addRecordAtStart(newPauseLen, coords);
                                listMode = "g";
                            }
                        }
                        break;
                    }
                    default : {
                        // entered a number to change in list
                        if (PehoUtils.safeParseInt(listCommandUserInput) == null)  {
                            // double checking that its a number
                            System.out.println("Not an integer or allowed command as answer. Redo!");
                            System.out.println("-------------------------------");
                            listCommandUserInput = null;
                        } else {
                            // GO INTO ListEntryChange
                            // =======================
                            listEntryNoToChange = PehoUtils.safeParseInt(listCommandUserInput);

                            switch (listMode) {
                                case "p" : {
                                    //if (listEntryNoToChange != 0) {
                                        while (userInputString == null) {
                                            System.out.println("Min pace after pause: "+PehoUtils.mps2minpkm(watchFitFile.pauseRecords.get(listEntryNoToChange-1).distPause/watchFitFile.pauseRecords.get(listEntryNoToChange-1).timePause)+"min/km");
                                            System.out.print("Enter new pause time in seconds: ");
                                            userInputString = userInputScanner.nextLine();
                                            System.out.println();

                                            if (PehoUtils.safeParseInt(userInputString) == null)  {
                                                System.out.println("Not an integer as answer. Redo!");
                                                System.out.println("-------------------------------");
                                                userInputString = null;
                                            } else {
                                                newPauseLen = PehoUtils.safeParseInt(userInputString);
                                                System.out.println("New pace after pause: "+PehoUtils.mps2minpkm(watchFitFile.pauseRecords.get(listEntryNoToChange-1).distPause/(watchFitFile.pauseRecords.get(listEntryNoToChange-1).timePause-newPauseLen))+"min/km.");
                                                System.out.print("OK? (n=not/enter other=ok)?");
                                                userInputString = userInputScanner.nextLine();
                                                System.out.println();
                                                if (userInputString.equals("n")) {
                                                    userInputString = null;
                                                }
                                            }
                                        }
                                        watchFitFile.shortenPause(listEntryNoToChange, newPauseLen);
                                    //} //if
                                    break;
                                }

                                case "inc" : {
                                    //if (listEntryNoToChange != 0) {
                                        while (userInputString == null) {
                                            //System.out.println("Min pace after pause: "+watchFitFile.mps2minpkm(watchFitFile.pauseRecords.get(listEntryNoToChange-1).distPause/watchFitFile.pauseRecords.get(listEntryNoToChange-1).timePause)+"min/km");
                                            System.out.print("Enter time before to put into pause in seconds: ");
                                            userInputString = userInputScanner.nextLine();
                                            System.out.println();

                                            if (PehoUtils.safeParseInt(userInputString) == null)  {
                                                System.out.println("Not an integer as answer. Redo!");
                                                System.out.println("-------------------------------");
                                                userInputString = null;
                                            } else {
                                                newPauseLen = PehoUtils.safeParseInt(userInputString);
                                                //System.out.println("New pace after pause: "+watchFitFile.mps2minpkm(watchFitFile.pauseRecords.get(listEntryNoToChange-1).distPause/(watchFitFile.pauseRecords.get(listEntryNoToChange-1).timePause-newPauseLen))+"min/km.");
                                                System.out.print("OK? (n=not/enter other=ok)?");
                                                userInputString = userInputScanner.nextLine();
                                                System.out.println();
                                                if (userInputString.equals("n")) {
                                                    userInputString = null;
                                                }
                                            }
                                        }
                                        watchFitFile.increasePause(listEntryNoToChange, newPauseLen);
                                    //} //if
                                    break;
                                }

                                case "g" : {
                                    //if (listEntryNoToChange != 0) {
                                    double[] coords = {0.0, 0.0};
                                    while (userInputString == null) {
                                        System.out.println("Dist in gap: "+PehoUtils.m2km2(watchFitFile.gapRecords.get(listEntryNoToChange-1).getDistGap()) + "km");
                                        System.out.println("Time in gap: "+PehoUtils.sec2minSecShort(watchFitFile.gapRecords.get(listEntryNoToChange-1).getTimeGap()) + "min");
                                        System.out.println("Pace in gap: "+PehoUtils.mps2minpkm(watchFitFile.gapRecords.get(listEntryNoToChange-1).getDistGap() / watchFitFile.gapRecords.get(listEntryNoToChange-1).getTimeGap()) + "min/km");
                                        System.out.print("Enter new GPS point OR s to stop: ");
                                        userInputString = userInputScanner.nextLine();
                                        System.out.println();
                                        if (!userInputString.equals("s")) {
                                            try {
                                                coords = GeoUtils.parseCoordinates(userInputString);
                                            } catch (Exception e) {
                                                System.out.println("Error parsing '" + userInputString + "': " + e.getMessage());
                                                System.out.println("ENTER NEW GPS String!!!");
                                                listCommandUserInput = null;
                                                userInputString = null;
                                            }
                                        }
                                    }
                                    //}
                                    if (!userInputString.equals("s")) {
                                        watchFitFile.savedStrOrgFileInfo += "Modifying gap no: " + listEntryNoToChange + System.lineSeparator();
                                        watchFitFile.savedStrOrgFileInfo += "   GPS coords user input: "+ userInputString + System.lineSeparator();
                                        watchFitFile.addRecordInGap(listEntryNoToChange, coords);
                                    }
                                    break;
                                }

                                default : {
                                }
                            } //switch
                        }
                    }


                    }
                // d used as parameter to print method
                // f used as parameter to print method
                            } // listEntryNo > 0
            

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
                if (conf.getExtraFilename().equals("")) {
                    conf.setExtraFilename("c2.fit");
                }
                conf.setExtraFilename(conf.getFilePathPrefix() + conf.getExtraFilename());
                conf.setExtraFilename(PehoUtils.checkFile(conf.getExtraFilename()));
                hasC2Fit = watchFitFile.hasC2FitFile(conf.getExtraFilename());
                if (hasC2Fit) {
                    watchFitFile.initLapExtraRecords();

                    System.out.println("======== HAS C2 FITFILE ==========");
                    FitFile c2FitFile = new FitFile ();

                    c2FitFile.readFitFile (conf.getExtraFilename());
                    //c2FitFile.changeStartTime(conf.timeOffsetSec);
                    c2FitFile.createFileSummary();
                    //c2FitFile.printLapRecords0();
                    //c2FitFile.printSecRecords0();

                    //watchFitFile.addDevFieldDescr();

                    watchFitFile.mergeC2CiqAndFitData(c2FitFile, conf.getC2FitFileDistanceStartCorrection());
                    //watchFitFile.printSecRecords();

                    if (!conf.getUseManualC2SyncSeconds().toLowerCase().equals("yes")) {
                        watchFitFile.SyncDataInTimeFromSkiErg(conf.getUseManualC2SyncSeconds());
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
        //watchFitFile.printSecRecords();
        //watchFitFile.printLapRecords0();
        //watchFitFile.printLapAllSummery();
        //watchFitFile.printLapLongSummery();
        watchFitFile.printWriteLapSummery(conf.getFilePathPrefix() + newDateTime + outputFilenameBase + "-mergedJava" + (int)(conf.getTimeOffsetSec()/60) + "min-laps.txt");
        //watchFitFile.printCourse();
        //watchFitFile.printDevFieldDescr();
        //watchFitFile.printDevDataId();
        //watchFitFile.printFieldDescr();
    }

}


