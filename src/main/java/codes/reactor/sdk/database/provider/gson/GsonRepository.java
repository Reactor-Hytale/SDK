package codes.reactor.sdk.database.provider.gson;

import codes.reactor.sdk.database.repository.sync.Repository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
final class GsonRepository<ID, T> implements Repository<ID, T> {

    private final Gson gson;
    private final Class<T> entityType;
    private final Path entityFolder;

    private Path fileOf(ID id) {
        return entityFolder.resolve(id.toString() + ".json");
    }

    @Override
    public boolean exists(@NotNull ID id) {
        return Files.exists(fileOf(id));
    }

    @Override
    public T save(@NotNull ID id, @NotNull T entity) {
        try {
            Files.writeString(fileOf(id), gson.toJson(entity), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return entity;
        } catch (IOException e) {
            throw new IllegalStateException("Error writing entity file " + id, e);
        }
    }

    @Override
    public void saveAll(@NotNull Collection<Map.Entry<ID, T>> collection) {
        for (var entry : collection) {
            save(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public @Nullable T find(@NotNull ID id) {
        Path file = fileOf(id);
        if (!Files.exists(file)) return null;

        try {
            String json = Files.readString(file);
            return gson.fromJson(json, entityType);
        } catch (IOException e) {
            throw new IllegalStateException("Error reading entity file " + id, e);
        }
    }

    @Override
    public List<T> findAll() {
        try (Stream<Path> pathStream = Files.list(entityFolder)) {
            return pathStream.filter(p -> p.toString().endsWith(".json"))
                .map(path -> {
                    try {
                        return gson.fromJson(Files.readString(path), entityType);
                    } catch (IOException e) {
                        throw new IllegalStateException("Error reading file: " + path, e);
                    }
                }).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("Error listing entity folder", e);
        }
    }

    @Override
    public List<T> findAll(List<String> whitelistFields) {
        // No projection support -> just return full objects
        return findAll();
    }

    @Override
    public List<T> findByCriteria(@NotNull Map<String, Object> criteria) {
        if (criteria.isEmpty()) return findAll();

        return findAll().stream().filter(entity -> {
            for (var entry : criteria.entrySet()) {
                try {
                    var field = entity.getClass().getDeclaredField(entry.getKey());
                    field.setAccessible(true);
                    Object value = field.get(entity);

                    if (!Objects.equals(value, entry.getValue())) {
                        return false;
                    }
                } catch (ReflectiveOperationException ignored) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    @Override
    public void delete(@NotNull ID id) {
        try {
            Files.deleteIfExists(fileOf(id));
        } catch (IOException e) {
            throw new IllegalStateException("Error deleting entity file " + id, e);
        }
    }

    @Override
    public long count() {
        try(Stream<Path> listFiles = Files.list(entityFolder)) {
            return listFiles.filter(f -> f.toString().endsWith(".json")).count();
        } catch (IOException e) {
            throw new IllegalStateException("Error counting entity files", e);
        }
    }
}
