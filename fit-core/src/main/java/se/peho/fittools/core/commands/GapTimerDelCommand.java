package se.peho.fittools.core.commands;

import java.util.Scanner;

import com.garmin.fit.Event;
import com.garmin.fit.EventType;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitDateTime;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class GapTimerDelCommand implements Command {
    @Override
    public String getKey() { return "gtdel"; }

    @Override
    public String getDescription() { return "Delete timer events in GAP, fix crash/reboot issues."; }

    @Override
    public String getCategory() { return "Gaps"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        while (true) {
            Integer gapNo = InputHelper.askForNumber("Enter GAP number to DELETE Timer events in and crash/reboot issues", sc);
            if (gapNo == null) return;

            if (gapNo > watchFitFile.getGapList().size() || gapNo < 1) {
                System.out.println("==XX> Gap number must be within range. Enter a new gap number.");
                continue;
            }
            Long gapStart = watchFitFile.getGapList().get(gapNo - 1).getTimeStart();
            Long gapStop = watchFitFile.getGapList().get(gapNo - 1).getTimeStop();

            watchFitFile.deleteEvents(gapStart, gapStop, Event.TIMER, EventType.INVALID);
            System.out.println("==>> Deleted Timer events between " + FitDateTime.toString(gapStart, watchFitFile.getDiffMinutesLocalUTC()) + " and " + FitDateTime.toString(gapStop, watchFitFile.getDiffMinutesLocalUTC()) + " (inclusive).");

            watchFitFile.createTimerList();
            watchFitFile.createPauseList();
            watchFitFile.createGapList();
            watchFitFile.printGapList("",0);
            break;
        }
    }
}
