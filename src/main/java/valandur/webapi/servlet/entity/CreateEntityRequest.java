package valandur.webapi.servlet.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.cache.DataCache;
import valandur.webapi.cache.world.CachedWorld;

import java.util.Collection;
import java.util.Optional;

@JsonDeserialize
public class CreateEntityRequest {

    @JsonDeserialize
    private String world;
    public Optional<CachedWorld> getWorld() {
        return DataCache.getWorld(world);
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
