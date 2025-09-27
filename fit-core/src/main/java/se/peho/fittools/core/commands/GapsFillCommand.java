package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class GapsFillCommand implements Command {
    @Override
    public String getKey() { return "fill"; }

    @Override
    public String getDescription() { return "Fill gaps with one-second records"; }

    @Override
    public String getCategory() { return "Gaps"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        watchFitFile.fillRecordsInGap();

        watchFitFile.createTimerList();
        watchFitFile.createPauseList();
        watchFitFile.createGapList();
        watchFitFile.printGapList("",0);
    }
}
