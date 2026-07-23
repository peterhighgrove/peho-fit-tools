package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class RecFixCommand implements Command {
    @Override
    public String getKey() { return "recfixtime"; }

    @Override
    public String getDescription() { return "Fix record times issues, fill empty and delete duplicate"; }

    @Override
    public String getCategory() { return "Records"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        watchFitFile.fixNullRecordTimes();

        watchFitFile.createTimerList();
        watchFitFile.createPauseList();
        watchFitFile.createGapList();
    }
}