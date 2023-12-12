package io.valandur.webapi.spigot.world;

import io.valandur.webapi.spigot.SpigotWebAPI;
import io.valandur.webapi.world.Block;
import io.valandur.webapi.world.GameRule;
import io.valandur.webapi.world.World;
import io.valandur.webapi.world.WorldService;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.data.BlockData;

public class SpigotWorldService extends WorldService<SpigotWebAPI> {

  private final Server server;

  public SpigotWorldService(SpigotWebAPI webapi) {
    super(webapi);

    this.server = webapi.getPlugin().getServer();
  }

  @Override
  public Collection<World> getWorlds() {
    var worlds = new ArrayList<World>();
    for (var world : server.getWorlds()) {
      worlds.add(this.toWorld(world));
    }
    return worlds;
  }

  @Override
  public Block getBlockAt(UUID worldId, int x, int y, int z) {
    var world = server.getWorld(worldId);
    if (world == null) {
      throw new BadRequestException("World not found: " + worldId);
    }

    var block = world.getBlockAt(x, y, z);
    return this.toBlock(block.getBlockData());
  }

  @Override
  public void setBlockAt(UUID worldId, int x, int y, int z, Block block) {
    var world = server.getWorld(worldId);
    if (world == null) {
      throw new BadRequestException("World not found: " + worldId);
    }

    var blockData = this.fromBlock(block);
    world.getBlockAt(x, y, z).setBlockData(blockData);
  }


  private World toWorld(org.bukkit.World world) {
    var gameRuleNames = world.getGameRules();
    var gameRules = new ArrayList<GameRule>();
    for (var gameRuleName : gameRuleNames) {
      var gameRule = org.bukkit.GameRule.getByName(gameRuleName);
      if (gameRule != null) {
        var value = world.getGameRuleValue(gameRule);
        gameRules.add(new GameRule(gameRule.getName(), value));
      }
    }

    return new World(
        world.getUID(),
        world.getEnvironment().name(),
        world.getName(),
        world.getDifficulty().name(),
        world.getSeed(),
        gameRules
    );
  }

  private Block toBlock(BlockData block) {
    return new Block(
        block.getMaterial().name()
    );
  }

  private BlockData fromBlock(Block block) throws WebApplicationException {
    var material = this.fromType(block.type);
    return Bukkit.createBlockData(material);
  }

  private Material fromType(String type) {
    var material = Material.matchMaterial(type);
    if (material == null) {
      throw new BadRequestException("Invalid block type: " + type);
    }

    return material;
  }
}
