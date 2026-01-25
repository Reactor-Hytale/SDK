package codes.reactor.sdk.event.executor;

import java.lang.invoke.MethodHandle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hypixel.hytale.event.ICancellable;
import lombok.RequiredArgsConstructor;
import codes.reactor.sdk.event.EventExecutor;
import codes.reactor.sdk.event.special.Cancellable;

@RequiredArgsConstructor
public final class ListenerMethodHandleExecutor implements EventExecutor {

    private final Logger logger;
    private final Object listener;
    private final boolean ignoreCancelled;
    private final MethodHandle methodHandle;

    @Override
    public void execute(final Object event) {
        if (event instanceof Cancellable cancellable && cancellable.isCancelled() && !ignoreCancelled) {
            return;
        }
        if (event instanceof ICancellable cancellable && cancellable.isCancelled() && !ignoreCancelled) {
            return;
        }
        try {
            methodHandle.invoke(listener, event);
        } catch (final Throwable e) {
            logger.log(Level.SEVERE, "Error executing the listener " + listener.getClass(), e);
        }
    }
}