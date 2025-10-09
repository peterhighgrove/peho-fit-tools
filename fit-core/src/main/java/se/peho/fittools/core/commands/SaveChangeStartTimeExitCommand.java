package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.Conf;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class SaveChangeStartTimeExitCommand implements Command {

    private final Conf conf;

    public SaveChangeStartTimeExitCommand(Conf conf) {
        this.conf = conf;
    }
    
    @Override
    public String getKey() { return "save"; }

    @Override
    public String getDescription() { return "SAVE, Change time & EXIT"; }

    @Override
    public String getCategory() { return "Save & Exit"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        while (true) {
            System.out.println("");
            Integer changeMinutes = InputHelper.askForNumber("Enter number of minutes to add to starttime (- for substract)", String.valueOf(conf.getTimeOffsetSec()/60), sc);
            if (changeMinutes == null) return;
            conf.setTimeOffsetSec(changeMinutes * 60);

            System.out.println("");
            String baseActivityDescr = InputHelper.askForString("Enter base activity description", conf.getProfileNameSuffix(), sc);
            if (baseActivityDescr == null) return;
            conf.setProfileNameSuffix(baseActivityDescr);

            watchFitFile.createTimerList();
            watchFitFile.changeStartTime(conf.getTimeOffsetSec());
            watchFitFile.saveChanges(conf);
            System.exit(0);
        }
    }
}
