package codes.reactor.sdk.color;

import codes.reactor.sdk.message.ChatColor;
import codes.reactor.sdk.message.util.ColorConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CYMKConvertorTest {

    // https://encycolorpedia.com/f0c05a
    private static final float
        MIMOSA_CYAN = 0,
        MIMOSA_YELLOW = 0.625F,
        MIMOSA_MAGENTA = 0.2F,
        MIMOSA_KEY = 0.059F;

    private static final ChatColor MIMOSA_RGB = ChatColor.rgb(240, 192, 90);

    @Test
    public void compareCYMK() {
        final int[] rgb = ColorConverter.cymkToRgb(MIMOSA_CYAN, MIMOSA_YELLOW, MIMOSA_MAGENTA, MIMOSA_KEY);
        Assertions.assertEquals(MIMOSA_RGB, ChatColor.rgb(rgb[0], rgb[1], rgb[2]));
    }
}