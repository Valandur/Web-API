package valandur.webapi.block;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.BiomeVolume;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.config.BaseConfig;
import valandur.webapi.config.BlockConfig;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The block service performs block operations on the world, such as getting or setting them.
 */
public class BlockService {

    private static final String configFileName = "blocks.conf";

    private static Map<UUID, BlockOperation> blockOps = new ConcurrentHashMap<>();

    private static final String UNKOWN_BIOME_ID = "<unknown>";
    private static int MAX_BLOCK_GET_SIZE = 1000000;
    private static int MAX_BLOCK_UPDATE_SIZE = 1000000;
    private static int MAX_BLOCKS_PER_SECOND = 10000;
    private static Vector3i BIOME_INTERVAL = new Vector3i(4, 0, 4);


    public void init() {
        Path configPath = WebAPI.getConfigPath().resolve(configFileName).normalize();
        BlockConfig config = BaseConfig.load(configPath, new BlockConfig());

        MAX_BLOCK_GET_SIZE = config.maxBlockGetSize;
        MAX_BLOCK_UPDATE_SIZE = config.maxBlockUpdateSize;
        MAX_BLOCKS_PER_SECOND = config.maxBlocksPerSecond;
    }

    /**
     * Starts a new block operation on the world.
     * @param operation The operation which will be saved and started.
     * @return The block operation which was started.
     */
    public BlockOperation startBlockOperation(BlockOperation operation) {
        blockOps.put(operation.getUUID(), operation);
        operation.start();
        return operation;
    }

    /**
     * Gets a list of all currently running block operations.
     * @return A list of running block operations.
     */
    public Collection<BlockOperation> getBlockOperations() {
        return new ArrayList<>(blockOps.values());
    }

    /**
     * Gets the block operation with the specified key.
     * @param uuid The key of the block operation.
     * @return An optional containing the block operation if it was found.
     */
    public Optional<BlockOperation> getBlockOperation(UUID uuid) {
        if (!blockOps.containsKey(uuid))
            return Optional.empty();
        return Optional.of(blockOps.get(uuid));
    }

    /**
     * Gets the block in the specified world at the specified position.
     * @param world The world to get the block from.
     * @param pos The position of the block.
     * @return The block state of the block that was requested
     */
    public BlockState getBlockAt(CachedWorld world, Vector3i pos) {
        return WebAPI.runOnMain(() -> world.getLive().getBlock(pos).copy());
    }

    /**
     * Gets the maximum number of blocks per second the server processes.
     * @return The maximum number of blocks processed per second.
     */
    public int getMaxBlocksPerSecond() {
        return MAX_BLOCKS_PER_SECOND;
    }

    /**
     * Gets the maximum number of blocks that can be retrieved with a single operation.
     * @return The maximum number of blocks that can be checked in a single operation.
     */
    public int getMaxGetBlocks() {
        return MAX_BLOCK_GET_SIZE;
    }

    /**
     * Gets the maximum number of blocks that can be changed with a single operation.
     * @return The maximum number of blocks that can be changed in a single operation.
     */
    public int getMaxUpdateBlocks() {
        return MAX_BLOCK_UPDATE_SIZE;
    }

    /**
     * Gets the biome type ids for the specified area. The biome type is checked for every n-th block within the
     * region, where n is defined by {@link #BIOME_INTERVAL}. This means the resulting array will contain the
     * blocks indexed by x-direction first, with the array and sub-arrays being 1/n-th the size of the specified
     * region.
     * @param world The world to get the biome ids from
     * @param min The lowest point that defines the region.
     * @param max The highest point that defines the region.
     * @return A matrix containing the biome ids for every n-th block, indexed by x-coordinate first.
     */
    public String[][] getBiomes(CachedWorld world, Vector3i min, Vector3i max) {
        return WebAPI.runOnMain(() -> {
            World w = world.getLive();
            BiomeVolume vol = w.getBiomeView(min, max).getRelativeBiomeView();
            Vector3i size = vol.getBiomeSize();

            int maxX = (int)Math.ceil(size.getX() / (float)BIOME_INTERVAL.getX());
            int maxZ = (int)Math.ceil(size.getZ() / (float)BIOME_INTERVAL.getZ());
            String[][] biomes = new String[maxX][maxZ];
            for (int x = 0; x < maxX; x++) {
                for (int z = 0; z < maxZ; z++) {
                    int newX = x * BIOME_INTERVAL.getX();
                    int newZ = z * BIOME_INTERVAL.getZ();
                    biomes[x][z] = vol.getBiome(newX, 0, newZ).getId();
                    if (biomes[x][z] == null) {
                        WebAPI.getLogger().warn("Unknown biome at [" + (min.getX() + newX) + "," + (min.getZ() + newZ) + "]");
                        biomes[x][z] = UNKOWN_BIOME_ID;
                    }
                }
            }

            return biomes;
        });
    }
}
