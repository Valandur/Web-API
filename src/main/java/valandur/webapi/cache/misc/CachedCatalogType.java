package valandur.webapi.cache.misc;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.text.translation.Translatable;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.misc.ICachedCatalogType;

public class CachedCatalogType extends CachedObject<CatalogType> implements ICachedCatalogType {

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
            try {
                this.name = ((Translatable) catalogType).getTranslation().get();
            } catch (AbstractMethodError ignored) {
                this.name = catalogType.getName();
            }
        } else {
            this.name = catalogType.getName();
        }
    }
}
