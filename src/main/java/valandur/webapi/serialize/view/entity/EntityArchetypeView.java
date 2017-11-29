package valandur.webapi.serialize.view.entity;

import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.entity.EntityType;
import valandur.webapi.api.serialize.BaseView;

public class EntityArchetypeView extends BaseView<EntityArchetype> {

    public EntityType type;


    public EntityArchetypeView(EntityArchetype value) {
        super(value);

        this.type = value.getType();
    }
}
