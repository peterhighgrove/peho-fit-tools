package se.peho.fittools.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import se.peho.fittools.core.commands.FileInfoShowDetailedCommand;
import se.peho.fittools.core.commands.GapAddGpsCommand;
import se.peho.fittools.core.commands.GapTimerCreateCommand;
import se.peho.fittools.core.commands.GapEventsPrintCommand;
import se.peho.fittools.core.commands.GapShowListCommand;
import se.peho.fittools.core.commands.GapShowListFilteredCommand;
import se.peho.fittools.core.commands.GapTimerDelCommand;
import se.peho.fittools.core.commands.GapsFillCommand;
import se.peho.fittools.core.commands.LapMergeCommand;
import se.peho.fittools.core.commands.LapShowList1Command;
import se.peho.fittools.core.commands.LapShowList2Command;
import se.peho.fittools.core.commands.LapShowList3Command;
import se.peho.fittools.core.commands.LapShowList4Command;
import se.peho.fittools.core.commands.PauseTimersPrintCommand;
import se.peho.fittools.core.commands.PauseIncreaseCommand;
import se.peho.fittools.core.commands.PauseShortenCommand;
import se.peho.fittools.core.commands.PauseShowListCommand;
import se.peho.fittools.core.commands.PauseShowListFilteredCommand;
import se.peho.fittools.core.commands.PauseTimerDelCommand;
import se.peho.fittools.core.commands.PrintRecordsCommand;
import se.peho.fittools.core.commands.RecDeleteCreateGapCommand;
import se.peho.fittools.core.commands.RecStartCommand;
import se.peho.fittools.core.commands.SaveChangeStartTimeExitCommand;
import se.peho.fittools.core.commands.SplitShowListCommand;
import se.peho.fittools.core.commands.StopPrintCommand;
import se.peho.fittools.core.commands.WktShowCommand;

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
            new LapMergeCommand(),
            new SplitShowListCommand(),
            new PrintRecordsCommand(),
            new RecDeleteCreateGapCommand(),
            new RecStartCommand(),
            new WktShowCommand(),
            new StopPrintCommand(),
            new FileInfoShowDetailedCommand(),
            new SaveChangeStartTimeExitCommand(conf)
        };

        // Add each command to the map using its own key
        for (Command cmd : cmds) {
            commands.put(cmd.getKey(), cmd);
        }
    }

    public void run() {
        
        // READING FIT FILE
        watchFitFile.readFitFile (conf.getInputFilePath());

        // SAVE INFO ABOUT FILE BEFORE UPDATIING
        watchFitFile.saveFileInfoBefore();
        watchFitFile.createTimerList();
        watchFitFile.createPauseList();
        watchFitFile.createGapList();

        boolean firstTime = true;

        while (true) {
            printMainMenu();
/*             if (firstTime) {
                printFullMenu();
                firstTime = false;
            } else {
                printMainMenu();
            } */

            String choice = sc.nextLine().trim();

            if (choice.equals("x")) {
                System.out.println("Nothing done. Exiting.");
                break;
            } else if (choice.equals("m")) {
                printFullMenu();
                choice = sc.nextLine().trim(); // read new choice after showing menu
            }

            Command cmd = commands.get(choice);
            if (cmd != null) {
                cmd.run(sc, watchFitFile);
            } else {
                System.out.println("Unknown command: " + choice);
            }
        }
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
