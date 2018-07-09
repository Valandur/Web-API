package valandur.webapi.cache.misc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.type.DyeColor;
import valandur.webapi.cache.CachedObject;

@ApiModel("DyeColor")
public class CachedDyeColor extends CachedObject<DyeColor> {

    @ApiModelProperty(value = "The unique id of this dye color", required = true)
    public String id;

    @ApiModelProperty(value = "The name of this dye color", required = true)
    public String name;

    @ApiModelProperty(value = "The color represented by this dye color", required = true)
    public CachedColor color;


    public CachedDyeColor(DyeColor value) {
        super(value);

        this.id = value.getId();
        this.name = value.getTranslation().get();
        this.color = new CachedColor(value.getColor());
    }
}
