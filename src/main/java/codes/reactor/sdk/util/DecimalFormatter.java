package codes.reactor.sdk.util;

import lombok.experimental.UtilityClass;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@UtilityClass
public final class DecimalFormatter {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.GERMANY));
    private static final DecimalFormat LONG_CURRENCY_FORMAT = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.US));

    public static String formatCurrency(final long amount) {
        if (amount < 1000L) {
            return String.valueOf(amount);
        }
        if (amount < 1_000_000L) {
            return formatNumber(amount / 1000D) + 'K';
        }
        if (amount < 1_000_000_000L) {
            return formatNumber(amount / 1_000_000D) + 'M';
        }
        if (amount < 1_000_000_000_000L) {
            return formatNumber(amount / 1_000_000_000D) + 'B';
        }
        if (amount < 1_000_000_000_000_000L) {
            return formatNumber(amount / 1_000_000_000_000D) + 'T';
        }
        return formatNumber(amount / 1_000_000_000_000_000D) + 'Q';
    }

    public static String formatCurrencyLong(final long amount) {
        return LONG_CURRENCY_FORMAT.format(amount);
    }

    public static String formatNumber(double value) {
        final String formatted = DECIMAL_FORMAT.format(value);

        if (formatted.endsWith(".0")) {
            return formatted.substring(0, formatted.length() - 2);
        }

        return formatted;
    }
}