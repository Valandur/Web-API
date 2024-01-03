package io.valandur.webapi.hook;

import io.valandur.webapi.Config;

import java.util.Collection;
import java.util.Collections;

public interface HookConfig extends Config {

    Collection<EventHook> defaultEventHooks = Collections.emptySet();

    Collection<EventHook> getEventHooks();

    Collection<CommandHook> defaultCommandHooks = Collections.emptySet();

    Collection<CommandHook> getCommandHooks();
}
