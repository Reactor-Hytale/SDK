package codes.reactor.sdk.database.repository;

import codes.reactor.sdk.database.repository.async.AsyncRepository;
import codes.reactor.sdk.database.repository.async.AsyncRepositoryWrapper;
import codes.reactor.sdk.database.repository.sync.Repository;
import lombok.Setter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class DefaultRepositoryFactory implements RepositoryFactory {

    @Setter
    private ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    private final RepositoryProvider delegate;

    public DefaultRepositoryFactory(RepositoryProvider delegate) {
        this.delegate = delegate;
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

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }
}