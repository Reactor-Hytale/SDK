package codes.reactor.sdk.database.repository;

import codes.reactor.sdk.database.provider.DatabaseProvider;
import codes.reactor.sdk.database.repository.sync.Repository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ProviderBackedRepositoryProvider implements RepositoryProvider {
    private final DatabaseProvider provider;

    @Override
    public <ID, T> Repository<ID, T> create(Class<T> entityClass, String databaseName) {
        return provider.acquire(entityClass, databaseName);
    }
}