package codes.reactor.sdk.database.repository.async;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AsyncRepository<ID, T> {
    CompletableFuture<Boolean> exists(ID id);
    CompletableFuture<T> save(ID id, T entity);
    CompletableFuture<Void> saveAll(Map<ID, T> data);
    CompletableFuture<T> find(ID id);
    CompletableFuture<List<T>> findAll();
    CompletableFuture<List<T>> findAll(final List<String> whitelistFields);
    CompletableFuture<List<T>> findByCriteria(Map<String, Object> criteria);
    CompletableFuture<Void> delete(ID id);
    CompletableFuture<Long> count();
}