package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.InputHelper;

public class WktInsertCommand implements Command {

    @Override
    public String getKey() { return "wkti"; }

    @Override
    public String getDescription() { return "Set lap/split workout interval types"; }

    @Override
    public String getCategory() { return "Workouts"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        int lapCount = watchFitFile.getLapMesg() != null ? watchFitFile.getLapMesg().size() : 0;
        if (lapCount == 0) {
            System.out.println("==XX> No LAP messages found.");
            return;
        }

        while (true) {
            Integer warmupLaps = InputHelper.askForNumber("How many warmup laps at the beginning?", "0", sc);
            if (warmupLaps == null) {
                return;
            }
            if (warmupLaps < 0) {
                System.out.println("==XX> Warmup laps must be >= 0.");
                continue;
            }

            Integer cooldownLaps = InputHelper.askForNumber("How many cooldown laps at the end?", "0", sc);
            if (cooldownLaps == null) {
                return;
            }
            if (cooldownLaps < 0) {
                System.out.println("==XX> Cooldown laps must be >= 0.");
                continue;
            }

            if (warmupLaps + cooldownLaps > lapCount) {
                System.out.println("==XX> warmup + cooldown exceeds total laps (" + lapCount + ").");
                continue;
            }

            boolean useRest;
            while (true) {
                System.out.print("Rest or recover after active laps? (rest/recover, b = back): ");
                String choice = sc.nextLine().trim().toLowerCase();
                if (choice.equals("b")) {
                    return;
                }
                if (choice.equals("rest") || choice.equals("r")) {
                    useRest = true;
                    break;
                }
                if (choice.equals("recover") || choice.equals("recovery") || choice.equals("rec")) {
                    useRest = false;
                    break;
                }
                System.out.println("==XX> Enter rest or recover.");
            }

            watchFitFile.applyWorkoutIntervalPattern(warmupLaps, cooldownLaps, useRest);
            return;
        }
    }
}
