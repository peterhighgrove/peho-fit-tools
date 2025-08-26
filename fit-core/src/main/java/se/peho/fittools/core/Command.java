package se.peho.fittools.core;

import java.util.Scanner;

public interface Command {
    String getKey();
    String getDescription();
    void run(Scanner sc, FitFile watchFitFile);
}
