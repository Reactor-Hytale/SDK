package codes.reactor.sdk.event;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface EventBus {
    /**
     * Register all methods that use {@link Listener} as annotation
     * @param listener object with listener methods
     */
    void register(final @NotNull Object listener);

    /**
     * Register a simple listener
     * @param eventClass event to hear
     * @param listener consumer of the event
     */
    <T> void register(final @NotNull Class<T> eventClass, final @NotNull Consumer<T> listener);

    /**
     * Register a listener with custom event executor,
     * all process of executing the listener is handled by the event-executor
     * @param listener instance of the listener to register
     * @param eventClass event to hear
     * @param phase listener phase
     * @param executor executor of the listener
     */
    void register(final @NotNull Object listener, final @NotNull Class<?> eventClass,
                  final @NotNull ListenerPhase phase, final @NotNull EventExecutor executor);

    /**
     * @param listener object with listener methods
     */
    void unregister(final @NotNull Object listener);

    /**
     * Execute all listeners with the same event-class
     * @param event Custom event (can be any object)
     */
    void post(final @NotNull Object event);

    /**
     * Remove all listener from eventbus
     */
    void clear();
}