package io.valandur.webapi.world;

import io.valandur.webapi.SpigotWebAPI;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.WebApplicationException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.Collection;

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
    public Block getBlockAt(String world, int x, int y, int z) {
        var optWorld =
                server.getWorlds().stream().filter(w -> w.getEnvironment().name().equalsIgnoreCase(world)).findAny();
        if (optWorld.isEmpty()) {
            throw new BadRequestException("World not found: " + world);
        }

        var bukkitWorld = optWorld.get();

        var block = bukkitWorld.getBlockAt(x, y, z);
        return this.toBlock(block.getBlockData());
    }

    @Override
    public void setBlockAt(String world, int x, int y, int z, Block block) {
        var optWorld =
                server.getWorlds().stream().filter(w -> w.getEnvironment().name().equalsIgnoreCase(world)).findAny();
        if (optWorld.isEmpty()) {
            throw new BadRequestException("World not found: " + world);
        }

        var bukkitWorld = optWorld.get();

        var blockData = this.fromBlock(block);
        bukkitWorld.getBlockAt(x, y, z).setBlockData(blockData);
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
