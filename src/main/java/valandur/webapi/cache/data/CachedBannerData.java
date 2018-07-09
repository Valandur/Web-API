package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.tileentity.BannerData;
import org.spongepowered.api.data.type.DyeColor;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.cache.tileentity.CachedPatternLayer;

import java.util.List;
import java.util.stream.Collectors;

@ApiModel("BannerData")
public class CachedBannerData extends CachedObject<BannerData> {

    @ApiModelProperty(value = "The color of the banner", required = true)
    public CachedCatalogType<DyeColor> color;

    @ApiModelProperty(value = "The patterns on the banner", required = true)
    public List<CachedPatternLayer> patterns;


    public CachedBannerData(BannerData value) {
        super(value);

        this.color = new CachedCatalogType<>(value.baseColor().get());
        this.patterns = value.patternsList().getAll().stream()
                .map(CachedPatternLayer::new)
                .collect(Collectors.toList());
    }
}
