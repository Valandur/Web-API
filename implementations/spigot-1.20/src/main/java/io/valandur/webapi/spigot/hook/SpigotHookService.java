package io.valandur.webapi.spigot.hook;

import io.valandur.webapi.hook.HookService;
import io.valandur.webapi.spigot.SpigotWebAPI;

public class SpigotHookService extends HookService<SpigotWebAPI> {
    public SpigotHookService(SpigotWebAPI webapi) {
        super(webapi);
    }
}
