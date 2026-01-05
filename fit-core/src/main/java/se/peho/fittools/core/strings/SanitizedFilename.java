package se.peho.fittools.core.strings;

public class SanitizedFilename {

    private String filename = null;

    private static String sanitizeFilename(String name) {
        return name
            .replace("/", "_")
            .replace(":", ".")
            .replaceAll("[\\\\/:*?\"<>|!]", "-")
            ;
    }
    
    public SanitizedFilename(String name) {
        this.filename = sanitizeFilename(name);
    }

    public String getName() {
        return this.filename;
    }
}
