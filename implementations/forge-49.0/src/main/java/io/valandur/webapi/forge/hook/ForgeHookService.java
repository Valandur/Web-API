package io.valandur.webapi.forge.hook;

import io.valandur.webapi.forge.ForgeWebAPI;
import io.valandur.webapi.hook.HookService;

public class ForgeHookService extends HookService<ForgeWebAPI> {

    public ForgeHookService(ForgeWebAPI webapi) {
        super(webapi);
    }
}
