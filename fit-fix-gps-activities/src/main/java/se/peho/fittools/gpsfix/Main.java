package se.peho.fittools.gpsfix;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import se.peho.fittools.core.Conf;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.MenuRunner;
 
public class Main {

    public static void main(String[] args) {
        SimpleDateFormat sweDateTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        System.out.println(" ========================= START OF PROGRAM fit-fix-gps-activities / gpsfix ===========================" + sweDateTime.format(Calendar.getInstance().getTime()));
        
        
        //int conf.Integer.parseInt( = 1; // 1 = winter time in swe, used for string conv to filename
        // Reading CONF FILE
        
        System.out.println("ArgsLen: " + args.length);

        Conf conf = new Conf(args);
        
        // ================================
        // START
        // ================================

        FitFile watchFitFile = new FitFile();

        // READING FIT FILE
        watchFitFile.readFitFile (conf.getInputFilePath());

        // INTERACTIVE MENU
        MenuRunner menu = new MenuRunner(watchFitFile, conf);
        menu.run();

            /* while (listEntryNoToChange > 0) {
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
             */

            // ================================
            // END
            // ================================
        //}

    }

}


