package valandur.webapi.serialize.view.tileentity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.meta.PatternLayer;
import org.spongepowered.api.data.type.BannerPatternShape;
import org.spongepowered.api.data.type.DyeColor;
import valandur.webapi.api.serialize.BaseView;

@ApiModel("PatternLayer")
public class PatternLayerView extends BaseView<PatternLayer> {

    @ApiModelProperty(value = "The base shape of this pattern", required = true)
    public BannerPatternShape shape;

    @ApiModelProperty(value = "The color of the pattern", required = true)
    public DyeColor color;


    public PatternLayerView(PatternLayer value) {
        super(value);

        this.shape = value.getShape();
        this.color = value.getColor();
    }
}
