package com.solab.alarms.util;

/** A class containing some utility functions.
 * 
 * @author Enrique Zamudio
 */
public class Utils {

	/** Replaces all occurrences of var with value, inside the specified string. */
	public static String replaceAll(String var, String value, String string) {
		int pos = string.indexOf(var);
		if (pos >= 0) {
			StringBuilder buf = new StringBuilder(string);
			while (pos >= 0) {
				buf.replace(pos, pos + var.length(), value);
				pos = buf.indexOf(var, pos + value.length());
			}
			return buf.toString();
		} else {
			return string;
		}
	}

    public static void replaceAll(StringBuilder sb, String original, String replacement) {
        int idx = sb.indexOf(original);
        while (idx >= 0) {
            sb.replace(idx, idx+original.length(), replacement);
            idx = sb.indexOf(original, idx+replacement.length());
        }
    }
}
