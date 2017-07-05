package valandur.webapi.block;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.BiomeVolume;
import org.spongepowered.api.world.extent.BlockVolume;
import org.spongepowered.api.world.extent.StorageType;
import valandur.webapi.WebAPI;
import valandur.webapi.api.block.IBlockService;
import valandur.webapi.api.block.IBlockUpdate;
import valandur.webapi.api.cache.world.ICachedWorld;

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

    public Optional<BlockVolume> getBlockVolume(ICachedWorld world, Vector3i min, Vector3i max) {
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
    public Optional<BlockState> getBlockAt(ICachedWorld world, Vector3i pos) {
        return WebAPI.runOnMain(() -> {
            Optional<?> obj = world.getLive();

            if (!obj.isPresent())
                return null;

            World w = (World)obj.get();
            return w.getBlock(pos).copy();
        });
    }

    public Optional<String[][]> getBiomes(ICachedWorld world, Vector3i min, Vector3i max) {
        return WebAPI.runOnMain(() -> {
            Optional<?> obj = world.getLive();

            if (!obj.isPresent())
                return null;

            World w = (World)obj.get();
            Vector3i interval = new Vector3i(4, 0, 4);
            BiomeVolume vol = w.getBiomeView(min, max).getRelativeBiomeView();
            Vector3i size = vol.getBiomeSize();

            int maxX = (int)Math.ceil(size.getX() / (float)interval.getX());
            int maxZ = (int)Math.ceil(size.getZ() / (float)interval.getZ());
            String[][] biomes = new String[maxX][maxZ];
            for (int x = 0; x < maxX; x++) {
                for (int z = 0; z < maxZ; z++) {
                    biomes[x][z] = vol.getBiome(x * interval.getX(), 0, z * interval.getZ()).getId();
                }
            }

            return biomes;
        });
    }
}
