package valandur.webapi.block;

import org.spongepowered.api.event.Event;

public interface BlockUpdateEvent extends Event {
    BlockUpdate getBlockUpdate();
}
