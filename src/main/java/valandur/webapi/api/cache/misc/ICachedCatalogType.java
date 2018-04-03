package valandur.webapi.api.cache.misc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.CatalogType;
import valandur.webapi.api.cache.ICachedObject;
import java.util.Optional;

@ApiModel("CatalogType")
public interface ICachedCatalogType<T extends CatalogType> extends ICachedObject<T> {

    /***
     * Gets the unique id of the catalog type
     * @return The unique id of the catalog type
     */
    @ApiModelProperty(value = "The unique id that indexes this catalog type", required = true)
    String getId();

    /**
     * Gets the (translated) readable name of the catalog type.
     * @return The readable name of the catalog type
     */
    @ApiModelProperty(value = "The name of the catalog type", required = true, readOnly = true)
    String getName();

    /**
     * Gets the live version of this CachedCatalogType.
     * @param clazz The class of the CatalogType.
     * @return The live CatalogType.
     */
    @ApiModelProperty(hidden = true)
    Optional<T> getLive(Class<T> clazz);
}
