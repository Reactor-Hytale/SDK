package codes.reactor.sdk.lang;

import codes.reactor.sdk.config.ConfigServiceByContext;
import codes.reactor.sdk.config.section.ConfigSection;
import codes.reactor.sdk.config.service.ConfigService;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@RequiredArgsConstructor
@Setter
public final class LangLoader {
    private final @NotNull HytaleLogger logger;
    private final @NotNull ConfigService configService;
    private final @NotNull ClassLoader classLoader;

    private final @NotNull Function<CommandSender, String> localeFunction;
    private final @NotNull Function<String, Message> messageFunction;

    private final @NotNull File folder;
    private final @NotNull String defaultLangFile;

    public MultiLang load() {
        if (!folder.exists()) {
            return createDefaultLang();
        }

        final Map<String, Lang> langMap = new HashMap<>();

        final File[] files = folder.listFiles();
        if (files == null) {
            return emptyLang();
        }

        for (final File langFile : files) {
            loadLangFile(langFile, langMap);
        }

        if (langMap.isEmpty()) {
            return emptyLang();
        }

        final String defaultLangName = defaultLangFile.split("\\.")[0];
        Lang defaultLang = langMap.get(defaultLangName);
        if (defaultLang == null) {
            logger.atWarning().log("Can't found the default lang " + defaultLangName + ". Available Lang: " + langMap.keySet());
            defaultLang = langMap.values().iterator().next();
        }
        return new MultiLang(localeFunction, messageFunction, langMap, defaultLang);
    }

    private void loadLangFile(File langFile, Map<String, Lang> langMap) {
        final String fileName = langFile.getName();
        for (final String fileExtension : configService.fileExtensions()) {
            if (!fileName.endsWith(fileExtension)) {
                logger.atWarning().log("The lang file " + fileName + " isn't supported. Available file extensions: " + configService.fileExtensions());
                return;
            }
        }

        final ConfigSection langConfig = configService.load(langFile.toPath());

        final Lang minecraftLang = loadLang(langConfig);
        langMap.put(fileName.substring(0, fileName.indexOf('.')), minecraftLang);

        final List<String> aliases = langConfig.getStringList("aliases");

        for (final String alias : aliases) {
            langMap.put(alias, minecraftLang);
        }
    }

    private @NotNull MultiLang createDefaultLang() {
        final ConfigSection defaultLangSection;
        final String resourcePath = folder.getName() + "/" + defaultLangFile;
        final File outFile = new File(folder, defaultLangFile);

        try {
            if (configService instanceof ConfigServiceByContext byContext) {
                defaultLangSection = byContext.createIfAbsentAndLoad(resourcePath);
            } else {
                defaultLangSection = configService.createIfAbsentAndLoad(resourcePath, classLoader);
            }
        } catch (IOException e) {
            logger.atSevere().withCause(e).log("Error on create or load the file " + outFile);
            return emptyLang();
        }
        return new MultiLang(localeFunction, messageFunction, new HashMap<>(), loadLang(defaultLangSection));
    }

    private @NotNull MultiLang emptyLang() {
        return new MultiLang(localeFunction, messageFunction, new HashMap<>(), new Lang());
    }

    private Lang loadLang(final ConfigSection lang) {
        final ConfigSection messages = lang.getSection("messages");
        if (messages == null) {
            return new Lang(new HashMap<>());
        }
        return new Lang(getSectionMessage("", messages.getData().entrySet(), new HashMap<>()));
    }

    private Map<String, String> getSectionMessage(
        final String prefixKey,
        final Set<? extends Map.Entry<?, ?>> entries,
        final Map<String, String> messages
    ) {
        for (final Map.Entry<?, ?> entry : entries) {
            final String key = prefixKey + entry.getKey();

            if (entry.getValue() instanceof List<?> list) {
                messages.put(key, listToString(list));
                continue;
            }

            if (entry.getValue() instanceof ConfigSection configSection) {
                getSectionMessage(key + '.', configSection.getData().entrySet(), messages);
                continue;
            }

            if (entry.getValue() instanceof Map<?,?> map) {
                getSectionMessage(key + '.', map.entrySet(), messages);
                continue;
            }

            messages.put(key, entry.getValue().toString());
        }
        return messages;
    }

    private String listToString(final List<?> list) {
        final StringBuilder builder = new StringBuilder();
        final int size = list.size();
        int i = 0;
        for (final Object line : list) {
            builder.append(line);
            if (++i != size) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }
}