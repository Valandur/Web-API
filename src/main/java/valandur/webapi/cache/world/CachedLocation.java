package valandur.webapi.cache.world;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedVector3d;
import valandur.webapi.serialize.JsonDetails;

@ApiModel("Location")
public class CachedLocation extends CachedObject<Location> {

    private CachedWorld world;
    @ApiModelProperty(value = "The world this location refers to", required = true)
    @JsonDetails(value = false, simple = true)
    public CachedWorld getWorld() {
        return world;
    }

    private CachedVector3d position;
    @ApiModelProperty(value = "The position within the world that this location refers to", required = true)
    public CachedVector3d getPosition() {
        return position;
    }


    public CachedLocation(String worldNameOrUuid, double x, double y, double z) {
        super(null);

        this.world = cacheService.getWorld(worldNameOrUuid).orElse(null);
        this.position = new CachedVector3d(x, y, z);
    }
    public CachedLocation(CachedWorld world, double x, double y, double z) {
        super(null);

        this.world = world;
        this.position = new CachedVector3d(x, y, z);
    }
    public CachedLocation(Location<World> location) {
        super(null);

        this.world = cacheService.getWorld(location.getExtent());
        this.position = new CachedVector3d(location.getPosition());
    }

    @Override
    public Location<World> getLive() {
        return new Location<>(world.getLive(), position.getLive());
    }
}
