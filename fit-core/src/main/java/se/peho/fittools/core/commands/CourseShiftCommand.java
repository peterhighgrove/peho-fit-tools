package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class CourseShiftCommand implements Command {
    @Override
    public String getKey() { return "csh"; }

    @Override
    public String getDescription() { return "Shift course left/right"; }

    @Override
    public String getCategory() { return "Course points"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        watchFitFile.getCPointFix().shiftGpsPointsSideways(sc);
    }
}