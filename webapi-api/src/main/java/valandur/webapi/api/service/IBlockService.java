package valandur.webapi.api.service;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.extent.BlockVolume;
import valandur.webapi.api.block.IBlockUpdate;
import valandur.webapi.api.cache.world.CachedWorld;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IBlockService {

    IBlockUpdate startBlockUpdate(UUID worldId, List<Tuple<Vector3i, BlockState>> blocks);

    Collection<IBlockUpdate> getBlockUpdates();
    Optional<IBlockUpdate> getBlockUpdate(UUID uuid);

    Optional<BlockVolume> getBlockVolume(CachedWorld world, Vector3i min, Vector3i max);
    Optional<BlockState> getBlockAt(CachedWorld world, Vector3i pos);
}
