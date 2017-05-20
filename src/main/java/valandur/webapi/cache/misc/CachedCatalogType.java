package valandur.webapi.cache.misc;

import org.spongepowered.api.CatalogType;
import valandur.webapi.cache.CachedObject;

public class CachedCatalogType extends CachedObject {

    private String id;
    public String getId() {
        return id;
    }

    private String name;
    public String getName() {
        return name;
    }


    public CachedCatalogType(CatalogType catalogType) {
        super(catalogType);

        this.id = catalogType.getId();
        this.name = catalogType.getName();
    }
}
