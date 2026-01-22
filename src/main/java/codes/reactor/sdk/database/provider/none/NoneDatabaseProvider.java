package codes.reactor.sdk.database.provider.none;

import codes.reactor.sdk.database.provider.DatabaseProvider;
import codes.reactor.sdk.database.repository.sync.Repository;

public final class NoneDatabaseProvider implements DatabaseProvider {
    @Override
    public void close() {}

    @Override
    public <ID, T> Repository<ID, T> acquire(final Class<T> entityClass, final String databaseName) {
        return new NoneRepository<>();
    }
}
