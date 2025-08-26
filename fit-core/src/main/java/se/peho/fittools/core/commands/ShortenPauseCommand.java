package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class ShortenPauseCommand implements Command {
    @Override
    public String getKey() { return "s"; }

    @Override
    public String getDescription() { return "Shorten a pause"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        Integer pauseNo = InputHelper.askForNumber("Enter pause number to modify", sc);
        if (pauseNo == null) return;
        Integer newPauseLen = InputHelper.askForNumber("Enter new pause length", sc);
        if (newPauseLen == null) return;
        watchFitFile.shortenPause(pauseNo, newPauseLen.longValue());

        watchFitFile.createPauseList();
        watchFitFile.createGapList();
        watchFitFile.printGapList("",0);
    }
}
