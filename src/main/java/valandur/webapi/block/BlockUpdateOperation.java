package valandur.webapi.block;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.World;
import valandur.webapi.cache.world.CachedWorld;

import java.util.Map;

public class BlockUpdateOperation extends BlockOperation {

    public BlockUpdateOperation(CachedWorld world, Vector3i min, Vector3i max, Map<Vector3i, BlockState> blocks) {
        super(world, min, max);
    }

    @Override
    protected boolean processBlock(World world, Vector3i pos) {
        return false;
    }
}
