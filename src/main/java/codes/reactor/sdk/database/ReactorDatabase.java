package codes.reactor.sdk.database;

import codes.reactor.sdk.database.provider.DatabaseProvider;
import codes.reactor.sdk.database.repository.DefaultRepositoryFactory;
import codes.reactor.sdk.database.repository.ProviderBackedRepositoryProvider;
import codes.reactor.sdk.database.repository.RepositoryFactory;
import lombok.Getter;

public final class ReactorDatabase {
    private final DatabaseProvider provider;

    @Getter
    private final RepositoryFactory repositoryFactory;

    private ReactorDatabase(DatabaseProvider provider) {
        this.provider = provider;
        this.repositoryFactory = new DefaultRepositoryFactory(new ProviderBackedRepositoryProvider(provider));
    }

    public DatabaseContext getContext(String databaseName) {
        return new DatabaseContext(databaseName);
    }

    public void shutdown() {
        provider.close();
    }
}
