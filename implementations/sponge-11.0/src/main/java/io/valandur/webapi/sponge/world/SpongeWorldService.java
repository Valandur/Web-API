package io.valandur.webapi.sponge.world;

import io.valandur.webapi.sponge.SpongeWebAPI;
import io.valandur.webapi.world.Block;
import io.valandur.webapi.world.CreateWorldData;
import io.valandur.webapi.world.GameRule;
import io.valandur.webapi.world.World;
import io.valandur.webapi.world.WorldConstants;
import io.valandur.webapi.world.WorldService;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.NotFoundException;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.WorldTypes;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.generation.config.WorldGenerationConfig;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.server.WorldManager;
import org.spongepowered.api.world.server.WorldTemplate;
import org.spongepowered.api.world.server.storage.ServerWorldProperties;

public class SpongeWorldService extends WorldService<SpongeWebAPI> {

  private final WorldManager worldManager;

  public SpongeWorldService(SpongeWebAPI webapi) {
    super(webapi);

    this.worldManager = Sponge.server().worldManager();
  }

  @Override
  public Collection<World> getWorlds() {
    var worlds = new ArrayList<World>();
    for (var key : worldManager.worldKeys()) {
      var world = worldManager.world(key);
      try {
        var props = world.isPresent()
            ? world.get().properties()
            : worldManager.loadProperties(key).get().orElseThrow();
        worlds.add(toWorld(props, world.isPresent()));
      } catch (Exception e) {
        logger.warn("Could not get world properties for " + key + ": " + e);
      }
    }
    return worlds;
  }

  @Override
  public World getWorld(String worldName) {
    var key = ResourceKey.resolve(worldName);
    if (worldManager.worldKeys().stream().noneMatch(k -> k.equals(key))) {
      throw new NotFoundException("World not found: " + worldName);
    }

    try {
      var world = worldManager.world(key);
      var props = world.isPresent()
              ? world.get().properties()
              : worldManager.loadProperties(key).get().orElseThrow();
      return toWorld(props, world.isPresent());
    } catch (Exception e) {
      throw new InternalServerErrorException(e);
    }
  }

  @Override
  public WorldConstants getConstants() {
    var types = RegistryTypes.WORLD_TYPE.get().stream()
        .map(t -> t.key(RegistryTypes.WORLD_TYPE).asString()).toList();
    var difficulties = RegistryTypes.DIFFICULTY.get().stream()
        .map(t -> t.key(RegistryTypes.DIFFICULTY).asString()).toList();
    return new WorldConstants(
        types,
        difficulties
    );
  }

  @Override
  public World createWorld(CreateWorldData data) {
    if (data.name() == null) {
      throw new BadRequestException("World requires a name");
    }
    if (data.type() == null) {
      throw new BadRequestException("World requires a type");
    }

    var template = WorldTemplate.builder();

    var nameKey = ResourceKey.minecraft(data.name());
    if (worldManager.worldExists(nameKey)) {
      throw new BadRequestException("A world with this name already exists");
    }
    template.key(nameKey);

    var type = WorldTypes.registry().findValue(ResourceKey.resolve(data.type()));
    if (type.isEmpty()) {
      throw new BadRequestException("World type not found: " + data.type());
    }
    template.add(Keys.WORLD_TYPE, type.get());

    if (data.seed() != null) {
      template.add(Keys.SEED, data.seed());
    }

    if (data.difficulty() != null) {
      var diff = Difficulties.registry().findValue(ResourceKey.resolve(data.difficulty()));
      if (diff.isEmpty()) {
        throw new BadRequestException("Difficulty not found: " + data.difficulty());
      }
      template.add(Keys.WORLD_DIFFICULTY, diff.get());
    }

    try {
      var temp = template.build();
      var res = worldManager.loadWorld(temp).get();
      return toWorld(res.properties(), true);
    } catch (Exception e) {
      throw new InternalServerErrorException(e);
    }
  }

  @Override
  public void deleteWorld(String worldName) {
    var key = ResourceKey.resolve(worldName);

    try {
      worldManager.deleteWorld(key).get();
    } catch (Exception e) {
      throw new InternalServerErrorException(e);
    }
  }

  @Override
  public void loadWorld(String worldName) {
    var key = ResourceKey.resolve(worldName);

    try {
      worldManager.loadWorld(key).get();
    } catch (Exception e) {
      throw new InternalServerErrorException(e);
    }
  }

  @Override
  public void unloadWorld(String worldName) {
    var key = ResourceKey.resolve(worldName);

    try {
      worldManager.unloadWorld(key).get();
    } catch (Exception e) {
      throw new InternalServerErrorException(e);
    }
  }

  @Override
  public Block getBlockAt(String worldName, int x, int y, int z) {
    var optWorld = worldManager.world(ResourceKey.resolve(worldName));
    if (optWorld.isEmpty()) {
      throw new BadRequestException("World not found or not loaded: " + worldName);
    }

    var world = optWorld.get();
    var block = world.block(x, y, z);
    return this.toBlock(block);
  }

  @Override
  public void setBlockAt(String worldName, int x, int y, int z, Block block) {
    var optWorld = worldManager.world(ResourceKey.resolve(worldName));
    if (optWorld.isEmpty()) {
      throw new BadRequestException("World not found or not loaded: " + worldName);
    }

    var world = optWorld.get();
    var blockState = this.fromBlock(block);
    var success = world.setBlock(x, y, z, blockState);
    if (!success) {
      throw new InternalServerErrorException("Could not set block");
    }
  }

  private World toWorld(ServerWorldProperties props, boolean isLoaded) {
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

    var seed = props.get(Keys.WORLD_GEN_CONFIG).map(WorldGenerationConfig::seed).orElse(null);

    var type = props.get(Keys.WORLD_TYPE).map(v -> v.key(RegistryTypes.WORLD_TYPE).asString()).orElse("");

    return new World(
        props.name(),
        type,
        isLoaded,
        props.difficulty().key(RegistryTypes.DIFFICULTY).asString(),
        seed + "",
        gameRules
    );
  }

  private Block toBlock(BlockState block) {
    return new Block(block.type().key(RegistryTypes.BLOCK_TYPE).asString());
  }

  private BlockState fromBlock(Block block) {
    var type = this.fromType(block.type());
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
