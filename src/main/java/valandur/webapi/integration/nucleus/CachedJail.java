package valandur.webapi.integration.nucleus;

import com.flowpowered.math.vector.Vector3d;
import io.github.nucleuspowered.nucleus.api.nucleusdata.NamedLocation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.world.CachedLocation;
import valandur.webapi.util.Constants;

@ApiModel("NucleusJail")
public class CachedJail extends CachedObject<NamedLocation> {

    private String name;
    @ApiModelProperty(value = "The unique name of this jail", required = true)
    public String getName() {
        return name;
    }

    private CachedLocation location;
    @ApiModelProperty(value = "The location of the jail", required = true)
    public CachedLocation getLocation() {
        return location;
    }

    private Vector3d rotation;
    @ApiModelProperty(value = "The rotation of players within the jail", required = true)
    public Vector3d getRotation() {
        return rotation;
    }


    public CachedJail() {
        super(null);
    }
    public CachedJail(NamedLocation value) {
        super(value);

        this.name = value.getName();
        this.location = value.getLocation().map(CachedLocation::new).orElse(null);
        this.rotation = value.getRotation();
    }

    @Override
    public String getLink() {
        return Constants.BASE_PATH + "/nucleus/jail/" + name;
    }
}
