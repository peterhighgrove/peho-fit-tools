package se.peho.fittools.core.commands;

import java.util.List;
import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class PauseIncreaseCommand implements Command {
    @Override
    public String getKey() { return "pinc"; }

    @Override
    public String getDescription() { return "Increase a pause"; }

    @Override
    public String getCategory() { return "Pauses"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        while (true) {
            watchFitFile.printPauseList("", 0);
            System.out.println();
            Integer pauseNo = InputHelper.askForNumber("Enter pause number to increase", sc);
            if (pauseNo == null) return;
            if (pauseNo > watchFitFile.getPauseList().size() || pauseNo < 1) {
                System.out.println("==XX> Pause number must be within range. Enter a new pause number.");
                continue;
            }

            Integer secs = InputHelper.askForNumber("Enter time (secs) to add", sc);
            if (secs == null) return;

            Long newPauseStartTime = watchFitFile.getPauseList().get(pauseNo - 1).getTimeStart() - secs;
            Long oldPauseStartTime = watchFitFile.getPauseList().get(pauseNo - 1).getTimeStart();

            if (watchFitFile.checkForLapStartsBetweenTimeValues(newPauseStartTime, oldPauseStartTime)) {
                return;
            }

            if (watchFitFile.checkForPausesAndGivePrintedResultBasedOnTime(newPauseStartTime, oldPauseStartTime)) {
                return;
            }

            watchFitFile.increasePause(pauseNo, secs.longValue());

            watchFitFile.createTimerList();
            watchFitFile.createPauseList();
            watchFitFile.createGapList();
            watchFitFile.printPauseList("", 0);
            break;
        }
    }
}
