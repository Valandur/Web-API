package valandur.webapi.block;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.World;
import valandur.webapi.api.block.IBlockUpdateOperation;
import valandur.webapi.api.cache.world.ICachedWorld;

import java.util.Map;

public class BlockUpdateOperation extends BlockOperation implements IBlockUpdateOperation {

    Map<Vector3i, BlockState> newStates;


    public BlockUpdateOperation(ICachedWorld world, Vector3i min, Vector3i max, Map<Vector3i, BlockState> blocks) {
        super(world, min, max);

        this.newStates = blocks;
    }

    @Override
    protected void processBlock(World world, Vector3i pos) {
        BlockState state = newStates.get(pos);

        if (state == null)
            return;

        world.setBlock(pos, state, cause);
    }
}
