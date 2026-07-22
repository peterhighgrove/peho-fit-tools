package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class CoursePaceCommand implements Command {
    @Override
    public String getKey() { return "cpace"; }

    @Override
    public String getDescription() { return "Set course pace and recalculate record times"; }

    @Override
    public String getCategory() { return "Records"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
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

            System.out.print("Apply pace " + input + " to all records (y/n, b = back): ");
            String confirm = sc.nextLine().trim();
            if (confirm.equalsIgnoreCase("b")) {
                return;
            }
            if (!confirm.equalsIgnoreCase("y")) {
                System.out.println("No changes applied.");
                continue;
            }

            watchFitFile.applyConstantPaceToRecordTimesFromGps(input);

            while (true) {
                System.out.print("Adjust course points with syncCPointTimeAndDistanceToClosestRecordByGps() (y/n, b = back): ");
                String syncCPoints = sc.nextLine().trim();
                if (syncCPoints.equalsIgnoreCase("b")) {
                    return;
                }
                if (syncCPoints.equalsIgnoreCase("y")) {
                    watchFitFile.getCPointFix().syncCPointTimeAndDistanceToClosestRecordByGps();
                    break;
                }
                if (syncCPoints.equalsIgnoreCase("n")) {
                    break;
                }
                System.out.println("==XX> Please answer y or n.");
            }

            watchFitFile.createTimerList();
            System.out.println("cpace completed. Timer list recalculated.");
            return;
        }
    }
}