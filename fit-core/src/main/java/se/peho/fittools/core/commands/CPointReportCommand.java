package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class CPointReportCommand implements Command {
    @Override
    public String getKey() { return "cpr"; }

    @Override
    public String getDescription() { return "Show Course points report"; }

    @Override
    public String getCategory() { return "Course points"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        watchFitFile.getCPointReportGenerator().printCPoints();
    }
}