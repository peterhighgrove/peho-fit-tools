package se.peho.fittools.core.commands;

import java.util.Scanner;

import com.garmin.fit.Event;
import com.garmin.fit.EventType;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitDateTime;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class PauseTimerDelCommand implements Command {
    @Override
    public String getKey() { return "p2g"; }

    @Override
    public String getDescription() { return "Create GAP. Delete timer events in PAUSE."; }

    @Override
    public String getCategory() { return "Pauses"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        while (true) {
            Integer pauseNo = InputHelper.askForNumber("Enter PAUSE number to DELETE Timer events in and create gap", sc);
            if (pauseNo == null) return;

            if (pauseNo > watchFitFile.getPauseList().size() || pauseNo < 1) {
                System.out.println("==XX> Pause number must be within range. Enter a new pause number.");
                continue;
            }

            watchFitFile.getPauseFix().pauseToGap(pauseNo);

            watchFitFile.createTimerList();
            watchFitFile.createPauseList();
            watchFitFile.createGapList();
            watchFitFile.printGapList("",0);
            watchFitFile.printPauseList("",0);
            break;
        }
    }
}
