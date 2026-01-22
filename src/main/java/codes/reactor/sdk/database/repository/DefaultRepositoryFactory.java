package codes.reactor.sdk.database.repository;

import codes.reactor.sdk.database.repository.async.AsyncRepository;
import codes.reactor.sdk.database.repository.async.AsyncRepositoryWrapper;
import codes.reactor.sdk.database.repository.sync.Repository;
import lombok.Getter;

import java.util.concurrent.ExecutorService;

public final class DefaultRepositoryFactory implements RepositoryFactory {

    @Getter
    private final ExecutorService executor;

    private final RepositoryProvider delegate;

    public DefaultRepositoryFactory(RepositoryProvider delegate, ExecutorService executor) {
        this.delegate = delegate;
        this.executor = executor;
    }

    @Override
    public <ID, T> Repository<ID, T> createSync(Class<T> entityClass, String databaseName) {
        return delegate.create(entityClass, databaseName);
    }

    @Override
    public <ID, T> AsyncRepository<ID, T> createAsync(Class<T> entityClass, String databaseName) {
        Repository<ID, T> syncRepo = createSync(entityClass, databaseName);
        return new AsyncRepositoryWrapper<>(syncRepo, executor);
    }

}