package valandur.webapi.api.cache.misc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.translation.Translatable;
import valandur.webapi.api.cache.CachedObject;

import java.util.Optional;

public class CachedCatalogType<T extends CatalogType> extends CachedObject<T> implements ICachedCatalogType<T> {

    private String id;
    public String getId() {
        return id;
    }

    private String name;
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
            } catch (AbstractMethodError ignored) {
                this.name = value.getName();
            }
        } else {
            this.name = value.getName();
        }
    }

    public Optional<T> getLive(Class<T> clazz) {
        return Sponge.getRegistry().getType(clazz, this.id);
    }

    @Override
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public String getLink() {
        return null;
    }
}
