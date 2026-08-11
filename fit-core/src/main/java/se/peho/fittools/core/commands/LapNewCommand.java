package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class LapNewCommand implements Command {

    @Override
    public String getKey() { return "lapn"; }

    @Override
    public String getDescription() { return "Create New Lap At Total Timer"; }

    @Override
    public String getCategory() { return "Laps"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        if (watchFitFile.getRecordMesgAddOnRecords() == null || watchFitFile.getRecordMesgAddOnRecords().isEmpty()) {
            watchFitFile.createTimerList();
        }

        watchFitFile.getLapReportGenerator().printLapReport1();

        Long maxTimer = watchFitFile.getLastTimerInTimerList();
        System.out.println("Last timer value is: " + maxTimer + "s");

        while (true) {
            Long splitTimer = InputHelper.askForTimer("Enter TOTAL TIMER where new lap should start", sc);
            if (splitTimer == null) {
                return;
            }

            if (splitTimer <= 0L) {
                System.out.println("==XX> Timer must be positive.");
                continue;
            }
            if (splitTimer >= maxTimer) {
                System.out.println("==XX> Timer must be smaller than last timer in file.");
                continue;
            }

            watchFitFile.LapNew(splitTimer);

            watchFitFile.createTimerList();
            watchFitFile.createPauseList();
            watchFitFile.createGapList();
            watchFitFile.getLapReportGenerator().printLapReport1();
            return;
        }
    }
}
