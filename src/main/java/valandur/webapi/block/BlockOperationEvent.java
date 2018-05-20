package valandur.webapi.block;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class BlockOperationEvent extends AbstractEvent {

    protected BlockOperation operation;
    public BlockOperation getBlockOperation() {
        return operation;
    }

    @Override
    public Cause getCause() {
        return operation.getCause();
    }


    public BlockOperationEvent(BlockOperation op) {
        this.operation = op;
    }
}
