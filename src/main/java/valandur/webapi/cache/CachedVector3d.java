package valandur.webapi.cache;

import com.flowpowered.math.vector.Vector3d;
import com.google.gson.annotations.Expose;

public class CachedVector3d extends CachedObject {
    @Expose
    public double x;

    @Expose
    public double y;

    @Expose
    public double z;

    public static CachedVector3d copyFrom(Vector3d vector) {
        CachedVector3d cache = new CachedVector3d();
        cache.x  = vector.getX();
        cache.y = vector.getY();
        cache.z = vector.getZ();
        return cache;
    }

    @Override
    public int getCacheDuration() {
        return 0;
    }
}
