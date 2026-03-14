package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.Conf;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class ActivityAddAnother implements Command {

    @Override
    public String getKey() { return "aa"; }

    @Override
    public String getDescription() { return "Add Activity from another file."; }

    @Override
    public String getCategory() { return "Info"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        while (true) {
            String filenameToAdd = "";
            System.out.println("");
            watchFitFile.printActivityTimeInfo();
            watchFitFile.printActivitySessionTimes();
            System.out.println("");
            
            watchFitFile.printActivityTimes();
            filenameToAdd = InputHelper.askForString("Filename of the file to add", sc);
            if (filenameToAdd == null) return;
            Conf confForFileToAdd = new Conf(new String[]{"", "0",filenameToAdd});
            FitFile fitFileToAdd = new FitFile();
            fitFileToAdd.readFitFile(confForFileToAdd.getInputFilePath());
            watchFitFile.addActivityFromAnotherFile(fitFileToAdd);

            watchFitFile.createTimerList();
            watchFitFile.createPauseList();
            watchFitFile.createGapList();
            
            watchFitFile.printActivityTimes();
            return;
        }
    }
}
