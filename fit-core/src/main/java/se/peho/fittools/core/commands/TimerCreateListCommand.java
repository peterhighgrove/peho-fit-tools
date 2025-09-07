package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class TimerCreateListCommand implements Command {
    @Override
    public String getKey() { return "calc"; }

    @Override
    public String getCategory() { return "Timer"; }

    @Override
    public String getDescription() { return "Calc TIMER value for all records"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {

        watchFitFile.createTimerList();
    }
}
