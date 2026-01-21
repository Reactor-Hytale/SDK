package codes.reactor.sdk.plugin;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import org.jetbrains.annotations.NotNull;

public abstract class ReactorPlugin extends JavaPlugin {

    protected final @NotNull PluginContext context;

    public ReactorPlugin(@NotNull final JavaPluginInit init) {
        super(init);
        context = new PluginContext(
            getName(),
            getFile(),
            getLogger(),
            getClassLoader()
        );
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    @Override
    protected void shutdown0(final boolean shutdown) {
        super.shutdown0(shutdown);
    }
}
