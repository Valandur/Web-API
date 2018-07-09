package valandur.webapi.cache.item;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkShape;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.cache.misc.CachedColor;

import java.util.List;
import java.util.stream.Collectors;

@ApiModel("FireworkEffect")
public class CachedFireworkEffect extends CachedObject<FireworkEffect> {

    @ApiModelProperty(value = "The shape of the firework", required = true)
    public CachedCatalogType<FireworkShape> shape;

    @ApiModelProperty(value = "True if the firework flickers, false otherwise", required = true)
    public boolean flickers;

    @ApiModelProperty(value = "The colors that make up this firework", required = true)
    public List<CachedColor> colors;

    @ApiModelProperty(value = "The fade colors that this firework has", required = true)
    public List<CachedColor> fadeColors;

    @ApiModelProperty(value = "True if this firework has a trail, false otherwise", required = true)
    public boolean trail;


    public CachedFireworkEffect(FireworkEffect value) {
        super(value);

        this.shape = new CachedCatalogType<>(value.getShape());
        this.flickers = value.flickers();
        this.colors = value.getColors().stream().map(CachedColor::new).collect(Collectors.toList());
        this.fadeColors = value.getFadeColors().stream().map(CachedColor::new).collect(Collectors.toList());
        this.trail = value.hasTrail();
    }
}
