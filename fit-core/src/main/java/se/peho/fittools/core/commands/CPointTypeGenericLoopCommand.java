package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class CPointTypeGenericLoopCommand implements Command {
    @Override
    public String getKey() { return "cptg"; }

    @Override
    public String getDescription() { return "Change Generic Course point types (loop)"; }

    @Override
    public String getCategory() { return "Course points"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        watchFitFile.getCPointFix().changeGenericCPointTypes(sc);
    }
}