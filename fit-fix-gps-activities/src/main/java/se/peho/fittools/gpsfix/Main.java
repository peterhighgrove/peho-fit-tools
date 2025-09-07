package se.peho.fittools.gpsfix;

import se.peho.fittools.core.Conf;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.MenuRunner;
 
public class Main {

    public static void main(String[] args) {
        
        System.out.println("ArgsLen: " + args.length);

        // Reading CONF FILE
        Conf conf = new Conf(args);
        
        // ================================
        // START
        // ================================

        FitFile watchFitFile = new FitFile();

        // INTERACTIVE MENU
        MenuRunner menu = new MenuRunner(watchFitFile, conf);
        menu.run();

        // ================================
        // END
        // ================================
    }

}


