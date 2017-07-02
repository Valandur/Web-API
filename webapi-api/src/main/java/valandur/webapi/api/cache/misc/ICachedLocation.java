package valandur.webapi.api.cache.misc;

import com.flowpowered.math.vector.Vector3d;
import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.cache.world.ICachedWorld;

public interface ICachedLocation extends ICachedObject {

    ICachedWorld getWorld();

    Vector3d getPosition();
}
