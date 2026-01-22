package codes.reactor.sdk.database;

import codes.reactor.sdk.database.provider.DatabaseProvider;
import codes.reactor.sdk.database.repository.DefaultRepositoryFactory;
import codes.reactor.sdk.database.repository.ProviderBackedRepositoryProvider;
import codes.reactor.sdk.database.repository.RepositoryFactory;
import lombok.Getter;
import lombok.NonNull;

public final class CommonDatabase {

    private static CommonDatabase instance;
    private final DatabaseProvider provider;

    @Getter
    private final RepositoryFactory repositoryFactory;
    @Getter
    private final @NonNull String contextPrefix;

    private CommonDatabase(DatabaseProvider provider, final @NonNull String contextPrefix) {
        this.provider = provider;
        this.repositoryFactory = new DefaultRepositoryFactory(new ProviderBackedRepositoryProvider(provider));
        this.contextPrefix = contextPrefix;
    }

    public static void setProvider(final @NonNull DatabaseProvider provider, final @NonNull String contextPrefix) {
        if (instance != null) {
            instance.provider.close();
        }
        instance = new CommonDatabase(provider, contextPrefix);
    }

    public static CommonDatabase getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ReactorDatabaseAPI not initialized.");
        }
        return instance;
    }

    public DatabaseContext getContext(String databaseName) {
        return new DatabaseContext(contextPrefix + databaseName);
    }

    public static void shutdown() {
        if (instance != null) {
            instance.provider.close();
            instance = null;
        }
    }
}
