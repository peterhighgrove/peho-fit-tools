package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class PauseShortenCommand implements Command {
    @Override
    public String getKey() { return "s"; }

    @Override
    public String getDescription() { return "Shorten a pause"; }

    @Override
    public String getCategory() { return "Pauses"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        while (true) {
            watchFitFile.printPauseList("", 0);
            System.out.println();
            Integer pauseNo = InputHelper.askForNumber("Enter pause number to modify", sc);
            if (pauseNo == null) return;
            if (pauseNo > watchFitFile.getPauseList().size() || pauseNo < 1) {
                System.out.println("==XX> Pause number must be within range. Enter a new pause number.");
                continue;
            }

            Integer newPauseLen = InputHelper.askForNumber("Enter new pause length", sc);
            if (newPauseLen == null) return;

            watchFitFile.shortenPause(pauseNo, newPauseLen.longValue());

            watchFitFile.createTimerList();
            watchFitFile.createPauseList();
            watchFitFile.createGapList();
            watchFitFile.printGapList("",0);
            break;
        }
    }
}
