package se.peho.fittools.core.commands;

import java.util.Scanner;

import com.garmin.fit.Event;
import com.garmin.fit.EventType;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitDateTime;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class GapTimerCreateCommand implements Command {
    @Override
    public String getKey() { return "g2p"; }

    @Override
    public String getDescription() { return "Convert to PAUSE. Create timer events in GAP."; }

    @Override
    public String getCategory() { return "Gaps"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        while (true) {
            Integer gapNo = InputHelper.askForNumber("Enter GAP number to CREATE TIMER events in and create PAUSE", sc);
            if (gapNo == null) return;

            if (gapNo > watchFitFile.getGapList().size() || gapNo < 1) {
                System.out.println("==XX> Gap number must be within range. Enter a new gap number.");
                continue;
            }

            Long gapStart = watchFitFile.getGapList().get(gapNo - 1).getTimeStart();
            Long gapStop = watchFitFile.getGapList().get(gapNo - 1).getTimeStop();

            watchFitFile.clearTempUpdateLogg();
            watchFitFile.appendTempUpdateLoggLn("-------------------------");
            watchFitFile.appendTempUpdateLoggLn("Creating timer events in GAP to create a PAUSE");
            watchFitFile.appendTempUpdateLoggLn("For GAP no: " + gapNo);
            watchFitFile.appendTempUpdateLoggLn("-------------------------");

            watchFitFile.createEvent(gapStart, Event.TIMER, EventType.STOP_ALL);
            watchFitFile.createEvent(gapStop, Event.TIMER, EventType.START);
            watchFitFile.appendTempUpdateLoggLn("==>> Created Timer events between "
                 + FitDateTime.toString(gapStart, watchFitFile.getDiffMinutesLocalUTC())
                 + " and "
                 + FitDateTime.toString(gapStop, watchFitFile.getDiffMinutesLocalUTC())
                 + " (inclusive).");

            watchFitFile.updateActivityInfoWhenDeletingGapToPause(gapNo - 1);

            System.out.println(watchFitFile.getTempUpdateLogg());
            watchFitFile.appendUpdateLogg(watchFitFile.getTempUpdateLogg());

            watchFitFile.createTimerList();
            watchFitFile.createPauseList();
            watchFitFile.createGapList();
            break;
        }
    }
}
