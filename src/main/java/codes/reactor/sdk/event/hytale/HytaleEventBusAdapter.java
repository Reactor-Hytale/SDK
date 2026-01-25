package codes.reactor.sdk.event.hytale;

import codes.reactor.sdk.event.EventBus;
import codes.reactor.sdk.event.EventExecutor;
import codes.reactor.sdk.event.ListenerPhase;
import codes.reactor.sdk.event.executor.ListenerConsumerExecutor;
import codes.reactor.sdk.event.loader.MethodListenerLoader;
import codes.reactor.sdk.event.simplebus.EventStorage;
import codes.reactor.sdk.event.simplebus.RegisteredListener;
import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.event.IAsyncEvent;
import com.hypixel.hytale.event.IBaseEvent;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.HytaleServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Logger;

public final class HytaleEventBusAdapter implements EventBus {

    public static final com.hypixel.hytale.event.EventBus HYTALE_EVENT_BUS = HytaleServer.get().getEventBus();
    private static volatile HytaleEventBusAdapter instance;

    private final HytaleLogger logger;
    private final MethodListenerLoader methodListenerLoader;

    private final Map<Class<?>, EventStorage> eventStorageMap;
    private final Map<Object, Collection<RegisteredListener>> owners;

    private HytaleEventBusAdapter(final @NotNull HytaleLogger logger) {
        this.logger = logger;
        this.methodListenerLoader = new MethodListenerLoader(Logger.getLogger("ReactorEventBus-MethodLoader"));
        this.eventStorageMap = new ConcurrentHashMap<>();
        this.owners = new ConcurrentHashMap<>();

        logger.atInfo().log("HytaleEventBusAdapter initialized successfully");
    }

    public static HytaleEventBusAdapter getInstance() {
        if (instance == null) {
            synchronized (HytaleEventBusAdapter.class) {
                if (instance == null) {
                    instance = new HytaleEventBusAdapter(HytaleLogger.get("ReactorEventBus"));
                }
            }
        }
        return instance;
    }

    @Override
    public void register(final @NotNull Object listener) {
        final Collection<MethodListenerLoader.MethodListener> methodListeners = methodListenerLoader.load(listener);

        if (methodListeners.isEmpty()) {
            return;
        }

        final Collection<RegisteredListener> listeners = owners.computeIfAbsent(listener, _ -> new ArrayList<>(methodListeners.size()));

        for (final MethodListenerLoader.MethodListener methodListener : methodListeners) {
            final Class<?> eventClass = methodListener.eventClass();
            final RegisteredListener registeredListener = new RegisteredListener(
                methodListener.executor(),
                eventClass,
                methodListener.phase(),
                methodListener.priority()
            );

            listeners.add(registeredListener);
            registerListenerIntoStorage(eventClass, eventStorageMap.get(eventClass), registeredListener);
        }
    }

    private void registerListenerIntoStorage(
        final @NotNull Class<?> eventClass,
        @Nullable EventStorage storage,
        final @NotNull RegisteredListener registeredListener
    ) {
        if (IAsyncEvent.class.isAssignableFrom(eventClass)) {
            registerAsyncEventIntoStorage(storage, eventClass, registeredListener);
            return;
        }
        if (IBaseEvent.class.isAssignableFrom(eventClass)) {
            registerBaseEventIntoStorage(storage, eventClass, registeredListener);
            return;
        }

        if (storage == null) {
            storage = new EventStorage();
            eventStorageMap.put(eventClass, storage);
        }
        storage.addListener(registeredListener);
    }

    private void registerAsyncEventIntoStorage(
        @Nullable EventStorage storage,
        final @NotNull Class<?> eventClass,
        final @NotNull RegisteredListener registeredListener
    ) {
        if (storage == null) {
            storage = new EventStorage();
            final EventStorage immutableStorage = storage;
            eventStorageMap.put(eventClass, storage);

            HYTALE_EVENT_BUS.registerGlobal(
                EventPriority.FIRST,
                (Class) eventClass,
                immutableStorage::execute
            );
        }

        storage.addListener(registeredListener);
    }

    private void registerBaseEventIntoStorage(
        @Nullable EventStorage storage,
        final @NotNull Class<?> eventClass,
        final @NotNull RegisteredListener registeredListener
    ) {
        if (storage == null) {
            storage = new EventStorage();
            eventStorageMap.put(eventClass, storage);

            final @NotNull EventStorage finalStorage = storage;
            HYTALE_EVENT_BUS.registerGlobal(
                EventPriority.FIRST,
                (Class<? super IBaseEvent<?>>) eventClass,
                finalStorage::execute
            );
        }

        storage.addListener(registeredListener);
    }

    @Override
    public <T> void register(final @NotNull Class<T> eventClass, final @NotNull Consumer<T> listener) {
        register(listener, eventClass, ListenerPhase.DEFAULT, new ListenerConsumerExecutor<>(listener));
    }

    @Override
    public void register(
        final @NotNull Object listener,
        final @NotNull Class<?> eventClass,
        final @NotNull ListenerPhase phase,
        final @NotNull EventExecutor executor
    ) {
        final EventStorage storage = eventStorageMap.computeIfAbsent(eventClass, _ -> new EventStorage());

        final RegisteredListener registeredListener = new RegisteredListener(executor, eventClass, phase, 0);
        registerListenerIntoStorage(eventClass, storage, registeredListener);

        final Collection<RegisteredListener> listeners = owners.computeIfAbsent(listener, _ -> new ArrayList<>(1));

        listeners.add(registeredListener);
    }

    @Override
    public void unregister(final @NotNull Object listener) {
        final Collection<RegisteredListener> listeners = owners.remove(listener);
        if (listeners == null) {
            return;
        }

        for (final RegisteredListener registeredListener : listeners) {
            final EventStorage eventStorage = eventStorageMap.get(registeredListener.eventClass());
            if (eventStorage != null) {
                eventStorage.remove(registeredListener);
            }
        }
    }

    @Override
    public void post(final @NotNull Object event) {
        if (event instanceof IAsyncEvent<?> asyncEvent) {
            HYTALE_EVENT_BUS.dispatchForAsync((Class) asyncEvent.getClass(), null).dispatch(asyncEvent);
            return;
        }

        if (event instanceof IBaseEvent<?> baseEvent) {
            HYTALE_EVENT_BUS.dispatchFor((Class) baseEvent.getClass(), null).dispatch(baseEvent);
            return;
        }

        final EventStorage eventStorage = eventStorageMap.get(event.getClass());
        if (eventStorage != null) {
            eventStorage.execute(event);
        }
    }

    @Override
    public void clear() {
        owners.clear();
        eventStorageMap.values().forEach(EventStorage::clear);
    }
}