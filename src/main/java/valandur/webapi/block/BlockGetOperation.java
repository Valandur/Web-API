package valandur.webapi.block;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.World;
import valandur.webapi.api.block.IBlockGetOperation;
import valandur.webapi.api.cache.world.ICachedWorld;

public class BlockGetOperation extends BlockOperation implements IBlockGetOperation {

    private BlockState[][][] blockStates;

    @Override
    public BlockState[][][] getBlocks() {
        return blockStates;
    }


    public BlockGetOperation(ICachedWorld world, Vector3i min, Vector3i max) {
        super(world, min, max);
        blockStates = new  BlockState[size.getX()][size.getY()][size.getZ()];
    }

    @Override
    protected void processBlock(World world, Vector3i pos) {
        int x = pos.getX() - min.getX();
        int y = pos.getY() - min.getY();
        int z = pos.getZ() - min.getZ();
        blockStates[x][y][z] = world.getBlock(pos).copy();
    }
}
