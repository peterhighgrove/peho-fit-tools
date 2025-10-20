package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitDateTime;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class PrintRecordsCommand implements Command {
    
    @Override
    public String getKey() { return "recpr"; }

    @Override
    public String getDescription() { return "Print records between timer values"; }

    @Override
    public String getCategory() { return "Records"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        System.out.println();
        System.out.println("Last timer value is: " + FitDateTime.toTimerString(watchFitFile.getLastTimerInTimerList()));

        while (true) { 

            // Input of start timer value
            System.out.println();
            Long startGapTimer = InputHelper.askForTimer("Enter timer value for FIRST record that are going to be printed", sc);
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
            Long stopGapTimer = InputHelper.askForTimer("Enter timer value for LAST record that are going to be printed", sc);
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

            watchFitFile.printMessagesBetweenTimers(startGapTimer, stopGapTimer);

            watchFitFile.createTimerList();
            watchFitFile.createPauseList();
            watchFitFile.createGapList();
            watchFitFile.printGapList("", 0);
            return;
        }
    }
}
