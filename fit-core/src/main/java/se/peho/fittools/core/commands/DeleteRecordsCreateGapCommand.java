package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class DeleteRecordsCreateGapCommand implements Command {
    
    @Override
    public String getKey() { return "del"; }

    @Override
    public String getDescription() { return "Delete records and create GAP"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        Long startGapTimer = InputHelper.askForTimer("Enter timer value for FIRST record that are going to be deleted", sc);
        if (startGapTimer == null) return;
        Long stopGapTimer = InputHelper.askForTimer("Enter timer value for LAST record that are going to be deleted", sc);
        if (stopGapTimer == null) return;
        if (stopGapTimer <= startGapTimer) {
            System.out.println("==XX> Last timer value must be larger than first timer value. Returning to main menu.");
            return;
        }
        watchFitFile.deleteRecordsCreateGap(startGapTimer, stopGapTimer);

        //watchFitFile.createTimerList();
        //watchFitFile.createPauseList();
        watchFitFile.createGapList();
        watchFitFile.printGapList("", 0);
    }
}
