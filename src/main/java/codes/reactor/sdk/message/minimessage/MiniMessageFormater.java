package codes.reactor.sdk.message.minimessage;

import codes.reactor.sdk.util.StringUtil;
import com.hypixel.hytale.server.core.Message;
import static codes.reactor.sdk.message.minimessage.TagTokenizer.TokenType.*;
import static codes.reactor.sdk.message.minimessage.TagTokenizer.Token;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class MiniMessageFormater {

    static List<Message> format(final String text, final Map<String, MiniTag> tags) {
        final List<TagTokenizer.Token> tokens = TagTokenizer.tokenize(text);
        final List<Message> components = new ArrayList<>();
        final Deque<OpenTag> openTags = new ArrayDeque<>();

        Message component = Message.empty();

        for (final Token token : tokens) {
            if (token.type() == TEXT) {
                component.insert(token.value());
                applyTags(component, openTags, components);
                components.add(component);
                component = Message.empty();
                continue;
            }

            final boolean isCloseTag = token.value().charAt(1) == '/';
            final String tagContent = token.value().substring(isCloseTag ? 2 : 1, token.value().length() - 1); // Remove '<', '</' and '>'
            final List<String> args = StringUtil.split(tagContent, ':');
            final MiniTag messageTag = tags.get(args.getFirst());

            if (messageTag == null) {
                components.add(Message.raw(token.value()));
                continue;
            }

            if (!isCloseTag) {
                openTags.push(new OpenTag(messageTag, args));
                continue;
            }

            messageTag.onClose(component);
            closeTag(openTags, tagContent, component);
        }
        return components;
    }

    private static void applyTags(final Message component, final Deque<OpenTag> openTags, final List<Message> components) {
        final Iterator<OpenTag> iterator = openTags.iterator();

        while (iterator.hasNext()) {
            final OpenTag openTag = iterator.next();
            openTag.tag.parse(component, openTag.args, components);

            if (openTag.tag.autoCloseableTag()) {
                iterator.remove();
            }
        }
    }

    private static void closeTag(final Deque<OpenTag> openTags, final String tagContent, final Message component) {
        final Iterator<OpenTag> iterator = openTags.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().args.getFirst().equals(tagContent)) {
                iterator.remove();
                break;
            }
        }
    }

    private static record OpenTag(
        MiniTag tag,
        List<String> args
    ) {}
}