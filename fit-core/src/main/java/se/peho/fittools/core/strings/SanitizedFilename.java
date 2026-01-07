package se.peho.fittools.core.strings;

public class SanitizedFilename {

    private String filename = null;

    private static String sanitizeFilename(String name) {

        String filename = name
            .replace("/", "_")
            .replace(":", ".")
            .replaceAll("[\\\\/:*?\"<>|!]", "-")
            ;

        System.out.println("  Extracted Filename:'" + name + "'");
        System.out.println("        => Sanitized:'" + filename + "'");

        return filename;
    }
    
    public SanitizedFilename(String name) {
        this.filename = sanitizeFilename(name);
    }

    public String get() {
        return this.filename;
    }

    public static String get(String name) {
        return new SanitizedFilename(name).get();
    }
}
