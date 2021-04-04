package io.valandur.webapi.world;

import io.valandur.webapi.ForgeWebAPI;
import net.minecraft.world.GameRules;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.Collection;

public class ForgeWorldService extends WorldService<ForgeWebAPI> {

    public ForgeWorldService(ForgeWebAPI webapi) {
        super(webapi);
    }

    @Override
    public Collection<World> getWorlds() {
        var worlds = new ArrayList<World>();
        var server = ServerLifecycleHooks.getCurrentServer();
        for (var world : server.getWorlds()) {
            worlds.add(this.toWorld(world));
        }
        return worlds;
    }

    private World toWorld(net.minecraft.world.server.ServerWorld world) {
        var gameRules = new ArrayList<GameRule>();
        var rules = world.getGameRules();
        GameRules.visitAll(new GameRules.IRuleEntryVisitor() {
            @Override
            public <T extends GameRules.RuleValue<T>> void visit(GameRules.RuleKey<T> key, GameRules.RuleType<T> type) {
                gameRules.add(new GameRule(key.getName(), rules.get(key).stringValue()));
            }
        });

        return new World(
                "",
                ((IServerWorldInfo) world.getWorldInfo()).getWorldName(),
                world.getDimensionKey().getLocation().toString(),
                world.getDifficulty().name(),
                world.getSeed(),
                gameRules
        );
    }
}
