package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class PauseShowListFilteredCommand implements Command {
    @Override
    public String getKey() { return "pf"; }

    @Override
    public String getDescription() { return "Show PAUSE list with filter/more details"; }

    @Override
    public String getCategory() { return "Pauses"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        Integer minPauseToShow = InputHelper.askForNumber("Enter min distance (m) for pauses to show", sc);
        if (minPauseToShow == null) return;
        String detailedListCommand = InputHelper.askForString("Enter d for detailed list", sc);
        if (detailedListCommand == null) return;

        watchFitFile.printPauseList(detailedListCommand, minPauseToShow);
    }
}
