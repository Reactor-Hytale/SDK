package codes.reactor.sdk.util;

import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public final class StringUtil {

    /**
     * Split a string by per delimiter character
     *
     * @param text the string to split
     * @param character delimiter character
     * @return a list of all strings
     */
    public static List<String> split(final String text, final char character) {
        final ArrayList<String> list = new ArrayList<>();
        int start = 0;
        int index;
        while ((index = text.indexOf(character, start)) != -1) {
            list.add(text.substring(start, index));
            start = index + 1;
        }
        if (start == 0) {
            list.add(text);
        } else {
            list.add(text.substring(start));
        }
        return list;
    }

    /**
     * Split by char (ignoring extra)
     * Example input: "       a  bcd        efg      h  i"
     * Output: [a,bcd,efg,h,i]
     *
     * @param text to split
     * @param charToFound to split and ignore if found extra
     * @return a list of all split
     */
    public static List<String> splitIgnoreExtra(final String text, final char charToFound) {
        final ArrayList<String> list = new ArrayList<>();
        int length = text.length();
        int start = -1;

        for (int i = 0; i < length; i++) {
            final char character = text.charAt(i);
            if (character != charToFound) {
                if (start == -1) {
                    start = i;
                }
                continue;
            }
            if (start != -1) {
                list.add(text.substring(start, i));
                start = -1;
            }
        }

        if (start != -1) {
            list.add(text.substring(start));
        }
        return list;
    }

    public static String combine(final Collection<String> strings) {
        final StringBuilder builder = new StringBuilder();
        for (final String string : strings) {
            builder.append(string);
        }
        return builder.toString();
    }

    public static String combine(final Collection<String> strings, char separatorChar) {
        final StringBuilder builder = new StringBuilder();
        int i = 0;
        for (final String string : strings) {
            builder.append(string);
            if (++i != strings.size()) {
                builder.append(separatorChar);
            }
        }
        return builder.toString();
    }

    public static int length(final String string) {
        return string == null ? 0 : string.length();
    }
}