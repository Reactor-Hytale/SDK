package codes.reactor.sdk.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.UUID;

@UtilityClass
public final class UUIDUtil {

    /**
     * Convert a string to uuid.
     *
     * @param input String to convert (max length 16)
     * @return string converted to uuid
     */
    public static UUID stringToUUID(final String input) {
        if (input.length() > 16) {
            throw new InvalidParameterException("Input string need be have a length less or equals than 16");
        }

        final byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        final byte[] uuidBytes = new byte[16];

        System.arraycopy(bytes, 0, uuidBytes, 0, input.length());

        long mostSigBits = 0;
        for (int i = 0; i < 8; i++) {
            mostSigBits = (mostSigBits << 8) | (uuidBytes[i] & 0xFF);
        }

        long leastSigBits = 0;
        for (int i = 8; i < 16; i++) {
            leastSigBits = (leastSigBits << 8) | (uuidBytes[i] & 0xFF);
        }
        return new UUID(mostSigBits, leastSigBits);
    }

    public static byte[] uuidToBytes(final @NotNull UUID uuid) {
        long msw = uuid.getMostSignificantBits();
        long lsw = uuid.getLeastSignificantBits();
        return new byte[] {
            (byte)(msw >>> 56),
            (byte)(msw >>> 48),
            (byte)(msw >>> 40),
            (byte)(msw >>> 32),
            (byte)(msw >>> 24),
            (byte)(msw >>> 16),
            (byte)(msw >>> 8),
            (byte)(msw),

            (byte)(lsw >>> 56),
            (byte)(lsw >>> 48),
            (byte)(lsw >>> 40),
            (byte)(lsw >>> 32),
            (byte)(lsw >>> 24),
            (byte)(lsw >>> 16),
            (byte)(lsw >>> 8),
            (byte)(lsw),
        };
    }
}
