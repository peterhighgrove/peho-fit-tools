package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class ShowGapListFilteredCommand implements Command {
    @Override
    public String getKey() { return "gf"; }

    @Override
    public String getDescription() { return "Show GAP list with filter/more details"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        Integer minGapToShow = InputHelper.askForNumber("Enter min time (sec) for gaps to show", sc);
        if (minGapToShow == null) return;
        String detailedListCommand = InputHelper.askForString("Enter d for detailed list", sc);
        if (detailedListCommand == null) return;

        watchFitFile.createGapList();
        watchFitFile.printGapList(detailedListCommand, minGapToShow);
    }
}
