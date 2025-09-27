package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class RecStartCommand implements Command {
    @Override
    public String getKey() { return "start"; }

    @Override
    public String getDescription() { return "Add time at start"; }

    @Override
    public String getCategory() { return "Records"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        double[] coords = InputHelper.askForCoords("Enter GPS START coords", sc);
        if (coords == null) return;
        Integer secs = InputHelper.askForNumber("Enter seconds to add", sc);
        if (secs == null) return;
        watchFitFile.addRecordAtStart(secs.longValue(), coords);
        
        watchFitFile.createTimerList();
        watchFitFile.createPauseList();
        watchFitFile.createGapList();
        watchFitFile.printGapList("",0);
    }
}
