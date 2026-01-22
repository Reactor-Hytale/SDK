package codes.reactor.sdk.database.repository;

import codes.reactor.sdk.database.repository.async.AsyncRepository;
import codes.reactor.sdk.database.repository.sync.Repository;

import java.util.concurrent.ExecutorService;

public interface RepositoryFactory {

    <ID, T> Repository<ID, T> createSync(Class<T> entityClass, String databaseName);

    <ID, T> AsyncRepository<ID, T> createAsync(Class<T> entityClass, String databaseName);

    ExecutorService getExecutor();
}