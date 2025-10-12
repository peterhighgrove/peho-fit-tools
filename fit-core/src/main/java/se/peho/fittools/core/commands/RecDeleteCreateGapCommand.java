package se.peho.fittools.core.commands;

import java.util.List;
import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitDateTime;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class RecDeleteCreateGapCommand implements Command {
    
    @Override
    public String getKey() { return "recd"; }

    @Override
    public String getDescription() { return "Delete records and create GAP"; }

    @Override
    public String getCategory() { return "Records"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        System.out.println();
        System.out.println("Last timer value is: " + FitDateTime.toTimerString(watchFitFile.getLastTimerInTimerList()));

        while (true) { 

            // Input of start timer value
            System.out.println();
            Long startGapTimer = InputHelper.askForTimer("Enter timer value for FIRST record that are going to be deleted", sc);
            if (startGapTimer == null) return;
            if (startGapTimer <= 0) {
                System.out.println("==XX> Timer values must be positive. Enter a new timer value.");
                continue;
            }
            if (startGapTimer >= watchFitFile.getLastTimerInTimerList()) {
                System.out.println("==XX> Timer values must be within range of existing records. Enter a new timer value.");
                continue;
            }

            // Input of stop timer value
            Long stopGapTimer = InputHelper.askForTimer("Enter timer value for LAST record that are going to be deleted", sc);
            if (stopGapTimer == null) return;
            if (stopGapTimer <= 0) {
                System.out.println("==XX> Timer values must be positive. Enter new timer values.");
                continue;
            }
            if (stopGapTimer >= watchFitFile.getLastTimerInTimerList()) {
                System.out.println("==XX> Timer values must be within range of existing records. Enter new timer values.");
                continue;
            }

            // Check if stop is larger than start
            if (stopGapTimer <= startGapTimer) {
                System.out.println("==XX> Last timer value must be larger than first timer value. Enter new timer values.");
                continue;
            }

            // Check if there is at least one record between start and stop
            if ((watchFitFile.countRecordsBetweenTimerValues(startGapTimer, stopGapTimer)) < 1) {
                System.out.println("==XX> It must at least be one data record between start and stop. Enter new timer values.");
                continue;
            }

            if (watchFitFile.checkForLapStartsBetweenTimerValues(startGapTimer, stopGapTimer)) {
                return;
            }

            if (watchFitFile.checkForPausesAndGivePrintedResultBasedOnTimer(startGapTimer, stopGapTimer)) {
                return;
            }

            watchFitFile.clearTempUpdateLogg();
            watchFitFile.appendTempUpdateLogg("-------------------------" + System.lineSeparator());
            watchFitFile.appendTempUpdateLogg("Deleting records to create a GAP" + System.lineSeparator());
            watchFitFile.appendTempUpdateLogg("-------------------------" + System.lineSeparator());

            watchFitFile.deleteRecordsCreateGap(startGapTimer, stopGapTimer);

            watchFitFile.createTimerList();
            watchFitFile.createPauseList();
            watchFitFile.createGapList();
            watchFitFile.printGapList("", 0);
            return;
        }
    }
}
