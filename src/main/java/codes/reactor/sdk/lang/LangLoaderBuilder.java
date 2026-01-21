package codes.reactor.sdk.lang;

import codes.reactor.sdk.config.ConfigServiceByContext;
import codes.reactor.sdk.message.ChatLegacy;
import codes.reactor.sdk.message.minimessage.MiniMessage;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.entity.entities.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.function.Function;

public class LangLoaderBuilder {

    // TODO: Hytale don't support multilang right now :(
    // public static final Function<CommandSender, String> PER_PLAYER_LOCALE_FUNCTION = sender ->
    //    sender instanceof Player player ? player.getPlayerRef().getLanguage() : null;

    public static final Function<String, Message> MINIMESSAGE_FUNCTION = MiniMessage::format;
    public static final Function<String, Message> LEGACY_FUNCTION = ChatLegacy::fromLegacy;

    private @NotNull Function<CommandSender, String> localeFunction = (_) -> "en_US";
    private @NotNull Function<String, Message> messageFunction = MINIMESSAGE_FUNCTION;

    private @NotNull String langFolder = "lang";
    private @NotNull String defaultLangFile = "en_US.yml";

    public LangLoaderBuilder localeFunction(final @NotNull Function<CommandSender, String> localeFunction) {
        this.localeFunction = localeFunction;
        return this;
    }

    public LangLoaderBuilder useLocale(final @NotNull String locale) {
        this.localeFunction = (_) -> locale;
        return this;
    }

    public LangLoaderBuilder messageFunction(final @NotNull Function<String, Message> messageFunction) {
        this.messageFunction = messageFunction;
        return this;
    }

    public LangLoaderBuilder useMiniMessageFormat() {
        this.messageFunction = MINIMESSAGE_FUNCTION;
        return this;
    }

    public LangLoaderBuilder useLegacyFormat() {
        this.messageFunction = LEGACY_FUNCTION;
        return this;
    }

    public LangLoaderBuilder langFolder(final @NotNull String folder) {
        this.langFolder = folder;
        return this;
    }

    public LangLoaderBuilder defaultLangFile(final @NotNull String defaultLangFile) {
        this.defaultLangFile = defaultLangFile;
        return this;
    }

    public LangLoader build(final ConfigServiceByContext configServiceByContext) {
        return new LangLoader(
            configServiceByContext.getContext().logger(),
            configServiceByContext,
            configServiceByContext.getContext().classLoader(),

            localeFunction,
            messageFunction,

            new File(langFolder),
            defaultLangFile
        );
    }
}