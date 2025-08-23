package se.peho.fittools.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PehoUtils {
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private static final String[] SEARCH_LOCATIONS = {
        "./conf.txt",                          // current directory
        System.getProperty("user.home") + "/Downloads/conf.txt", // home dir
        System.getProperty("user.home") + "/.myapp/conf.txt", // home dir
        "/etc/myapp/conf.txt"                  // system-wide (Linux/Mac)
        // On Windows you could also add System.getenv("APPDATA") + "\\MyApp\\conf.txt"
    };
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static Path findConfigFile() {
        for (String location : SEARCH_LOCATIONS) {
            File f = new File(location);
            if (f.exists() && f.isFile()) {
                return f.toPath();
            }
        }
        return null; // nothing found
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static Integer safeParseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null; // or you could return a default value like 0
        }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static void renameFile(String oldName, String newName) {
        if (oldName.equals(newName)) {
        	System.out.println("===FILE ALREADY CORRECT NAME: "+oldName);
        	return ;
        }
    	File oldNameFile = new File(oldName);
    	File newNameFile = new File(newName);
    	if (newNameFile.exists()) {
    		// Rename file (or directory)
            try {
                Files.move(Paths.get(newName), Paths.get(newName + "-backup"), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("============== RENAME BACKUP UNSUCCESSFUL from:" + newName + " to:" + newName + "-backup");
            }
            System.out.println("============== RENAME BACKUP SUCCESS from:" + newName + " to:" + newName + "-backup");
    	}
		
        try {
            Files.move(Paths.get(oldName), Paths.get(newName), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("============== RENAME SRC FILE UNSUCCESSFUL from:" + oldName + " to:" + newName);
        }
        System.out.println("============== RENAME SRC FILE SUCCESS from:" + oldName + " to:" + newName);
    }
    	
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String checkFile(String fitFilePath) {
        File newFitFile = new File(fitFilePath);
        System.out.println("=========> FILE: " + fitFilePath);
        System.out.println("=========> EXTENSION: " + getFileExtension(newFitFile));
        if (getFileExtension(newFitFile).equals("zip")) {
            fitFilePath = unzip(newFitFile);
            newFitFile = new File(fitFilePath);
        }
        if (!newFitFile.exists()) {
            System.out.println("**********************");
            System.out.println(getFileExtension(newFitFile).toUpperCase()+" FILE DO NOT EXIST: "+newFitFile);
            System.out.println("**********************");
            System.exit(0);
        }
        return fitFilePath;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String getFileExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf(".");
        
        if (lastDotIndex == -1 || lastDotIndex == 0) {
            return ""; // No extension found
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String unzip(File zipFile) {
        if (!zipFile.exists() || !zipFile.isFile()) {
            System.out.println("**********************");
            System.out.println("NO ZIP file.");
            System.out.println("**********************");
            System.exit(0);
        }

        String destDirectory = zipFile.getParent(); // Extract to same directory
        String unzippedFile = "";
        File destDir = new File(destDirectory);

        try (FileInputStream fis = new FileInputStream(zipFile);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(destDir, entry.getName());
                unzippedFile = newFile.getPath();
                System.out.println("=========> FILENAME IN ZIP: " + unzippedFile);

                if (entry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs(); // Ensure parent exists
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return unzippedFile;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String mps2kmph3(Float speed) {
        Float newSpeed = ((float) Math.round(speed * 3600f / 1000f *1000)/1000);
        return "" + newSpeed;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String mps2kmph2(Float speed) {
        Float newSpeed = ((float) Math.round(speed * 3600f / 1000f *100)/100);
        return "" + newSpeed;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String mps2kmph1(Float speed) {
        Float newSpeed = ((float) Math.round(speed * 3600f / 1000f *10)/10);
        return "" + newSpeed;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String m2km1(Float speed) {
        String speedStr = String.valueOf(Float.valueOf(Math.round(speed / 1000f *10))/10);
        return speedStr;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String m2km2(Float speed) {
        String speedStr = String.valueOf(Float.valueOf(Math.round(speed / 1000f *100))/100);
        return speedStr;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String mps2minpkm(Float speed) {
        String speedStr = "";
        if (speed.equals(0)) {
            speedStr = "-";
        } else {
            speedStr = sec2minSecLong(1 / (speed / 1000f));
        }
        return speedStr;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String mps2minp500m(Float speed) {
        String speedStr = "";
        if (speed.equals(0)) {
            speedStr = "-";
        } else {
            speedStr = sec2minSecLong(1 / (speed / 1000f) / 2f);
        }
        return speedStr;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String sec2minSecShort(Float seconds) {
        int min = (int) (seconds / 60);
        int sec = (int) (Math.round((seconds / 60f - min) * 60));
        if (sec == 60) {
            min++;
            sec = 0;
        }
        String minStr = String.valueOf(min);
        if (sec == 0) {
            minStr += "";
        } else if (sec < 10) {
            minStr += ":0" + String.valueOf(sec);
        } else {
            minStr += ":" + String.valueOf(sec);
        }
        return minStr;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String sec2minSecShort(Long seconds) {
        int min = (int) (seconds / 60);
        int sec = (int) (Math.round((seconds / 60f - min) * 60));
        if (sec == 60) {
            min++;
            sec = 0;
        }
        String minStr = String.valueOf(min);
        if (sec == 0) {
            minStr += "";
        } else if (sec < 10) {
            minStr += ":0" + String.valueOf(sec);
        } else {
            minStr += ":" + String.valueOf(sec);
        }
        return minStr;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String sec2minSecLong(Float seconds) {
        int min = (int) (seconds / 60);
        int sec = (int) (Math.round((seconds / 60f - min) * 60));
        if (sec == 60) {
            min++;
            sec = 0;
        }
        String minStr = String.valueOf(min);
        if (sec < 10) {
            minStr += ":0" + String.valueOf(sec);
        } else {
            minStr += ":" + String.valueOf(sec);
        }
        return minStr;
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String sec2minSecLong(Long seconds) {
        int min = (int) (seconds / 60);
        int sec = (int) (Math.round((seconds / 60f - min) * 60));
        if (sec == 60) {
            min++;
            sec = 0;
        }
        String minStr = String.valueOf(min);
        if (sec < 10) {
            minStr += ":0" + String.valueOf(sec);
        } else {
            minStr += ":" + String.valueOf(sec);
        }
        return minStr;
    }

}
