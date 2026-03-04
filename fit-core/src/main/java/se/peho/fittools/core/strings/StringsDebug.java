package se.peho.fittools.core.strings;

/**
 * Static flag that gates all System.out.println calls in the strings package.
 * Set by Conf when --debugstrings (or --debug) is present in the arguments.
 */
public class StringsDebug {
    public static boolean enabled = false;
}
