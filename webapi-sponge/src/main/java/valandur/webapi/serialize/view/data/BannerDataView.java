package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.tileentity.BannerData;
import org.spongepowered.api.data.meta.PatternLayer;
import org.spongepowered.api.data.type.DyeColor;
import valandur.webapi.serialize.BaseView;

import java.util.List;

@ApiModel("BannerData")
public class BannerDataView extends BaseView<BannerData> {

    @ApiModelProperty(value = "The color of the banner", required = true)
    public DyeColor color;

    @ApiModelProperty(value = "The patterns on the banner", required = true)
    public List<PatternLayer> patterns;


    public BannerDataView(BannerData value) {
        super(value);

        this.color = value.baseColor().get();
        this.patterns = value.patternsList().getAll();
    }
}
