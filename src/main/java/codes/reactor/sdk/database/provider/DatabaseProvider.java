package codes.reactor.sdk.database.provider;

import codes.reactor.sdk.database.repository.sync.Repository;

public interface DatabaseProvider {
    void close();

    <ID, T> Repository<ID, T> acquire(final Class<T> entityClass, final String databaseName);

    default void verifyConnection(String databaseName) {}
}