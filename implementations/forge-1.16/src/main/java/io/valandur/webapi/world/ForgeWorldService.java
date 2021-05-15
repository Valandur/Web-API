package io.valandur.webapi.world;

import io.valandur.webapi.ForgeWebAPI;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.WebApplicationException;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;

public class ForgeWorldService extends WorldService<ForgeWebAPI> {

    private final MinecraftServer server;

    public ForgeWorldService(ForgeWebAPI webapi) {
        super(webapi);

        this.server = ServerLifecycleHooks.getCurrentServer();
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
        var loc = ResourceLocation.tryCreate(world);
        if (loc == null) {
            throw new BadRequestException("Invalid world type: " + world);
        }

        for (var forgeWorld : server.getWorlds()) {
            if (!forgeWorld.getDimensionKey().getLocation().equals(loc)) {
                continue;
            }

            var blockState = forgeWorld.getBlockState(new BlockPos(x, y, z));
            return this.toBlock(blockState);
        }

        throw new InternalServerErrorException("World not found: " + world);
    }

    @Override
    public void setBlockAt(String world, int x, int y, int z, Block block) {
        var loc = ResourceLocation.tryCreate(world);
        if (loc == null) {
            throw new BadRequestException("Invalid world type: " + world);
        }

        for (var forgeWorld : server.getWorlds()) {
            if (!forgeWorld.getDimensionKey().getLocation().equals(loc)) {
                continue;
            }

            var blockState = this.fromBlock(block);
            var success = forgeWorld.setBlockState(new BlockPos(x, y, z), blockState);
            if (!success) {
                throw new InternalServerErrorException("Could not set block");
            }
        }

        throw new InternalServerErrorException("World not found: " + world);
    }


    private World toWorld(net.minecraft.world.server.ServerWorld world) {
        var gameRules = new ArrayList<GameRule>();
        var rules = world.getGameRules();
        GameRules.visitAll(new GameRules.IRuleEntryVisitor() {
            @Override
            @ParametersAreNonnullByDefault
            public <T extends GameRules.RuleValue<T>> void visit(GameRules.RuleKey<T> key,
                                                                 GameRules.RuleType<T> type) {
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

    private Block toBlock(BlockState block) {
        var loc = block.getBlock().getRegistryName();
        return new Block(loc != null ? loc.toString() : "");
    }

    private BlockState fromBlock(Block block) throws WebApplicationException {
        var type = this.fromType(block.type);
        return type.getDefaultState();
    }

    private net.minecraft.block.Block fromType(String type) {
        var loc = ResourceLocation.tryCreate(type);
        if (loc == null) {
            throw new BadRequestException("Invalid block type: " + type);
        }

        return ForgeRegistries.BLOCKS.getValue(loc);
    }
}
