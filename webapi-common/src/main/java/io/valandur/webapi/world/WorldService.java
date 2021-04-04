package io.valandur.webapi.world;

import io.valandur.webapi.WebAPI;
import io.valandur.webapi.logger.Logger;

import java.util.Collection;

public abstract class WorldService {

    protected WebAPI<?> webapi;
    protected Logger logger;

    public WorldService(WebAPI<?> webapi) {
        this.webapi = webapi;
        this.logger = webapi.getLogger();
    }

    public abstract Collection<World> getWorlds();
}
