package io.valandur.webapi.world;

import io.valandur.webapi.Service;
import io.valandur.webapi.WebAPI;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public abstract class WorldService<T extends WebAPI<?, ?>> extends Service<T> {

    public WorldService(T webapi) {
        super(webapi);
    }

    public abstract Collection<World> getWorlds();

    public abstract WorldConstants getConstants();

    public abstract World createWorld(CreateWorldData data);

    public abstract void deleteWorld(UUID worldId);

    public abstract World loadWorld(UUID worldId);

    public abstract void unloadWorld(UUID worldId);

    public abstract Block getBlockAt(UUID worldId, int x, int y, int z);

    public abstract void setBlockAt(UUID worldId, int x, int y, int z, Block block);
}
