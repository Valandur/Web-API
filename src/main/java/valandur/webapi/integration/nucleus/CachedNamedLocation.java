package valandur.webapi.integration.nucleus;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.flowpowered.math.vector.Vector3d;
import io.github.nucleuspowered.nucleus.api.nucleusdata.NamedLocation;
import org.spongepowered.api.world.Location;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.world.CachedLocation;

import java.util.Optional;

@JsonDeserialize
public class CachedNamedLocation extends CachedObject<NamedLocation> {

    @JsonDeserialize
    private String name;
    public String getName() {
        return name;
    }

    @JsonDeserialize
    private CachedLocation location;
    public CachedLocation getLocation() {
        return location;
    }

    @JsonDeserialize
    private Vector3d rotation;
    public Vector3d getRotation() {
        return rotation;
    }


    public CachedNamedLocation() {
        super(null);
    }
    public CachedNamedLocation(NamedLocation value) {
        super(value);

        this.name = value.getName();
        this.location = value.getLocation().map(CachedLocation::new).orElse(null);
        this.rotation = value.getRotation();
    }
}
