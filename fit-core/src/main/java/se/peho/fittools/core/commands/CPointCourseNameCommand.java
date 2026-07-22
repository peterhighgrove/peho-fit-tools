package se.peho.fittools.core.commands;

import java.util.Scanner;

import se.peho.fittools.core.Command;
import se.peho.fittools.core.FitFile;

public class CPointCourseNameCommand implements Command {
    @Override
    public String getKey() { return "cpcn"; }

    @Override
    public String getDescription() { return "Change Course name in COURSE mesg"; }

    @Override
    public String getCategory() { return "Course points"; }

    @Override
    public void run(Scanner sc, FitFile watchFitFile) {
        watchFitFile.getCPointFix().changeCourseName(sc);
    }
}