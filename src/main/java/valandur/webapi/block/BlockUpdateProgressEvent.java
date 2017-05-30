package valandur.webapi.block;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class BlockUpdateProgressEvent extends AbstractEvent implements BlockUpdateEvent {

    private BlockUpdate update;

    @Override
    public BlockUpdate getBlockUpdate() {
        return update;
    }

    @Override
    public Cause getCause() {
        return update.getCause();
    }


    public BlockUpdateProgressEvent(BlockUpdate update) {
        this.update = update;
    }
}
