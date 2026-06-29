package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class LapShowListDebugCommand implements Command {
    @Override
    public String getKey() { return "lapd"; }

    @Override
    public String getDescription() { return "Show LAP Debug list, DebugLapRecords"; }

    @Override
    public String getCategory() { return "Laps"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {

        watchFitFile.getLapReportGenerator().debugLapRecords(watchFitFile.getLapMesg(), watchFitFile.getRecordMesg());
    }
}
