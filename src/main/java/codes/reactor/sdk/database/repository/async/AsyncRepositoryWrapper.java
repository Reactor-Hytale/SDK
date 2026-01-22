package codes.reactor.sdk.database.repository.async;

import codes.reactor.sdk.database.repository.sync.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class AsyncRepositoryWrapper<ID, T> implements AsyncRepository<ID, T> {

    private final Repository<ID, T> delegate;
    private final ExecutorService executor;

    public AsyncRepositoryWrapper(Repository<ID, T> delegate, ExecutorService executor) {
        this.delegate = delegate;
        this.executor = executor;
    }

    @Override
    public CompletableFuture<Boolean> exists(ID id) {
        return CompletableFuture.supplyAsync(() -> delegate.exists(id), executor);
    }

    @Override
    public CompletableFuture<T> save(ID id, T entity) {
        return CompletableFuture.supplyAsync(() -> delegate.save(id, entity), executor);
    }

    @Override
    public CompletableFuture<Void> saveAll(Map<ID, T> data) {
        return CompletableFuture.runAsync(() -> delegate.saveAll(data.entrySet()), executor);
    }

    @Override
    public CompletableFuture<T> find(ID id) {
        return CompletableFuture.supplyAsync(() -> delegate.find(id), executor);
    }

    @Override
    public CompletableFuture<List<T>> findAll() {
        return CompletableFuture.supplyAsync(delegate::findAll, executor);
    }

    @Override
    public CompletableFuture<List<T>> findAll(final List<String> whitelistFields) {
        return CompletableFuture.supplyAsync(() -> delegate.findAll(whitelistFields), executor);
    }

    @Override
    public CompletableFuture<List<T>> findByCriteria(Map<String, Object> criteria) {
        return CompletableFuture.supplyAsync(() -> delegate.findByCriteria(criteria), executor);
    }

    @Override
    public CompletableFuture<Void> delete(ID id) {
        return CompletableFuture.runAsync(() -> delegate.delete(id), executor);
    }

    @Override
    public CompletableFuture<Long> count() {
        return CompletableFuture.supplyAsync(delegate::count, executor);
    }
}
