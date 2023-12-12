package io.valandur.webapi.fabric.world;

import io.valandur.webapi.fabric.FabricWebAPI;
import io.valandur.webapi.world.Block;
import io.valandur.webapi.world.GameRule;
import io.valandur.webapi.world.World;
import io.valandur.webapi.world.WorldService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.Key;
import net.minecraft.world.GameRules.Rule;
import net.minecraft.world.GameRules.Type;
import net.minecraft.world.GameRules.Visitor;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;

public class FabricWorldService extends WorldService<FabricWebAPI> {

  private Map<ServerWorld, UUID> worldKeys = new ConcurrentHashMap<>();
  private Map<UUID, ServerWorld> worlds = new ConcurrentHashMap<>();

  public FabricWorldService(FabricWebAPI webapi) {
    super(webapi);
  }

  @Override
  public Collection<World> getWorlds() {
    updateWorlds();

    var worlds = new ArrayList<World>();
    for (var world : webapi.getPlugin().getServer().getWorlds()) {
      worlds.add(toWorld(world));
    }
    return worlds;
  }

  @Override
  public Block getBlockAt(UUID worldId, int x, int y, int z) {
    var world = worlds.get(worldId);
    var state = world.getBlockState(new BlockPos(x, y, z));
    return null;
  }

  @Override
  public void setBlockAt(UUID worldId, int x, int y, int z, Block block) {

  }

  private World toWorld(ServerWorld world) {
    var gameRules = new ArrayList<GameRule>();
    var rules = world.getGameRules();
    GameRules.accept(new GameRules.Visitor() {
      @Override
      public <T extends Rule<T>> void visit(Key<T> key, Type<T> type) {
        gameRules.add(new GameRule(key.getName(), rules.get(key).serialize()));
      }
    });

    String name = null;
    var props = world.getLevelProperties();
    if (props instanceof LevelProperties) {
      name = ((LevelProperties) props).getLevelName();
    } else if (props instanceof UnmodifiableLevelProperties) {
      name = ((UnmodifiableLevelProperties) props).getLevelName();
    }

    return new World(
        worldKeys.get(world),
        world.getDimensionKey().getValue().toString(),
        name,
        world.getDifficulty().name(),
        world.getSeed(),
        gameRules
    );
  }

  private void updateWorlds() {
    for (var world : webapi.getPlugin().getServer().getWorlds()) {
      var uuid = UUID.randomUUID();
      if (worldKeys.putIfAbsent(world, uuid) == null) {
        worlds.put(uuid, world);
      }
    }
  }
}
