package valandur.webapi.integration.nations;

import com.arckenver.nations.object.Rect;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.world.ICachedWorld;

public class CachedRect extends CachedObject<Rect> {

    private ICachedWorld world;
    private int minX;
    private int minY;
    private int maxX;
    private int maxY;
    
    
    public CachedRect(Rect value) {
        super(value);

        this.world = cacheService.getWorld(value.getWorld()).orElse(null);
        this.minX = value.getMinX();
        this.maxX = value.getMaxX();
        this.minY = value.getMinY();
        this.maxY = value.getMaxY();
    }
}
