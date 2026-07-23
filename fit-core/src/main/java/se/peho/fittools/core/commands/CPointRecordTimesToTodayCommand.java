package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class CPointRecordTimesToTodayCommand implements Command {
    @Override
    public String getKey() { return "cpdate"; }

    @Override
    public String getDescription() { return "Change record/CPoint dates to today"; }

    @Override
    public String getCategory() { return "Course points"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        watchFitFile.getCPointFix().changeRecordAndCPointTimesToTodayDate();
    }
}