package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class ChangeStartTimeCommand implements Command {
    @Override
    public String getKey() { return "c"; }

    @Override
    public String getDescription() { return "Change start time of all records in file."; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        Integer changeMinutes = InputHelper.askForNumber("Enter number of minutes to add to starttime (- for substract)", sc);
        if (changeMinutes == null) return;
        watchFitFile.createTimerList();
        watchFitFile.changeStartTime(changeMinutes * 60);
    }
}
