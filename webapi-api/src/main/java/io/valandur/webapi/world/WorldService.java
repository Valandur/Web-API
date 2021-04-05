package io.valandur.webapi.world;

import io.valandur.webapi.Service;
import io.valandur.webapi.WebAPI;

import java.util.Collection;
import java.util.UUID;

public abstract class WorldService<T extends WebAPI<?, ?>> extends Service<T> {

    public WorldService(T webapi) {
        super(webapi);
    }

    public abstract Collection<World> getWorlds();

    public abstract Block getBlockAt(String worldType, int x, int y, int z);

    public abstract void setBlockAt(String worldType, int x, int y, int z, Block block);
}
