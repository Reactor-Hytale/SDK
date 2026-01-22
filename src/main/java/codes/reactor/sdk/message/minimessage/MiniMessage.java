package codes.reactor.sdk.message.minimessage;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import codes.reactor.sdk.message.minimessage.tag.ColorTags;
import codes.reactor.sdk.message.minimessage.tag.LinkTag;
import codes.reactor.sdk.message.minimessage.tag.StyleTags;
import com.hypixel.hytale.server.core.Message;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class MiniMessage {

    public static final Map<String, MiniTag> GLOBAL_TAGS = new Object2ObjectOpenHashMap<>();

    static {
        ColorTags.registerTags();
        StyleTags.registerTags();
        registerTag(new LinkTag(), "link");
    }

    public static List<Message> formatList(final String text) {
        return formatList(text, GLOBAL_TAGS);
    }

    public static Message format(final String text) {
        return format(text, GLOBAL_TAGS);
    }

    public static List<Message> formatList(final String text, final Map<String, MiniTag> tags) {
        return MiniMessageFormater.format(text, tags);
    }

    public static Message format(final String text, final Map<String, MiniTag> tags) {
        return Message.empty().insertAll(formatList(text, tags));
    }

    public static void registerTag(final MiniTag tag, final String... aliases) {
        for (final String alias : aliases) {
            GLOBAL_TAGS.put(alias, tag);
        }
    }

    public static void registerTag(final Consumer<Message> onAdd, final Consumer<Message> onClose, final boolean autoCloseable, final String... aliases) {
        final MiniTag tag = new MiniTag() {
            public void parse(Message fullComponent, List<String> args, List<Message> output) {
                onAdd.accept(fullComponent);
            }

            public void onClose(Message nextComponent) {
                onClose.accept(nextComponent);
            }

            @Override
            public boolean autoCloseableTag() {
                return autoCloseable;
            }
        };
        for (final String alias : aliases) {
            GLOBAL_TAGS.put(alias, tag);
        }
    }
}