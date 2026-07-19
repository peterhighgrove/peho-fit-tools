package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class CPointCleanNamePrefixesCommand implements Command {
    @Override
    public String getKey() { return "cpc"; }

    @Override
    public String getDescription() { return "Clean Course point name prefixes"; }

    @Override
    public String getCategory() { return "Course points"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        watchFitFile.getCPointFix().cleanCPointNamePrefixes();
    }
}
