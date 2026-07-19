package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class CPointInsertCharCommand implements Command {
    @Override
    public String getKey() { return "cpi"; }

    @Override
    public String getDescription() { return "Insert type char in Course point names"; }

    @Override
    public String getCategory() { return "Course points"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        watchFitFile.getCPointFix().insertTypeCharInCPointNames();
    }
}