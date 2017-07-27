package valandur.webapi.block;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import valandur.webapi.api.block.IBlockOperationEvent;

public class BlockOperationProgressEvent extends AbstractEvent implements IBlockOperationEvent.Progress {

    private BlockOperation operation;

    @Override
    public BlockOperation getBlockOperation() {
        return operation;
    }

    @Override
    public Cause getCause() {
        return operation.getCause();
    }


    public BlockOperationProgressEvent(BlockOperation operation) {
        this.operation = operation;
    }
}
