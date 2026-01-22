package codes.reactor.sdk.database.provider.mongodb;

import codes.reactor.sdk.database.provider.DatabaseProvider;
import codes.reactor.sdk.database.repository.sync.Repository;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import lombok.RequiredArgsConstructor;
import org.bson.Document;

@RequiredArgsConstructor
final class MongoDBProvider implements DatabaseProvider {

    private final MongoClient client;

    @Override
    public void close() {
        client.close();
    }

    @Override
    public <ID, T> Repository<ID, T> acquire(final Class<T> entityClass, final String databaseName) {
        return new MongoDBRepository<>(client.getDatabase(databaseName).getCollection(entityClass.getName(), entityClass));
    }

    @Override
    public void verifyConnection(final String databaseName) {
        try {
            client.getDatabase(databaseName).runCommand(new Document("ping", 1));
        } catch (MongoException e) {
            throw new IllegalStateException("MongoDB ping failed for database '" + databaseName + "'", e);
        }
    }
}