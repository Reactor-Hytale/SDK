package codes.reactor.sdk.lang;

import codes.reactor.sdk.util.PlaceholderReplacement;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

@Data
@AllArgsConstructor
public final class MultiLang {

    private final Function<CommandSender, String> localeFunction;
    private final Function<String, Message> messageFunction;

    private Map<String, Lang> langMap;
    private Lang defaultLang;

    public void send(final String key, final CommandSender sender) {
        final String message = getLang(sender).get(key);
        if (message != null) {
            sender.sendMessage(messageFunction.apply(message));
        }
    }

    public void send(final String key, final CommandSender sender, final Object... replacements) {
        final String message = get(key, sender);
        if (message == null) {
            return;
        }
        sender.sendMessage(messageFunction.apply(PlaceholderReplacement.applyReplacements(message, replacements)));
    }

    public void send(final String key, final Collection<CommandSender> senders, final Object... replacements) {
        for (final CommandSender sender : senders) {
            send(key, sender, replacements);
        }
    }

    public String get(final String key, final CommandSender sender) {
        return getLang(sender).get(key);
    }

    public Lang getLang(final CommandSender sender) {
        if (langMap.size() == 1) {
            return defaultLang;
        }
        final Lang lang = langMap.get(localeFunction.apply(sender));
        return (lang == null) ? defaultLang : lang;
    }

}