package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitDateTime;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class DeleteRecordsCreateGapCommand implements Command {
    
    @Override
    public String getKey() { return "del"; }

    @Override
    public String getDescription() { return "Delete records and create GAP"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        System.out.println("Last timer value is: " + FitDateTime.toTimerString(watchFitFile.getLastTimerInTimerList()));

        // Input of start timer value
        Long startGapTimer = InputHelper.askForTimer("Enter timer value for FIRST record that are going to be deleted", sc);
        if (startGapTimer == null) return;
        if (startGapTimer <= 0) {
            System.out.println("==XX> Timer values must be positive. Returning to main menu.");
            return;
        }
        if (startGapTimer >= watchFitFile.getLastTimerInTimerList()) {
            System.out.println("==XX> Timer values must be within range of existing records. Returning to main menu.");
            return;
        }

        // Input of stop timer value
        Long stopGapTimer = InputHelper.askForTimer("Enter timer value for LAST record that are going to be deleted", sc);
        if (stopGapTimer == null) return;
        if (stopGapTimer <= 0) {
            System.out.println("==XX> Timer values must be positive. Returning to main menu.");
            return;
        }
        if (stopGapTimer >= watchFitFile.getLastTimerInTimerList()) {
            System.out.println("==XX> Timer values must be within range of existing records. Returning to main menu.");
            return;
        }

        // Check if stop is larger than start
        if (stopGapTimer <= startGapTimer) {
            System.out.println("==XX> Last timer value must be larger than first timer value. Returning to main menu.");
            return;
        }

        // Check if there is at least one record between start and stop
        if ((watchFitFile.countRecordsBetweenTimerValues(startGapTimer, stopGapTimer)) < 1) {
            System.out.println("==XX> It must at least be one data record between start and stop. Returning to main menu.");
            return;
        }

        watchFitFile.deleteRecordsCreateGap(startGapTimer, stopGapTimer);

        watchFitFile.createTimerList();
        watchFitFile.createPauseList();
        watchFitFile.createGapList();
        watchFitFile.printGapList("", 0);
    }
}
