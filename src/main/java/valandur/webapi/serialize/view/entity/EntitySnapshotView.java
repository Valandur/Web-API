package valandur.webapi.serialize.view.entity;

import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.World;
import valandur.webapi.api.serialize.BaseView;

import java.util.UUID;

public class EntitySnapshotView extends BaseView<EntitySnapshot> {

    public UUID uuid;
    public EntityType type;
    public Transform<World> transform;


    public EntitySnapshotView(EntitySnapshot value) {
        super(value);

        this.uuid = value.getUniqueId().orElse(null);
        this.type = value.getType();
        this.transform = value.getTransform().orElse(null);
    }
}
