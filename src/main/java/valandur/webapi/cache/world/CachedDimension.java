package valandur.webapi.cache.world;

import org.spongepowered.api.world.Dimension;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;

public class CachedDimension extends CachedObject {

    private CachedCatalogType type;
    public CachedCatalogType getType() {
        return type;
    }

    private int height;
    public int getHeight() {
        return height;
    }

    private int buildHeight;
    public int getBuildHeight() {
        return buildHeight;
    }


    public CachedDimension(Dimension dimension) {
        super(dimension);

        this.type = new CachedCatalogType(dimension.getType());
        this.height = dimension.getHeight();
        this.buildHeight = dimension.getBuildHeight();
    }
}
