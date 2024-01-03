package io.valandur.webapi.forge.hook;

import io.valandur.webapi.forge.config.ForgeConfig;
import io.valandur.webapi.hook.CommandHook;
import io.valandur.webapi.hook.EventHook;
import io.valandur.webapi.hook.HookConfig;

import java.util.Collection;

public class ForgeHookConfig extends ForgeConfig implements HookConfig {

    public ForgeHookConfig() {
        super("hooks.toml");

        build();
    }

    @Override
    public Collection<EventHook> getEventHooks() {
        return defaultEventHooks;
    }

    @Override
    public Collection<CommandHook> getCommandHooks() {
        return defaultCommandHooks;
    }
}
