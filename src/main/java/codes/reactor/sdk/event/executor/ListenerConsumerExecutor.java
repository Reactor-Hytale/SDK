package codes.reactor.sdk.event.executor;

import java.util.function.Consumer;

import lombok.RequiredArgsConstructor;
import codes.reactor.sdk.event.EventExecutor;
import codes.reactor.sdk.event.special.Cancellable;

@RequiredArgsConstructor
public final class ListenerConsumerExecutor<T> implements EventExecutor {
    private final Consumer<T> consumer;

    @Override
    @SuppressWarnings("unchecked")
    public void execute(final Object event) {
        if (event instanceof Cancellable cancellable && cancellable.isCancelled()) {
            return;
        }
        consumer.accept((T)event);
    }
}