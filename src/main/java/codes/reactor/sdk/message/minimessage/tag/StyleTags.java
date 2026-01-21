package codes.reactor.sdk.message.minimessage.tag;

import codes.reactor.sdk.message.ChatColor;
import codes.reactor.sdk.message.minimessage.MiniMessage;

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

        MiniMessage.registerTag( // Not native support by hytale
            (component) -> component.color(ChatColor.WHITE.getName()),
            (nextComponent) -> nextComponent.color(ChatColor.WHITE.getName()),
            true,
            "reset", "r");
    }
}