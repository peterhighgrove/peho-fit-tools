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

        // READING FIT FILE
        watchFitFile.readFitFile (conf.getInputFilePath());

        // SAVE INFO ABOUT FILE BEFORE UPDATIING
        watchFitFile.saveFileInfoBefore();
        watchFitFile.createTimerList();
        watchFitFile.createPauseList();
        watchFitFile.createGapList();

        // INTERACTIVE MENU
        MenuRunner menu = new MenuRunner(watchFitFile, conf);
        menu.run();

        // ================================
        // END
        // ================================
    }

}


