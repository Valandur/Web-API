package valandur.webapi.serialize.view.misc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.util.Color;
import valandur.webapi.serialize.BaseView;

@ApiModel("Color")
public class ColorView extends BaseView<Color> {

    @ApiModelProperty(value = "The red value of the color", required = true)
    public int r;

    @ApiModelProperty(value = "The green value of the color", required = true)
    public int g;

    @ApiModelProperty(value = "The blue value of the color", required = true)
    public int b;


    public ColorView(Color value) {
        super(value);

        this.r = value.getRed();
        this.g = value.getGreen();
        this.b = value.getBlue();
    }
}
