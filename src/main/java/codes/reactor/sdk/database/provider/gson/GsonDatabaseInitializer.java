package codes.reactor.sdk.database.provider.gson;

import codes.reactor.sdk.config.section.ConfigSection;
import codes.reactor.sdk.database.provider.DatabaseProvider;
import codes.reactor.sdk.database.provider.gson.type.UUIDTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@UtilityClass
public final class GsonDatabaseInitializer {

    public static DatabaseProvider init(final ConfigSection config) {
        final String basePathStr = config.getString("path");
        if (basePathStr == null || basePathStr.isBlank()) {
            throw new IllegalArgumentException("Missing 'path' configuration for Gson database provider");
        }

        final Path basePath = Path.of(basePathStr).toAbsolutePath().normalize();

        try {
            if (Files.exists(basePath) && !Files.isDirectory(basePath)) {
                throw new IllegalStateException("Configured 'path' exists but is not a directory: " + basePath);
            }
            Files.createDirectories(basePath);

            if (!Files.isWritable(basePath)) {
                throw new IllegalStateException("Configured 'path' is not writable: " + basePath);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to prepare base path for Gson provider: " + basePath, e);
        }

        final Gson gson = new GsonBuilder()
            .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
            .setPrettyPrinting()
            .create();

        return new GsonDatabaseProvider(basePath, gson);
    }
}