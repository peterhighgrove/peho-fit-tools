package se.peho.fittools.gpsfix;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import se.peho.fittools.core.Conf;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.MenuRunner;
 
public class Main {

    public static void main(String[] args) {
        SimpleDateFormat sweDateTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        System.out.println(" ========================= START OF PROGRAM fit-fix-gps-activities / gpsfix ===========================" + sweDateTime.format(Calendar.getInstance().getTime()));
        
        
        //int conf.Integer.parseInt( = 1; // 1 = winter time in swe, used for string conv to filename
        // Reading CONF FILE
        
        System.out.println("ArgsLen: " + args.length);

        Conf conf = new Conf(args);
        
        // ================================
        // START
        // ================================

        FitFile watchFitFile = new FitFile();

        // READING FIT FILE
        watchFitFile.readFitFile (conf.getInputFilePath());

        // SAVE INFO ABOUT FILE BEFORE UPDATIING
        watchFitFile.saveFileInfoBefore();
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


