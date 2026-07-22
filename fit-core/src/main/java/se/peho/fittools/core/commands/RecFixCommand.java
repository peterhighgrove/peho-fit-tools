package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class RecFixCommand implements Command {
    @Override
    public String getKey() { return "recfix"; }

    @Override
    public String getDescription() { return "Apply automatic REC_TIME fixes"; }

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