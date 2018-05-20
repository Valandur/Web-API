package valandur.webapi.serialize.view.item;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkShape;
import org.spongepowered.api.util.Color;
import valandur.webapi.serialize.BaseView;

import java.util.List;

@ApiModel("FireworkEffect")
public class FireworkEffectView extends BaseView<FireworkEffect> {

    @ApiModelProperty(value = "The shape of the firework", required = true)
    public FireworkShape shape;

    @ApiModelProperty(value = "True if the firework flickers, false otherwise", required = true)
    public boolean flickers;

    @ApiModelProperty(value = "The colors that make up this firework", required = true)
    public List<Color> colors;

    @ApiModelProperty(value = "The fade colors that this firework has", required = true)
    public List<Color> fadeColors;

    @ApiModelProperty(value = "True if this firework has a trail, false otherwise", required = true)
    public boolean trail;


    public FireworkEffectView(FireworkEffect value) {
        super(value);

        this.shape = value.getShape();
        this.flickers = value.flickers();
        this.colors = value.getColors();
        this.fadeColors = value.getFadeColors();
        this.trail = value.hasTrail();
    }
}
