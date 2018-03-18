package valandur.webapi.api.cache.entity;

import com.flowpowered.math.vector.Vector3d;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.entity.Entity;
import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.cache.misc.ICachedCatalogType;
import valandur.webapi.api.cache.misc.ICachedInventory;
import valandur.webapi.api.cache.world.CachedLocation;

import java.util.UUID;

@ApiModel("Entity")
public interface ICachedEntity extends ICachedObject<Entity> {

    @ApiModelProperty(value = "The type of entity", required = true)
    ICachedCatalogType getType();

    @ApiModelProperty(value = "The unique UUID of the entity", required = true)
    UUID getUUID();

    @ApiModelProperty(value = "The current location of the entity", required = true)
    CachedLocation getLocation();

    @ApiModelProperty(value = "The current rotation of the entity", required = true)
    Vector3d getRotation();

    @ApiModelProperty(value = "The current velocity of the entity", required = true)
    Vector3d getVelocity();

    @ApiModelProperty(value = "The current scale of the entity", required = true)
    Vector3d getScale();

    @ApiModelProperty("The current inventory of the entity (if any)")
    ICachedInventory getInventory();
}
