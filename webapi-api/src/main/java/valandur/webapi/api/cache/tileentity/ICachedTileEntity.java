package valandur.webapi.api.cache.tileentity;

import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.cache.misc.ICachedInventory;
import valandur.webapi.api.cache.misc.ICachedLocation;

public interface ICachedTileEntity extends ICachedObject {

    String getType();

    ICachedLocation getLocation();

    ICachedInventory getInventory();
}
