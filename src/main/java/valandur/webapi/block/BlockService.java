package valandur.webapi.block;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.BiomeVolume;
import valandur.webapi.WebAPI;
import valandur.webapi.api.block.IBlockOperation;
import valandur.webapi.api.block.IBlockService;
import valandur.webapi.api.cache.world.ICachedWorld;

import java.util.*;
import java.util.stream.Collectors;

public class BlockService implements IBlockService {
    private static Map<UUID, IBlockOperation> blockOps = new HashMap<>();

    private static int MAX_BLOCK_GET_SIZE = 1000000;
    private static int MAX_BLOCK_UPDATE_SIZE = 1000000;
    private static int MAX_BLOCKS_PER_SECOND = 10000;
    private static Vector3i BIOME_INTERVAL = new Vector3i(4, 0, 4);


    @Override
    public IBlockOperation startBlockOperation(IBlockOperation operation) {
        blockOps.put(operation.getUUID(), operation);
        operation.start();
        return operation;
    }

    @Override
    public Collection<IBlockOperation> getBlockOperations() {
        return blockOps.values().stream().map(b -> (IBlockOperation)b).collect(Collectors.toList());
    }
    @Override
    public Optional<IBlockOperation> getBlockOperation(UUID uuid) {
        if (!blockOps.containsKey(uuid))
            return Optional.empty();
        return Optional.of(blockOps.get(uuid));
    }

    @Override
    public Optional<BlockState> getBlockAt(ICachedWorld world, Vector3i pos) {
        return WebAPI.runOnMain(() -> {
            Optional<?> obj = world.getLive();

            if (!obj.isPresent())
                return null;

            World w = (World)obj.get();
            return w.getBlock(pos).copy();
        });
    }


    @Override
    public int getMaxBlocksPerSecond() {
        return MAX_BLOCKS_PER_SECOND;
    }
    @Override
    public int getMaxGetBlocks() {
        return MAX_BLOCK_GET_SIZE;
    }
    @Override
    public int getMaxUpdateBlocks() {
        return MAX_BLOCK_UPDATE_SIZE;
    }

    @Override
    public Vector3i getBiomeInterval() {
        return BIOME_INTERVAL;
    }

    @Override
    public Optional<String[][]> getBiomes(ICachedWorld world, Vector3i min, Vector3i max) {
        return WebAPI.runOnMain(() -> {
            Optional<?> obj = world.getLive();

            if (!obj.isPresent())
                return null;

            World w = (World)obj.get();
            BiomeVolume vol = w.getBiomeView(min, max).getRelativeBiomeView();
            Vector3i size = vol.getBiomeSize();

            int maxX = (int)Math.ceil(size.getX() / (float)BIOME_INTERVAL.getX());
            int maxZ = (int)Math.ceil(size.getZ() / (float)BIOME_INTERVAL.getZ());
            String[][] biomes = new String[maxX][maxZ];
            for (int x = 0; x < maxX; x++) {
                for (int z = 0; z < maxZ; z++) {
                    biomes[x][z] = vol.getBiome(x * BIOME_INTERVAL.getX(), 0, z * BIOME_INTERVAL.getZ()).getId();
                }
            }

            return biomes;
        });
    }
}
