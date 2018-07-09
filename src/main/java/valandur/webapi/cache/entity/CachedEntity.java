package valandur.webapi.cache.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.world.World;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.cache.misc.CachedInventory;
import valandur.webapi.cache.misc.CachedVector3d;
import valandur.webapi.cache.world.CachedLocation;
import valandur.webapi.serialize.JsonDetails;
import valandur.webapi.util.Constants;

import javax.ws.rs.NotFoundException;
import java.util.Optional;
import java.util.UUID;

@ApiModel("Entity")
public class CachedEntity extends CachedObject<Entity> {

    protected CachedCatalogType type;
    @ApiModelProperty(value = "The type of entity", required = true)
    public CachedCatalogType getType() {
        return type;
    }

    protected UUID uuid;
    @ApiModelProperty(value = "The unique UUID of the entity", required = true)
    public UUID getUUID() {
        return uuid;
    }

    private CachedLocation location;
    @ApiModelProperty(value = "The current location of the entity", required = true)
    public CachedLocation getLocation() {
        return location;
    }

    private CachedVector3d rotation;
    @JsonDetails
    @ApiModelProperty(value = "The current rotation of the entity", required = true)
    public CachedVector3d getRotation() {
        return rotation;
    }

    private CachedVector3d velocity;
    @JsonDetails
    @ApiModelProperty(value = "The current velocity of the entity", required = true)
    public CachedVector3d getVelocity() {
        return velocity;
    }

    private CachedVector3d scale;
    @JsonDetails
    @ApiModelProperty(value = "The current scale of the entity", required = true)
    public CachedVector3d getScale() {
        return scale;
    }

    private CachedInventory inventory;
    @JsonDetails
    @ApiModelProperty("The current inventory of the entity (if any)")
    public CachedInventory getInventory() {
        return inventory;
    }


    public CachedEntity(Entity entity) {
        super(entity);

        this.type = new CachedCatalogType<>(entity.getType());
        this.uuid = UUID.fromString(entity.getUniqueId().toString());
        this.location = new CachedLocation(entity.getLocation());

        this.rotation = new CachedVector3d(entity.getRotation());
        this.velocity = new CachedVector3d(entity.getVelocity());
        this.scale = new CachedVector3d(entity.getScale());

        if (entity instanceof Carrier) {
            try {
                this.inventory = new CachedInventory(((Carrier) entity).getInventory());
            } catch (AbstractMethodError ignored) {}
        }
    }

    @Override
    public Entity getLive() {
        for (World w : Sponge.getServer().getWorlds()) {
            Optional<Entity> e = w.getEntity(uuid);
            if (e.isPresent())
                return e.get();
        }
        throw new NotFoundException("Could not find entity: " + uuid);
    }

    @Override
    @JsonIgnore(false)
    public String getLink() {
        return Constants.BASE_PATH + "/entity/" + uuid;
    }
}
