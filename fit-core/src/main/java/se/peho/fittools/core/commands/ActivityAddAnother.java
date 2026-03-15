package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;
import se.peho.fittools.core.PehoUtils;
import java.io.File;

public class ActivityAddAnother implements Command {

    @Override
    public String getKey() { return "aa"; }

    @Override
    public String getDescription() { return "Add Activity from another file."; }

    @Override
    public String getCategory() { return "Activity"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {

        System.out.println("");
        watchFitFile.printActivityTimes();
        System.out.println("");

        while (true) {
            String filenameToAdd = "";
            filenameToAdd = InputHelper.askForString("Filename of the file to add", sc);
            if (filenameToAdd == null) return;

            filenameToAdd = filenameToAdd.trim();
            if (filenameToAdd.isEmpty()) continue;

            String resolvedInput = filenameToAdd.isEmpty() || new File(filenameToAdd).isAbsolute()
                    ? filenameToAdd
                    : PehoUtils.resolveDownloadsFolder() + File.separator + filenameToAdd;
            String resolvedFilePath = PehoUtils.resolveValidFile(resolvedInput, new String[]{"aaa.zip", "aaa.fit"}, PehoUtils.resolveDownloadsFolder());
            if (resolvedFilePath == null) {
                System.out.println("File not found or invalid FIT/ZIP file.");
                continue;
            }

            FitFile fitFileToAdd = new FitFile();
            fitFileToAdd.readFitFile(resolvedFilePath);
            if (fitFileToAdd.getAllMesg().isEmpty()) {
                System.out.println("Unable to read FIT data from file: " + resolvedFilePath);
                continue;
            }

            watchFitFile.addActivityFromAnotherFile(fitFileToAdd);

            watchFitFile.createTimerList();
            watchFitFile.createPauseList();
            watchFitFile.createGapList();
            
            watchFitFile.printActivityTimes();
            return;
        }
    }
}
