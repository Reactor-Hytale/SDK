package codes.reactor.sdk.database.provider.none;

import codes.reactor.sdk.database.repository.sync.Repository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class NoneRepository<ID, T> implements Repository<ID, T> {
    @Override
    public boolean exists(@NotNull final ID id) {
        return false;
    }

    @Override
    public T save(@NotNull final ID id, @NotNull final T entity) {
        return null;
    }

    @Override
    public void saveAll(@NotNull final Collection<Map.Entry<ID, T>> collection) {}

    @Override
    public @Nullable T find(@NotNull final ID id) {
        return null;
    }

    @Override
    public List<T> findAll() {
        return List.of();
    }

    @Override
    public List<T> findAll(final List<String> whitelistFields) {
        return List.of();
    }

    @Override
    public List<T> findByCriteria(@NotNull final Map<String, Object> criteria) {
        return List.of();
    }

    @Override
    public void delete(@NotNull final ID id) {}

    @Override
    public long count() {
        return 0;
    }
}
