package valandur.webapi.block;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.BlockVolume;
import org.spongepowered.api.world.extent.StorageType;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.world.CachedWorld;

import java.util.*;

public class Blocks {
    private static Map<UUID, BlockUpdate> blockUpdates = new HashMap<>();

    public static int MAX_BLOCK_GET_SIZE = 1000;
    public static int MAX_BLOCK_UPDATE_SIZE = 100;

    public static UUID startBlockUpdate(UUID worldId, List<Tuple<Vector3i, BlockState>> blocks) {
        BlockUpdate update = new BlockUpdate(worldId, blocks);
        blockUpdates.put(update.getUUID(), update);
        update.start();
        return update.getUUID();
    }

    public static Collection<BlockUpdate> getBlockUpdates() {
        return blockUpdates.values();
    }
    public static Optional<BlockUpdate> getBlockUpdate(UUID uuid) {
        if (!blockUpdates.containsKey(uuid))
            return Optional.empty();
        return Optional.of(blockUpdates.get(uuid));
    }

    public static Optional<BlockVolume> getBlockVolume(CachedWorld world, Vector3i min, Vector3i max) {
        return WebAPI.runOnMain(() -> {
            Optional<?> obj = world.getLive();

            if (!obj.isPresent())
                return null;

            World w = (World)obj.get();
            return w.getBlockView(min, max).getBlockCopy(StorageType.STANDARD);
        });
    }
    public static Optional<BlockState> getBlockAt(CachedWorld world, Vector3i pos) {
        return WebAPI.runOnMain(() -> {
            Optional<?> obj = world.getLive();

            if (!obj.isPresent())
                return null;

            World w = (World)obj.get();
            return w.getBlock(pos).copy();
        });
    }
}
