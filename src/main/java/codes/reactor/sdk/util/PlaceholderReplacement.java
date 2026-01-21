package codes.reactor.sdk.util;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public final class PlaceholderReplacement {

    public Map<String, String> getReplacements(final Object... replacements) {
        final Map<String, String> map = new HashMap<>(replacements.length / 2);
        for (int i = 0; i < replacements.length; i += 2) {
            map.put(replacements[i].toString(), replacements[i + 1].toString());
        }
        return map;
    }

    public static String applyReplacements(String message, Object... replacements) {
        if (replacements.length == 0) {
            return message;
        }
        return applyReplacements(message, getReplacements(replacements));
    }

    public static String applyReplacements(String message, Map<String, String> replacements) {
        final StringBuilder result = new StringBuilder(message.length());
        final int length = message.length();

        for (int i = 0; i < length; i++) {
            final char character = message.charAt(i);

            if (character == '%') {
                final int end = message.indexOf('%', i + 1);

                if (end != -1) {
                    String placeholder = message.substring(i, end + 1);

                    String replacement = replacements.get(placeholder);
                    if (replacement != null) {
                        result.append(replacement);
                        i = end; // skip
                        continue;
                    }
                }
            }

            result.append(character);
        }

        return result.toString();
    }
}
