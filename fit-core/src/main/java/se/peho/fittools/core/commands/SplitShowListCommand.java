package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class SplitShowListCommand implements Command {
    @Override
    public String getKey() { return "spl"; }

    @Override
    public String getDescription() { return "Show SPLIT list"; }

    @Override
    public String getCategory() { return "Splits"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {

        watchFitFile.printSplitSummary();
    }
}
