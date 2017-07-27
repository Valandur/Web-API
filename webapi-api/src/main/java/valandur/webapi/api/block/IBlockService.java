package valandur.webapi.api.block;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockState;
import valandur.webapi.api.cache.world.ICachedWorld;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * The block service performs block operations on the world, such as getting or setting them.
 */
public interface IBlockService {

    /**
     * Gets the maximum number of blocks per second the server processes.
     * @return The maximum number of blocks processed per second.
     */
    int getMaxBlocksPerSecond();

    /**
     * Gets the maximum number of blocks that can be retrieved with a single operation.
     * @return The maximum number of blocks that can be checked in a single operation.
     */
    int getMaxGetBlocks();

    /**
     * Gets the maximum number of blocks that can be changed with a single operation.
     * @return The maximum number of blocks that can be changed in a single operation.
     */
    int getMaxUpdateBlocks();

    /**
     * Starts a new block operation on the world.
     * @param operation The operation which will be saved and started.
     * @return The block operation which was started.
     */
    IBlockOperation startBlockOperation(IBlockOperation operation);

    /**
     * Gets a list of all currently running block operations.
     * @return A list of running block operations.
     */
    Collection<IBlockOperation> getBlockOperations();

    /**
     * Gets the block operation with the specified key.
     * @param uuid The key of the block operation.
     * @return An optional containing the block operation if it was found.
     */
    Optional<IBlockOperation> getBlockOperation(UUID uuid);

    /**
     * Gets the block in the specified world at the specified position.
     * @param world The world to get the block from.
     * @param pos The position of the block.
     * @return An optional containing the block if it was found.
     */
    Optional<BlockState> getBlockAt(ICachedWorld world, Vector3i pos);

    /**
     * Gets the interval at which blocks are checked in the {@link #getBiomes(ICachedWorld, Vector3i, Vector3i)}
     * method. The y-coordinate does not matter, as biomes are only in x and z direction.
     * @return The current block interval.
     */
    Vector3i getBiomeInterval();

    /**
     * Gets the biome type ids for the specified area. The biome type is checked for every n-th block within the
     * region, where n is defined by {@link #getBiomeInterval()}. This means the resulting array will contain the
     * blocks indexed by x-direction first, with the array and sub-arrays being 1/n-th the size of the specified
     * region.
     * @param world The world to get the biome ids from
     * @param min The lowest point that defines the region.
     * @param max The highest point that defines the region.
     * @return A matrix containing the biome ids for every n-th block, indexed by x-coordinate first.
     */
    Optional<String[][]> getBiomes(ICachedWorld world, Vector3i min, Vector3i max);
}
