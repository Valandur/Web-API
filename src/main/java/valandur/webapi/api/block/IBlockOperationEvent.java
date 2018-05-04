package valandur.webapi.api.block;

import org.spongepowered.api.event.Event;

public interface IBlockOperationEvent extends Event {

    IBlockOperation getBlockOperation();

    interface Progress extends IBlockOperationEvent {}

    interface StatusChange extends IBlockOperationEvent {}
}
