package codes.reactor.sdk.util.currency;

import lombok.experimental.UtilityClass;

import static codes.reactor.sdk.util.currency.CurrencyDeserializerException.ErrorCode.*;

@UtilityClass
public final class CurrencyDeserializer {

    public static long deserialize(final String string) {
        if (string.isEmpty()) {
            throw new CurrencyDeserializerException(EMPTY_STRING);
        }

        StringBuilder numberBuilder = new StringBuilder();
        long exponent = 1;

        final int length = string.length();
        for (int i = 0; i < length; i++) {
            final char character = string.charAt(i);
            switch (character) {
                case '-', '+' -> throw new CurrencyDeserializerException(FOUND_SIGN_CHARACTER);
                case '_', ' ' -> {} // Delimiter characters
                case '0','1','2','3','4','5','6','7','8','9','.',',' -> numberBuilder.append(character);

                case 'Q', 'q' -> exponent = 1_000_000_000_000_000L;
                case 'T', 't' -> exponent = 1_000_000_000_000L;
                case 'B', 'b' -> exponent = 1_000_000_000L;
                case 'M', 'm' -> exponent = 1_000_000L;
                case 'K', 'k' -> exponent = 1000L;

                default -> throw new CurrencyDeserializerException(String.valueOf(character), FOUND_INVALID_NUMBER);
            }
        }

        return (long) getResult(numberBuilder, exponent);
    }

    private static double getResult(StringBuilder numberBuilder, long exponent) {
        if (numberBuilder.isEmpty()) {
            throw new CurrencyDeserializerException(EMPTY_STRING);
        }

        String normalized = numberBuilder.toString().replace(',', '.');
        double base;
        try {
            base = Double.parseDouble(normalized);
        } catch (NumberFormatException e) {
            throw new CurrencyDeserializerException(FOUND_INVALID_NUMBER);
        }

        double result = base * exponent;

        // Overflow check
        if (result > Long.MAX_VALUE) {
            throw new CurrencyDeserializerException(POSITIVE_OVERFLOW);
        }
        if (result < Long.MIN_VALUE) {
            throw new CurrencyDeserializerException(NEGATIVE_OVERFLOW);
        }
        return result;
    }
}