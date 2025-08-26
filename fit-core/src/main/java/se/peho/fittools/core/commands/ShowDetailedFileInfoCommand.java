package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class ShowDetailedFileInfoCommand implements Command {
    @Override
    public String getKey() { return "info"; }

    @Override
    public String getDescription() { return "Print detailed info about file"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        watchFitFile.printDetailedFileInfo();
    }
}
