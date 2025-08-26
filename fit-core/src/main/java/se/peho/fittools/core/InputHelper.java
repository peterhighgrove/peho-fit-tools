package se.peho.fittools.core;

import java.util.Scanner;

public class InputHelper {
    public static Integer askForNumber(String prompt, Scanner sc) {
        while (true) {
            System.out.print(prompt + " (b = back): ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("b")) return null;
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Not a valid number.");
            }
        }
    }

    public static String askForString(String prompt, Scanner sc) {
        while (true) {
            System.out.print(prompt + " (b = back): ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("b")) return null;
            try {
                return input;
            } catch (Exception e) {
                System.out.println("Invalid string, try again.");
            }
        }
    }

    public static double[] askForCoords(String prompt, Scanner sc) {
        while (true) {
            System.out.print(prompt + " (b = back): ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("b")) return null;
            try {
                return GeoUtils.parseCoordinates(input); // your existing util
            } catch (Exception e) {
                System.out.println("Invalid coords, try again.");
            }
        }
    }
}
