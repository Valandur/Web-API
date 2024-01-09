package io.valandur.webapi.world;

import io.valandur.webapi.Service;
import io.valandur.webapi.WebAPI;
import java.util.Collection;

public abstract class WorldService<T extends WebAPI<?, ?>> extends Service<T> {

    public WorldService(T webapi) {
        super(webapi);
    }

    public abstract Collection<World> getWorlds();

    public abstract WorldConstants getConstants();

    public abstract World createWorld(CreateWorldData data);

    public abstract World getWorld(String worldName);

    public abstract void deleteWorld(String worldName);

    public abstract void loadWorld(String worldName);

    public abstract void unloadWorld(String worldName);

    public abstract Block getBlockAt(String worldName, int x, int y, int z);

    public abstract void setBlockAt(String worldName, int x, int y, int z, Block block);
}
