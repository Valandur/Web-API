package io.valandur.webapi.forge.world;

import io.valandur.webapi.forge.ForgeWebAPI;
import io.valandur.webapi.world.Block;
import io.valandur.webapi.world.CreateWorldData;
import io.valandur.webapi.world.World;
import io.valandur.webapi.world.WorldConstants;
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
    public World getWorld(String worldName) {
        return null;
    }

    @Override
    public WorldConstants getConstants() {
        return null;
    }

    @Override
    public World createWorld(CreateWorldData data) {
        return null;
    }

    @Override
    public void deleteWorld(String worldName) {

    }

    @Override
    public void loadWorld(String worldName) {

    }

    @Override
    public void unloadWorld(String worldName) {

    }

    @Override
    public Block getBlockAt(String worldName, int x, int y, int z) {
        return null;
    }

    @Override
    public void setBlockAt(String worldName, int x, int y, int z, Block block) {

    }
}
