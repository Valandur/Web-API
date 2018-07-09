package valandur.webapi.cache.misc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.translation.Translatable;
import valandur.webapi.cache.CachedObject;

import java.util.Optional;

@ApiModel("CatalogType")
public class CachedCatalogType<T extends CatalogType> extends CachedObject<T> {

    private String id;
    /***
     * Gets the unique id of the catalog type
     * @return The unique id of the catalog type
     */
    @ApiModelProperty(value = "The unique id that indexes this catalog type", required = true)
    public String getId() {
        return id;
    }

    private String name;
    /**
     * Gets the (translated) readable name of the catalog type.
     * @return The readable name of the catalog type
     */
    @ApiModelProperty(value = "The name of the catalog type", required = true, readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getName() {
        return name;
    }

    public CachedCatalogType() {
        super(null);
    }
    public CachedCatalogType(T value) {
        super(value, false);

        this.id = value.getId();
        if (value instanceof Translatable) {
            try {
                this.name = ((Translatable) value).getTranslation().get();
            } catch (AbstractMethodError | NullPointerException ignored) {
                this.name = value.getName();
            }
        } else {
            this.name = value.getName();
        }
    }

    /**
     * Gets the live version of this CachedCatalogType.
     * @param clazz The class of the CatalogType.
     * @return The live CatalogType.
     */
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public Optional<T> getLive(Class<T> clazz) {
        return Sponge.getRegistry().getType(clazz, this.id);
    }
}
