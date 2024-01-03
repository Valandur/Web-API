package io.valandur.webapi.forge.world;

import io.valandur.webapi.forge.ForgeWebAPI;
import io.valandur.webapi.world.Block;
import io.valandur.webapi.world.World;
import io.valandur.webapi.world.WorldService;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class ForgeWorldService extends WorldService<ForgeWebAPI> {

    public ForgeWorldService(ForgeWebAPI webapi) {
        super(webapi);
    }

    @Override
    public Collection<World> getWorlds() {
        return Collections.emptySet();
    }

    @Override
    public Block getBlockAt(UUID worldId, int x, int y, int z) {
        return null;
    }

    @Override
    public void setBlockAt(UUID worldId, int x, int y, int z, Block block) {

    }
}
