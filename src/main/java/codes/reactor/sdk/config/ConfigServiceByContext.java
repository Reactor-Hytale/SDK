package codes.reactor.sdk.config;

import codes.reactor.sdk.config.section.ConfigSection;
import codes.reactor.sdk.config.service.ConfigService;
import codes.reactor.sdk.config.service.ConfigServiceRegistry;
import codes.reactor.sdk.plugin.PluginContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;

@RequiredArgsConstructor
public class ConfigServiceByContext implements ConfigService {

    private final Path directory;
    private final ConfigService service;

    @Getter
    private final PluginContext context;

    public static ConfigServiceByContext from(final String format, final PluginContext context) {
        return from(ConfigServiceRegistry.getService(format), context);
    }

    public static ConfigServiceByContext from(final ConfigService configService, final PluginContext context) {
        return new ConfigServiceByContext(
            context.path(),
            configService,
            context
        );
    }

    @Override
    public ConfigSection load(final Path path) {
        final Path resolved = resolveUnderPluginDir(path);
        return service.load(resolved);
    }

    @Override
    public void save(final Path path, final ConfigSection section, final SaveOptions options) {
        final Path resolved = resolveUnderPluginDir(path);
        ensureParent(resolved);
        service.save(resolved, section, options);
    }

    @Override
    public ConfigSection createIfAbsentAndLoad(
        final String fileName,
        final Path outDestination,
        final ClassLoader classLoader
    ) throws IOException {
        Objects.requireNonNull(fileName, "fileName");
        Objects.requireNonNull(outDestination, "outDestination");
        final Path resolvedDest = resolveUnderPluginDir(outDestination.isAbsolute()
            ? outDestination
            : directory.resolve(outDestination));
        ensureParent(resolvedDest);
        return service.createIfAbsentAndLoad(fileName, resolvedDest, classLoader);
    }

    public ConfigSection createIfAbsentAndLoad(final String fileName) throws IOException {
        final Path dest = directory.resolve(fileName);
        ensureParent(dest);
        return service.createIfAbsentAndLoad(fileName, dest, context.classLoader());
    }

    @Override
    public Collection<String> fileExtensions() {
        return service.fileExtensions();
    }

    private Path resolveUnderPluginDir(final Path path) {
        if (path == null) {
            throw new IllegalArgumentException("path == null");
        }
        if (path.isAbsolute()) {
            return path.normalize();
        }
        return directory.resolve(path).normalize();
    }

    private void ensureParent(final Path target) {
        final Path parent = target.getParent();
        if (parent != null) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new UncheckedIOException("Can't create the directory: " + parent, e);
            }
        }
    }
}