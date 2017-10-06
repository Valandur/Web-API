package valandur.webapi.api.cache.world;

import org.spongepowered.api.world.World;
import valandur.webapi.api.cache.ICachedObject;

import java.util.UUID;

public interface ICachedWorld extends ICachedObject<World> {

    UUID getUUID();

    String getName();

    boolean isLoaded();
}
