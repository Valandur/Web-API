package valandur.webapi.cache.misc;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.text.translation.Translatable;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.misc.ICachedCatalogType;

public class CachedCatalogType<T extends CatalogType> extends CachedObject<T> implements ICachedCatalogType<T> {

    private String id;
    public String getId() {
        return id;
    }

    private String name;
    public String getName() {
        return name;
    }


    public CachedCatalogType(T value) {
        super(value, false);

        this.id = value.getId();
        if (value instanceof Translatable) {
            try {
                this.name = ((Translatable) value).getTranslation().get();
            } catch (AbstractMethodError ignored) {
                this.name = value.getName();
            }
        } else {
            this.name = value.getName();
        }
    }
}
