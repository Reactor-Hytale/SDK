package codes.reactor.sdk.message.minimessage.tag;

import codes.reactor.sdk.message.ChatColor;
import codes.reactor.sdk.message.minimessage.MiniMessage;
import com.hypixel.hytale.server.core.Message;

public final class StyleTags {

    public static void registerTags() {
        MiniMessage.registerTag(
            (component) -> component.bold(true),
            (nextComponent) -> nextComponent.bold(false),
            true,
            "bold", "b");

        MiniMessage.registerTag(
            (component) -> component.italic(true),
            (nextComponent) -> nextComponent.italic(false),
            true,
            "italic", "i");

        MiniMessage.registerTag( // Not supported by hytale
            (_) -> {},
            (_) -> {},
            true,
            "obfuscated", "o");

        MiniMessage.registerTag( // Not supported by hytale
            (_) -> {},
            (_) -> {},
            true,
            "strikethrough", "s");

        MiniMessage.registerTag( // Not supported by hytale
            (_) -> {},
            (_) -> {},
            true,
            "underlined", "u");

        MiniMessage.registerTag(
            StyleTags::resetComponent,
            StyleTags::resetComponent,
            true,
            "reset", "r");
    }

    private static void resetComponent(Message component) {
        component.color(ChatColor.WHITE.getHex());
        component.italic(false);
        component.bold(false);
        component.monospace(false);
    }
}