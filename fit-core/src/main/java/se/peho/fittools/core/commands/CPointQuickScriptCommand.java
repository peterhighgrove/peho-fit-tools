package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class CPointQuickScriptCommand implements Command {
    @Override
    public String getKey() { return "cpq"; }

    @Override
    public String getDescription() { return "Commands: cptg, cpm, cpace, recfix, cpa, cpi, cpb15, csh7"; }

    @Override
    public String getCategory() { return "Course points"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        watchFitFile.getCPointFix().changeGenericCPointTypes(sc);
        watchFitFile.getCPointFix().checkAndMoveCPointsAfterRecords(sc);

        while (true) {
            System.out.print("Enter new pace (mm:ss min/km) (b = back): ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("b")) {
                return;
            }

            if (!input.matches("\\d{1,3}:[0-5]\\d")) {
                System.out.println("==XX> Invalid pace format. Expected mm:ss (example: 5:30).");
                continue;
            }
            watchFitFile.applyConstantPaceToRecordTimesFromGps(input);
            watchFitFile.getCPointFix().syncCPointTimeAndDistanceToClosestRecordByGps();
            watchFitFile.createTimerList();

            watchFitFile.fixNullRecordTimes();
            watchFitFile.createTimerList();
            watchFitFile.createPauseList();
            watchFitFile.createGapList();

            watchFitFile.getCPointFix().changeCPointNamesFromAbbrevList();
            watchFitFile.getCPointFix().insertTypeCharInCPointNames();
            watchFitFile.getCPointFix().moveCPointsBack(sc);
            watchFitFile.getCPointFix().shiftGpsPointsSideways(sc);
            return;
        }
    }
}