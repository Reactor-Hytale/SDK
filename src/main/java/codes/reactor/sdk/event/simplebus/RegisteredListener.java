package codes.reactor.sdk.event.simplebus;

import codes.reactor.sdk.event.EventExecutor;
import codes.reactor.sdk.event.ListenerPhase;

public record RegisteredListener(
    EventExecutor executor,
    Class<?> eventClass,
    ListenerPhase phase,
    int priority
) {}