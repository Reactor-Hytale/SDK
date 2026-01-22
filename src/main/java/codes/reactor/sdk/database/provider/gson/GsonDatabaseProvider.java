package codes.reactor.sdk.database.provider.gson;

import codes.reactor.sdk.database.provider.DatabaseProvider;
import codes.reactor.sdk.database.repository.sync.Repository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
final class GsonDatabaseProvider implements DatabaseProvider {

    private final Path basePath;
    private final Gson gson;

    @Override
    public void close() {
        // No resources to release
    }

    @Override
    public <ID, T> Repository<ID, T> acquire(Class<T> entityClass, String databaseName) {
        try {
            Path dbPath = basePath.resolve(databaseName).toAbsolutePath();
            Files.createDirectories(dbPath);

            Path entityFolder = dbPath.resolve(entityClass.getName());
            Files.createDirectories(entityFolder);

            return new GsonRepository<>(gson, entityClass, entityFolder);

        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize Gson repository", e);
        }
    }

    @Override
    public void verifyConnection(String databaseName) {
        if (!Files.exists(basePath)) {
            throw new IllegalStateException("Database folder does not exist: " + basePath);
        }
    }
}
