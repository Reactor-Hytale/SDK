package codes.reactor.sdk.event;

public interface EventExecutor {
    void execute(final Object event);
}