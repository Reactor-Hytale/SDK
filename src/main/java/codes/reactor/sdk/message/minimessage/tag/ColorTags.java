package codes.reactor.sdk.message.minimessage.tag;

import codes.reactor.sdk.message.ChatColor;
import codes.reactor.sdk.message.minimessage.MiniMessage;
import codes.reactor.sdk.message.minimessage.MiniTag;
import com.hypixel.hytale.server.core.Message;

import java.util.List;

public final class ColorTags {

    public static void registerTags() {
        for (final ChatColor legacyChatColor : ChatColor.LEGACY_COLORS) {
            registerTag(legacyChatColor);
        }
        MiniMessage.registerTag(new ColorTag(), "color");
    }

    private static void registerTag(final ChatColor color) {
        MiniMessage.registerTag(chatColorTag(color), color.getName());
    }

    public static MiniTag chatColorTag(final ChatColor color) {
        return new MiniTag() {
            @Override
            public void parse(Message fullComponent, List<String> args, List<Message> output) {
                fullComponent.color(color.getName());
            }
            @Override
            public void onClose(Message nextComponent) {
                nextComponent.color(ChatColor.WHITE.getName());
            }
            @Override
            public boolean autoCloseableTag() {
                return true;
            }
        };
    }

    private static final class ColorTag implements MiniTag {
        @Override
        public void parse(Message fullComponent, List<String> args, List<Message> output) {
            if (args.get(1).isEmpty()) { // when not color is set <color:>
                fullComponent.color(ChatColor.WHITE.getName());
                return;
            }
            ChatColor color = ChatColor.byLegacyName(fullComponent.getAnsiMessage());
            if (color == null) {
                color = ChatColor.hex(args.get(1));
            }
            fullComponent.color(color.getName());
        }

        @Override
        public void onClose(Message nextComponent) {
            nextComponent.color(ChatColor.WHITE.getName());
        }
    }
}