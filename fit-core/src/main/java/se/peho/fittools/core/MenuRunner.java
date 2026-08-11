package se.peho.fittools.core;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import se.peho.fittools.core.commands.*;

public class MenuRunner {
    private final FitFile watchFitFile;
    private final Conf conf;
    private final Map<String, Command> commands = new LinkedHashMap<>();
    private final Scanner sc = new Scanner(System.in);

    public MenuRunner(FitFile watchFitFile, Conf conf) {
        this.watchFitFile = watchFitFile;
        this.conf = conf;
        registerCommands();
    }

    private void registerCommands() {

        Command[] cmds = {
            new ActivityInfoShowDetailedCommand(),
            new ActivityChangeTime(),
            new ActivityAddAnother(),
            new ActivityFileStructureCommand(),
            new ActivityMesgCSVCommand(),
            new ActivityDumpMesgCommand(),
            new GapShowListCommand(),
            new GapShowListFilteredCommand(),
            new GapAddGpsCommand(),
            new GapsFillCommand(),
            new GapTimerDelCommand(),
            new GapEventsPrintCommand(),
            new GapTimerCreateCommand(),
            new PauseShowListCommand(),
            new PauseShowListFilteredCommand(),
            new PauseShortenCommand(),
            new PauseIncreaseCommand(),
            //new PauseDeleteCommand(),
            new PauseTimerDelCommand(),
            new PauseTimersPrintCommand(),
            new LapShowList1Command(),
            new LapShowList2Command(),
            new LapShowList3Command(),
            new LapShowList4Command(),
            new LapShowListIntervalCommand(),
            new LapShowListDebugCommand(),
            new LapMergeCommand(),
            new LapNewCommand(),
            new SplitShowListCommand(), 
            new CPointQuickScriptCommand(),
            new CPointReportCommand(),
            new CPointReportGenericsCommand(),
            new CPointRenameCommand(),
            new CourseNameCommand(),
            new CPointAbbrevCommand(),
            new CPointBackCommand(),
            new CPointMoveAfterRecordsCommand(),
            new CourseShiftCommand(),
            new CPointTimeToClosestRecordCommand(),
            new CourseToNewDateCommand(),
            new CPointInsertCharCommand(),
            new CPointTypeChangeCommand(),
            new CPointTypeGenericLoopCommand(),
            new CPointCleanNamePrefixesCommand(),
            new PrintRecordsCommand(),
            new CoursePaceCommand(),
            new RecFixCommand(),
            new RecDeleteCreateGapCommand(),
            new RecStartCommand(),
            new WktShowCommand(),
            new StopPrintCommand(),
            new SaveChangeStartTimeExitCommand(conf)
        };

        // Add each command to the map using its own key
        for (Command cmd : cmds) {
            commands.put(cmd.getKey(), cmd);
        }
    }

    public void run() {
        prepareFile();

        while (true) {
            printMainMenu();
            String choice = sc.nextLine().trim();

            if (choice.equals("x")) {
                System.out.println("Nothing done. Exiting.");
                break;
            } else if (choice.equals("m")) {
                printFullMenu();
                choice = sc.nextLine().trim();
                if (choice.equals("x")) {
                    System.out.println("Nothing done. Exiting.");
                    break;
                }
            }

            runCommand(choice, sc);
        }
    }

    public List<String> getAvailableCommandKeys() {
        return new ArrayList<>(commands.keySet());
    }

    public void runCommand(String choice, Scanner scanner) {
        Command cmd = commands.get(choice);
        if (cmd != null) {
            cmd.run(scanner, watchFitFile);
        } else {
            System.out.println("Unknown command: " + choice);
        }
    }

    public void runSingleCommand(String choice, String inputText) {
        String effectiveInput = inputText == null ? "" : inputText;
        Scanner scanner = new Scanner(new ByteArrayInputStream(effectiveInput.getBytes(StandardCharsets.UTF_8)));
        prepareFile();
        runCommand(choice, scanner);
    }

    private void prepareFile() {
        watchFitFile.readFitFile(conf.getInputFilePath());

        if (watchFitFile.isCourseFile()) {
            watchFitFile.printAndAppendUpdateLogLn("======== Course file detected. Only course-related commands will be available.");
            watchFitFile.checkAndFixNullRecordTimes();
            watchFitFile.fixLapAndEventTimestampsFromRecords();

            if (watchFitFile.checkLapTotalsAndEnhancedAvgSpeed()) {
                watchFitFile.printAndAppendUpdateLogLn("Lap totals and enhanced average speed are correct.");
            } else {
                watchFitFile.printAndAppendUpdateLogLn("Lap totals and enhanced average speed are correct.");
                watchFitFile.fixLapTotalsAndEnhancedAvgSpeed();
            }
        } else {
            watchFitFile.printAndAppendUpdateLogLn("======== Non-course file detected. All commands will be available.");
        }

        watchFitFile.saveFileInfoBefore();
        watchFitFile.createTimerList();
        watchFitFile.createPauseList();
        watchFitFile.createGapList();
    }

    private void printMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("(m) Show full menu");
        System.out.println("(x) Stop without saving");
        System.out.print("Choose action: ");
    }

    private void printFullMenu() {
        System.out.println("\n=== FULL MENU ===");

        // Group commands by category
        Map<String, List<Command>> grouped = new LinkedHashMap<>();
        for (Command cmd : commands.values()) {
            grouped.computeIfAbsent(cmd.getCategory(), k -> new ArrayList<>()).add(cmd);
        }

        for (Map.Entry<String, List<Command>> entry : grouped.entrySet()) {
            System.out.println("-- " + entry.getKey() + " --");
            for (Command cmd : entry.getValue()) {
                System.out.println("(" + cmd.getKey() + ") " + cmd.getDescription());
            }
        }

        System.out.println("(x) Stop without saving");
        System.out.print("Choose action: ");
    }
}
