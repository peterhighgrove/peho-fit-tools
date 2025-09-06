package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class DeletePauseCommand implements Command {
    @Override
    public String getKey() { return "delp"; }

    @Override
    public String getDescription() { return "Delete a pause"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        while (true) {
            watchFitFile.printPauseList("", 0);
            System.out.println();
            Integer pauseNo = InputHelper.askForNumber("Enter PAUSE number to DELETE", sc);
            if (pauseNo == null) return;

            if (pauseNo > watchFitFile.getPauseList().size() || pauseNo < 1) {
                System.out.println("==XX> Pause number must be within range. Enter a new pause number.");
                continue;
            }

            watchFitFile.deletePause(pauseNo);

            watchFitFile.createTimerList();
            watchFitFile.createPauseList();
            watchFitFile.createGapList();
            watchFitFile.printPauseList("", 0);
            watchFitFile.printGapList("", 0);
            break;
        }
    }
}
