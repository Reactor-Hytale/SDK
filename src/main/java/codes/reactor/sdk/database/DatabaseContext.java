package codes.reactor.sdk.database;

import codes.reactor.sdk.database.repository.RepositoryFactory;
import codes.reactor.sdk.database.repository.async.AsyncRepository;
import codes.reactor.sdk.database.repository.sync.Repository;

public record DatabaseContext(String databaseName, RepositoryFactory repositoryFactory) {

    public <ID, T> Repository<ID, T> getRepository(Class<T> clazz) {
        return repositoryFactory.createSync(clazz, databaseName);
    }

    public <ID, T> AsyncRepository<ID, T> getAsyncRepository(Class<T> clazz) {
        return repositoryFactory.createAsync(clazz, databaseName);
    }
}