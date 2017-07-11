package valandur.webapi.block;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import valandur.webapi.api.block.IBlockOperationEvent;

public class BlockOperationStatusChangeEvent extends AbstractEvent implements IBlockOperationEvent.StatusChange {

    private BlockOperation operation;

    @Override
    public BlockOperation getBlockOperation() {
        return operation;
    }

    @Override
    public Cause getCause() {
        return operation.getCause();
    }

    public BlockOperationStatusChangeEvent(BlockOperation update) {
        this.operation = update;
    }
}
