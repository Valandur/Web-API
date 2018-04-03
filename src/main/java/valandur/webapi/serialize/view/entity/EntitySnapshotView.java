package valandur.webapi.serialize.view.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.World;
import valandur.webapi.api.serialize.BaseView;

import java.util.UUID;

@ApiModel("EntitySnapshot")
public class EntitySnapshotView extends BaseView<EntitySnapshot> {

    @ApiModelProperty("The uuid of the entity")
    public UUID uuid;

    @ApiModelProperty("The type of the entity")
    public EntityType type;

    @ApiModelProperty("The transform of the entity")
    public Transform<World> transform;


    public EntitySnapshotView(EntitySnapshot value) {
        super(value);

        this.uuid = value.getUniqueId().orElse(null);
        this.type = value.getType();
        this.transform = value.getTransform().orElse(null);
    }
}
