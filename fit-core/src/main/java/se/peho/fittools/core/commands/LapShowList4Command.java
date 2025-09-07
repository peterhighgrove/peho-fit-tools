package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class LapShowList4Command implements Command {
    @Override
    public String getKey() { return "lap4"; }

    @Override
    public String getDescription() { return "Show LAP list, LapAllSummary"; }

    @Override
    public String getCategory() { return "Laps"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {

        watchFitFile.printLapAllSummary();
    }
}
