package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class GapShowListCommand implements Command {
    @Override
    public String getKey() { return "g"; }

    @Override
    public String getDescription() { return "Show GAP list"; }

    @Override
    public String getCategory() { return "Gaps"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {

        watchFitFile.printGapList("",0);
    }
}
