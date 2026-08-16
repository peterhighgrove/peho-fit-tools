package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class SplitLapAnalyzeCommand implements Command {
    @Override
    public String getKey() { return "spla"; }

    @Override
    public String getDescription() { return "Analyze SPLIT vs LAP matches"; }

    @Override
    public String getCategory() { return "Splits"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        watchFitFile.analyzeSplitsAgainstLaps();
    }
}
