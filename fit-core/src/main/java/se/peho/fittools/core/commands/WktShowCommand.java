package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class WktShowCommand implements Command {
    @Override
    public String getKey() { return "wkt"; }

    @Override
    public String getDescription() { return "Print detailed info about Workouts"; }

    @Override
    public String getCategory() { return "Info"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        watchFitFile.printWktInfo();
        watchFitFile.printWktSessionInfo();
        watchFitFile.printWktStepInfo();
    }
}
