package codes.reactor.sdk.event.loader;

import lombok.RequiredArgsConstructor;
import codes.reactor.sdk.event.EventExecutor;
import codes.reactor.sdk.event.Listener;
import codes.reactor.sdk.event.ListenerPhase;
import codes.reactor.sdk.event.executor.ListenerMethodHandleExecutor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public final class MethodListenerLoader {

    private final Logger logger;

    public List<MethodListener> load(final Object object) {
        final Class<?> sourceClass = object.getClass();
        final Method[] methods = sourceClass.getDeclaredMethods();
        if (methods.length == 0) {
            logger.warning("The class " + sourceClass + " don't contains any method");
            return List.of();
        }

        final List<MethodListener> listeners = new ArrayList<>(methods.length);

        for (final Method method : methods) {
            final Listener listener = method.getAnnotation(Listener.class);
            if (listener == null) {
                continue;
            }

            if (method.getParameterCount() != 1) {
                logger.warning("Error trying to load the listener " + method.getName() +" in the class " + sourceClass + ". The method need be exactly 1 parameter");
                continue;
            }

            final Class<?> firstParameter = method.getParameterTypes()[0];
            final MethodHandle methodHandle;
            try {
                methodHandle = MethodHandles.publicLookup().unreflect(method);
            } catch (final IllegalAccessException e) {
                logger.log(Level.SEVERE, "Error trying to load the listener " + method.getName() + " in the class " + sourceClass, e);
                continue;
            }

            listeners.add(new MethodListener(
                firstParameter,
                listener.phase(),
                listener.priority(),
                new ListenerMethodHandleExecutor(logger, object, listener.ignoreCancelled(), methodHandle)));
        }

        return listeners;
    }

    public record MethodListener(
        Class<?> eventClass,
        ListenerPhase phase,
        int priority,
        EventExecutor executor
    ){}
}
