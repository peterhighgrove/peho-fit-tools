package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class LapShowListIntervalCommand implements Command {
    @Override
    public String getKey() { return "lapi"; }

    @Override
    public String getDescription() { return "Show LAP Interval list, createActiveRestLapSummery"; }

    @Override
    public String getCategory() { return "Laps"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {

        watchFitFile.getLapReportGenerator().printActiveRestLapSummery();
    }
}
