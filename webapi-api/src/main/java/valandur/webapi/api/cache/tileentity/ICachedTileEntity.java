package valandur.webapi.api.cache.tileentity;

import org.spongepowered.api.block.tileentity.TileEntity;
import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.cache.misc.ICachedCatalogType;

public interface ICachedTileEntity extends ICachedObject<TileEntity> {

    ICachedCatalogType getType();
}
