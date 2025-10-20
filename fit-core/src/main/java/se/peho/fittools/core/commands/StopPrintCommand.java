package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class StopPrintCommand implements Command {
    @Override
    public String getKey() { return "lowpr"; }

    @Override
    public String getDescription() { return "Find LOW movement, stopped moving without pause watch, and print a list"; }

    @Override
    public String getCategory() { return "Low Movement"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        while (true) {
            System.out.println();
            Integer stopWindowSize = InputHelper.askForNumber("Enter number of records to look for a with low movement, stop window", sc);
            if (stopWindowSize == null) return;
            if (stopWindowSize > 200 || stopWindowSize < 1) {
                System.out.println("==XX> Low movement window size must be within 1-200. Enter a new low movement window size.");
                continue;
            }

            Integer maxDist = InputHelper.askForNumber("Enter max distance (meters) during low movement window", sc);
            if (maxDist == null) return;

            watchFitFile.printCombinedStopsPausesLowMovement(stopWindowSize, maxDist.floatValue());

            watchFitFile.createTimerList();
            watchFitFile.createPauseList();
            watchFitFile.createGapList();
            break;
        }
    }
}
