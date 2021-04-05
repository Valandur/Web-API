package io.valandur.webapi.world;

import io.valandur.webapi.SpigotWebAPI;
import jakarta.ws.rs.BadRequestException;
import org.bukkit.Material;
import org.bukkit.Server;

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
    public Block getBlockAt(String worldType, int x, int y, int z) {
        var optWorld = server.getWorlds().stream().filter(w -> w.getEnvironment().name().equalsIgnoreCase(worldType)).findAny();
        if (optWorld.isEmpty()) {
            throw new BadRequestException("World with type not found: " + worldType);
        }

        var world = optWorld.get();
        var block = world.getBlockAt(x, y, z);
        return new Block(block.getType().name());
    }

    @Override
    public void setBlockAt(String worldType, int x, int y, int z, Block block) {
        var optWorld = server.getWorlds().stream().filter(w -> w.getEnvironment().name().equalsIgnoreCase(worldType)).findAny();
        if (optWorld.isEmpty()) {
            throw new BadRequestException("World with type not found: " + worldType);
        }

        var world = optWorld.get();

        var material = Material.getMaterial(block.type);
        if (material == null) {
            throw new BadRequestException("Invalid block type: " + block.type);
        }

        world.getBlockAt(x, y, z).setType(material);
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


}
