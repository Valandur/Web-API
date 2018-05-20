package valandur.webapi.serialize.view.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.entity.EntityType;
import valandur.webapi.serialize.BaseView;

@ApiModel("EntityArchtype")
public class EntityArchetypeView extends BaseView<EntityArchetype> {

    @ApiModelProperty(value = "The entity type represented by this archtype", required = true)
    public EntityType type;


    public EntityArchetypeView(EntityArchetype value) {
        super(value);

        this.type = value.getType();
    }
}
