package io.valandur.webapi.fabric.config;

import io.valandur.webapi.hook.HookConfig;
import io.valandur.webapi.hook.CommandHook;
import io.valandur.webapi.hook.EventHook;

import java.util.Collection;
import java.util.Collections;

public class FabricHookConfig implements HookConfig {
    @Override
    public void save() throws Exception {

    }

    @Override
    public void load() throws Exception {

    }

    @Override
    public Collection<EventHook> getEventHooks() {
        return Collections.emptySet();
    }

    @Override
    public Collection<CommandHook> getCommandHooks() {
        return Collections.emptySet();
    }
}
