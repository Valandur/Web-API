package valandur.webapi.api.cache.misc;

import org.spongepowered.api.CatalogType;
import valandur.webapi.api.cache.ICachedObject;

public interface ICachedCatalogType extends ICachedObject<CatalogType> {

    /***
     * Gets the unique id of the catalog type
     * @return The unique id of the catalog type
     */
    String getId();

    /**
     * Gets the (translated) readable name of the catalog type.
     * @return The readable name of the catalog type
     */
    String getName();
}
