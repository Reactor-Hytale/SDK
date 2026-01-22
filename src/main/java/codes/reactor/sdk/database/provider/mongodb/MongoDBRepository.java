package codes.reactor.sdk.database.provider.mongodb;

import codes.reactor.sdk.database.repository.sync.Repository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
final class MongoDBRepository<ID, T> implements Repository<ID, T> {

    private static final String ID_KEY = "_id";
    private static final ReplaceOptions UPSERT_OPTIONS = new ReplaceOptions().upsert(true);
    private static final CountOptions EXIST_COUNT_OPTIONS = new CountOptions().limit(1);

    private final MongoCollection<T> collection;

    @Override
    public boolean exists(@NotNull final ID id) {
        return collection.countDocuments(Filters.eq(ID_KEY, id), EXIST_COUNT_OPTIONS) > 0;
    }

    @Override
    public T save(@NotNull final ID id, @NotNull final T entity) {
        collection.replaceOne(
            Filters.eq(ID_KEY, id),
            entity,
            UPSERT_OPTIONS
        );
        return entity;
    }

    @Override
    public void saveAll(@NotNull final Collection<Map.Entry<ID, T>> data) {
        final var writes = new ArrayList<WriteModel<T>>(data.size());

        for (final Map.Entry<ID, T> entry : data) {
            writes.add(new ReplaceOneModel<>(
                Filters.eq(ID_KEY, entry.getKey()),
                entry.getValue(),
                UPSERT_OPTIONS
            ));
        }

        if (!writes.isEmpty()) {
            collection.bulkWrite(writes);
        }
    }

    @Override
    public T find(@NotNull final ID id) {
        return collection.find(Filters.eq(ID_KEY, id)).limit(1).first();
    }

    @Override
    public List<T> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    @Override
    public List<T> findAll(final List<String> whitelistFields) {
        return collection.find()
            .projection(Projections.include(whitelistFields))
            .into(new ArrayList<>());
    }

    @Override
    public List<T> findByCriteria(@NotNull final Map<String, Object> criteria) {
        return collection.find(buildFilter(criteria)).into(new ArrayList<>());
    }

    @Override
    public void delete(@NotNull final ID id) {
        collection.deleteOne(Filters.eq(ID_KEY, id));
    }

    @Override
    public long count() {
        return collection.countDocuments();
    }

    private Bson buildFilter(Map<String, Object> criteria) {
        if (criteria == null || criteria.isEmpty()) {
            return new Document();
        }
        return Filters.and(criteria.entrySet().stream()
            .map(e -> Filters.eq(e.getKey(), e.getValue()))
            .toList());
    }
}