package codes.reactor.sdk.database.repository;

import codes.reactor.sdk.database.repository.sync.Repository;

public interface RepositoryProvider {
    <ID, T> Repository<ID, T> create(Class<T> entityClass, String databaseName);
}