package valandur.webapi.api.cache.tileentity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.block.tileentity.TileEntity;
import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.cache.misc.ICachedCatalogType;
import valandur.webapi.api.cache.misc.ICachedInventory;
import valandur.webapi.api.cache.world.CachedLocation;

@ApiModel("TileEntity")
public interface ICachedTileEntity extends ICachedObject<TileEntity> {

    @ApiModelProperty(value = "The type of this tile entity", required = true)
    ICachedCatalogType getType();

    @ApiModelProperty(value = "The location of this tile entity", required = true)
    CachedLocation getLocation();

    @ApiModelProperty("The inventory this tile entity has (if any)")
    ICachedInventory getInventory();
}
