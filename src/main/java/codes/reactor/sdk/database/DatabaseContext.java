package codes.reactor.sdk.database;

import codes.reactor.sdk.database.repository.async.AsyncRepository;
import codes.reactor.sdk.database.repository.sync.Repository;

import java.util.concurrent.ExecutorService;

public record DatabaseContext(String databaseName) {

    public ExecutorService getExecutorService() {
        return CommonDatabase.getInstance().getRepositoryFactory().getExecutor();
    }

    public <ID, T> Repository<ID, T> getRepository(Class<T> clazz) {
        return CommonDatabase.getInstance().getRepositoryFactory().createSync(clazz, databaseName);
    }

    public <ID, T> AsyncRepository<ID, T> getAsyncRepository(Class<T> clazz) {
        return CommonDatabase.getInstance().getRepositoryFactory().createAsync(clazz, databaseName);
    }
}