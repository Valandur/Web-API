package valandur.webapi.api.block;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.extent.BlockVolume;
import valandur.webapi.api.cache.world.ICachedWorld;

import java.util.*;

/**
 * The block service performs block operations on the world, such as getting or setting them.
 */
public interface IBlockService {

    /**
     * Starts a new block update, which will be completed over time so that it doesn't lag the server.
     * @param worldId The id of the world that the operation is started on.
     * @param blocks The list of positions and block states specifying the changes to be made.
     * @return The new block update that will be processed by the Web-API.
     */
    IBlockUpdate startBlockUpdate(UUID worldId, List<Tuple<Vector3i, BlockState>> blocks);

    /**
     * Gets a list of all currently running block operations.
     * @return A list of running block operations.
     */
    Collection<IBlockUpdate> getBlockUpdates();

    /**
     * Gets the block update with the specified key.
     * @param uuid The key of the block update.
     * @return An optional containing the block update if it was found.
     */
    Optional<IBlockUpdate> getBlockUpdate(UUID uuid);

    /**
     * Gets the blocks contained within the specified volume.
     * @param world The world from which the blocks are retrieved.
     * @param min The minimum coordinates of the cuboid.
     * @param max The maximum coordinates of the cuboid.
     * @return An optional containing the volume of blocks.
     */
    Optional<BlockVolume> getBlockVolume(ICachedWorld world, Vector3i min, Vector3i max);

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
