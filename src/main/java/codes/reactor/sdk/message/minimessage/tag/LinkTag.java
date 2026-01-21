package codes.reactor.sdk.message.minimessage.tag;

import codes.reactor.sdk.message.minimessage.MiniTag;
import codes.reactor.sdk.util.StringUtil;
import com.hypixel.hytale.server.core.Message;

import java.util.List;

public final class LinkTag implements MiniTag {

    @Override
    public void parse(final Message fullComponent, final List<String> args, final List<Message> output) {
        fullComponent.link(StringUtil.combine(args));
    }

    @Override
    public void onClose(final Message nextComponent) {
        nextComponent.link("");
    }
}
