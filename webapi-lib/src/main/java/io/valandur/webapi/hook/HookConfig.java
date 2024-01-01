package io.valandur.webapi.hook;

import io.valandur.webapi.Config;

import java.util.Collection;

public interface HookConfig extends Config {

    String name = "hooks";

    Collection<EventHook> getEventHooks();

    Collection<CommandHook> getCommandHooks();
}
