package valandur.webapi.integration.nucleus;

import io.github.nucleuspowered.nucleus.api.nucleusdata.Home;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.cache.world.CachedLocation;

public class CachedHome extends CachedObject<Home> {

    private String name;
    public String getName() {
        return name;
    }

    private ICachedPlayer user;
    public ICachedPlayer getUser() {
        return user;
    }

    private CachedLocation location;
    public CachedLocation getLocation() {
        return location;
    }

    public CachedHome(Home value) {
        super(value);

        this.name = value.getName();
        this.user = cacheService.getPlayer(value.getUser());
        this.location = value.getLocation().map(CachedLocation::new).orElse(null);
    }
}
