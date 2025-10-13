package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitDateTime;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;
import se.peho.fittools.core.PehoUtils;

public class PauseShortenCommand implements Command {
    @Override
    public String getKey() { return "ps"; }

    @Override
    public String getDescription() { return "Shorten a pause"; }

    @Override
    public String getCategory() { return "Pauses"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {

        Long gTime = null;
        Float gDist = null;

        while (true) {
            watchFitFile.printPauseList("", 0);
            System.out.println();
            Integer pauseNo = InputHelper.askForNumber("Enter pause number to modify", sc);
            if (pauseNo == null) return;
            if (pauseNo > watchFitFile.getPauseList().size() || pauseNo < 1) {
                System.out.println("==XX> Pause number must be within range. Enter a new pause number.");
                continue;
            }

            gTime = watchFitFile.getPauseList().get(pauseNo - 1).getTimePause();
            gDist = watchFitFile.getPauseList().get(pauseNo - 1).getDistPause();

            System.out.println();
            System.out.println("New gap dist: " + Math.round(gDist) + " m / " + PehoUtils.m2km2(gDist) + " km");
            System.out.println("New gap maxtime: " + gTime + " sec / " + FitDateTime.toTimerString(gTime));
            System.out.println("New gap minspeed: " + PehoUtils.mps2minpkm(gDist / gTime) + " min/km / " + PehoUtils.mps2kmph2(gDist / gTime) + " km/h");
            Integer newPauseTime = InputHelper.askForNumber("Enter new pause length in seconds", sc);
            if (newPauseTime == null) return;

            // New GAP time 
            gTime = gTime - newPauseTime;

            System.out.println();
            System.out.println("New gap time: " + gTime + " sec / " + FitDateTime.toTimerString(gTime));
            System.out.println("New gap speed: " + PehoUtils.mps2minpkm(gDist / gTime) + " min/km / " + PehoUtils.mps2kmph2(gDist / gTime) + " km/h");

            System.out.println("");
            String yesNo = InputHelper.askForString("Happy with gap (y/n)", "y", sc);
            if (yesNo == null) return;
            if (!yesNo.equalsIgnoreCase("y")) continue;

            watchFitFile.shortenPause(pauseNo, newPauseTime.longValue());

            watchFitFile.createTimerList();
            watchFitFile.createPauseList();
            watchFitFile.createGapList();
            watchFitFile.printGapList("",0);
            break;
        }
    }
}
