package io.valandur.webapi.sponge.hook;

import io.leangen.geantyref.TypeToken;
import io.valandur.webapi.hook.HookConfig;
import io.valandur.webapi.hook.CommandHook;
import io.valandur.webapi.hook.EventHook;
import io.valandur.webapi.sponge.SpongeWebAPIPlugin;
import io.valandur.webapi.sponge.config.SpongeConfig;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SpongeHookConfig extends SpongeConfig implements HookConfig {

    private static final TypeToken<List<EventHook>> EVENTS_TYPE = new TypeToken<>() {
    };

    private static final TypeToken<List<CommandHook>> COMMANDS_TYPE = new TypeToken<>() {
    };

    public SpongeHookConfig(SpongeWebAPIPlugin plugin) {
        super(plugin, "hooks.conf", Collections.singletonMap(TypeToken.get(EventHook.class), new EventHookSerializer()));
    }

    @Override
    public Collection<EventHook> getEventHooks() {
        return get("events", EVENTS_TYPE, Collections.emptyList());
    }

    @Override
    public Collection<CommandHook> getCommandHooks() {
        return get("commands", COMMANDS_TYPE, Collections.emptyList());
    }
}
