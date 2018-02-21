package valandur.webapi.api.cache.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flowpowered.math.vector.Vector3d;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.serialize.JsonDetails;

import java.util.Optional;

@ApiModel("Location")
public class CachedLocation extends CachedObject<Location> {

    private ICachedWorld world;
    @ApiModelProperty(value = "The world this location refers to", required = true)
    @JsonDetails(value = false, simple = true)
    public ICachedWorld getWorld() {
        return world;
    }

    private Vector3d position;
    @ApiModelProperty(value = "The position within the world that this location refers to", required = true)
    public Vector3d getPosition() {
        return position;
    }


    public CachedLocation(String worldNameOrUuid, double x, double y, double z) {
        super(null);

        this.world = cacheService.getWorld(worldNameOrUuid).orElse(null);
        this.position = new Vector3d(x, y, z);
    }
    public CachedLocation(ICachedWorld world, double x, double y, double z) {
        super(null);

        this.world = world;
        this.position = new Vector3d(x, y, z);
    }
    public CachedLocation(Location<World> location) {
        super(null);

        this.world = cacheService.getWorld(location.getExtent());
        this.position = location.getPosition().clone();
    }

    @Override
    public Optional<Location> getLive() {
        Optional<World> optWorld = world.getLive();
        return optWorld.map(w -> new Location<>(w, position));
    }

    @Override
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public String getLink() {
        return null;
    }
}
