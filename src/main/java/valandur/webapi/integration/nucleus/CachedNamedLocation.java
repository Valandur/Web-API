package valandur.webapi.integration.nucleus;

import io.github.nucleuspowered.nucleus.api.nucleusdata.NamedLocation;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.world.CachedLocation;

public class CachedNamedLocation extends CachedObject<NamedLocation> {

    private String name;
    public String getName() {
        return name;
    }

    private CachedLocation location;
    public CachedLocation getLocation() {
        return location;
    }


    public CachedNamedLocation(NamedLocation value) {
        super(value);

        this.name = value.getName();
        this.location = value.getLocation().map(CachedLocation::new).orElse(null);
    }
}
