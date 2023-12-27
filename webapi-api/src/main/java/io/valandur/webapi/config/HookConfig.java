package io.valandur.webapi.config;

import io.valandur.webapi.hook.CommandHook;
import io.valandur.webapi.hook.EventHook;
import java.util.Collection;

public interface HookConfig extends Config {
  Collection<EventHook> getEventHooks();
  Collection<CommandHook> getCommandHooks();
}
