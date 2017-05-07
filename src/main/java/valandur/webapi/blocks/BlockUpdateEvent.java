package valandur.webapi.blocks;

import org.spongepowered.api.event.Event;

public interface BlockUpdateEvent extends Event {
    BlockUpdate getBlockUpdate();
}
