package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.ActivityReportGenerator;
import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class ActivityMesgCSVCommand implements Command {
    @Override
    public String getKey() { return "ac"; }

    @Override
    public String getDescription() { return "Write all mesg to CSV"; }

    @Override
    public String getCategory() { return "Activity"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        new ActivityReportGenerator(watchFitFile).writeMesgList();
    }
}
