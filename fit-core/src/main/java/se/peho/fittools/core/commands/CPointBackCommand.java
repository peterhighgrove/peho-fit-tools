package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class CPointBackCommand implements Command {
    @Override
    public String getKey() { return "cpb"; }

    @Override
    public String getDescription() { return "Move Course point back on course"; }

    @Override
    public String getCategory() { return "Course points"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        watchFitFile.getCPointFix().moveCPointsBack(sc);
    }
}