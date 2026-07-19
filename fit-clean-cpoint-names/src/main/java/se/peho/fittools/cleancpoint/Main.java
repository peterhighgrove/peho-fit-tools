package se.peho.fittools.cleancpoint;

import se.peho.fittools.core.Conf;
import se.peho.fittools.core.FitFile;
import se.peho.fittools.core.CPointFix;

public class Main {

    public static void main(String[] args) {
        
        System.out.println("ArgsLen: " + args.length);

        // Reading CONF FILE
        Conf conf = new Conf(args, true); // skipExtraFilename = true to not require an extra file
        
        // ================================
        // START
        // ================================

        FitFile fitFile = new FitFile();
        
        // READING FIT FILE
        fitFile.readFitFile(conf.getInputFilePath());

        // CLEAN COURSE POINT NAME PREFIXES
        CPointFix cPointFix = new CPointFix(fitFile);
        cPointFix.cleanCPointNamePrefixes();

        // SAVE FIT FILE
        fitFile.saveChanges(conf);

        // ================================
        // END
        // ================================
    }

}
