package valandur.webapi.block;

import com.flowpowered.math.vector.Vector3i;
import io.swagger.annotations.ApiModel;
import org.spongepowered.api.world.World;
import valandur.webapi.cache.block.CachedBlockState;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.serialize.JsonDetails;

@ApiModel(parent = BlockOperation.class)
public class BlockGetOperation extends BlockOperation {

    @Override
    public BlockOperationType getType() {
        return BlockOperationType.GET;
    }

    private CachedBlockState[][][] blockStates;

    @JsonDetails
    public CachedBlockState[][][] getBlocks() {
        return blockStates;
    }


    public BlockGetOperation(CachedWorld world, Vector3i min, Vector3i max) {
        super(world, min, max);
        blockStates = new CachedBlockState[size.getX()][size.getY()][size.getZ()];
    }

    @Override
    protected void processBlock(World world, Vector3i pos) {
        int x = pos.getX() - min.getX();
        int y = pos.getY() - min.getY();
        int z = pos.getZ() - min.getZ();
        blockStates[x][y][z] = new CachedBlockState(world.getBlock(pos));
    }
}
