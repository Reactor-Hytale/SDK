package codes.reactor.sdk.config.service;

import codes.reactor.sdk.config.exception.UnsupportedConfigFormatException;
import codes.reactor.sdk.config.service.yaml.YamlConfigService;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public final class ConfigServiceRegistry {
    private static final Map<String, ConfigService> SERVICES = new HashMap<>();

    static {
        addService(new YamlConfigService());
    }

    public static ConfigService getService(final String configFormat) {
        final ConfigService provider = SERVICES.get(configFormat);
        if (provider == null) {
            throw new UnsupportedConfigFormatException(configFormat);
        }
        return provider;
    }

    public static void addService(final ConfigService service) {
        for (final String fileExtension : service.fileExtensions()) {
            SERVICES.put(fileExtension, service);
        }
    }
}