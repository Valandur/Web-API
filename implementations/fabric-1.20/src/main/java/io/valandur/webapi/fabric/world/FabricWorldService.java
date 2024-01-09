package io.valandur.webapi.fabric.world;

import io.valandur.webapi.fabric.FabricWebAPI;
import io.valandur.webapi.world.Block;
import io.valandur.webapi.world.CreateWorldData;
import io.valandur.webapi.world.GameRule;
import io.valandur.webapi.world.World;
import io.valandur.webapi.world.WorldConstants;
import io.valandur.webapi.world.WorldService;
import jakarta.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.Key;
import net.minecraft.world.GameRules.Rule;
import net.minecraft.world.GameRules.Type;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;

public class FabricWorldService extends WorldService<FabricWebAPI> {

  private final MinecraftServer server;

  public FabricWorldService(FabricWebAPI webapi) {
    super(webapi);

    this.server = webapi.getPlugin().getServer();
  }

  @Override
  public Collection<World> getWorlds() {
    var worlds = new ArrayList<World>();
    for (var world : server.getWorlds()) {
      worlds.add(toWorld(world, true));
    }
    return worlds;
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
    ServerWorld world = null;
    for (var tempWorld : server.getWorlds()) {
      if (getName(tempWorld) == worldName) {
        world = tempWorld;
        break;
      }
    }
    if (world == null) {
      throw new NotFoundException("World not found: " + worldName);
    }

    var state = world.getBlockState(new BlockPos(x, y, z));
    return null;
  }

  @Override
  public void setBlockAt(String worldName, int x, int y, int z, Block block) {

  }

  private World toWorld(ServerWorld world, boolean isLoaded) {
    var gameRules = new ArrayList<GameRule>();
    var rules = world.getGameRules();
    GameRules.accept(new GameRules.Visitor() {
      @Override
      public <T extends Rule<T>> void visit(Key<T> key, Type<T> type) {
        gameRules.add(new GameRule(key.getName(), rules.get(key).serialize()));
      }
    });

    String name = getName(world);

    return new World(
        name,
        world.getDimensionKey().getValue().toString(),
        isLoaded,
        world.getDifficulty().name(),
        world.getSeed() + "",
        gameRules
    );
  }

  private String getName(ServerWorld world) {
    String name = null;
    var props = world.getLevelProperties();
    if (props instanceof LevelProperties) {
      name = ((LevelProperties) props).getLevelName();
    } else if (props instanceof UnmodifiableLevelProperties) {
      name = ((UnmodifiableLevelProperties) props).getLevelName();
    }
    return name;
  }
}