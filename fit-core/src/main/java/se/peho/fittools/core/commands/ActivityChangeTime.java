package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.Conf;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class ActivityChangeTime implements Command {

    @Override
    public String getKey() { return "at"; }

    @Override
    public String getDescription() { return "Chenge Activity time"; }

    @Override
    public String getCategory() { return "Info"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        while (true) {
            String yesToChange = "";
            System.out.println("");
            watchFitFile.printActivityTimeInfo();
            watchFitFile.printActivitySessionTimes();
            System.out.println("");
            
            watchFitFile.printActivityTimes();
            yesToChange = InputHelper.askForString("Do want to change ACTIVITY time to FIRST record time (y/n)", sc);
            if (yesToChange == null) return;
            if (yesToChange.toLowerCase().equals("y")) watchFitFile.changeActivityTimeUTCandLocalToFirstTimeRecordDiff();
            
            watchFitFile.printSessionTimes();
            yesToChange = InputHelper.askForString("Do want to change SESSION time to FIRST record time (y/n)", sc);
            if (yesToChange == null) return;
            if (yesToChange.toLowerCase().equals("y")) watchFitFile.changeSessionTimeToFirstTimeRecordDiff();
            
            watchFitFile.printSessionStartTimes();
            yesToChange = InputHelper.askForString("Do want to change SESSION START time to FIRST record time (y/n)", sc);
            if (yesToChange == null) return;
            if (yesToChange.toLowerCase().equals("y")) watchFitFile.changeSessionStartTimeToFirstTimeRecordDiff();

            watchFitFile.printActivitySessionTimes();
            return;
        }
    }
}
