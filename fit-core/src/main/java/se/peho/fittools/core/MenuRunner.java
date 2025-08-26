package se.peho.fittools.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import se.peho.fittools.core.commands.FillGapsCommand;
import se.peho.fittools.core.commands.IncreasePauseCommand;
import se.peho.fittools.core.commands.ModifyGapCommand;
import se.peho.fittools.core.commands.ShortenPauseCommand;
import se.peho.fittools.core.commands.StartCommand;

public class MenuRunner {
    private final FitFile watchFitFile;
    private final Map<String, Command> commands = new LinkedHashMap<>();
    private final Scanner sc = new Scanner(System.in);

    public MenuRunner(FitFile watchFitFile) {
        this.watchFitFile = watchFitFile;
        registerCommands();
    }

    private void registerCommands() {
        commands.put("p", new ShortenPauseCommand());
        commands.put("inc", new IncreasePauseCommand());
        commands.put("g", new ModifyGapCommand());
        commands.put("fill", new FillGapsCommand());
        commands.put("start", new StartCommand());
    }

    public void run() {
        while (true) {
            printMenu();
            String choice = sc.nextLine().trim();

            if (choice.equals("s")) {
                System.out.println("Nothing done. Exiting.");
                break;
            } else if (choice.equals("0")) {
                // watchFitFile.saveChanges();
                break;
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
        System.out.println("(0) Save & exit");
        System.out.println("(s) Stop without saving");
        System.out.print("Choose action: ");
    }
}
