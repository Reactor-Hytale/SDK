package codes.reactor.sdk.event.special;

public interface Cancellable {
    boolean isCancelled();
    void setCancelled(final boolean state);
}