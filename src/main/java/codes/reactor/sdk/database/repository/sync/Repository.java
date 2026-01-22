package codes.reactor.sdk.database.repository.sync;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Repository<ID, T> {

    boolean exists(@NotNull ID id);

    T save(@NotNull ID id, @NotNull T entity);
    void saveAll(@NotNull Collection<Map.Entry<ID, T>> collection);

    @Nullable T find(@NotNull ID id);

    List<T> findAll();
    List<T> findAll(final List<String> whitelistFields);
    List<T> findByCriteria(@NotNull Map<String, Object> criteria);

    void delete(@NotNull ID id);

    long count();
}