package valandur.webapi.cache.misc;

import org.spongepowered.api.CatalogType;
import valandur.webapi.api.cache.misc.ICachedCatalogType;
import valandur.webapi.cache.CachedObject;

public class CachedCatalogType extends CachedObject implements ICachedCatalogType {

    private String id;
    @Override
    public String getId() {
        return id;
    }

    private String name;
    @Override
    public String getName() {
        return name;
    }


    public CachedCatalogType(CatalogType catalogType) {
        super(catalogType);

        this.id = catalogType.getId();
        this.name = catalogType.getName();
    }
}
