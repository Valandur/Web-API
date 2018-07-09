package valandur.webapi.cache.tileentity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.meta.PatternLayer;
import org.spongepowered.api.data.type.BannerPatternShape;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.cache.misc.CachedDyeColor;

@ApiModel("PatternLayer")
public class CachedPatternLayer extends CachedObject<PatternLayer> {

    @ApiModelProperty(value = "The base shape of this pattern", required = true)
    public CachedCatalogType<BannerPatternShape> shape;

    @ApiModelProperty(value = "The color of the pattern", required = true)
    public CachedDyeColor color;


    public CachedPatternLayer(PatternLayer value) {
        super(value);

        this.shape = new CachedCatalogType<>(value.getShape());
        this.color = new CachedDyeColor(value.getColor());
    }
}
