package codes.reactor.sdk.database.provider;

import codes.reactor.sdk.config.section.ConfigSection;
import codes.reactor.sdk.database.provider.gson.GsonDatabaseInitializer;
import codes.reactor.sdk.database.provider.mongodb.MongoDBInitializer;
import codes.reactor.sdk.database.provider.none.NoneDatabaseProvider;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Logger;

@RequiredArgsConstructor
public final class ProviderLoader {

    private final Logger logger;

    public DatabaseProvider load(final @NotNull ConfigSection config) {
        final String type = config.getString("type");
        if (type == null) {
            logger.warning("Database type isn't specified");
            return null;
        }

        return switch(type.toLowerCase()) {
            case "mongo", "mongodb" -> MongoDBInitializer.init(Objects.requireNonNull(config.getSection("mongodb")));
            case "gson", "json" -> GsonDatabaseInitializer.init(Objects.requireNonNull(config.getSection("gson")));
            case "none", "null" -> new NoneDatabaseProvider();
            default -> {
                logger.warning("Can't found the database provider " + type);
                yield null;
            }
        };
    }
}