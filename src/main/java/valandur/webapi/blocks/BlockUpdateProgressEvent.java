package valandur.webapi.blocks;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class BlockUpdateProgressEvent extends AbstractEvent {

    private final BlockUpdate blockUpdate;
    public BlockUpdate getBlockUpdate() {
        return blockUpdate;
    }


    public BlockUpdateProgressEvent(BlockUpdate update) {
        this.blockUpdate = update;
    }

    @Override
    public Cause getCause() {
        return blockUpdate.getCause();
    }
}
