package valandur.webapi.services;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.BlockVolume;
import org.spongepowered.api.world.extent.StorageType;
import valandur.webapi.WebAPI;
import valandur.webapi.api.block.IBlockUpdate;
import valandur.webapi.api.cache.world.CachedWorld;
import valandur.webapi.api.service.IBlockService;
import valandur.webapi.block.BlockUpdate;

import java.util.*;
import java.util.stream.Collectors;

public class BlockService implements IBlockService {
    private static Map<UUID, BlockUpdate> blockUpdates = new HashMap<>();

    public static int MAX_BLOCK_GET_SIZE = 1000;
    public static int MAX_BLOCK_UPDATE_SIZE = 100;

    public IBlockUpdate startBlockUpdate(UUID worldId, List<Tuple<Vector3i, BlockState>> blocks) {
        BlockUpdate update = new BlockUpdate(worldId, blocks);
        blockUpdates.put(update.getUUID(), update);
        update.start();
        return update;
    }

    public Collection<IBlockUpdate> getBlockUpdates() {
        return blockUpdates.values().stream().map(b -> (IBlockUpdate)b).collect(Collectors.toList());
    }
    public Optional<IBlockUpdate> getBlockUpdate(UUID uuid) {
        if (!blockUpdates.containsKey(uuid))
            return Optional.empty();
        return Optional.of(blockUpdates.get(uuid));
    }

    public Optional<BlockVolume> getBlockVolume(CachedWorld world, Vector3i min, Vector3i max) {
        return WebAPI.runOnMain(() -> {
            Optional<?> obj = world.getLive();

            if (!obj.isPresent() || !(obj.get() instanceof World))
                return null;

            try {
                World w = (World) obj.get();
                return w.getBlockView(min, max).getBlockCopy(StorageType.STANDARD);
            } catch (OutOfMemoryError ignored) {
                WebAPI.getLogger().warn("Not enough memory to process block volume!");
                return null;
            }
        });
    }
    public Optional<BlockState> getBlockAt(CachedWorld world, Vector3i pos) {
        return WebAPI.runOnMain(() -> {
            Optional<?> obj = world.getLive();

            if (!obj.isPresent())
                return null;

            World w = (World)obj.get();
            return w.getBlock(pos).copy();
        });
    }
}
