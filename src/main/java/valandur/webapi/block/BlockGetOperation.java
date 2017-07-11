package valandur.webapi.block;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.World;
import valandur.webapi.cache.world.CachedWorld;

public class BlockGetOperation extends BlockOperation {

    public BlockGetOperation(CachedWorld world, Vector3i min, Vector3i max) {
        super(world, min, max);
    }

    @Override
    protected boolean processBlock(World world, Vector3i pos) {
        return false;
    }
}
