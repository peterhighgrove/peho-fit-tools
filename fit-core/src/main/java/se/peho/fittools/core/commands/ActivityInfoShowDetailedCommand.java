package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class ActivityInfoShowDetailedCommand implements Command {
    @Override
    public String getKey() { return "i"; }

    @Override
    public String getDescription() { return "Print detailed info about activity"; }

    @Override
    public String getCategory() { return "Activity"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        watchFitFile.printDetailedFileInfo();
    }
}
