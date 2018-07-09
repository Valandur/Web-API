package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.HorseData;
import org.spongepowered.api.data.type.HorseColor;
import org.spongepowered.api.data.type.HorseStyle;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;

@ApiModel("HorseData")
public class CachedHorseData extends CachedObject<HorseData> {

    @ApiModelProperty(value = "The color of the horse", required = true)
    public CachedCatalogType<HorseColor> color;

    @ApiModelProperty(value = "The style of the horse", required = true)
    public CachedCatalogType<HorseStyle> style;


    public CachedHorseData(HorseData value) {
        super(value);

        this.color = new CachedCatalogType<>(value.color().get());
        this.style = new CachedCatalogType<>(value.style().get());
    }
}
