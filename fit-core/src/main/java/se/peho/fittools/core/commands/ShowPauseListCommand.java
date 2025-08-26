package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class ShowPauseListCommand implements Command {
    @Override
    public String getKey() { return "p"; }

    @Override
    public String getDescription() { return "Show PAUSE list"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {

        watchFitFile.printPauseList("", 0);
    }
}
