package se.peho.fittools.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import se.peho.fittools.core.commands.ChangeStartTimeCommand;
import se.peho.fittools.core.commands.FillGapsCommand;
import se.peho.fittools.core.commands.IncreasePauseCommand;
import se.peho.fittools.core.commands.ModifyGapCommand;
import se.peho.fittools.core.commands.ShortenPauseCommand;
import se.peho.fittools.core.commands.ShowDetailedFileInfoCommand;
import se.peho.fittools.core.commands.ShowGapListCommand;
import se.peho.fittools.core.commands.ShowGapListFilteredCommand;
import se.peho.fittools.core.commands.ShowPauseListCommand;
import se.peho.fittools.core.commands.ShowPauseListFilteredCommand;
import se.peho.fittools.core.commands.StartCommand;

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
            new ShowPauseListCommand(),
            new ShowPauseListFilteredCommand(),
            new ShowGapListCommand(),
            new ShowGapListFilteredCommand(),
            new ShortenPauseCommand(),
            new IncreasePauseCommand(),
            new ModifyGapCommand(),
            new FillGapsCommand(),
            new StartCommand(),
            new ShowDetailedFileInfoCommand(),
            new ChangeStartTimeCommand()
        };

        // Add each command to the map using its own key
        for (Command cmd : cmds) {
            commands.put(cmd.getKey(), cmd);
        }

    }

    public void run() {
        while (true) {
            printMenu();
            String choice = sc.nextLine().trim();

            if (choice.equals("x")) {
                System.out.println("Nothing done. Exiting.");
                break;
            } else if (choice.equals("s")) {
                String doublecheckCommand = InputHelper.askForString("Have you changed starttime (Enter/anything = YES) or", sc);
                if (doublecheckCommand != null) {
                    watchFitFile.saveChanges(conf);
                }
            } else {
                Command cmd = commands.get(choice);
                if (cmd != null) {
                    cmd.run(sc, watchFitFile);
                } else {
                    System.out.println("Unknown command: " + choice);
                }
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== MAIN MENU ===");
        for (Command cmd : commands.values()) {
            System.out.println("(" + cmd.getKey() + ") " + cmd.getDescription());
        }
        System.out.println("(s) Save & exit");
        System.out.println("(x) Stop without saving");
        System.out.print("Choose action: ");
    }
}
