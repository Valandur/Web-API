package valandur.webapi.api.cache.misc;

import org.spongepowered.api.CatalogType;
import valandur.webapi.api.cache.ICachedObject;

import java.util.Optional;

public interface ICachedCatalogType<T extends CatalogType> extends ICachedObject<T> {

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

    /**
     * Gets the live version of this CachedCatalogType.
     * @param clazz The class of the CatalogType.
     * @return The live CatalogType.
     */
    Optional<T> getLive(Class<T> clazz);
}
