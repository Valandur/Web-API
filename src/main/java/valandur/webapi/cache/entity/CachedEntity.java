package valandur.webapi.cache.entity;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.world.World;
import valandur.webapi.api.cache.entity.ICachedEntity;
import valandur.webapi.api.cache.misc.ICachedInventory;
import valandur.webapi.api.cache.misc.ICachedLocation;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedInventory;
import valandur.webapi.cache.misc.CachedLocation;

import java.util.Optional;
import java.util.UUID;

public class CachedEntity extends CachedObject implements ICachedEntity {

    protected String type;
    public String getType() {
        return type;
    }

    protected UUID uuid;
    public UUID getUUID() {
        return uuid;
    }

    private ICachedLocation location;
    public ICachedLocation getLocation() {
        return location;
    }

    private Vector3d rotation;
    public Vector3d getRotation() {
        return rotation;
    }

    private Vector3d velocity;
    public Vector3d getVelocity() {
        return velocity;
    }

    private Vector3d scale;
    public Vector3d getScale() {
        return scale;
    }

    private ICachedInventory inventory;
    public ICachedInventory getInventory() {
        return inventory;
    }


    public CachedEntity(Entity entity) {
        super(entity);

        this.type = entity.getType().getId();
        this.uuid = UUID.fromString(entity.getUniqueId().toString());
        this.location = new CachedLocation(entity.getLocation());

        this.rotation = entity.getRotation().clone();
        this.velocity = entity.getVelocity().clone();
        this.scale = entity.getScale().clone();

        if (entity instanceof Carrier) {
            try {
                this.inventory = new CachedInventory(((Carrier) entity).getInventory());
            } catch (AbstractMethodError ignored) {}
        }
    }

    @Override
    public Optional<?> getLive() {
        for (World w : Sponge.getServer().getWorlds()) {
            Optional<Entity> e = w.getEntity(uuid);
            if (e.isPresent())
                return Optional.of(e.get());
        }
        return Optional.empty();
    }

    @Override
    public String getLink() {
        return "/api/entity/" + uuid;
    }
}
