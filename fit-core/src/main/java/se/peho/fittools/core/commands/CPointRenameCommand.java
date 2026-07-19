package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class CPointRenameCommand implements Command {
    @Override
    public String getKey() { return "cpn"; }

    @Override
    public String getDescription() { return "Change one Course point name"; }

    @Override
    public String getCategory() { return "Course points"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        watchFitFile.getCPointFix().changeCPointName(sc);
    }
}