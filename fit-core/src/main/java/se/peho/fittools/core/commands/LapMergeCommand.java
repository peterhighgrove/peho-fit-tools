package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class LapMergeCommand implements Command {
    
    @Override
    public String getKey() { return "lapm"; }

    @Override
    public String getDescription() { return "Merge Laps"; }

    @Override
    public String getCategory() { return "Laps"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        watchFitFile.printLapRecords();
        System.out.println("Last lap number is: " + watchFitFile.getLapMesg().size());

        while (true) { 

            // Input of start lap number
            System.out.println();
            Integer firstLapToBeMerged = InputHelper.askForNumber("Enter FIRST LAP to be merged", sc);
            if (firstLapToBeMerged == null) return;
            if (firstLapToBeMerged <= 0) {
                System.out.println("==XX> Lap number must be positive. Enter a new lap number.");
                continue;
            }
            if (firstLapToBeMerged >= watchFitFile.getLapMesg().size()) {
                System.out.println("==XX> Lap number must be within range of existing records. Enter a new lap number.");
                continue;
            }

            // Input of stop lap number
            Integer lastLapToBeMerged = InputHelper.askForNumber("Enter LAST LAP to be merged", sc);
            if (lastLapToBeMerged == null) return;
            if (lastLapToBeMerged <= 0) {
                System.out.println("==XX> Lap number must be positive. Enter new lap numbers.");
                continue;
            }
            if (lastLapToBeMerged > watchFitFile.getLapMesg().size()) {
                System.out.println("==XX> Lap number must be within range of existing laps. Enter new lap numbers.");
                continue;
            }

            // Check if stop is larger than start
            if (firstLapToBeMerged >= lastLapToBeMerged) {
                System.out.println("==XX> Last lap number must be larger than first lap number. Enter new lap numbers.");
                continue;
            }

            watchFitFile.mergeLaps(firstLapToBeMerged, lastLapToBeMerged);

            watchFitFile.createTimerList();
            watchFitFile.createPauseList();
            watchFitFile.createGapList();
            watchFitFile.printLapRecords();
            return;
        }
    }
}
