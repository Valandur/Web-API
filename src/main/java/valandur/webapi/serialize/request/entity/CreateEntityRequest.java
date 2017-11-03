package valandur.webapi.serialize.request.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.world.ICachedWorld;

import java.util.Collection;
import java.util.Optional;

@JsonDeserialize
public class CreateEntityRequest {

    @JsonDeserialize
    private String world;
    public Optional<ICachedWorld> getWorld() {
        return WebAPI.getCacheService().getWorld(world);
    }

    @JsonDeserialize
    private Vector3d position;
    public Vector3d getPosition() {
        return position;
    }

    @JsonDeserialize
    private String type;
    public Optional<EntityType> getEntityType() {
        Collection<EntityType> types = Sponge.getRegistry().getAllOf(EntityType.class);
        return types.stream().filter(g -> g.getId().equalsIgnoreCase(type) || g.getName().equalsIgnoreCase(type)).findAny();
    }
}
