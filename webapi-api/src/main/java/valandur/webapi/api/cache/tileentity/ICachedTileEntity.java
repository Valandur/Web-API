package valandur.webapi.api.cache.tileentity;

import org.spongepowered.api.block.tileentity.TileEntity;
import valandur.webapi.api.cache.ICachedObject;

public interface ICachedTileEntity extends ICachedObject<TileEntity> {

    String getType();
}
