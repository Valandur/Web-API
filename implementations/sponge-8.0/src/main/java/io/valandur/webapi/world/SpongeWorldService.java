package io.valandur.webapi.world;

import io.valandur.webapi.SpongeWebAPI;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.WebApplicationException;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.server.WorldManager;

import java.util.ArrayList;
import java.util.Collection;

public class SpongeWorldService extends WorldService<SpongeWebAPI> {

    private final WorldManager worldManager;

    public SpongeWorldService(SpongeWebAPI webapi) {
        super(webapi);

        this.worldManager = Sponge.server().worldManager();
    }

    @Override
    public Collection<World> getWorlds() {
        var worlds = new ArrayList<World>();
        for (var world : worldManager.worlds()) {
            worlds.add(this.toWorld(world));
        }
        return worlds;
    }

    @Override
    public Block getBlockAt(String world, int x, int y, int z) {
        var key = ResourceKey.resolve(world);
        var entry = RegistryTypes.WORLD_TYPE.get().findEntry(key);
        if (entry.isEmpty()) {
            throw new BadRequestException("Invalid world type: " + world);
        }

        var optWorld = worldManager.world(key);
        if (optWorld.isEmpty()) {
            throw new Error("World not found: " + world);
        }

        var serverWorld = optWorld.get();
        var block = serverWorld.block(x, y, z);
        return this.toBlock(block);
    }

    @Override
    public void setBlockAt(String world, int x, int y, int z, Block block) {
        var key = ResourceKey.resolve(world);
        var entry = RegistryTypes.WORLD_TYPE.get().findEntry(key);
        if (entry.isEmpty()) {
            throw new BadRequestException("Invalid world type: " + world);
        }

        var optWorld = worldManager.world(key);
        if (optWorld.isEmpty()) {
            throw new Error("World not found: " + world);
        }

        var serverWorld = optWorld.get();
        var blockState = this.fromBlock(block);
        var success = serverWorld.setBlock(x, y, z, blockState);
        if (!success) {
            throw new Error("Could not set block");
        }
    }


    private World toWorld(org.spongepowered.api.world.server.ServerWorld world) {
        var props = world.properties();
        var gameRules = new ArrayList<GameRule>();
        for (var gameRule : props.gameRules().entrySet()) {
            Object value = null;
            var rawValue = gameRule.getValue().toString();
            if (rawValue.equalsIgnoreCase("true")) {
                value = true;
            } else if (rawValue.equalsIgnoreCase("false")) {
                value = false;
            }
            if (value == null) {
                try {
                    value = Integer.parseInt(rawValue);
                } catch (NumberFormatException ignored) {
                }
            }
            if (value == null) {
                try {
                    value = Double.parseDouble(rawValue);
                } catch (NumberFormatException ignored) {
                }
            }
            gameRules.add(new GameRule(gameRule.getKey().toString(), value));
        }

        return new World(
                world.properties().worldType().key(RegistryTypes.WORLD_TYPE).asString(),
                props.displayName().map(Object::toString).orElse(null),
                world.difficulty().toString(),
                world.seed(),
                gameRules
        );
    }

    private Block toBlock(BlockState block) {
        return new Block(block.type().key(RegistryTypes.BLOCK_TYPE).asString());
    }

    private BlockState fromBlock(Block block) throws WebApplicationException {
        var type = this.fromType(block.type);
        return BlockState.builder().blockType(type).build();
    }

    private BlockType fromType(String type) {
        var key = ResourceKey.resolve(type);
        var entry = RegistryTypes.BLOCK_TYPE.get().findEntry(key);
        if (entry.isEmpty()) {
            throw new BadRequestException("Invalid block type: " + type);
        }

        return entry.get().value();
    }
}
