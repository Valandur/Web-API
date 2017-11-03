package valandur.webapi.serialize.request.block;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockState;
import valandur.webapi.WebAPI;
import valandur.webapi.api.block.IBlockOperation;
import valandur.webapi.api.cache.world.ICachedWorld;

import java.util.Optional;

public class CreateOperationRequest {

    @JsonDeserialize
    private IBlockOperation.BlockOperationType type;
    public IBlockOperation.BlockOperationType getType() {
        return type;
    }

    @JsonDeserialize
    private String world;
    public Optional<ICachedWorld> getWorld() {
        return WebAPI.getCacheService().getWorld(world);
    }

    @JsonDeserialize
    private Vector3i min;
    public Vector3i getMin() {
        return min;
    }

    @JsonDeserialize
    private Vector3i max;
    public Vector3i getMax() {
        return max;
    }

    @JsonDeserialize
    private BlockState block;
    public BlockState getBlock() {
        return block;
    }

    @JsonDeserialize
    private BlockState[][][] blocks;
    public BlockState[][][] getBlocks() {
        return blocks;
    }
}
