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
    public String getDescription() { return "Change Activity time"; }

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
            
            yesToChange = InputHelper.askForString("Do want to change ACTIVITY UTC time to a specific time (yyyy-mm-dd-hh-mm-ss / n)", sc);
            if (yesToChange == null) return;
            if (yesToChange.length() == 19) watchFitFile.changeActivityTimeUTCToDateString(yesToChange);
            
            yesToChange = InputHelper.askForString("Do want to change ACTIVITY LOCAL time to FIRST record time (yyyy-mm-dd-hh-mm-ss / n)", sc);
            if (yesToChange == null) return;
            if (yesToChange.length() == 19) watchFitFile.changeActivityTimeLocalToDateString(yesToChange);
            
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
