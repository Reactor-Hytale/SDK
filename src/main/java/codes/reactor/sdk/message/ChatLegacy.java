package codes.reactor.sdk.message;

import com.hypixel.hytale.server.core.Message;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to transform legacy-formatted text (ampersand based) into Hytale
 * {@link Message} objects.
 * <p>
 * This implementation adheres to the custom logic where color codes do NOT reset
 * active styles (Bold, Italic), fixing the standard 1.8 behavior.
 */
@UtilityClass
public final class ChatLegacy {

    private static final Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");
    private static final Map<Character, String> LEGACY_COLORS = new HashMap<>();

    static {
        // Initialize standard Minecraft legacy colors mapped to Hex
        LEGACY_COLORS.put('0', "#000000"); // Black
        LEGACY_COLORS.put('1', "#0000AA"); // Dark Blue
        LEGACY_COLORS.put('2', "#00AA00"); // Dark Green
        LEGACY_COLORS.put('3', "#00AAAA"); // Dark Aqua
        LEGACY_COLORS.put('4', "#AA0000"); // Dark Red
        LEGACY_COLORS.put('5', "#AA00AA"); // Dark Purple
        LEGACY_COLORS.put('6', "#FFAA00"); // Gold
        LEGACY_COLORS.put('7', "#AAAAAA"); // Gray
        LEGACY_COLORS.put('8', "#555555"); // Dark Gray
        LEGACY_COLORS.put('9', "#5555FF"); // Blue
        LEGACY_COLORS.put('a', "#55FF55"); // Green
        LEGACY_COLORS.put('b', "#55FFFF"); // Aqua
        LEGACY_COLORS.put('c', "#FF5555"); // Red
        LEGACY_COLORS.put('d', "#FF55FF"); // Light Purple
        LEGACY_COLORS.put('e', "#FFFF55"); // Yellow
        LEGACY_COLORS.put('f', "#FFFFFF"); // White
    }

    /**
     * Transforms a legacy-formatted string using the `&` character into a Hytale Message.
     *
     * @param text The legacy text containing formatting codes (e.g., "&aHello &lWorld").
     * @return A {@link Message} object representing the formatted text.
     */
    public static Message fromLegacy(final String text) {
        return parse(text, '&');
    }

    /**
     * Transforms a legacy-formatted string using a custom character into a Hytale Message.
     *
     * @param text        The legacy text.
     * @param colorChar   The character used for formatting (usually '&' or '§').
     * @return A {@link Message} object.
     */
    public static Message parse(final String text, final char colorChar) {
        if (text == null || text.isEmpty()) {
            return Message.empty();
        }

        // We create a root message that will hold all fragments as children
        final Message root = Message.empty();

        // State tracking
        final StringBuilder buffer = new StringBuilder();
        StyleState state = new StyleState();

        final int length = text.length();

        for (int i = 0; i < length; i++) {
            char current = text.charAt(i);

            // Handle Hex colors defined as literal #RRGGBB in the text (if not preceded by color char logic)
            // Note: The original code had a pre-process regex. Here we check strictly for the color char first
            // to maintain legacy compatibility, but we can allow implicit hex if needed.
            // For now, adhering to legacy logic:

            if (current == colorChar && i + 1 < length) {
                char code = text.charAt(i + 1);

                // Check if it's a valid hex code following the color char?
                // Usually legacy is &a. If we want &#FFFFFF, let's handle that custom format if prevalent,
                // otherwise check standard codes.

                String newColor = null;
                boolean isFormat = false;
                boolean isReset = false;

                if (LEGACY_COLORS.containsKey(code)) {
                    newColor = LEGACY_COLORS.get(code);
                    i++; // Skip code char
                } else if (code == '#') {
                    // Handle Hex: &#FFFFFF or just & followed by # (custom impl)
                    // Let's verify we have 6 chars ahead
                    if (i + 7 < length) {
                        String hexCandidate = text.substring(i + 2, i + 8);
                        if (HEX_PATTERN.matcher("#" + hexCandidate).matches()) {
                            newColor = "#" + hexCandidate;
                            i += 7; // Skip # and 6 chars
                        }
                    }
                } else {
                    // Formatting codes
                    switch (code) {
                        case 'l': case 'L':
                            flush(root, buffer, state);
                            state.bold = true;
                            isFormat = true;
                            break;
                        case 'o': case 'O':
                            flush(root, buffer, state);
                            state.italic = true;
                            isFormat = true;
                            break;
                        case 'r': case 'R':
                            flush(root, buffer, state);
                            state.reset();
                            isReset = true;
                            break;
                        // Hytale Message API snippet provided implies no public setters for these yet:
                        case 'n': case 'N': // Underline
                        case 'm': case 'M': // Strikethrough
                        case 'k': case 'K': // Obfuscated
                            // TODO: Uncomment if Message API exposes these methods
                            // flush(root, buffer, state);
                            // state.underlined = true;
                            // isFormat = true;
                            break;
                    }
                    if (isFormat || isReset) {
                        i++; // Skip code char
                    }
                }

                // If we found a color
                if (newColor != null) {
                    flush(root, buffer, state);
                    state.color = newColor;

                    // CUSTOM BEHAVIOR:
                    // Standard MC: Color resets styles.
                    // Requested Fix: Color keeps styles.
                    // So we do NOT reset boolean flags here.
                } else if (!isFormat && !isReset) {
                    // Not a valid code, treat '&' as literal text
                    buffer.append(current);
                }

                continue;
            } else if (current == '#') {
                // Support raw usage of #RRGGBB without '&' prefix?
                // The original code had explicit Regex processing for this.
                // Let's handle it inline.
                if (i + 6 < length) {
                    String hexCandidate = text.substring(i + 1, i + 7);
                    Matcher m = HEX_PATTERN.matcher("#" + hexCandidate);
                    if (m.matches()) {
                        flush(root, buffer, state);
                        state.color = "#" + hexCandidate;
                        // Hex color change also preserves style in this implementation
                        i += 6;
                        continue;
                    }
                }
            }

            buffer.append(current);
        }

        // Flush remaining text
        flush(root, buffer, state);

        return root;
    }

    /**
     * Creates a Message component from the buffer and current state, appends it to root, and clears buffer.
     */
    private static void flush(Message root, StringBuilder buffer, StyleState state) {
        if (buffer.isEmpty()) return;

        Message part = Message.raw(buffer.toString());

        if (state.color != null) {
            part.color(state.color);
        }
        if (state.bold) {
            part.bold(true);
        }
        if (state.italic) {
            part.italic(true);
        }
        // Note: Missing public setters for underline/obfuscated in provided Message class

        root.insert(part);
        buffer.setLength(0);
    }

    /**
     * Internal state tracker for styles.
     */
    private static class StyleState {
        String color = null; // null = default/white
        boolean bold = false;
        boolean italic = false;
        // boolean underlined = false;
        // boolean strikethrough = false;
        // boolean obfuscated = false;

        void reset() {
            color = null;
            bold = false;
            italic = false;
            // underlined = false;
            // strikethrough = false;
            // obfuscated = false;
        }
    }
}