package codes.reactor.sdk.lang;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class Lang {
    private final Map<String, String> messages;

    public Lang() {
        messages = new HashMap<>();
    }

    public Lang(final Map<String, String> messages) {
        this.messages = messages;
    }

    public String get(final String key) {
        return messages.get(key);
    }

    public String getFormatted(final String key, final Object... format) {
        final String message = messages.get(key);
        if (message == null) {
            return null;
        }
        return String.format(message, format);
    }

    public void send(final String key, final CommandSender sender) {
        final String message = get(key);
        if (message != null) {
            sender.sendMessage(Message.raw(message));
        }
    }

    public void sendFormatted(final String key, final CommandSender sender, final Object... format) {
        final String message = getFormatted(key, format);
        if (message != null) {
            sender.sendMessage(Message.raw(message));
        }
    }

    @Override
    public String toString() {
        return messages.toString();
    }
}