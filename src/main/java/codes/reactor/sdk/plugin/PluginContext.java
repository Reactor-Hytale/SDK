package codes.reactor.sdk.plugin;

import com.hypixel.hytale.logger.HytaleLogger;

import java.nio.file.Path;

public record PluginContext(
    String name,
    Path path,
    HytaleLogger logger,
    ClassLoader classLoader
) {
}
