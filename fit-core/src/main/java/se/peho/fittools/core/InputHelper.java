package se.peho.fittools.core;

import java.util.Scanner;

public class InputHelper {
    
    public static Long askForTimer(String prompt, Scanner sc) {
        while (true) {
            System.out.print(prompt + " (format ss, m:ss or h:mm:ss) (b = back): ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("b")) return null;

            // Accept ss, m:ss, or h:mm:ss
            if (!input.matches("\\d{1,}(:\\d{1,2}){0,2}")) {
                System.out.println("XXXX> Invalid format. Expected ss, m:ss, or h:mm:ss (e.g., 45, 5:30, 1:05:30).");
            } else {
                try {
                    String[] parts = input.split(":");
                    int hours = 0;
                    int minutes = 0;
                    int seconds;

                    if (parts.length == 1) {
                        // ss
                        seconds = Integer.parseInt(parts[0]);
                    } else if (parts.length == 2) {
                        // m:ss
                        minutes = Integer.parseInt(parts[0]);
                        seconds = Integer.parseInt(parts[1]);
                    } else {
                        // h:mm:ss
                        hours = Integer.parseInt(parts[0]);
                        minutes = Integer.parseInt(parts[1]);
                        seconds = Integer.parseInt(parts[2]);
                    }

                    if (minutes < 0 || minutes > 59) {
                        System.out.println("XXXX> Minutes must be between 0 and 59.");
                    } else if (seconds < 0 || seconds > 59) {
                        System.out.println("XXXX> Seconds must be between 0 and 59.");
                        continue;
                    }

                    long totalSeconds = hours * 3600L + minutes * 60L + seconds;

                    // Return Garmin timestamp (duration offset from Garmin epoch)
                    return totalSeconds;
                } catch (NumberFormatException e) {
                    System.out.println("XXXX> Invalid number. Please try again.");
                }
            }
        }
    }

    public static Integer askForNumber(String prompt, Scanner sc) {
        while (true) {
            System.out.print(prompt + " (b = back): ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("b")) return null;
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("XXXX> Not a valid number.");
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
                System.out.println("XXXX> Invalid string, try again.");
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
                System.out.println("XXXX> Invalid coords, try again.");
            }
        }
    }
}
