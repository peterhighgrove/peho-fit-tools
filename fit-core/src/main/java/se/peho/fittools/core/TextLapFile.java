package se.peho.fittools.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextLapFile {
    //
    List<LapRecord> lapRecords = new ArrayList<>();
    int numberOfLaps;

    public void parseTextLapFile (String filename) {

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ", 3); // Split into max 3 parts
                if (parts.length >= 2) { // At least two values should be present
                    Float level = Float.parseFloat(parts[0]); //resistance or level
                    Float distance = Float.parseFloat(parts[1]) * 10;  //convert to m
                    String comment = (parts.length == 3) ? parts[2] : ""; // Handle optional text
                    lapRecords.add(new LapRecord(level, distance, comment));
                }
            }
            this.numberOfLaps = lapRecords.size();
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
    /*public void mergeLapDataInFitFile(FitFile fitFile) {
        int i = 0;
        for (LapRecord record : lapRecords) {
            if (i == 0) {
                fitFile.lapRecords.get(i).setTotalDistance(record.distance);
            }
            else {
                fitFile.lapRecords.get(i).setTotalDistance(record.distance - lapRecords.get(i-1).distance);
            }
            fitFile.lapRecords.get(i).setAvgSpeed(fitFile.lapRecords.get(i).getTotalDistance() / fitFile.lapRecords.get(i).getTotalTimerTime());
            i++;
        }
    }*/
    public void printToConsole() {
        // Print parsed data
        System.out.println("--------------------------------------------------");
        for (LapRecord row : lapRecords) {
            System.out.println(row);
        }
        System.out.println("--------------------------------------------------");
    }
    public boolean isNotNumberOfLapsEqual(FitFilePerMesgType fitFile) {
        boolean notEqual = false;
        if (fitFile.numberOfLaps != this.numberOfLaps) {
            System.out.println("No of Laps -- FIT:" + fitFile.numberOfLaps + ", TXT:" + this.numberOfLaps);
            System.out.println("NOT EQUAL");
            notEqual = true;
        }
        return notEqual;
    }
    class LapRecord {
        Float level;
        Float distance;
        String comment;

        public LapRecord(Float level, Float distance, String comment) {
            this.level = level;
            this.distance = distance;
            this.comment = comment;
        }

        @Override
        public String toString() {
            return level + " " + distance + " " + comment;
        }
    }

}
