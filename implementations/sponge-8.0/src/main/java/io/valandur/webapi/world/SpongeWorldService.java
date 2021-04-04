package io.valandur.webapi.world;

import io.valandur.webapi.SpongeWebAPI;
import org.spongepowered.api.Sponge;

import java.util.ArrayList;
import java.util.Collection;

public class SpongeWorldService extends WorldService<SpongeWebAPI> {

    public SpongeWorldService(SpongeWebAPI webapi) {
        super(webapi);
    }

    @Override
    public Collection<World> getWorlds() {
        var worlds = new ArrayList<World>();
        for (var world : Sponge.server().worldManager().worlds()) {
            worlds.add(this.toWorld(world));
        }
        return worlds;
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
                world.uniqueId().toString(),
                props.displayName().map(Object::toString).orElse(null),
                world.properties().worldType().toString(),
                world.difficulty().toString(),
                world.seed(),
                gameRules
        );
    }
}
