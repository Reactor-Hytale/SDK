package codes.reactor.sdk.message.minimessage;

import com.hypixel.hytale.server.core.Message;

import java.util.List;

public interface MiniTag {

    /**
     * @param fullComponent The component with text inside of tag, example: "<b>test" is "test"
     * @param args The arg 0 is the prefix, example: "bold" or "b". Next are args splitted by ':'. Example: "<color:arg1:arg2>"
     * @param output A list of all output chat components
     */
    void parse(
        Message fullComponent,
        List<String> args,
        List<Message> output
    );

    /**
     * @param nextComponent The next component to be added
     */
    void onClose(Message nextComponent);

    /**
     * {@code true} Executes once and discard
     * {@code false} Executes in next components until the tag is closed
     */
    default boolean autoCloseableTag() {
        return false;
    }
}