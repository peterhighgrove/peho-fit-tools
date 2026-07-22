package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class CPointTimeToClosestRecordCommand implements Command {
    @Override
    public String getKey() { return "cpct"; }

    @Override
    public String getDescription() { return "Match CPoint time/dist to closest record GPS"; }

    @Override
    public String getCategory() { return "Course points"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        watchFitFile.getCPointFix().syncCPointTimeAndDistanceToClosestRecordByGps();
    }
}