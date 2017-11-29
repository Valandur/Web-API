package valandur.webapi.cache.misc;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.text.translation.Translatable;
import valandur.webapi.api.cache.CachedObject;

public class CachedCatalogType extends CachedObject<CatalogType> {

    private String id;
    public String getId() {
        return id;
    }

    private String name;
    public String getName() {
        return name;
    }


    public CachedCatalogType(CatalogType catalogType) {
        super(catalogType, false);

        this.id = catalogType.getId();
        if (catalogType instanceof Translatable) {
            this.name = ((Translatable)catalogType).getTranslation().get();
        } else {
            this.name = catalogType.getName();
        }
    }
}
