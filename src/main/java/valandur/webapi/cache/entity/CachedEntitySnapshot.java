package valandur.webapi.cache.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.cache.world.CachedTransform;

import java.util.UUID;

@ApiModel("EntitySnapshot")
public class CachedEntitySnapshot extends CachedObject<EntitySnapshot> {

    @ApiModelProperty("The uuid of the entity")
    public UUID uuid;

    @ApiModelProperty("The type of the entity")
    public CachedCatalogType<EntityType> type;

    @ApiModelProperty("The transform of the entity")
    public CachedTransform transform;


    public CachedEntitySnapshot(EntitySnapshot value) {
        super(value);

        this.uuid = value.getUniqueId().orElse(null);
        this.type = new CachedCatalogType<>(value.getType());
        value.getTransform().ifPresent(t -> this.transform = new CachedTransform(t));
    }
}
