package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class IncreasePauseCommand implements Command {
    @Override
    public String getKey() { return "inc"; }

    @Override
    public String getDescription() { return "Increase a pause"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        Integer pauseNo = InputHelper.askForNumber("Enter pause number to increase", sc);
        if (pauseNo == null) return;
        Integer secs = InputHelper.askForNumber("Enter time (secs) to add", sc);
        if (secs == null) return;
        watchFitFile.increasePause(pauseNo, secs.longValue());

        watchFitFile.createTimerList();
        watchFitFile.createPauseList();
        watchFitFile.printPauseList("", 0);
    }
}
