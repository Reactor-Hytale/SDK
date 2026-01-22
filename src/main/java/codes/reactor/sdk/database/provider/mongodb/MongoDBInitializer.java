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

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@UtilityClass
public final class MongoDBInitializer {

    public static DatabaseProvider init(final ConfigSection config) throws MongoClientException {
        final String user = config.getString("user");
        final String pass = config.getString("password");
        final String host = config.getString("host");
        final String database = config.getString("database");

        final int port = config.getInt("port");
        final int connectionTimeOut = Math.max(1, config.getInt("connect-timeout-seconds"));
        final int readTimeOut = Math.max(1, config.getInt("read-timeout-seconds"));

        if (port < 1024 || port > 49151) {
            throw new MongoConfigurationException("Mongodb port need be 1024 - 49151. See iana registered ports");
        }

        String uri;
        if (config.getBoolean("uri-enable")) {
            uri = Objects.requireNonNull(config.getString("uri"), "Uri can't be null");
        } else {
            uri = "mongodb://" + user + ':' + pass + '@' + host + ':' + port + "/?authSource=" + database;
        }

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
            .applyConnectionString(new ConnectionString(uri))
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

        return new MongoDBProvider(MongoClients.create(settings));
    }
}