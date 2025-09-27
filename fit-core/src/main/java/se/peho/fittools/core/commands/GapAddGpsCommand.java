package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class GapAddGpsCommand implements Command {
    @Override
    public String getKey() { return "a"; }

    @Override
    public String getDescription() { return "Add GPS points in gap"; }

    @Override
    public String getCategory() { return "Gaps"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        Integer gapNo = InputHelper.askForNumber("Enter gap number", sc);
        if (gapNo == null) return;
        double[] coords = InputHelper.askForCoords("Enter GPS point", sc);
        if (coords == null) return;
        watchFitFile.addRecordInGap(gapNo, coords);

        watchFitFile.createTimerList();
        watchFitFile.createPauseList();
        watchFitFile.createGapList();
        watchFitFile.printGapList("",0);
    }
}
