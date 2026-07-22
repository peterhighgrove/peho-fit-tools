package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class CPointMoveAfterRecordsCommand implements Command {
    @Override
    public String getKey() { return "cpm"; }

    @Override
    public String getDescription() { return "Check/move CPoints after records"; }

    @Override
    public String getCategory() { return "Course points"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        watchFitFile.getCPointFix().checkAndMoveCPointsAfterRecords(sc);
    }
}