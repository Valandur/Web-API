package io.valandur.webapi.world;

import io.valandur.webapi.SpigotWebAPI;
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
                world.getUID().toString(),
                world.getName(),
                world.getEnvironment().name(),
                world.getDifficulty().name(),
                world.getSeed(),
                gameRules
        );
    }
}
