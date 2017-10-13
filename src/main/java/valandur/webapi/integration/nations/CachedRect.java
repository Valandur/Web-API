package valandur.webapi.integration.nations;

import com.arckenver.nations.object.Rect;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.world.ICachedWorld;

public class CachedRect extends CachedObject<Rect> {

    private ICachedWorld world;
    public ICachedWorld getWorld() {
        return world;
    }

    private int minX;
    public int getMinX() {
        return minX;
    }

    private int minY;
    public int getMinY() {
        return minY;
    }

    private int maxX;
    public int getMaxX() {
        return maxX;
    }

    private int maxY;
    public int getMaxY() {
        return maxY;
    }
    
    
    public CachedRect(Rect value) {
        super(value);

        this.world = cacheService.getWorld(value.getWorld()).orElse(null);
        this.minX = value.getMinX();
        this.maxX = value.getMaxX();
        this.minY = value.getMinY();
        this.maxY = value.getMaxY();
    }
}
