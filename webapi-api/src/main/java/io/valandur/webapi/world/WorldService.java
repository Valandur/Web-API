package io.valandur.webapi.world;

import io.valandur.webapi.Service;
import io.valandur.webapi.WebAPI;

import java.util.Collection;

public abstract class WorldService<T extends WebAPI<?, ?>> extends Service<T> {

    public WorldService(T webapi) {
        super(webapi);
    }

    public abstract Collection<World> getWorlds();

    public abstract Block getBlockAt(String world, int x, int y, int z);

    public abstract void setBlockAt(String world, int x, int y, int z, Block block);
}
