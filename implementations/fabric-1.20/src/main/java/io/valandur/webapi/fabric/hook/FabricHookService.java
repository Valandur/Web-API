package io.valandur.webapi.fabric.hook;

import io.valandur.webapi.fabric.FabricWebAPI;
import io.valandur.webapi.hook.HookService;

public class FabricHookService extends HookService<FabricWebAPI> {
    public FabricHookService(FabricWebAPI webapi) {
        super(webapi);
    }
}
