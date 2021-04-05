package io.valandur.webapi.world;

import io.valandur.webapi.ForgeWebAPI;
import io.valandur.webapi.item.ItemStack;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.WebApplicationException;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameRules;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;

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

    @Override
    public Block getBlockAt(String worldType, int x, int y, int z) {
        throw new InternalServerErrorException("Method not implemented");
    }

    @Override
    public void setBlockAt(String worldType, int x, int y, int z, Block block) {
        throw new InternalServerErrorException("Method not implemented");
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
                world.getDimensionKey().getLocation().toString(),
                ((IServerWorldInfo) world.getWorldInfo()).getWorldName(),
                world.getDifficulty().name(),
                world.getSeed(),
                gameRules
        );
    }

    private BlockState fromBlock(Block block) throws WebApplicationException {
        var type = this.fromType(block.type);
        return new BlockState(type, null, null);
    }

    private net.minecraft.block.Block fromType(String type) {
        var loc = ResourceLocation.tryCreate(type);
        if (loc == null) {
            throw new BadRequestException("Invalid block type: " + type);
        }

        return ForgeRegistries.BLOCKS.getValue(loc);
    }
}
