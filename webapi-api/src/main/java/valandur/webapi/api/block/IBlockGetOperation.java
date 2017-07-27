package valandur.webapi.api.block;

import org.spongepowered.api.block.BlockState;

public interface IBlockGetOperation extends IBlockOperation {

    BlockState[][][] getBlocks();
}
