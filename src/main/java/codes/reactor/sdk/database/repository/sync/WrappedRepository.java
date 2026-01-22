package codes.reactor.sdk.database.repository.sync;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class WrappedRepository<ID, T> implements Repository<ID, T> {
    private final Repository<ID, T> repository;

    @Override
    public boolean exists(@NotNull final ID id) {
        return repository.exists(id);
    }

    @Override
    public T save(@NotNull final ID id, @NotNull final T entity) {
        return repository.save(id, entity);
    }

    @Override
    public void saveAll(@NotNull final Collection<Map.Entry<ID, T>> collection) {
        repository.saveAll(collection);
    }

    @Override
    public @Nullable T find(@NotNull final ID id) {
        return repository.find(id);
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public List<T> findAll(final List<String> whitelistFields) {
        return repository.findAll(whitelistFields);
    }

    @Override
    public List<T> findByCriteria(@NotNull final Map<String, Object> criteria) {
        return repository.findByCriteria(criteria);
    }

    @Override
    public void delete(@NotNull final ID id) {
        repository.delete(id);
    }

    @Override
    public long count() {
        return repository.count();
    }
}