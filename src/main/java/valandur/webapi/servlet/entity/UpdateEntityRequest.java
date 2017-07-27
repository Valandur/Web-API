package valandur.webapi.servlet.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.world.ICachedWorld;

import java.util.Collection;
import java.util.Optional;

@JsonDeserialize
public class UpdateEntityRequest {

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
    private Vector3d velocity;
    public Vector3d getVelocity() {
        return velocity;
    }

    @JsonDeserialize
    private Vector3d rotation;
    public Vector3d getRotation() {
        return rotation;
    }

    @JsonDeserialize
    private Vector3d scale;
    public Vector3d getScale() {
        return scale;
    }

    @JsonDeserialize
    private DamageRequest damage;
    public DamageRequest getDamage() {
        return damage;
    }


    @JsonDeserialize
    public static class DamageRequest {
        @JsonDeserialize
        private Integer amount;
        public Integer getAmount() {
            return amount;
        }

        private String type;
        public Optional<DamageType> getDamageType() {
            Collection<DamageType> types = Sponge.getRegistry().getAllOf(DamageType.class);
            return types.stream().filter(t -> t.getId().equalsIgnoreCase(type) || t.getName().equalsIgnoreCase(type)).findAny();
        }
    }
}
