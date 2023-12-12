package io.valandur.webapi.sponge.world;

import io.valandur.webapi.sponge.SpongeWebAPI;
import io.valandur.webapi.world.Block;
import io.valandur.webapi.world.GameRule;
import io.valandur.webapi.world.World;
import io.valandur.webapi.world.WorldService;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.server.WorldManager;

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
  public Block getBlockAt(UUID worldId, int x, int y, int z) {
    var optWorld = worldManager.worldKey(worldId).flatMap(worldManager::world);
    if (optWorld.isEmpty()) {
      throw new BadRequestException("World not found: " + worldId);
    }

    var world = optWorld.get();
    var block = world.block(x, y, z);
    return this.toBlock(block);
  }

  @Override
  public void setBlockAt(UUID worldId, int x, int y, int z, Block block) {
    var optWorld = worldManager.worldKey(worldId).flatMap(worldManager::world);
    if (optWorld.isEmpty()) {
      throw new BadRequestException("World not found: " + worldId);
    }

    var world = optWorld.get();
    var blockState = this.fromBlock(block);
    var success = world.setBlock(x, y, z, blockState);
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
        world.uniqueId(),
        world.properties().worldType().key(RegistryTypes.WORLD_TYPE).asString(),
        props.displayName().map(txt -> PlainTextComponentSerializer.plainText().serialize(txt))
            .orElse(null),
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
