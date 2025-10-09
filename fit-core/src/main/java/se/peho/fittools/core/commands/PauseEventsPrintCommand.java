package se.peho.fittools.core.commands;

import java.util.Scanner;

import com.garmin.fit.Event;
import com.garmin.fit.EventType;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitDateTime;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class PauseEventsPrintCommand implements Command {
    @Override
    public String getKey() { return "pepr"; }

    @Override
    public String getDescription() { return "Print events in PAUSE."; }

    @Override
    public String getCategory() { return "Pauses"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        while (true) {
            Integer pauseNo = InputHelper.askForNumber("Enter PAUSE number to PRINT events in", sc);
            if (pauseNo == null) return;

            if (pauseNo > watchFitFile.getPauseList().size() || pauseNo < 1) {
                System.out.println("==XX> Pause number must be within range. Enter a new pause number.");
                continue;
            }
            Long pauseStart = watchFitFile.getPauseList().get(pauseNo - 1).getTimeStart();
            Long pauseStop = watchFitFile.getPauseList().get(pauseNo - 1).getTimeStop();

            watchFitFile.printEvents(pauseStart, pauseStop, Event.TIMER, EventType.INVALID);
            System.out.println("==>> Printed Timer events between " + FitDateTime.toString(pauseStart, watchFitFile.getDiffMinutesLocalUTC()) + " and " + FitDateTime.toString(pauseStop, watchFitFile.getDiffMinutesLocalUTC()) + " (inclusive).");
            System.out.println("");

            watchFitFile.createTimerList();
            watchFitFile.createPauseList();
            watchFitFile.createGapList();
            break;
        }
    }
}
