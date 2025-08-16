package se.peho.fittools.core;

import com.garmin.fit.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import javax.print.DocFlavor;
import jdk.jfr.consumer.RecordedThread;


public class SportProfileFitFile {

    FileInputStream in;
    Decode decode;
    MesgBroadcaster broadcaster;

    List<Mesg> mesgRecordsExtra = new ArrayList<>();
    List<Mesg> mesgRecords = new ArrayList<>();

    String outputString = ""; 
    Field savedField;

    
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void readFitFileExtra (String inputFilePath) {
        
        try {
            // Verify the file exists and is a valid FIT file
            File file = new File(inputFilePath);
            if (!file.exists()) { // || !file.isTrue()
                System.err.println("File not found: " + inputFilePath);
                return;
            }
            in = new FileInputStream(inputFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Error opening file ");
        }

        try {
            // Create a Decode object
            decode = new Decode();

            // Create a MesgBroadcaster for decoding
            broadcaster = new MesgBroadcaster(decode);

            addListenersExtra();

            // Decode the FIT file

            decode.read(in, broadcaster);
            try {
                in.close();
            } catch (Exception e) {
                System.out.println("============== Closing Exception: " + e);
            }
        } catch (FitRuntimeException e) {
            System.err.println("Error processing FIT file: " + e.getMessage());
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void addListenersExtra() {

        broadcaster.addListener (new MesgListener() {
            @Override
            public void onMesg(Mesg mesg) {
                mesgRecordsExtra.add(mesg);
            }
        });
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void readFitFile (String inputFilePath) {
        
        try {
            // Verify the file exists and is a valid FIT file
            File file = new File(inputFilePath);
            if (!file.exists()) { // || !file.isTrue()
                System.err.println("File not found: " + inputFilePath);
                return;
            }
            in = new FileInputStream(inputFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Error opening file ");
        }

        try {
            // Create a Decode object
            decode = new Decode();

            // Create a MesgBroadcaster for decoding
            broadcaster = new MesgBroadcaster(decode);

            addListeners();

            // Decode the FIT file

            decode.read(in, broadcaster);
            try {
                in.close();
            } catch (Exception e) {
                System.out.println("============== Closing Exception: " + e);
            }
        } catch (FitRuntimeException e) {
            System.err.println("Error processing FIT file: " + e.getMessage());
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void addListeners() {

        broadcaster.addListener (new MesgListener() {
            @Override
            public void onMesg(Mesg mesg) {
                mesgRecords.add(mesg);
            }
        });
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void mesgSave() {
        int i = 0;

        for (Mesg record : mesgRecordsExtra) {
            i++;
            //if (record.getNum() != 20 && record.getNum() != 78 && record.getNum() != 233 && record.getNum() != 324) {
            //if (record.getNum() == 70) {
                for (Field field : record.getFields()) {
                    //if (record.getNum() == 70 && field.getNum() == 29){
                    if (record.getNum() == 2 && field.getNum() == 167){
                        //Field newField = new Field("ASAS",29,7,0d,0d,"",false,Profile.Type.STRING);
                        savedField = field;
                    }
                    for (int j = 1; j < field.getNumValues(); j++) {
                    }

                }
            //}

        }

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void mesgInsert() {
        int i = 0;

        for (Mesg record : mesgRecords) {
            i++;
            if (record.getNum() == 2){
                //Field newField = new Field("ASAS",29,7,0d,0d,"",false,Profile.Type.STRING);
                record.addField(savedField);
            }
            //if (record.getNum() != 20 && record.getNum() != 78 && record.getNum() != 233 && record.getNum() != 324) {
            //if (record.getNum() == 70) {
                for (Field field : record.getFields()) {
                    //if (record.getNum() == 70 && field.getNum() == 29){
                    if (record.getNum() == 2 && field.getNum() == 167){
                        //Field newField = new Field("ASAS",29,7,0d,0d,"",false,Profile.Type.STRING);
                    }
                    for (int j = 1; j < field.getNumValues(); j++) {
                    }

                }
            //}

        }

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void mesgPrinter() {
        int i = 0;

        for (Mesg record : mesgRecords) {
            i++;
            //if (record.getNum() != 20 && record.getNum() != 78 && record.getNum() != 233 && record.getNum() != 324) {
            //if (record.getNum() == 70) {
                outputString += "MesgNo: " + i;
                outputString += " MesgName: " + record.getName();
                outputString += " MesgNum: " + record.getNum();
                for (Field field : record.getFields()) {
                    //if (record.getNum() == 70 && field.getNum() == 29){
                    /*if (record.getNum() == 2 && field.getNum() == 167){
                        //Field newField = new Field("ASAS",29,7,0d,0d,"",false,Profile.Type.STRING);
                        outputString += System.lineSeparator();
                        outputString += "   Field:" + field.getNum() + ":" + field.getName() + ":" + field.getStringValue();
                        outputString += "    type:" + field.getType();
                        outputString += "    profile:" + field.getProfileType();
                        outputString += "    units:" + field.getUnits();
                        outputString += "    scale:"  ;
                        outputString += "    offset:" ;
                        outputString += "    isAccumulated:" + field.getIsAccumulated();
                        outputString += "    components:" ;
                        outputString += "    subFields:" ;
                        outputString += "    isExpanded:" ;
                    }*/
                    outputString += System.lineSeparator();
                    outputString += "   Field:" + field.getNum() + ":" + field.getName() + ":" + field.getStringValue();
                    for (int j = 1; j < field.getNumValues(); j++) {
                        outputString += System.lineSeparator();
                        outputString += "      NumVal:" + field.getStringValue(j);
                    }

                }
                //outputString += " Field: " + record.getField(1);
                outputString += System.lineSeparator();
            //}

        }
        System.out.println(outputString);

    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void encodeNewFit (String outputFilePath) {

        System.out.println("Encode Activity FIT File");

        try {
            FileEncoder encode;
            encode = new FileEncoder(new java.io.File(outputFilePath), Fit.ProtocolVersion.V2_0);

            for (Mesg record : mesgRecords) {
                encode.write(record);
            }
            encode.close();
        } catch (FitRuntimeException e) {
            System.err.println("Error opening file ......fit");
            return;
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

}
