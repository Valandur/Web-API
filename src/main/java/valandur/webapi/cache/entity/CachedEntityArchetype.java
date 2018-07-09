package valandur.webapi.cache.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.entity.EntityType;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;

@ApiModel("EntityArchtype")
public class CachedEntityArchetype extends CachedObject<EntityArchetype> {

    @ApiModelProperty(value = "The entity type represented by this archtype", required = true)
    public CachedCatalogType<EntityType> type;


    public CachedEntityArchetype(EntityArchetype value) {
        super(value);

        this.type = new CachedCatalogType<>(value.getType());
    }
}
