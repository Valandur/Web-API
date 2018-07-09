package valandur.webapi.cache.misc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.util.Color;
import valandur.webapi.cache.CachedObject;

@ApiModel("Color")
public class CachedColor extends CachedObject<Color> {

    @ApiModelProperty(value = "The red value of the color", required = true)
    public int r;

    @ApiModelProperty(value = "The green value of the color", required = true)
    public int g;

    @ApiModelProperty(value = "The blue value of the color", required = true)
    public int b;


    public CachedColor(Color value) {
        super(value);

        this.r = value.getRed();
        this.g = value.getGreen();
        this.b = value.getBlue();
    }
}
