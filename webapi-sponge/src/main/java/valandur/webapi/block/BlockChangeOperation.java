package valandur.webapi.block;

import com.flowpowered.math.vector.Vector3i;
import io.swagger.annotations.ApiModel;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.World;
import valandur.webapi.cache.world.CachedWorld;

import java.util.Map;

@ApiModel(parent = BlockOperation.class)
public class BlockChangeOperation extends BlockOperation {

    Map<Vector3i, BlockState> newStates;

    @Override
    public BlockOperationType getType() {
        return BlockOperationType.CHANGE;
    }

    public BlockChangeOperation(CachedWorld world, Vector3i min, Vector3i max, Map<Vector3i, BlockState> blocks) {
        super(world, min, max);

        this.newStates = blocks;
    }

    @Override
    protected void processBlock(World world, Vector3i pos) {
        BlockState state = newStates.get(pos);

        if (state == null)
            return;

        world.setBlock(pos, state, BlockChangeFlags.NONE);
    }
}
