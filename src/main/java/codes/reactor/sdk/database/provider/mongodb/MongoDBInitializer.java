package codes.reactor.sdk.database.provider.mongodb;

import codes.reactor.sdk.config.section.ConfigSection;
import codes.reactor.sdk.database.provider.DatabaseProvider;
import com.mongodb.*;
import com.mongodb.client.MongoClients;
import lombok.experimental.UtilityClass;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@UtilityClass
public final class MongoDBInitializer {

    public static DatabaseProvider init(final ConfigSection config) throws MongoClientException {

        final int connectionTimeOut = Math.max(1, config.getInt("connect-timeout-seconds"));
        final int readTimeOut = Math.max(1, config.getInt("read-timeout-seconds"));

        final String uri = getUri(config);
        final ConnectionString connectionString = new ConnectionString(uri);

        final ServerApi serverApi = ServerApi.builder()
            .version(ServerApiVersion.V1)
            .build();

        final CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(
            PojoCodecProvider.builder()
                .conventions(Arrays.asList(
                    Conventions.ANNOTATION_CONVENTION,
                    Conventions.SET_PRIVATE_FIELDS_CONVENTION
                ))
                .automatic(true)
                .build()
        );

        final MongoClientSettings settings = MongoClientSettings.builder()
            .serverApi(serverApi)
            .applyConnectionString(connectionString)
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .codecRegistry(CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                pojoCodecRegistry
            ))
            .applyToSocketSettings(builder -> builder
                .connectTimeout(connectionTimeOut, TimeUnit.SECONDS)
                .readTimeout(readTimeOut, TimeUnit.SECONDS)
            )
            .build();

        final MongoDBProvider mongoDBProvider = new MongoDBProvider(MongoClients.create(settings));

        final String databaseName = resolveDatabaseName(config, connectionString);
        mongoDBProvider.verifyConnection(databaseName);

        return mongoDBProvider;
    }

    private static @NotNull String getUri(ConfigSection config) {
        if (config.getBoolean("uri-enable")) {
            return Objects.requireNonNull(config.getString("uri"), "Uri can't be null");
        }

        final int port = config.getInt("port");
        if (port < 1024 || port > 49151) {
            throw new MongoConfigurationException("Mongodb port need be 1024 - 49151. See iana registered ports");
        }

        final String user = config.getString("user");
        final String pass = config.getString("password");
        final String host = config.getString("host");
        final String database = config.getString("database");
        return "mongodb://" + user + ':' + pass + '@' + host + ':' + port + "/?authSource=" + database;
    }

    private static @NotNull String resolveDatabaseName(
        final ConfigSection config,
        final ConnectionString connectionString
    ) {
        if (connectionString.getDatabase() != null) {
            return connectionString.getDatabase();
        }

        String database = config.getString("database");
        if (database != null && !database.isBlank()) {
            return database;
        }

        throw new MongoConfigurationException(
            "No database specified. Define it in the MongoDB URI or in config.database"
        );
    }
}