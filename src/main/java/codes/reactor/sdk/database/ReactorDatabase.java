package codes.reactor.sdk.database;

import codes.reactor.sdk.database.provider.DatabaseProvider;
import codes.reactor.sdk.database.repository.DefaultRepositoryFactory;
import codes.reactor.sdk.database.repository.ProviderBackedRepositoryProvider;
import codes.reactor.sdk.database.repository.RepositoryFactory;
import lombok.Getter;
import lombok.NonNull;

public final class ReactorDatabase {
    private final DatabaseProvider provider;

    @Getter
    private final RepositoryFactory repositoryFactory;
    @Getter
    private final @NonNull String contextPrefix;

    private ReactorDatabase(DatabaseProvider provider, final @NonNull String contextPrefix) {
        this.provider = provider;
        this.repositoryFactory = new DefaultRepositoryFactory(new ProviderBackedRepositoryProvider(provider));
        this.contextPrefix = contextPrefix;
    }

    public DatabaseContext getContext(String databaseName) {
        return new DatabaseContext(contextPrefix + databaseName);
    }

    public void shutdown() {
        provider.close();
    }
}
